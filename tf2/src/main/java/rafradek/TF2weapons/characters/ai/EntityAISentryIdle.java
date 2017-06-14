package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.building.EntitySentry;

public class EntityAISentryIdle extends EntityAIBase {

	EntitySentry host;
	boolean direction;

	public EntityAISentryIdle(EntitySentry sentry) {
		this.host = sentry;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return this.host.getAttackTarget() == null;
	}

	@Override
	public void updateTask() {
		// System.out.println("Rotating");
		if (this.direction) {
			this.host.rotationYawHead += 2.5f;
			if (this.host.rotationYawHead >= this.host.rotationYaw)
				this.direction = false;
		} else {
			this.host.rotationYawHead -= 2.5f;
			if (this.host.rotationYawHead <= this.host.rotationYaw)
				this.direction = true;
		}
	}

}
