package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import rafradek.TF2weapons.characters.EntityDemoman;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.characters.EntityHeavy;
import rafradek.TF2weapons.characters.EntityPyro;
import rafradek.TF2weapons.characters.EntityScout;
import rafradek.TF2weapons.characters.EntitySniper;
import rafradek.TF2weapons.characters.EntitySoldier;
import rafradek.TF2weapons.characters.EntitySpy;

public class TF2ConfigVars {

	public static Configuration conf;
	public static int destTerrain;
	public static boolean medigunLock;
	public static boolean swapAttackButton;
	public static boolean fastMetalProduction;
	public static boolean shootAttract;
	public static boolean disableSpawn;
	public static boolean disableBossSpawn;
	public static boolean disableInvasion;
	public static boolean disableLoot;
	public static int bossReappear;
	public static boolean disableContracts;
	public static boolean disableGeneration;
	public static boolean randomCrits;
	public static boolean overworldOnly;
	public static String spawnOres;
	public static String naturalCheck;
	public static String biomeCheck;
	public static int batchDamage;
	public static float damageMultiplier;
	public static float healthScale;
	public static boolean dynamicLights;
	public static boolean dynamicLightsProj;
	public static boolean deadRingerTrigger;
	public static float medicChance;
	public static int sentryTargets;
	public static float dispenserRepair;
	public static boolean dispenserPlayers;
	public static boolean teleporterPlayers;
	public static boolean teleporterEntities;
	public static boolean enableUdp;
	public static boolean targetSentries;
	public static float dropAmmo;
	public static float speedMult;
	public static float armorMult;
	public static float mercenaryVolume;
	public static float bossVolume;
	public static float gunVolume;
	public static boolean enchantedExplosion;
	public static float maxEnergy;
	public static boolean buildingsUseEnergy;
	public static int sentryUseEnergy;
	public static int dispenserUseEnergy;
	public static int teleporterUseEnergy;
	public static boolean fastItemCooldown;
	public static boolean freeUseItems;
	public static int maxMetalEngineer;
	public static boolean fastBuildEngineer;
	public static boolean longDurationBanner;
	public static boolean oldDispenser;
	public static boolean scaleAttributes;
	public static float worldScale;
	public static float hatMercenaryMult;
	
	
	public static Map<Class<? extends EntityLiving>, Integer> spawnRate;

	public static ArrayList<ResourceLocation> repairBlacklist;
	public static ArrayList<ResourceLocation> hostileBlacklist;

	public TF2ConfigVars() {
		// TODO Auto-generated constructor stub
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
		swapAttackButton = conf.getBoolean("Swap mouse buttons", "gameplay", false, "Swaps attack and use buttons on weapons");
		
		fastMetalProduction = conf.getBoolean("Fast metal production", "adaption", false, "Dispensers produce metal every 5 seconds");
		healthScale = conf.getInt("TF2 - Minecraft health translation", "adaption", 200,-10000,10000, "How much 10 minecraft hearts are worth in TF2 health");
		damageMultiplier = 200f/healthScale;
		
		fastItemCooldown = conf.getBoolean("Fast item cooldown", "adaption", true, "Sandman balls and cleavers recover faster than normal");
		freeUseItems = conf.getBoolean("Free use items", "adaption", false, "Throwable and consumable items are free to use");
		longDurationBanner = conf.getBoolean("Long duration banner", "adaption", true, "Banner buffs build longer and last longer");
		worldScale = conf.getFloat("World scale", "adaption", 0.9f, 0f, Float.MAX_VALUE, "How many meters long minecraft block is");
		boolean old = conf.hasKey("gameplay", "Fast metal production");
		if (old) {
			conf.moveProperty("gameplay", "TF2 - Minecraft health translation", "adaption");
			conf.moveProperty("gameplay", "Fast metal production", "adaption");
		}
		
		disableSpawn = conf.getBoolean("Disable mob spawning", "spawn rate", false, "Disable mod-specific mobs spawning");
		overworldOnly = conf.getBoolean("Spawn only in overworld", "spawn rate", false, "Disable spawning in custom dimensions");
		biomeCheck = conf.get("spawn rate", "Spawn in biomes", "Default","Default - biomes that spawn vanilla monsters").setValidValues(new String[] { "All", "Default", "Vanilla only" }).getString();
		disableBossSpawn = conf.getBoolean("Disable boss spawn", "spawn rate", false, "Disable random tf2 boss spawn");
		disableInvasion =conf.getBoolean("Disable invasion event", "spawn rate", false, "Disable invasion event");
		disableContracts =conf.getBoolean("Disable contracts", "gameplay", false, "Stop new contracts from appearing");
		disableGeneration = conf.getBoolean("Disable structures", "world gen", false, "Disable structures generation, such as Mann Co. building");
		disableLoot = conf.getBoolean("Disable chest loot", "world gen", false, "Disable chest loot generated by this mod");
		
		TF2weapons.weaponVersion = TF2weapons.conf.getInt("Weapon Config Version", "internal", TF2weapons.getCurrentWeaponVersion(), 0, 1000, "");
		conf.get("gameplay", "Disable structures", false).setRequiresMcRestart(true);
		
		old = conf.hasKey("gameplay", "Natural mob detection");
		if (old) {
			conf.moveProperty("gameplay", "Natural mob detection", "mercenary");
		}
		
		naturalCheck = conf.get("mercenary", "Natural mob detection", "Always").setValidValues(new String[] { "Always", "Fast", "Never" }).getString();
		shootAttract = conf.getBoolean("Shooting attracts mobs", "gameplay", true, "Gunfire made by players attracts mobs");
		randomCrits = conf.getBoolean("Random critical hits", "gameplay", true, "Enables randomly appearing critical hits that deal 3x more damage");
		
		deadRingerTrigger = conf.getBoolean("Feign death events", "gameplay", true, "Does feign death trigger death events, set to false in case of mod conflicts");
		dynamicLights = conf.getBoolean("Dynamic Lights", "modcompatibility", true, "Enables custom light sources for AtomicStryker's Dynamic Lights mod")
				&& Loader.isModLoaded("dynamiclights");
		dynamicLightsProj = conf.getBoolean("Dynamic Lights - Projectiles", "modcompatibility", true, "Should projectiles emit light");
		bossReappear = conf.getInt("Boss respawn cooldown", "gameplay", 360000, 0, Integer.MAX_VALUE, "Maximum boss reappear time in ticks. Bosses always spawn in full moon");
		
		
		String[] hostileblacklist = conf.getStringList("Hostile entity blacklist", "gameplay", new String[] {
				"minecraft:enderman", "minecraft:zombie_pigman"
		}, "Entity IDs that should not be considered hostile");
		
		enableUdp = conf.getBoolean("Enable UDP (experimental)", "gameplay", false, "");
		batchDamage = getIndexSelected(conf.get("gameplay", "Attack invulnerability period", "Ignore", "Ignore, respect, or batch damage dealt during mob invulnerability",
				new String[] {"Ignore", "Batch", "Respect"}), 0);
		
		bossVolume = conf.getFloat("Boss volume (radius)", "sound volume", 4f, 0, 10, "Values above 1 increase sound radius only");
		mercenaryVolume = conf.getFloat("Mercenary volume (radius)", "sound volume", 0.6f, 0, 10, "Values above 1 increase sound radius only");
		gunVolume = conf.getFloat("Gun volume (radius)", "sound volume", 2f, 0, 10, "Applies only to players, values above 1 increase sound radius only");
		
		enchantedExplosion = conf.getBoolean("Enchanted blast jumping", "gameplay", true, "Strafing, no air resistance and reduced gravity when blast jumping");
		dropAmmo = conf.getFloat("Ammo drop chance", "gameplay", 0.15f, 0f, 1f, "Chance of dropping ammo from non-TF2 hostile creature");
		
		speedMult = conf.getFloat("Mercenary speed multiplier", "mercenary", 0.8f, 0f, 2f, "Speed multiplier of mercenaries. Does not apply to owned mercenaries");
		armorMult = conf.getFloat("Armored mercenary chance", "mercenary", 0.06f, 0f, 10f, "Base chance of armored mercenaries. Altered by difficulty level");
		hatMercenaryMult = conf.getFloat("Hatted mercenary chance", "mercenary", 1f, 0f, 10f, "Base chance of hatted mercenaries. Altered by difficulty level");
		maxMetalEngineer = conf.getInt("Engineer max metal", "mercenary", 500,0, Integer.MAX_VALUE, "Starting metal for engineers");
		fastBuildEngineer = conf.getBoolean("Engineer fast build", "mercenary", true, "Should engineers build at extra speed");
		scaleAttributes = conf.getBoolean("Reduce vs player damage", "mercenary", true, "Reduce weapon effectiveness versus players");
		old = conf.hasKey("gameplay", "Buildings use energy");
		if (old) {
			conf.moveProperty("gameplay", "Dispenser repair rate", "building");
			conf.moveProperty("gameplay", "Repair blacklist", "building");
			conf.moveProperty("gameplay", "Buildings use energy", "building");
			conf.moveProperty("gameplay", "Sentry energy use", "building");
			conf.moveProperty("gameplay", "Dispenser energy use", "building");
			conf.moveProperty("gameplay", "Teleport energy use", "building");
			conf.moveProperty("gameplay", "Mobs target sentries", "building");
		}
		oldDispenser = conf.getBoolean("Old dispenser behavior", "building", false, "When enabled, no material is being used to repair");
		buildingsUseEnergy = conf.getBoolean("Buildings use energy", "building", false, "");
		sentryUseEnergy = conf.getInt("Sentry energy use", "building", 100, 0, 40000, "Energy use on attack");
		dispenserUseEnergy = conf.getInt("Dispenser energy use", "building", 15, 0, 40000, "Energy use on repairs and heals");
		teleporterUseEnergy = conf.getInt("Teleport energy use", "building", 20000, 0, 40000, "Energy use on teleport");
		dispenserRepair = conf.getFloat("Dispenser repair rate", "building", 3, 0, 10000, "Repair per 1 metal. Reduced by enchants");
		String[] blacklist = conf.getStringList("Repair blacklist", "building", new String[0], "Item IDs that should not be repairable by dispensers");
		targetSentries = conf.getBoolean("Mobs target sentries", "building", true, "Mobs will attack sentries that dont shoot");
		
		if(!buildingsUseEnergy) {
			sentryUseEnergy = 0;
			dispenserUseEnergy = 0;
			teleporterUseEnergy = 0;
		}
		
		conf.getBoolean("Attack on hurt", "default building targets", true, "");
		conf.getBoolean("Attack other players", "default building targets", false, "");
		conf.getBoolean("Attack hostile mobs", "default building targets", true, "");
		conf.getBoolean("Attack friendly creatures", "default building targets", false, "");
		conf.getBoolean("Dispensers heal neutral players", "default building targets", true, "");
		conf.getBoolean("Neutral players can teleport", "default building targets", true, "");
		conf.getBoolean("Entities can teleport", "default building targets", true, "");
		
		spawnRate = new HashMap<>();
		spawnRate.put(EntityScout.class, conf.getInt("Scout", "spawn rate", 12, 0, 1000, ""));
		spawnRate.put(EntityPyro.class, conf.getInt("Pyro", "spawn rate", 12, 0, 1000, ""));
		spawnRate.put(EntitySoldier.class, conf.getInt("Soldier", "spawn rate", 12, 0, 1000, ""));
		spawnRate.put(EntityHeavy.class, conf.getInt("Heavy", "spawn rate", 12, 0, 1000, ""));
		spawnRate.put(EntityDemoman.class, conf.getInt("Demoman", "spawn rate", 12, 0, 1000, ""));
		spawnRate.put(EntitySpy.class, conf.getInt("Spy", "spawn rate", 9, 0, 1000, ""));
		spawnRate.put(EntityEngineer.class, conf.getInt("Engineer", "spawn rate", 9, 0, 1000, ""));
		spawnRate.put(EntitySniper.class, conf.getInt("Sniper", "spawn rate", 9, 0, 1000, ""));
		TF2weapons.updateOreGenStatus();
	
		TF2weapons.updateMobSpawning();
		medicChance = conf.getFloat("Medic spawn chance", "spawn rate", 1, 0, 1000, "Medic spawn chance multiplier");
		
		repairBlacklist = new ArrayList<>();
		for(String id : blacklist) {
			repairBlacklist.add(new ResourceLocation(id));
		}
		hostileBlacklist = new ArrayList<>();
		for(String id : hostileblacklist) {
			hostileBlacklist.add(new ResourceLocation(id));
		}
		
		if (conf.hasChanged())
			conf.save();
	}

	public static int getIndexSelected(Property prop, int def) {
		if (prop.getValidValues() != null) {
			for (int i = 0; i < prop.getValidValues().length; i++) {
				if (prop.getString().equalsIgnoreCase(prop.getValidValues()[i]))
					return i;
			}
		}
		return def;
	}
}
