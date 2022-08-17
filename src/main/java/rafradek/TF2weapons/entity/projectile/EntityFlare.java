package rafradek.TF2weapons.entity.projectile;

import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import rafradek.TF2weapons.TF2ConfigVars;

public class EntityFlare extends EntityProjectileSimple {

	public EntityFlare(World world) {
		super(world);
		if (TF2ConfigVars.dynamicLights)
			this.makeLit();
	}

	@Override
	public double getGravity() {
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
	public int getBrightnessForRender() {
		return 15728880;
	}

	@Override
	public void addDamageTypes(DamageSource source) {
		source.setFireDamage();
	}

	@Override
	public boolean isBurning() {
		return true;
	}

	@Override
	public float getExplosionSize() {
		return 2.1f;
	}

	@Optional.Method(modid = "dynamiclights")
	@Override
	public int getLightLevel() {
		return 11;
	}
}
