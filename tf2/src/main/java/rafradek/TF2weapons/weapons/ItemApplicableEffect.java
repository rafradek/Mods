package rafradek.TF2weapons.weapons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rafradek.TF2weapons.ItemFromData;

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
