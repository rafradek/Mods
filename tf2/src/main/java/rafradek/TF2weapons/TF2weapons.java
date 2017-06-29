package rafradek.TF2weapons;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.TF2EventsCommon.DestroyBlockEntry;
import rafradek.TF2weapons.TF2EventsCommon.TF2ContainerListener;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.boss.BlockProp;
import rafradek.TF2weapons.boss.EntityHHH;
import rafradek.TF2weapons.boss.EntityMerasmus;
import rafradek.TF2weapons.boss.EntityMonoculus;
import rafradek.TF2weapons.boss.EntityTF2Boss;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.building.EntityTeleporter.TeleporterData;
import rafradek.TF2weapons.building.ItemBuildingBox;
import rafradek.TF2weapons.characters.EntityDemoman;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.characters.EntityHeavy;
import rafradek.TF2weapons.characters.EntityMedic;
import rafradek.TF2weapons.characters.EntityPyro;
import rafradek.TF2weapons.characters.EntitySaxtonHale;
import rafradek.TF2weapons.characters.EntityScout;
import rafradek.TF2weapons.characters.EntitySniper;
import rafradek.TF2weapons.characters.EntitySoldier;
import rafradek.TF2weapons.characters.EntitySpy;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.IEntityTF2;
import rafradek.TF2weapons.characters.ItemMonsterPlacerPlus;
import rafradek.TF2weapons.crafting.BlockAmmoFurnace;
import rafradek.TF2weapons.crafting.BlockCabinet;
import rafradek.TF2weapons.crafting.ContainerAmmoFurnace;
import rafradek.TF2weapons.crafting.ContainerTF2Workbench;
import rafradek.TF2weapons.crafting.GuiAmmoFurnace;
import rafradek.TF2weapons.crafting.GuiTF2Crafting;
import rafradek.TF2weapons.crafting.ItemTF2;
import rafradek.TF2weapons.crafting.OpenCrateRecipe;
import rafradek.TF2weapons.crafting.TileEntityAmmoFurnace;
import rafradek.TF2weapons.decoration.ContainerWearables;
import rafradek.TF2weapons.decoration.GuiWearables;
import rafradek.TF2weapons.decoration.InventoryWearables;
import rafradek.TF2weapons.loot.EntityBuildingFunction;
import rafradek.TF2weapons.loot.EntityOfClassFunction;
import rafradek.TF2weapons.loot.KilledByTeam;
import rafradek.TF2weapons.loot.RandomWeaponFunction;
import rafradek.TF2weapons.message.TF2ActionHandler;
import rafradek.TF2weapons.message.TF2BulletHandler;
import rafradek.TF2weapons.message.TF2CapabilityHandler;
import rafradek.TF2weapons.message.TF2ContractHandler;
import rafradek.TF2weapons.message.TF2DisguiseHandler;
import rafradek.TF2weapons.message.TF2GuiConfigHandler;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2ProjectileHandler;
import rafradek.TF2weapons.message.TF2PropertyHandler;
import rafradek.TF2weapons.message.TF2ShowGuiHandler;
import rafradek.TF2weapons.message.TF2UseHandler;
import rafradek.TF2weapons.message.TF2VelocityAddHandler;
import rafradek.TF2weapons.message.TF2WeaponDataHandler;
import rafradek.TF2weapons.message.TF2WeaponDropHandler;
import rafradek.TF2weapons.message.TF2WearableChangeHandler;
import rafradek.TF2weapons.projectiles.EntityBall;
import rafradek.TF2weapons.projectiles.EntityFlame;
import rafradek.TF2weapons.projectiles.EntityFlare;
import rafradek.TF2weapons.projectiles.EntityGrenade;
import rafradek.TF2weapons.projectiles.EntityJar;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.projectiles.EntityProjectileSimple;
import rafradek.TF2weapons.projectiles.EntityRocket;
import rafradek.TF2weapons.projectiles.EntityStickybomb;
import rafradek.TF2weapons.projectiles.EntityStickProjectile;
import rafradek.TF2weapons.upgrade.BlockUpgradeStation;
import rafradek.TF2weapons.upgrade.ContainerRecover;
import rafradek.TF2weapons.upgrade.ContainerUpgrades;
import rafradek.TF2weapons.upgrade.GuiRecover;
import rafradek.TF2weapons.upgrade.GuiUpgradeStation;
import rafradek.TF2weapons.upgrade.MannCoBuilding;
import rafradek.TF2weapons.upgrade.TileEntityUpgrades;
import rafradek.TF2weapons.weapons.InventoryAmmoBelt;
import rafradek.TF2weapons.weapons.ItemAmmo;
import rafradek.TF2weapons.weapons.ItemAmmoBelt;
import rafradek.TF2weapons.weapons.ItemAmmoPackage;
import rafradek.TF2weapons.weapons.ItemDisguiseKit;
import rafradek.TF2weapons.weapons.ItemFireAmmo;
import rafradek.TF2weapons.weapons.ItemHorn;
import rafradek.TF2weapons.weapons.ItemMeleeWeapon;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.ItemSniperRifle;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWrench;
import rafradek.TF2weapons.weapons.TF2Explosion;
import rafradek.TF2weapons.weapons.WeaponsCapability;
import scala.actors.threadpool.Arrays;

@Mod(modid = "rafradek_tf2_weapons", name = "TF2 Stuff", version = "1.1.6", guiFactory = "rafradek.TF2weapons.TF2GuiFactory", dependencies = "after:dynamiclights", updateJSON="https://rafradek.github.io/tf2stuffmod.json")
public class TF2weapons {

	public static final String MOD_ID = "rafradek_tf2_weapons";
	@Metadata(MOD_ID)
	public static ModMetadata metadata;

	@CapabilityInject(WeaponsCapability.class)
	public static final Capability<WeaponsCapability> WEAPONS_CAP = null;

	@CapabilityInject(InventoryWearables.class)
	public static final Capability<InventoryWearables> INVENTORY_CAP = null;

	@CapabilityInject(InventoryAmmoBelt.class)
	public static final Capability<InventoryAmmoBelt> INVENTORY_BELT_CAP = null;
	
	@CapabilityInject(TF2EventsCommon.TF2WorldStorage.class)
	public static final Capability<TF2EventsCommon.TF2WorldStorage> WORLD_CAP = null;

	@CapabilityInject(WeaponData.WeaponDataCapability.class)
	public static final Capability<WeaponData.WeaponDataCapability> WEAPONS_DATA_CAP = null;
	
	@CapabilityInject(TF2PlayerCapability.class)
	public static final Capability<TF2PlayerCapability> PLAYER_CAP = null;

	public int[] itemid = new int[9];
	public static Configuration conf;

	public static CreativeTabs tabutilitytf2;
	public static CreativeTabs tabweapontf2;
	public static CreativeTabs tabsurvivaltf2;
	// public static final ArmorMaterial OPARMOR =
	// EnumHelper.addArmorMaterial("OPARMOR", "", 1000, new int[] {24,0,0,0},
	// 100);
	public static SimpleNetworkWrapper network;
	public static Item itemPlacer;
	public static Item mobHeldItem;

	private static int weaponVersion;

	public static int destTerrain;
	public static boolean medigunLock;
	public static boolean fastMetalProduction;
	public static boolean dispenserHeal;
	public static boolean shootAttract;
	public static boolean disableSpawn;
	public static boolean disableEvent;
	public static int bossReappear;
	public static boolean disableContracts;
	public static boolean disableGeneration;
	public static boolean randomCrits;
	public static String spawnOres;
	public static String naturalCheck;
	public static float damageMultiplier;
	public static boolean dynamicLights;
	public static boolean dynamicLightsProj;
	
	@Instance(value = MOD_ID)
	public static TF2weapons instance;

	public File weaponDir;

	public static Block blockCabinet;
	public static Block blockCopperOre;
	public static Block blockProp;
	public static Block blockLeadOre;
	public static Block blockAustraliumOre;
	public static Block blockAustralium;
	public static Block blockUpgradeStation;
	public static Block blockAmmoFurnace;

	public static boolean generateCopper;
	public static boolean generateLead;
	public static boolean generateAustralium;

	public static Potion bonk;
	public static Potion stun;
	public static Potion crit;
	public static Potion buffbanner;
	public static Potion backup;
	public static Potion conch;
	public static Potion markDeath;
	public static Potion jarate;
	public static Potion madmilk;
	public static Potion critBoost;
	public static Potion charging;
	public static Potion uber;
	public static Potion it;
	public static Potion bombmrs;

	public static Item itemDisguiseKit;
	public static Item itemBuildingBox;
	public static Item itemSandvich;
	public static Item itemChocolate;
	public static Item itemAmmo;
	public static Item itemAmmoFire;
	public static Item itemAmmoPackage;
	public static Item itemAmmoMedigun;
	public static Item itemAmmoBelt;
	public static Item itemScoutBoots;
	public static Item itemMantreads;
	public static Item itemTF2;
	public static Item itemHorn;

	public static ResourceLocation lootTF2Character;
	public static ResourceLocation lootScout;
	public static ResourceLocation lootSpy;
	public static ResourceLocation lootHeavy;
	public static ResourceLocation lootEngineer;
	public static ResourceLocation lootMedic;
	public static ResourceLocation lootPyro;
	public static ResourceLocation lootSoldier;
	public static ResourceLocation lootDemoman;
	public static ResourceLocation lootSniper;
	public static ResourceLocation lootHale;

	public static byte[] itemDataCompressed;
	public static GZIPOutputStream out;
	
	public static StatBase cratesOpened;
	public static MinecraftServer server;
	public static EntityLivingBase dummyEnt = new EntityCreeper(null);
	
	public static BannerPattern redPattern;
	public static BannerPattern bluPattern;
	public static BannerPattern fastSpawn;
	
	public static int getCurrentWeaponVersion() {
		return 19;
	}

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {

		this.weaponDir = new File(event.getModConfigurationDirectory(), "TF2WeaponsLists");
		if (!this.weaponDir.exists())
			this.weaponDir.mkdirs();
		metadata.autogenerated = false;

		conf = new Configuration(event.getSuggestedConfigurationFile());
		boolean shouldCopy = false;
		if (!conf.hasKey("internal", "Weapon Config Version"))
			shouldCopy = true;
		createConfig();
		File outputFile = new File(this.weaponDir, "Weapons.json");
		File outputFile2 = new File(this.weaponDir, "Cosmetics.json");
		File outputFile3 = new File(this.weaponDir, "Crates.json");
		File file = event.getSourceFile();
		// System.out.println("LOLOLOLOLOLOL "+file.getAbsolutePath());
		// System.out.println("LOLOLOLOLOLOL2
		// "+event.getModConfigurationDirectory());
		// System.out.println("Istnieje? "+outputFile.exists());
		if (weaponVersion < getCurrentWeaponVersion())
			shouldCopy = true;
		if (!outputFile.exists() || shouldCopy) {
			conf.get("internal", "Weapon Config Version", getCurrentWeaponVersion()).set(getCurrentWeaponVersion());
			conf.save();

			if (file.isFile())
				try {
					ZipFile zip = new ZipFile(file);
					ZipEntry entry = zip.getEntry("Weapons.json");
					ZipEntry entryHats = zip.getEntry("Cosmetics.json");
					ZipEntry entryCrates = zip.getEntry("Crates.json");
					if (entry != null) {

						InputStream zin = zip.getInputStream(entry);
						byte[] bytes = new byte[(int) entry.getSize()];
						zin.read(bytes);
						FileOutputStream str = new FileOutputStream(outputFile);
						str.write(bytes);
						str.close();
						zin.close();

					}
					if (entryHats != null) {

						InputStream zin = zip.getInputStream(entryHats);
						byte[] bytes = new byte[(int) entryHats.getSize()];
						zin.read(bytes);
						FileOutputStream str = new FileOutputStream(outputFile2);
						str.write(bytes);
						str.close();
						zin.close();

					}
					if (entryCrates != null) {

						InputStream zin = zip.getInputStream(entryCrates);
						byte[] bytes = new byte[(int) entryCrates.getSize()];
						zin.read(bytes);
						FileOutputStream str = new FileOutputStream(outputFile3);
						str.write(bytes);
						str.close();
						zin.close();

					}
					zip.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				try {
					File inputFile = new File(file, "Weapons.json");
					File inputFileHats = new File(file, "Cosmetics.json");
					File inputFileCrates = new File(file, "Crates.json");
					FileInputStream istr = new FileInputStream(inputFile);

					byte[] bytes = new byte[(int) inputFile.length()];
					istr.read(bytes);
					FileOutputStream str = new FileOutputStream(outputFile);
					str.write(bytes);
					str.close();
					istr.close();

					istr = new FileInputStream(inputFileHats);

					bytes = new byte[(int) inputFileHats.length()];
					istr.read(bytes);
					str = new FileOutputStream(outputFile2);
					str.write(bytes);
					str.close();
					istr.close();

					istr = new FileInputStream(inputFileCrates);

					bytes = new byte[(int) inputFileCrates.length()];
					istr.read(bytes);
					str = new FileOutputStream(outputFile3);
					str.write(bytes);
					str.close();
					istr.close();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		bluPattern=EnumHelper.addEnum(BannerPattern.class, "BLU_PATTERN", new Class<?>[]{String.class,String.class}, "blu_base","bb");
		redPattern=EnumHelper.addEnum(BannerPattern.class, "RED_PATTERN", new Class<?>[]{String.class,String.class}, "red_base","rb");
		fastSpawn=EnumHelper.addEnum(BannerPattern.class, "FAST_SPAWN", new Class<?>[]{String.class,String.class}, "fast_spawn","fs");
		
		MapList.initMaps();
		TF2Attribute.initAttributes();
		
		/*File refFile=new File(event.getModConfigurationDirectory(), "TF2References");
		if(!refFile.exists())
			refFile.mkdirs();
		try {
			BufferedWriter attributes=new BufferedWriter(new FileWriter(new File(refFile,"attributes.txt")));
			attributes.write("ID - Name - Effect - State - Type - Default\n");
			for(int i=0;i<TF2Attribute.attributes.length;i++){
				TF2Attribute attr=TF2Attribute.attributes[i];
				if(attr != null){
					attributes.write(attr.id+" - "+attr.name+" - "+attr.effect+" - "+attr.state.toString()+" - "+attr.typeOfValue.toString()+" - "+attr.defaultValue+"\n");
				}
			}
			attributes.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		tabweapontf2 = new CreativeTabs("tf2weapons") {
			@Override
			public ItemStack getTabIconItem() {
				return ItemFromData.getNewStack("minigun");
			}
			
		};
		tabutilitytf2 = new CreativeTabs("tf2util") {
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(itemDisguiseKit);
			}
			
			@SideOnly(Side.CLIENT)
		    public void displayAllRelevantItems(NonNullList<ItemStack> list) {
				super.displayAllRelevantItems(list);
				for(int i=0;i<2;i++){
					for(int j=0;j<2;j++){
						ItemStack banner=new ItemStack(Items.BANNER,1,(i==0?EnumDyeColor.RED:EnumDyeColor.BLUE).getDyeDamage());
						NBTTagList patterns=new NBTTagList();
						banner.getOrCreateSubCompound("BlockEntityTag").setTag("Patterns", patterns);
						NBTTagCompound pattern=new NBTTagCompound();
						if(i==0){
							pattern.setString("Pattern", "rb");
							pattern.setInteger("Color", 15);
						}
						else{
							pattern.setString("Pattern", "bb");
							pattern.setInteger("Color", 15);
						}
						
						patterns.appendTag(pattern);
						if(j==1){
							NBTTagCompound patternfast=new NBTTagCompound();
							patternfast.setString("Pattern", "fs");
							patternfast.setInteger("Color", 15);
							patterns.appendTag(patternfast);
						}
						
						list.add(banner);
					}
				}
		    }
		};
		tabsurvivaltf2 = new CreativeTabs("tf2misc") {
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(Item.getItemFromBlock(blockCabinet));
			}
		};
		// EntityRegistry.registerModEntity(EntityBullet.class, "bullet", 1,
		// this, 256, 100, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"heavy"),EntityHeavy.class, "heavy", 2, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"scout"),EntityScout.class, "scout", 3, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"sniper"),EntitySniper.class, "sniper", 4, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"soldier"),EntitySoldier.class, "soldier", 5, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"pyro"),EntityPyro.class, "pyro", 6, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"demoman"),EntityDemoman.class, "demoman", 7, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"medic"),EntityMedic.class, "medic", 8, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"spy"),EntitySpy.class, "spy", 9, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"engineer"),EntityEngineer.class, "engineer", 10, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"rocket"),EntityRocket.class, "rocket", 11, this, 64, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"flame"),EntityFlame.class, "flame", 12, this, 0, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"grenade"),EntityGrenade.class, "grenade", 13, this, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"sticky"),EntityStickybomb.class, "sticky", 14, this, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"simple"),EntityProjectileSimple.class, "simple", 26, this, 64, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"syringe"),EntityStickProjectile.class, "syringe", 15, this, 64, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"flare"),EntityFlare.class, "flare", 20, this, 64, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"jar"),EntityJar.class, "jar", 21, this, 64, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"ball"),EntityBall.class, "ball", 22, this, 64, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"sentry"),EntitySentry.class, "sentry", 16, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"dispenser"),EntityDispenser.class, "dispenser", 17, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"teleporter"),EntityTeleporter.class, "teleporter", 18, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"hale"),EntitySaxtonHale.class, "hale", 19, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"monoculus"),EntityMonoculus.class, "monoculus", 23, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"hhh"),EntityHHH.class, "hhh", 24, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID,"merasmus"),EntityMerasmus.class, "merasmus", 25, this, 80, 3, true);
		// GameRegistry.registerItem(new ItemArmor(TF2weapons.OPARMOR, 3,
		// 0).setUnlocalizedName("oparmor").setTextureName("diamond_helmet").setCreativeTab(tabtf2),"oparmor");
		GameRegistry.register(itemPlacer = new ItemMonsterPlacerPlus().setUnlocalizedName("monsterPlacer").setRegistryName(TF2weapons.MOD_ID + ":placer"));
		GameRegistry.register(itemDisguiseKit = new ItemDisguiseKit().setUnlocalizedName("disguiseKit").setRegistryName(TF2weapons.MOD_ID + ":disguise_kit"));
		GameRegistry.register(itemBuildingBox = new ItemBuildingBox().setUnlocalizedName("buildingBox").setRegistryName(TF2weapons.MOD_ID + ":building_box"));
		GameRegistry.register(itemSandvich = new ItemFood(14, 1, false).setPotionEffect(new PotionEffect(MobEffects.REGENERATION, 120, 2), 1f).setUnlocalizedName("sandvich")
				.setCreativeTab(tabutilitytf2).setRegistryName(TF2weapons.MOD_ID + ":sandvich"));
		GameRegistry.register(itemChocolate = new ItemFood(7, 0.6F, false).setPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 600, 0), 1f).setUnlocalizedName("chocolate")
				.setCreativeTab(tabutilitytf2).setRegistryName(TF2weapons.MOD_ID + ":chocolate"));
		GameRegistry.register(itemHorn = new ItemHorn().setUnlocalizedName("horn").setRegistryName(TF2weapons.MOD_ID + ":horn"));
		GameRegistry.register(itemAmmo = new ItemAmmo().setUnlocalizedName("tf2ammo").setRegistryName(TF2weapons.MOD_ID + ":ammo"));
		GameRegistry.register(itemAmmoPackage = new ItemAmmoPackage().setUnlocalizedName("tf2ammobox").setRegistryName(TF2weapons.MOD_ID + ":ammo_box"));
		GameRegistry.register(itemAmmoFire = new ItemFireAmmo(10, 350).setUnlocalizedName("tf2ammo").setRegistryName(TF2weapons.MOD_ID + ":ammo_fire"));
		GameRegistry.register(itemAmmoMedigun = new ItemFireAmmo(12, 1400).setUnlocalizedName("tf2ammo").setRegistryName(TF2weapons.MOD_ID + ":ammo_medigun"));
		GameRegistry.register(itemAmmoBelt = new ItemAmmoBelt().setUnlocalizedName("ammoBelt").setRegistryName(TF2weapons.MOD_ID + ":ammo_belt").setCreativeTab(tabsurvivaltf2));
		GameRegistry.register(itemScoutBoots = new ItemArmorTF2(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET,"Allows double jumping",0f)
				.setUnlocalizedName("scoutBoots").setRegistryName(TF2weapons.MOD_ID + ":scout_shoes").setCreativeTab(tabutilitytf2));
		GameRegistry.register(itemMantreads = new ItemArmorTF2(ArmorMaterial.IRON, 0, EntityEquipmentSlot.FEET,"Deals 1.8x falling damage to the player you land on",0.75f)
				.setUnlocalizedName("mantreads").setRegistryName(TF2weapons.MOD_ID + ":mantreads").setCreativeTab(tabutilitytf2));
		// GameRegistry.register(itemCopperIngot=new
		// Item().setUnlocalizedName("ingotCopper").setCreativeTab(tabtf2).setRegistryName(TF2weapons.MOD_ID+":ingotCopper"));
		// GameRegistry.register(itemLeadIngot=new
		// Item().setUnlocalizedName("ingotLead").setCreativeTab(tabtf2).setRegistryName(TF2weapons.MOD_ID+":ingotLead"));
		// GameRegistry.register(itemAustraliumIngot=new
		// Item().setUnlocalizedName("ingotAustralium").setCreativeTab(tabtf2).setRegistryName(TF2weapons.MOD_ID+":ingotAustralium"));
		GameRegistry.register(itemTF2 = new ItemTF2().setRegistryName(TF2weapons.MOD_ID + ":itemTF2"));

		GameRegistry.registerTileEntity(TileEntityUpgrades.class, "UpgradeStation");
		GameRegistry.registerTileEntity(TileEntityAmmoFurnace.class, "AmmoFurnace");

		registerBlock(blockCabinet = new BlockCabinet().setHardness(5.0F).setResistance(10.0F).setUnlocalizedName("cabinet"), TF2weapons.MOD_ID + ":tf2workbench");
		registerBlock(blockAmmoFurnace = new BlockAmmoFurnace().setHardness(5.0F).setResistance(10.0F).setUnlocalizedName("ammoFurnace"), TF2weapons.MOD_ID + ":ammo_furnace");
		registerBlock(blockUpgradeStation = new BlockUpgradeStation().setBlockUnbreakable().setResistance(10.0F).setUnlocalizedName("upgradeStation"),
				TF2weapons.MOD_ID + ":upgrade_station");
		registerBlock(blockCopperOre = new BlockOre().setCreativeTab(tabsurvivaltf2).setHardness(3.0F).setResistance(5.0F).setUnlocalizedName("oreCopper"),
				TF2weapons.MOD_ID + ":copper_ore");
		registerBlock(blockLeadOre = new BlockOre().setCreativeTab(tabsurvivaltf2).setHardness(3.0F).setResistance(5.0F).setUnlocalizedName("oreLead"),
				TF2weapons.MOD_ID + ":lead_ore");
		registerBlock(blockAustraliumOre = new BlockOre().setCreativeTab(tabsurvivaltf2).setHardness(6.0F).setResistance(10.0F).setUnlocalizedName("oreAustralium"),
				TF2weapons.MOD_ID + ":australium_ore");
		registerBlock(blockAustralium = new Block(Material.IRON, MapColor.GOLD).setCreativeTab(tabsurvivaltf2).setHardness(9.0F).setResistance(20.0F)
				.setUnlocalizedName("blockAustralium"), TF2weapons.MOD_ID + ":australium_block");
		GameRegistry.register(blockProp= new BlockProp(Material.WOOD, MapColor.GOLD).setHardness(2.0F).setResistance(4.0F)
				.setUnlocalizedName("blockProp").setRegistryName(TF2weapons.MOD_ID + ":prop_block"));

		OreDictionary.registerOre("oreCopper", blockCopperOre);
		OreDictionary.registerOre("oreLead", blockLeadOre);
		OreDictionary.registerOre("oreAustralium", blockAustraliumOre);
		OreDictionary.registerOre("blockAustralium", blockAustralium);
		OreDictionary.registerOre("ingotCopper", new ItemStack(itemTF2, 1, 0));
		OreDictionary.registerOre("ingotLead", new ItemStack(itemTF2, 1, 1));
		OreDictionary.registerOre("ingotAustralium", new ItemStack(itemTF2, 1, 2));
		OreDictionary.registerOre("nuggetAustralium", new ItemStack(itemTF2, 1, 9));

		blockCopperOre.setHarvestLevel("pickaxe", 1);
		blockLeadOre.setHarvestLevel("pickaxe", 1);
		blockAustraliumOre.setHarvestLevel("pickaxe", 2);

		ItemAmmo.STACK_FILL = new ItemStack(itemAmmo);
		CapabilityManager.INSTANCE.register(TF2PlayerCapability.class, new NullStorage<TF2PlayerCapability>(), new Callable<TF2PlayerCapability>() {

			@Override
			public TF2PlayerCapability call() throws Exception {
				// TODO Auto-generated method stub
				return new TF2PlayerCapability(null);
			}

		});
		CapabilityManager.INSTANCE.register(WeaponsCapability.class, new NullStorage<WeaponsCapability>(), new Callable<WeaponsCapability>() {

			@Override
			public WeaponsCapability call() throws Exception {
				// TODO Auto-generated method stub
				return new WeaponsCapability(null);
			}

		});
		CapabilityManager.INSTANCE.register(InventoryWearables.class, new NullStorage<InventoryWearables>(), new Callable<InventoryWearables>() {

			@Override
			public InventoryWearables call() throws Exception {
				// TODO Auto-generated method stub
				return new InventoryWearables(null);
			}

		});
		CapabilityManager.INSTANCE.register(WeaponData.WeaponDataCapability.class, new NullStorage<WeaponData.WeaponDataCapability>(),
				new Callable<WeaponData.WeaponDataCapability>() {

					@Override
					public WeaponData.WeaponDataCapability call() throws Exception {
						// TODO Auto-generated method stub
						return new WeaponData.WeaponDataCapability();
					}

				});
		CapabilityManager.INSTANCE.register(TF2EventsCommon.TF2WorldStorage.class, new NullStorage<TF2EventsCommon.TF2WorldStorage>(),
				new Callable<TF2EventsCommon.TF2WorldStorage>() {

					@Override
					public TF2EventsCommon.TF2WorldStorage call() throws Exception {
						// TODO Auto-generated method stub
						return new TF2EventsCommon.TF2WorldStorage();
					}

				});
		GameRegistry.register(bonk = new PotionTF2Item(false, 0x696969, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/bonk.png")).setPotionName("effect.bonk")
				.setRegistryName(TF2weapons.MOD_ID + ":bonkEff"));
		GameRegistry.register(stun = new PotionTF2(true, 0, 3, 1).setPotionName("effect.stun").setRegistryName(TF2weapons.MOD_ID + ":stunEff")
				.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-14B354F0D8BA", -0.5D, 2));
		GameRegistry.register(crit = new PotionTF2Item(false, 0, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/critacola.png")).setPotionName("effect.crit")
				.setRegistryName(TF2weapons.MOD_ID + ":critEff")
				.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-14B354E56B59", 0.25D, 2));
		GameRegistry.register(buffbanner = new PotionTF2Item(false, 0, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/buffbanner.png")).setPotionName("effect.banner")
				.setRegistryName(TF2weapons.MOD_ID + ":bannerEff"));
		GameRegistry.register(backup = new PotionTF2Item(false, 0, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/backup.png")).setPotionName("effect.backup")
				.setRegistryName(TF2weapons.MOD_ID + ":backupEff"));
		GameRegistry.register(conch = new PotionTF2Item(false, 0, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/conch.png")).setPotionName("effect.conch")
				.setRegistryName(TF2weapons.MOD_ID + ":conchEff")
				.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-14B35B5565E2", 0.25D, 2));
		GameRegistry.register(markDeath = new PotionTF2(true, 0, 1, 2).setPotionName("effect.markDeath").setRegistryName(TF2weapons.MOD_ID + ":markDeathEff"));
		GameRegistry.register(critBoost = new PotionTF2(false, 0, 4, 0).setPotionName("effect.critBoost").setRegistryName(TF2weapons.MOD_ID + ":critBoostEff"));
		GameRegistry.register(jarate = new PotionTF2Item(true, 0xFFD500, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/jarate.png")).setPotionName("effect.jarate")
				.setRegistryName(TF2weapons.MOD_ID + ":jarateEff"));
		GameRegistry.register(madmilk = new PotionTF2Item(true, 0xF1F1F1, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/madmilk.png")).setPotionName("effect.madmilk")
				.setRegistryName(TF2weapons.MOD_ID + ":madmilkEff"));
		GameRegistry.register(charging = new PotionTF2Item(false, 0, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/charging_targe.png")).setPotionName("effect.charging")
				.setRegistryName(TF2weapons.MOD_ID + ":chargingEff")
				.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-14B35B5565E6", 2D, 2));
		GameRegistry.register(uber = new PotionTF2Item(false, 0, new ResourceLocation(TF2weapons.MOD_ID, "textures/items/medigun_im.png")).setPotionName("effect.uber")
				.setRegistryName(TF2weapons.MOD_ID + ":uberEff"));
		GameRegistry.register(it = new PotionTF2(true, 0xFFFFFFFF, 1, 2).setPotionName("effect.it").setRegistryName(TF2weapons.MOD_ID + ":itEff"));
		GameRegistry.register(bombmrs = new PotionTF2(false, 0xFFFFFFFF, 1, 2).setPotionName("effect.bombmrs").setRegistryName(TF2weapons.MOD_ID + ":bombEff")
				.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE6F-7CE8-4030-940E-14B354F0D8BA", 1.25D, 2));
		// conf.save();
		WeaponData.PropertyType.init();
		if(!disableGeneration){
			MapGenStructureIO.registerStructureComponent(MannCoBuilding.class, "ViMC");
			VillagerRegistry.instance().registerVillageCreationHandler(new MannCoBuilding.CreationHandler());
		}
		TF2Sounds.registerSounds();
		if (event.getSide() == Side.CLIENT) {
			loadWeapons();
			//System.out.println(MapList.nameToData.get("rocketlauncher"));
		}
		cratesOpened = (new StatBasic("stat.cratesOpened", new TextComponentTranslation("stat.cratesOpened", new Object[0]))).registerStat();

		proxy.preInit();
		MinecraftForge.EVENT_BUS.register(new TF2EventsCommon());
		MinecraftForge.ORE_GEN_BUS.register(new TF2EventsCommon());
	}

	public static void createConfig() {
		if(conf.get("gameplay", "Destructible terrain", "Upgrade only", "Explosions can destroy blocks").getType()==Property.Type.BOOLEAN)
			conf.getCategory("gameplay").remove("Destructible terrain");
		String destr=conf.get("gameplay", "Destructible terrain", "Upgrade only", "Explosions can destroy blocks").setValidValues(new String[] { "Always", "Upgrade only", "Never" }).getString();
		if(destr.equalsIgnoreCase("Always"))
			destTerrain=2;
		else if(destr.equalsIgnoreCase("Upgrade only"))
			destTerrain=1;
		else
			destTerrain=0;
		medigunLock = conf.getBoolean("Medigun lock target", "gameplay", false, "Left Click selects healing target");
		fastMetalProduction = conf.getBoolean("Fast metal production", "gameplay", false, "Dispensers produce metal every 5 seconds");
		dispenserHeal = conf.getBoolean("Dispensers heal players", "gameplay", true, "Dispensers heal other players");
		disableSpawn = conf.getBoolean("Disable mob spawning", "gameplay", false, "Disable mod-specific mobs spawning (Requires game restart)");
		disableEvent = conf.getBoolean("Disable events", "gameplay", false, "Disable random tf2 boss spawn and invasion event");
		disableContracts = conf.getBoolean("Disable contracts", "gameplay", false, "Stop new contracts from appearing");
		disableGeneration = conf.getBoolean("Disable structures", "gameplay", false, "Disable structures generation, such as Mann Co. building");
		weaponVersion = conf.getInt("Weapon Config Version", "internal", getCurrentWeaponVersion(), 0, 1000, "");
		conf.get("gameplay", "Disable mob spawning", false).setRequiresMcRestart(true);
		conf.get("gameplay", "Disable structures", false).setRequiresMcRestart(true);
		spawnOres = conf.get("gameplay", "Spawn ores", "Default").setValidValues(new String[] { "Always", "Default", "Never" }).getString();
		naturalCheck = conf.get("gameplay", "Natural mob detection", "Always").setValidValues(new String[] { "Always", "Fast", "Never" }).getString();
		shootAttract = conf.getBoolean("Shooting attracts mobs", "gameplay", true, "Gunfire made by players attracts mobs");
		randomCrits = conf.getBoolean("Random critical hits", "gameplay", true, "Enables randomly appearing critical hits that deal 3x more damage");
		damageMultiplier = 200f/(float)conf.getInt("TF2 - Minecraft health translation", "gameplay", 200,-10000,10000, "How much 10 minecraft hearts are worth in TF2 health");
		dynamicLights = conf.getBoolean("Dynamic Lights", "modcompatibility", true, "Enables custom light sources for AtomicStryker's Dynamic Lights mod")
				&& Loader.isModLoaded("dynamiclights");
		dynamicLightsProj = conf.getBoolean("Dynamic Lights - Projectiles", "modcompatibility", true, "Should projectiles emit light");
		bossReappear = conf.getInt("Boss respawn cooldown", "gameplay", 360000, 0, Integer.MAX_VALUE, "Maximum boss reappear time in ticks. Bosses always spawn in full moon");
		updateOreGenStatus();

		if (conf.hasChanged())
			conf.save();
	}

	public static void syncConfig() {
		ConfigCategory gameplay=conf.getCategory("gameplay");
		
		String destr=gameplay.get("Destructible terrain").getString();
		if(destr.equalsIgnoreCase("Always"))
			destTerrain=2;
		else if(destr.equalsIgnoreCase("Upgrade only"))
			destTerrain=1;
		else
			destTerrain=0;
		medigunLock = gameplay.get("Medigun lock target").getBoolean();
		fastMetalProduction = gameplay.get("Fast metal production").getBoolean();
		dispenserHeal = gameplay.get("Dispensers heal players").getBoolean();
		disableSpawn = gameplay.get("Disable mob spawning").getBoolean();
		disableEvent = gameplay.get("Disable events").getBoolean();
		disableContracts = gameplay.get("Disable contracts").getBoolean();
		disableGeneration = gameplay.get("Disable structures").getBoolean();
		spawnOres = gameplay.get("Spawn ores").getString();
		naturalCheck = gameplay.get("Natural mob detection").getString();
		shootAttract = gameplay.get("Shooting attracts mobs").getBoolean();
		randomCrits = gameplay.get("Random critical hits").getBoolean();
		bossReappear = gameplay.get("Boss respawn cooldown").getInt();
		damageMultiplier = 200f/(float)gameplay.get("TF2 - Minecraft health translation").getInt(200);
		
		dynamicLights = conf.get("modcompatibility", "Dynamic Lights", true).getBoolean() && Loader.isModLoaded("dynamiclights");
		dynamicLightsProj = conf.get("modcompatibility", "Dynamic Lights - Projectiles", true).getBoolean();
		updateOreGenStatus();

		conf.save();
	}

	@SidedProxy(clientSide = "rafradek.TF2weapons.ClientProxy", serverSide = "rafradek.TF2weapons.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			AchievementPage.registerAchievementPage(new TF2Achievements());
		}
		GameRegistry.addSmelting(new ItemStack(blockCopperOre), new ItemStack(itemTF2, 1, 0), 0.5f);
		GameRegistry.addSmelting(new ItemStack(blockLeadOre), new ItemStack(itemTF2, 1, 1), 0.55f);
		GameRegistry.addSmelting(new ItemStack(blockAustraliumOre), new ItemStack(itemTF2, 1, 2), 2f);
		GameRegistry.addSmelting(new ItemStack(itemTF2, 1, 3), new ItemStack(Items.IRON_INGOT, 2), 0.35f);
		GameRegistry.registerFuelHandler(new IFuelHandler() {

			@Override
			public int getBurnTime(ItemStack fuel) {
				// TODO Auto-generated method stub
				return fuel.getItem() instanceof ItemCrate ? 300 : 0;
			}

		});
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCabinet), "SCS", "SIS", 'S', new ItemStack(itemTF2, 1, 3), 'C', "workbench", 'I', "blockIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTF2, 1, 2), "AAA", "AAA", "AAA", 'A', new ItemStack(itemTF2, 1, 6)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemTF2, 9, 6), "ingotAustralium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockAustralium), "AAA", "AAA", "AAA", 'A', new ItemStack(itemTF2, 1, 2)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemTF2, 9, 2), "blockAustralium"));
		GameRegistry.addRecipe(new OpenCrateRecipe());

		LootFunctionManager.registerFunction(new EntityBuildingFunction.Serializer());
		LootFunctionManager.registerFunction(new EntityOfClassFunction.Serializer());
		LootFunctionManager.registerFunction(new RandomWeaponFunction.Serializer());
		LootConditionManager.registerCondition(new KilledByTeam.Serializer());
		
		LootTableList.register(new ResourceLocation(MOD_ID, "chests/simple_dungeon"));
		LootTableList.register(new ResourceLocation(MOD_ID, "chests/nether_bridge"));
		LootTableList.register(new ResourceLocation(MOD_ID, "chests/stronghold_corridor"));
		LootTableList.register(new ResourceLocation(MOD_ID, "chests/end_city_treasure"));
		LootTableList.register(new ResourceLocation(MOD_ID, "chests/abandoned_mineshaft"));
		
		lootTF2Character = LootTableList.register(new ResourceLocation(MOD_ID, "entities/tf2character"));
		lootScout = LootTableList.register(new ResourceLocation(MOD_ID, "entities/scout"));
		lootHeavy = LootTableList.register(new ResourceLocation(MOD_ID, "entities/heavy"));
		lootSniper = LootTableList.register(new ResourceLocation(MOD_ID, "entities/sniper"));
		lootSpy = LootTableList.register(new ResourceLocation(MOD_ID, "entities/spy"));
		lootDemoman = LootTableList.register(new ResourceLocation(MOD_ID, "entities/demoman"));
		lootEngineer = LootTableList.register(new ResourceLocation(MOD_ID, "entities/engineer"));
		lootSoldier = LootTableList.register(new ResourceLocation(MOD_ID, "entities/soldier"));
		lootMedic = LootTableList.register(new ResourceLocation(MOD_ID, "entities/medic"));
		lootPyro = LootTableList.register(new ResourceLocation(MOD_ID, "entities/pyro"));
		lootHale = LootTableList.register(new ResourceLocation(MOD_ID, "entities/hale"));

		ArrayList<Biome> biomesList = new ArrayList<Biome>();
		for (Biome biome : GameRegistry.findRegistry(Biome.class)){
			if(biome!=Biomes.HELL && biome!=Biomes.SKY)
			biomesList.add(biome);
		}
		if (!disableSpawn) {
			Biome[] biomes = biomesList.toArray(new Biome[biomesList.size()]);
			EntityRegistry.addSpawn(EntitySpy.class, 9, 1, 3, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntityPyro.class, 12, 1, 3, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntityDemoman.class, 12, 1, 3, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntitySoldier.class, 12, 1, 3, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntitySniper.class, 9, 1, 3, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntityHeavy.class, 12, 1, 3, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntityScout.class, 12, 1, 3, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntityEngineer.class, 9, 1, 3, EnumCreatureType.MONSTER, biomes);
		}

		// new
		// Item(2498).setUnlocalizedName("FakeItem").setTextureName(TF2weapons.MOD_ID+":saw").setCreativeTab(CreativeTabs.tabBlock);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(itemPlacer, new BehaviorDefaultDispenseItem() {
			@Override
			public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
				EnumFacing enumfacing = p_82487_1_.getBlockState().getValue(BlockDispenser.FACING);
				double d0 = p_82487_1_.getX() + enumfacing.getFrontOffsetX();
				double d1 = p_82487_1_.getY() + 0.2F;
				double d2 = p_82487_1_.getZ() + enumfacing.getFrontOffsetZ();
				Entity entity = ItemMonsterPlacerPlus.spawnCreature(p_82487_1_.getWorld(), p_82487_2_.getItemDamage(), d0, d1, d2,
						p_82487_2_.getTagCompound() != null && p_82487_2_.getTagCompound().hasKey("SavedEntity") ? p_82487_2_.getTagCompound().getCompoundTag("SavedEntity")
								: null);

				if (entity instanceof EntityLivingBase && p_82487_2_.hasDisplayName())
					((EntityLiving) entity).setCustomNameTag(p_82487_2_.getDisplayName());

				p_82487_2_.splitStack(1);
				return p_82487_2_;
			}
		});

		Iterator<String> iterator = MapList.weaponClasses.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			GameRegistry.register(MapList.weaponClasses.get(name), new ResourceLocation(MOD_ID, "" + name.toLowerCase()));
		}

		

		network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
		network.registerMessage(TF2ActionHandler.class, TF2Message.ActionMessage.class, 0, Side.SERVER);
		network.registerMessage(TF2PropertyHandler.class, TF2Message.PropertyMessage.class, 2, Side.SERVER);
		network.registerMessage(TF2BulletHandler.class, TF2Message.BulletMessage.class, 3, Side.SERVER);
		network.registerMessage(TF2ProjectileHandler.class, TF2Message.PredictionMessage.class, 4, Side.SERVER);
		network.registerMessage(TF2GuiConfigHandler.class, TF2Message.GuiConfigMessage.class, 5, Side.SERVER);
		network.registerMessage(TF2CapabilityHandler.class, TF2Message.CapabilityMessage.class, 7, Side.SERVER);
		network.registerMessage(TF2ShowGuiHandler.class, TF2Message.ShowGuiMessage.class, 9, Side.SERVER);
		network.registerMessage(TF2DisguiseHandler.class, TF2Message.DisguiseMessage.class, 11, Side.SERVER);
		network.registerMessage(TF2ActionHandler.class, TF2Message.ActionMessage.class, 0, Side.CLIENT);
		network.registerMessage(TF2UseHandler.class, TF2Message.UseMessage.class, 1, Side.CLIENT);
		network.registerMessage(TF2PropertyHandler.class, TF2Message.PropertyMessage.class, 2, Side.CLIENT);
		network.registerMessage(TF2CapabilityHandler.class, TF2Message.CapabilityMessage.class, 6, Side.CLIENT);
		network.registerMessage(TF2WeaponDataHandler.class, TF2Message.WeaponDataMessage.class, 8, Side.CLIENT);
		network.registerMessage(TF2WearableChangeHandler.class, TF2Message.WearableChangeMessage.class, 10, Side.CLIENT);
		network.registerMessage(TF2WeaponDropHandler.class, TF2Message.WeaponDroppedMessage.class, 12, Side.CLIENT);
		network.registerMessage(TF2ContractHandler.class, TF2Message.ContractMessage.class, 13, Side.CLIENT);
		network.registerMessage(TF2VelocityAddHandler.class, TF2Message.VelocityAddMessage.class, 14, Side.CLIENT);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {

			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				// TODO Auto-generated method stub
				BlockPos pos = new BlockPos(x, y, z);
				if (ID == 0){
					ContainerWearables container=new ContainerWearables(player.inventory, player.getCapability(INVENTORY_CAP, null), false, player);
					container.addListener(new TF2EventsCommon.TF2ContainerListener((EntityPlayerMP) player));
					return container;
				}
				else if (ID == 1 && world.getBlockState(pos).getBlock() instanceof BlockCabinet)
					return new ContainerTF2Workbench(player, player.inventory,
							/* (TileEntityCabinet) world.getTileEntity(pos), */ world, pos);
				else if (ID == 2 && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityUpgrades){
					if(((TileEntityUpgrades) world.getTileEntity(pos)).attributes.size()==0){
						((TileEntityUpgrades) world.getTileEntity(pos)).generateUpgrades(player.getRNG());
					}
					if(player instanceof EntityPlayerMP)
						((EntityPlayerMP)player).connection.sendPacket(world.getTileEntity(pos).getUpdatePacket());
					return new ContainerUpgrades(player, player.inventory, (TileEntityUpgrades) world.getTileEntity(pos), world, pos);
				}
				else if (ID == 3 && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityAmmoFurnace)
					return new ContainerAmmoFurnace(player.inventory, (TileEntityAmmoFurnace) world.getTileEntity(pos));
				else if (ID == 4){
					
					/*if(!world.getCapability(WORLD_CAP, null).lostItems.containsKey(player.getName()))
						world.getCapability(WORLD_CAP, null).lostItems.put(player.getName(), new ItemStackHandler(27));
					return new ContainerRecover(player.inventory, world.getCapability(WORLD_CAP, null).lostItems.get(player.getName()));*/
				}
				return null;
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				// TODO Auto-generated method stub
				BlockPos pos = new BlockPos(x, y, z);
				if (ID == 0)
					return new GuiWearables(new ContainerWearables(player.inventory, player.getCapability(INVENTORY_CAP, null), true, player));
				else if (ID == 1 && world.getBlockState(pos).getBlock() instanceof BlockCabinet)
					return new GuiTF2Crafting(player.inventory,
							/* (TileEntityCabinet) world.getTileEntity(pos), */world, pos);
				else if (ID == 2 && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityUpgrades){
					return new GuiUpgradeStation(player.inventory, (TileEntityUpgrades) world.getTileEntity(pos), world, pos);
				}
				else if (ID == 3 && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityAmmoFurnace)
					return new GuiAmmoFurnace(player.inventory, (TileEntityAmmoFurnace) world.getTileEntity(pos));
				else if (ID == 4)
					return new GuiRecover(player.inventory,new ItemStackHandler(27));
				return null;
			}

		});
		// TickRegistry.registerTickHandler(new CommonTickHandler(),
		// Side.SERVER);

		// FMLCommonHandler.instance().bus().register(new
		// TF2EventBusListener());
		proxy.registerRenderInformation();
		MapList.nameToData.clear();
		MapList.buildInAttributes.clear();

	}

	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent event) {

		updateOreGenStatus();
	}

	public static void updateOreGenStatus() {

		//System.out.println("Generowane: " + OreDictionary.getOres("oreCopper").size());
		generateCopper = false;
		generateLead = false;
		generateAustralium = false;

		if (spawnOres.equals("Always")) {
			generateCopper = true;
			generateLead = true;
			generateAustralium = true;
		} else if (spawnOres.equals("Default")) {
			generateAustralium = true;
			if (OreDictionary.getOres("oreCopper").size() == 1)
				generateCopper = true;
			if (OreDictionary.getOres("oreLead").size() == 1)
				generateLead = true;
		}
	}

	public static void registerBlock(Block block, String name) {
		GameRegistry.register(block.setRegistryName(name));
		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
		proxy.registerItemBlock(item);
	}

	public static void sendTracking(IMessage message,Entity entity){
		if(entity instanceof EntityPlayerMP)
			network.sendTo(message, (EntityPlayerMP) entity);
		for(EntityPlayer player:((WorldServer)entity.world).getEntityTracker().getTrackingPlayers(entity)){
			network.sendTo(message, (EntityPlayerMP) player);
		}
	}
	/*public static TargetPoint pointFromEntity(Entity entity) {
		return new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 256);
	}*/

	public static void loadWeapons() {

		MapList.nameToData.clear();
		MapList.buildInAttributes.clear();
		try {
			ByteArrayOutputStream output=new ByteArrayOutputStream();
			DataOutputStream stream=new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(output)));
		
			loadConfig(new File(instance.weaponDir, "Weapons.json"),stream);
			File[] files = instance.weaponDir.listFiles(new FilenameFilter() {
	
				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".json") && !arg1.equalsIgnoreCase("Weapons.json");
				}
	
			});
			for (File file : files)
				loadConfig(file,stream);
			stream.flush();
			stream.close();
			itemDataCompressed=output.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void loadConfig(File file,DataOutput output) {
		/*
		 * Configuration weaponsFile= new Configuration(file);
		 * weaponsFile.load();
		 */
		ArrayList<WeaponData> list = WeaponData.parseFile(file);
		for (WeaponData data : list) {
			String weaponEntry = data.getName();
			// Class<?> weaponClass =
			// MapList.weaponClasses.get(weaponData.get("Class").getString());
			try {
				// System.out.println("attach "+weaponEntry);
				if (PropertyType.BASED_ON.hasKey(data) && MapList.nameToData.containsKey(PropertyType.BASED_ON.getString(data)))
					data = attach(MapList.nameToData.get(PropertyType.BASED_ON.getString(data)), data);
				loadWeapon(weaponEntry, data);
				
				output.writeUTF(data.getName());
				output.writeByte(data.properties.size());
				for (PropertyType type : data.properties.keySet()){
					
					type.serialize(output, data);
				}
				output.writeByte(Math.max(data.attributes.size(), data.crateContent.size()));
				for (Entry<TF2Attribute, Float> attr : data.attributes.entrySet()) {
					output.writeByte(attr.getKey().id);
					output.writeFloat(attr.getValue());
				}
				for (Entry<String, Integer> entry : data.crateContent.entrySet()) {
					output.writeUTF(entry.getKey());
					output.writeFloat(entry.getValue());
				}

			} catch (Exception var4) {
				var4.printStackTrace();
			}
		}
	}
	public static void loadWeapon(String name, WeaponData weapon) {
		IForgeRegistry<SoundEvent> registry = GameRegistry.findRegistry(SoundEvent.class);
		for (PropertyType propType : weapon.properties.keySet())
			if (propType.name.contains("sound")) {
				ResourceLocation soundLocation = new ResourceLocation(propType.getString(weapon));
				if (!registry.containsKey(soundLocation)) {
					SoundEvent.REGISTRY.register(0,soundLocation,new SoundEvent(soundLocation));
					if (propType==WeaponData.PropertyType.FIRE_SOUND || propType==WeaponData.PropertyType.FIRE_LOOP_SOUND || propType==WeaponData.PropertyType.CHARGED_FIRE_SOUND)
						SoundEvent.REGISTRY.register(0,new ResourceLocation(propType.getString(weapon) + ".crit"),new SoundEvent(new ResourceLocation(propType.getString(weapon) + ".crit")));
				}
			}
		/*
		 * else{ weaponList[Integer.parseInt(weaponEntry)] =(ItemUsable)
		 * weaponClass.getConstructor(new Class[] {ConfigCategory.class,
		 * ConfigCategory.class}).newInstance(new Object[] {weaponData, null});
		 * }
		 */
		// GameRegistry.registerItem(weaponList[Integer.parseInt(weaponEntry)],
		// "weapon"+Integer.parseInt(weaponEntry));
		MapList.nameToData.put(name, weapon);
		// System.out.println("Weapon read: "+name);
		/*
		 * for(Entry<PropertyType, WeaponData.Property>
		 * entry:weapon.properties.entrySet()){
		 * System.out.println("Property: "+entry.getKey().name+" Value: "+entry.
		 * getValue().intValue+" "+entry.getValue().stringValue); }
		 */
		// LanguageRegistry.instance().addStringLocalization(weaponData.get("Name").getString()+".name",
		// weaponData.get("Name").getString());
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("Type", name);
		NBTTagCompound tag2 = new NBTTagCompound();
		if (!weapon.attributes.isEmpty())
			for (Entry<TF2Attribute, Float> entry : weapon.attributes.entrySet())
				tag2.setFloat(String.valueOf(entry.getKey().id), entry.getValue());
		tag.setTag("Attributes", tag2);
		MapList.buildInAttributes.put(name, tag);
	}

	@SuppressWarnings("unchecked")
	public static WeaponData attach(WeaponData base, WeaponData additional) {
		for (PropertyType prop : base.properties.keySet())
			if (!additional.properties.containsKey(prop))
				additional.properties.put(prop, base.properties.get(prop));
		MapList.buildInAttributes.put(additional.getName(), MapList.buildInAttributes.get(base.getName()));
		// System.out.println("merged: "+additional.getName()+" "+key);

		// new ConfigCategory(null, additional);
		return additional;
	}

	@Mod.EventHandler
	public void serverPreInit(FMLServerAboutToStartEvent event) {
		// System.out.println("Starting server");
		
		loadWeapons();
		if (!event.getServer().isDedicatedServer())
			for (WeaponData weapon : MapList.nameToData.values())
				ClientProxy.RegisterWeaponData(weapon);
		if(event.getSide()==Side.SERVER)
			AchievementPage.registerAchievementPage(new TF2Achievements());
	}

	@Mod.EventHandler
	public void serverInit(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandGiveWeapon());
		event.registerServerCommand(new CommandResetWeapons());
		event.registerServerCommand(new CommandResetStat());
		event.registerServerCommand(new CommandGenerateReferences());
		try {
			server = event.getServer();
			File input = new File(((AnvilSaveConverter) server.getActiveAnvilConverter()).savesDirectory, server.getFolderName() + "/teleports.dat");
			NBTTagCompound tagRoot = CompressedStreamTools.readCompressed(new FileInputStream(input));
			NBTTagCompound tag = tagRoot.getCompoundTag("Teleporters");
			for (String keys : tag.getKeySet()) {
				TeleporterData[] blockArray = new TeleporterData[EntityTeleporter.TP_PER_PLAYER];
				EntityTeleporter.teleporters.put(UUID.fromString(keys), blockArray);
				NBTTagCompound exitTag = tag.getCompoundTag(keys);
				for (int i = 0; i < EntityTeleporter.TP_PER_PLAYER; i++)
					if (exitTag.hasKey(Integer.toString(i))) {
						int[] array = exitTag.getIntArray(Integer.toString(i));
						blockArray[i] = new TeleporterData(new BlockPos(array[0], array[1], array[2]), array[3], array[4]);
					}
			}
			EntityTeleporter.tpCount = tagRoot.getInteger("TPCount");

		} catch (IOException e) {
			System.err.println("Reading teleporter data skipped");
		}
	}

	@Mod.EventHandler
	public void serverStop(FMLServerStoppingEvent event) {
		MapList.nameToData.clear();
		MapList.buildInAttributes.clear();

		File output = new File(((AnvilSaveConverter) server.getActiveAnvilConverter()).savesDirectory, server.getFolderName() + "/teleports.dat");
		NBTTagCompound tagRoot = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		tagRoot.setTag("Teleporters", tag);

		for (Entry<UUID, TeleporterData[]> entry : EntityTeleporter.teleporters.entrySet()) {
			NBTTagCompound exitTag = new NBTTagCompound();
			for (int i = 0; i < EntityTeleporter.TP_PER_PLAYER; i++) {
				TeleporterData blockPos = entry.getValue()[i];
				if (blockPos != null)
					exitTag.setIntArray(Integer.toString(i), new int[] { blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.id, blockPos.dimension});
			}
			tag.setTag(entry.getKey().toString(), exitTag);
		}
		tagRoot.setInteger("TPCount", EntityTeleporter.tpCount);

		try {
			CompressedStreamTools.writeCompressed(tagRoot, new FileOutputStream(output));
		} catch (IOException e) {
			e.printStackTrace();
		}

		EntityTeleporter.teleporters.clear();
		EntityTeleporter.tpCount = 0;
	}

	/*
	 * public static void openWearableGUI(EntityPlayerMP player){ InventoryBasic
	 * inventory=new InventoryBasic("Wearables", false, 3); NBTTagList
	 * nbttaglist = player.getEntityData().getTagList("Wearables", 10); for (int
	 * i = 0; i < nbttaglist.tagCount(); ++i) { NBTTagCompound nbttagcompound =
	 * nbttaglist.getCompoundTagAt(i); int j = nbttagcompound.getByte("Slot") &
	 * 255; inventory.setInventorySlotContents(j,
	 * ItemStack.loadItemStackFromNBT(nbttagcompound)); } if
	 * (player.openContainer != player.inventoryContainer) {
	 * player.closeScreen(); }
	 * 
	 * player.getNextWindowId(); player.connection.sendPacket(new
	 * SPacketOpenWindow(player.currentWindowId, "rafradek_wearables",
	 * inventory.getDisplayName(), inventory.getSizeInventory(),
	 * player.getEntityId())); player.openContainer = new
	 * ContainerHorseInventory(player.inventory, inventory, horse, this);
	 * player.openContainer.windowId = player.currentWindowId;
	 * player.openContainer.addListener(this); }
	 */

	public static int calculateCritPre(ItemStack stack, EntityLivingBase living) {
		int thisCritical = 0;
		
		if (living.getActivePotionEffect(crit) != null || living.getActivePotionEffect(buffbanner) != null)
			thisCritical = 1;
		if ( thisCritical == 0 && (living.getCapability(WEAPONS_CAP, null).focusedShot(stack) || living.getCapability(WEAPONS_CAP, null).focusShotRemaining>0))
			thisCritical = 1;
		if (randomCrits && !stack.isEmpty() && stack.getItem() instanceof ItemWeapon) {
			ItemWeapon item = (ItemWeapon) stack.getItem();
			if ((!item.rapidFireCrits(stack) && item.hasRandomCrits(stack, living) && living.getRNG().nextFloat() <= item.critChance(stack, living))
					|| living.getCapability(WEAPONS_CAP, null).critTime > 0)
				thisCritical = 2;
		}
		if (living.getActivePotionEffect(critBoost) != null)
			thisCritical = 2;
		
		if (!stack.isEmpty() && (!(stack.getItem() instanceof ItemWeapon) || stack.getItem() instanceof ItemMeleeWeapon)
				&& (living.getActivePotionEffect(charging) != null || living.getCapability(WEAPONS_CAP, null).ticksBash > 0))
			if (thisCritical < 2 && (living.getCapability(WEAPONS_CAP, null).ticksBash > 0 && living.getCapability(WEAPONS_CAP, null).bashCritical)
					|| (living.getActivePotionEffect(charging) != null && living.getActivePotionEffect(charging).getDuration() < 14))
				thisCritical = 2;
			else if (thisCritical == 0 && (living.getCapability(WEAPONS_CAP, null).ticksBash > 0 || living.getActivePotionEffect(charging).getDuration() < 35))
				thisCritical = 1;
		return thisCritical;
	}

	public static DamageSource causeBulletDamage(ItemStack weapon, Entity shooter, int critical, Entity projectile) {
		return (new DamageSourceProjectile(weapon, projectile, shooter, critical)).setProjectile();
	}

	public static DamageSource causeDirectDamage(ItemStack weapon, Entity shooter, int critical) {
		return (new DamageSourceDirect(weapon, shooter, critical));
	}

	public static int roundUp(double value, int zero) {
		if (value > (int) value)
			return (int) value + 1;

		return (int) value;
	}

	public static Vec3d radiusRandom3D(float radius, Random random) {
		double x, y, z;
		double radius2 = radius * radius;
		do{ 
			x = random.nextDouble() * radius * 2 - radius;
			y = random.nextDouble() * radius * 2 - radius;
			z = random.nextDouble() * radius * 2 - radius;
		} while (x * x + y * y + z * z > radius2);
		return new Vec3d(x, y, z);

	}

	public static Vec3d radiusRandom2D(float radius, Random random) {
		/*
		 * double x=random.nextDouble()*radius*2-radius; radius -= Math.abs(x);
		 * double y=random.nextDouble()*radius*2-radius;
		 */

		/*
		 * double t = 4*Math.PI*random.nextDouble()*radius-radius; double u =
		 * (random.nextDouble()*radius*2-radius)+(random.nextDouble()*radius*2-
		 * raddddddddius); double r = u>1?2-u:u;
		 */
		float a = random.nextFloat(), b = random.nextFloat();
		return new Vec3d(Math.max(a, b) * radius * MathHelper.cos((float) (2f * Math.PI * Math.min(a, b) / Math.max(a, b))),
				Math.max(a, b) * radius * MathHelper.sin((float) (2f * Math.PI * Math.min(a, b) / Math.max(a, b))), 0);
	}

	public static List<RayTraceResult> pierce(World world, EntityLivingBase living, double startX, double startY, double startZ, double endX, double endY, double endZ,
			boolean headshot, float size, boolean pierce) {
		ArrayList<RayTraceResult> targets = new ArrayList<>();
		Vec3d var17 = new Vec3d(startX, startY, startZ);
		Vec3d var3 = new Vec3d(endX, endY, endZ);
		RayTraceResult var4 = world.rayTraceBlocks(var17, var3, false, true, false);
		var17 = new Vec3d(startX, startY, startZ);
		var3 = new Vec3d(endX, endY, endZ);

		if (var4 != null)
			var3 = new Vec3d(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);

		Entity var5 = null;
		List<Entity> var6 = world.getEntitiesWithinAABBExcludingEntity(living,
				living.getEntityBoundingBox().addCoord(endX - startX, endY - startY, endZ - startZ).expand(2D, 2D, 2D));
		// System.out.println("shoot: "+startX+","+startY+","+startZ+", do:
		// "+endX+","+endY+","+endZ+" Count: "+var6.size());
		double var7 = 0.0D;
		Vec3d collideVec = new Vec3d(0, 0, 0);
		for (Entity target : var6)
			if (target.canBeCollidedWith() && (!(target instanceof EntityLivingBase) || (target instanceof EntityLivingBase && ((EntityLivingBase) target).deathTime <= 0))) {
				AxisAlignedBB oldBB = target.getEntityBoundingBox();
				if (world.isRemote && TF2EventsClient.moveEntities) {
					float ticktime = TF2EventsClient.tickTime;
					target.setEntityBoundingBox(target.getEntityBoundingBox().offset((target.prevPosX - target.posX) * (1 - ticktime),
							(target.prevPosY - target.posY) * (1 - ticktime), (target.prevPosZ - target.posZ) * (1 - ticktime)));
				}
				AxisAlignedBB var12 = target.getEntityBoundingBox().expand(size, (double) size, size);
				RayTraceResult var13 = var12.calculateIntercept(var17, var3);

				if (var13 != null) {
					double var14 = var17.squareDistanceTo(var13.hitVec);

					if (!pierce && (var14 < var7 || var7 == 0.0D)) {
						var5 = target;
						var7 = var14;
						collideVec = var13.hitVec;
					} else if (pierce)
						targets.add(getTraceResult(target, var13.hitVec, headshot, var17, var3));
				}
				target.setEntityBoundingBox(oldBB);
			}
		// var4.hitInfo=false;
		if (!pierce && var5 != null && !(var5 instanceof EntityLivingBase && ((EntityLivingBase) var5).getHealth() <= 0))
			targets.add(getTraceResult(var5, collideVec, headshot, var17, var3));
		if (targets.isEmpty() && var4 != null && var4.typeOfHit == Type.BLOCK)
			targets.add(var4);
		else if (targets.isEmpty())
			targets.add(new RayTraceResult(Type.MISS, var3, null, null));
		return targets;
	}

	public static RayTraceResult getTraceResult(Entity target, Vec3d hitVec, boolean headshot, Vec3d start, Vec3d end) {
		RayTraceResult result = new RayTraceResult(target, hitVec);
		if (target instanceof EntityLivingBase && !(target instanceof EntityBuilding) && headshot) {
			Boolean var13 = getHead((EntityLivingBase)target).isVecInside(hitVec);
			result.hitInfo = var13;
		}
		return result;
	}

	public static AxisAlignedBB getHead(EntityLivingBase target){
		double ymax = target.getEntityBoundingBox().maxY;
		AxisAlignedBB head = new AxisAlignedBB(target.posX - 0.32, ymax - 0.32, target.posZ - 0.32, target.posX + 0.32, ymax + 0.20, target.posZ + 0.32);
		if (target instanceof EntityCreeper || target instanceof EntityEnderman || target instanceof EntityIronGolem)
			head=head.offset(0, -0.2, 0);
		if (target.width > target.height * 0.64) {
			float offsetX = -MathHelper.sin(target.renderYawOffset * 0.017453292F) * target.width * 0.35f;
	       // float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
	        float offsetZ = MathHelper.cos(target.renderYawOffset * 0.017453292F) * target.width * 0.35f;
			/*double offsetX = MathHelper.cos(target.renderYawOffset / 180.0F * (float) Math.PI) * target.width / 2;
			double offsetZ = -(double) (MathHelper.sin(target.renderYawOffset / 180.0F * (float) Math.PI) * target.width / 2);// cos*/
			// double offsetX2=- (double)(MathHelper.sin(living.rotationYaw
			// / 180.0F * (float)Math.PI) * var5.width/2);
			// double offsetZ2=(double)(MathHelper.cos(living.rotationYaw /
			// 180.0F * (float)Math.PI) * var5.width/2);//cos
			// System.out.println("Offsets: "+offsetX+" "+offsetZ+"
			// "+offsetX2+" "+offsetZ2);
			head=head.offset(offsetX, 0, offsetZ);
		}
		return head;
	}
	public static boolean isUsingShield(Entity shielded, DamageSource source) {
		if (shielded instanceof EntityLivingBase && ((EntityLivingBase) shielded).isActiveItemStackBlocking()) {
			Vec3d location = source.getDamageLocation();
			if(location == null)
				location = source.getSourceOfDamage().getPositionVector();
			if(location != null) {
				Vec3d vec3d1 = shielded.getLook(1.0F);
				Vec3d vec3d2 = location.subtractReverse(shielded.getPositionVector()).normalize();
				vec3d2 = new Vec3d(vec3d2.xCoord, 0.0D, vec3d2.zCoord);
	
				if (vec3d2.dotProduct(vec3d1) < 0.0D)
					return true;
			}
		}
		return false;
	}

	public static int calculateCritPost(Entity target, EntityLivingBase shooter, int initial, ItemStack stack) {
		if (initial > 0 && (target instanceof EntityLivingBase && ((EntityLivingBase) target).getActivePotionEffect(TF2weapons.backup) != null))
			initial = 0;
		if (initial == 0 && (target instanceof EntityLivingBase && (((EntityLivingBase) target).getActivePotionEffect(TF2weapons.markDeath) != null
				|| ((EntityLivingBase) target).getActivePotionEffect(TF2weapons.jarate) != null)))
			initial = 1;
		if (initial == 0 && !stack.isEmpty() && !target.onGround && !target.isInWater() && TF2Attribute.getModifier("Minicrit Airborne", stack, 0, shooter) != 0)
			initial = 1;
		if (initial < 2 && (!stack.isEmpty() && target.isBurning() && TF2Attribute.getModifier("Crit Burn", stack, 0, shooter) != 0))
			initial = 2;
		if (initial < 2 && (!stack.isEmpty() && shooter != null && shooter instanceof EntityPlayer && 
				(shooter.getDataManager().get(TF2EventsCommon.ENTITY_EXP_JUMP) || shooter.isElytraFlying())
				&& TF2Attribute.getModifier("Crit Rocket", stack, 0, shooter) != 0))
			initial = 2;
		if (initial == 1 && (!stack.isEmpty() && shooter != null && shooter instanceof EntityPlayer && TF2Attribute.getModifier("Crit Mini", stack, 0, shooter) != 0))
			initial = 2;
		if (target instanceof EntityTF2Boss && initial == 1)
			initial = 0;
		if (target instanceof EntityMerasmus && ((EntityMerasmus) target).getActivePotionEffect(TF2weapons.stun) != null)
			initial = 2;
		return initial;
	}

	public static float calculateDamage(Entity target, World world, EntityLivingBase living, ItemStack stack, int critical, float distance) {
		ItemWeapon weapon = (ItemWeapon) stack.getItem();
		float calculateddamage = weapon.getWeaponDamage(stack, living, target);

		if (calculateddamage == 0)
			return 0f;
		if (target instanceof EntityBuilding || target==living)
			return calculateddamage;
		if (critical == 2)
			calculateddamage *= 3;
		else if (critical == 1)
			calculateddamage *= 1.35f;
		if (target == dummyEnt)
			distance *= 0.5f;
		float falloff=weapon.getWeaponDamageFalloff(stack);
		if (!(target instanceof EntityTF2Boss) &&  falloff> 0 && (critical < 2 || target == living))
			if (distance <= falloff)
				// calculateddamage *=weapon.maxDamage - ((distance /
				// (float)weapon.damageFalloff) *
				// (weapon.maxDamage-weapon.damage));
				calculateddamage *= lerp(weapon.getWeaponMaxDamage(stack, living), 1f, (distance / falloff));
			else if (critical == 0)
				// calculateddamage
				// *=Math.max(weapon.getWeaponMinDamage(stack,living)/weapon.getWeaponDamage(stack,living),
				// ((weapon.getWeaponDamage(stack,living)) -
				// (((distance-weapon.getWeaponDamageFalloff(stack)) /
				// ((float)weapon.getWeaponDamageFalloff(stack)*2)) *
				// (weapon.getWeaponDamage(stack,living)-weapon.getWeaponMinDamage(stack,living))))/weapon.getWeaponDamage(stack,living));
				calculateddamage *= lerp(1f, weapon.getWeaponMinDamage(stack, living), 
						Math.min(1,(distance-falloff)/(TF2Attribute.getModifier("Accuracy", stack, falloff*2f, living)-falloff)));
						/*(Math.min(distance / weapon.getWeaponDamageFalloff(stack), TF2Attribute.getModifier("Accuracy", stack, 2, living)) - 1)
								/ (TF2Attribute.getModifier("Accuracy", stack, 2, living) - 1));*/
		// calculateddamage *= 1 -
		// (1-weapon.getWeaponMinDamage(stack,living))*(Math.min(distance/weapon.getWeaponDamageFalloff(stack),2*TF2Attribute.getModifier("Accuracy",
		// stack, 1,living))-1*TF2Attribute.getModifier("Accuracy", stack,
		// 1,living));
		// System.out.println((distance-weapon.getWeaponDamageFalloff(stack))-(weapon.getWeaponDamageFalloff(stack)*2));
		if (target instanceof EntityEnderman && !(stack.getItem() instanceof ItemMeleeWeapon))
			calculateddamage *= 0.4f;
		
		/*
		 * if (living instanceof IRangedWeaponAttackMob)
		 * calculateddamage*=((IRangedWeaponAttackMob)living).
		 * getAttributeModifier("Damage");
		 */
		return calculateddamage;
	}

	public static float lerp(float v0, float v1, float t) {
		return (1 - t) * v0 + t * v1;
	}

	public static boolean isOnSameTeam(Entity entity1, Entity entity2) {
		return (getTeam(entity1) == getTeam(entity2) && getTeam(entity1) != null) || (entity2 instanceof EntityBuilding && ((EntityBuilding) entity2).getOwner() == entity1)
				|| (entity1 instanceof EntityBuilding && ((EntityBuilding) entity1).getOwner() == entity2) || entity1 == entity2;

	}

	public static Team getTeam(Entity living) {
		if (living == null)
			return null;
		else if (!(living instanceof IThrowableEntity))
			return living.getTeam();
		else
			return getTeam(((IThrowableEntity) living).getThrower());
	}

	public static int getTeamForDisplay(Entity living) {
		if (living instanceof EntityTF2Character)
			return ((EntityTF2Character) living).getEntTeam();
		else if (living instanceof EntityBuilding)
			return ((EntityBuilding) living).getEntTeam();
		else if (living instanceof EntityPlayer)
			return ((EntityPlayer) living).getTeam() == living.world.getScoreboard().getTeam("BLU") ? 1 : 0;
		else if (living instanceof IThrowableEntity)
			return getTeamForDisplay(((IThrowableEntity) living).getThrower());
		return 0;
	}

	public static boolean canHit(EntityLivingBase shooter, Entity ent) {
		// System.out.println("allowed: "+isOnSameTeam(shooter,ent)+"
		// "+!(shooter.getTeam()!=null&&shooter.getTeam().getAllowFriendlyFire())+"
		// "+(ent!=shooter)+" "+!(shooter instanceof
		// EntityBuilding&&((EntityBuilding)shooter).getOwner()==ent));
		return ent.isEntityAlive() && !(ent instanceof EntityLivingBase && isOnSameTeam(shooter, ent) && !(shooter.getTeam() != null && shooter.getTeam().getAllowFriendlyFire())
				&& (ent != shooter) && !(shooter instanceof EntityBuilding && ((EntityBuilding) shooter).getOwner() == ent));
	}

	public static boolean lookingAt(EntityLivingBase entity, double max, double targetX, double targetY, double targetZ) {
		/*double d0 = targetX - entity.posX;
		double d1 = targetY - (entity.posY + entity.getEyeHeight());
		double d2 = targetZ - entity.posZ;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		float f = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
		float f1 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
		float compareyaw = Math.abs(180 - Math.abs(Math.abs(f - MathHelper.wrapDegrees(entity.rotationYawHead)) - 180));
		float comparepitch = Math.abs(180 - Math.abs(Math.abs(f1 - entity.rotationPitch) - 180));
		// System.out.println("Angl: "+compareyaw+" "+comparepitch);
		return compareyaw < max && comparepitch < max;*/
		return isLyingInCone(new Vec3d(targetX, targetY, targetZ),entity.getPositionEyes(1),entity.getPositionEyes(1).add(entity.getLookVec()),(float) Math.toRadians(max));
	}
	
	public static boolean lookingAtFast(EntityLivingBase entity, double max, double targetX, double targetY, double targetZ) {
		double d0 = targetX - entity.posX;
		double d1 = targetY - (entity.posY + entity.getEyeHeight());
		double d2 = targetZ - entity.posZ;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		float f = (float) (MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
		float f1 = (float) (-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));
		float compareyaw = Math.abs(180 - Math.abs(Math.abs(f - MathHelper.wrapDegrees(entity.rotationYawHead)) - 180));
		float comparepitch = Math.abs(180 - Math.abs(Math.abs(f1 - entity.rotationPitch) - 180));
		// System.out.println("Angl: "+compareyaw+" "+comparepitch);
		return compareyaw < max && comparepitch < max/2D;
	}
	/**
	 * @param x coordinates of point to be tested 
	 * @param t coordinates of apex point of cone
	 * @param b coordinates of center of basement circle
	 * @param aperture in radians
	 */
	static public boolean isLyingInCone(Vec3d x, Vec3d start, Vec3d end, 
	                                    float aperture){

	    // This is for our convenience
	    float halfAperture = aperture/2.f;

	    // Vector pointing to X point from apex
	    Vec3d apexToXVect = start.subtract(x);

	    // Vector pointing from apex to circle-center point.
	    Vec3d axisVect = start.subtract(end);

	    // X is lying in cone only if it's lying in 
	    // infinite version of its cone -- that is, 
	    // not limited by "round basement".
	    // We'll use dotProd() to 
	    // determine angle between apexToXVect and axis.
	    boolean isInInfiniteCone = apexToXVect.dotProduct(axisVect)
	                               /apexToXVect.lengthVector()/axisVect.lengthVector()
	                                 >
	                               // We can safely compare cos() of angles 
	                               // between vectors instead of bare angles.
	                               MathHelper.cos(halfAperture);


	    return isInInfiniteCone;

	    // X is contained in cone only if projection of apexToXVect to axis
	    // is shorter than axis. 
	    // We'll use dotProd() to figure projection length.
	   /* boolean isUnderRoundCap = dotProd(apexToXVect,axisVect)
	                              /magn(axisVect)
	                                <
	                              magn(axisVect);
	    return isUnderRoundCap;*/
	}
	public static boolean dealDamage(Entity entity, World world, EntityLivingBase living, ItemStack stack, int critical, float damage, DamageSource source) {
		if (world.isRemote || damage == 0)
			return false;
		double lvelocityX = entity.motionX;
		double lvelocityY = entity.motionY;
		double lvelocityZ = entity.motionZ;
		entity.hurtResistantTime = 0;

		
		
		damage *=damageMultiplier;

		if (entity == living && source instanceof TF2DamageSource && living instanceof EntityPlayer && living.getTeam() != null) {
			((TF2DamageSource) source).setAttackSelf();
			dummyEnt.world = world;
		}
		
		if (!stack.isEmpty() && stack.getItem() instanceof ItemWeapon && ItemFromData.getData(stack).hasProperty(PropertyType.HIT_SOUND))
			TF2weapons.playSound(entity, ItemFromData.getSound(stack, PropertyType.HIT_SOUND), ItemFromData.getData(stack).getName().equals("fryingpan") ? 2F : 0.7F, 1F);
		//System.out.println("dealt: " + damage);
		boolean knockback = canHit(living, entity);
		if (knockback && isUsingShield(entity, source)) {
			((EntityLivingBase)entity).getActiveItemStack().damageItem((int) (damage*(source.isExplosion()?4f:2f)), (EntityLivingBase) entity);
			damage*=0.45f;
		}
		float prehealth=entity instanceof EntityLivingBase?((EntityLivingBase)entity).getHealth():0f;
		if (knockback && entity.attackEntityFrom(source, damage)) {
			//System.out.println("realD");
			if (source instanceof TF2DamageSource && !((TF2DamageSource)source).getWeaponOrig().isEmpty())
				stack=((TF2DamageSource)source).getWeaponOrig();
			if (!stack.isEmpty() && stack.getItem() instanceof ItemWeapon)
				((ItemWeapon) stack.getItem()).onDealDamage(stack, living, entity, source, entity instanceof EntityLivingBase?prehealth-((EntityLivingBase) entity).getHealth():0f);
			if (living instanceof EntityPlayer) {
				if (!ItemUsable.lastDamage.containsKey(living))
					ItemUsable.lastDamage.put(living, new float[20]);
				ItemUsable.lastDamage.get(living)[0] += damage;
			}
			if (entity instanceof EntityLivingBase) {

				EntityLivingBase livingTarget = (EntityLivingBase) entity;
				
				// System.out.println(livingTarget.getHealth());
				// System.out.println("Scaled"+source.isDifficultyScaled()+"
				// "+damage);
				livingTarget.hurtResistantTime = 20;
				if (critical == 2)
					TF2weapons.playSound(entity, TF2Sounds.MISC_CRIT, 1.5F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
				else if (critical == 1)
					TF2weapons.playSound(entity, TF2Sounds.MISC_MINI_CRIT, 1.5F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
				if (!(entity instanceof EntityBuilding))
					TF2weapons.playSound(entity, TF2Sounds.MISC_PAIN, 1F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
				/*
				 * if (living instanceof EntityPlayer && !world.isRemote) {
				 * EntityPlayer player = (EntityPlayer) living; String
				 * string=""; for(int i=0; i<20;i++){
				 * string+=ItemUsable.lastDamage.get(living)[i]+" "; }
				 * 
				 * //player.addChatMessage(string); //
				 * player.addChatMessage("Health: " + livingTarget.getHealth() +
				 * "/" + livingTarget.getMaxHealth() + " Armor: " +
				 * livingTarget.getTotalArmorValue()*4+
				 * "% Critical: "+critical+" Distance: "+distance); }
				 */
				livingTarget.isAirBorne = false;
				livingTarget.motionX = lvelocityX;
				livingTarget.motionY = lvelocityY;
				livingTarget.motionZ = lvelocityZ;
				livingTarget.velocityChanged = false;
			}
		}
		return knockback;
	}

	public static float damageBlock(BlockPos pos, EntityLivingBase living, World world, ItemStack stack, int critical, float damage, Vec3d forwardVec, Explosion explosion) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block.isAir(state, world, pos) || TF2weapons.destTerrain == 0 || state.getBlockHardness(world, pos) < 0 ||
				(!(living instanceof EntityPlayer) && !world.getGameRules().getBoolean("mobGriefing")) || (living instanceof EntityPlayer && !world.isBlockModifiable((EntityPlayer) living, pos)))
			return 0;

		DestroyBlockEntry finalEntry = null;
		int entryId = 0;
		int emptyId = -1;
		for (int i = 0; i < TF2EventsCommon.destroyProgress.size(); i++) {
			DestroyBlockEntry entry = TF2EventsCommon.destroyProgress.get(i);
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
				TF2EventsCommon.destroyProgress.set(emptyId, finalEntry);
				entryId = emptyId;
			} else {
				TF2EventsCommon.destroyProgress.add(finalEntry);
				entryId = TF2EventsCommon.destroyProgress.size() - 1;
			}

		}

		/*if (block instanceof BlockChest) {
			((TileEntityChest) world.getTileEntity(pos)).setLootTable(LootTableList.CHESTS_NETHER_BRIDGE, living.getRNG().nextLong());
		}*/
		float hardness = getHardness(state, world, pos);

		if (!stack.isEmpty() && stack.getItem() instanceof ItemSniperRifle && hardness > 100)
			damage *= 3;
		finalEntry.curDamage += damage;

		if (living != null)
			world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + entryId), pos, (int) ((finalEntry.curDamage / hardness) * 10));

		if (finalEntry.curDamage >= hardness) {
			if (living != null && living instanceof EntityPlayer)
				block.harvestBlock(world, (EntityPlayer) living, pos, state, null, stack);
			else {
				block.dropBlockAsItem(world, pos, state, 0);
				block.onBlockExploded(world, pos, explosion);
			}
			TF2EventsCommon.destroyProgress.remove(finalEntry);

			boolean flag = (living == null || !(living instanceof EntityPlayer) && world.isAirBlock(pos)) || block.removedByPlayer(state, world, pos, (EntityPlayer) living, true);

			if (flag) {
				if (living != null) {
					world.playEvent(2001, pos, Block.getStateId(state));
					world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + entryId), pos, -1);
				}
				block.onBlockDestroyedByPlayer(world, pos, state);

				if (forwardVec != null) {
					RayTraceResult trace = world.rayTraceBlocks(living.getPositionVector().addVector(0, living.getEyeHeight(), 0), forwardVec, false, true, false);
					if (trace != null)
						damageBlock(trace.getBlockPos(), living, world, stack, critical, finalEntry.curDamage - hardness, forwardVec, explosion);
				}
			}
			return finalEntry.curDamage - hardness;
		}
		return 0;
	}

	public static float getHardness(IBlockState state, World world, BlockPos pos) {
		return state.getBlockHardness(world, pos) * (!state.getMaterial().isToolNotRequired() && !(state.getBlock() instanceof BlockStone) ? 12f : 5.5f);
	}

	public static int getMetal(EntityLivingBase entity) {
		if (entity instanceof EntityEngineer)
			return ((EntityEngineer) entity).metal;

		return entity.getHeldItem(EnumHand.MAIN_HAND) != null && entity.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemWrench
				? 200 - entity.getHeldItem(EnumHand.MAIN_HAND).getItemDamage() : 0;
	}

	public static void setMetal(EntityLivingBase entity, int amount) {
		if (entity instanceof EntityEngineer)
			((EntityEngineer) entity).metal = amount;
		else if (entity.getHeldItem(EnumHand.MAIN_HAND) != null && entity.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemWrench)
			entity.getHeldItem(EnumHand.MAIN_HAND).setItemDamage(200 - amount);
	}

	public static class NullStorage<T> implements IStorage<T> {

		@Override
		public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
			// TODO Auto-generated method stub

		}

	}

	public static int getExperiencePoints(EntityPlayer player) {
		int playerLevel = player.experienceLevel;
		player.experienceLevel = 0;
		int totalExp = 0;
		for (int i = 0; i < playerLevel; i++) {
			player.experienceLevel = i;
			totalExp += player.xpBarCap();
		}
		player.experienceLevel = playerLevel;
		totalExp += Math.round(player.experience * player.xpBarCap());
		return totalExp;
	}

	public static void setExperiencePoints(EntityPlayer player, int amount) {
		player.experienceLevel = 0;
		player.experience = 0;
		player.addExperience(amount);
	}

	public static void stun(EntityLivingBase living, int duration, boolean noMovement) {
		if (!(living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.isCreativeMode)) {
			living.addPotionEffect(new PotionEffect(stun, duration, noMovement ? 1 : 0));
			living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 0));
		}
	}

	public static void playSound(Entity entity, SoundEvent event, float volume, float pitch) {
		entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, event, entity.getSoundCategory(), volume, pitch);
	}

	public static boolean isEnemy(EntityLivingBase living, EntityLivingBase living2) {
		return (living2 instanceof IMob || living2 instanceof EntityPlayer
				|| (living2 instanceof EntityLiving && !(living2 instanceof EntityBuilding) && ((EntityLiving) living2).getAttackTarget() == living))
				&& !isOnSameTeam(living, living2);
	}
	
	public static void igniteAndAchievement(Entity target, EntityLivingBase living, int sec) {

		if (living instanceof EntityPlayerMP) {
			if (target.hasCapability(TF2weapons.WEAPONS_CAP, null) && target.getDataManager().get(TF2EventsCommon.ENTITY_EXP_JUMP)) {
				((EntityPlayer) living).addStat(TF2Achievements.PILOT_LIGHT);
			}
			if (ItemFromData.isSameType(living.getHeldItemMainhand(), "flaregun") && !target.isBurning()) {
				((EntityPlayer) living).addStat(TF2Achievements.FLAREGUN_IGNITED);
				if (((EntityPlayerMP) living).getStatFile().readStat(TF2Achievements.FLAREGUN_IGNITED) >= 100)
					((EntityPlayer) living).addStat(TF2Achievements.ATTENTION_GETTER);
			}
		}
		target.setFire(sec);
	}

	public static void explosion(World world, EntityLivingBase shooter, ItemStack weapon, Entity exploder, Entity direct, double x, double y, double z, float size,
			float damageMult, int critical, float distance) {
		float blockDmg = TF2Attribute.getModifier("Destroy Block", weapon, 0, shooter)
				* TF2weapons.calculateDamage(TF2weapons.dummyEnt, world, shooter, weapon, critical, distance);

		TF2Explosion explosion = new TF2Explosion(world, exploder, x, y, z, size, direct, blockDmg,1);

		// System.out.println("ticks: "+this.ticksExisted);
		explosion.isFlaming = false;
		explosion.isSmoking = true;
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		Iterator<Entity> affectedIterator = explosion.affectedEntities.keySet().iterator();
		int killedInRow = 0;
		while (affectedIterator.hasNext()) {
			Entity ent = affectedIterator.next();
			distance = (float) TF2weapons.getDistanceBox(shooter, ent.posX, ent.posY, ent.posZ, ent.width+0.1, ent.height+0.1);
			critical = TF2weapons.calculateCritPost(ent, shooter, critical, weapon);
			float dmg = TF2weapons.calculateDamage(ent, world, shooter, weapon, critical, distance) * damageMult;
			
			Vec3d vec=explosion.getKnockbackMap().get(ent);
			if(vec != null) {
				boolean expJump=ent == shooter && explosion.affectedEntities.size() == 1;
				vec=vec.scale(dmg/(expJump?6f:9f) * 
						(ent instanceof EntityLivingBase && !expJump? 1-((EntityLivingBase)ent).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue():1));	
				if(ent.motionY!=0)
					ent.fallDistance=(float) Math.max(0f, ent.fallDistance*((ent.motionY+vec.yCoord)/ent.motionY));
				if(vec.yCoord>0) {
					if(expJump) {
						//ent.fallDistance -= vec.yCoord * 8 - 1;
					}
					else
						ent.fallDistance -= vec.yCoord * 3 - 1;
				}
				ent.addVelocity(vec.xCoord, vec.yCoord, vec.zCoord);
				explosion.getKnockbackMap().put(ent, vec);
				
			}
			if(ent==shooter)
				dmg= TF2Attribute.getModifier("Self Damage", weapon, dmg, shooter);
			TF2weapons.dealDamage(ent, world, shooter, weapon, critical, explosion.affectedEntities.get(ent) * dmg,
					TF2weapons.causeBulletDamage(weapon, shooter, critical, exploder).setExplosion());
			if (critical == 2 && !ent.isEntityAlive() && ent instanceof EntityLivingBase) {
				killedInRow++;
				if (killedInRow > 2 && exploder instanceof EntityRocket && shooter instanceof EntityPlayerMP && TF2weapons.isEnemy(shooter, (EntityLivingBase) ent)) {
					((EntityPlayerMP) shooter).addStat(TF2Achievements.CRIT_ROCKET_KILL);
				}
			}
			if (exploder instanceof EntityProjectileBase && ((EntityProjectileBase)exploder).sentry != null && ent instanceof EntityLivingBase) {
				EntitySentry sentry=((EntityProjectileBase)exploder).sentry;
				((EntityLivingBase) ent).setLastAttacker(sentry);
				((EntityLivingBase) ent).setRevengeTarget(sentry);
				if (!ent.isEntityAlive())
					sentry.setKills(sentry.getKills() + 1);
			}
			
		}
		Iterator<EntityPlayer> iterator = world.playerEntities.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = iterator.next();

			if (entityplayer.getDistanceSq(x, y, z) < 4096.0D)
				((EntityPlayerMP) entityplayer).connection
						.sendPacket(new SPacketExplosion(x, y, z, size, explosion.affectedBlockPositions, explosion.getKnockbackMap().get(entityplayer)));
		}
	}
	public static double getDistanceSqBox(Entity target, double x, double y,double z,double widthO,double heightO){
		double xdiff=target.posX-x;
		double ydiff=target.posY+target.height/2-y-heightO/2;
		double zdiff=target.posZ-z;
		double widthCom=(target.width+widthO)/2;
		double heightCom=(target.height+heightO)/2;
		if(Math.abs(xdiff)-widthCom<0)
			xdiff=0;
		else
			xdiff=xdiff>0?xdiff-widthCom:xdiff+widthCom;
			
		if(Math.abs(zdiff)-widthCom<0)
			zdiff=0;
		else
			zdiff=zdiff>0?zdiff-widthCom:zdiff+widthCom;
			
		if(Math.abs(ydiff)-heightCom<0)
			ydiff=0;
		else
			ydiff=ydiff>0?ydiff-target.height/2:ydiff+target.height/2;
		return xdiff*xdiff+zdiff*zdiff+ydiff*ydiff;
	}
	public static double getDistanceBox(Entity target, double x, double y,double z,double widthO,double heightO){
		return Math.sqrt(getDistanceSqBox(target,x,y,z,widthO,heightO));
	}
}
