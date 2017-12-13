package rafradek.TF2weapons.crafting;

import java.util.ArrayList;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class RecipeToScrap extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ArrayList<ItemStack> stacks = new ArrayList<>();

		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack stack = inv.getStackInSlot(x);
			if (!stack.isEmpty())
				if (stacks.size() < 2 && stack.getItem() instanceof ItemFromData && ItemFromData.getData(stack).getInt(PropertyType.COST) >= 6)
					stacks.add(stack);
				else
					return false;
		}
		// System.out.println("matches "+(australium&&stack2!=null));
		return stacks.size() == 2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		return new ItemStack(TF2weapons.itemTF2, 1, 3);
	}

	@Override
	public ItemStack getRecipeOutput() {
		// TODO Auto-generated method stu
		return new ItemStack(TF2weapons.itemTF2, 1, 3);
	}

	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return nonnulllist;
    }

	@Override
	public boolean canFit(int width, int height) {
		// TODO Auto-generated method stub
		return width * height >= 2;
	}

}
