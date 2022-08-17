package rafradek.TF2weapons.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2weapons;

public class ItemTF2 extends Item {

	public static final String[] NAMES = new String[] { "ingotCopper", "ingotLead", "ingotAustralium", "scrapMetal",
			"reclaimedMetal", "refinedMetal", "nuggetAustralium", "key", "crate", "randomWeapon", "randomHat", "logicBoard", "strangifier"};

	public ItemTF2() {
		this.setHasSubtypes(true);
		this.setCreativeTab(TF2weapons.tabsurvivaltf2);
		this.setUnlocalizedName("tf2item");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + NAMES[stack.getMetadata()%NAMES.length];
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return (stack.getMetadata() == 9 || stack.getMetadata() == 10) ? 1 : 64;
	}

	@Override
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		// System.out.println(this.getCreativeTab());
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		for (int i = 0; i < 8; i++)
			par3List.add(new ItemStack(this, 1, i));
		par3List.add(new ItemStack(this, 1, 11));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Token")) {
			tooltip.add(ItemToken.CLASS_NAMES[stack.getTagCompound().getByte("Token")]);
		}
	}
	
	
}
