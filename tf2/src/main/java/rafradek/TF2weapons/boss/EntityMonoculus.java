package rafradek.TF2weapons.boss;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2DamageSource;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.ItemWeapon;

public class EntityMonoculus extends EntityTF2Boss {

	public int teleport = 200;
	public int angryTicks = 0;
	public int begin = 30;
	public float toAngry=0;
	private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(EntityMonoculus.class,
			DataSerializers.BOOLEAN);

	public EntityMonoculus(World worldIn) {
		super(worldIn);
		this.setSize(4, 4);
		this.setHeldItem(EnumHand.MAIN_HAND, ItemFromData.getNewStack("mnceye"));

		this.moveHelper = new MonoculusMoveHelper(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void entityInit() {
		super.entityInit();
		this.dataManager.register(ANGRY, false);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(5, new AIRandomFly(this));
		this.tasks.addTask(6, new AIFireballAttack(this));
		this.tasks.addTask(7, new AILookAround(this));
		
		this.targetTasks.taskEntries.clear();
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, 0,true, false,
				new Predicate<EntityLivingBase>() {

					@Override
					public boolean apply(EntityLivingBase input) {
						// TODO Auto-generated method stub
						return input instanceof EntityPlayer || input instanceof EntityTF2Character;
					}

				}));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
	}
	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	public boolean isAngry() {
		return this.getDataManager().get(ANGRY);
	}

	public void setAngry(int ticks) {
		this.angryTicks=ticks;
		boolean angry=ticks>0;
		this.toAngry=0;
		if (angry)
			TF2Attribute.setAttribute(this.getHeldItemMainhand(), TF2Attribute.attributes[19],
					3 * (0.85f + this.level * 0.15f));
		else
			TF2Attribute.setAttribute(this.getHeldItemMainhand(), TF2Attribute.attributes[19],
					1 * (0.85f + this.level * 0.15f));

		this.getDataManager().set(ANGRY, angry);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		

		if (super.attackEntityFrom(source, amount)) {
			if (source instanceof TF2DamageSource) {
				if (((TF2DamageSource) source).getCritical() > 0) {
					if (!this.isAngry())
						this.setAngry(160);
				}
			}
			else{
				this.toAngry+=amount;
				if(this.toAngry>=55*(1+level*0.08f)){
					this.setAngry(120);
				}
			}
			return true;
		}
		return false;
	}
	public SoundEvent getDeathSound(){
		return TF2Sounds.MOB_MONOCULUS_DEFEAT;
	}
	public SoundEvent getAppearSound(){
		return TF2Sounds.MOB_MONOCULUS_START;
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setShort("Begin", (short)this.begin);
		nbt.setShort("Angry", (short)this.angryTicks);
		nbt.setShort("Teleport", (short)this.teleport);
		
	}
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.begin=nbt.getShort("Begin");
		this.angryTicks=nbt.getShort("Angry");
		this.setAngry(this.angryTicks);
		this.teleport=nbt.getShort("Teleport");
	}
	/**
	 * Moves the entity based on the specified heading.
	 */
	@Override
	public void travel(float strafe, float forward, float par3) {
		if (this.isInWater()) {
			this.moveRelative(strafe, forward, par3, 0.02F);
			this.move(MoverType.SELF,this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else if (this.isInLava()) {
			this.moveRelative(strafe, forward, par3, 0.02F);
			this.move(MoverType.SELF,this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float f = 0.91F;

			if (this.onGround)
				f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX),
						MathHelper.floor(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;

			float f1 = 0.16277136F / (f * f * f);
			this.moveRelative(strafe, forward, par3, this.onGround ? 0.1F * f1 : 0.02F);
			f = 0.91F;

			if (this.onGround)
				f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX),
						MathHelper.floor(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= f;
			this.motionY *= f;
			this.motionZ *= f;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d1 = this.posX - this.prevPosX;
		double d0 = this.posZ - this.prevPosZ;
		float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

		if (f2 > 1.0F)
			f2 = 1.0F;

		this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	/**
	 * returns true if this entity is by a ladder, false otherwise
	 */

	@Override
	public boolean isOnLadder() {
		return false;
	}

	@Override
	public void onLivingUpdate() {
		if (this.getHeldItemMainhand().isEmpty() || !(this.getHeldItemMainhand().getItem() instanceof ItemWeapon))
			this.setHeldItem(EnumHand.MAIN_HAND, ItemFromData.getNewStack("mnceye"));
		
		super.onLivingUpdate();
		if (this.begin-- > 20 && this.world.isRemote)
			for (int i = 0; i < 40; i++) {
				Vec3d pos = TF2Util.radiusRandom2D(2.7f, this.rand);
				this.world.spawnParticle(EnumParticleTypes.PORTAL, pos.x + this.posX, this.posY - 0.5,
						pos.y + this.posZ, 0, 0, 0, new int[0]);
			}
		if (this.ticksExisted == 1) {

		}
		
		//System.out.println(" "+this.rotationPitch);
		if (!this.world.isRemote) {
			if (this.getAttackTarget() == null) {
				double f = MathHelper.sqrt(this.motionX * this.motionX
						+ this.motionZ * this.motionZ);
				this.rotationYaw = -((float) MathHelper.atan2(this.motionX,
						this.motionZ)) * (180F / (float) Math.PI);
				this.rotationPitch = -((float) MathHelper.atan2(this.motionY, f))
						* (180F / (float) Math.PI);
				this.renderYawOffset = this.rotationYaw;
				this.rotationYawHead = this.rotationYaw;
			} else {
				EntityLivingBase entitylivingbase = this.getAttackTarget();
				double d0 = 64.0D;

				if (entitylivingbase.getDistanceSqToEntity(this) < 4096.0D) {
					double d1 = entitylivingbase.posX - this.posX;
					double d2 = entitylivingbase.posZ - this.posZ;
					double d3 = entitylivingbase.posY - (this.posY + this.getEyeHeight());
					double f = MathHelper.sqrt(d1 * d1 + d2 * d2);
					this.rotationYaw = -((float) MathHelper.atan2(d1, d2)) * (180F / (float) Math.PI);
					this.renderYawOffset = this.rotationYaw;
					this.rotationPitch = -((float) MathHelper.atan2(d3, f)) * (180F / (float) Math.PI);
					this.rotationYawHead = this.rotationYaw;
				}
			}
			if (this.ticksExisted%20==0 && !this.isAngry() &&this.rand.nextInt(20)==0)
				this.setAngry(100);
				
			this.toAngry=Math.max(0, this.toAngry-0.4f);
			if (this.isAngry() && --this.angryTicks <= 0)
				this.setAngry(0);
			if (--this.teleport <= 0)
				for (int i = 0; i < 10; i++) {

					double x = this.posX + rand.nextDouble() * 48 - 24;
					double z = this.posZ + rand.nextDouble() * 48 - 24;
					double y = this.posY + rand.nextDouble() * 48 - 24;
					y = Math.min(y, this.world.getTopSolidOrLiquidBlock(new BlockPos(x, y, z)).getY() + 16);
					if (this.attemptTeleport(x, y, z)) {
						for (int j = 0; j < 40; j++) {
							Vec3d pos = TF2Util.radiusRandom3D(2.7f, this.rand);
							this.world.spawnParticle(EnumParticleTypes.PORTAL, pos.x + this.posX,
									pos.y + this.posY, pos.z + this.posZ, 0, 0, 0, new int[0]);
						}
						this.teleport += 160 + rand.nextInt(80);
						break;
					}
				}
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		// this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(175);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7D);

	}

	public void dropFewItems(boolean hit,int looting){
		if(this.rand.nextBoolean())
			this.entityDropItem(ItemFromData.getNewStack("bombinomicon"), 0);
		ItemStack hat=ItemFromData.getNewStack("monoculus");
		hat.getTagCompound().setShort("BossLevel",(short)this.level);
		this.entityDropItem(hat, 0);
	}
	/*public void addAchievement(EntityPlayer player){
		super.addAchievement(player);
		player.addStat(TF2Achievements.MONOCULUS);
	}*/
	@Override
	public float getEyeHeight() {
		return this.height / 2;
	}

	static class AIFireballAttack extends EntityAIBase {
		private final EntityMonoculus parentEntity;
		private int attackTimer;

		public int triple;

		public AIFireballAttack(EntityMonoculus ghast) {
			this.parentEntity = ghast;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			return this.parentEntity.begin <= 0 && this.parentEntity.getAttackTarget() != null;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			//this.attackTimer = 0;
		}

		/**
		 * Resets the task
		 */
		/*
		 * public void resetTask() { this.parentEntity.setAttacking(false); }
		 */

		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
			double d0 = 64.0D;

			if (entitylivingbase.getDistanceSqToEntity(this.parentEntity) < 4096.0D
					&& this.parentEntity.canEntityBeSeen(entitylivingbase)) {
				World world = this.parentEntity.world;
				this.attackTimer--;

				if (this.attackTimer <= 0) {
					double d1 = 4.0D;
					/*
					 * double d2 = entitylivingbase.posX -
					 * (this.parentEntity.posX + vec3d.x * 4.0D); double d3
					 * = entitylivingbase.getEntityBoundingBox().minY +
					 * (double)(entitylivingbase.height / 2.0F) - (0.5D +
					 * this.parentEntity.posY +
					 * (double)(this.parentEntity.height / 2.0F)); double d4 =
					 * entitylivingbase.posZ - (this.parentEntity.posZ +
					 * vec3d.z * 4.0D); world.playEvent((EntityPlayer)null,
					 * 1016, new BlockPos(this.parentEntity), 0);
					 * EntityLargeFireball entitylargefireball = new
					 * EntityLargeFireball(world, this.parentEntity, d2, d3,
					 * d4); entitylargefireball.explosionPower = 3;
					 * entitylargefireball.posX = this.parentEntity.posX +
					 * vec3d.x * 4.0D; entitylargefireball.posY =
					 * this.parentEntity.posY +
					 * (double)(this.parentEntity.height / 2.0F) + 0.5D;
					 * entitylargefireball.posZ = this.parentEntity.posZ +
					 * vec3d.z * 4.0D;
					 * world.spawnEntity(entitylargefireball);
					 */
					if(this.parentEntity.isAngry())
						this.parentEntity.playSound(TF2Sounds.MOB_MONOCULUS_SHOOT_MAD, 3f, 1);
					else
						this.parentEntity.playSound(TF2Sounds.MOB_MONOCULUS_SHOOT, 3f, 1);
					
					((ItemProjectileWeapon) this.parentEntity.getHeldItemMainhand().getItem()).shoot(
							this.parentEntity.getHeldItemMainhand(), this.parentEntity, world, 2, EnumHand.MAIN_HAND);
					if (this.triple > 0) {
						triple--;
						this.attackTimer = Math.max(4, 6 - this.parentEntity.level / 5);
						
					} else {
						this.attackTimer = Math.max(11, 29 - this.parentEntity.level);
						if (this.parentEntity.isAngry())
							triple = 2;
					}
					//System.out.println(this.attackTimer);

				}
			} else if (this.attackTimer > 0)
				--this.attackTimer;

			// this.parentEntity.setAttacking(this.attackTimer > 10);
		}
	}

	static class AILookAround extends EntityAIBase {
		private final EntityMonoculus parentEntity;

		public AILookAround(EntityMonoculus ghast) {
			this.parentEntity = ghast;
			this.setMutexBits(2);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			return this.parentEntity.begin <= 0;
		}

		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			if (this.parentEntity.getAttackTarget() == null) {
				double f = MathHelper.sqrt(this.parentEntity.motionX * this.parentEntity.motionX
						+ this.parentEntity.motionZ * this.parentEntity.motionZ);
				this.parentEntity.rotationYaw = -((float) MathHelper.atan2(this.parentEntity.motionX,
						this.parentEntity.motionZ)) * (180F / (float) Math.PI);
				this.parentEntity.rotationPitch = -((float) MathHelper.atan2(this.parentEntity.motionY, f))
						* (180F / (float) Math.PI);
				this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
				this.parentEntity.rotationYawHead = this.parentEntity.rotationYaw;
			} else {
				EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
				double d0 = 64.0D;

				if (entitylivingbase.getDistanceSqToEntity(this.parentEntity) < 4096.0D) {
					double d1 = entitylivingbase.posX - this.parentEntity.posX;
					double d2 = entitylivingbase.posZ - this.parentEntity.posZ;
					double d3 = entitylivingbase.posY - (this.parentEntity.posY + this.parentEntity.getEyeHeight());
					double f = MathHelper.sqrt(d1 * d1 + d2 * d2);
					this.parentEntity.rotationYaw = -((float) MathHelper.atan2(d1, d2)) * (180F / (float) Math.PI);
					this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
					this.parentEntity.rotationPitch = -((float) MathHelper.atan2(d3, f)) * (180F / (float) Math.PI);
					this.parentEntity.rotationYawHead = this.parentEntity.rotationYaw;
				}
			}
			//System.out.println("coto: "+this.parentEntity.rotationPitch);
		}
	}

	static class AIRandomFly extends EntityAIBase {
		private final EntityMonoculus parentEntity;

		public AIRandomFly(EntityMonoculus ghast) {
			this.parentEntity = ghast;
			this.setMutexBits(1);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (this.parentEntity.begin > 0)
				return false;
			EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();

			if (!entitymovehelper.isUpdating() || !this.parentEntity.isWithinHomeDistanceCurrentPosition())
				return true;
			else {
				double d0 = entitymovehelper.getX() - this.parentEntity.posX;
				double d1 = entitymovehelper.getY() - this.parentEntity.posY;
				double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				return d3 < 1.0D || d3 > 3600.0D;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			Random random = this.parentEntity.getRNG();
			double d0 = this.parentEntity.posX + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
			BlockPos pos = this.parentEntity.world.getTopSolidOrLiquidBlock(this.parentEntity.getPosition());
			double d1 = Math.min(pos.getY() + 16, this.parentEntity.posY + (random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double d2 = this.parentEntity.posZ + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
			if(this.parentEntity.isWithinHomeDistanceCurrentPosition()
					|| this.parentEntity.getHomePosition().distanceSq(d0, d1, d2) < this.parentEntity.getDistanceSq(this.parentEntity.getHomePosition()))
				this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
		}
	}

	static class MonoculusMoveHelper extends EntityMoveHelper {
		private final EntityMonoculus parentEntity;
		private int courseChangeCooldown;

		public MonoculusMoveHelper(EntityMonoculus ghast) {
			super(ghast);
			this.parentEntity = ghast;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (this.action == EntityMoveHelper.Action.MOVE_TO) {
				double d0 = this.posX - this.parentEntity.posX;
				double d1 = this.posY - this.parentEntity.posY;
				double d2 = this.posZ - this.parentEntity.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;

				if (this.courseChangeCooldown-- <= 0) {
					this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
					d3 = MathHelper.sqrt(d3);

					if (this.isNotColliding(this.posX, this.posY, this.posZ, d3)) {
						this.parentEntity.motionX += d0 / d3 * 0.1D;
						this.parentEntity.motionY += d1 / d3 * 0.1D;
						this.parentEntity.motionZ += d2 / d3 * 0.1D;
					} else
						this.action = EntityMoveHelper.Action.WAIT;
				}
			}
		}

		/**
		 * Checks if entity bounding box is not colliding with terrain
		 */
		private boolean isNotColliding(double x, double y, double z, double p_179926_7_) {
			double d0 = (x - this.parentEntity.posX) / p_179926_7_;
			double d1 = (y - this.parentEntity.posY) / p_179926_7_;
			double d2 = (z - this.parentEntity.posZ) / p_179926_7_;
			AxisAlignedBB axisalignedbb = this.parentEntity.getEntityBoundingBox();

			for (int i = 1; i < p_179926_7_; ++i) {
				axisalignedbb = axisalignedbb.offset(d0, d1, d2);

				if (!this.parentEntity.world.getCollisionBoxes(this.parentEntity, axisalignedbb).isEmpty())
					return false;
			}

			return true;
		}
	}
}
