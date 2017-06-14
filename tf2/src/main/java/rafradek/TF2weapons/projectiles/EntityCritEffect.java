package rafradek.TF2weapons.projectiles;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCritEffect extends Particle {

	public EntityCritEffect(World worldIn, double p_i46352_2_, double p_i46352_4_, double p_i46352_6_, int team) {
		super(worldIn, p_i46352_2_, p_i46352_4_, p_i46352_6_);
		this.setParticleTextureIndex(0);
		this.setPosition(this.posX + this.rand.nextFloat() * 0.2f - 0.1f,
				this.posY + this.rand.nextFloat() * 0.2f - 0.1f, this.posZ + this.rand.nextFloat() * 0.2f - 0.1f);
		this.particleScale *= this.rand.nextFloat() * 5F + 2F;
		if (team == 0)
			this.setRBGColorF(1, 0, 0);
		else
			this.setRBGColorF(0, 0, 1);
		this.motionY += 0.03;
		this.particleAlpha = Math.min(1 / this.particleScale * 3, 1);
		this.particleMaxAge = 20;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.particleAlpha *= 0.9f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_) {
		return 15728880;
	}

	public float getBrightness(float p_70013_1_) {
		return 1.0F;
	}
}
