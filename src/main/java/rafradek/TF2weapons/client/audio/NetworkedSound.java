package rafradek.TF2weapons.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class NetworkedSound extends MovingSound {

	private Entity parent;
	private boolean isStatic;
	private int id;
	public NetworkedSound(Entity parent, SoundEvent soundIn, SoundCategory categoryIn, float volume, float pitch, int id, boolean repeat) {
		super(soundIn, categoryIn);
		this.parent = parent;
		this.volume = volume;
		this.pitch = pitch;
		this.id = id;
		this.repeat = repeat;
		// TODO Auto-generated constructor stub
	}

	public NetworkedSound(Vec3d pos, SoundEvent soundIn, SoundCategory categoryIn, float volume, float pitch, int id, boolean repeat) {
		super(soundIn, categoryIn);
		this.isStatic = true;
		this.volume = volume;
		this.pitch = pitch;
		this.id = id;
		this.repeat = repeat;
		this.xPosF = (float) pos.x;
		this.yPosF = (float) pos.y;
		this.zPosF = (float) pos.z;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		if (!this.isStatic) {
			if (parent != null && !parent.isDead) {
				this.xPosF = (float) parent.posX;
				this.yPosF = (float) parent.posY;
				this.zPosF = (float) parent.posZ;
			}
			else
				this.donePlaying = true;
		}
	}

}
