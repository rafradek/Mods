package rafradek.TF2weapons.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemUsable;

public class JumperRecipe implements IRecipe {

	String nameBefore;
	String nameAfter;
	
	public JumperRecipe(String nameBefore, String nameAfter) {
		this.nameAfter=nameAfter;
		this.nameBefore=nameBefore;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int feather = 0;
		ItemStack stack2 = ItemStack.EMPTY;

		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack stack = inv.getStackInSlot(x);
			if (!stack.isEmpty())
				if (stack.getItem() == Items.FEATHER) {
					if (feather < 8)
						feather++;
					else
						return false;
				} else if (stack2.isEmpty() && ItemFromData.isSameType(stack, nameBefore))
					stack2 = stack;
				else
					return false;
		}
		// System.out.println("matches "+(australium&&stack2!=null));
		return feather == 8 && !stack2.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		ItemStack stack2 = ItemStack.EMPTY;

		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack stack = inv.getStackInSlot(x);
			if (!stack.isEmpty())
				if (!(stack.getItem() == Items.FEATHER))
					stack2 = stack;
		}
		// System.out.println("OutPut: "+stack2);
		if (!stack2.isEmpty()) {
			if(ItemFromData.isSameType(stack2, nameBefore))
				stack2=ItemFromData.getNewStack(nameAfter);
		}
		return stack2;
	}

	@Override
	public int getRecipeSize() {
		// TODO Auto-generated method stub
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		// TODO Auto-generated method stu
		return ItemFromData.getNewStack(nameAfter);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		NonNullList<ItemStack> aitemstack = NonNullList.withSize(inv.getSizeInventory(),ItemStack.EMPTY);

		for (int i = 0; i < aitemstack.size(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			aitemstack.set(i,net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
		}

		return aitemstack;
	}

}
