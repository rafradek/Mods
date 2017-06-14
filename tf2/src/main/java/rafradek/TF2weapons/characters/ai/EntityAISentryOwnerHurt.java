package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import rafradek.TF2weapons.building.EntitySentry;

public class EntityAISentryOwnerHurt extends EntityAITarget {

	public EntityLivingBase target;
	public int timer;
	public EntitySentry sentry;

	public EntityAISentryOwnerHurt(EntitySentry creature, boolean checkSight) {
		super(creature, checkSight);
		this.sentry = creature;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase owner = this.sentry.getOwner();
		if (owner != null) {
			this.target = owner.getAITarget();
			return (this.sentry.getAttackFlags() & 1) == 1 && this.isSuitableTarget(this.target, false);
		}
		return false;
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.target);

		super.startExecuting();
	}

}
