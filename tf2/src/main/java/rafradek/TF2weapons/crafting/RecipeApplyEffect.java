package rafradek.TF2weapons.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.ItemPDA;
import rafradek.TF2weapons.weapons.ItemApplicableEffect;
import rafradek.TF2weapons.weapons.ItemBackpack;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemKillstreakKit;
import rafradek.TF2weapons.weapons.ItemStrangifier;
import rafradek.TF2weapons.weapons.ItemUsable;

public class RecipeApplyEffect implements IRecipe, IRecipeTF2{

	public static final ItemStack ITEM = ItemFromData.getNewStack("minigun");

	public Item type;
	public RecipeApplyEffect(Item type) {
		this.type = type;
		
	}
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack effect = ItemStack.EMPTY;
		ItemStack stack2 = ItemStack.EMPTY;

		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack stack = inv.getStackInSlot(x);
			if (!stack.isEmpty())
				if (effect.isEmpty() && stack.getItem() == type) {
					effect = stack;
				} else if (stack2.isEmpty() && stack.getItem() instanceof ItemUsable)
					stack2 = stack;
				else
					return false;
		}
		// System.out.println("matches "+(australium&&stack2!=null));
		return !effect.isEmpty() && !stack2.isEmpty() && ((ItemApplicableEffect) effect.getItem()).isApplicable(effect, stack2);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		ItemStack effect = ItemStack.EMPTY;
		ItemStack stack2 = ItemStack.EMPTY;

		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack stack = inv.getStackInSlot(x);
			if (!stack.isEmpty())
				if (effect.isEmpty() && stack.getItem() == type) {
					effect = stack;
				} else if (stack2.isEmpty() && stack.getItem() instanceof ItemUsable)
					stack2 = stack;
		}
		// System.out.println("OutPut: "+stack2);
		if (!stack2.isEmpty()) {
			stack2 = stack2.copy();
			if (!stack2.hasTagCompound())
				stack2.setTagCompound(new NBTTagCompound());
			stack2.setCount( 1);
			((ItemApplicableEffect)effect.getItem()).apply(effect, stack2);
				
			//stack2.getTagCompound().setBoolean("Australium", true);
			//
		}
		return stack2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		ItemStack stack = ITEM.copy();
		((ItemApplicableEffect) type).apply(new ItemStack(type), stack);
		return stack;
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

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceLocation getRegistryName() {
		// TODO Auto-generated method stub
		return new ResourceLocation(TF2weapons.MOD_ID, this.type.toString());
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		// TODO Auto-generated method stub
		return IRecipe.class;
	}

	@Override
	public boolean canFit(int width, int height) {
		// TODO Auto-generated method stub
		return width * height >= 2;
	}
	@Override
	public ItemStack getSuggestion(int slot) {
		return slot == 0 ? ITEM : slot == 1 ? new ItemStack(this.type) : ItemStack.EMPTY;
	}

}
