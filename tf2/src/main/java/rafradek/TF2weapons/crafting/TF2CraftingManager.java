package rafradek.TF2weapons.crafting;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class TF2CraftingManager {
	public static final ShapelessOreRecipe[] AMMO_RECIPES = new ShapelessOreRecipe[14];
	public static final TF2CraftingManager INSTANCE = new TF2CraftingManager();
	private final List<IRecipe> recipes = Lists.<IRecipe>newArrayList();

	public TF2CraftingManager() {
		ItemStack bonk = ItemFromData.getNewStack("bonk");
		bonk.setCount( 2);
		ItemStack cola = ItemFromData.getNewStack("critcola");
		cola.setCount( 2);
		addRecipe(TF2CraftingManager.AMMO_RECIPES[1] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 9, 1),
				new Object[] { "ingotCopper", "ingotLead", "gunpowder" }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[2] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 25, 2),
				new Object[] { "ingotCopper", "ingotLead", "gunpowder" }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[3] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 25, 3),
				new Object[] { "ingotCopper", "ingotLead", "gunpowder" }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[4] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 10, 4),
				new Object[] { "ingotCopper", "ingotLead", "gunpowder" }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[5] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 40, 5),
				new Object[] { "ingotCopper", "ingotLead", "gunpowder" }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[6] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 4, 6),
				new Object[] { "ingotCopper", "ingotLead", "gunpowder" }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[7] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 12, 7),
				new Object[] { "ingotIron", "ingotIron", Blocks.TNT }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[8] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 12, 8),
				new Object[] { "ingotIron", "ingotIron", Blocks.TNT }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[11] = new ShapelessOreRecipe(
				null, new ItemStack(TF2weapons.itemAmmo, 12, 11), new Object[] { "ingotIron", "ingotIron", Blocks.TNT }));
		addRecipe(new ShapedOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 8, 13),
				new Object[] { " R ", "RIR", " R ", 'I', "ingotIron", 'R', "dustRedstone" }));
		addRecipe(new ShapedOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 8, 14),
				new Object[] { "SLS", 'S', "string", 'L',"leather" }));
		addShapelessRecipe(new ItemStack(TF2weapons.itemAmmoMedigun, 1),
				new Object[] { Items.SPECKLED_MELON, Items.GHAST_TEAR, new ItemStack(Items.DYE, 1, 15) });
		addRecipe(new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmoFire, 1),
				new Object[] { "ingotIron", Items.MAGMA_CREAM, "ingotIron" }));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[9] = new ShapelessOreRecipe(null, new ItemStack(TF2weapons.itemAmmo, 30, 9),
				new Object[] { "ingotIron", "paper" }));
		ItemStack cleaver = ItemFromData.getNewStack("cleaver");
		cleaver.setCount(1);
		addRecipe(new ShapedOreRecipe(null,cleaver, new Object[] { "I", "W", 'I', "ingotIron", 'W', "stickWood"}));
		addRecipe(new AustraliumRecipe());
		addRecipe(new JumperRecipe("rocketlauncher","rocketjumper"));
		addRecipe(new JumperRecipe("stickybomblauncher","stickyjumper"));
		addRecipe(new ShapedOreRecipe(null, ItemFromData.getNewStack("cloak"), new Object[] { "AAA", "LGL", "AAA", 'A',
				"ingotAustralium", 'G', "blockGlass", 'L', "leather" }));
		addRecipe(new ShapedOreRecipe(null, ItemFromData.getNewStack("deadringer"), new Object[] { " A ", "AGA", " A ", 'A',
				"ingotAustralium", 'G', "blockGlass" }));
		addRecipe(new ShapedOreRecipe(null, TF2weapons.itemDisguiseKit, new Object[] { "I I", "PAG", "I I", 'A',
				"ingotAustralium", 'I', "ingotIron", 'G', "blockGlass", 'P', "paper" }));
		addRecipe(new ShapedOreRecipe(null,ItemFromData.getNewStack("sapper"),
				new Object[] { " R ", "IRI", " R ", 'I', "ingotIron", 'R', "dustRedstone" }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemHorn),
				new Object[] { "CLC", "C C", " C ", 'C', "ingotCopper", 'L', "leather" }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemBuildingBox, 1, 18),
				new Object[] { "RDR", "GIG", "III", 'D', new ItemStack(Blocks.DISPENSER), 'I', "ingotIron", 'G',
						"gunpowder", 'R', "dustRedstone" }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemBuildingBox, 1, 20),
				new Object[] { "MDR", "SIm", "rIG", 'D', new ItemStack(Blocks.DISPENSER), 'I', "ingotIron", 'M',
						new ItemStack(TF2weapons.itemAmmoMedigun), 'G', new ItemStack(TF2weapons.itemAmmo, 1, 8), 'R',
						"dustRedstone", 'r', new ItemStack(TF2weapons.itemAmmo, 1, 7), 'S',
						new ItemStack(TF2weapons.itemAmmo, 1, 1), 'm', new ItemStack(TF2weapons.itemAmmo, 1, 2) }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemBuildingBox, 1, 22), new Object[] { "IAI", "RAR",
				"IAI", 'I', "ingotIron", 'A', new ItemStack(TF2weapons.itemTF2, 1, 6), 'R', "dustRedstone" }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.blockAmmoFurnace),
				new Object[] { "RIG", "SFr", "sIM", 'F', new ItemStack(Blocks.FURNACE), 'I', "ingotIron", 'M',
						new ItemStack(TF2weapons.itemAmmo, 1, 2), 'G', new ItemStack(TF2weapons.itemAmmo, 1, 8), 'R',
						new ItemStack(TF2weapons.itemAmmo, 1, 7), 'r', new ItemStack(TF2weapons.itemAmmo, 1, 6), 's',
						new ItemStack(TF2weapons.itemAmmo, 1, 1), 'S', new ItemStack(TF2weapons.itemAmmo, 1, 11) }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemAmmoBelt),
				new Object[] { " IL", "IL ", "L  ", 'I', "ingotIron", 'L', "leather" }));
		addRecipe(new ShapedOreRecipe(null,bonk,
				new Object[] { "SDS", "IWI", "SAS", 'I', "ingotIron", 'A', new ItemStack(TF2weapons.itemTF2, 1, 6), 'W',
						new ItemStack(Items.WATER_BUCKET), 'S', new ItemStack(Items.SUGAR), 'D', "dyeYellow" }));
		addRecipe(new ShapedOreRecipe(null,cola,
				new Object[] { "SDS", "IWI", "SAS", 'I', "ingotIron", 'A', new ItemStack(TF2weapons.itemTF2, 1, 6), 'W',
						new ItemStack(Items.WATER_BUCKET), 'S', new ItemStack(Items.SUGAR), 'D', "dyeMagenta" }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemSandvich),
				new Object[] { " B ", "LHL", " B ", 'B', new ItemStack(Items.BREAD), 'L',
						new ItemStack(Blocks.TALLGRASS, 1, 1), 'H', new ItemStack(Items.PORKCHOP) }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemChocolate, 2), new Object[] { "CCC", "CCC", "MII",
				'C', new ItemStack(Items.DYE, 1, 3), 'M', new ItemStack(Items.MILK_BUCKET), 'I', "paper" }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemScoutBoots), new Object[] { "FFF", "FBF", "FFF", 'F',
				new ItemStack(Items.FEATHER), 'B', new ItemStack(Items.LEATHER_BOOTS) }));
		addRecipe(new ShapedOreRecipe(null,new ItemStack(TF2weapons.itemMantreads),
				new Object[] { " B ", "III", 'I', "ingotIron", 'B', new ItemStack(Items.IRON_BOOTS) }));

		ItemStack jarate = ItemFromData.getNewStack("jarate");
		jarate.getTagCompound().setBoolean("IsEmpty", true);
		addRecipe(new ShapedOreRecipe(null,jarate, new Object[] { " G ", "G G", "GGG", 'G', "paneGlass" }));
		ItemStack madmilk = ItemFromData.getNewStack("madmilk");
		madmilk.getTagCompound().setBoolean("IsEmpty", true);
		addRecipe(new ShapedOreRecipe(null,madmilk, new Object[] { " G ", "G G", "GGG", 'G', "paneGlass" }));
		ItemStack banner=new ItemStack(Items.BANNER,1,EnumDyeColor.RED.getDyeDamage());
		banner.getOrCreateSubCompound("BlockEntityTag").setTag("Patterns", new NBTTagList());
		banner.setCount(2);
		addRecipe(new ShapedOreRecipe(null,ItemFromData.getNewStack("startwrench"), new Object[] { " II", " S ", "I  ", 'I', "ingotIron", 'S', new ItemStack(TF2weapons.itemTF2, 1, 3) }));
		addRecipe(new ShapedOreRecipe(null,banner,
				new Object[] { "WWW", "WWW", "AS ", 'W',new ItemStack(Blocks.WOOL), 'A',new ItemStack(TF2weapons.itemTF2, 1, 2),'S', Items.STICK }));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2, 1, 4), new ItemStack(TF2weapons.itemTF2, 1, 3),
				new ItemStack(TF2weapons.itemTF2, 1, 3), new ItemStack(TF2weapons.itemTF2, 1, 3));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2, 1, 5), new ItemStack(TF2weapons.itemTF2, 1, 4),
				new ItemStack(TF2weapons.itemTF2, 1, 4), new ItemStack(TF2weapons.itemTF2, 1, 4));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2, 3, 4), new ItemStack(TF2weapons.itemTF2, 1, 5));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2, 3, 3), new ItemStack(TF2weapons.itemTF2, 1, 4));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2, 1, 9), new ItemStack(TF2weapons.itemTF2, 1, 3),
				new ItemStack(TF2weapons.itemTF2, 1, 3));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2, 1, 10), new ItemStack(TF2weapons.itemTF2, 1, 5),
				new ItemStack(TF2weapons.itemTF2, 1, 5), new ItemStack(TF2weapons.itemTF2, 1, 5));
		
		addRecipe(new OpenCrateRecipe());
		addRecipe(new RecipeToScrap());
	}

	public ShapedRecipes addRecipe(ItemStack stack, Object... recipeComponents) {
		ShapedPrimer primer = CraftingHelper.parseShaped(recipeComponents);
		ShapedRecipes recipe;
        recipe=(ShapedRecipes) new ShapedRecipes("", primer.width, primer.height, primer.input, stack);
		this.recipes.add(recipe);
		return recipe;
	}

	/**
	 * Adds a shapeless crafting recipe to the the game.
	 */
	public void addShapelessRecipe(ItemStack stack, Object... recipeComponents) {
		NonNullList<Ingredient> list = NonNullList.create();

		for (Object object : recipeComponents)
			list.add(CraftingHelper.getIngredient(object));

		ShapelessRecipes recipe;
        recipe= (ShapelessRecipes) new ShapelessRecipes("", stack, list);
		this.recipes.add(recipe);
	}

	/**
	 * Adds an IRecipe to the list of crafting recipes.
	 */
	public void addRecipe(IRecipe recipe) {
		this.recipes.add(recipe);
	}

	/**
	 * Retrieves an ItemStack that has multiple recipes for it.
	 */
	@Nullable
	public ItemStack findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn) {
		for (IRecipe irecipe : this.recipes)
			if (irecipe.matches(craftMatrix, worldIn))
				return irecipe.getCraftingResult(craftMatrix);

		return ItemStack.EMPTY;
	}

	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World worldIn) {
		for (IRecipe irecipe : this.recipes)
			if (irecipe.matches(craftMatrix, worldIn))
				return irecipe.getRemainingItems(craftMatrix);

		NonNullList<ItemStack> aitemstack = NonNullList.withSize(craftMatrix.getSizeInventory(),ItemStack.EMPTY);

		for (int i = 0; i < aitemstack.size(); ++i)
			aitemstack.set(i,craftMatrix.getStackInSlot(i));

		return aitemstack;
	}

	public List<IRecipe> getRecipeList() {
		return this.recipes;
	}
}
