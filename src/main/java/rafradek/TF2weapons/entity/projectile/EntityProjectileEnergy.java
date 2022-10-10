package rafradek.TF2weapons.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityProjectileEnergy extends EntityProjectileSimple {

	double struck;

	public EntityProjectileEnergy(World world) {
		super(world);
	}

	@Override
	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.setType(4);

	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
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
		this.hitEntities.clear();
		if (this.struck == 0) {
			this.struck = new Vec3d(this.motionX, this.motionY, this.motionZ).lengthVector();
		}

	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if (this.struck == 0)
			super.onHitGround(x, y, z, mop);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.hitEntities.size() > 0) {
			this.hitEntities.clear();
		} else if (this.struck != 0) {
			this.struck = 0;
		}
	}
}
