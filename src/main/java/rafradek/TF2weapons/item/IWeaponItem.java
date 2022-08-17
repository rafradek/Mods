package rafradek.TF2weapons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IWeaponItem {

	float getWeaponDamage(ItemStack stack, EntityLivingBase living, Entity target);
}
