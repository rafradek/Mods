package rafradek.TF2weapons.entity.projectile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class EntityRocketEffect extends Particle {
	public EntityRocket rocket;
	private boolean nextDead;

	public EntityRocketEffect(World par1World, EntityRocket rocket) {
		super(par1World, rocket.posX, rocket.posY, rocket.posZ);
		// this.noClip=true;
		this.rocket = rocket;
		this.particleScale = 1;
		this.particleMaxAge = 200;
		this.setSize(0.1f, 0.1f);
		this.setParticleTextureIndex(163);
		this.particleAlpha = 0.75f;
		// this.setParticleIcon(TF2EventBusListener.pelletIcon);
		// this.setParticleTextureIndex(81);
		this.multipleParticleScaleBy(2);
		// TODO Auto-generated constructor stub
		this.setRBGColorF(1f, 0.5f, 0f);
		this.particleTextureJitterX = this.rand.nextFloat();
		this.particleTextureJitterY = this.rand.nextFloat();
	}

	@Override
	public void onUpdate() {
		if (rocket.isDead) {
			this.setExpired();
			return;
		}
		this.setPosition(rocket.prevPosX - rocket.motionX * 0.5f, rocket.prevPosY - rocket.motionY * 0.5f,
				rocket.prevPosZ - rocket.motionZ * 0.5f);
		this.motionX = rocket.motionX;
		this.motionY = rocket.motionY;
		this.motionZ = rocket.motionZ;
		super.onUpdate();
	}

	/*
	 * public int getFXLayer() { return 2; }
	 */

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_) {
		return 15728880;
	}

	public float getBrightness(float p_70013_1_) {
		return 1.0F;
	}
}
