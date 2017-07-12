package rafradek.TF2weapons.characters.ai;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemUsable;

public class EntityAIRepair extends EntityAIBase {

	/** The entity the AI instance has been applied to */
	protected final EntityEngineer entityHost;

	/**
	 * The entity (as a RangedAttackMob) the AI instance has been applied to.
	 */
	protected EntityBuilding attackTarget;

	protected float entityMoveSpeed;
	protected int field_75318_f;

	/**
	 * The maximum time the AI has to wait before peforming another ranged
	 * attack.
	 */
	private float attackRange;
	protected float attackRangeSquared;

	protected boolean pressed;
	private boolean altpreesed;
	protected boolean dodging;
	protected boolean dodge;
	public boolean jump;
	public float dodgeSpeed = 1f;
	public int jumprange;
	public int searchTimer;
	private boolean firstTick;
	private boolean runMetal;

	public float gravity;

	public boolean explosive;

	public EntityAIRepair(EntityEngineer par1IRangedAttackMob, float par2, float par5) {
		this.field_75318_f = 0;
		this.entityHost = par1IRangedAttackMob;
		this.entityMoveSpeed = par2;
		this.attackRange = par5;
		this.attackRangeSquared = par5 * par5;

		this.setMutexBits(3);
	}

	public void setRange(float range) {
		this.attackRange = range;
		this.attackRangeSquared = range * range;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean isValidTarget(EntityBuilding building) {
		return building != null && building.isEntityAlive()
				&& (building.getMaxHealth() > building.getHealth()
						|| ((this.entityHost.getAttackTarget() == null || this.entityHost.getAttackTarget().isDead)
								&& building.canUseWrench()));
	}

	@Override
	public boolean shouldExecute() {
		this.searchTimer--;
		if(this.entityHost.getWepCapability().getMetal() <= 0){
			List<EntityDispenser> list=this.entityHost.world.getEntitiesWithinAABB(EntityDispenser.class,
				this.entityHost.getEntityBoundingBox().grow(10, 3, 10), new Predicate<EntityDispenser>() {

					@Override
					public boolean apply(EntityDispenser input) {
						return TF2weapons.isOnSameTeam(input, entityHost) && !input.isDisabled() &&input.getMetal()>0;
					}

				});
			if(!list.isEmpty()){
				list.sort(new EntityAINearestAttackableTarget.Sorter(this.entityHost));
				this.runMetal=true;
				this.attackTarget = list.get(0);
				this.entityHost.switchSlot(2);
				return true;
			}
			return false;
		}
		this.runMetal=false;
		EntityBuilding building = this.entityHost.sentry;
		if (this.isValidTarget(building) || this.isValidTarget(building = this.entityHost.dispenser)) {
			this.attackTarget = building;
			this.entityHost.switchSlot(2);
			return true;
		} else if (this.searchTimer <= 0) {
			this.searchTimer = 4;
			for (EntityBuilding build : this.entityHost.world.getEntitiesWithinAABB(EntityBuilding.class,
					this.entityHost.getEntityBoundingBox().grow(10, 3, 10), new Predicate<EntityBuilding>() {

						@Override
						public boolean apply(EntityBuilding input) {
							return TF2weapons.isOnSameTeam(input, entityHost) && isValidTarget(input);
						}

					})) {
				this.attackTarget = build;
				this.entityHost.switchSlot(2);
				return true;
			}
		}

		return this.attackTarget != null;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting() {
		return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		if (this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state != 0) {
			pressed = false;
			if (this.entityHost.getHeldItemMainhand().getItem() instanceof ItemWeapon)
				((ItemWeapon) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).endUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 0);
			this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
			TF2weapons.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
		}
		this.entityHost.getNavigator().clearPathEntity();
		this.attackTarget = null;
		this.field_75318_f = 0;
		this.entityHost.switchSlot(0);
	}

	/**
	 * Updates the task
	 */
	public double lookingAtMax() {
		if (this.attackTarget == null)
			return 0;
		double d0 = this.attackTarget.posX - this.entityHost.posX;
		double d1 = (this.attackTarget.posY + this.attackTarget.getEyeHeight())
				- (this.entityHost.posY + this.entityHost.getEyeHeight());
		double d2 = this.attackTarget.posZ - this.entityHost.posZ;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		float f = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
		float f1 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
		float compareyaw = Math
				.abs(180 - Math.abs(Math.abs(f - MathHelper.wrapDegrees(this.entityHost.rotationYawHead)) - 180));
		float comparepitch = Math.abs(180 - Math.abs(Math.abs(f1 - this.entityHost.rotationPitch) - 180));
		// System.out.println("Angled: "+compareyaw+" "+comparepitch);
		return Math.max(compareyaw, comparepitch);
	}

	@Override
	public void updateTask() {
		if (!this.isValidTarget(attackTarget))
			this.attackTarget = null;
		if (this.attackTarget == null) {
			this.resetTask();
			return;
		}
		double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY,
				this.attackTarget.posZ);

		double lookX = this.attackTarget.posX;
		double lookY = this.attackTarget.posY + this.attackTarget.getEyeHeight();
		double lookZ = this.attackTarget.posZ;
		// System.out.println("raytracing:"+this.entityHost.world.rayTraceBlocks(new
		// Vec3d(this.entityHost.posX,this.entityHost.posY+this.entityHost.getEyeHeight(),this.entityHost.posZ),
		// new Vec3d(lookX,this.attackTarget.posY,lookZ)));
		boolean comeCloser = this.entityHost.getEntitySenses().canSee(this.attackTarget);
		// boolean flag =
		// comeCloser&&TF2weapons.lookingAt(this.entityHost,15,lookX,lookY,lookZ);
		this.entityHost.setJumping(true);
		if (comeCloser)
			++this.field_75318_f;
		else
			this.field_75318_f = 0;

		if (d0 <= this.attackRangeSquared && this.field_75318_f >= 6) {
			if (!this.dodging) {
				this.entityHost.getNavigator().clearPathEntity();
				this.dodging = true;
			}
		} else {
			this.dodging = false;
			/*
			 * if(this.entityHost.onGround&&this.entityHost instanceof
			 * EntitySoldier&&this.entityHost.getHeldItem(EnumHand.MAIN_HAND).
			 * getItemDamage()<this.entityHost.getHeldItem(EnumHand.MAIN_HAND).
			 * getMaxDamage()-1){
			 * ((EntitySoldier)this.entityHost).rocketJump=true; }
			 */
			this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
		}
		// if(!(this.entityHost instanceof
		// EntitySoldier&&((EntitySoldier)this.entityHost).rocketJump))
		this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);
		// this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget,
		// 1.0F, 90.0F);
		if (d0 <= this.attackRangeSquared/* && this.entityHost.getAmmo() > 0 */&& !this.runMetal) {
			if (!pressed) {
				pressed = true;
				((ItemUsable) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).startUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 1);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 1;
				TF2weapons.sendTracking(new TF2Message.ActionMessage(1, entityHost), entityHost);
				// System.out.println("coœdo");
			}

		} else {
			if (pressed) {
				((ItemUsable) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).endUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 0);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
				TF2weapons.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
				// System.out.println("coœz");
			}
			pressed = false;
		}
		// if(){
		// }
	}

}
