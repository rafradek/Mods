package rafradek.TF2weapons.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface IFireMethod {

	public void shoot(ItemStack stack, EntityLivingBase living, World world, int thisCritical, EnumHand hand);

	void use(ItemStack stack, EntityLivingBase living, World world, int thisCritical, EnumHand hand);
}
