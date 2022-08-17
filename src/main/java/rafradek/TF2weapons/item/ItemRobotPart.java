package rafradek.TF2weapons.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import rafradek.TF2weapons.TF2weapons;

public class ItemRobotPart extends Item {

	public static final int[] LEVEL = {0,0,0,1,1,2,2};
	public static final int[] LEVEL1 = {0,1,2};
	public static final int[] LEVEL2 = {3,4};
	public static final int[] LEVEL3 = {5,6};
	public ItemRobotPart() {
		this.setHasSubtypes(true);
		this.setCreativeTab(TF2weapons.tabsurvivaltf2);
		this.setUnlocalizedName("robotpart");
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.robotpart."+stack.getMetadata();
	}

	public static int getLevel(ItemStack stack) {
		return LEVEL[stack.getMetadata()%LEVEL.length];
	}

	public static int getVariant(ItemStack stack) {
		return stack.getMetadata()%3;
	}

	@Override
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		// System.out.println(this.getCreativeTab());
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		for (int i = 0; i < LEVEL.length; i++)
			par3List.add(new ItemStack(this, 1, i));
	}
	static {
	}
}
