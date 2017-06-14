package rafradek.blocklauncher;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipesBlockLauncher implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		int launchers = 0;
		int slimeballs = 0;
		int feathers = 0;
		int ingots = 0;
		boolean activator = false;
		boolean shotblock = false;
		/*
		 * int bows=0; int flintandsteel=0; int powder=0; int redstone=0;
		 */
		for (int i = 0; i < inventorycrafting.getSizeInventory(); i++)
			if (inventorycrafting.getStackInSlot(i) != null && inventorycrafting.getStackInSlot(i).getCount() != 0)
				if (inventorycrafting.getStackInSlot(i).getItem() instanceof TNTCannon) {
					launchers++;
					activator = BlockLauncher.cannon.isActivator(inventorycrafting.getStackInSlot(i));
					shotblock = BlockLauncher.cannon.isSpreader(inventorycrafting.getStackInSlot(i));
				} else if (inventorycrafting.getStackInSlot(i).getItem() == Items.SLIME_BALL)
					slimeballs++;
				else if (inventorycrafting.getStackInSlot(i).getItem() == Items.FEATHER)
					feathers++;
				else if (inventorycrafting.getStackInSlot(i).getItem() == Items.IRON_INGOT)
					ingots++;
				else
					return false;
		if (launchers == 1 && (slimeballs == 0 || slimeballs == 4 || slimeballs == 8)
				&& (feathers == 0 || (feathers == 4 && activator)) && (ingots == 0 || ingots == 4 || (ingots == 8 && shotblock)))
			return true;
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		int launchers = 0;
		int launcherslot = -1;
		int slimeballs = 0;
		int feathers = 0;
		int ingots = 0;
		/*
		 * int bows=0; int flintandsteel=0; int powder=0; int redstone=0;
		 */
		for (int i = 0; i < inventorycrafting.getSizeInventory(); i++)
			if (inventorycrafting.getStackInSlot(i) != null && inventorycrafting.getStackInSlot(i).getCount() != 0)
				if (inventorycrafting.getStackInSlot(i).getItem() instanceof TNTCannon) {
					launchers++;
					launcherslot = i;
				} else if (inventorycrafting.getStackInSlot(i).getItem() == Items.SLIME_BALL)
					slimeballs++;
				else if (inventorycrafting.getStackInSlot(i).getItem() == Items.FEATHER)
					feathers++;
				else if (inventorycrafting.getStackInSlot(i).getItem() == Items.IRON_INGOT)
					ingots++;
				else
					return null;
		if (launchers == 1) {
			ItemStack stack = inventorycrafting.getStackInSlot(launcherslot).copy();
			if (stack.getTagCompound() == null)
				stack.setTagCompound(new NBTTagCompound());
			if (slimeballs == 4) {
				stack.getTagCompound().setBoolean("Sticky", true);
				stack.getTagCompound().removeTag("Bouncy");
			} else if (slimeballs == 8) {
				stack.getTagCompound().setBoolean("Bouncy", true);
				stack.getTagCompound().removeTag("Sticky");
			}
			if (feathers == 4)
				stack.getTagCompound().setBoolean("Harmless", true);
			if (ingots == 8)
				stack.getTagCompound().setBoolean("Stack", true);
			else if(ingots == 4)
				stack.getTagCompound().setBoolean("Crushing", true);
			/*
			 * if(bows==1){ stack.getTagCompound().setBoolean("BowLike",true); }
			 * if(flintandsteel==1){
			 * stack.getTagCompound().setBoolean("Activator",true); }
			 * if(powder==1){ stack.getTagCompound().setBoolean("Powder", true);
			 * } if(redstone==1){ stack.getTagCompound().setBoolean("Glowstone",
			 * true); }
			 */
			return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getRecipeSize() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		// TODO Auto-generated method stub
		return ItemStack.EMPTY;
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

}
