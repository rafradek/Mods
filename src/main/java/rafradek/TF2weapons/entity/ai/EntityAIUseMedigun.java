package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemMedigun;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.TF2Util;

public class EntityAIUseMedigun extends EntityAIUseRangedWeapon {

	public EntityAIUseMedigun(EntityTF2Character par1IRangedAttackMob, float par2, float par5) {
		super(par1IRangedAttackMob, par2, par5);

	}

	@Override
	public boolean shouldExecute() {
		return this.entityHost.getHeldItemMainhand().getItem() instanceof ItemMedigun && super.shouldExecute();
	}
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */

	/**
	 * Resets the task
	 */

	@Override
	public void resetTask() {
		this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget(-1);
		this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
		TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
		TF2Util.sendTracking(new TF2Message.CapabilityMessage(entityHost, false), entityHost);
		if (this.jump)
			this.entityHost.jump = false;
		this.attackTarget = null;
		this.comeCloser = 0;
		this.rangedAttackTime = -1;
		this.pressed = false;
	}

	@Override
	public void updateTask() {
		if ((this.attackTarget != null && this.attackTarget.deathTime > 0) || this.entityHost.deathTime > 0) {
			this.resetTask();
			return;
		}
		if (this.attackTarget == null)
			return;
		double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY,
				this.attackTarget.posZ);

		double lookX = this.attackTarget.posX;
		double lookY = this.attackTarget.posY + this.attackTarget.getEyeHeight();
		double lookZ = this.attackTarget.posZ;
		/*boolean stay = this.entityHost.getEntitySenses().canSee(this.attackTarget);
		this.entityHost.setJumping(true);
		if (stay) {
			++this.comeCloser;
			if (d0 <= (double) 12)
				this.comeCloser = 20;
		} else
			this.comeCloser = 0;
		//System.out.println(this.comeCloser+" "+this.attackRangeSquared+" "+d0);
		if (d0 <= 52 && this.comeCloser >= 20) {
			if (!this.dodging) {
				this.entityHost.getNavigator().clearPath();
				this.dodging = true;
			}
		} else {
			this.dodging = false;
			this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
		}*/

		this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);
		// this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget,
		// 1.0F, 90.0F);
		double range = ItemFromData.getData(this.entityHost.getHeldItemMainhand()).getFloat(PropertyType.RANGE);
		if (d0 <= range * range) {

			if (!pressed) {
				pressed = true;
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget(this.attackTarget.getEntityId());
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 1;
				TF2Util.sendTracking(new TF2Message.ActionMessage(1, entityHost), entityHost);
				TF2Util.sendTracking(new TF2Message.CapabilityMessage(entityHost, false), entityHost);
				// System.out.println("coœdo");
			}
			else {
				if((this.attackTarget.getHealth()/this.attackTarget.getMaxHealth() < 0.35F && (this.attackTarget.ticksExisted - this.attackTarget.getRevengeTimer()) < 25)
						|| (this.attackTarget instanceof EntityPlayer && this.attackTarget.getCapability(TF2weapons.PLAYER_CAP, null).medicCharge)) {
					Potion effect=Potion.getPotionFromResourceLocation(ItemFromData.getData(this.entityHost.getHeldItemMainhand()).getString(PropertyType.EFFECT_TYPE));
					if (this.attackTarget.getActivePotionEffect(effect) == null)
					((ItemMedigun)this.entityHost.getHeldItemMainhand().getItem()).startUse(this.entityHost.getHeldItemMainhand(), this.entityHost, this.entityHost.world, 0, 2);
				}
			}

		} else {
			if (pressed) {
				if (this.jump)
					this.entityHost.jump = false;
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget(-1);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
				TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
				TF2Util.sendTracking(new TF2Message.CapabilityMessage(entityHost, false), entityHost);
				//System.out.println("coœz");
			}
			pressed = false;
		}
		// if(){
		if (this.jump && d0 < this.jumprange)
			this.entityHost.jump = true;
		else if (this.jump)
			this.entityHost.jump = false;

		// }
	}

}
