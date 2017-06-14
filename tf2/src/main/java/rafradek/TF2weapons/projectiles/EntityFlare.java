package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import rafradek.TF2weapons.TF2weapons;

public class EntityFlare extends EntityProjectileSimple {

	public EntityFlare(World world) {
		super(world);
		if (TF2weapons.dynamicLights)
			this.makeLit();
	}

	public EntityFlare(World world, EntityLivingBase living, EnumHand hand) {
		super(world, living, hand);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double getGravity() {
		return 0.019f;
	}

	@Override
	public void spawnParticles(double x, double y, double z) {
		if (this.isInWater())
			this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, this.motionX, this.motionY,
					this.motionZ);
		else
			this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 15728880;
	}

	@Override
	public boolean isBurning() {
		return true;
	}

	@Optional.Method(modid = "dynamiclights")
	@Override
	public int getLightLevel() {
		// TODO Auto-generated method stub
		return 11;
	}
}
