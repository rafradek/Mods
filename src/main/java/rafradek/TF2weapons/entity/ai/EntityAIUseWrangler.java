package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.entity.mercenary.EntityEngineer;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.TF2Util;

public class EntityAIUseWrangler extends EntityAIUseRangedWeapon {

	public EntityAIUseWrangler(EntityTF2Character par1IRangedAttackMob, float par2, float par5) {
		super(par1IRangedAttackMob, par2, par5);
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		return ((EntityEngineer) this.entityHost).shouldUseWrangler() && super.shouldExecute();
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
		if (this.entityHost.usedSlot == 1) {
			this.entityHost.switchSlot(0);
			this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
			TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);

		}
		if (this.jump)
			this.entityHost.jump = false;
		this.attackTarget = null;
		this.comeCloser = 0;
		this.rangedAttackTime = -1;
		this.pressed = false;

	}

	@Override
	public void updateTask() {
		if ((this.attackTarget != null && this.attackTarget.deathTime > 0) || this.entityHost.deathTime > 0
				|| !((EntityEngineer) this.entityHost).shouldUseWrangler()) {
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
		this.entityHost.switchSlot(1);
		ItemStack stack = this.entityHost.getHeldItemMainhand();
		EntitySentry sentry = ((EntityEngineer) this.entityHost).sentry;
		/*
		 * boolean stay = this.entityHost.getEntitySenses().canSee(this.attackTarget);
		 * this.entityHost.setJumping(true); if (stay) { ++this.comeCloser; if (d0 <=
		 * (double) 12) this.comeCloser = 20; } else this.comeCloser = 0;
		 * //System.out.println(this.comeCloser+" "+this.attackRangeSquared+" "+d0); if
		 * (d0 <= 52 && this.comeCloser >= 20) { if (!this.dodging) {
		 * this.entityHost.getNavigator().clearPath(); this.dodging = true; } } else {
		 * this.dodging = false;
		 * this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget,
		 * this.entityMoveSpeed); }
		 */

		this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);
		// this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget,
		// 1.0F, 90.0F);
		double range = 60;
		Entity entityhit = TF2Util.pierce(this.entityHost.world, sentry, sentry.posX,
				sentry.posY + sentry.getEyeHeight(), sentry.posZ, entityHost.getAttackTarget().posX,
				entityHost.getAttackTarget().posY + entityHost.getAttackTarget().getEyeHeight(),
				entityHost.getAttackTarget().posZ, false, 0, false).get(0).entityHit;
		boolean overlap = entityhit == this.entityHost;

		if (overlap) {
			Vec3d movevec = Vec3d.fromPitchYaw(0, sentry.rotationYawHead + 90);
			this.entityHost.getNavigator().tryMoveToXYZ(this.entityHost.posX + movevec.x,
					this.entityHost.posY + movevec.y, this.entityHost.posZ + movevec.z, 1f);
		}

		if (d0 <= range * range && !overlap) {

			if (!pressed) {
				pressed = true;
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 3;
				TF2Util.sendTracking(new TF2Message.ActionMessage(3, entityHost), entityHost);
				// System.out.println("coœdo");
			}

		} else {
			if (pressed) {
				if (this.jump)
					this.entityHost.jump = false;
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
				TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);

				// System.out.println("coœz");
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
