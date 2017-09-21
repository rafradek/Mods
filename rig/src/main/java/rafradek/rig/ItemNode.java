package rafradek.rig;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemNode extends Item {

	public ItemNode() {
		this.setHasSubtypes(true);
		this.setCreativeTab(RIG.tabRig);
		this.setUnlocalizedName("node");
	}

	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + (stack.getItemDamage() == 1 ? ".gold" : ".iron");
	}
	
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(!this.isInCreativeTab(tab))
			return;
		items.add(new ItemStack(this));
		items.add(new ItemStack(this,1,1));
    }
}
