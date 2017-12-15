package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.characters.EntityTF2Character;

public class EntityAIFindDispenser extends EntityAIBase {

	public EntityTF2Character host;
	
	public float range;
	public float rangeSq;
	
	public EntityDispenser target;
	public Path path;
	
	public int delay;
	
	public EntityAIFindDispenser(EntityTF2Character host, float range) {
		this.host=host;
		this.range=range;
		this.rangeSq=range*range;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		if(this.host.getAmmo() <= 0 || (this.host.getAttackTarget() == null && (this.host.getHealth()<this.host.getMaxHealth() || !this.host.isAmmoFull()))) {
			for(EntityDispenser disp : host.world.getEntitiesWithinAABB(EntityDispenser.class, this.host.getEntityBoundingBox().grow(range, range/4f, range), disp -> {
				return disp.isEntityAlive() && host.getDistanceSqToEntity(disp) <= rangeSq && TF2Util.isOnSameTeam(host, disp);
			})) {
				this.target = disp;
				this.path=host.getNavigator().getPathToEntityLiving(disp);
				return this.path != null;
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		this.host.getNavigator().setPath(path, 1.0f);
	}
	
	@Override
	public boolean shouldContinueExecuting()
    {
		return this.target != null && target.isEntityAlive() && !(this.host.isAmmoFull() && this.host.getHealth() >= this.host.getMaxHealth());
    }
	
	@Override
	public void updateTask() {
		if(this.host.getDistanceSqToEntity(target) > 2) {
			if(this.host.ticksExisted % 9 == 0)
				if(!this.host.getNavigator().tryMoveToEntityLiving(host, 1.0f)) {
					this.target=null;
					return;
				}
			if (this.host.getAttackTarget() == null)
			this.host.getLookHelper().setLookPositionWithEntity(this.target, 30, 15);
		}
		else
			this.host.getNavigator().clearPathEntity();
	}
}
