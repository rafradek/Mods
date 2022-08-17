package rafradek.TF2weapons.entity.projectile;

import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2ConfigVars;

public class EntityFuryFireball extends EntityProjectileSimple {

	public EntityFuryFireball(World world) {
		super(world);
		if (TF2ConfigVars.dynamicLights)
			this.makeLit();
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
	public void addDamageTypes(DamageSource source) {
		source.setFireDamage();
	}

	@Override
	public int getBrightnessForRender() {
		return 15728880;
	}

	@Override
	public boolean isBurning() {
		return true;
	}

	@Optional.Method(modid = "dynamiclights")
	@Override
	public int getLightLevel() {
		return 12;
	}

	@Override
	public int getMaxTime() {
		return 4;
	}

	@Override
	public float getCollisionSize() {
		return 0.4f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		double d0 = this.posX - x;
		double d1 = this.posY - y;
		double d2 = this.posZ - z;
		double d3 = d0 * d0 + d1 * d1 + d2 * d2;
		return this.isInRangeToRenderDist(d3);
	}
}
