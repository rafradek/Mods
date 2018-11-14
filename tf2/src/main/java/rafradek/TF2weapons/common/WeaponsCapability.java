package rafradek.TF2weapons.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.entity.mercenary.EntityEngineer;
import rafradek.TF2weapons.entity.mercenary.EntityMedic;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.projectile.EntityStickybomb;
import rafradek.TF2weapons.item.ItemHuntsman;
import rafradek.TF2weapons.item.ItemMinigun;
import rafradek.TF2weapons.item.ItemParachute;
import rafradek.TF2weapons.item.ItemSapper;
import rafradek.TF2weapons.item.ItemSniperRifle;
import rafradek.TF2weapons.item.ItemToken;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.TF2Util;
import rafradek.TF2weapons.util.WeaponData;

public class WeaponsCapability implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	public static final int MAX_METAL=200;
	public static final int MAX_METAL_ENGINEER=500;
	
	public EntityLivingBase owner;
	public int state;
	public int minigunTicks;
	private int fire1Cool;
	private int fire2Cool;
	public EnumHand reloadingHand;
	public int reloadCool;
	public int lastFire;
	//public int critTime;
	//public int healTarget = -1;
	public boolean mainHand;
	public HashMap<String, Integer> effectsCool = new HashMap<String, Integer>();
	public int chargeTicks;
	//public boolean charging;
	public int critTimeCool;
	public TF2Message.PredictionMessage[] predictionList = new TF2Message.PredictionMessage[8];
	public float recoil;
	public int invisTicks;
	public int disguiseTicks;
	public boolean pressedStart;
	public boolean doubleJumped;
	public EntitySentry controlledSentry;
	public ResourceLocation skinDisguise;
	public boolean skinRetrieved;
	public String lastDisguiseValue;
	public boolean lastDisgused;
	public String skinType;
	//public int zombieHuntTicks;
	public int ticksBash;
	public boolean bashCritical;
	//public int collectedHeads;
	public int collectedHeadsTime;
	public boolean wornEye;
	public int killsSpinning;
	//public int cratesOpened;
	//public float dodgedDmg;
	public int tickAirblasted;
	//public int killsAirborne;
	public int itProtection;
	public int killsAirborne;
	public int focusShotTicks;
	public int focusShotRemaining;
	public int fanCool;
	public boolean knockbackActive;
	public boolean appliedMouseSlow;
	public int hitNoMiss;
	public int sentryTargets = 5;
	public boolean dispenserPlayer;
	public boolean teleporterPlayer;
	public boolean teleporterEntity;
	public boolean forcedClass;
	
	public float lastHitCharge;
	public EntityDataManager dataManager;
	
	public EntityLivingBase entityDisguise;
	
	public ArrayList<EntityStickybomb> activeBomb= new ArrayList<>();
	public float oldFactor;
	public int expJumpGround;
	public double lastPosX;
	public double lastPosY;
	public double lastPosZ;
	public ItemStack lastWeapon = ItemStack.EMPTY;
	
	public long ticksTotal;
	public boolean fireCoolReduced;
	
	private static final DataParameter<Boolean> EXP_JUMP = new DataParameter<Boolean>(6, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> CHARGING = new DataParameter<Boolean>(11, DataSerializers.BOOLEAN);
	private static final DataParameter<String> DISGUISE_TYPE = new DataParameter<String>(7, DataSerializers.STRING);
	private static final DataParameter<Boolean> DISGUISED = new DataParameter<Boolean>(8, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> INVIS = new DataParameter<Boolean>(9, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> FEIGN = new DataParameter<Boolean>(10, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> CRIT_TIME= new DataParameter<Integer>(0, DataSerializers.VARINT);
	private static final DataParameter<Integer> HEADS= new DataParameter<Integer>(1, DataSerializers.VARINT);
	private static final DataParameter<Integer> HEAL_TARGET= new DataParameter<Integer>(2, DataSerializers.VARINT);
	private static final DataParameter<Integer> METAL= new DataParameter<Integer>(3, DataSerializers.VARINT);
	private static final DataParameter<Float> PHLOG_RAGE= new DataParameter<Float>(4, DataSerializers.FLOAT);
	private static final DataParameter<Float> KNOCKBACK_RAGE= new DataParameter<Float>(5, DataSerializers.FLOAT);
	private static final DataParameter<Integer> TOKEN_USED= new DataParameter<Integer>(12, DataSerializers.VARINT);
	//public int killsSpinning;
	
	/*public HashMap<Class<? extends Entity>, Short> highestBossLevel = new HashMap<>();
	public int nextBossTicks;
	public int stickybombKilled;
	public boolean engineerKilled;
	public boolean sentryKilled;
	public boolean dispenserKilled;
	public int[] cachedAmmoCount=new int[15];
	public int headshotsRow;
	public int sapperTime;
	public EntityLivingBase buildingOwnerKill;*/

	public WeaponsCapability(EntityLivingBase entity) {
		this.owner = entity;

		this.dataManager = new EntityDataManager(entity);
		this.dataManager.register(CRIT_TIME, 0);
		this.dataManager.register(HEADS, 0);
		this.dataManager.register(HEAL_TARGET, -1);
		this.dataManager.register(PHLOG_RAGE, 0f);
		this.dataManager.register(KNOCKBACK_RAGE, 0f);
		this.dataManager.register(METAL, MAX_METAL);
		this.dataManager.register(FEIGN, false);
		this.dataManager.register(INVIS, false);
		this.dataManager.register(DISGUISED, false);
		this.dataManager.register(DISGUISE_TYPE, "");
		this.dataManager.register(EXP_JUMP, false);
		this.dataManager.register(CHARGING, false);
		this.dataManager.register(TOKEN_USED, -1);
		//this.nextBossTicks = (int) (entity.world.getWorldTime() + entity.getRNG().nextInt(360000));
	}

	public int getCritTime() {
		return this.dataManager.get(CRIT_TIME);
	}
	
	public void setCritTime(int time) {
		this.dataManager.set(CRIT_TIME, time);
	}
	
	public int getHealTarget() {
		return this.dataManager.get(HEAL_TARGET);
	}
	
	public void setHealTarget(int target) {
		this.dataManager.set(HEAL_TARGET, target);
	}
	
	public int getHeads() {
		return this.dataManager.get(HEADS);
	}
	
	public int getMetal() {
		return this.dataManager.get(METAL);
	}
	public boolean hasMetal(int metal) {
		boolean hasIngot = this.owner instanceof EntityPlayer 
				&& TF2Util.hasEnoughItem(((EntityPlayer)this.owner).inventory, stackL -> stackL.getItem() == Items.IRON_INGOT, MathHelper.ceil((metal - this.getMetal())/ 50f));
		return hasIngot || this.getMetal() >= metal;
	}
	public int consumeMetal(int metal, boolean allowPartial) {
		int usedMetal = 0;
		
		if (this.owner instanceof EntityPlayer) {
			ItemStack ingot = new ItemStack(Items.IRON_INGOT);
			while(((allowPartial && this.getMetal() == 0) || (!allowPartial && this.getMetal() < metal)) && ((EntityPlayer)this.owner).inventory.hasItemStack(ingot)) {
				((EntityPlayer)this.owner).inventory.clearMatchingItems(Items.IRON_INGOT, 0, 1, null);
				this.setMetal(this.getMetal() + 50);
			}
		}
		if (allowPartial || this.getMetal() >= metal) {
			usedMetal = Math.min(this.getMetal(), metal);
			this.setMetal(this.getMetal() - usedMetal);
		}
		return usedMetal;
	}
	
	public void setMetal(int metal) {
		this.dataManager.set(METAL, MathHelper.clamp(metal,0,this.owner instanceof EntityEngineer?TF2ConfigVars.maxMetalEngineer:MAX_METAL));
	}
	
	public int getMaxMetal() {
		return this.owner instanceof EntityEngineer ? TF2ConfigVars.maxMetalEngineer : WeaponsCapability.MAX_METAL;
	}
	
	public float getPhlogRage() {
		return this.dataManager.get(PHLOG_RAGE);
	}
	
	public void setPhlogRage(float rage) {
		this.dataManager.set(PHLOG_RAGE, rage);
	}
	
	public float getKnockbackRage() {
		return this.dataManager.get(KNOCKBACK_RAGE);
	}
	
	public void setKnockbackRage(float rage) {
		this.dataManager.set(KNOCKBACK_RAGE, rage);
	}
	public boolean isInvisible() {
		return this.dataManager.get(INVIS);
	}
	
	public void setInvisible(boolean invis) {
		this.dataManager.set(INVIS, invis);
	}
	
	public void setDisguised(boolean val) {
		this.dataManager.set(DISGUISED, val);
	}
	
	public boolean isDisguised() {
		return this.dataManager.get(DISGUISED);
	}
	
	public void setExpJump(boolean val) {
		this.dataManager.set(EXP_JUMP, val);
		if(val) {
			this.expJumpGround = 2;
			TF2Util.sendTracking(new TF2Message.ActionMessage(28, this.owner), this.owner);
		}
	}
	
	public boolean isExpJump() {
		return this.dataManager.get(EXP_JUMP);
	}
	
	public void setDisguiseType(String val) {
		this.dataManager.set(DISGUISE_TYPE, val);
	}
	
	public String getDisguiseType() {
		return this.dataManager.get(DISGUISE_TYPE);
	}
	
	public void setFeign(boolean val) {
		this.dataManager.set(FEIGN, val);
	}
	
	public boolean isFeign() {
		return this.dataManager.get(FEIGN);
	}
	
	public void setCharging(boolean val) {
		this.dataManager.set(CHARGING, val);
		if(!val)
			this.chargeTicks = 0;
	}
	
	public boolean isCharging() {
		return this.dataManager.get(CHARGING);
	}
	
	public void setUsedToken(int val) {
		this.dataManager.set(TOKEN_USED, val);
	}
	
	public int getUsedToken() {
		return this.dataManager.get(TOKEN_USED);
	}
	
	public void addEffectCooldown(String name, int time) {
		this.effectsCool.put(name, time);
		if (this.owner instanceof EntityPlayerMP)
			TF2weapons.network.sendTo(new TF2Message.EffectCooldownMessage(name, time), (EntityPlayerMP) this.owner);
	}
	public void addHead(ItemStack weapon) {

		this.collectedHeadsTime = owner.ticksExisted;
		this.dataManager.set(HEADS, this.dataManager.get(HEADS) + 1);
		/*this.owner.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH)
				.applyModifier(new AttributeModifier(HEADS_HEALTH, "Heads modifier", collectedHeads, 0));
		this.owner.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED)
				.applyModifier(new AttributeModifier(HEADS_SPEED, "Heads modifier", collectedHeads * 0.04, 2));*/
		this.owner.heal(TF2Attribute.getModifier("Max Health Kill", weapon, 0, this.owner));
	}

	public boolean focusedShot(ItemStack stack){
		int stackLevel=(int) TF2Attribute.getModifier("Focus", stack, 0, owner);
		return stackLevel>0 && this.focusShotTicks>68-stackLevel*21+((ItemUsable)stack.getItem()).getFiringSpeed(stack, owner)/50;
	}
	
	public void onChangeValue(DataParameter<?> param, Object newValue) {
		if(param.getId() == 11 && !((Boolean)newValue)){
			this.chargeTicks = 0;
		}
		if(param.getId() == 6 && (Boolean)newValue)
			this.expJumpGround = 2;
	}
	
	public boolean isUsingParachute() {
		ItemStack stack = this.owner.getItemStackFromSlot(EntityEquipmentSlot.CHEST); 
		return stack.getItem() instanceof ItemParachute && stack.getTagCompound().getBoolean("Deployed");
	}
	
	public void tick() {
		// System.out.println("graczin"+state);
		Iterator<Entry<String, Integer>> iterator = effectsCool.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next();
			entry.setValue(entry.getValue() - 1);
			if (entry.getValue() <= 0)
				iterator.remove();
		}
		if (!this.owner.world.isRemote && this.dataManager.get(HEADS) > 0 && collectedHeadsTime < this.owner.ticksExisted - Math.max(100,2000 - MathHelper.log2(this.dataManager.get(HEADS))*300) ) {
			this.dataManager.set(HEADS, this.dataManager.get(HEADS) - 1);
			collectedHeadsTime = this.owner.ticksExisted;
			
			/*this.owner.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH)
					.applyModifier(new AttributeModifier(HEADS_HEALTH, "Heads modifier", collectedHeads, 0));
			this.owner.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED)
					.applyModifier(new AttributeModifier(HEADS_SPEED, "Heads modifier", collectedHeads * 0.04, 2));*/
		}
		
		
		/*
		 * if(itemProperties.get(client).get(player)==null){
		 * itemProperties.get(client).put(player, new NBTTagCompound()); }
		 */
		// player.getEntityData().setTag("TF2", tag);
		//this.zombieHuntTicks--;
		
		this.ticksBash--;
		this.itProtection--;
		this.fanCool--;
		this.ticksTotal++;
		this.lastFire -= 50;
		if(this.doubleJumped && this.owner.onGround){
			this.doubleJumped=false;
		}
		
		if (this.owner.isSprinting() && this.getUsedToken() >= 0){
			if(this.owner.onGround) {
				owner.motionX *= 0.82D;
	            owner.motionZ *= 0.82D;
			}
            //owner.setSprinting(false);
		}
		
		if (!this.owner.world.isRemote) {
			if (this.owner.ticksExisted % 20 == 0 && (this.owner instanceof EntityMedic || this.getUsedToken() == 6)) {
				int lastHitTime = this.owner.ticksExisted - this.owner.getEntityData().getInteger("lasthit");
				if (lastHitTime >= 120)
					this.owner.heal(0.6f);
				else if(lastHitTime >= 60)
					this.owner.heal(TF2Util.lerp(0.6f, 0.3f, (lastHitTime-60)/60f));
				else
					this.owner.heal(0.3f);
			}
		}
		
		if(this.owner.world.isRemote && this.owner == Minecraft.getMinecraft().player) {
			if(this.owner.getHeldItemMainhand().getItem() instanceof ItemSniperRifle && this.isCharging() && !this.appliedMouseSlow) {
				Minecraft.getMinecraft().gameSettings.mouseSensitivity *= 0.4f;
				this.appliedMouseSlow = true;
			}
			else if(!this.isCharging() && this.appliedMouseSlow) {
				Minecraft.getMinecraft().gameSettings.mouseSensitivity *= 2.5f;
				this.appliedMouseSlow = false;
			}
		}
		if(this.knockbackActive && this.getKnockbackRage() >= 0f && this.owner.ticksExisted % 2 == 0) {
			this.setKnockbackRage(this.getKnockbackRage() - 0.007f);
			this.knockbackActive = this.getKnockbackRage() > 0f;
		}
		if (this.reloadCool > 0)
			this.reloadCool -= 50;
		
		boolean hadItem = false;
		boolean continueReload = false;
		for (EnumHand hand: EnumHand.values()) {
			ItemStack stack = owner.getHeldItem(hand);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemUsable && (hand == EnumHand.MAIN_HAND || (hand == EnumHand.OFF_HAND && !hadItem 
					&& ((ItemUsable)stack.getItem()).getDoubleWieldBonus(lastWeapon, owner) != 1) || ItemUsable.isDoubleWielding(owner))) {
				hadItem = true;
				ItemUsable item = (ItemUsable) stack.getItem();
				if (!(this.owner instanceof EntityPlayer) || (this.owner.world.isRemote && this.owner != ClientProxy.getLocalPlayer()))
					item.onUpdate(stack, owner.world, owner, 0, true);
				WeaponData.WeaponDataCapability stackcap = stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null);
				if(TF2Attribute.getModifier("Focus", stack, 0, owner)!=0){
					this.focusShotTicks+=this.owner.isSprinting()?0:1;
					this.focusShotRemaining--;
				}
				if (stackcap.active == 1) {
					if (!this.lastWeapon.isEmpty()) {
						stackcap.fire1Cool = stackcap.fire2Cool =
								(int) Math.max(stackcap.fire1Cool, ((TF2Attribute.getModifier("Holster Time", this.lastWeapon, 1f, this.owner) - 1f)*item.getDeployTime(stack, owner)));
						
						this.lastWeapon = ItemStack.EMPTY;
					}
					if (stackcap.fire1Cool <= 0) {
						stackcap.active = 2;
						item.draw(this, stack, owner, owner.world);
		
						if ((this.state & 3) > 0) {
							this.state = this.state & 7;
							if ((this.state & 3) > 0)
								item.startUse(stack, owner, owner.world, 0, this.state & 3);
						}
					}
				}
				if (owner.world.isRemote)
					stack.setAnimationsToGo(0);
				if (owner instanceof EntityTF2Character) {
					EntityTF2Character shooter = ((EntityTF2Character) owner);
					if (shooter.getAttackTarget() != null) {
						shooter.targetPrevPos[1] = shooter.targetPrevPos[0];
						shooter.targetPrevPos[3] = shooter.targetPrevPos[2];
						shooter.targetPrevPos[5] = shooter.targetPrevPos[4];
						shooter.targetPrevPos[0] = shooter.getAttackTarget().posX;
						shooter.targetPrevPos[2] = shooter.getAttackTarget().posY;
						shooter.targetPrevPos[4] = shooter.getAttackTarget().posZ;
					}
				}
				
				this.stateDo(owner, stack, hand);
	
				if((state & 4) == 4 && stack.getItem() instanceof ItemWeapon && !this.knockbackActive && this.getKnockbackRage() >= 1f) {
					this.knockbackActive = true;
				}
				
				if ((!owner.world.isRemote || owner != Minecraft.getMinecraft().player)
						&& stack.getItem() instanceof ItemWeapon && ((ItemWeapon) stack.getItem()).hasClip(stack)
						&& (!item.searchForAmmo(owner, stack).isEmpty()
								|| owner.world.isRemote)) {
					if (((state & 4) != 0 || stack.getItemDamage() == stack.getMaxDamage() || continueReload) && this.reloadingHand == null
							&& stack.getItemDamage() != 0 && this.reloadCool <= 0
							&& (this.getPrimaryCooldown() <= 0 || ((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack))
							&& owner.getActivePotionEffect(TF2weapons.stun) == null) {
						this.reloadingHand = hand;
						this.reloadCool = ((ItemWeapon) stack.getItem()).getWeaponFirstReloadTime(stack, owner);
	
						if (!owner.world.isRemote && owner instanceof EntityPlayerMP)
							TF2weapons.network.sendTo(
									new TF2Message.UseMessage(stack.getItemDamage(), true, -1, hand),
									(EntityPlayerMP) owner);
	
						if (owner.world.isRemote && ((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack))
							TF2weapons.proxy.playReloadSound(owner, stack);
	
					} else if (this.getPrimaryCooldown() <= 0 || ((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack))
						while (this.reloadingHand == hand && this.reloadCool <= 0 && stack.getItemDamage() != 0) {
							// System.out.println("On client:
							// "+owner.world.isRemote);
							int maxAmmoUse = item.getAmmoAmount(owner, stack);
							int consumeAmount = 0;
	
							if (((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack)) {
								consumeAmount += Math.min(stack.getItemDamage(), maxAmmoUse);
								stack.setItemDamage(Math.max(0, stack.getItemDamage() - consumeAmount));
								maxAmmoUse -= consumeAmount;
	
								/*if (maxAmmoUse > 0 && ItemUsable.isDoubleWielding(owner)) {
									consumeAmount += Math.min(owner.getHeldItemOffhand().getItemDamage(), maxAmmoUse);
									owner.getHeldItemOffhand()
											.setItemDamage(Math.max(0, stack.getItemDamage() - consumeAmount));
								}*/
	
							} else {
								consumeAmount = 1;
								stack.setItemDamage(stack.getItemDamage() - 1);
								TF2weapons.proxy.playReloadSound(owner, stack);
							}
							
							if (!owner.world.isRemote)
								item.consumeAmmoGlobal(owner, stack, consumeAmount);
							if (!owner.world.isRemote && owner instanceof EntityPlayerMP)
								TF2weapons.network.sendTo(
										new TF2Message.UseMessage(stack.getItemDamage(), true, -1,hand),
										(EntityPlayerMP) owner);
	
							this.reloadCool += ((ItemWeapon) stack.getItem()).getWeaponReloadTime(stack, owner);
	
							if (stack.getItemDamage() == 0) {
								this.reloadingHand = null;
								continueReload =true;
								this.reloadCool = 0;
							}
						}
				} else if (this.reloadingHand == hand)
					this.reloadingHand = null;
			}
		}
		if (!hadItem) {
			owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ItemMinigun.slowdown);
			owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ItemSniperRifle.slowdown);
			owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ItemHuntsman.slowdown);
		}
		
		if (!this.owner.world.isRemote && this.dataManager.isDirty()) {
			TF2Util.sendTracking(new TF2Message.CapabilityMessage(this.owner, false), this.owner);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return TF2weapons.WEAPONS_CAP != null && capability == TF2weapons.WEAPONS_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (TF2weapons.WEAPONS_CAP != null && capability == TF2weapons.WEAPONS_CAP)
			return TF2weapons.WEAPONS_CAP.cast(this);
		return null;
	}

	public boolean startedPress() {
		if (this.pressedStart) {
			this.pressedStart = false;
			return true;
		}
		return false;

	}

	public boolean shouldShoot(EntityLivingBase player, int state, EnumHand hand) {
		return (!(!player.world.isRemote
				&& player instanceof EntityPlayer
				&& this.predictionList[hand.ordinal() + (state == 1 ? 0 : 2)] == null))
				&& !((player.world.isRemote || !(player instanceof EntityPlayer)) && (this.state & state) != state);
	}

	public void stateDo(EntityLivingBase player, ItemStack stack, EnumHand hand) {
		if (!TF2Util.canInteract(player))
			return;
		
		ItemUsable item = (ItemUsable) stack.getItem();
		WeaponData.WeaponDataCapability stackcap = WeaponData.getCapability(stack);

		if ((this.state & 1) != 0 && stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active == 2)

			item.fireTick(stack, player, player.world);
		while (stackcap.fire1Cool <= 0 && shouldShoot(player, 1, hand)) {

		
			TF2Message.PredictionMessage message = null;
			if (!player.world.isRemote && player instanceof EntityPlayer) {
				message = this.predictionList[hand.ordinal()];
				this.predictionList[hand.ordinal()] = null;
			}

			if (message == null) {
				boolean canFire = item.canFire(player.world, player, stack);
				/*if (ItemUsable.isDoubleWielding(player) && ((ItemUsable) player.getHeldItemOffhand().getItem()).canFire(
						player.world, player, player.getHeldItemOffhand()) && (this.lastFire > 0 || !canFire))
					this.mainHand = !this.mainHand;
				else*/
					//this.mainHand = true;
				if ( !canFire)
					break;
			} else {
				
				this.mainHand = message.hand == EnumHand.MAIN_HAND;
				if (message.hand != hand || !item.canFire(player.world, player, stack))
					break;
			}
			stackcap.fire1Cool += Math.max(1,item.getFiringSpeed(stack, player));

			if (player instanceof EntityTF2Character)
				((EntityTF2Character) player).onShot();

			if (this.isDisguised() && !(item instanceof ItemSapper))
				TF2EventsCommon.disguise(player, false);

			double oldX = player.posX, oldY = player.posY, oldZ = player.posZ;
			float oldPitch = player.rotationPitch, oldYaw = player.rotationYawHead;

			if (!player.world.isRemote && player instanceof EntityPlayer)
				this.preparePlayerPrediction(player, message);
			if (player.world.isRemote && player == ClientProxy.getLocalPlayer())
				message = new TF2Message.PredictionMessage(player.posX, player.posY, player.posZ, player.rotationPitch,
						player.rotationYawHead, 1, hand);

			item.use(stack, player, player.world,
					hand, message);

			player.removePotionEffect(TF2weapons.charging);

			if (player.world.isRemote && player == ClientProxy.getLocalPlayer()) {
				// System.out.println("Shoot Res: "+message.target);
				TF2weapons.network.sendToServer(message);
				/*TF2UdpClient client = TF2UdpClient.instance;
				PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
				buffer.writeShort(player.getCapability(TF2weapons.PLAYER_CAP, null).udpServerId);
				buffer.writeShort(0);
				buffer.writeByte(0);
				buffer.writeLong(System.currentTimeMillis());
				TF2weapons.network.sendToServer(new TF2Message.AttackSyncMessage(System.currentTimeMillis()));
				client.channel.writeAndFlush(new DatagramPacket(buffer, client.address));*/
			}

			player.posX = oldX;
			player.posY = oldY;
			player.posZ = oldZ;
			player.rotationYawHead = oldYaw;
			player.rotationPitch = oldPitch;

			/*
			 * if(!player.world.isRemote && player instanceof EntityPlayer){
			 * this.predictionList.remove(0);
			 * System.out.println(this.predictionList.size()); }
			 */

			this.lastFire = 1250;
			this.fireCoolReduced = false;
			if (stack.getItem() instanceof ItemWeapon) {
				this.reloadCool = 0;
				this.reloadingHand = null;
			}
			
		}
		if ((this.state & 2) != 0 && stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active == 2)
			((ItemUsable) stack.getItem()).altFireTick(stack, player, player.world);
		while (stackcap.fire2Cool <= 0 && shouldShoot(player, 2, hand)) {

			// System.out.println("PLAJERRRR: "+player);
			TF2Message.PredictionMessage message = null;
			if (!player.world.isRemote && player instanceof EntityPlayer) {
				message = this.predictionList[hand.ordinal() + 2];
				this.predictionList[hand.ordinal() + 2] = null;
			}

			if (item.getAltFiringSpeed(stack, player) == Short.MAX_VALUE
					|| !item.canAltFire(player.world, player, stack))
				break;
			stackcap.fire2Cool += item.getAltFiringSpeed(stack, player);

			if (this.isDisguised())
				TF2EventsCommon.disguise(player, false);

			double oldX = player.posX, oldY = player.posY, oldZ = player.posZ;
			float oldPitch = player.rotationPitch, oldYaw = player.rotationYawHead;

			if (!player.world.isRemote && player instanceof EntityPlayer)
				this.preparePlayerPrediction(player, message);

			((ItemUsable) stack.getItem()).altUse(stack, player, player.world);

			player.removePotionEffect(TF2weapons.charging);
			if (player.world.isRemote && player == ClientProxy.getLocalPlayer())
				TF2weapons.network.sendToServer(new TF2Message.PredictionMessage(player.posX, player.posY, player.posZ,
						player.rotationPitch, player.rotationYawHead, 2, EnumHand.MAIN_HAND));

			/*
			 * if(!player.world.isRemote && player instanceof EntityPlayer){
			 * this.predictionList.remove(0);
			 * System.out.println(this.predictionList.size()); }
			 */
			player.posX = oldX;
			player.posY = oldY;
			player.posZ = oldZ;
			player.rotationYawHead = oldYaw;
			player.rotationPitch = oldPitch;

			if (!player.world.isRemote)
				ItemUsable.sps++;
			if (stack.getItem() instanceof ItemWeapon) {
				this.reloadCool = 0;
				this.reloadingHand = null;
			}
		}

	}

	public void preparePlayerPrediction(EntityLivingBase player, TF2Message.PredictionMessage message) {
		player.posX = message.x;
		player.posY = message.y;
		player.posZ = message.z;
		player.rotationYawHead = message.yaw;
		player.rotationPitch = message.pitch;
		this.mainHand = message.hand == EnumHand.MAIN_HAND;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByte("VisTicks", (byte) this.invisTicks);
		tag.setByte("DisguiseTicks", (byte) this.disguiseTicks);
		NBTTagCompound cld = new NBTTagCompound();
		for (Entry<String, Integer> entry : this.effectsCool.entrySet())
			cld.setInteger(entry.getKey(), entry.getValue());
		tag.setTag("Cooldowns", cld);
		tag.setInteger("HealTarget", this.getHealTarget());
		tag.setShort("Heads", this.dataManager.get(HEADS).shortValue());
		tag.setShort("HeadsCool", (short) (this.collectedHeadsTime - this.owner.ticksExisted));
		tag.setBoolean("Cloaked", this.isInvisible());
		tag.setBoolean("Disguised", this.isDisguised());
		tag.setString("DisguiseType", this.getDisguiseType());
		tag.setInteger("KillsSpinning", this.killsSpinning);
		tag.setInteger("FocusedShot", this.focusShotTicks);
		tag.setInteger("KnockbackFANCool", this.fanCool);
		tag.setInteger("Metal", this.getMetal());
		tag.setFloat("Phlog", this.getPhlogRage());
		tag.setFloat("Knockback", this.getKnockbackRage());
		tag.setLong("TicksTotal", this.ticksTotal);
		if (this.forcedClass)
			tag.setByte("Token", (byte) this.getUsedToken());
		//tag.setBoolean("Uber", this.owner.getDataManager().get(TF2EventBusListener.ENTITY_UBER));
		//tag.setFloat("DodgedDmg", this.dodgedDmg);
		//tag.setInteger("KillsSpinning", this.killsSpinning);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.invisTicks = nbt.getByte("VisTicks");
		this.disguiseTicks = nbt.getByte("DisguiseTicks");
		this.setHealTarget(nbt.getInteger("HealTarget"));
		NBTTagCompound cld = nbt.getCompoundTag("Cooldowns");
		for (String key : cld.getKeySet())
			this.effectsCool.put(key, cld.getInteger(key));
		this.dataManager.set(HEADS, (int) nbt.getShort("Heads"));
		this.collectedHeadsTime = nbt.getShort("HeadsCool");
		this.setInvisible(nbt.getBoolean("Cloaked"));
		this.setDisguised(nbt.getBoolean("Disguised"));
		//this.owner.getDataManager().set(TF2EventBusListener.ENTITY_UBER, nbt.getBoolean("Uber"));
		this.setDisguiseType(nbt.getString("DisguiseType"));
		if (this.isDisguised())
			TF2EventsCommon.disguise(this.owner, true);
		this.killsSpinning=nbt.getInteger("KillsSpinning");
		this.focusShotTicks=nbt.getInteger("FocusedShot");
		this.fanCool=nbt.getInteger("KnockbackFANCool");
		//this.dodgedDmg=nbt.getFloat("DodgedDmg");
		this.setPhlogRage(nbt.getFloat("Phlog"));
		this.setMetal(nbt.getInteger("Metal"));
		this.ticksTotal = nbt.getLong("TicksTotal");
		this.setKnockbackRage(nbt.getFloat("KnockbackRage"));
		if(nbt.hasKey("Token")) {
			((ItemToken)TF2weapons.itemToken).updateAttributes(new ItemStack(TF2weapons.itemToken, 1, nbt.getByte("Token")), this.owner);
			this.forcedClass = true;
		}
		//this.killsSpinning=nbt.getInteger("KillsSpinning");
	}
	
	public static EntityDataManager getDataManager(Entity ent) {
		return ent.getCapability(TF2weapons.WEAPONS_CAP, null).dataManager;
	}
	
	public static WeaponsCapability get(Entity ent) {
		return ent.getCapability(TF2weapons.WEAPONS_CAP, null);
	}

	public int getPrimaryCooldown() {
		return Math.max(this.getPrimaryCooldown(EnumHand.MAIN_HAND),this.getPrimaryCooldown(EnumHand.OFF_HAND));
	}

	public void setPrimaryCooldown(int fire1Cool) {
		this.setPrimaryCooldown(EnumHand.MAIN_HAND, fire1Cool);
		this.setPrimaryCooldown(EnumHand.OFF_HAND, fire1Cool);
	}
	
	public int getPrimaryCooldown(EnumHand hand) {
		if (this.owner.getHeldItem(hand).getItem() instanceof ItemUsable)
			return this.owner.getHeldItem(hand).getCapability(TF2weapons.WEAPONS_DATA_CAP, null).fire1Cool;
		else 
			return 0;
	}

	public void setPrimaryCooldown(EnumHand hand, int fire1Cool) {
		if (this.owner.getHeldItem(hand).hasCapability(TF2weapons.WEAPONS_DATA_CAP, null))
			this.owner.getHeldItem(hand).getCapability(TF2weapons.WEAPONS_DATA_CAP, null).fire1Cool = fire1Cool;
	}
	
	public int getSecondaryCooldown() {
		return Math.max(this.getSecondaryCooldown(EnumHand.MAIN_HAND),this.getSecondaryCooldown(EnumHand.OFF_HAND));
	}

	public void setSecondaryCooldown(int fire1Cool) {
		this.setSecondaryCooldown(EnumHand.MAIN_HAND, fire1Cool);
		this.setSecondaryCooldown(EnumHand.OFF_HAND, fire1Cool);
	}
	
	public int getSecondaryCooldown(EnumHand hand) {
		if (this.owner.getHeldItem(hand).getItem() instanceof ItemUsable)
			return this.owner.getHeldItem(hand).getCapability(TF2weapons.WEAPONS_DATA_CAP, null).fire2Cool;
		else 
			return 0;
	}

	public void setSecondaryCooldown(EnumHand hand, int fire1Cool) {
		if (this.owner.getHeldItem(hand).hasCapability(TF2weapons.WEAPONS_DATA_CAP, null))
			this.owner.getHeldItem(hand).getCapability(TF2weapons.WEAPONS_DATA_CAP, null).fire2Cool = fire1Cool;
	}
}
