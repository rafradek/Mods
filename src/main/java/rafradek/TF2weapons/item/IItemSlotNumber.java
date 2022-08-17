package rafradek.TF2weapons.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemSlotNumber {

	public boolean catchSlotHotkey(ItemStack stack, EntityPlayer player);
	public void onSlotSelection(ItemStack stack, EntityPlayer player, int slot);
}
