package rafradek.TF2weapons.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemApplicableEffect extends Item {

	public ItemApplicableEffect() {
		// TODO Auto-generated constructor stub
	}

	public boolean isApplicable(ItemStack stack, ItemStack weapon) {
		return stack.hasTagCompound() && ItemFromData.getData(weapon).getName().equals(stack.getTagCompound().getString("Weapon"));
	}
	
	public void apply(ItemStack stack, ItemStack weapon) {
		
	}
}
