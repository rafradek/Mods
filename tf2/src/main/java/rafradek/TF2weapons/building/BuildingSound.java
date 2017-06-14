package rafradek.TF2weapons.building;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class BuildingSound extends MovingSound {

	public EntityBuilding sentry;
	private int state;

	protected BuildingSound(EntityBuilding sentry, SoundEvent location, int state) {
		super(location, SoundCategory.NEUTRAL);
		this.sentry = sentry;
		this.volume = 1f;
		this.repeat = true;
		this.state = state;
	}

	@Override
	public void update() {
		this.xPosF = (float) sentry.posX;
		this.yPosF = (float) sentry.posY;
		this.zPosF = (float) sentry.posZ;
		if (this.sentry.getHealth() <= 0 || this.sentry.isDead)
			this.stopPlaying();
	}

	public void stopPlaying() {
		this.donePlaying = true;
	}

}
