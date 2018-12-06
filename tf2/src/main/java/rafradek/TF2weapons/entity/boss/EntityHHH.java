package rafradek.TF2weapons.entity.boss;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.audio.TF2Sounds;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.util.TF2DamageSource;
import rafradek.TF2weapons.util.TF2Util;

public class EntityHHH extends EntityTF2Boss {

	public int targetTime;
	private int attackTick;
	private int scareTick;
	private int teleportTime;
	private int toTeleportTime;
	public int begin=30;
	private float damageTakenNoPath;
	private int aboveGroundTicks;

	public EntityHHH(World worldIn) {
		super(worldIn);
		this.setSize(0.9f, 2.85f);
		this.stepHeight=1.05f;
		this.setNoAI(true);
		this.setHeldItem(EnumHand.MAIN_HAND, ItemFromData.getNewStack("headtaker"));
		this.getHeldItemMainhand().addEnchantment(Enchantments.KNOCKBACK, 10);
	}
	protected void initEntityAI()
    {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class,2, false,false,new Predicate<EntityLivingBase>(){

			@Override
			public boolean apply(EntityLivingBase input) {
				// TODO Auto-generated method stub
				return input.getActivePotionEffect(TF2weapons.it)!=null;
			}
        	
        }));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntitySentry>(this, EntitySentry.class,2, false,false, null));
		/*this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class,2, false,false,new Predicate<EntityLivingBase>(){

			@Override
			public boolean apply(EntityLivingBase input) {
				// TODO Auto-generated method stub
				return (input instanceof EntityTF2Character || input instanceof EntityPlayer) && getDistanceSqToEntity(input)<600;
			}
        	
        }));*/
        
    }
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		// this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(105);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.10D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(13D);

	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData p_110161_1_) {
		p_110161_1_=super.onInitialSpawn(diff, p_110161_1_);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()*(0.88+this.level*0.12));
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue()*(0.9+this.level*0.1));
		return p_110161_1_;
	}
	public void dropFewItems(boolean hit,int looting){
		if(this.rand.nextBoolean())
			this.entityDropItem(ItemFromData.getNewStack("headtaker"), 0);
		//ItemStack hat=ItemFromData.getNewStack("monoculus");
		//hat.getTagCompound().setShort("BossLevel",(short)this.level);
		//this.entityDropItem(hat, 0);
	}
	
	@Override
	public void onLivingUpdate() {
		
		if (this.getHeldItemMainhand().isEmpty() || !(this.getHeldItemMainhand().getItem() instanceof ItemWeapon))
			this.setHeldItem(EnumHand.MAIN_HAND, ItemFromData.getNewStack("headtaker"));
		
		super.onLivingUpdate();
		if (this.begin-- > 20 && this.world.isRemote)
			for (int i = 0; i < 40; i++) {
				Vec3d pos = TF2Util.radiusRandom2D(2.2f, this.rand);
				this.world.spawnParticle(EnumParticleTypes.PORTAL, pos.x + this.posX, this.posY - 0.5,
						pos.y + this.posZ, 0, 0, 0, new int[0]);
			}
		
		if(!world.isRemote && this.begin<=0){
			if(this.begin==0){
				this.setNoAI(false);
			}
			if(this.getAttackTarget()!=null && !(this.getAttackTarget() instanceof EntitySentry) && this.getAttackTarget().getActivePotionEffect(TF2weapons.it)==null)
				this.setAttackTarget(null);
			this.scareTick--;
			if(this.getAttackTarget()==null && this.ticksExisted%5==0){
				List<EntityLivingBase> list=this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(20, 10, 20), new Predicate<EntityLivingBase>(){

					@Override
					public boolean apply(EntityLivingBase input) {
						// TODO Auto-generated method stub
						return (input instanceof EntityTF2Character || input instanceof EntityPlayer || input == getRevengeTarget()) && getDistanceSqToEntity(input)<600
								&& EntityAITarget.isSuitableTarget(EntityHHH.this, input, false, false);
					}
					
				});
				Collections.sort(list, new EntityAINearestAttackableTarget.Sorter(this));
				if(list.size()>0)
					list.get(0).addPotionEffect(new PotionEffect(TF2weapons.it,600));
				else if(this.getRevengeTarget() != null) {
					this.getRevengeTarget().addPotionEffect(new PotionEffect(TF2weapons.it,600));
				}
				if(this.scareTick<=0){
					boolean one=false;
					List<EntityLivingBase> lists=this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(10, 10, 10), new Predicate<EntityLivingBase>(){

						@Override
						public boolean apply(EntityLivingBase input) {
							// TODO Auto-generated method stub
							return getDistanceSqToEntity(input)<100 && !TF2Util.isOnSameTeam(EntityHHH.this, input) && !(input.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()
									&& input.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem()==Item.getItemFromBlock(Blocks.PUMPKIN));
						}
						
					});
					if(lists.size()>1) {
						for(EntityLivingBase living:lists){
							one=true;
							TF2Util.stun(living, 80, false);
						}
					}
					if(one)
						this.playSound(TF2Sounds.MOB_HHH_ALERT, 1.75F, 1);
					this.scareTick=200;
				}
			}
			/*if (this.getAttackTarget() != null) {
				if (this.getNavigator().getPathToEntityLiving(this.getAttackTarget()) != null)
					System.out.println("has way:"+this.getNavigator().getPathToEntityLiving(this.getAttackTarget()).getFinalPathPoint().y);
				
				
			}*/
			if (this.toTeleportTime <= 0 && this.getAttackTarget() != null && this.ticksExisted % 5 == 0) {
				Path path = this.getNavigator().getPathToEntityLiving(this.getAttackTarget());
				boolean shouldTeleport;
				if (path == null)
					shouldTeleport = this.getDistanceSqToEntity(this.getAttackTarget())>1.5&&this.getDistanceSq(this.getAttackTarget().posX, this.posY, this.getAttackTarget().posZ)<1;
				else
					shouldTeleport = Math.abs(path.getFinalPathPoint().y - this.getAttackTarget().posY) > 1.5 && this.getAttackTarget().onGround;
				if (shouldTeleport) {
					this.toTeleportTime=40;
					this.teleportTime=250;
					this.setNoAI(true);
					this.setSneaking(true);
				}
			}
			/*if(--this.teleportTime<=0&&this.getAttackTarget()!=null&&(this.getNavigator().noPath()||this.getDistanceSqToEntity(this.getAttackTarget())>1.5&&this.getDistanceSq(this.getAttackTarget().posX, this.posY, this.getAttackTarget().posZ)<0.75)){
				
			}*/
			if(this.toTeleportTime>0){
				if(this.world instanceof WorldServer){
					WorldServer world=(WorldServer)this.world;
						world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY - 0.5,
								this.posZ, 8, 2.7, 0, 2.7, 0, new int[0]);
				}
				if(--this.toTeleportTime==0){
					this.setSneaking(false);
					this.setNoAI(false);
					Vec3d pos = null;
					if (this.getAttackTarget()!=null) 
						pos = new Vec3d(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ);
					/*else
						pos = RandomPositionGenerator.findRandomTarget(this, 16, 6);*/
					if(pos != null && this.attemptTeleportForce(pos.x, pos.y, pos.z)){
						this.addPotionEffect(new PotionEffect(TF2weapons.stun, 30, 3));
						for (int i = 0; i < 40; i++) {
							Vec3d partpos = TF2Util.radiusRandom2D(2.7f, this.rand);
							this.world.spawnParticle(EnumParticleTypes.PORTAL, partpos.x + this.posX, this.posY - 0.5,
									partpos.y + this.posZ, 0, 0, 0, new int[0]);
						}
					}
				}
			}
				
			if(--this.attackTick<=0){
				List<EntityLivingBase> ents=this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(3, 1, 3), new Predicate<EntityLivingBase>(){
	
					@Override
					public boolean apply(EntityLivingBase input) {
						// TODO Auto-generated method stub
						return getDistanceSqToEntity(input)<4&&!TF2Util.isOnSameTeam(EntityHHH.this, input) && 
								(TF2Util.lookingAt(EntityHHH.this, 30, input.posX, input.posY+input.getEyeHeight(), input.posZ) || input.getCollisionBoundingBox() != null);
					}
					
				});
				if(!ents.isEmpty()){
					this.swingArm(EnumHand.MAIN_HAND);
					this.attackTick=20;
					for(EntityLivingBase living:ents){
						this.attackEntityAsMob(living);
					}
				}
			}
		}
		
	}
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		

		if (super.attackEntityFrom(source, amount)) {
			if (this.getAttackTarget() == null || this.getNavigator().noPath()) {
				this.damageTakenNoPath += amount;
				if (this.damageTakenNoPath >= 20) {
					this.damageTakenNoPath = 0;
					this.toTeleportTime=40;
					this.teleportTime=250;
					this.setNoAI(true);
					this.setSneaking(true);
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean attackEntityAsMob(Entity entityIn)
    {
		if(super.attackEntityAsMob(entityIn)){
			
			this.playSound(TF2Sounds.MOB_HHH_HIT, 1F, 1F);
			if(!entityIn.isEntityAlive())
				this.playSound(TF2Sounds.MOB_HHH_ATTACK, 1.75f, 1);
			return true;
		}
		return false;
    }
	public AxisAlignedBB getBreakingBB(){
		if(this.getAttackTarget()!=null){
			AxisAlignedBB orig=this.getEntityBoundingBox();
			return new AxisAlignedBB(orig.minX-0.5, orig.minY+1, orig.minZ-0.5,
					orig.maxX+0.5, orig.maxY, orig.maxZ+0.5);
		}
		else
			return super.getBreakingBB();
	}
	public boolean breakBlocks(){
		boolean flag=super.breakBlocks();
		if(flag){
			this.swingArm(EnumHand.MAIN_HAND);
			this.playSound(TF2Sounds.MOB_HHH_MISS, 1, 1);
		}
		return flag;
	}
	@Override
	public int getTalkInterval() {
		return 130;
	}
	public void setDead(){
		if(this.getAttackTarget()!=null){
			this.getAttackTarget().removePotionEffect(TF2weapons.it);
		}
		super.setDead();
	}
	public void setAttackTarget(EntityLivingBase ent){
		if(this.getAttackTarget()!=null&&ent instanceof EntityBuilding)
			return;
		if(ent!=null&&ent.hasCapability(TF2weapons.WEAPONS_CAP, null)&&ent.getCapability(TF2weapons.WEAPONS_CAP, null).itProtection>0)
			return;
		
		if(ent!=this.getAttackTarget()&&this.getAttackTarget()!=null){
			boolean hadeffect=this.getAttackTarget().getActivePotionEffect(TF2weapons.it)!=null;
			this.getAttackTarget().removePotionEffect(TF2weapons.it);
			if(ent==null&&hadeffect)
				this.getAttackTarget().addPotionEffect(new PotionEffect(TF2weapons.it,12));
		}
		if(ent!=null){
			/*if(ent.getActivePotionEffect(TF2weapons.it)==null&&this.scareTick<=0){
				for(EntityLivingBase living:this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(10, 10, 10), new Predicate<EntityLivingBase>(){

					@Override
					public boolean apply(EntityLivingBase input) {
						// TODO Auto-generated method stub
						return getDistanceSqToEntity(input)<100 && !TF2weapons.isOnSameTeam(EntityHHH.this, input) && !(input.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null 
								&& input.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem()==Item.getItemFromBlock(Blocks.PUMPKIN));
					}
					
				})){
					TF2weapons.stun(living, 100, false);
				}
				this.scareTick=200;
			}*/
			this.targetTime=300;
			ent.addPotionEffect(new PotionEffect(TF2weapons.it,600));
			if(ent!=this.getAttackTarget()&&this.rand.nextBoolean()){
				
			}
		}
		super.setAttackTarget(ent);
	}
	/*public void addAchievement(EntityPlayer player){
		super.addAchievement(player);
		player.addStat(TF2Achievements.HHH);
	}*/
	public SoundEvent getAmbientSound(){
		return TF2Sounds.MOB_HHH_SAY;
	}
	public SoundEvent getDeathSound(){
		return TF2Sounds.MOB_HHH_DEFEAT;
	}
	public SoundEvent getAppearSound(){
		return TF2Sounds.MOB_HHH_START;
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setShort("Begin", (short)this.begin);
		nbt.setShort("Scare", (short)this.scareTick);
		nbt.setShort("Teleport", (short)this.teleportTime);
		nbt.setFloat("NoPathDamange", this.damageTakenNoPath);
	}
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.damageTakenNoPath = nbt.getFloat("NoPathDamage");
		this.begin=nbt.getShort("Begin");
		this.scareTick=nbt.getShort("Scare");
		this.teleportTime=nbt.getShort("Teleport");
	}
}
