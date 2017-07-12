package rafradek.blocklauncher;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rafradek.blocklauncher.BlockEventBus.DestroyBlockEntry;

@Mod(modid = "rafradek_blocklauncher", name = "Block Launcher", version = "2.1.1")
public class BlockLauncher {

	public static TNTCannon cannon;
	public static Item launchpart;
	public static Item launchpartBetter;

	public static final String MOD_ID="rafradek_blocklauncher";
	@SidedProxy(modId = "rafradek_blocklauncher", clientSide = "rafradek.blocklauncher.BLClientProxy", serverSide = "rafradek.blocklauncher.BLCommonProxy")
	public static BLCommonProxy proxy;
	public static CreativeTabs tabblocklauncher;
	public static Enchantment enchPower;
	public static Enchantment enchEff;
	public static Enchantment enchLoot;
	public static Enchantment enchShrink;
	public static Enchantment enchHeavy;
	public static Block fireench;
	public static Enchantment enchFire;
	public static EnumEnchantmentType enchType = EnumHelper.addEnchantmentType("BLOCK_LAUNCHER", null);
	public static Enchantment enchGravity;
	public static Enchantment enchMultiple;

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		tabblocklauncher = new CreativeTabs("blocklauncher") {
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(cannon);
			}
		}.setRelevantEnchantmentTypes(enchType);
		ForgeRegistries.ENCHANTMENTS.register(enchPower = new EnchantmentPowerBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:power")));
		ForgeRegistries.ENCHANTMENTS.register(enchEff = new EnchantmentEfficiencyBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:efficiency")));
		ForgeRegistries.ENCHANTMENTS.register(enchLoot = new EnchantmentDropBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:drop")));
		ForgeRegistries.ENCHANTMENTS.register(enchShrink = new EnchantmentShrinkBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:shrink")));
		ForgeRegistries.ENCHANTMENTS.register(enchHeavy = new EnchantmentHeavyBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:heavy")));
		ForgeRegistries.ENCHANTMENTS.register(enchFire = new EnchantmentFireBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:fire")));
		ForgeRegistries.ENCHANTMENTS.register(enchGravity = new EnchantmentGravityBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:gravity")));
		ForgeRegistries.ENCHANTMENTS.register(enchMultiple = new EnchantmentMultipleBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:multiple")));
		/*
		 * Enchantment.addToBookList(enchPower);
		 * Enchantment.addToBookList(enchEff);
		 * Enchantment.addToBookList(enchLoot);
		 * Enchantment.addToBookList(enchHeavy);
		 * Enchantment.addToBookList(enchFire);
		 * Enchantment.addToBookList(enchGravity);
		 * Enchantment.addToBookList(enchMultiple);
		 */

		MinecraftForge.EVENT_BUS.register(new BlockEventBus());
		// FMLCommonHandler.instance().bus().register(new BlockEventBus());
		EntityRegistry.registerModEntity(new ResourceLocation("rafradek_blocklauncher","blockenchanted"),EntityFallingEnchantedBlock.class, "blockenchanted", 0, this, 160,
				Integer.MAX_VALUE, true);
		// EntityRegistry.registerModEntity(EntityTNTPrimedBetter.class,
		// "rafradek_tnt_primed", 1, this, 160, 10, true);
		// GameRegistry.registerBlock(fireench=new
		// BlockFireEnchanted().setHardness(0.0F).setLightLevel(1.0F).setStepSound(Block.soundTypeWood).setUnlocalizedName("fire"),
		// "rafradek_fire");
		ForgeRegistries.ITEMS.register(cannon = (TNTCannon) new TNTCannon().setRegistryName(new ResourceLocation("rafradek_blocklauncher:cannon")));
		ForgeRegistries.ITEMS.register(
				launchpart = new Item().setCreativeTab(tabblocklauncher).setUnlocalizedName("launchpart").setRegistryName(new ResourceLocation("rafradek_blocklauncher:launchpart")));
		ForgeRegistries.ITEMS.register(
				launchpartBetter = new Item().setCreativeTab(tabblocklauncher).setUnlocalizedName("launchpart_better")
				.setRegistryName(new ResourceLocation("rafradek_blocklauncher:launchpart_better")));
		ForgeRegistries.RECIPES.register(new RecipesBlockLauncher().setRegistryName("rafradek_blocklauncher:tntcannons"));
		ItemStack stack1 = new ItemStack(cannon);
		stack1.setTagCompound(new NBTTagCompound());
		stack1.getTagCompound().setInteger("Type", 0);
		ItemStack stack2 = stack1.copy();
		stack2.getTagCompound().setInteger("Type", 1);
		ItemStack stack3 = stack1.copy();
		stack3.getTagCompound().setInteger("Type", 2);
		ItemStack stack6 = stack1.copy();
		stack6.getTagCompound().setInteger("Type", 3);
		ItemStack stack7 = stack1.copy();
		stack7.getTagCompound().setInteger("Type", 4);
		ItemStack stack8 = stack1.copy();
		stack8.getTagCompound().setInteger("Type", 5);
		ItemStack stack4 = stack1.copy();
		stack4.getTagCompound().setInteger("Type", 16);
		ItemStack stack5 = stack1.copy();
		stack5.getTagCompound().setInteger("Type", 17);
		ItemStack stack9 = stack1.copy();
		stack9.getTagCompound().setInteger("Type", 18);

		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"br"), null, stack1, "ABC", "  D", Character.valueOf('A'), Items.IRON_INGOT, Character.valueOf('B'),
				launchpart, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Blocks.PLANKS);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"bc"), null, stack2, "ABC", " D ", Character.valueOf('A'), Blocks.OBSIDIAN, Character.valueOf('B'),
				launchpart, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"sb"), null, stack3, "ABC", "D E", Character.valueOf('A'), Items.IRON_INGOT, Character.valueOf('B'),
				launchpart, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Items.STICK,
				Character.valueOf('E'), Items.LEATHER);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"bf"), null, stack4, "ABD", " CE", Character.valueOf('A'), Items.IRON_INGOT, Character.valueOf('B'),
				launchpart, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Items.FLINT_AND_STEEL,
				Character.valueOf('E'), Blocks.PLANKS);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"bl"), null, stack5, "ABD", "ECF", Character.valueOf('A'), Items.IRON_INGOT, Character.valueOf('B'),
				launchpart, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Items.FLINT_AND_STEEL,
				Character.valueOf('E'), Blocks.PLANKS, Character.valueOf('F'), Blocks.GLASS);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"bt"), null, stack6, "DAB", " CA", Character.valueOf('A'), Items.IRON_INGOT, Character.valueOf('B'),
				launchpart, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Items.FLINT_AND_STEEL);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"cb"), null, stack7, "ABC", Character.valueOf('A'), Blocks.IRON_BLOCK, Character.valueOf('B'),
				launchpartBetter, Character.valueOf('C'), Items.REDSTONE);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"sr"), null, stack8, "ABC", "  D", Character.valueOf('A'), Items.DIAMOND, Character.valueOf('B'),
				launchpartBetter, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Blocks.PLANKS);
		GameRegistry.addShapedRecipe(new ResourceLocation(MOD_ID,"ml"), null, stack9, "ABD", " CE", Character.valueOf('A'), Blocks.IRON_BLOCK, Character.valueOf('B'),
				launchpartBetter, Character.valueOf('C'), Items.REDSTONE, Character.valueOf('D'), Items.FLINT_AND_STEEL,
				Character.valueOf('E'), Blocks.PLANKS);
		/*GameRegistry.addRecipe(new ItemStack(launchpart), "BCB", " A ", Character.valueOf('A'), Blocks.IRON_BLOCK,
				Character.valueOf('B'), Blocks.PISTON, Character.valueOf('C'), Items.REDSTONE);
		GameRegistry.addRecipe(new ItemStack(launchpartBetter), "BCB", " A ", Character.valueOf('A'),
				Blocks.DIAMOND_BLOCK, Character.valueOf('B'), Blocks.PISTON, Character.valueOf('C'),
				Blocks.REDSTONE_BLOCK);*/
		proxy.registerRender();
	}

	@Mod.EventHandler
	public void postInit(FMLInitializationEvent event) {

	}

	public static float getHardness(IBlockState block, World world) {
		if (block.getBlock() == Blocks.COBBLESTONE){
			
			return 2.1f;
		}
		if (block.getBlockHardness(world, new BlockPos(0, 0, 0)) == 0)
			return 0;
		return Math.min((block.getMaterial().isToolNotRequired() ? block.getBlockHardness(world, new BlockPos(0, 0, 0))
				: block.getBlockHardness(world, new BlockPos(0, 0, 0)) * 1.9f) / 1.55f + 0.65f, 50f);
	}
	public static float damageBlock(BlockPos pos, EntityLivingBase living, World world, float damage) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block.isAir(state, world, pos) || state.getMaterial() == Material.WATER || state.getMaterial() == Material.LAVA || state.getBlockHardness(world, pos) < 0)
			return damage;

		DestroyBlockEntry finalEntry = null;
		int entryId = 0;
		int emptyId = -1;
		for (int i = 0; i < BlockEventBus.destroyProgress.size(); i++) {
			DestroyBlockEntry entry = BlockEventBus.destroyProgress.get(i);
			if (emptyId == -1 && entry == null)
				emptyId = i;
			if (entry != null && entry.world == world && entry.pos.equals(pos)) {
				finalEntry = entry;
				entryId = i;
				break;
			}
		}
		if (finalEntry == null) {
			finalEntry = new DestroyBlockEntry(pos, world);
			if (emptyId != -1) {
				 BlockEventBus.destroyProgress.set(emptyId, finalEntry);
				entryId = emptyId;
			} else {
				 BlockEventBus.destroyProgress.add(finalEntry);
				entryId =  BlockEventBus.destroyProgress.size() - 1;
			}

		}

		/*if (block instanceof BlockChest) {
			((TileEntityChest) world.getTileEntity(pos)).setLootTable(LootTableList.CHESTS_NETHER_BRIDGE, living.getRNG().nextLong());
		}*/
		float hardness = BlockLauncher.getHardness(state, world);

		finalEntry.curDamage += damage;

		if (living != null)
			world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + entryId), pos, (int) ((finalEntry.curDamage / hardness) * 10));

		if (finalEntry.curDamage >= hardness) {
			if (living != null && living instanceof EntityPlayer)
				block.harvestBlock(world, (EntityPlayer) living, pos, state, null, ItemStack.EMPTY);
			else {
				block.dropBlockAsItem(world, pos, state, 0);
			}
			BlockEventBus.destroyProgress.remove(finalEntry);

			boolean flag = (living == null || !(living instanceof EntityPlayer) && world.isAirBlock(pos)) || block.removedByPlayer(state, world, pos, (EntityPlayer) living, true);

			if (flag) {
				if (living != null) {
					world.playEvent(2001, pos, Block.getStateId(state));
					world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + entryId), pos, -1);
				}
				block.onBlockDestroyedByPlayer(world, pos, state);

			}
			return finalEntry.curDamage - hardness;
		}
		return 0;
	}
	public static Vec3d normalize(Vec3d vec) {
		double maxValue = Math.max(vec.x, Math.max(vec.y, vec.z));
		return new Vec3d(vec.x / maxValue, vec.y / maxValue, vec.z / maxValue);
	}
}
