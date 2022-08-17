package rafradek.TF2weapons.entity.ai;

import com.google.common.base.Predicate;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumHand;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.mercenary.EntityEngineer;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.TF2Util;

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
	protected boolean dodging;
	protected boolean dodge;
	public boolean jump;
	public float dodgeSpeed = 1f;
	public int jumprange;
	public int searchTimer;
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
		return building != null && building.isEntityAlive() && this.entityHost.isWithinHomeDistanceFromPosition(building.getPosition())
				&& (building.getMaxHealth() > building.getHealth()
						|| ((this.entityHost.getAttackTarget() == null || this.entityHost.getAttackTarget().isDead) && this.entityHost.hasSentryAndDispenser()
								&& building.canUseWrench()));
	}

	@Override
	public boolean shouldExecute() {
		this.searchTimer--;
		if (this.entityHost.grabbed != null || this.entityHost.loadout.getStackInSlot(2).isEmpty())
			return false;
		if(this.entityHost.getWepCapability().getMetal() <= 0){
			/*Entity
			if(!list.isEmpty()){
				list.sort(new EntityAINearestAttackableTarget.Sorter(this.entityHost));
				this.runMetal=true;
				this.attackTarget = list.get(0);
				this.entityHost.switchSlot(2);
				return true;
			}*/
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
					return TF2Util.isOnSameTeam(input, entityHost) && isValidTarget(input);
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
			TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
		}
		this.entityHost.getNavigator().clearPath();
		this.attackTarget = null;
		this.field_75318_f = 0;
		this.entityHost.switchSlot(0);
	}

	/**
	 * Updates the task
	 */

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
		boolean comeCloser = this.entityHost.getEntitySenses().canSee(this.attackTarget);

		this.entityHost.setJumping(true);
		if (comeCloser)
			++this.field_75318_f;
		else
			this.field_75318_f = 0;

		if (d0 <= this.attackRangeSquared && this.field_75318_f >= 6) {
			if (!this.dodging) {
				this.entityHost.getNavigator().clearPath();
				this.dodging = true;
			}
		} else {
			this.dodging = false;
			this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
		}

		this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);

		if (d0 <= this.attackRangeSquared && !this.runMetal) {
			if (!pressed) {
				pressed = true;
				((ItemUsable) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).startUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 1);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 1;
				TF2Util.sendTracking(new TF2Message.ActionMessage(1, entityHost), entityHost);
			}

		} else {
			if (pressed) {
				((ItemUsable) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).endUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 0);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
				TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
			}
			pressed = false;
		}
	}

}
