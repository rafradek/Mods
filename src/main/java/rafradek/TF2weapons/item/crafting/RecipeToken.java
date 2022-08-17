package rafradek.TF2weapons.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.item.ItemToken;

public class RecipeToken extends ShapelessOreRecipe {

	public RecipeToken(ResourceLocation group, ItemStack result, Object[] recipe) {
		super(group, result, recipe);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		ItemStack result = super.getCraftingResult(inv);
		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack stack = inv.getStackInSlot(x);
			if (stack.getItem() instanceof ItemToken) {
				result.setTagCompound(new NBTTagCompound());
				result.getTagCompound().setByte("Token", (byte) stack.getMetadata());
			}
		}
		return result;
	}
}
