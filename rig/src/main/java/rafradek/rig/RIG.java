package rafradek.rig;

import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.TF2weapons.NullStorage;
import rafradek.TF2weapons.inventory.ContainerWearables;
import rafradek.TF2weapons.inventory.InventoryWearables;
import rafradek.blocklauncher.BlockEventBus.DestroyBlockEntry;

@Mod(modid = "rafradek_rig", name = "Resource Integration Gear", version = "0.1")
public class RIG {

	public static Item launchpart;
	public static Item launchpartBetter;

	@CapabilityInject(ItemStackHandler.class)
	public static final Capability<ItemStackHandler> RIG_ITEM = null;
	
	public static final String MOD_ID="rafradek_rig";
	@SidedProxy(modId = MOD_ID, clientSide = "rafradek.rig.RIGClientProxy", serverSide = "rafradek.rig.RIGCommonProxy")
	public static RIGCommonProxy proxy;
	public static CreativeTabs tabRig;
	public static Block bench;
	public static Item rig;
	public static Item node;

	@Instance
	public static RIG instance;
	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		tabRig = new CreativeTabs("rig") {
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(rig);
			}
		};
		CapabilityManager.INSTANCE.register(ItemStackHandler.class, new IStorage<ItemStackHandler>() {

			@Override
			public NBTBase writeNBT(Capability<ItemStackHandler> capability, ItemStackHandler instance, EnumFacing side) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void readNBT(Capability<ItemStackHandler> capability, ItemStackHandler instance, EnumFacing side, NBTBase nbt) {
				// TODO Auto-generated method stub
				
			}
			
		}, new Callable<ItemStackHandler>() {

			@Override
			public ItemStackHandler call() throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

		});
		registerBlock(bench = new BlockRIGEnchant().setUnlocalizedName("bench"), MOD_ID+":bench");
		ForgeRegistries.ITEMS.register(rig = new ItemRIG().setRegistryName(MOD_ID, "rig").setCreativeTab(tabRig));
		ForgeRegistries.ITEMS.register(node = new ItemNode().setRegistryName(MOD_ID, "node").setCreativeTab(tabRig));
		//ForgeRegistries.ENCHANTMENTS.register(enchPower = new EnchantmentPowerBL().setRegistryName(new ResourceLocation("rafradek_blocklauncher:power")));
		/*
		 * Enchantment.addToBookList(enchPower);
		 * Enchantment.addToBookList(enchEff);
		 * Enchantment.addToBookList(enchLoot);
		 * Enchantment.addToBookList(enchHeavy);
		 * Enchantment.addToBookList(enchFire);
		 * Enchantment.addToBookList(enchGravity);
		 * Enchantment.addToBookList(enchMultiple);
		 */

		MinecraftForge.EVENT_BUS.register(new RIGEvents());
		proxy.registerRender();
	}

	@Mod.EventHandler
	public void postInit(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {

			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				if (ID == 0){
					ContainerBench container = new ContainerBench(player, player.inventory, world, new BlockPos(x, y, z));
					return container;
					//ContainerWearables container=new ContainerWearables(player.inventory, player.getCapability(INVENTORY_CAP, null), false, player);
					//container.addListener(new TF2EventsCommon.TF2ContainerListener((EntityPlayerMP) player));
					//return container;
				}
				return null;
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				if (ID == 0) {
					return new GuiBench(player.inventory, world, new BlockPos(x, y, z));
				}
				return null;
			}
			
		});
	}
	
	public static ItemStack equip(EntityPlayer player, ItemStack stack) {
		ItemStack previous=player.getCapability(RIG.RIG_ITEM, null).getStackInSlot(0);
		System.out.println("item in: "+ previous);
		player.getCapability(RIG.RIG_ITEM, null).setStackInSlot(0, stack);
		if(!previous.isEmpty())
			player.getAttributeMap().removeAttributeModifiers(((ItemRIG)previous.getItem()).getAttributeModifiers(previous));
		player.getAttributeMap().applyAttributeModifiers(((ItemRIG)stack.getItem()).getAttributeModifiers(stack));
		return previous;
	}

	public static void registerBlock(Block block, String name) {
		ForgeRegistries.BLOCKS.register(block.setRegistryName(name));
		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		ForgeRegistries.ITEMS.register(item);
		proxy.registerItemBlock(item);
	}
}
