package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.EntityTF2Character.Order;

public class EntityAIFollowTrader extends EntityAIBase {

	public EntityTF2Character owner;
	private int timeToRecalcPath;

	public EntityAIFollowTrader(EntityTF2Character entity) {
		this.owner = entity;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return this.owner.getOwner() != null && this.owner.getDistanceSqToEntity(this.owner.getOwner()) > 100 && this.owner.isEntityAlive() && this.owner.getOrder() == Order.FOLLOW && 
				!(this.owner.getAttackTarget() != null && this.owner.getOwner().getDistanceSqToEntity(this.owner) < 600);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
	}

	public void resetTask() {
		this.owner.getNavigator().clearPathEntity();
	}
	@Override
	public boolean shouldContinueExecuting() {
		return !this.owner.getNavigator().noPath() && this.owner.getDistanceSqToEntity(this.owner.getOwner()) > 100;
	}

	@Override
	public void updateTask() {

		if(this.owner.getAttackTarget() == null || (this.owner.getAttackTarget().getDistanceSqToEntity(this.owner) < this.owner.getAttackTarget().getDistanceSqToEntity(this.owner.getOwner())))
			this.owner.getLookHelper().setLookPositionWithEntity(this.owner.getOwner(), 10.0F, 10F);

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 8;

			this.owner.getNavigator().tryMoveToEntityLiving(this.owner.getOwner(), 1.2f);
		}
	}
}
