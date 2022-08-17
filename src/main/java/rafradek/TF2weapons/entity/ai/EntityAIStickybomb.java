package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.mercenary.EntityDemoman;
import rafradek.TF2weapons.item.ItemStickyLauncher;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.TF2Util;

public class EntityAIStickybomb extends EntityAIBase {

	/** The entity the AI instance has been applied to */
	protected final EntityDemoman entityHost;

	/**
	 * The entity (as a RangedAttackMob) the AI instance has been applied to.
	 */

	protected float entityMoveSpeed;

	/**
	 * The maximum time the AI has to wait before peforming another ranged
	 * attack.
	 */
	protected float attackRangeSquared;

	protected boolean pressed;
	protected boolean dodging;
	public boolean jump;
	public float dodgeSpeed = 1f;
	public int jumprange;
	public int searchTimer;
	public Vec3d attackTarget;

	public float gravity;

	public boolean explosive;

	public EntityAIStickybomb(EntityDemoman par1IRangedAttackMob, float par2, float par5) {
		this.entityHost = par1IRangedAttackMob;
		this.entityMoveSpeed = par2;
		this.attackRangeSquared = par5 * par5;

		this.setMutexBits(3);
	}

	public void setRange(float range) {
		this.attackRangeSquared = range * range;
	}

	@Override
	public boolean shouldExecute() {
		if (this.entityHost.isRobot() || this.entityHost.getAttackTarget() != null || this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).activeBomb.size()>7)
			return false;
		ItemStack stickybomb = this.entityHost.loadout.getStackInSlot(1);
		return stickybomb.getItem() instanceof ItemStickyLauncher && ((ItemUsable) stickybomb.getItem()).isAmmoSufficient(stickybomb, this.entityHost, true);
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
		this.attackTarget=null;
		this.entityHost.switchSlot(0);
	}

	/**
	 * Updates the task
	 */

	@Override
	public void updateTask() {
		if(this.attackTarget==null)
			this.attackTarget=RandomPositionGenerator.findRandomTarget(this.entityHost, 10, 4);
		if(this.attackTarget==null)
			return;
		this.entityHost.switchSlot(1);
		if (!(this.entityHost.getHeldItemMainhand().getItem() instanceof ItemStickyLauncher))
			return;
		double d0 = this.entityHost.getDistanceSq(this.attackTarget.x, this.attackTarget.y,
				this.attackTarget.z);

		double lookX = this.attackTarget.x;
		double lookY = this.attackTarget.y;
		double lookZ = this.attackTarget.z;

		if (d0 <= this.attackRangeSquared) {
			if (!this.dodging) {
				this.entityHost.getNavigator().clearPath();
				this.dodging = true;
			}
		} else {
			this.dodging = false;

			this.entityHost.getNavigator().tryMoveToXYZ(this.attackTarget.x,this.attackTarget.y,this.attackTarget.z, this.entityMoveSpeed);
		}
		this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);
		if (d0 <= this.attackRangeSquared) {
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
		// if(){
		// }
	}

}
