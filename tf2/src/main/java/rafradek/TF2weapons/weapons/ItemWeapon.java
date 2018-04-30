package rafradek.TF2weapons.weapons;

import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import atomicstryker.dynamiclights.client.DynamicLights;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.IWeaponItem;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2EventsClient;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.ItemToken;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

public abstract class ItemWeapon extends ItemUsable implements IWeaponItem {
	/*
	 * public float damage; public float scatter; public int pellets; public
	 * float maxDamage; public int damageFalloff; public float minDamage; public
	 * int reload; public int clipSize; public boolean hasClip; public boolean
	 * clipType; public boolean randomCrits; public float criticalDamage; public
	 * int firstReload; public int knockback;
	 */
	public static boolean shouldSwing = false;
	public static int critical;
	protected static final UUID HEALTH_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785AAB6");
	protected static final UUID SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE97871BC2");

	public static final UUID HEADS_HEALTH = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785FC3A");
	public static final UUID HEADS_SPEED = UUID.fromString("FA233E1C-4180-4865-B01B-B4A79785FC3A");
	public AttributeModifier headsHealthMod = new AttributeModifier(HEADS_HEALTH, "Heads modifier", 0, 0);
	public AttributeModifier headsSpeedMod = new AttributeModifier(HEADS_SPEED, "Heads modifier", 0, 2);
	public static boolean inHand;

	public ItemWeapon() {
		super();
		this.addPropertyOverride(new ResourceLocation("inhand"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn != null && inHand)
					return 1;
				return 0;
			}
		});
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		if (par5 && ((EntityLivingBase)par3Entity).getHeldItemMainhand() == par1ItemStack) {
			WeaponsCapability cap = par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null);
			if (TF2ConfigVars.randomCrits && !par2World.isRemote && cap.critTimeCool <= 0) {
				cap.critTimeCool = 20;
				if (this.rapidFireCrits(par1ItemStack) && this.hasRandomCrits(par1ItemStack, par3Entity)
						&& ((EntityLivingBase) par3Entity).getRNG().nextFloat() <= this.critChance(par1ItemStack,
								par3Entity)) {
					cap.setCritTime(40);
					// System.out.println("Apply crits rapid");
				}
			}
			if (TF2Attribute.getModifier("Kill Count", par1ItemStack, 0, null)!=0){
				par1ItemStack.getTagCompound().setInteger("Heads", cap.getHeads());
			}
			if (cap.getCritTime() > 0)
				cap.setCritTime(cap.getCritTime()-1);
			cap.critTimeCool -= 1;
			/*
			 * if(par3Entity instanceof
			 * EntityTF2Character&&((EntityTF2Character)
			 * par3Entity).getAttackTarget()!=null){
			 * System.out.println(this.getWeaponSpreadBase(par1ItemStack,
			 * (EntityLivingBase) par3Entity));
			 * if(par1ItemStack.getTagCompound().getInteger("reload")<=100&&!((
			 * EntityTF2Character)par3Entity).attack.lookingAt(this.
			 * getWeaponSpreadBase(par1ItemStack, (EntityLivingBase)
			 * par3Entity)*100+1)){
			 * par1ItemStack.getTagCompound().setInteger("reload", 100); }
			 * //par1ItemStack.getTagCompound().setBoolean("WaitProper", true);
			 * }
			 */
		}
	}

	public boolean canPenetrate(ItemStack stack, EntityLivingBase shooter) {
		return getData(stack).getBoolean(PropertyType.PENETRATE) || TF2Attribute.getModifier("Penetration", stack, 0, shooter) != 0;
	}
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		// boolean mainHand=living instanceof
		// EntityPlayer&&living.getEntityData().getCompoundTag("TF2").getBoolean("mainhand");
		WeaponsCapability cap=living.getCapability(TF2weapons.WEAPONS_CAP, null);
		if (this.holdingMode(stack, living) > 0 && !cap.isCharging()) {
			cap.setCharging(true);
			cap.chargeTicks = 0;
			if (world.isRemote)
				ClientProxy.playWeaponSound(living, ItemFromData.getSound(stack, PropertyType.CHARGE_SOUND), false, 0,
						stack);
			return true;
		}
		if (stack.getItemDamage() != stack.getMaxDamage())
			if (this.hasClip(stack)) {
				stack.damageItem(1, living);
				
					
			}
		if (living instanceof EntityPlayer && hand == EnumHand.MAIN_HAND)
			((EntityPlayer) living).resetCooldown();
		else if (world.isRemote && Minecraft.getMinecraft().player == living)
			Minecraft.getMinecraft().getItemRenderer().resetEquippedProgress(EnumHand.OFF_HAND);

		int thisCritical = TF2Util.calculateCritPre(stack, living);

		critical = thisCritical;
		
		
		if(cap.focusShotTicks>0){
			
			if(cap.focusedShot(stack))
				cap.focusShotRemaining=8;
			cap.focusShotTicks=0;
		}
		
		if (/* living instanceof EntityTF2Ch\aracter&& */this.getAmmoType(
				stack) != 0/* &&((EntityTF2Character)living).getAmmo()>=0 */) {
			//

			if (living instanceof EntityTF2Character && ((EntityTF2Character) living).getAmmo() >= 0 && !this.hasClip(stack))
				((EntityTF2Character) living).useAmmo(((ItemWeapon) stack.getItem()).getActualAmmoUse(stack, living, 1));
			else if (living instanceof EntityPlayer && !((EntityPlayer) living).capabilities.isCreativeMode
					&& !this.hasClip(stack)) {
				if(TF2Attribute.getModifier("Metal Ammo", stack, 0, living) != 0)
					cap.setMetal(cap.getMetal()-((ItemWeapon) stack.getItem()).getActualAmmoUse(stack, living, 1));
				else {
					ItemStack stackAmmo = this.searchForAmmo(living, stack);
					if (!stackAmmo.isEmpty())
						((ItemAmmo) stackAmmo.getItem()).consumeAmmo(living, stackAmmo,
								((ItemWeapon) stack.getItem()).getActualAmmoUse(stack, living, 1));
				}
			}
		}
		if (!world.isRemote && living instanceof EntityPlayerMP)
			TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), false,!this.hasClip(stack)?this.getAmmoAmount(living, stack):-1, hand),(EntityPlayerMP) living);

		this.doFireSound(stack, living, world, thisCritical);
		
		if (world.isRemote)
			this.doMuzzleFlash(stack, living, hand);

		if (!living.onGround && living.getCapability(TF2weapons.WEAPONS_CAP, null).fanCool<=0 && TF2Attribute.getModifier("KnockbackFAN", stack, 0, living)!=0){
			Vec3d look=living.getLook(1f);
			living.addVelocity(-look.x*0.66, -look.y*0.58, -look.z*0.66);
		}
		
		if (living instanceof EntityPlayer && !((EntityPlayer)living).capabilities.isCreativeMode && living.getCapability(TF2weapons.PLAYER_CAP, null).zombieHuntTicks <= 0
				&& (!(this instanceof ItemMeleeWeapon || this instanceof ItemJar) || getData(stack).getName().equals("fryingpan"))) {
			living.getCapability(TF2weapons.PLAYER_CAP, null).zombieHuntTicks = 15;
			TF2Util.attractMobs(living, world);
		}
			
		/*ItemStack toUse=stack;
		if(!getNewStack(getData(stack).getString(PropertyType.OVERRIDE)).isEmpty())
			toUse = getNewStack(getData(stack).getString(PropertyType.OVERRIDE));*/
		for (int x = 0; x < this.getWeaponPelletCount(stack, living); x++)
			// System.out.println("shoot");
			/*
			 * EntityBullet bullet; if(target==null){ bullet = new
			 * EntityBullet(world, living, this.scatter); } else{ bullet = new
			 * EntityBullet(world, living, target, this.scatter); } bullet.stack
			 * = stack; bullet.setDamage(this.damage*damagemult); bullet.damageM
			 * = this.maxDamage; bullet.damageMM = this.damageFalloff;
			 * bullet.minDamage = this.minDamage; if(thisCritical)
			 * bullet.critical = true; bullet.setDamage(this.damage*3); }
			 * world.spawnEntity(bullet);
			 */
			
			this.shoot(stack, living, world, thisCritical, hand);
		
		return true;
	}

	@Optional.Method(modid = "dynamiclights")
	public void doMuzzleFlashLight(ItemStack stack, EntityLivingBase living) {
		MuzzleFlashLightSource light = new MuzzleFlashLightSource(living);
		TF2EventsClient.muzzleFlashes.add(light);
		DynamicLights.addLightSource(light);
	}

	/*public IFireMethod getFireMethod(ItemStack stack, EntityLivingBase living) {
		return getData(stack).hasProperty(PropertyType.PROJECTILE);
	}*/
	public abstract void shoot(ItemStack stack, EntityLivingBase living, World world, int thisCritical, EnumHand hand);

	public void doFireSound(ItemStack stack, EntityLivingBase living, World world, int critical) {
		if (ItemFromData.getData(stack).hasProperty(PropertyType.FIRE_SOUND)) {
			SoundEvent soundToPlay = SoundEvent.REGISTRY
					.getObject(new ResourceLocation(ItemFromData.getData(stack).getString(PropertyType.FIRE_SOUND)
							+ (critical == 2 ? ".crit" : "")));
			
			living.playSound(soundToPlay, living instanceof EntityLiving && !(((EntityLiving) living).getAttackTarget() instanceof EntityPlayer) ? TF2ConfigVars.mercenaryVolume : TF2ConfigVars.gunVolume, 1f);
			if (world.isRemote)
				ClientProxy.removeReloadSound(living);
		}
	}
	
	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		/*
		 * boolean flag=true; if(living instanceof
		 * EntityTF2Character&&((EntityTF2Character)living).getAmmo()<=0){
		 * flag=false; }
		 */
		return super.canFire(world, living, stack) && !(this.holdingMode(stack, living) > 0 && living.getCapability(TF2weapons.WEAPONS_CAP, null).isCharging())
				&& ( this.isAmmoSufficient(stack, living, false)
						|| (living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.isCreativeMode));
	}

	@Override
	public boolean endUse(ItemStack stack, EntityLivingBase living, World world, int action, int newState) {
		/*
		 * if(!world.isRemote&&(!ClientProxy.fireSounds.containsKey(living)||
		 * ClientProxy.fireSounds.get(living).type!=3)){ worl }
		 */
		WeaponsCapability cap = living.getCapability(TF2weapons.WEAPONS_CAP, null);
		if (this.holdingMode(stack, living) > 0 && (newState & 1) == 0 && cap.isCharging()) {
			// System.out.println("stop charging "+newState);
			
			cap.fire1Cool = this.getFiringSpeed(stack, living);

			if (world.isRemote && ClientProxy.fireSounds.get(living) != null)
				ClientProxy.fireSounds.get(living).setDone();
			// Minecraft.getMinecraft().getSoundHandler().stopSound(ClientProxy.fireSounds.get(living));
			this.use(stack, living, world, EnumHand.MAIN_HAND, null);
			cap.setCharging(false);
			cap.lastFire = 1250;
			if (world.isRemote)
				sps++;
			cap.reloadCool = 0;
			if ((cap.state & 8) != 0)
				cap.state -= 8;
		}
		return false;
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		/*
		 * if (par1ItemStack.hasTagCompound()) {
		 * par2List.add("Firing: "+Integer.toString(par1ItemStack.getTagCompound
		 * ().getShort("reload")));
		 * par2List.add("Reload: "+Integer.toString(par1ItemStack.getTagCompound
		 * ().getShort("reloadd")));
		 * par2List.add("Crit: "+Integer.toString(par1ItemStack.getTagCompound()
		 * .getShort("crittime"))); }
		 */

		if (this.hasClip(stack))
			tooltip.add(
					"Clip: " + (this.getWeaponClipSize(stack, null) - stack.getItemDamage())
							+ "/" + this.getWeaponClipSize(stack, null));
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		
		if (slot == EntityEquipmentSlot.MAINHAND && getData(stack) != ItemFromData.BLANK_DATA && stack.hasTagCompound() 
				&& ItemToken.allowUse(stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).usedClass, getData(stack).getString(PropertyType.CLASS))) {
			int heads=Math.min((int)TF2Attribute.getModifier("Kill Count", stack, 0, null), stack.getTagCompound().getInteger("Heads"));
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier",
							this.getWeaponDamage(stack, null, null) * this.getWeaponPelletCount(stack, null) - 1, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(
					ATTACK_SPEED_MODIFIER, "Weapon modifier", -4 + (1000D / this.getFiringSpeed(stack, null)), 0));
			float addHealth = TF2Attribute.getModifier("Health", stack, 0, null)+heads * TF2Attribute.getModifier("Max Health Kill", stack, 0, null);
			if (addHealth != 0)
				multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(),
						new AttributeModifier(HEALTH_MODIFIER, "Weapon modifier", addHealth, 0));
			float addSpeed = TF2Attribute.getModifier("Speed", stack, 1 + heads * TF2Attribute.getModifier("Speed Kill", stack, 0, null), null);
			if (addSpeed != 1)
				multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(),
						new AttributeModifier(SPEED_MODIFIER, "Weapon modifier", addSpeed - 1, 2));
		}
		stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).usedClass = -1;
		return multimap;
	}

	public float critChance(ItemStack stack, Entity entity) {
		float chance = 0.025f;
		if (ItemUsable.lastDamage.containsKey(entity))
			for (int i = 0; i < 20; i++)
				chance += ItemUsable.lastDamage.get(entity)[i] / 800;
		return Math.min(chance, 0.125f);
	}

	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		WeaponsCapability cap = living.getCapability(TF2weapons.WEAPONS_CAP, null);
		if (this.holdingMode(stack, living) > 0 && cap.isCharging())
			// System.out.println("charging "+tag.getShort("chargeticks"));
			cap.chargeTicks += 1;
			if (cap.chargeTicks >= this.holdingMode(stack, living) && !this.shouldKeepCharged(stack, living))
				this.endUse(stack, living, world, 1, 0);
				
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}

	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		if(!world.isRemote && living instanceof EntityPlayer && living.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal() >= 100 
				&& TF2Attribute.getModifier("Pick Building", stack, 0, living)>0) {
			Vec3d forward=living.getLook(1f).scale(120).add(living.getPositionEyes(1));
			RayTraceResult result=TF2Util.pierce(world, living, living.posX, living.posY+living.getEyeHeight(), living.posZ, forward.x, forward.y, forward.z, false, 0.5f, false).get(0);
			if(result.entityHit != null && result.entityHit instanceof EntityBuilding && result.entityHit.isEntityAlive() && !((EntityBuilding)result.entityHit).isSapped() && ((EntityBuilding)result.entityHit).getOwner() == living) {
				result.entityHit.setPosition(living.posX, living.posY, living.posZ);
				((EntityBuilding) result.entityHit).grab();
				living.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(living.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal()-100);
			}
		}
		return false;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return stack.hasTagCompound() ? this.getWeaponClipSize(stack, null) : 0;
	}

	@Override
	public float getWeaponDamage(ItemStack stack, EntityLivingBase living, Entity target) {
		float damage = ItemFromData.getData(stack).getFloat(PropertyType.DAMAGE);
		if(living == null || living!=target){
			damage=TF2Attribute.getModifier("Damage", stack,damage, living);
			if (living != null && (this.isDoubleWielding(living) || living.isHandActive()))
				damage *= 0.85f;
			if (target != null && !target.isBurning())
				damage = TF2Attribute.getModifier("Damage Non Burn", stack, damage, living);
			if (target != null && target.isBurning())
				damage = TF2Attribute.getModifier("Damage Burning", stack, damage, living);
			if (living != null && living.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null){
				//System.out.println("Pre "+damage);
				damage=(float) calculateModifiers(living.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE),ATTACK_DAMAGE_MODIFIER,damage,1D/9D);
				//System.out.println("Post "+damage);
			}
			
		}
		return damage;
	}

	
	public float getWeaponMaxDamage(ItemStack stack, EntityLivingBase living) {
		return ItemFromData.getData(stack).getFloat(PropertyType.MAX_DAMAGE);
	}

	public float getWeaponMinDamage(ItemStack stack, EntityLivingBase living) {
		return ItemFromData.getData(stack).getFloat(PropertyType.MIN_DAMAGE);
	}

	public float getWeaponSpread(ItemStack stack, EntityLivingBase living) {
		float base = this.getWeaponSpreadBase(stack, living);
		if (living instanceof EntityTF2Character && ((EntityTF2Character) living).getAttackTarget() != null) {
			float totalRotation = 0;
			for (int i = 0; i < 20; i++)
				totalRotation += ((EntityTF2Character) living).lastRotation[i];
			
			/*
			 * double
			 * speed=Math.sqrt((target.posX-shooter.targetPrevPos[1])*(target.
			 * posX-shooter.targetPrevPos[1])+(target.posY-shooter.targetPrevPos
			 * [3]) (target.posY-shooter.targetPrevPos[3])+(target.posZ-shooter.
			 * targetPrevPos[5])*(target.posZ-shooter.targetPrevPos[5]));
			 */
			base += /* (speed+0.045) */((EntityTF2Character) living).getMotionSensitivity() * totalRotation * (this instanceof ItemProjectileWeapon ? 0.2f : 0.01f);
			// System.out.println(target.motionX+" "+target.motionY+"
			// "+target.motionZ+"
			// "+(speed+0.045)*((EntityTF2Character)living).getMotionSensitivity());
			/*
			 * shooter.targetPrevPosX=target.posX;
			 * shooter.targetPrevPosY=target.posY;
			 * shooter.targetPrevPosZ=target.posZ;
			 */
		}
		return Math.abs(base);
	}

	public float getWeaponSpreadBase(ItemStack stack, EntityLivingBase living) {
		return living != null && ItemFromData.getData(stack).getBoolean(PropertyType.SPREAD_RECOVERY)
				&& living.getCapability(TF2weapons.WEAPONS_CAP, null).lastFire <= 0
						? 0
						: TF2Attribute.getModifier("Spread", stack,
								ItemFromData.getData(stack).getFloat(PropertyType.SPREAD), living)
								/ TF2Attribute.getModifier("Accuracy", stack, 1, living)
								* (living != null && (this.isDoubleWielding(living) || living.isHandActive()) ? 1.5f
										: 1f)
								* (TF2Attribute.getModifier("Spread Health", stack, 1f, living) != 1f ?
									this.getHealthBasedBonus(stack, living, TF2Attribute.getModifier("Spread Health", stack, 1f, living)) : 1f);
	}

	public int getWeaponPelletCount(ItemStack stack, EntityLivingBase living) {
		return (int) (TF2Attribute.getModifier("Pellet Count", stack,
				ItemFromData.getData(stack).getInt(PropertyType.PELLETS), living));
	}

	public float getWeaponDamageFalloff(ItemStack stack) {
		return ItemFromData.getData(stack).getFloat(PropertyType.DAMAGE_FALOFF);
	}

	public int getWeaponReloadTime(ItemStack stack, EntityLivingBase living) {
		return (int) (TF2Attribute.getModifier("Reload Time", stack,
				ItemFromData.getData(stack).getInt(PropertyType.RELOAD_TIME), living));
	}

	public int getWeaponFirstReloadTime(ItemStack stack, EntityLivingBase living) {
		return (int) (TF2Attribute.getModifier("Reload Time", stack,
				ItemFromData.getData(stack).getInt(PropertyType.RELOAD_TIME_FIRST), living)
				* (living != null && this.isDoubleWielding(living) ? 2f : 1f));
	}

	public boolean hasClip(ItemStack stack) {
		// System.out.println("Clip:"+stack.getTagCompound());
		return ItemFromData.getData(stack).getBoolean(PropertyType.RELOADS_CLIP);
	}

	public int getWeaponClipSize(ItemStack stack, EntityLivingBase living) {
		// System.out.println("With tag: "+stack.getTagCompound());
		int headsbonus = (int) (TF2Attribute.getModifier("Clip Kill", stack, 0, living) * 
				Math.min(TF2Attribute.getModifier("Kill Count", stack, 0, living), stack.getTagCompound().getInteger("Heads")));
		return (int) (TF2Attribute.getModifier("Clip Size", stack,
				ItemFromData.getData(stack).getInt(PropertyType.CLIP_SIZE), living)) + headsbonus;
	}

	public boolean IsReloadingFullClip(ItemStack stack) {
		return ItemFromData.getData(stack).getBoolean(PropertyType.RELOADS_FULL_CLIP);
	}

	public boolean hasRandomCrits(ItemStack stack, Entity par3Entity) {
		return par3Entity instanceof EntityPlayer && !par3Entity.world.isRemote && ItemFromData.getData(stack).getBoolean(PropertyType.RANDOM_CRITS)
				&& TF2Attribute.getModifier("Random Crit", stack, 0, null) == 0;
	}

	public double getWeaponKnockback(ItemStack stack, EntityLivingBase living) {
		return TF2Attribute.getModifier("Knockback", stack, ItemFromData.getData(stack).getInt(PropertyType.KNOCKBACK),living) 
				* (living.hasCapability(TF2weapons.WEAPONS_CAP, null) && WeaponsCapability.get(living).knockbackActive 
						&& TF2Attribute.getModifier("Knockback Rage", stack, 0, living) != 0? 4.5 : 1);
	}

	public boolean rapidFireCrits(ItemStack stack) {
		return ItemFromData.getData(stack).getBoolean(PropertyType.RAPIDFIRE_CRITS);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return true;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return !shouldSwing;
	}

	@Override
	public void draw(WeaponsCapability cap, ItemStack stack, EntityLivingBase living, World world) {
		cap.reloadCool = 0;
		cap.setCritTime(0);
		cap.state = cap.state & 7;
		//if (!world.isRemote && living instanceof EntityPlayerMP)
			//TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), false,ItemAmmo.getAmmoAmount(living, stack), EnumHand.MAIN_HAND),(EntityPlayerMP) living);

		super.draw(cap, stack, living, world);
	}
	
	public void holster(WeaponsCapability cap, ItemStack stack, EntityLivingBase living, World world) {
		super.holster(cap, stack, living, world);
		cap.focusShotRemaining=0;
		cap.focusShotTicks=0;
		cap.setCharging(false);
		float removeHealth=0;
		for (Entry<String, AttributeModifier> entry : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries())
        {
			if(entry.getValue().getID()==HEALTH_MODIFIER)
				removeHealth+=entry.getValue().getAmount();
            //IAttributeInstance iattributeinstance = livinggetAttributeInstanceByName((String)entry.getKey());
        }
		if(removeHealth != 0)
			living.setHealth((living.getMaxHealth()/(removeHealth+living.getMaxHealth())*living.getHealth()));
	}
	
	public boolean onHit(ItemStack stack, EntityLivingBase attacker, Entity target, float damage, int critical, boolean simulate) {
		if(target instanceof EntityBuilding && TF2Util.isOnSameTeam(attacker, target) && !((EntityBuilding)target).isSapped()) {
			float repair=TF2Attribute.getModifier("Repair Building", stack, 0, attacker);
			if(repair>0) {
				if (!simulate)
					((EntityBuilding)target).heal(repair);
				return false;
			}
		}
		else if(target instanceof EntityLivingBase && TF2Util.isOnSameTeam(attacker, target) && !(target instanceof EntityBuilding)) {
			float heal=damage*TF2Attribute.getModifier("Heal Target", stack, 0, attacker);
			if(heal>0) {
				if (!simulate)
				((EntityLivingBase) target).heal(heal);
				return false;
			}
		}
		if (target instanceof EntityLivingBase && !TF2Util.isEnemy(attacker, (EntityLivingBase) target)) {
			float speedtime=TF2Attribute.getModifier("Speed Hit", stack, 0, attacker);
			if(!simulate && speedtime > 0) {
				((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SPEED,(int) (speedtime*20),1));
				attacker.addPotionEffect(new PotionEffect(MobEffects.SPEED,(int) (speedtime*36),1));
			}
		}
		return true;
	}
	public void onDealDamage(ItemStack stack, EntityLivingBase attacker, Entity target, DamageSource source, float amount) {
		if (TF2Attribute.getModifier("Burn Hit", stack, 0, attacker) > 0)
			TF2Util.igniteAndAchievement(target, attacker, (int) TF2Attribute.getModifier("Burn Time", stack,
					TF2Attribute.getModifier("Burn Hit", stack, 0, attacker), attacker) + 1);
		if (target instanceof EntityLivingBase && attacker.hasCapability(TF2weapons.WEAPONS_CAP, null)){
			boolean enemy = TF2Util.isEnemy(attacker, (EntityLivingBase) target);
			/*if (attacker instanceof EntityPlayerMP && !target.isEntityAlive() && 
			if (attacker instanceof EntityPlayerMP && target instanceof EntitySniper && !target.isEntityAlive() && 
					&& TF2weapons.isEnemy(attacker, (EntityLivingBase) target)){
					*/
			float metalhit = TF2Attribute.getModifier("Metal Hit", stack, 0, attacker);
			if (metalhit != 0) {
				int restore=(int) (amount*metalhit/TF2ConfigVars.damageMultiplier);
				if(!enemy && restore > 30)
					restore=30;
				attacker.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal((int) (attacker.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal()+restore));
			}
			if (!target.isEntityAlive() && !(target instanceof EntityBuilding)
					&& TF2Attribute.getModifier("Kill Count", stack, 0, attacker)!=0){
				stack.getTagCompound().setInteger("Heads", stack.getTagCompound().getInteger("Heads")+1);
			}
			float healthHit=TF2Attribute.getModifier("Health Hit", stack, 0, attacker);
			if (healthHit > 0)
				attacker.heal(enemy ? healthHit : healthHit/2f);
			float bleed=TF2Attribute.getModifier("Bleed", stack, 0, attacker);
			if (bleed > 0) {
				((EntityLivingBase) target).addPotionEffect(new PotionEffect(TF2weapons.bleeding,(int) (bleed*20f)+10,0));
			}
			int rage = (int) TF2Attribute.getModifier("Knockback Rage", stack, 0, attacker);
			if (enemy && !WeaponsCapability.get(attacker).knockbackActive && rage > 0) {
				WeaponsCapability.get(attacker).setKnockbackRage(Math.min(1, WeaponsCapability.get(attacker).getKnockbackRage()+amount*(0.025f+rage*0.017f)));
			}
			if (enemy && TF2Attribute.getModifier("Uber Hit", stack, 0, attacker) > 0)
				if (attacker instanceof EntityPlayer)
					for (ItemStack medigun : ((EntityPlayer) attacker).inventory.mainInventory)
						if (medigun != null && medigun.getItem() instanceof ItemMedigun) {
							medigun.getTagCompound().setFloat("ubercharge",
									MathHelper.clamp(
											medigun.getTagCompound().getFloat("ubercharge")
													+ TF2Attribute.getModifier("Uber Hit", stack, 0, attacker) / 100,
											0, 1));
							if (stack.getTagCompound().getFloat("ubercharge") >= 1)
								attacker.playSound(ItemFromData.getSound(stack, PropertyType.CHARGED_SOUND), 1.2f, 1);
							break;
						}
			if (TF2Attribute.getModifier("Fire Rate Hit", stack, 1, attacker) != 1) {
				//System.out.println(this.getFiringSpeed(stack, attacker) * (1-(1/TF2Attribute.getModifier("Fire Rate Hit", stack, 1, attacker))));
				attacker.getCapability(TF2weapons.WEAPONS_CAP, null).fire1Cool-= this.getFiringSpeed(stack, attacker) * (1-(1/TF2Attribute.getModifier("Fire Rate Hit", stack, 1, attacker)));
				if(attacker instanceof EntityPlayerMP)
					TF2weapons.network.sendTo(new TF2Message.ActionMessage(27,attacker), (EntityPlayerMP) attacker);
			}
		}
	}

	public boolean doMuzzleFlash(ItemStack stack, EntityLivingBase attacker, EnumHand hand) {

		ClientProxy.spawnFlashParticle(attacker.world, attacker, hand);
		
		if(TF2ConfigVars.dynamicLights)
			this.doMuzzleFlashLight(stack, attacker);
		return true;
	}
	public boolean showInfoBox(ItemStack stack, EntityPlayer player){
		return getAmmoType(stack)!=0;
	}
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player){
		String[] result=new String[2];
		boolean metalammo=TF2Attribute.getModifier("Metal Ammo", stack, 0, player)!=0;
		
		int holdTickMax=holdingMode(stack, player);
		
		if(holdTickMax > 0 && player.getCapability(TF2weapons.WEAPONS_CAP, null).isCharging()) {
			int chargeTicks=player.getCapability(TF2weapons.WEAPONS_CAP, null).chargeTicks;
			int progress= (int) ((float)chargeTicks/(float)holdTickMax*20);
			result[0]="";
			if(progress>0) {
				for(int i=0;i<20;i++){
					if(i<progress)
						result[0]=result[0]+"|";
					else
						result[0]=result[0]+".";
				}
			}
		}
		else {
			if(metalammo)
				result[0] = "METAL";
			else
				result[0] = "AMMO";
			
			int focus=(int) TF2Attribute.getModifier("Focus", stack, 0, player);
			int progress=0;
			if(focus!=0){
				progress=(int) (((float)player.getCapability(TF2weapons.WEAPONS_CAP, null).focusShotTicks/(float)(70-focus*23+((ItemUsable)stack.getItem()).getFiringSpeed(stack, player)/50))*3f);
			}
			else if(TF2Attribute.getModifier("Headshot", stack, 0, player)>0) {
				progress=(1250-player.getCapability(TF2weapons.WEAPONS_CAP, null).lastFire)/416;
			}
			if(progress>0) {
				result[0]=result[0]+" ";
				for(int i=0;i<progress && i<3;i++){
					result[0]=result[0]+"\u2588";
				}
			}
		}
		int ammoLeft=0;
		if (getAmmoType(stack) < player.getCapability(TF2weapons.PLAYER_CAP, null).cachedAmmoCount.length)
			ammoLeft=player.getCapability(TF2weapons.PLAYER_CAP, null).cachedAmmoCount[getAmmoType(stack)];
		else
			ammoLeft = this.getAmmoAmount(player, stack);
		if(metalammo)
			ammoLeft=player.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal();
		
		if(hasClip(stack)){
			int inClip=stack.getMaxDamage()-stack.getItemDamage();
			
			if(isDoubleWielding(player)){
				inClip+=player.getHeldItemOffhand().getMaxDamage()-player.getHeldItemOffhand().getItemDamage();
			}
			result[1]=inClip+"/"+ammoLeft;
			
		}
		else{
			result[1]=Integer.toString(ammoLeft);
		}
		return result;
	}
	public int holdingMode(ItemStack stack, EntityLivingBase shooter) {
		return (int) TF2Attribute.getModifier("Charged Grenades", stack, 0, shooter);
	}
	
	public boolean shouldKeepCharged(ItemStack stack, EntityLivingBase shooter) {
		return false;
	}
	
	public float getProjectileSpeed(ItemStack stack, EntityLivingBase living) {
		return TF2Attribute.getModifier("Proj Speed", stack,
				ItemFromData.getData(stack).getFloat(PropertyType.PROJECTILE_SPEED), living);
	}
	
	public boolean isAmmoSufficient(ItemStack stack, EntityLivingBase living, boolean all) {
		
		//System.out.println((living.world.isRemote && living != ClientProxy.getLocalPlayer())+" "+ ((!this.hasClip(stack) || all) && !ItemAmmo.searchForAmmo(living, stack).isEmpty()) +" "+ (this.hasClip(stack) && stack.getItemDamage() < stack.getMaxDamage()));
		return (living.world.isRemote && living != ClientProxy.getLocalPlayer()) || (((!this.hasClip(stack) || all) && !this.searchForAmmo(living, stack).isEmpty())
				|| (this.hasClip(stack) && stack.getItemDamage() < stack.getMaxDamage()));
	}
	
	public boolean isItemStackDamageable()
    {
		return false;
    }
	
	public void setDamage(ItemStack stack, int damage)
    {
        super.setDamage(stack, damage);
        if(damage > this.getMaxDamage(stack))
        	stack.setItemDamage(this.getMaxDamage(stack));
    }
	
	public boolean canHeadshot(EntityLivingBase living, ItemStack stack) {
		// TODO Auto-generated method stub
		return living.getCapability(TF2weapons.WEAPONS_CAP, null).lastFire <= 0 && TF2Attribute.getModifier("Headshot", stack, 0, living) > 0;
	}
	
	public float getAdditionalGravity(EntityLivingBase living, ItemStack stack, double initial) {
		return TF2Attribute.getModifier("Gravity", stack, (float) initial, living);
	}
	
	public float getCharge(EntityLivingBase living, ItemStack stack) {
		if (living == null)
			return 0f;
		if (WeaponsCapability.get(living).lastHitCharge != 0)
			return WeaponsCapability.get(living).lastHitCharge;
		if (this.holdingMode(stack, living) == 0)
			return 0f;
		
		return MathHelper.clamp((float) WeaponsCapability.get(living).chargeTicks / (float) this.holdingMode(stack, living), 0f, 1f);
	}
}
