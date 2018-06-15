package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.building.EntitySentry;

public class EntityAISentryAttack extends EntityAIBase {

	public EntitySentry host;
	public EntityLivingBase target;
	private boolean lockTarget;

	public EntityAISentryAttack(EntitySentry sentry) {
		this.host = sentry;
		this.setMutexBits(1);
	}

	@Override
	public void resetTask() {
		this.target = null;
		this.host.setSoundState(0);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return !this.host.isDisabled() && !this.host.isControlled() && (target = this.host.getAttackTarget()) != null
				&& this.host.getEntitySenses().canSee(this.host.getAttackTarget());
	}

	@Override
	public void updateTask() {
		// System.out.println("Executing: "+this.target+"
		// "+this.host.attackDelay);
		if ((this.target != null && this.target.deathTime > 0) || this.host.deathTime > 0) {
			this.resetTask();
			return;
		}
		if (this.target == null)
			return;
		EntityLivingBase owner = this.host.getOwner();
		if (owner == null || owner.isDead)
			owner = this.host;
		double lookX = this.target.posX;
		double lookY = this.target.posY + this.target.height / 2;
		double lookZ = this.target.posZ;
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
