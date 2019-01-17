package rafradek.TF2weapons.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.util.TF2Util;

public class EntityProjectileEnergy extends EntityProjectileSimple {

	double struck;
	
	public EntityProjectileEnergy(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.setType(4);
	}
	
	public boolean isPushable() {
		return false;
	}
	
	public boolean canPenetrate() {
		return true;
	}
	
	@Override
	public double getGravity() {
		return 0;
	}
	
	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		super.onHitMob(entityHit, mop);
		if (this.struck == 0) {
			this.struck = new Vec3d(this.motionX, this.motionY, this.motionZ).lengthVector();
			this.motionX = (this.motionX / this.struck) * 0.9;
			this.motionY = (this.motionY / this.struck) * 0.9;
			this.motionZ = (this.motionZ / this.struck) * 0.9;
		}
	}
	
	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if (this.struck == 0)
			super.onHitGround(x, y, z, mop);
	}
	
	@Override
	public void spawnParticles(double x, double y, double z) {
		ClientProxy.spawnBisonParticle(world, x, y, z, TF2Util.getTeamColor(this.shootingEntity));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.hitEntities.size() > 0) {
			this.hitEntities.clear();
		}
		else if(this.struck != 0) {
			this.motionX = (this.motionX * this.struck) / 0.9;
			this.motionY = (this.motionY * this.struck) / 0.9;
			this.motionZ = (this.motionZ * this.struck) / 0.9;
			this.struck = 0;
		}
	}
}
