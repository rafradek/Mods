package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.util.TF2Util;

public class EntityAISentryAttack extends EntityAIBase {

	public EntitySentry host;
	private boolean lockTarget;

	public EntityAISentryAttack(EntitySentry sentry) {
		this.host = sentry;
		this.setMutexBits(1);
	}

	@Override
	public void resetTask() {
		this.host.setSoundState(0);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return !this.host.isDisabled() && !this.host.isControlled() && (this.host.getAttackTarget()) != null
				&& this.host.getEntitySenses().canSee(this.host.getAttackTarget());
	}

	@Override
	public void updateTask() {

		EntityLivingBase target = this.host.getAttackTarget();
		if (target == null)
			return;
		
		if ((target != null && target.deathTime > 0) || this.host.deathTime > 0) {
			this.resetTask();
			return;
		}
		EntityLivingBase owner = this.host.getOwner();
		if (owner == null || owner.isDead)
			owner = this.host;
		double lookX = target.posX;
		double lookY = target.posY + target.height / 2;
		double lookZ = target.posZ;
		if (this.lockTarget)
			this.host.getLookHelper().setLookPosition(lookX, lookY, lookZ, 30, 75);
		else
			this.host.getLookHelper().setLookPosition(lookX, lookY, lookZ, (5f + this.host.getLevel() * 2.25f) * (this.host.isMini() ? 1.35f : 1f), 50);
		if (TF2Util.lookingAt(this.host, 24, lookX, lookY, lookZ)) {
			this.lockTarget = true;
			this.host.shootBullet(owner);
			this.host.shootRocket(owner);
		} else {
			this.lockTarget = false;
			if (this.host.getSoundState() > 2)
				this.host.setSoundState(1);
		}
	}
}
