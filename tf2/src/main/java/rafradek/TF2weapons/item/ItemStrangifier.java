package rafradek.TF2weapons.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.common.MapList;

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
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("Weapon") ? super.isApplicable(stack, weapon) : weapon.getItem() instanceof ItemUsable || weapon.getItem() instanceof ItemCloak
				|| weapon.getItem() instanceof ItemPDA || weapon.getItem() instanceof ItemBackpack;
	}
	
	public void apply(ItemStack stack, ItemStack weapon) {
		weapon.getTagCompound().setBoolean("Strange", true);
	}
}
