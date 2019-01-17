package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character.Order;

public class EntityAIHold extends EntityAIBase {

	public EntityTF2Character owner;
	private int timeToRecalcPath;

	public EntityAIHold(EntityTF2Character entity) {
		this.owner = entity;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return this.owner.getOwner() != null && this.owner.getDistanceSq(this.owner.getOwner()) > 100 && this.owner.isEntityAlive() && this.owner.getOrder() == Order.FOLLOW && 
				!(this.owner.getAttackTarget() != null && this.owner.getOwner().getDistanceSq(this.owner) < 600);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
	}

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

			this.owner.getNavigator().tryMoveToEntityLiving(this.owner.getOwner(), 1.2f);
		}
	}
}
