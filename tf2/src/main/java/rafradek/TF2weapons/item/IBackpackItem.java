package rafradek.TF2weapons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IBackpackItem {

	default void checkItem(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		if (!par5) {
			if(((EntityPlayer)par3Entity).inventory.getStackInSlot(par4) == par1ItemStack) {
				((EntityPlayer)par3Entity).inventory.setInventorySlotContents(par4, ItemStack.EMPTY);
			}
			else if (((EntityPlayer)par3Entity).getHeldItemOffhand() == par1ItemStack){
				((EntityPlayer)par3Entity).inventory.offHandInventory.set(0, ItemStack.EMPTY);
			}
		}
	}
}
