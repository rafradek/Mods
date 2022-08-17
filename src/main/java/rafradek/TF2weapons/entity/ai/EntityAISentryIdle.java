package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import rafradek.TF2weapons.entity.building.EntitySentry;

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
		return this.host.getAttackTarget() == null && !this.host.isDisabled();
	}

	@Override
	public void updateTask() {
		//System.out.println("Rotating "+this.host.rotationYaw+" "+this.host.rotationYawHead);
		if (this.direction) {
			this.host.rotationYawHead += 2.5f;
			if (this.host.rotationYawHead >= this.host.rotationYaw+50)
				this.direction = false;
		} else {
			this.host.rotationYawHead -= 2.5f;
			if (this.host.rotationYawHead <= this.host.rotationYaw-50)
				this.direction = true;
		}
	}

}
