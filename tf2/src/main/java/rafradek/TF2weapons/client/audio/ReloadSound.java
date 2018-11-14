package rafradek.TF2weapons.client.audio;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class ReloadSound extends PositionedSound implements ITickableSound {

	public boolean done;

	public ReloadSound(SoundEvent soundResource, Entity entity) {
		super(soundResource, SoundCategory.NEUTRAL);
		this.xPosF = (float) entity.posX;
		this.yPosF = (float) entity.posY;
		this.zPosF = (float) entity.posZ;
		this.volume = 0.6f;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDonePlaying() {
		// TODO Auto-generated method stub
		return this.done;
	}

}
