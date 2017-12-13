package rafradek.TF2weapons.building;

import java.lang.reflect.Field;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAISentryAttack;
import rafradek.TF2weapons.characters.ai.EntityAISentryIdle;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.pages.Contract.Objective;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntitySentry extends EntityBuilding {

	public ItemStack sentryBullet = ItemFromData.getNewStack("sentrybullet");
	public ItemStack sentryBullet2 = ItemFromData.getNewStack("sentrybullet2");
	public ItemStack sentryRocket = ItemFromData.getNewStack("sentryrocket");
	public float rotationDefault = 0;
	public int attackDelay;
	public int attackDelayRocket;
	public boolean shootRocket;
	public boolean shootBullet;
	public int mercsKilled;
	//public SentryLookHelper lookHelper;
	private static final DataParameter<Integer> AMMO = EntityDataManager.createKey(EntitySentry.class,
			DataSerializers.VARINT);
	private static final DataParameter<Integer> ROCKET = EntityDataManager.createKey(EntitySentry.class,
			DataSerializers.VARINT);
	private static final DataParameter<Integer> KILLS = EntityDataManager.createKey(EntitySentry.class,
			DataSerializers.VARINT);
	private static final DataParameter<Boolean> CONTROLLED = EntityDataManager.createKey(EntitySentry.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> TARGET = EntityDataManager.createKey(EntitySentry.class,
			DataSerializers.BYTE);

	public EntitySentry(World worldIn) {
		super(worldIn);
		this.setSize(0.8f, 0.8f);
		for(Field field:EntityLiving.class.getDeclaredFields()) {
			if(field.getName().equals("lookHelper") || field.getName().equals("field_70749_g")) {
				field.setAccessible(true);
				try {
					field.set(this, new SentryLookHelper(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public EntitySentry(World worldIn, EntityLivingBase owner) {
		super(worldIn, owner);
		this.setSize(0.8f, 0.8f);
		//this.lookHelper=new SentryLookHelper(this);
	}

	@Override
	public void adjustSize() {
		if (this.getLevel() == 1)
			this.setSize(0.8f, 0.8f);
		else if (this.getLevel() == 2)
			this.setSize(1f, 1f);
		else if (this.getLevel() == 3)
			this.setSize(1.2f, 1.2f);
		//System.out.println(x);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isControlled())
			amount *= 0.5f;
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public SoundEvent getSoundNameForState(int state) {
		switch (state) {
		case 0:
			return this.getLevel() == 1 ? TF2Sounds.MOB_SENTRY_SCAN_1
					: (this.getLevel() == 2 ? TF2Sounds.MOB_SENTRY_SCAN_2 : TF2Sounds.MOB_SENTRY_SCAN_3);
		// case 2:return TF2weapons.MOD_ID+":mob.sentry.shoot."+this.getLevel();
		case 3:
			return TF2Sounds.MOB_SENTRY_EMPTY;
		default:
			return super.getSoundNameForState(state);
		}
	}

	@Override
	public void setAttackTarget(EntityLivingBase target) {
		if (TF2Util.isOnSameTeam(this.getOwner(), target))
			return;
		if (target != this.getAttackTarget())
			this.playSound(TF2Sounds.MOB_SENTRY_SPOT, 1.5f, 1f);
		super.setAttackTarget(target);
	}

	@Override
	public void applyTasks() {

		//this.targetTasks.addTask(1, new EntityAISentryOwnerHurt(this, true));
		this.targetTasks.addTask(2, new EntityAINearestChecked(this, EntityLivingBase.class, true, false,
				new Predicate<EntityLivingBase>() {
					@Override
					public boolean apply(EntityLivingBase target) {
						return (((((getAttackFlags() & 2) == 2 && getOwnerId() != null) && target instanceof EntityPlayer) 
								|| target.getTeam() != null
								|| ((getAttackFlags() & 1) == 1 && (getRevengeTarget()==target || (getOwner() != null && getOwner().getRevengeTarget()==target)))
								|| ((getAttackFlags() & 4) == 4 && target instanceof IMob && getOwnerId() != null)
								|| ((getAttackFlags() & 4) == 4 && target instanceof EntityLiving && ((EntityLiving) target).getAttackTarget() == getOwner()))
								|| ((getAttackFlags() & 8) == 8 && !(target instanceof EntityPlayer) && !(target instanceof IMob) && getOwnerId() != null))
								&& (!TF2Util.isOnSameTeam(EntitySentry.this, target))
								&& (!(target instanceof EntityTF2Character && TF2ConfigVars.naturalCheck.equals("Never"))
										|| !((EntityTF2Character) target).natural);

					}
				}, false));
		this.tasks.addTask(1, new EntityAISentryAttack(this));
		this.tasks.addTask(2, new EntityAISentryIdle(this));
	}

	@Override
	public void onLivingUpdate() {

		if (!this.world.isRemote && this.ticksExisted == 1)
			this.getAttackFlags();
		if (this.rotationDefault == 0)
			this.rotationDefault = this.rotationYawHead;
		if (this.attackDelay > 0)
			this.attackDelay--;
		if (this.attackDelayRocket > 0)
			this.attackDelayRocket--;
		this.ignoreFrustumCheck = this.isControlled();
		if (this.isControlled() && !this.world.isRemote) {
			Vec3d lookVec = this.getOwner().getLookVec().scale(200);
			List<RayTraceResult> trace = TF2Util.pierce(world, this.getOwner(), this.getOwner().posX,
					this.getOwner().posY + this.getOwner().getEyeHeight(), this.getOwner().posZ,
					this.getOwner().posX + lookVec.x,
					this.getOwner().posY + this.getOwner().getEyeHeight() + lookVec.y,
					this.getOwner().posZ + lookVec.z, false, 0.02f, false);
			this.getLookHelper().setLookPosition(trace.get(0).hitVec.x, trace.get(0).hitVec.y,
					trace.get(0).hitVec.z, 30, 75);
		}
		if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive())
			this.setAttackTarget(null);
		super.onLivingUpdate();
	}

	@Override
	public ItemStack getHeldItem(EnumHand hand) {
		return sentryRocket;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		// this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValu(1.6D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(AMMO, 150);
		this.dataManager.register(ROCKET, 20);
		this.dataManager.register(KILLS, 0);
		this.dataManager.register(TARGET, (byte) -1);
		this.dataManager.register(CONTROLLED, Boolean.valueOf(false));
	}

	public void shootRocket(EntityLivingBase owner) {
		while (this.getLevel() == 3 && this.getRocketAmmo() > 0 && this.attackDelayRocket <= 0) {
			this.attackDelayRocket += 60;
			if (this.isControlled())
				this.attackDelayRocket *= 0.75f;
			try {
				// System.out.println(owner);
				this.playSound(TF2Sounds.MOB_SENTRY_ROCKET, 1.5f, 1f);
				EntityProjectileBase proj = MapList.projectileClasses
						.get(ItemFromData.getData(this.sentryRocket).getString(PropertyType.PROJECTILE))
						.getConstructor(World.class, EntityLivingBase.class, EnumHand.class)
						.newInstance(this.world, this, EnumHand.MAIN_HAND);
				proj.shootingEntity = owner;
				proj.usedWeapon = sentryRocket;
				proj.sentry = this;
				this.world.spawnEntity(proj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.setRocketAmmo(this.getRocketAmmo() - 1);
			/*
			 * RayTraceResult bullet=TF2weapons.pierce(this.host.world,
			 * this.host, this.host.posX, this.host.posY+this.host.height/2,
			 * this.host.posZ, this.target.posX,
			 * this.target.posY+this.target.height/2, this.target.posZ,false,
			 * 0.08f); if(bullet.entityHit!=null){ DamageSource
			 * src=TF2weapons.causeBulletDamage("Sentry Gun",
			 * this.host.getOwner(), 0); TF2weapons.dealDamage(bullet.entityHit,
			 * this.host.world, this.host.owner, null, 0, 1.6f, src); Vec3d
			 * dist=new
			 * Vec3d(this.host.posX-bullet.entityHit.posX,this.host.posY-bullet.
			 * entityHit.posY,this.host.posZ-bullet.entityHit.posZ).normalize();
			 * bullet.entityHit.addVelocity(dist.x,dist.y,
			 * dist.z); }
			 */

		}
	}

	public void shootBullet(EntityLivingBase owner) {
		this.setSoundState(this.getAmmo() > 0 ? 2 : 3);
		Vec3d attackPos = this.isControlled()
				? new Vec3d(this.getLookHelper().getLookPosX(), this.getLookHelper().getLookPosY(),
						this.getLookHelper().getLookPosZ())
				: this.getAttackTarget().getPositionVector().addVector(0, this.getAttackTarget().getEyeHeight(), 0);
		while (this.attackDelay <= 0 && this.getAmmo() > 0) {
			if(this.getOwnerId() != null && this.ticksExisted % 10 == 0)
				TF2Util.attractMobs(this, this.world);
			this.attackDelay += this.getLevel() > 1 ? 2.5f : 5f;
			if (this.isControlled())
				this.attackDelay /= 2;
			this.playSound(this.getLevel() == 1 ? TF2Sounds.MOB_SENTRY_SHOOT_1 : TF2Sounds.MOB_SENTRY_SHOOT_2, 1.5f,
					1f);
			List<RayTraceResult> list = TF2Util.pierce(this.world, this, this.posX,
					this.posY + this.getEyeHeight(), this.posZ, attackPos.x, attackPos.y, attackPos.z,
					false, 0.08f, false);
			for (RayTraceResult bullet : list)
				if (bullet.entityHit != null) {

					DamageSource src = TF2Util.causeDirectDamage(sentryBullet, owner, 0).setProjectile();

					if (TF2Util.dealDamage(bullet.entityHit, this.world, owner, this.sentryBullet,
							TF2Util.calculateCritPost(bullet.entityHit, null, 0, ItemStack.EMPTY), 1.6f, src)) {
						Vec3d dist = new Vec3d(bullet.entityHit.posX - this.posX, bullet.entityHit.posY - this.posY,
								bullet.entityHit.posZ - this.posZ).normalize();
						dist=dist.scale(0.25 * (this.getLevel()>1? 0.7 : 1));
						if(this.isControlled())
							dist=dist.scale(0.25);
						if(bullet.entityHit instanceof EntityLivingBase )
							dist=dist.scale(1-((EntityLivingBase) bullet.entityHit).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue());
						if(dist.lengthSquared()>0f) {
							bullet.entityHit.addVelocity(dist.x, dist.y, dist.z);
							bullet.entityHit.isAirBorne=bullet.entityHit.motionY>0.05;
							if (bullet.entityHit instanceof EntityPlayerMP)
								TF2weapons.network.sendTo(new TF2Message.VelocityAddMessage(dist, bullet.entityHit.isAirBorne), (EntityPlayerMP) bullet.entityHit);
							
							if (bullet.entityHit instanceof EntityLivingBase) {
								((EntityLivingBase) bullet.entityHit).setLastAttackedEntity(this);
								((EntityLivingBase) bullet.entityHit).setRevengeTarget(this);
								if (!bullet.entityHit.isEntityAlive()) {
									this.setKills(this.getKills() + 1);
									if(owner instanceof EntityPlayer && bullet.entityHit instanceof EntityTF2Character && ++this.mercsKilled%5==0) {
										owner.getCapability(TF2weapons.PLAYER_CAP, null).completeObjective(Objective.KILLS_SENTRY, this.sentryBullet);
									}
										
								}
							}
						}
					}
				}
			this.setAmmo(this.getAmmo() - 1);
		}
	}

	@Override
	public boolean canEntityBeSeen(Entity entityIn) {
		return this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + this.getEyeHeight(), this.posZ),
				new Vec3d(entityIn.posX, entityIn.posY + entityIn.getEyeHeight(), entityIn.posZ), false, true,
				false) == null;
	}

	public int getMaxAmmo() {
		return this.getLevel() == 1 ? 150 : 200;
	}

	public int getAmmo() {
		return this.dataManager.get(AMMO);
	}

	public int getKills() {
		return this.dataManager.get(KILLS);
	}

	public int getRocketAmmo() {
		return this.dataManager.get(ROCKET);
	}

	public int getTargetInfo() {
		return this.dataManager.get(TARGET);
	}

	public void setAmmo(int ammo) {
		this.dataManager.set(AMMO, ammo);
	}

	public void setRocketAmmo(int ammo) {
		this.dataManager.set(ROCKET, ammo);
	}

	public void setKills(int kills) {
		this.dataManager.set(KILLS, kills);
	}

	public void setControlled(boolean control) {
		this.dataManager.set(CONTROLLED, control);
	}

	public void setTargetInfo(int target) {
		this.dataManager.set(TARGET, (byte) target);
	}

	public int getAttackFlags() {
		if (this.getTargetInfo() == -1)
			this.setTargetInfo(this.getOwner() != null && this.getOwner() instanceof EntityPlayer ? 
					WeaponsCapability.get(this.getOwner()).sentryTargets
					: 1);
		return this.getTargetInfo();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setShort("Ammo", (short) this.getAmmo());
		par1NBTTagCompound.setShort("RocketAmmo", (short) this.getRocketAmmo());
		par1NBTTagCompound.setShort("Kills", (short) this.getKills());
		par1NBTTagCompound.setShort("MercKills", (short) this.mercsKilled);
		par1NBTTagCompound.setShort("AttackFlags", (short) this.getTargetInfo());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);

		this.setAmmo(par1NBTTagCompound.getShort("Ammo"));
		this.setRocketAmmo(par1NBTTagCompound.getShort("RocketAmmo"));
		this.setKills(par1NBTTagCompound.getShort("Kills"));
		this.mercsKilled=par1NBTTagCompound.getShort("MercKills");
		this.setTargetInfo(par1NBTTagCompound.getShort("AttackFlags"));
	}

	@Override
	public float getCollHeight() {
		return 1.2f;
	}

	@Override
	public float getCollWidth() {
		return 1.12f;
	}

	@Override
	public float getEyeHeight() {
		return this.height / 2 + 0.2f;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isSapped() ? null : TF2Sounds.MOB_SENTRY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_SENTRY_DEATH;
	}

	@Override
	public boolean canUseWrench() {
		return super.canUseWrench() || this.getAmmo() < this.getMaxAmmo() || this.getRocketAmmo() < 20;
	}

	@Override
	public void upgrade() {
		super.upgrade();
		this.setAmmo(200);
	}

	public boolean isControlled() {
		// TODO Auto-generated method stub
		return this.isEntityAlive() && this.dataManager.get(CONTROLLED);// this.getOwner()
																		// !=
																		// null
																		// &&
																		// this.getOwner()
																		// instanceof
																		// EntityPlayer
																		// &&
																		// this.getOwner().getCapability(TF2weapons.WEAPONS_CAP,
																		// null).controlledSentry==this;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (this.world.isRemote && player == this.getOwner() && hand == EnumHand.MAIN_HAND) {
			ClientProxy.showGuiSentry(this);
			return true;
		}
		return false;
	}
	
	public void onDeath(DamageSource s){
		super.onDeath(s);
		/*if(s.getEntity() !=null && s.getEntity() instanceof EntityPlayer && s instanceof TF2DamageSource && !((TF2DamageSource)s).getWeapon().isEmpty() && ItemFromData.getData(((TF2DamageSource)s).getWeapon()).getName().equals("pistol")){
			((EntityPlayer)s.getEntity()).addStat(TF2Achievements.GUN_DOWN);
		}*/
	}
	
	@SideOnly(Side.CLIENT)
	public void renderGUI(BufferBuilder renderer, Tessellator tessellator, EntityPlayer player, int width, int height, GuiIngame gui) {
		ClientProxy.setColor(TF2Util.getTeamColor(player), 0.7f, 0, 0.25f, 0.8f);
		
		gui.drawTexturedModalRect(20, 2, 0, 112,124, 60);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
		gui.drawTexturedModalRect(0, 0, 0, 48, 144, 64);
		/*renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(width / 2 - 72, height / 2 + 84, 0.0D).tex(0.0D, 0.4375D).endVertex();
		renderer.pos(width / 2 + 72, height / 2 + 84, 0.0D).tex(0.5625D, 0.4375D).endVertex();
		renderer.pos(width / 2 + 72, height / 2 + 20, 0.0D).tex(0.5625D, 0.1875D).endVertex();
		renderer.pos(width / 2 - 72, height / 2 + 20, 0.0D).tex(0.0D, 0.1875D).endVertex();
		tessellator.draw();*/
		double imagePos = this.getLevel() == 1 ? 0.375D : this.getLevel() == 2 ? 0.1875D : 0D;

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(19, 56, 0.0D).tex(0.75D, imagePos + 0.1875D).endVertex();
		renderer.pos(67, 56, 0.0D).tex(0.9375D, imagePos + 0.1875D).endVertex();
		renderer.pos(67, 8, 0.0D).tex(0.9375D, imagePos).endVertex();
		renderer.pos(19, 8, 0.0D).tex(0.75D, imagePos).endVertex();
		tessellator.draw();

		imagePos = this.getLevel() == 3 ? 0D : 0.0625D;
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(67, 57, 0.0D).tex(0.9375D, 0.0625D + imagePos).endVertex();
		renderer.pos(83, 57, 0.0D).tex(1D, 0.0625D + imagePos).endVertex();
		renderer.pos(83, 41, 0.0D).tex(1D, imagePos).endVertex();
		renderer.pos(67, 41, 0.0D).tex(0.9375D, imagePos).endVertex();
		tessellator.draw();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(67, 21, 0.0D).tex(0.9375D, 0.25D).endVertex();
		renderer.pos(83, 21, 0.0D).tex(1D, 0.25D).endVertex();
		renderer.pos(83, 5, 0.0D).tex(1D, 0.1875D).endVertex();
		renderer.pos(67, 5, 0.0D).tex(0.9375D, 0.1875D).endVertex();
		tessellator.draw();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(67, 39, 0.0D).tex(0.9375D, 0.1875D).endVertex();
		renderer.pos(83, 39, 0.0D).tex(1D, 0.1875D).endVertex();
		renderer.pos(83, 23, 0.0D).tex(1D, 0.125D).endVertex();
		renderer.pos(67, 23, 0.0D).tex(0.9375D, 0.125D).endVertex();
		tessellator.draw();

		imagePos = this.getLevel() == 1 ? 0.3125D : this.getLevel() == 2 ? 0.375D : 0.4375D;
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(50, 18, 0.0D).tex(0.9375D, 0.0625D + imagePos).endVertex();
		renderer.pos(66, 18, 0.0D).tex(1D, 0.0625D + imagePos).endVertex();
		renderer.pos(66, 2, 0.0D).tex(1D, imagePos).endVertex();
		renderer.pos(50, 2, 0.0D).tex(0.9375D, imagePos).endVertex();
		tessellator.draw();

		gui.drawString(gui.getFontRenderer(), Integer.toString(this.getKills()),
				85, 9, 16777215);
		float health = this.getHealth() / this.getMaxHealth();
		if (health > 0.33f) {
			GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
		} else {
			GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		for (int i = 0; i < health * 11; i++) {

			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(19, 55 - i * 5, 0.0D).endVertex();
			renderer.pos(9, 55 - i * 5, 0.0D).endVertex();
			renderer.pos(9, 59 - i * 5, 0.0D).endVertex();
			renderer.pos(19, 59 - i * 5, 0.0D).endVertex();
			tessellator.draw();
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
		renderer.begin(7, DefaultVertexFormats.POSITION);
		renderer.pos(85, 38, 0.0D).endVertex();
		renderer.pos(140, 38, 0.0D).endVertex();
		renderer.pos(140, 24, 0.0D).endVertex();
		renderer.pos(85, 24, 0.0D).endVertex();
		tessellator.draw();

		renderer.begin(7, DefaultVertexFormats.POSITION);
		renderer.pos(85, 56, 0.0D).endVertex();
		renderer.pos(140, 56, 0.0D).endVertex();
		renderer.pos(140, 42, 0.0D).endVertex();
		renderer.pos(85, 42, 0.0D).endVertex();
		tessellator.draw();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
		renderer.begin(7, DefaultVertexFormats.POSITION);
		renderer.pos(85, 38, 0.0D).endVertex();
		renderer.pos(85 + (double) this.getAmmo() / (double) this.getMaxAmmo() * 55D,
				38, 0.0D).endVertex();
		renderer.pos(85 + (double) this.getAmmo() / (double) this.getMaxAmmo() * 55D,
				24, 0.0D).endVertex();
		renderer.pos(85, 24, 0.0D).endVertex();
		tessellator.draw();

		double xOffset = this.getLevel() < 3 ? this.getProgress() * 0.275D : this.getRocketAmmo() * 2.75D;
		renderer.begin(7, DefaultVertexFormats.POSITION);
		renderer.pos(85, 56, 0.0D).endVertex();
		renderer.pos(85 + xOffset, 56, 0.0D).endVertex();
		renderer.pos(85 + xOffset, 42, 0.0D).endVertex();
		renderer.pos(85, 42, 0.0D).endVertex();
		tessellator.draw();
	}
	
	public int getGuiHeight() {
		return 64;
	}
	
	public int getConstructionTime() {
		return 10500;
	}
	/*public EntityLookHelper getLookHelper() {
		return lookHelper;
	}*/
}
