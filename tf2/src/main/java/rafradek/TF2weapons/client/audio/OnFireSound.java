package rafradek.TF2weapons.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;

public class OnFireSound extends MovingSound {

	public Entity target;

	public OnFireSound(SoundEvent soundIn, Entity target) {
		super(soundIn, target.getSoundCategory());
		this.xPosF = (float) target.posX;
		this.yPosF = (float) target.posY;
		this.zPosF = (float) target.posZ;
		this.volume = 0.6f;
		this.pitch = 1f;
		this.target = target;
		this.repeat = true;
	}

	@Override
	public void update() {
		this.xPosF = (float) target.posX;
		this.yPosF = (float) target.posY;
		this.zPosF = (float) target.posZ;
		if (this.target.ticksExisted - this.target.getEntityData().getInteger("LastHitBurn") > 5)
			this.donePlaying = true;
	}

}
