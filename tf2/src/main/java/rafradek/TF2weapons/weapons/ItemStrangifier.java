package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.building.ItemPDA;

public class ItemStrangifier extends ItemApplicableEffect {

	public ItemStrangifier() {
		this.setMaxStackSize(1);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (stack.hasTagCompound() && MapList.nameToData.containsKey(stack.getTagCompound().getString("Weapon"))) {
			return MapList.nameToData.get(stack.getTagCompound().getString("Weapon")).getName()+" "+super.getItemStackDisplayName(stack);
		}
		return super.getItemStackDisplayName(stack);
	}
	
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		if (!stack.hasTagCompound())
			tooltip.add("Applicable to any weapon");
		tooltip.add("Combine with the matching weapon in a crafting table");
	}
	
	public boolean isApplicable(ItemStack stack, ItemStack weapon) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("Weapon") ? super.isApplicable(stack, weapon) : stack.getItem() instanceof ItemUsable || stack.getItem() instanceof ItemCloak
				|| stack.getItem() instanceof ItemPDA || stack.getItem() instanceof ItemBackpack;
	}
	
	public void apply(ItemStack stack, ItemStack weapon) {
		weapon.getTagCompound().setBoolean("Strange", true);
	}
}
