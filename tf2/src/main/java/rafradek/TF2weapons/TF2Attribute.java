package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.text.TextFormatting;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.projectiles.EntityProjectileSimple;
import rafradek.TF2weapons.weapons.ItemBulletWeapon;
import rafradek.TF2weapons.weapons.ItemChargingTarge;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemFlameThrower;
import rafradek.TF2weapons.weapons.ItemMedigun;
import rafradek.TF2weapons.weapons.ItemMinigun;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemWrench;
import rafradek.TF2weapons.weapons.ItemSniperRifle;
import rafradek.TF2weapons.weapons.ItemSoldierBackpack;
import rafradek.TF2weapons.weapons.ItemUsable;

public class TF2Attribute {

	public static TF2Attribute[] attributes = new TF2Attribute[128];

	public int id;
	public String name;
	public Type typeOfValue;
	public String effect;
	public float defaultValue;
	public State state;

	private Predicate<ItemStack> canApply;

	public int numLevels;

	public float perLevel;

	public int cost;

	private int weight;

	public static final Predicate<ItemStack> ITEM_WEAPON = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemWeapon;
		}

	};
	public static final Predicate<ItemStack> NOT_FLAMETHROWER = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemWeapon && !(input.getItem() instanceof ItemFlameThrower);
		}

	};
	public static final Predicate<ItemStack> FLAMETHROWER = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemFlameThrower;
		}

	};
	public static final Predicate<ItemStack> IGNITE = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemFlameThrower || getModifier("BurnOnHit", input, 0, null) > 0;
		}

	};
	public static final Predicate<ItemStack> WITH_CLIP = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemWeapon && ((ItemWeapon) input.getItem()).hasClip(input);
		}

	};
	public static final Predicate<ItemStack> WITH_SPREAD = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemWeapon
					&& (((ItemWeapon) input.getItem()).getWeaponSpreadBase(input, null) != 0
							|| ((ItemWeapon) input.getItem()).getWeaponMinDamage(input, null) != 1);
		}

	};
	public static final Predicate<ItemStack> WITH_AMMO = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemUsable
					&& ItemFromData.getData(input).getInt(PropertyType.AMMO_TYPE) != 0;
		}

	};
	public static final Predicate<ItemStack> ITEM_BULLET = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			if (input.getItem() instanceof ItemBulletWeapon && !ItemFromData.getData(input).hasProperty(PropertyType.PROJECTILE))
				return true;
			else {
				Class<?> clazz = MapList.projectileClasses
						.get(ItemFromData.getData(input).getString(PropertyType.PROJECTILE));
				return clazz != null && EntityProjectileSimple.class.isAssignableFrom(clazz);
			}
		}

	};
	public static final Predicate<ItemStack> ITEM_PROJECTILE = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemProjectileWeapon || ItemFromData.getData(input).hasProperty(PropertyType.PROJECTILE);
		}

	};
	public static final Predicate<ItemStack> ITEM_MINIGUN = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemMinigun;
		}

	};
	public static final Predicate<ItemStack> ITEM_SNIPER_RIFLE = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemSniperRifle;
		}

	};
	public static final Predicate<ItemStack> EXPLOSIVE = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return (input.getItem() instanceof ItemProjectileWeapon || ItemFromData.getData(input).hasProperty(PropertyType.PROJECTILE) )&& !(input.getItem() instanceof ItemFlameThrower
					|| (MapList.projectileClasses.get(ItemFromData.getData(input).getString(PropertyType.PROJECTILE)) != null 
					&& EntityProjectileSimple.class.isAssignableFrom(MapList.projectileClasses.get(ItemFromData.getData(input).getString(PropertyType.PROJECTILE)))));
		}

	};
	public static final Predicate<ItemStack> MEDIGUN = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemMedigun;
		}

	};
	public static final Predicate<ItemStack> BANNER = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemSoldierBackpack;
		}

	};
	public static final Predicate<ItemStack> SHIELD = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemChargingTarge;
		}

	};
	public static final Predicate<ItemStack> WATCH = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemCloak;
		}

	};
	public static final Predicate<ItemStack> WRENCH = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemWrench;
		}

	};
	/**
	 * 
	 * @param id
	 * @param name
	 * @param effect
	 * @param typeOfValue
	 * @param defaultValue
	 * @param state
	 *            1 dodatni, 0 neutralny, -1 ujemny
	 */

	public static enum Type {
		PERCENTAGE, INVERTED_PERCENTAGE, ADDITIVE;
	}

	public static enum State {
		POSITIVE, NEGATIVE, NEUTRAL, HIDDEN;
	}

	public TF2Attribute(int id, String name, String effect, Type typeOfValue, float defaultValue, State state,
			Predicate<ItemStack> canApply, float perLevel, int numLevels, int cost, int weight) {
		this.id = id;
		attributes[id] = this;
		MapList.nameToAttribute.put(name, this);
		this.name = name;
		this.effect = effect;
		this.typeOfValue = typeOfValue;
		this.defaultValue = defaultValue;
		this.state = state;
		this.canApply = canApply;
		this.numLevels = numLevels;
		this.perLevel = perLevel;
		this.cost = cost;
		this.weight = weight;
	}

	public static void initAttributes() {
		new TF2Attribute(0, "DamageBonus", "Damage", Type.PERCENTAGE, 1f, State.POSITIVE, ITEM_WEAPON, 0.20f, 5, 180,
				4);
		new TF2Attribute(1, "DamagePenalty", "Damage", Type.PERCENTAGE, 1f, State.NEGATIVE, ITEM_WEAPON, 0.15f, 2, -140,
				1);
		new TF2Attribute(2, "ClipSizeBonus", "Clip Size", Type.PERCENTAGE, 1f, State.POSITIVE, WITH_CLIP, 0.5f, 4, 150,
				3);
		new TF2Attribute(3, "ClipSizePenalty", "Clip Size", Type.PERCENTAGE, 1f, State.NEGATIVE, WITH_CLIP, 0.25f, 2,
				-8, 1);
		new TF2Attribute(4, "MinigunSpinBonus", "Minigun Spinup", Type.INVERTED_PERCENTAGE, 1f, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), -0.15f, 4, 80, 1);
		new TF2Attribute(5, "MinigunSpinPenalty", "Minigun Spinup", Type.PERCENTAGE, 1f, State.NEGATIVE, ITEM_MINIGUN,
				0.1f, 2, -200, 1);
		new TF2Attribute(6, "FireRateBonus", "Fire Rate", Type.INVERTED_PERCENTAGE, 1f, State.POSITIVE,
				NOT_FLAMETHROWER, -0.08f, 5, 80, 4);
		new TF2Attribute(7, "FireRatePenalty", "Fire Rate", Type.INVERTED_PERCENTAGE, 1f, State.NEGATIVE,
				NOT_FLAMETHROWER, 0.06f, 2, -100, 1);
		new TF2Attribute(8, "SpreadBonus", "Spread", Type.INVERTED_PERCENTAGE, 1f, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), -0.15f, 3, 120, 1);
		new TF2Attribute(9, "SpreadPenalty", "Spread", Type.PERCENTAGE, 1f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(10, "PelletBonus", "Pellet Count", Type.PERCENTAGE, 1f, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(11, "PelletPenalty", "Pellet Count", Type.PERCENTAGE, 1f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(12, "ReloadRateBonus", "Reload Time", Type.INVERTED_PERCENTAGE, 1f, State.POSITIVE, Predicates.and(WITH_CLIP, stack -> {
			return !ItemFromData.getData(stack).getBoolean(PropertyType.RELOADS_FULL_CLIP);
			}),
				-0.2f, 3, 100, 2);
		new TF2Attribute(13, "ReloadRatePenalty", "Reload Time", Type.PERCENTAGE, 1f, State.NEGATIVE, WITH_CLIP, 0.2f,
				3, -200, 1);
		new TF2Attribute(14, "KnockbackBonus", "Knockback", Type.PERCENTAGE, 1f, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(15, "KnockbackPenalty", "Knockback", Type.PERCENTAGE, 1f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(16, "ChargeBonus", "Charge", Type.PERCENTAGE, 1f, State.POSITIVE, ITEM_SNIPER_RIFLE, 0.25f, 4,
				100, 1);
		new TF2Attribute(17, "ChargePenalty", "Charge", Type.INVERTED_PERCENTAGE, 1f, State.NEGATIVE, ITEM_SNIPER_RIFLE,
				-0.15f, 2, -140, 1);
		new TF2Attribute(18, "SpreadAdd", "Spread", Type.ADDITIVE, 0f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(19, "ProjectileSpeedBonus", "Proj Speed", Type.PERCENTAGE, 1f, State.POSITIVE, ITEM_PROJECTILE,
				0.25f, 4, 80, 2);
		new TF2Attribute(20, "ProjectileSpeedPenalty", "Proj Speed", Type.PERCENTAGE, 1f, State.NEGATIVE,
				ITEM_PROJECTILE, 0.15f, 2, -150, 1);
		new TF2Attribute(21, "ExplosionRadiusBonus", "Explosion Radius", Type.PERCENTAGE, 1f, State.POSITIVE, EXPLOSIVE,
				0.2f, 4, 80, 1);
		new TF2Attribute(22, "ExplosionRadiusPenalty", "Explosion Radius", Type.PERCENTAGE, 1f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(23, "DestroyOnImpact", "Coll Remove", Type.ADDITIVE, 0f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(24, "AmmoEfficiencyBonus", "Ammo Eff", Type.INVERTED_PERCENTAGE, 1f, State.POSITIVE, WITH_AMMO,
				-0.2f, 3, 120, 2);
		new TF2Attribute(25, "AmmoEfficiencyPenalty", "Ammo Eff", Type.PERCENTAGE, 1f, State.NEGATIVE, WITH_AMMO, 0.15f,
				2, -150, 2);
		new TF2Attribute(26, "Penetration", "Penetration", Type.ADDITIVE, 0, State.POSITIVE, ITEM_BULLET, 1, 1, 200, 1);
		new TF2Attribute(27, "HealRateBonus", "Heal", Type.PERCENTAGE, 1f, State.POSITIVE, MEDIGUN, 0.25f, 4, 100, 2);
		new TF2Attribute(28, "HealRatePenalty", "Heal", Type.PERCENTAGE, 1f, State.NEGATIVE, MEDIGUN, -0.15f, 2, -150,
				1);
		new TF2Attribute(29, "OverHealBonus", "Overheal", Type.PERCENTAGE, 1f, State.POSITIVE, MEDIGUN, 0.25f, 4, 100,
				1);
		new TF2Attribute(30, "OverHealPenalty", "Overheal", Type.PERCENTAGE, 1f, State.NEGATIVE, MEDIGUN, -0.15f, 2,
				-150, 1);
		new TF2Attribute(31, "BurnTimeBonus", "Burn Time", Type.PERCENTAGE, 1f, State.POSITIVE, IGNITE, 0.5f, 4, 140,
				2);
		new TF2Attribute(32, "BurnTimePenalty", "Burn Time", Type.PERCENTAGE, 1f, State.NEGATIVE, IGNITE, -0.5f, 4, 1,
				2);
		new TF2Attribute(33, "HealthOnKill", "Health Kill", Type.ADDITIVE, 0, State.POSITIVE, ITEM_WEAPON, 2.0f, 4, 80,
				2);
		new TF2Attribute(34, "AccuracyBonus", "Accuracy", Type.PERCENTAGE, 1f, State.POSITIVE, WITH_SPREAD, 0.25f, 3,
				160, 2);
		new TF2Attribute(35, "BuffDurationBonus", "Buff Duration", Type.PERCENTAGE, 1f, State.POSITIVE, BANNER, 0.25f,
				4, 80, 1);
		new TF2Attribute(36, "FlameRangeBonus", "Flame Range", Type.PERCENTAGE, 1f, State.POSITIVE, FLAMETHROWER, 0.25f,
				4, 80, 1);
		new TF2Attribute(37, "CritBurning", "Crit Burn", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(38, "BurnOnHit", "Burn Hit", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(39, "DestroyBlock", "Destroy Block", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.or(ITEM_BULLET, EXPLOSIVE), 1f, 2, 200, 1);
		new TF2Attribute(40, "NoRandomCrit", "Random Crit", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 1f, 2, 180, 1);
		new TF2Attribute(41, "CritRocket", "Crit Rocket", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(42, "CritMini", "Crit Mini", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(43, "UberOnHit", "Uber Hit", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(44, "BallRelease", "Ball Release", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(45, "HealthPenalty", "Health", Type.ADDITIVE, 0f, State.HIDDEN,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(46, "MovementBonus", "Speed", Type.PERCENTAGE, 1, State.HIDDEN,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(47, "MarkForDeathSelf", "Mark Death", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(48, "CritOnKill", "Crit Kill", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(49, "FireResistBonus", "Fire Resist", Type.INVERTED_PERCENTAGE, 1, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(50, "DamageResistPenalty", "Damage Resist", Type.INVERTED_PERCENTAGE, 1, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(51, "ExplosionResistBonus", "Explosion Resist", Type.INVERTED_PERCENTAGE, 1, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(52, "DamageNonBurnPenalty", "Damage Non Burn", Type.PERCENTAGE, 1, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(53, "CollectHeads", "Kill Count", Type.ADDITIVE, 0, State.HIDDEN,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(54, "ExplodeDeath", "Explode Death", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(55, "RechargeBonus", "Charge Recharge", Type.PERCENTAGE, 1, State.POSITIVE,
				SHIELD, 0.75f, 3, 80, 1);
		new TF2Attribute(56, "UberRateBonus", "Uber Rate", Type.PERCENTAGE, 1f, State.POSITIVE, MEDIGUN, 0.25f, 4, 120,
				1);
		new TF2Attribute(57, "UberRatePenalty", "Uber Rate", Type.PERCENTAGE, 1f, State.NEGATIVE, MEDIGUN, -0.15f, 2,
				-150, 1);
		new TF2Attribute(58, "RangeIncrease", "Range", Type.PERCENTAGE, 1f, State.NEUTRAL, Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(59, "ExplosiveBullets", "Explode Bullet", Type.ADDITIVE, 0, State.POSITIVE, Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(60, "WeaponMode", "Weapon Mode", Type.ADDITIVE, 0, State.HIDDEN, Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(61, "HealthOnHit", "Health Hit", Type.ADDITIVE, 0, State.POSITIVE, Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(62, "Breakable", "Breakable", Type.ADDITIVE, 0, State.HIDDEN, Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(63, "Focus", "Focus", Type.ADDITIVE, 0, State.POSITIVE, ITEM_WEAPON, 1, 2, 200, 1);
		new TF2Attribute(64, "KnockbackFAN", "KnockbackFAN", Type.ADDITIVE, 0, State.POSITIVE,Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(65, "MinicritAirborne", "Minicrit Airborne", Type.ADDITIVE, 0, State.POSITIVE,Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(66, "CannotAirblast", "Cannot Airblast", Type.ADDITIVE, 0, State.NEGATIVE,Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(67, "RageCrit", "Rage Crit", Type.ADDITIVE, 0, State.POSITIVE,Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(68, "NoAmmo", "No Ammo", Type.ADDITIVE, 0, State.POSITIVE,Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(69, "DamageBurnBonus", "Damage Burning", Type.PERCENTAGE, 1, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(70, "RingFire", "Ring Fire", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(71, "AmmoDrainSpinned", "Ammo Spinned", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(72, "StickybombBonus", "Stickybomb Count", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(73, "StickyControl", "Weapon Mode", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(74, "ArmTimePenalty", "Arm Time", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(75, "RepairBuilding", "Repair Building", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(76, "PickBuilding", "Pick Building", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(77, "Headshot", "Headshot", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(78, "HealTarget", "Heal Target", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(79, "NeedScope", "Weapon Mode", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(80, "DamageBonusCharged", "Damage Charged", Type.PERCENTAGE, 1, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(81, "SelfDamageReduced", "Self Damage", Type.PERCENTAGE, 1, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(82, "StickybombPenalty", "Stickybomb Count", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(83, "SpeedOnHitAlly", "Speed Hit", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(84, "ChargedGrenades", "Charged Grenades", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(85, "MetalAsAmmo", "Metal Ammo", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(86, "MetalOnHit", "Metal Hit", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(87, "CloakDurationBonus", "Cloak Duration", Type.PERCENTAGE, 1, State.POSITIVE,
				WATCH, 0.25f, 3, 240, 1);
		new TF2Attribute(88, "CloakRegenBonus", "Cloak Regen", Type.PERCENTAGE, 1, State.POSITIVE,
				WATCH, 0.5f, 2, 160, 1);
		new TF2Attribute(89, "CloakDrainActivate", "Cloak Drain", Type.PERCENTAGE, 1, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(90, "NoExternalCloak", "No External Cloak", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(91, "CritStunned", "Crit Stun", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(92, "BleedingDuration", "Bleed", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(93, "MiniCritDistance", "Minicrit Distance", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(94, "MaxHealthOnKill", "Max Health Kill", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(95, "SpeedOnKill", "Speed Kill", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(96, "ClipOnKill", "Clip Kill", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(97, "AirborneBonus", "Airborne Bonus", Type.ADDITIVE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(98, "DestroyProjectiles", "Destroy Projectiles", Type.ADDITIVE, 0, State.POSITIVE,
				ITEM_BULLET, 1, 2, 180, 2);
		new TF2Attribute(99, "KnockbackRage", "Knockback Rage", Type.ADDITIVE, 0, State.POSITIVE,
				ITEM_MINIGUN, 1, 3, 120, 1);
		new TF2Attribute(100, "DeployTimeBonus", "Deploy Time", Type.PERCENTAGE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(101, "FireRateHealthBonus", "Fire Rate Health", Type.PERCENTAGE, 0, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(102, "SpreadHealthPenalty", "Spread Health", Type.PERCENTAGE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(103, "AutoFireClip", "Auto Fire", Type.ADDITIVE, 0, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(104, "HealthRegen", "Health Regen", Type.ADDITIVE, 0f, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(105, "Gravity", "Gravity", Type.ADDITIVE, 0f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(106, "FireRateHitBonus", "Fire Rate Hit", Type.PERCENTAGE, 1f, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(107, "ConstructionRateBonus", "Construction Rate", Type.PERCENTAGE, 1f, State.POSITIVE,
				WRENCH, 0.4f, 3, 160, 1);
		new TF2Attribute(108, "ConstructionRatePenalty", "Construction Rate", Type.PERCENTAGE, 1f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(109, "UpgradeRatePenalty", "Upgrade Rate", Type.PERCENTAGE, 1f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(110, "TeleportCost", "Teleporter Cost", Type.PERCENTAGE, 1f, State.POSITIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(111, "MetalUsedOnHitPenalty", "Metal Used", Type.PERCENTAGE, 1f, State.NEGATIVE,
				Predicates.<ItemStack>alwaysFalse(), 0, 0, 0, 1);
		new TF2Attribute(112, "Looting", "Looting", Type.ADDITIVE, 0, State.POSITIVE,
				ITEM_WEAPON, 1, 3, 220, 2);
		// new TF2Attribute(23, "He", "Coll Remove", "Additive", 0f, -1);
	}

	public static float addValue(float value, NBTTagCompound attributelist, String effect) {
		for(String name : attributelist.getKeySet()) {
			NBTBase tag = attributelist.getTag(name);
			if (tag instanceof NBTTagFloat) {
				TF2Attribute attribute = attributes[Integer.parseInt(name)];
				if (attribute != null && attribute.effect.equals(effect))
					if (attribute.typeOfValue == Type.ADDITIVE)
						value += ((NBTTagFloat) tag).getFloat();
					else
						value *= ((NBTTagFloat) tag).getFloat();
			}
		}
		return value;
	}
	public static float getModifier(String effect, ItemStack stack, float initial, EntityLivingBase entity) {
		//long nanoTimeStart=System.nanoTime();
		if(!stack.hasCapability(TF2weapons.WEAPONS_DATA_CAP, null))
			return initial;
		float value = stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).getAttributeValue(stack, effect, initial);
		/*if (!stack.isEmpty() && stack.getTagCompound() != null) {
			//NBTTagCompound attributeList = stack.getTagCompound().getCompoundTag("Attributes");
			value=addValue(value,stack.getTagCompound().getCompoundTag("Attributes"),effect);
			if(MapList.buildInAttributes.get(ItemFromData.getData(stack).getName()) != null)
				value=addValue(value,MapList.buildInAttributes.get(ItemFromData.getData(stack).getName()),effect);
		}*/
		if (entity != null && entity instanceof EntityTF2Character)
			value *= ((EntityTF2Character) entity).getAttributeModifier(effect);
		/*if(!Thread.currentThread().getName().equals("Client Thread"))
			TF2EventsCommon.tickTimeOther[TF2weapons.server.getTickCounter()%20]+=System.nanoTime()-nanoTimeStart;*/
		return value;
	}

	public String getTranslatedString(float value, boolean withColor) {
		String valueStr = String.valueOf(value);
		if (this.typeOfValue == Type.PERCENTAGE)
			valueStr = Integer.toString(Math.round((value - 1) * 100)) + "%";
		else if (this.typeOfValue == Type.INVERTED_PERCENTAGE)
			valueStr = Integer.toString(Math.round((1 - value) * 100)) + "%";
		else if (this.typeOfValue == Type.ADDITIVE)
			valueStr = Integer.toString(Math.round(value));

		if (withColor) {
			TextFormatting color = this.state == State.POSITIVE ? TextFormatting.AQUA
					: (this.state == State.NEGATIVE ? TextFormatting.RED : TextFormatting.WHITE);
			return color + I18n.format("weaponAttribute." + this.name, new Object[] { valueStr });
		} else
			return I18n.format("weaponAttribute." + this.name, new Object[] { valueStr });

	}

	public static List<TF2Attribute> getAllPassibleAttributesForUpgradeStation() {
		List<TF2Attribute> list = new ArrayList<>();
		for (TF2Attribute attr : attributes)
			if (attr != null && attr.canApply != Predicates.<ItemStack>alwaysFalse() && attr.state == State.POSITIVE)
				for (int i = 0; i < attr.weight; i++)
					list.add(attr);
		return list;
	}

	public static void setAttribute(ItemStack stack, TF2Attribute attr, float value) {
		if (!stack.isEmpty() && stack.hasTagCompound())
			stack.getTagCompound().getCompoundTag("Attributes").setFloat(String.valueOf(attr.id), value);
	}

	public static void upgradeItemStack(ItemStack stack, int value, Random rand) {
		List<TF2Attribute> list = new ArrayList<>();
		int lowestCost = Integer.MAX_VALUE;
		int maxCount = value / 30;
		for (TF2Attribute attr : attributes)
			if (attr != null && attr.canApply(stack) && attr.state == State.POSITIVE) {
				for (int i = 0; i < attr.weight; i++)
					list.add(attr);
				lowestCost = Math.min(lowestCost, attr.cost);
			}
		if (list.size() > 0) {
			int i = 0;
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("Attributes");
			while (i < maxCount && value >= lowestCost) {
				i++;
				TF2Attribute attr = list.get(rand.nextInt(list.size()));
				if (attr.cost <= value && attr.calculateCurrLevel(stack) <= attr.numLevels) {
					value -= attr.cost;

					String key = String.valueOf(attr.id);

					if (!tag.hasKey(key))
						tag.setFloat(key, attr.defaultValue);
					tag.setFloat(key, tag.getFloat(key) + attr.getPerLevel(stack));
				}
			}
		}
	}

	public float getPerLevel(ItemStack stack) {
		return this.perLevel * (stack.getItem() instanceof ItemMinigun && this.effect.equals("Damage") ? 0.5f : 1f);
	}
	
	public int calculateCurrLevel(ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		if (!stack.getTagCompound().getCompoundTag("Attributes").hasKey(String.valueOf(this.id)))
			return 0;
		float valueOfAttr = stack.getTagCompound().getCompoundTag("Attributes").getFloat(String.valueOf(this.id));
		float floatVal=(valueOfAttr - this.defaultValue) / this.getPerLevel(stack);
		return Math.round(floatVal-0.3f);
	}

	public int getUpgradeCost(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemFromData))
			return this.cost;
		int baseCost = this.cost;
		if (ItemFromData.getData(stack).getInt(PropertyType.COST) <= 12)
			baseCost /= 2;
		if (this.effect.equals("Accuracy") && ItemFromData.getData(stack).getFloat(PropertyType.SPREAD) <= 0.025f)
			baseCost /= 2;
		if (stack.getMaxStackSize() > 1)
			baseCost = (baseCost/3) * stack.getCount();
		//int additionalCost = 0;
		int lastUpgradesCost = stack.getTagCompound().getInteger("TotalCost");
		/*if (lastUpgradesCost > 0) {
			additionalCost = lastUpgradesCost / 10;
			baseCost += baseCost / 10;
		}
		baseCost += additionalCost;*/
		baseCost*=1+((float)lastUpgradesCost/800f);
		return Math.min(1400, baseCost);
	}

	public boolean canApply(ItemStack stack) {
		return !stack.isEmpty() && this.canApply.apply(stack);
	}
}
