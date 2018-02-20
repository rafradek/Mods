package rafradek.TF2weapons;

import com.google.common.collect.Iterables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;

public class ItemFoodThrowable extends ItemFood {

	public ItemFoodThrowable(int amount, boolean isWolfFood) {
		super(amount, isWolfFood);
		// TODO Auto-generated constructor stub
	}

	public ItemFoodThrowable(int amount, float saturation, boolean isWolfFood) {
		super(amount, saturation, isWolfFood);
		// TODO Auto-generated constructor stub
	}

	public boolean onEntityItemUpdate(EntityItem entityItem) {
		EntityLivingBase living = Iterables.getFirst(entityItem.world.getEntitiesWithinAABB(EntityLivingBase.class, entityItem.getEntityBoundingBox(), (test) -> {
			return !(test instanceof EntityPlayer) && test.getHealth() < test.getMaxHealth() && test.isEntityAlive();
		}), null);
		
		if(living != null) {
			living.heal(living.getMaxHealth()*this.getHealAmount(entityItem.getItem())/28f);
			entityItem.getItem().shrink(1);
		}
        return false;
    }
}
