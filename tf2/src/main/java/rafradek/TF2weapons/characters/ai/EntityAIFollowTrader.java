package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.characters.EntityTF2Character;

public class EntityAIFollowTrader extends EntityAIBase {

	public EntityTF2Character owner;
	private int timeToRecalcPath;

	public EntityAIFollowTrader(EntityTF2Character entity) {
		this.owner = entity;
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return this.owner.lastTrader != null && this.owner.traderFollowTicks > 0 && this.owner.getAttackTarget() == null
				&& this.owner.getDistanceSqToEntity(owner.lastTrader) > 100;
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.owner.getNavigator().noPath() && this.owner.getDistanceSqToEntity(this.owner.lastTrader) > (100);
	}

	@Override
	public void updateTask() {
		// System.out.println("Up");
		if (this.owner.lastTrader.getRevengeTarget() != null)
			this.owner.setAttackTarget(owner.lastTrader.getRevengeTarget());
		this.owner.getLookHelper().setLookPositionWithEntity(this.owner.lastTrader, 10.0F, 10F);

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 8;

			this.owner.getNavigator().tryMoveToEntityLiving(this.owner.lastTrader, 1);
		}
	}
}
