package rafradek.TF2weapons.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.entity.EntityDummy;

public class EntityCleaver extends EntityProjectileSimple {

	public EntityCleaver(World world) {
		super(world);
		this.setType(2);
		// TODO Auto-generated constructor stub
	}

	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.usedWeapon.setCount(1);
	}
	
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		super.onHitGround(x, y, z, mop);
		if(!this.world.isRemote && this.damage <= 0 && !this.infinite) {
			this.entityDropItem(this.usedWeapon, 0f);
		}
	}
	
	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		super.onHitMob(entityHit, mop);
		if(!this.world.isRemote && this.isDead && !this.infinite) {
			if(entityHit.isEntityAlive()) {
				NBTTagList list=entityHit.getEntityData().getTagList("Cleavers", 10);
				list.appendTag(this.usedWeapon.serializeNBT());
				if(!entityHit.getEntityData().hasKey("Cleavers"))
					entityHit.getEntityData().setTag("Cleavers", list);
			}
			else
				this.entityDropItem(this.usedWeapon, 0f);
		}
	}
	
	@Override
	public double getGravity() {
		return 0.05f;
	}
}
