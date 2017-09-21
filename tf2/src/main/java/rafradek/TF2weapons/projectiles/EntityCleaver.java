package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCleaver extends EntityProjectileSimple {

	public EntityCleaver(World world) {
		super(world);
		this.setType(2);
		// TODO Auto-generated constructor stub
	}

	public EntityCleaver(World world, EntityLivingBase living, EnumHand hand) {
		super(world, living, hand);
		this.usedWeapon.setCount(1);
		// TODO Auto-generated constructor stub
	}

	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		super.onHitGround(x, y, z, mop);
		if(!this.world.isRemote && this.damage <= 0) {
			this.entityDropItem(this.usedWeapon, 0f);
		}
	}
	
	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		super.onHitMob(entityHit, mop);
		if(!this.world.isRemote && this.isDead) {
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
