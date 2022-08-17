package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character.Order;
import rafradek.TF2weapons.util.TF2Util;

public class EntityAIFollowTrader extends EntityAIBase {

	public EntityTF2Character owner;
	private int timeToRecalcPath;

	public EntityAIFollowTrader(EntityTF2Character entity) {
		this.owner = entity;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return this.owner.getOwner() != null && this.owner.getDistanceSq(this.owner.getOwner()) > 100 && this.owner.isEntityAlive() && this.owner.getOrder() == Order.FOLLOW &&
				!(this.owner.getAttackTarget() != null && this.owner.getOwner().getDistanceSq(this.owner) < 600);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
	}

	@Override
	public void resetTask() {
		this.owner.getNavigator().clearPath();
	}
	@Override
	public boolean shouldContinueExecuting() {
		return !this.owner.getNavigator().noPath() && this.owner.getDistanceSq(this.owner.getOwner()) > 100;
	}

	@Override
	public void updateTask() {

		if(this.owner.getAttackTarget() == null || (this.owner.getAttackTarget().getDistanceSq(this.owner) < this.owner.getAttackTarget().getDistanceSq(this.owner.getOwner())))
			this.owner.getLookHelper().setLookPositionWithEntity(this.owner.getOwner(), 10.0F, 10F);

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 8;

			double distance = this.owner.getDistanceSq(this.owner.getOwner());
			boolean move = this.owner.getNavigator().tryMoveToEntityLiving(this.owner.getOwner(), 1.08f);

			if((!move && distance > 750) || (this.owner.getNavigator().noPath() && distance > 144)) {

				TF2Util.teleportSafe(owner, owner.getOwner());
			}
		}
	}


}
