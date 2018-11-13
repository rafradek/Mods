package rafradek.TF2weapons.crafting;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.NBTLiterals;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemKillstreakKit;
import rafradek.TF2weapons.weapons.ItemWeapon;

public class ItemKillstreakFabricator extends ItemFabricator {

	public static NonNullList<TF2Ingredient> standardInput;
	public static NonNullList<TF2Ingredient> specializedInput;
	public static NonNullList<TF2Ingredient> proInput;
	
	public ItemKillstreakFabricator() {
		this.setCreativeTab(TF2weapons.tabsurvivaltf2);
		this.setMaxStackSize(1);
	}
	
	@Override
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		for (TF2Attribute attribute: TF2Attribute.attributes) {
			if (attribute != null && attribute.perKill != 0) {
				for(int level = 0; level < 3; level++) {
					par3List.add(new ItemStack(this, 1, attribute.id + (level<<9)));
				}
			}
		}
	}
	
	public int getLevel(ItemStack stack) {
		return (stack.getMetadata()>>9)+1;
	}
	
	@Override
	public NonNullList<TF2Ingredient> getInput(ItemStack stack, EntityPlayer player) {
		// TODO Auto-generated method stub
		switch (this.getLevel(stack)) {
		case 1: return standardInput;
		case 2: return specializedInput;
		case 3: return proInput;
		default: return null;
		}
	}

	@Override
	public NonNullList<ItemStack> getOutput(ItemStack stack, EntityPlayer player) {
		// TODO Auto-generated method stub
		NonNullList<ItemStack> list = NonNullList.create();
		list.add(new ItemStack(TF2weapons.itemKillstreak, 1, stack.getMetadata()));
		return list;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.killstreakfabricator." + ItemKillstreakKit.NAMES[MathHelper.clamp(this.getLevel(stack)-1,0,2)];
	}
	
	public TF2Attribute getAttribute(ItemStack stack) {
		return TF2Attribute.attributes[MathHelper.clamp(stack.getMetadata() & 511, 0, TF2Attribute.attributes.length)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		
		TF2Attribute attrib = this.getAttribute(stack);
		
		if (attrib != null) {
			tooltip.add("For each kill gain up to:");
			tooltip.add(attrib.getTranslatedString(attrib.defaultValue + attrib.perKill * ItemKillstreakKit.getBonusMult(this.getLevel(stack), attrib), true));
		}
		tooltip.add("");
		super.addInformation(stack, world, tooltip, advanced);
	}
	
	static {
		standardInput = NonNullList.create();
		standardInput.add(new IngredientItemStack(new ItemStack(TF2weapons.itemTF2, 12, 13)));
		specializedInput = NonNullList.create();
		specializedInput.add(new IngredientPredicate(stack -> {
			return (stack.getItem() instanceof ItemKillstreakKit && ((ItemKillstreakKit)stack.getItem()).getLevel(stack) == 0)
					|| (stack.getItem() instanceof ItemFromData && stack.getTagCompound().getByte(NBTLiterals.STREAK_LEVEL) > 0);
			}, 1, "Killstreak kit or weapon"));
		specializedInput.add(new IngredientItemStack(new ItemStack(TF2weapons.itemTF2, 12, 13)));
		specializedInput.add(new IngredientItemStack(new ItemStack(TF2weapons.itemTF2, 8, 14)));
		proInput = NonNullList.create();
		proInput.add(new IngredientPredicate(stack -> {
			return (stack.getItem() instanceof ItemKillstreakKit && ((ItemKillstreakKit)stack.getItem()).getLevel(stack) == 1) || 
					(stack.getItem() instanceof ItemFromData && stack.getTagCompound().getByte(NBTLiterals.STREAK_LEVEL) > 1);
			}, 1, "Specialized killstreak kit or weapon"));
		proInput.add(new IngredientItemStack(new ItemStack(TF2weapons.itemTF2, 12, 14)));
		proInput.add(new IngredientItemStack(new ItemStack(TF2weapons.itemTF2, 8, 15)));
	}
}
