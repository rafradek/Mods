package rafradek.TF2weapons.building;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.ItemMonsterPlacerPlus;

public class ItemBuildingBox extends ItemMonsterPlacerPlus {
	public ItemBuildingBox() {
		this.setCreativeTab(TF2weapons.tabutilitytf2);
		this.setUnlocalizedName("buildingbox");
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par2List,
			boolean par4) {
		/*
		 * super.addInformation(par1ItemStack, par2EntityPlayer, par2List,
		 * par4); if(par1ItemStack.getTagCompound()!=null)
		 * par2List.add("tag: "+par1ItemStack.getTagCompound().toString());
		 */
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		for (int i = 18; i < 24; i++)
			par3List.add(new ItemStack(par1, 1, i));
	}

	@Override
	@SuppressWarnings("deprecation")
	public String getItemStackDisplayName(ItemStack p_77653_1_) {
		// (I18n.translateToLocal(this.getUnlocalizedName()+".name")).trim();
		int i = p_77653_1_.getItemDamage() / 2;
		String s1 = "sentry";
		switch (i) {
		case 10:
			s1 = "dispenser";
			break;
		case 11:
			s1 = "teleporter";
			break;
		}
		return I18n.translateToLocal(this.getUnlocalizedName() + "." + s1 + ".name");
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_) {
		return 16777215;
	}

}
