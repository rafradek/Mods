package rafradek.TF2weapons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2EventsCommon.DestroyBlockEntry;
import rafradek.TF2weapons.TF2EventsCommon.InboundDamage;
import rafradek.TF2weapons.TF2EventsCommon.TF2WorldStorage;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.TF2EventsClient;
import rafradek.TF2weapons.client.audio.TF2Sounds;
import rafradek.TF2weapons.client.particle.EnumTF2Particles;
import rafradek.TF2weapons.common.MapList;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.EntityStatue;
import rafradek.TF2weapons.entity.IEntityTF2;
import rafradek.TF2weapons.entity.boss.EntityMerasmus;
import rafradek.TF2weapons.entity.boss.EntityMonoculus;
import rafradek.TF2weapons.entity.boss.EntityTF2Boss;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.building.EntityDispenser;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.projectile.EntityProjectileBase;
import rafradek.TF2weapons.item.ItemAmmo;
import rafradek.TF2weapons.item.ItemBackpack;
import rafradek.TF2weapons.item.ItemFireAmmo;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemMeleeWeapon;
import rafradek.TF2weapons.item.ItemSniperRifle;
import rafradek.TF2weapons.item.ItemToken;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.message.TF2Message;

public class TF2Util {

	protected static final UUID FOLLOW_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE978AD348");

	public static final Block[] NATURAL_BLOCKS = new Block[] {Blocks.STONE};
	public static final Predicate<Entity> TARGETABLE = target -> {
		return target.canBeCollidedWith() && (!(target instanceof EntityLivingBase) || (target instanceof EntityLivingBase && ((EntityLivingBase) target).deathTime <= 0))
				&& !(target instanceof EntityStatue);
	};

	public static int[] colorCode = new int[32];
	public static final float[] ASIN_VALUES = new float[512];

	public static List<RayTraceResult> pierce(World world, Entity living, double startX, double startY, double startZ, double endX, double endY, double endZ,
			boolean headshot, float size, boolean pierce, Predicate<Entity> selector) {
		ArrayList<RayTraceResult> targets = new ArrayList<>();
		Vec3d var17 = new Vec3d(startX, startY, startZ);
		Vec3d var3 = new Vec3d(endX, endY, endZ);
		boolean split = var3.subtract(var17).lengthSquared() > 16384D;
		Vec3d mid = new Vec3d((startX + endX) / 2D, (startY + endY) / 2D, (startZ + endZ) / 2D);
		RayTraceResult var4 = world.rayTraceBlocks(var17, var3, false, true, false);

		if (var4 != null/* || (var4 = world.rayTraceBlocks(mid, var3, false, true, false)) != null*/)
			var3 = new Vec3d(var4.hitVec.x, var4.hitVec.y, var4.hitVec.z);
		else
			var3 = new Vec3d(endX, endY, endZ);
		Entity var5 = null;
		Iterable<Entity> var6;
		if (var3.subtract(var17).lengthSquared() > 16384D)
			var6 = Iterables.concat(living.world.getEntitiesWithinAABBExcludingEntity(living,
					new AxisAlignedBB(startX, startY, startZ, mid.x, mid.y, mid.z).grow(2D, 2D, 2D)), living.world.getEntitiesWithinAABBExcludingEntity(living,
							new AxisAlignedBB(mid.x, mid.y, mid.z, endX, endY, endZ).grow(2D, 2D, 2D)));
		else
			var6 = living.world.getEntitiesWithinAABBExcludingEntity(living,
					new AxisAlignedBB(startX, startY, startZ, endX, endY, endZ).grow(2D, 2D, 2D));
		// System.out.println("shoot: "+startX+","+startY+","+startZ+", do:
		// "+endX+","+endY+","+endZ+" Count: "+var6.size());
		double var7 = 0.0D;
		RayTraceResult collideVec = new RayTraceResult((Entity)null,null);
		for (Entity target : var6)
			if (selector.apply(target)) {
				AxisAlignedBB oldBB = target.getEntityBoundingBox();
				if (world.isRemote && TF2EventsClient.moveEntities) {
					float ticktime = TF2EventsClient.tickTime;
					target.setEntityBoundingBox(target.getEntityBoundingBox().offset((target.prevPosX - target.posX) * (1 - ticktime),
							(target.prevPosY - target.posY) * (1 - ticktime), (target.prevPosZ - target.posZ) * (1 - ticktime)));
				}
				AxisAlignedBB var12 = target.getEntityBoundingBox().grow(size, (double) size, size);
				RayTraceResult var13 = var12.calculateIntercept(var17, var3);

				if (var13 == null && var12.contains(var3)) {
					var13 = new RayTraceResult(var3, EnumFacing.EAST);
				}

				if (var13 != null) {
					double var14 = var17.squareDistanceTo(var13.hitVec);

					if (!pierce && (var14 < var7 || var7 == 0.0D)) {
						var5 = target;
						var7 = var14;
						collideVec = var13;
					} else if (pierce)
						targets.add(TF2Util.getTraceResult(target, var13, size, headshot, var17, var3));
				}

				target.setEntityBoundingBox(oldBB);
			}
		// var4.hitInfo=false;
		if (!pierce && var5 != null && !(var5 instanceof EntityLivingBase && ((EntityLivingBase) var5).getHealth() <= 0))
			targets.add(TF2Util.getTraceResult(var5, collideVec, size, headshot, var17, var3));
		if ((pierce || targets.isEmpty()) && var4 != null && var4.typeOfHit == Type.BLOCK)
			targets.add(var4);
		else if (targets.isEmpty())
			targets.add(new RayTraceResult(Type.MISS, var3, null, null));
		return targets;
	}

	public static List<RayTraceResult> pierce(World world, Entity living, double startX, double startY, double startZ, double endX, double endY, double endZ,
			boolean headshot, float size, boolean pierce) {
		return pierce(world, living, startX, startY, startZ, endX, endY, endZ, headshot, size, pierce, TARGETABLE);
	}


	public static List<RayTraceResult> pierce(World world, Entity living, double length,
			boolean headshot, float size, boolean pierce) {
		Vec3d look= living.getLookVec().scale(length);
		return pierce(world, living, living.posX, living.posY+living.getEyeHeight(), living.posZ, living.posX+look.x,
				living.posY+living.getEyeHeight()+look.y, living.posZ+look.z, headshot, size, pierce, TARGETABLE);
	}

	public static RayTraceResult getTraceResult(Entity target, RayTraceResult hitVec, float size, boolean headshot, Vec3d start, Vec3d end) {
		RayTraceResult result = new RayTraceResult(target, hitVec.hitVec);

		if (headshot && target instanceof EntityLivingBase && !(target instanceof IEntityTF2 && !((IEntityTF2)target).hasHead())) {
			Boolean var13 = TF2Util.getHead((EntityLivingBase)target).calculateIntercept(start, end.add(end.subtract(start).normalize())) != null;
			result.hitInfo = var13;
		}
		result.sideHit=hitVec.sideHit;
		return result;
	}

	public static AxisAlignedBB getHead(EntityLivingBase target){
		double ymax = target.posY + target.getEyeHeight();
		boolean custom = target instanceof IEntityTF2;
		AxisAlignedBB head;
		if (!custom || (head = ((IEntityTF2)target).getHeadBox()) == null) {
			head = new AxisAlignedBB(target.posX - 0.25, ymax - 0.16, target.posZ - 0.25, target.posX + 0.25, ymax + 0.25, target.posZ + 0.25);

			if (target.width >= 0.63) {
				float offsetX = -MathHelper.sin(target.renderYawOffset * 0.017453292F) * Math.max(0f,target.width*0.6f-0.32f);
				float offsetZ = MathHelper.cos(target.renderYawOffset * 0.017453292F) * Math.max(0f,target.width*0.6f-0.32f);

				head = head.offset(offsetX, 0, offsetZ);
			}
		}
		return head;
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

	public static Vec3d radiusRandom2D(float radius, Random random, float yaw, float pitch, double dist) {
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
		double x = Math.max(a, b) * radius * MathHelper.cos((float) (2f * Math.PI * Math.min(a, b) / Math.max(a, b)));
		double y = Math.max(a, b) * radius * MathHelper.sin((float) (2f * Math.PI * Math.min(a, b) / Math.max(a, b)));
		double z = 1;

		float f = MathHelper.cos(yaw);
		float f1 = MathHelper.sin(yaw);
		float f2 = MathHelper.cos(pitch);
		float f3 = MathHelper.sin(pitch);

		double y1 = y * (double)f2 + z * (double)f3;
		double z1 = z * (double)f2 - y * (double)f3;
		double x1 = x * (double)f + z1 * (double)f1;
		double z2 = z1 * (double)f - x * (double)f1;
		return new Vec3d(x1 * dist, y1 * dist, z2 * dist);
	}
	public static boolean isUsingShield(Entity shielded, DamageSource source) {
		if (shielded instanceof EntityLivingBase && ((EntityLivingBase) shielded).isActiveItemStackBlocking()) {
			Vec3d location = source.getDamageLocation();
			if(location == null)
				location = source.getImmediateSource().getPositionVector();
			if(location != null) {
				Vec3d vec3d1 = shielded.getLook(1.0F);
				Vec3d vec3d2 = location.subtractReverse(shielded.getPositionVector()).normalize();
				vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);

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

		if (initial == 0 && !stack.isEmpty() && shooter != null) {
			float mindist=TF2Attribute.getModifier("Minicrit Distance", stack, 0, shooter);
			mindist*=mindist;
			if(mindist != 0f && target.getDistanceSq(shooter) >= mindist)
				initial = 1;
		}
		if (initial == 0 && (!stack.isEmpty() && target.isBurning() && TF2Attribute.getModifier("Crit Burn", stack, 0, shooter) == 2))
			initial = 1;
		else if (initial < 2 && (!stack.isEmpty() && target.isBurning() && TF2Attribute.getModifier("Crit Burn", stack, 0, shooter) == 1))
			initial = 2;

		if (initial < 2 && (!stack.isEmpty() && (target instanceof EntityLivingBase && ((EntityLivingBase) target).getActivePotionEffect(TF2weapons.stun) != null))
				&& TF2Attribute.getModifier("Crit Stun", stack, 0, shooter) != 0)
			initial = 2;

		if (initial < 2 && (!stack.isEmpty() && shooter != null && shooter instanceof EntityPlayer &&
				(shooter.getCapability(TF2weapons.WEAPONS_CAP, null).isExpJump() || shooter.isElytraFlying())
				&& TF2Attribute.getModifier("Crit Rocket", stack, 0, shooter) != 0))
			initial = 2;

		if (initial == 1 && (!stack.isEmpty() && shooter != null && shooter instanceof EntityPlayer && TF2Attribute.getModifier("Crit Mini", stack, 0, shooter) != 0))
			initial = 2;

		if (target instanceof EntityBuilding && initial == 1)
			initial = 0;

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
		if (target == living)
			return calculateddamage;
		if (critical == 2 && !(target instanceof IEntityTF2 && ((IEntityTF2)target).isBuilding()))
			calculateddamage *= 3;
		else if (critical == 1)
			calculateddamage *= 1.35f;
		if (target == TF2weapons.dummyEnt)
			distance *= 0.5f;
		float falloff=weapon.getWeaponDamageFalloff(stack);
		if (!(target instanceof IEntityTF2 && !((IEntityTF2)target).hasDamageFalloff()) &&  falloff> 0 && (critical < 2 || target == living))
			if (distance <= falloff)
				// calculateddamage *=weapon.maxDamage - ((distance /
				// (float)weapon.damageFalloff) *
				// (weapon.maxDamage-weapon.damage));
				calculateddamage *= TF2Util.lerp(weapon.getWeaponMaxDamage(stack, living), 1f, (distance / falloff));
			else if (critical == 0)
				// calculateddamage
				// *=Math.max(weapon.getWeaponMinDamage(stack,living)/weapon.getWeaponDamage(stack,living),
				// ((weapon.getWeaponDamage(stack,living)) -
				// (((distance-weapon.getWeaponDamageFalloff(stack)) /
				// ((float)weapon.getWeaponDamageFalloff(stack)*2)) *
				// (weapon.getWeaponDamage(stack,living)-weapon.getWeaponMinDamage(stack,living))))/weapon.getWeaponDamage(stack,living));
				calculateddamage *= TF2Util.lerp(1f, weapon.getWeaponMinDamage(stack, living),
						Math.min(1,(distance-falloff)/(TF2Attribute.getModifier("Accuracy", stack, falloff*2f, living)-falloff)));
		/*(Math.min(distance / weapon.getWeaponDamageFalloff(stack), TF2Attribute.getModifier("Accuracy", stack, 2, living)) - 1)
								/ (TF2Attribute.getModifier("Accuracy", stack, 2, living) - 1));*/
		// calculateddamage *= 1 -
		// (1-weapon.getWeaponMinDamage(stack,living))*(Math.min(distance/weapon.getWeaponDamageFalloff(stack),2*TF2Attribute.getModifier("Accuracy",
		// stack, 1,living))-1*TF2Attribute.getModifier("Accuracy", stack,
		// 1,living));
		// System.out.println((distance-weapon.getWeaponDamageFalloff(stack))-(weapon.getWeaponDamageFalloff(stack)*2));
		if (target instanceof EntityEnderman && !(stack.getItem() instanceof ItemMeleeWeapon || TF2Attribute.getModifier("Unblockable", stack, 0, living) != 0))
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

	public static float position(float v0, float v1, float lerp) {
		return  (lerp - v1) *  (1f/(v0-v1)) ;
	}

	public static boolean isOnSameTeam(Entity entity1, Entity entity2) {
		return (TF2Util.getTeam(entity1) == TF2Util.getTeam(entity2) && TF2Util.getTeam(entity1) != null)
				|| (entity1 instanceof IEntityOwnable && ((IEntityOwnable) entity1).getOwner() == entity2)
				|| (entity2 instanceof IEntityOwnable && ((IEntityOwnable) entity2).getOwner() == entity1) || entity1 == entity2;

	}

	public static Team getTeam(Entity living) {
		if (living == null)
			return null;
		else if (!(living instanceof IThrowableEntity))
			return living.getTeam();
		else
			return getTeam(((IThrowableEntity) living).getThrower());
	}

	public static int getTeamColor(Entity living) {
		/*if (living instanceof EntityTF2Character)
			return ((EntityTF2Character) living).getEntTeam();
		else if (living instanceof EntityBuilding)
			return ((EntityBuilding) living).getEntTeam();
		else if (living instanceof EntityPlayer)
			return ((EntityPlayer) living).getTeam() == living.world.getScoreboard().getTeam("BLU") ? 1 : 0;
		else if (living instanceof IThrowableEntity)
			return getTeamForDisplay(((IThrowableEntity) living).getThrower());*/
		return colorCode[living != null ? getTeamColorNumber(living) : 0];
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

	public static int getTeamColorNumber(Entity living) {
		if(living.isEntityAlive() && getTeam(living) != null && getTeam(living).getColor().getColorIndex() >= 0)
			return getTeam(living).getColor().getColorIndex();
		else
			return 7;
	}

	public static boolean canHit(EntityLivingBase shooter, Entity ent) {
		// System.out.println("allowed: "+isOnSameTeam(shooter,ent)+"
		// "+!(shooter.getTeam()!=null&&shooter.getTeam().getAllowFriendlyFire())+"
		// "+(ent!=shooter)+" "+!(shooter instanceof
		// EntityBuilding&&((EntityBuilding)shooter).getOwner()==ent));
		return ent.isEntityAlive() && !(ent instanceof EntityLivingBase && isOnSameTeam(shooter, ent) && !(shooter.getTeam() != null && shooter.getTeam().getAllowFriendlyFire())
				&& (ent != shooter) /*&& !(shooter instanceof IEntityOwnable && ((IEntityOwnable) shooter).getOwner() == ent)*/);
	}

	public static boolean lookingAt(EntityLivingBase entity, double max, double targetX, double targetY, double targetZ) {
		return TF2Util.isLyingInCone(new Vec3d(targetX, targetY, targetZ),entity.getPositionEyes(1),entity.getPositionEyes(1).add(entity.getLook(1)),(float) Math.toRadians(max));
	}

	public static boolean lookingAt(EntityLivingBase entity, double max, Entity target) {
		return TF2Util.isLyingInCone(target.getPositionEyes(1f),entity.getPositionEyes(1),entity.getPositionEyes(1).add(entity.getLook(1)),(float) Math.toRadians(max));
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

		boolean isInInfiniteCone = apexToXVect.dotProduct(axisVect)
				/apexToXVect.lengthVector()/axisVect.lengthVector() >
		MathHelper.cos(halfAperture);


		return isInInfiniteCone;
	}

	public static boolean dealDamage(Entity entity, World world, EntityLivingBase living, ItemStack stack, int critical, float damage, DamageSource source) {
		if (world.isRemote || damage == 0)
			return false;

		if (TF2ConfigVars.batchDamage == 0) {
			entity.hurtResistantTime = 0;
			if (entity instanceof MultiPartEntityPart && !(((MultiPartEntityPart)entity).parent instanceof EntityDragon))
				((Entity)(((MultiPartEntityPart)entity).parent)).hurtResistantTime = 0;
		}

		if(entity instanceof EntityPlayer && !(living instanceof EntityPlayer)) {
			if(world.getDifficulty() == EnumDifficulty.NORMAL) {
				damage *= 0.7f;
			}
			else if(world.getDifficulty() == EnumDifficulty.HARD) {
				damage *= 0.9f;
			}
			else
				damage *= 0.45f;
		}

		damage *=TF2ConfigVars.damageMultiplier;

		if (entity == living && source instanceof TF2DamageSource && living instanceof EntityPlayer && living.getTeam() != null) {
			((TF2DamageSource) source).setAttackSelf();
		}

		if (!stack.isEmpty() && stack.getItem() instanceof ItemWeapon && ItemFromData.getData(stack).hasProperty(PropertyType.HIT_SOUND))
			((ItemWeapon)stack.getItem()).playHitSound(stack, living, entity);
		boolean knockback = canHit(living, entity);
		if (knockback && isUsingShield(entity, source) && source.getDamageLocation() == null && TF2Attribute.getModifier("Unblockable", stack, 0, living) != 1) {
			((EntityLivingBase)entity).getActiveItemStack().damageItem((int) (damage*(source.isExplosion()?4f:2f)), (EntityLivingBase) entity);
			damage*=source.isExplosion() ? 0.3f : 0.45f;
		}


		boolean invPeriod = entity.hurtResistantTime > (entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).maxHurtResistantTime / 2f : 10f);

		if (TF2ConfigVars.batchDamage == 1 && world.getCapability(TF2weapons.WORLD_CAP, null).damage.containsKey(entity)) {
			damage += world.getCapability(TF2weapons.WORLD_CAP, null).damage.get(entity).damage;
			world.getCapability(TF2weapons.WORLD_CAP, null).damage.remove(entity);
		}

		if (knockback && !(invPeriod && TF2ConfigVars.batchDamage == 1)) {
			return dealDamageActual(entity, world, living, stack, critical, damage, source);
		}
		else if(knockback && invPeriod && TF2ConfigVars.batchDamage == 1) {
			InboundDamage inbound = world.getCapability(TF2weapons.WORLD_CAP, null).damage.get(entity);
			if (inbound != null) {
				damage += inbound.damage;
			}
			world.getCapability(TF2weapons.WORLD_CAP, null).damage.put(entity, new InboundDamage(source, damage, critical, living, stack));
			return true;
		}
		return false;
	}
	public static boolean dealDamageActual(Entity entity, World world, EntityLivingBase living, ItemStack stack, int critical, float damage, DamageSource source) {
		double lvelocityX = entity.motionX;
		double lvelocityY = entity.motionY;
		double lvelocityZ = entity.motionZ;
		float prehealth=entity instanceof EntityLivingBase?((EntityLivingBase)entity).getHealth()+((EntityLivingBase)entity).getAbsorptionAmount():0f;

		if (entity.attackEntityFrom(source, damage)) {
			if (source instanceof TF2DamageSource && !((TF2DamageSource)source).getWeaponOrig().isEmpty())
				stack=((TF2DamageSource)source).getWeaponOrig();
			if (!stack.isEmpty() && stack.getItem() instanceof ItemWeapon)
				((ItemWeapon) stack.getItem()).onDealDamage(stack, living, entity, source,
						entity instanceof EntityLivingBase?prehealth-(((EntityLivingBase) entity).getHealth()+((EntityLivingBase)entity).getAbsorptionAmount()):0f);
			if (living instanceof EntityPlayer) {
				if (!ItemUsable.lastDamage.containsKey(living))
					ItemUsable.lastDamage.put(living, new float[20]);
				ItemUsable.lastDamage.get(living)[0] += damage;
			}
			if (entity instanceof EntityLivingBase) {

				EntityLivingBase livingTarget = (EntityLivingBase) entity;

				try {
					if (living instanceof EntityTF2Character && ((EntityTF2Character) living).isSharing()) {
						ReflectionAccess.entityRecentlyHit.setInt(entity, 100);
						if(((EntityTF2Character) living).getOwner() != null) {
							ReflectionAccess.entityAttackingPlayer.set(entity, ((EntityTF2Character) living).getOwner());
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				livingTarget.hurtResistantTime = 20;
				if (TF2Attribute.getModifier("Silent Kill", stack, 0, living) == 0) {
					if (critical == 2)
						TF2Util.playSound(entity, TF2Sounds.MISC_CRIT, 1.5F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
					else if (critical == 1)
						TF2Util.playSound(entity, TF2Sounds.MISC_MINI_CRIT, 1.5F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
					if (!(entity instanceof EntityBuilding))
						TF2Util.playSound(entity, TF2Sounds.MISC_PAIN, 1F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
				}
				livingTarget.isAirBorne = false;
				livingTarget.motionX = lvelocityX;
				livingTarget.motionY = lvelocityY;
				livingTarget.motionZ = lvelocityZ;
				livingTarget.velocityChanged = false;
			}
			return true;
		}

		if (!stack.isEmpty() && stack.getItem() instanceof ItemWeapon) {
			((ItemWeapon) stack.getItem()).onHitFinal(stack, living, entity, source);
		}
		return false;
	}
	public static float damageBlock(BlockPos pos, EntityLivingBase living, World world, ItemStack stack, int critical, float damage, Vec3d forwardVec, Explosion explosion) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block.isAir(state, world, pos) || TF2ConfigVars.destTerrain == 0 || state.getBlockHardness(world, pos) < 0 ||
				(!(living instanceof EntityPlayer) && !world.getGameRules().getBoolean("mobGriefing")) || (living instanceof EntityPlayer && !world.isBlockModifiable((EntityPlayer) living, pos)))
			return 0;

		DestroyBlockEntry finalEntry = null;
		int entryId = 0;
		int emptyId = -1;
		TF2WorldStorage cap= world.getCapability(TF2weapons.WORLD_CAP, null);

		for (int i = 0; i < cap.destroyProgress.size(); i++) {
			DestroyBlockEntry entry = cap.destroyProgress.get(i);
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
				cap.destroyProgress.set(emptyId, finalEntry);
				entryId = emptyId;
			} else {
				cap.destroyProgress.add(finalEntry);
				entryId = cap.destroyProgress.size() - 1;
			}

		}

		/*if (block instanceof BlockChest) {
			((TileEntityChest) world.getTileEntity(pos)).setLootTable(LootTableList.CHESTS_NETHER_BRIDGE, living.getRNG().nextLong());
		}*/
		float hardness = TF2Util.getHardness(state, world, pos);

		if (!stack.isEmpty() && stack.getItem() instanceof ItemSniperRifle && hardness > 100)
			damage *= 3;
		finalEntry.curDamage += damage;

		if (living != null)
			world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + entryId), pos, (int) ((finalEntry.curDamage / hardness) * 10));

		if (finalEntry.curDamage >= hardness) {
			if (living != null && living instanceof EntityPlayer)
				block.harvestBlock(world, (EntityPlayer) living, pos, state, null, stack);
			else {
				block.dropBlockAsItem(world, pos, state, (int) TF2Attribute.getModifier("Looting", stack, 0, living));
				block.onBlockExploded(world, pos, explosion);
			}
			cap.destroyProgress.remove(finalEntry);

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

	public static boolean canInteract(EntityLivingBase entity) {
		return !(entity instanceof EntityPlayer && ((EntityPlayer)entity).isSpectator())
				&& entity.getActivePotionEffect(TF2weapons.stun) == null && (!entity.hasCapability(TF2weapons.WEAPONS_CAP, null)
						|| (WeaponsCapability.get(entity).invisTicks == 0 && !WeaponsCapability.get(entity).isFeign())) && entity.getActivePotionEffect(TF2weapons.bonk) == null;
	}

	public static void explosion(World world, EntityLivingBase shooter, ItemStack weapon, Entity exploder, Entity direct, double x, double y, double z, float size,
			float damageMult, int critical, float distance) {
		float blockDmg = TF2Attribute.getModifier("Destroy Block", weapon, 0, shooter)
				* calculateDamage(TF2weapons.dummyEnt, world, shooter, weapon, critical, distance);

		if(blockDmg > 0 && TF2Attribute.getModifier("Onyx Projectile", weapon, 0, shooter) != 0)
			blockDmg *= TF2Attribute.getModifier("Onyx Projectile", weapon, 0, shooter);
		SoundEvent sound = ItemFromData.getSound(weapon, PropertyType.EXPLOSION_SOUND);
		TF2Explosion explosion = new TF2Explosion(world, exploder, x, y, z, size, direct, blockDmg,1, sound);

		// System.out.println("ticks: "+this.ticksExisted);
		explosion.isFlaming = false;
		explosion.isSmoking = true;
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		int killedInRow = 0;

		for(Entry<Entity, Float> entry : explosion.affectedEntities.entrySet()) {
			Entity ent = entry.getKey();
			distance = (float) getDistanceBox(shooter, ent.posX, ent.posY, ent.posZ, ent.width+0.1, ent.height+0.1);
			int criticalloc = critical;
			if(exploder instanceof EntityProjectileBase && ((EntityProjectileBase)exploder).hitEntities.contains(ent)) {
				criticalloc = Math.max(criticalloc, 1);
				TF2Util.playSound(ent, TF2Sounds.DOUBLE_DONK, 3f, 1.0f);
				entry.setValue(1f);
			}
			criticalloc = calculateCritPost(ent, shooter, criticalloc, weapon);
			float dmg = calculateDamage(ent, world, shooter, weapon, criticalloc, distance) * damageMult;

			Vec3d vec=explosion.getKnockbackMap().get(ent);
			if(vec != null) {

				boolean expJump=ent == shooter;
				double scale = dmg;
				if (expJump) {
					String used =TF2Util.getWeaponUsedByClass(weapon);
					if (used!=null)
						scale *= ItemToken.EXPLOSION_VALUES[ItemToken.getClassID(used)];
				}
				else {
					if (ent instanceof EntityLivingBase)
						scale *= 1-((EntityLivingBase)ent).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
				}

				scale = Math.min(7.5,scale);
				if (expJump)
					scale = TF2Attribute.getModifier("Self Push Force", weapon, (float) scale, shooter);
				vec=vec.scale(scale * TF2ConfigVars.explosionKnockback);
				if(ent.motionY!=0)
					ent.fallDistance=(float) Math.max(0f, ent.fallDistance*((ent.motionY+vec.y)/ent.motionY));
				if(vec.y>0) {
					if(expJump) {
						//ent.fallDistance -= vec.y * 8 - 1;
					}
					else
						ent.fallDistance -= vec.y * 3 - 1;
				}
				ent.addVelocity(vec.x, vec.y, vec.z);
				//System.out.println("Explosion vec "+vec.lengthVector() * 20 + " "+ Math.sqrt(vec.x * vec.x + vec.z * vec.z) * 20 + " " + dmg * entry.getValue());
				explosion.getKnockbackMap().put(ent, vec);
				/*if (ent instanceof EntityPlayer)
					ent.addVelocity(vec.x*3, 0, vec.y*3);*/

			}
			if(ent == shooter) {
				dmg = TF2Attribute.getModifier("Self Damage", weapon, dmg, shooter);
				if (shooter.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == TF2weapons.itemGunboats)
					dmg *= 0.4f;
			}
			boolean fromSentry = exploder instanceof EntityProjectileBase && ((EntityProjectileBase)exploder).sentry != null && ent instanceof EntityLivingBase;

			DamageSource source;
			if (TF2Attribute.getModifier("Unblockable", weapon, 0, shooter) == 1)
				source = TF2Util.causeDirectDamage(weapon, shooter, criticalloc);
			else {
				source = TF2Util.causeBulletDamage(weapon, shooter, criticalloc, fromSentry ? ((EntityProjectileBase)exploder).sentry : exploder);
			}



			if (fromSentry) {
				if (((EntityProjectileBase)exploder).sentry.fromPDA)
					((TF2DamageSource)source).addAttackFlag(TF2DamageSource.SENTRY_PDA);
			}
			dealDamage(ent, world, shooter, weapon, criticalloc, entry.getValue() * dmg,
					source.setExplosion());
			/*if (critical == 2 && !ent.isEntityAlive() && ent instanceof EntityLivingBase) {
				killedInRow++;
				if (killedInRow > 2 && exploder instanceof EntityRocket && shooter instanceof EntityPlayerMP && TF2weapons.isEnemy(shooter, (EntityLivingBase) ent)) {
					((EntityPlayerMP) shooter).addStat(TF2Achievements.CRIT_ROCKET_KILL);
				}
			}*/
			if (fromSentry) {
				EntitySentry sentry=((EntityProjectileBase)exploder).sentry;
				((EntityLivingBase) ent).setLastAttackedEntity(sentry);
				((EntityLivingBase) ent).setRevengeTarget(sentry);

				if (!ent.isEntityAlive())
					sentry.scoreKill((EntityLivingBase)ent);
			}

		}
		Iterator<EntityPlayer> iterator = world.playerEntities.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = iterator.next();

			if (entityplayer.getDistanceSq(x, y, z) < 4096.0D && explosion.getKnockbackMap().containsKey(entityplayer))
				TF2weapons.network.sendTo(new TF2Message.VelocityAddMessage(explosion.getKnockbackMap().get(entityplayer), true), (EntityPlayerMP) entityplayer);
			/*((EntityPlayerMP) entityplayer).connection
						.sendPacket(new SPacketExplosion(x, y, z, size, explosion.affectedBlockPositions, explosion.getKnockbackMap().get(entityplayer)));*/
		}
	}

	public static int calculateCritPre(ItemStack stack, EntityLivingBase living) {
		int thisCritical = 0;
		WeaponsCapability cap = living.getCapability(TF2weapons.WEAPONS_CAP, null);
		if (living.getActivePotionEffect(TF2weapons.crit) != null || living.getActivePotionEffect(TF2weapons.buffbanner) != null)
			thisCritical = 1;
		if ( thisCritical == 0 && (cap.focusedShot(stack) || cap.focusShotRemaining>0))
			thisCritical = 1;
		if (thisCritical == 0 && !living.onGround && TF2Attribute.getModifier("Minicrit Airborne Self", stack, 0, living) == 1f)
			thisCritical = 1;
		if (TF2ConfigVars.randomCrits && !stack.isEmpty() && stack.getItem() instanceof ItemWeapon) {
			ItemWeapon item = (ItemWeapon) stack.getItem();
			if ((!item.rapidFireCrits(stack) && item.hasRandomCrits(stack, living) && living.getRNG().nextFloat() <= item.critChance(stack, living))
					|| cap.getCritTime() > 0)
				thisCritical = 2;
		}
		if (living.getActivePotionEffect(TF2weapons.critBoost) != null)
			thisCritical = 2;
		if (cap.hitNoMiss > 0 && cap.hitNoMiss + 1 >= TF2Attribute.getModifier("Hit Crit", stack, 0, living)) {
			thisCritical = 2;
		}
		if (!stack.isEmpty() && (!(stack.getItem() instanceof ItemWeapon) || stack.getItem() instanceof ItemMeleeWeapon)
				&& (living.getActivePotionEffect(TF2weapons.charging) != null || cap.ticksBash > 0))
			if (thisCritical < 2 && (cap.ticksBash > 0 && cap.bashCritical)
					|| (living.getActivePotionEffect(TF2weapons.charging) != null && living.getActivePotionEffect(TF2weapons.charging).getDuration() < 14))
				thisCritical = 2;
			else if (thisCritical == 0 && (cap.ticksBash > 0 || living.getActivePotionEffect(TF2weapons.charging).getDuration() < 35))
				thisCritical = 1;
		return thisCritical;
	}

	public static DamageSource causeBulletDamage(ItemStack weapon, Entity shooter, int critical, Entity projectile) {
		return (new DamageSourceProjectile(weapon, projectile, shooter, critical)).setProjectile();
	}

	public static DamageSource causeDirectDamage(ItemStack weapon, Entity shooter, int critical) {
		return (new DamageSourceDirect(weapon, shooter, critical));
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

	public static Vec3d rangeRandom3D(float radius, Random random) {
		float yaw = random.nextFloat() * 2f * (float)Math.PI;
		float y = random.nextFloat() * 2f - 1f;
		float x = MathHelper.cos(yaw) * MathHelper.cos(asin(y));
		float z = MathHelper.sin(yaw) * MathHelper.cos(asin(y));

		return new Vec3d(x * radius,y * radius,z * radius);
	}

	public static float asin(float value) {
		if (value >= 0)
			return ASIN_VALUES[(int)(value*511f)];
		else
			return -ASIN_VALUES[(int)(-value*511f)];
	}

	public static void stun(EntityLivingBase living, int duration, boolean noMovement) {
		if (!(living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.isCreativeMode)) {
			living.addPotionEffect(new PotionEffect(TF2weapons.stun, duration, noMovement ? 1 : 0));
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

	public static void igniteAndAchievement(Entity target, EntityLivingBase living, int sec, float upgrade) {

		/*if (living instanceof EntityPlayerMP) {
			if (target.hasCapability(TF2weapons.WEAPONS_CAP, null) && target.getDataManager().get(TF2EventsCommon.ENTITY_EXP_JUMP)) {
				((EntityPlayer) living).addStat(TF2Achievements.PILOT_LIGHT);
			}
			if (ItemFromData.isSameType(living.getHeldItemMainhand(), "flaregun") && !target.isBurning()) {
				((EntityPlayer) living).addStat(TF2Achievements.FLAREGUN_IGNITED);
				if (((EntityPlayerMP) living).getStatFile().readStat(TF2Achievements.FLAREGUN_IGNITED) >= 100)
					((EntityPlayer) living).addStat(TF2Achievements.ATTENTION_GETTER);
			}
		}*/

		try {
			int fire = ReflectionAccess.entityFire.getInt(target);
			if (target instanceof EntityLivingBase) {
				upgrade *= TF2Attribute.getModifier("Afterburn Reduction", ItemBackpack.getBackpack((EntityLivingBase) target), 1, living);
				fire *= 40f/EnchantmentProtection.getFireTimeForEntity((EntityLivingBase)target, 40);
			}

			if (fire <= 0)
				target.setFire(1 + MathHelper.ceil(sec * upgrade));

			else if (fire < 20 * MathHelper.ceil(8 * upgrade) + 20) {

				target.setFire(Math.min(MathHelper.floor(fire/20f) + Math.round(sec * upgrade),MathHelper.ceil(8 * upgrade)));
				ReflectionAccess.entityFire.setInt(target, ReflectionAccess.entityFire.getInt(target)+ fire % 20);

			}
			//	ReflectionAccess.entityFire.setInt(target, Math.min(fire + MathHelper.ceil(sec * upgrade) * 20, 20 * MathHelper.ceil(8 * upgrade) + fire % 20));
			//fire = ReflectionAccess.entityFire.getInt(target);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	public static void sendParticle(EnumTF2Particles type, Entity tracking, double x, double y, double z, double offsetX, double offsetY, double offsetZ, int count, int... params){
		sendTracking(new TF2Message.ParticleSpawnMessage(type.ordinal(), x, y, z, offsetX, offsetY, offsetZ, count, params), tracking);
	}

	public static void sendParticle(EnumTF2Particles type, World world, double x, double y, double z, double offsetX, double offsetY, double offsetZ, int count, int... params){
		TF2weapons.network.sendToAllAround(new TF2Message.ParticleSpawnMessage(type.ordinal(), x, y, z, offsetX, offsetY, offsetZ, count, params),
				new TargetPoint(world.provider.getDimension(), x, y, z, type.getRange()));
	}

	public static void sendTracking(IMessage message,Entity entity){
		if(entity instanceof EntityPlayerMP)
			TF2weapons.network.sendTo(message, (EntityPlayerMP) entity);
		for(EntityPlayer player:((WorldServer)entity.world).getEntityTracker().getTrackingPlayers(entity)){
			TF2weapons.network.sendTo(message, (EntityPlayerMP) player);
		}
	}

	public static void sendTrackingExcluding(IMessage message,Entity entity){
		for(EntityPlayer player:((WorldServer)entity.world).getEntityTracker().getTrackingPlayers(entity)){
			TF2weapons.network.sendTo(message, (EntityPlayerMP) player);
		}
	}

	/*public static TargetPoint pointFromEntity(Entity entity) {
		return new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 256);
	}*/

	public static float getDamageBeforeAbsorb(float damage, float totalArmor, float toughnessAttribute)
	{
		float f = 2.0F + toughnessAttribute / 4.0F;
		float f1 = MathHelper.clamp(totalArmor - damage / f, totalArmor * 0.2F, 20.0F);
		return damage * (1.0F - f1 / 25.0F);
	}

	public static boolean isOre(String ore, ItemStack stack) {
		for(ItemStack stackin : OreDictionary.getOres(ore)) {
			if(stackin.isItemEqual(stack))
				return true;
		}
		return false;
	}

	public static ItemStack getFirstItem(IInventory inventory, Predicate<ItemStack> pred) {

		for (int i = 0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack=inventory.getStackInSlot(i);
			if (!stack.isEmpty() && pred.apply(stack))
				return stack;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getFirstItem(IItemHandler inventory, Predicate<ItemStack> pred) {

		for (int i = 0; i<inventory.getSlots(); i++) {
			ItemStack stack=inventory.getStackInSlot(i);
			if (!stack.isEmpty() && pred.apply(stack))
				return stack;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getBestItem(IInventory inventory, Comparator<ItemStack> comp, Predicate<ItemStack> pred) {
		ItemStack greater = ItemStack.EMPTY;
		for (int i = 0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack=inventory.getStackInSlot(i);
			if ((pred == null || pred.apply(stack)) && comp.compare(stack, greater) > 0)
				greater = stack;
		}
		return greater;
	}

	public static ItemStack getBestItem(IItemHandler inventory, Comparator<ItemStack> comp, Predicate<ItemStack> pred) {
		ItemStack greater = ItemStack.EMPTY;
		for (int i = 0; i<inventory.getSlots(); i++) {
			ItemStack stack=inventory.getStackInSlot(i);
			if ((pred == null || pred.apply(stack)) && comp.compare(stack, greater) > 0)
				greater = stack;
		}
		return greater;
	}

	public static ItemStack mergeStackByDamage(IItemHandler inventory, ItemStack stack) {
		if (stack.isEmpty() || stack.getCount() > 1)
			return stack;
		ItemStack existingAmmo;
		int amount = 0;
		while (amount < ((ItemAmmo)stack.getItem()).getAmount(stack) && !(existingAmmo = TF2Util.getFirstItem(inventory, stackL -> stackL.getCount() == 1 && stackL.getItem() == stack.getItem()
				&& ((ItemFireAmmo)stackL.getItem()).getAmount(stackL) != ((ItemFireAmmo)stackL.getItem()).uses)).isEmpty()) {
			amount += ((ItemFireAmmo)existingAmmo.getItem()).restoreAmmo(existingAmmo, ((ItemAmmo)stack.getItem()).getAmount(stack));
			if (((ItemFireAmmo)existingAmmo.getItem()).getAmount(existingAmmo) == ((ItemFireAmmo)existingAmmo.getItem()).uses) {
				ItemStack copy = existingAmmo.copy();
				existingAmmo.setCount(0);
				ItemHandlerHelper.insertItemStacked(inventory, copy, false);
			}
		}
		((ItemAmmo)stack.getItem()).consumeAmmo(null, stack, amount);
		return stack;
	}
	public static boolean hasEnoughItem(IInventory inventory, Predicate<ItemStack> pred, int amount) {
		int count = 0;
		if (amount <= 0)
			return true;
		for (int i = 0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack=inventory.getStackInSlot(i);
			if (!stack.isEmpty() && pred.apply(stack)) {
				count += stack.getCount();
				if (count >= amount)
					return true;
			}

		}
		return count >= amount;
	}
	public static boolean hasEnoughItem(IItemHandler inventory, Predicate<ItemStack> pred, int amount) {
		int count = 0;
		if (amount <= 0)
			return true;
		for (int i = 0; i<inventory.getSlots(); i++) {
			ItemStack stack=inventory.getStackInSlot(i);
			if (!stack.isEmpty() && pred.apply(stack)) {
				count += stack.getCount();
				if (count >= amount)
					return true;
			}

		}
		return count >= amount;
	}
	public static int removeItemsMatching(IItemHandler inventory, int amount, Predicate<ItemStack> match) {
		int left = amount;
		for (int i = 0; i < inventory.getSlots(); i++) {
			if (match.apply(inventory.getStackInSlot(i))) {
				left = left - inventory.extractItem(i, left, false).getCount();
				if (left <= 0)
					return 0;
			}
		}
		return left;
	}

	public static void attractMobs(EntityLivingBase living, World world) {
		if (!world.isRemote && TF2ConfigVars.shootAttract && world.getDifficulty().getDifficultyId()>1) {

			int range=world.getDifficulty()==EnumDifficulty.HARD?60:38;
			for (EntityCreature mob : world.getEntitiesWithinAABB(EntityCreature.class,
					living.getEntityBoundingBox().grow(range, range, range), new Predicate<EntityCreature>() {

				@Override
				public boolean apply(EntityCreature input) {
					return input.getAttackTarget() == null && input.isNonBoss() && TF2Util.isHostile(input);
				}

			})) {
				mob.getLookHelper().setLookPositionWithEntity(mob, 60, 30);
				if (!TF2Util.isOnSameTeam(living, mob)) {
					if (mob.getEntitySenses().canSee(living) || mob.getDistanceSq(living)<150){
						mob.setAttackTarget(living);
						if(mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getModifier(FOLLOW_MODIFIER)==null)
							mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE)
							.applyModifier(new AttributeModifier(FOLLOW_MODIFIER, "Follow Check", 65-mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(), 0));
						//mob.getNavigator().tryMoveToEntityLiving(living, 1.1f);

						mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(FOLLOW_MODIFIER);
					}

					// CoroUtilPath.tryMoveToEntityLivingLongDist((EntityCreature)mob,
					// living, 1.1D);
					;
				}

			}
		}
	}

	public static void addModifierSafe(EntityLivingBase living, IAttribute attribute, AttributeModifier modifier, boolean saved) {
		living.getEntityAttribute(attribute).removeModifier(modifier.getID());
		living.getEntityAttribute(attribute).applyModifier(modifier);
		living.getEntityAttribute(attribute).getAttributeValue();
		if(!saved)
			modifier.setSaved(false);
	}

	public static boolean isHostile(EntityLivingBase living) {
		return living instanceof IMob && !TF2ConfigVars.hostileBlacklist.contains(EntityList.getKey(living));
	}

	public static Vec3d getRotationVector(float pitch, float yaw)
	{
		float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
	}
	public static double getYaw(double x, double z) {
		return MathHelper.atan2(x, z) * 180.0D / Math.PI;
	}

	public static boolean isBaseSame(NBTTagCompound base, NBTTagCompound second) {
		if (base != null)
			for(String key : base.getKeySet()) {
				if(!base.getTag(key).equals(second.getTag(key)))
					return false;
			}
		return true;
	}

	public static Vec3d getMovementVector(EntityLivingBase living) {
		Vec3d moveDir = new Vec3d(living.moveForward , living.moveStrafing, 0).normalize();
		float cos = MathHelper.cos(living.rotationYaw * 0.017453292F - (float)Math.PI);
		float sin = MathHelper.sin(living.rotationYaw * 0.017453292F - (float)Math.PI);
		return new Vec3d(-moveDir.y * cos + moveDir.x * sin, -moveDir.x * cos - moveDir.y * sin, 0);
	}

	public static boolean teleportSafe(EntityLiving toTeleport, Entity dest) {
		int i = MathHelper.floor(dest.posX) - 2;
		int j = MathHelper.floor(dest.posZ) - 2;
		int k = MathHelper.floor(dest.getEntityBoundingBox().minY);

		for (int l = 0; l <= 4; ++l)
		{
			for (int i1 = 0; i1 <= 4; ++i1)
			{
				if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && isTeleportFriendlyBlock(toTeleport, i, j, k, l, i1))
				{
					toTeleport.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), toTeleport.rotationYaw, toTeleport.rotationPitch);
					toTeleport.getNavigator().clearPath();
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isTeleportFriendlyBlock(EntityLivingBase owner, int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_)
	{
		BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
		IBlockState iblockstate = owner.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(owner.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID &&
				iblockstate.canEntitySpawn(owner) && owner.world.isAirBlock(blockpos.up()) && owner.world.isAirBlock(blockpos.up(2));
	}


	public static float getGravity(EntityLivingBase living) {
		if (living.isInWater() || living.hasNoGravity() || living.isElytraFlying() || living instanceof EntityWither || living instanceof EntityMonoculus || living instanceof EntityGhast)
			return 0;
		else
			return 0.08f;
	}

	public static void addAndSendEffect(EntityLivingBase living, PotionEffect effect) {
		living.addPotionEffect(effect);
		if (!living.world.isRemote)
			for (EntityPlayer player : ((WorldServer)living.world).getEntityTracker().getTrackingPlayers(living)) {
				((EntityPlayerMP)player).connection.sendPacket(new SPacketEntityEffect(living.getEntityId(), effect));
			}
	}

	public static Entity findAmmoSource(EntityLivingBase living, double range, boolean immediate) {
		return Iterables.getFirst(living.world.getEntitiesWithinAABB(EntityDispenser.class,
				living.getEntityBoundingBox().grow(range, range/4D, range), new Predicate<EntityDispenser>() {

			@Override
			public boolean apply(EntityDispenser input) {
				return TF2Util.isOnSameTeam(input, living) && (!immediate || (!input.isDisabled() &&input.getMetal()>0));
			}

		}), null);
	}

	public static boolean isNaturalBlock(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();

		for (Block block2 : NATURAL_BLOCKS) {
			if (block == block2)
				return true;
		}

		Biome biome = world.getBiome(pos);
		return block == biome.topBlock.getBlock() || block == biome.fillerBlock.getBlock();
	}

	public static Vec2f getAngleFromFacing(EnumFacing facing) {
		switch (facing) {
		case UP: return new Vec2f(0,90);
		case DOWN: return new Vec2f(0,-90);
		default : return new Vec2f(facing.getHorizontalAngle(),0);
		}
	}

	public static void stomp(EntityLivingBase living) {
		for (EntityLivingBase target : living.world.getEntitiesWithinAABB(EntityLivingBase.class,
				living.getEntityBoundingBox().grow(0.25, -living.motionY, 0.25), new Predicate<EntityLivingBase>() {

			@Override
			public boolean apply(EntityLivingBase input) {
				return input != living && !TF2Util.isOnSameTeam(input, living);
			}

		})) {

			float damage = Math.max(0, living.fallDistance - 3) * 1.8f;
			living.fallDistance = 0;
			if (damage > 0) {
				target.attackEntityFrom(new EntityDamageSource("fallpl", living), damage);
				TF2Util.playSound(living, TF2Sounds.WEAPON_MANTREADS, 1.5F, 1F);
			}
		}
	}

	public static void extractData(String input, File output, File source) {
		try {
			if (source.isFile()) {
				ZipFile zip = new ZipFile(source);
				ZipEntry entry = zip.getEntry(input);
				if (entry != null) {
					long crc = entry.getCrc();
					InputStream zin = zip.getInputStream(entry);
					byte[] bytes = new byte[(int) entry.getSize()];
					zin.read(bytes);
					FileOutputStream str = new FileOutputStream(output);
					str.write(bytes);
					str.flush();
					str.close();
					zin.close();

					FileInputStream istr = new FileInputStream(output);
					byte[] bytesc = new byte[istr.available()];
					istr.read(bytesc);
					CRC32 crc2 = new CRC32();
					crc2.update(bytesc);
					istr.close();

					if (crc2.getValue() != crc) {
						TF2weapons.corrupted = true;
					}
				}
				zip.close();
			}
			else {
				File inputFile = new File(source, input);
				FileInputStream istr = new FileInputStream(inputFile);

				byte[] bytes = new byte[(int) inputFile.length()];
				istr.read(bytes);
				FileOutputStream str = new FileOutputStream(output);
				str.write(bytes);
				str.flush();
				str.close();
				istr.close();

				FileInputStream istr2 = new FileInputStream(output);
				byte[] bytesc = new byte[istr2.available()];
				istr2.read(bytesc);
				CRC32 crc2 = new CRC32();
				crc2.update(bytesc);
				TF2weapons.LOGGER.info("Value: "+crc2.getValue());
				istr2.close();
			}
		}
		catch (IOException e) {
			TF2weapons.corrupted = true;
			e.printStackTrace();
		}

		if (TF2weapons.corrupted) {
			TF2weapons.instance.weaponDir.delete();
			TF2weapons.proxy.displayCorruptedFileError();
		}
	}

	public static boolean isWeaponOfClass(ItemStack stack, int slot, String name) {
		String parent = ItemFromData.getData(stack).getString(PropertyType.BASED_ON);
		WeaponData data;
		if (!parent.isEmpty())
			data = MapList.nameToData.get(parent);
		else
			data = ItemFromData.getData(stack);
		return ItemFromData.isItemOfClassSlot(data, slot, name);
	}

	public static String getWeaponUsedByClass(ItemStack stack) {
		if (ItemFromData.getData(stack) == ItemFromData.BLANK_DATA)
			return null;
		String parent = ItemFromData.getData(stack).getString(PropertyType.BASED_ON);
		WeaponData data;
		if (!parent.isEmpty() && MapList.nameToData.get(parent).hasProperty(PropertyType.SLOT))
			data = MapList.nameToData.get(parent);
		else
			data = ItemFromData.getData(stack);
		if (data.hasProperty(PropertyType.SLOT))
			return Iterables.getFirst(data.get(PropertyType.SLOT).keySet(),null);
		else
			return null;
	}

	public static EntityLivingBase getOwnerIfOwnable(EntityLivingBase living) {
		if (living instanceof IEntityOwnable && ((IEntityOwnable)living).getOwner() != null)
			return (EntityLivingBase) ((IEntityOwnable)living).getOwner();
		else
			return living;
	}

	public static Vec3d getHeightVec(World world, BlockPos pos) {
		/*EnumFacing facing = EnumFacing.getFacingFromVector((float)living.motionX, 0f, (float)living.motionZ);
		if (!living.world.isBlockFullCube(pos) && living.world.isBlockFullCube(pos.down())
				&& living.world.isBlockFullCube(pos.offset(facing)) && !living.world.isBlockFullCube(pos.offset(facing).up())) {
			vec2 = vec2.addVector(0, 1, 0).normalize().scale(motiona);*/
		Vec3d vec = new Vec3d(0,0,0);
		IBlockState center = world.getBlockState(pos);
		IBlockState down = world.getBlockState(pos.offset(EnumFacing.DOWN));
		IBlockState north = world.getBlockState(pos.offset(EnumFacing.NORTH));
		IBlockState south = world.getBlockState(pos.offset(EnumFacing.SOUTH));
		IBlockState west = world.getBlockState(pos.offset(EnumFacing.WEST));
		IBlockState east = world.getBlockState(pos.offset(EnumFacing.EAST));
		if (center.getCollisionBoundingBox(world, pos) != null)
			vec = vec.addVector(0, 1-center.getCollisionBoundingBox(world, pos).maxY, 0);
		else if (down.getCollisionBoundingBox(world, pos) != null)
			vec = vec.addVector(0, 2-down.getCollisionBoundingBox(world, pos).maxY, 0);
		if (north.getCollisionBoundingBox(world, pos) != null)
			vec = vec.addVector(0, 0, north.getCollisionBoundingBox(world, pos).maxY);
		if (south.getCollisionBoundingBox(world, pos) != null)
			vec = vec.addVector(0, 0, -south.getCollisionBoundingBox(world, pos).maxY);
		if (west.getCollisionBoundingBox(world, pos) != null)
			vec = vec.addVector(west.getCollisionBoundingBox(world, pos).maxY, 0, 0);
		if (east.getCollisionBoundingBox(world, pos) != null)
			vec = vec.addVector(-east.getCollisionBoundingBox(world, pos).maxY, 0, 0);
		//vec = vec.normalize();
		return vec;
	}
	public static ItemStack pickAmmo(ItemStack stack, EntityPlayer player, boolean addNormalInventory) {
		if (stack.getItem() instanceof ItemFireAmmo && stack.getCount() == 1) {
			stack = TF2Util.mergeStackByDamage(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), stack);
			if (!player.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3).isEmpty())
				stack = TF2Util.mergeStackByDamage(player.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)
						.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), stack);
			if (stack.isEmpty()) {
				return stack;
			}
		}
		if (stack.getItem() instanceof ItemAmmo && player.hasCapability(TF2weapons.INVENTORY_CAP, null) || addNormalInventory) {

			if (!player.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3).isEmpty()) {
				IItemHandler inv = player.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)
						.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				stack=ItemHandlerHelper.insertItemStacked(inv, stack, false);
				/*if(stack.isEmpty()){
					event.getItem().setItem(orig);
				}*/
				ItemStack weapon = player.getHeldItemMainhand();
				if(!weapon.isEmpty() && weapon.getItem() instanceof ItemWeapon)
					TF2weapons.network.sendTo(new TF2Message.UseMessage(weapon.getItemDamage(), false,
							((ItemUsable) weapon.getItem()).getAmmoAmount(player, weapon), EnumHand.MAIN_HAND),(EntityPlayerMP) player);
			}
			if (!stack.isEmpty() && addNormalInventory) {
				IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				stack=ItemHandlerHelper.insertItemStacked(inv, stack, false);
				ItemStack weapon = player.getHeldItemMainhand();
				if(!weapon.isEmpty() && weapon.getItem() instanceof ItemWeapon)
					TF2weapons.network.sendTo(new TF2Message.UseMessage(weapon.getItemDamage(), false,
							((ItemUsable) weapon.getItem()).getAmmoAmount(player, weapon), EnumHand.MAIN_HAND),(EntityPlayerMP) player);
			}
			if (stack.isEmpty()) {
				return stack;
			}
		}
		return stack;
	}

	public static int getValueOnAxis(Vec3i vec, Axis axis) {
		switch (axis) {
		case X: return vec.getX();
		case Y: return vec.getY();
		case Z: return vec.getZ();
		default: return 0;
		}
	}

	public static BlockPos setValueOnAxis(Vec3i vec, Axis axis, int value) {
		switch (axis) {
		case X: return new BlockPos(value, vec.getY(), vec.getZ());
		case Y: return new BlockPos(vec.getX(), value, vec.getZ());
		case Z: return new BlockPos(vec.getX(), vec.getY(), value);
		default: return new BlockPos(vec);
		}
	}
	static {
		for (int i = 0; i < 512; i++) {
			ASIN_VALUES[i] = (float) Math.asin(i/511D);
		}
		for (int i = 0; i < 32; ++i)
		{
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int i1 = (i >> 0 & 1) * 170 + j;

			if (i == 6)
			{
				k += 85;
			}

			if (i >= 16)
			{
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}
	}


}
