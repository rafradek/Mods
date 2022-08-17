package rafradek.TF2weapons.common;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import rafradek.TF2weapons.entity.projectile.EntityBall;
import rafradek.TF2weapons.entity.projectile.EntityCleaver;
import rafradek.TF2weapons.entity.projectile.EntityFlame;
import rafradek.TF2weapons.entity.projectile.EntityFlare;
import rafradek.TF2weapons.entity.projectile.EntityFuryFireball;
import rafradek.TF2weapons.entity.projectile.EntityGrenade;
import rafradek.TF2weapons.entity.projectile.EntityJar;
import rafradek.TF2weapons.entity.projectile.EntityOnyx;
import rafradek.TF2weapons.entity.projectile.EntityProjectileBase;
import rafradek.TF2weapons.entity.projectile.EntityProjectileEnergy;
import rafradek.TF2weapons.entity.projectile.EntityProjectileSimple;
import rafradek.TF2weapons.entity.projectile.EntityRocket;
import rafradek.TF2weapons.entity.projectile.EntityStickProjectile;
import rafradek.TF2weapons.item.ItemAirblast;
import rafradek.TF2weapons.item.ItemBackpack;
import rafradek.TF2weapons.item.ItemBonk;
import rafradek.TF2weapons.item.ItemBulletWeapon;
import rafradek.TF2weapons.item.ItemChargingTarge;
import rafradek.TF2weapons.item.ItemCleaver;
import rafradek.TF2weapons.item.ItemCloak;
import rafradek.TF2weapons.item.ItemCrate;
import rafradek.TF2weapons.item.ItemFlameThrower;
import rafradek.TF2weapons.item.ItemHuntsman;
import rafradek.TF2weapons.item.ItemJar;
import rafradek.TF2weapons.item.ItemJetpack;
import rafradek.TF2weapons.item.ItemJetpackTrigger;
import rafradek.TF2weapons.item.ItemKnife;
import rafradek.TF2weapons.item.ItemMedigun;
import rafradek.TF2weapons.item.ItemMeleeWeapon;
import rafradek.TF2weapons.item.ItemMinigun;
import rafradek.TF2weapons.item.ItemPDA;
import rafradek.TF2weapons.item.ItemParachute;
import rafradek.TF2weapons.item.ItemProjectileWeapon;
import rafradek.TF2weapons.item.ItemSapper;
import rafradek.TF2weapons.item.ItemSniperRifle;
import rafradek.TF2weapons.item.ItemSoldierBackpack;
import rafradek.TF2weapons.item.ItemStickyLauncher;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWearable;
import rafradek.TF2weapons.item.ItemWrangler;
import rafradek.TF2weapons.item.ItemWrench;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.WeaponData;

public class MapList {

	//public static Map<Class<? extends EntityTF2Character>, Integer> classNumbers;
	public static Map<String, Item> weaponClasses;
	public static Map<String, PropertyType<?>> propertyTypes;
	public static Map<String, Class<? extends EntityProjectileBase>> projectileClasses;
	public static Map<String, WeaponData> nameToData;
	public static Map<String, TF2Attribute> nameToAttribute;
	public static Map<String, ItemUsable> specialWeapons;
	public static Map<String, NBTTagCompound> buildInAttributes;

	// public static Map<MinigunLoopSound, EntityLivingBase > fireCritSounds;
	// public static Map<List<SpawnListEntry>, SpawnListEntry> scoutSpawn;

	public static void initMaps() {
		weaponClasses = new HashMap<>();
		projectileClasses = new HashMap<>();
		nameToData = new HashMap<>();
		propertyTypes = new HashMap<>();
		nameToAttribute = new HashMap<>();
		buildInAttributes = new HashMap<>();
		specialWeapons = new HashMap<>();
		/*WeaponData.propertyDeserializers = new HashMap<String, JsonDeserializer<ICapabilityProvider>>();
		WeaponData.propertyDeserializers.put("Attributes", new ItemFromData.AttributeSerializer());
		WeaponData.propertyDeserializers.put("Content", new ItemCrate.CrateSerializer());*/
		//classNumbers = new HashMap<>();

		//classNumbers.put(EntityScout.class, 0);
		// scoutSpawn=new HashMap<List<SpawnListEntry>, SpawnListEntry>();
		weaponClasses.put("sniperrifle", new ItemSniperRifle());
		weaponClasses.put("bullet", new ItemBulletWeapon());
		weaponClasses.put("minigun", new ItemMinigun());
		weaponClasses.put("projectile", new ItemProjectileWeapon());
		weaponClasses.put("stickybomb", new ItemStickyLauncher());
		weaponClasses.put("flamethrower", new ItemFlameThrower());
		weaponClasses.put("knife", new ItemKnife());
		weaponClasses.put("medigun", new ItemMedigun());
		weaponClasses.put("cloak", new ItemCloak());
		weaponClasses.put("wrench", new ItemWrench());
		weaponClasses.put("bonk", new ItemBonk());
		weaponClasses.put("cosmetic", new ItemWearable());
		weaponClasses.put("melee", new ItemMeleeWeapon());
		weaponClasses.put("sapper", new ItemSapper());
		weaponClasses.put("backpack", new ItemSoldierBackpack());
		weaponClasses.put("crate", new ItemCrate());
		weaponClasses.put("jar", new ItemJar());
		weaponClasses.put("wrangler", new ItemWrangler());
		weaponClasses.put("shield", new ItemChargingTarge());
		weaponClasses.put("cleaver", new ItemCleaver());
		weaponClasses.put("parachute", new ItemParachute());
		weaponClasses.put("huntsman", new ItemHuntsman());
		weaponClasses.put("jetpack", new ItemJetpack());
		weaponClasses.put("jetpacktrigger", new ItemJetpackTrigger());
		weaponClasses.put("pda", new ItemPDA());
		weaponClasses.put("airblast", new ItemAirblast());
		weaponClasses.put("backpackgeneric", new ItemBackpack());
		/*
		 * weaponDatas.put("sniperrifle", ); weaponDatas.put("bullet", new
		 * ItemBulletWeapon()); weaponDatas.put("minigun", new ItemMinigun());
		 * weaponDatas.put("projectile", new Itdew ItemFlameThrower());
		 * weaponDatas.put("knife", new ItemKnife()); weaponDatas.put("medigun",
		 * new ItemMedigun()); weaponDatas.put("cloak", new ItemCloak());
		 * weaponDatas.put("wrench", new ItemWrench()); weaponDatas.put("bonk",
		 * new ItemBonk()); weaponDatas.put("cosmetic", new ItemWearable());
		 */

		projectileClasses.put("rocket", EntityRocket.class);
		projectileClasses.put("fire", EntityFlame.class);
		projectileClasses.put("fireball", EntityFuryFireball.class);
		projectileClasses.put("flare", EntityFlare.class);
		projectileClasses.put("grenade", EntityGrenade.class);
		projectileClasses.put("syringe", EntityStickProjectile.class);
		projectileClasses.put("jar", EntityJar.class);
		projectileClasses.put("ball", EntityBall.class);
		projectileClasses.put("repairclaw", EntityStickProjectile.class);
		projectileClasses.put("arrow", EntityStickProjectile.class);
		projectileClasses.put("cleaver", EntityCleaver.class);
		projectileClasses.put("hhhaxe", EntityProjectileSimple.class);
		projectileClasses.put("energy", EntityProjectileEnergy.class);
		projectileClasses.put("onyx", EntityOnyx.class);
	}
}
