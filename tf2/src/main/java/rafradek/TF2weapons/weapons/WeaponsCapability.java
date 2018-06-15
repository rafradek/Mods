package rafradek.TF2weapons.weapons;

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
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.characters.EntityMedic;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.ItemToken;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.projectiles.EntityStickybomb;

public class WeaponsCapability implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	public static final int MAX_METAL=200;
	public static final int MAX_METAL_ENGINEER=500;
	
	public EntityLivingBase owner;
	public int state;
	public int minigunTicks;
	public int fire1Cool;
	public int fire2Cool;
	public int reloadCool;
	public int lastFire;
	//public int critTime;
	//public int healTarget = -1;
	public boolean mainHand;
	public HashMap<String, Integer> effectsCool = new HashMap<String, Integer>();
	public int chargeTicks;
	//public boolean charging;
	public int critTimeCool;
	public Queue<TF2Message.PredictionMessage> predictionList = new ArrayDeque<TF2Message.PredictionMessage>();
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
		
		ItemStack stack = owner.getHeldItem(EnumHand.MAIN_HAND);
		/*
		 * if(itemProperties.get(client).get(player)==null){
		 * itemProperties.get(client).put(player, new NBTTagCompound()); }
		 */
		// player.getEntityData().setTag("TF2", tag);
		//this.zombieHuntTicks--;
		
		this.ticksBash--;
		this.itProtection--;
		this.fanCool--;
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
		if (!stack.isEmpty() && stack.getItem() instanceof ItemUsable) {
			ItemUsable item = (ItemUsable) stack.getItem();
			if (!(this.owner instanceof EntityPlayer) || (this.owner.world.isRemote && this.owner != ClientProxy.getLocalPlayer()))
				item.onUpdate(stack, owner.world, owner, 0, true);
			WeaponData.WeaponDataCapability stackcap = stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null);
			if(TF2Attribute.getModifier("Focus", stack, 0, owner)!=0){
				this.focusShotTicks+=this.owner.isSprinting()?0:1;
				//System.out.println("Focus: "+this.focusShotTicks);
				this.focusShotRemaining--;
			}
			// if(!client)
			// System.out.println(client+" rel "
			// +item.getTagCompound().getShort("reload")+" "+state+"
			// "+item.getDisplayName());
			if (this.reloadCool > 0)
				this.reloadCool -= 50;
			if (this.fire1Cool > 0)
				this.fire1Cool -= 50;
			if (this.fire2Cool > 0)
				this.fire2Cool -= 50;
			if (stackcap.active == 1) {
				if (!this.lastWeapon.isEmpty()) {
					this.fire1Cool += (TF2Attribute.getModifier("Holster Time", this.lastWeapon, 1f, this.owner) - 1f)*item.getDeployTime(stack, owner);
					this.lastWeapon = ItemStack.EMPTY;
				}
				if (this.fire1Cool <= 0) {
					stackcap.active = 2;
					if (item.isDoubleWielding(owner))
						owner.getHeldItemOffhand().getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active = 2;
					item.draw(this, stack, owner, owner.world);
	
					if ((this.state & 3) > 0) {
						this.state = this.state & 7;
						if ((this.state & 3) > 0)
							item.startUse(stack, owner, owner.world, 0, this.state & 3);
					}
				}
			}
			if (owner.world.isRemote)
				// ((EntityPlayerMP)player).isChangingQuantityOnly=state>0;
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
			if (TF2Util.canInteract(owner))
				this.stateDo(owner, stack);

			if((state & 4) == 4 && stack.getItem() instanceof ItemWeapon && !this.knockbackActive && this.getKnockbackRage() >= 1f) {
				this.knockbackActive = true;
			}
			
			if ((!owner.world.isRemote || owner != Minecraft.getMinecraft().player)
					&& stack.getItem() instanceof ItemWeapon && ((ItemWeapon) stack.getItem()).hasClip(stack)
					&& (!item.searchForAmmo(owner, stack).isEmpty()
							|| owner.world.isRemote)) {
				if (((state & 4) != 0 || stack.getItemDamage() == stack.getMaxDamage()) && (state & 8) == 0
						&& stack.getItemDamage() != 0 && this.reloadCool <= 0
						&& (this.fire1Cool <= 0 || ((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack))
						&& owner.getActivePotionEffect(TF2weapons.stun) == null) {
					state += 8;
					this.reloadCool = ((ItemWeapon) stack.getItem()).getWeaponFirstReloadTime(stack, owner);

					if (!owner.world.isRemote && owner instanceof EntityPlayerMP)
						TF2weapons.network.sendTo(
								new TF2Message.UseMessage(stack.getItemDamage(), true, -1, EnumHand.MAIN_HAND),
								(EntityPlayerMP) owner);

					if (owner.world.isRemote && ((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack))
						TF2weapons.proxy.playReloadSound(owner, stack);

				} else if (this.fire1Cool <= 0 || ((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack))
					while ((state & 8) != 0 && this.reloadCool <= 0 && stack.getItemDamage() != 0) {
						// System.out.println("On client:
						// "+owner.world.isRemote);
						int maxAmmoUse = item.getAmmoAmount(owner, stack);
						int consumeAmount = 0;

						if (((ItemWeapon) stack.getItem()).IsReloadingFullClip(stack)) {
							consumeAmount += Math.min(stack.getItemDamage(), maxAmmoUse);
							stack.setItemDamage(Math.max(0, stack.getItemDamage() - consumeAmount));
							maxAmmoUse -= consumeAmount;

							if (maxAmmoUse > 0 && item.isDoubleWielding(owner)) {
								consumeAmount += Math.min(owner.getHeldItemOffhand().getItemDamage(), maxAmmoUse);
								owner.getHeldItemOffhand()
										.setItemDamage(Math.max(0, stack.getItemDamage() - consumeAmount));
							}

						} else {
							consumeAmount = 1;
							stack.setItemDamage(stack.getItemDamage() - 1);
							TF2weapons.proxy.playReloadSound(owner, stack);
						}
						if (!owner.world.isRemote)
							item.consumeAmmoGlobal(owner, stack, consumeAmount);
						if (!owner.world.isRemote && owner instanceof EntityPlayerMP)
							TF2weapons.network.sendTo(
									new TF2Message.UseMessage(stack.getItemDamage(), true, -1,EnumHand.MAIN_HAND),
									(EntityPlayerMP) owner);

						this.reloadCool += ((ItemWeapon) stack.getItem()).getWeaponReloadTime(stack, owner);

						if (stack.getItemDamage() == 0) {
							state -= 8;
							this.reloadCool = 0;
						}
					}
			} else
				state &= 7;
		} else {
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

	/*
	 * public static void tick(boolean client){
	 * 
	 * Map<EntityLivingBase,Integer>
	 * map=TF2ActionHandler.playerAction.get(client);
	 * 
	 * if(map.isEmpty()) return; Iterator<EntityLivingBase>
	 * iterator=map.keySet().iterator(); while(iterator.hasNext()) {
	 * EntityLivingBase player = iterator.next();
	 * 
	 * if(player.isDead||player.deathTime>0){
	 * 
	 * iterator.remove(); continue; } int state=0; if(map.get(player)!=null){
	 * state=map.get(player); }
	 * 
	 * ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
	 * 
	 * WeaponsCapability cap=player.getCapability(TF2weapons.WEAPONS_CAP, null);
	 * 
	 * cap.lastFire-=50; if (!stack.isEmpty()&&stack.getItem() instanceof
	 * ItemUsable) { ItemUsable item=(ItemUsable) stack.getItem();
	 * 
	 * //if(!client) //System.out.println(client+" rel "
	 * +item.getTagCompound().getShort("reload")+" "+state+" "+item.
	 * getDisplayName()); if (cap.reloadCool > 0) { cap.reloadCool-=50; } if
	 * (cap.fire1Cool > 0) { cap.fire1Cool-=50; } if (cap.fire2Cool > 0) {
	 * cap.fire2Cool-=50; }
	 * 
	 * if(client){ //((EntityPlayerMP)player).isChangingQuantityOnly=state>0;
	 * stack.animationsToGo=0; } if(player instanceof EntityTF2Character){
	 * EntityTF2Character shooter=((EntityTF2Character)player);
	 * if(shooter.getAttackTarget() != null){
	 * shooter.targetPrevPos[1]=shooter.targetPrevPos[0];
	 * shooter.targetPrevPos[3]=shooter.targetPrevPos[2];
	 * shooter.targetPrevPos[5]=shooter.targetPrevPos[4];
	 * shooter.targetPrevPos[0]=shooter.getAttackTarget().posX;
	 * shooter.targetPrevPos[2]=shooter.getAttackTarget().posY;
	 * shooter.targetPrevPos[4]=shooter.getAttackTarget().posZ; } }
	 * 
	 * state=stateDo(player, stack, state,cap, client);
	 * if((!client||player!=Minecraft.getMinecraft().player)&&stack.getItem()
	 * instanceof ItemRangedWeapon &&
	 * ((ItemRangedWeapon)stack.getItem()).hasClip(stack)){
	 * if(((state&4)!=0||stack.getItemDamage()==stack.getMaxDamage())&&(state&8)
	 * ==0&&stack.getItemDamage()!=0&& cap.reloadCool<=0
	 * &&(cap.fire1Cool<=0||((ItemRangedWeapon)stack.getItem()).
	 * IsReloadingFullClip(stack))){ state+=8;
	 * 
	 * cap.reloadCool=((ItemRangedWeapon)stack.getItem()).
	 * getWeaponFirstReloadTime(stack,player);
	 * 
	 * if(!client&&player instanceof EntityPlayerMP)
	 * TF2weapons.network.sendTo(new
	 * TF2Message.UseMessage(stack.getItemDamage(), true,EnumHand.MAIN_HAND),
	 * (EntityPlayerMP) player);
	 * 
	 * if(client&&((ItemRangedWeapon)stack.getItem()).IsReloadingFullClip(stack)
	 * ){ TF2weapons.proxy.playReloadSound(player,stack); }
	 * 
	 * } else if(cap.fire1Cool<=0||((ItemRangedWeapon)stack.getItem()).
	 * IsReloadingFullClip(stack)){ while ((state&8)!=0&&cap.reloadCool <= 0 &&
	 * stack.getItemDamage()!=0) {
	 * if(((ItemRangedWeapon)stack.getItem()).IsReloadingFullClip(stack)){
	 * stack.setItemDamage(0); if(item.isDoubleWielding(player)){
	 * player.getHeldItemOffhand().setItemDamage(0); } } else {
	 * stack.setItemDamage(stack.getItemDamage() - 1);
	 * TF2weapons.proxy.playReloadSound(player,stack); } if(!client&&player
	 * instanceof EntityPlayerMP) TF2weapons.network.sendTo(new
	 * TF2Message.UseMessage(stack.getItemDamage(), true,EnumHand.MAIN_HAND),
	 * (EntityPlayerMP) player);
	 * 
	 * cap.reloadCool+=((ItemRangedWeapon)stack.getItem()).getWeaponReloadTime(
	 * stack,player);
	 * 
	 * if(stack.getItemDamage()==0){ state-=8; cap.reloadCool=0; } } } } } else
	 * { player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).
	 * removeModifier(ItemMinigun.slowdown);
	 * player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).
	 * removeModifier(ItemSniperRifle.slowdown); } map.put(player, state); } }
	 */
	public boolean shouldShoot(EntityLivingBase player, int state) {
		return (!(!player.world.isRemote
				&& player instanceof EntityPlayer
				&& (this.predictionList.isEmpty()
						|| (this.predictionList.peek() != null && (this.predictionList.peek().state & state) != state)))
				&& !((player.world.isRemote || !(player instanceof EntityPlayer)) && (this.state & state) != state));
	}

	public void stateDo(EntityLivingBase player, ItemStack stack) {
		ItemUsable item = (ItemUsable) stack.getItem();

		if ((this.state & 1) != 0 && stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active == 2)

			item.fireTick(stack, player, player.world);
		while (this.fire1Cool <= 0 && shouldShoot(player, 1)) {

		
			TF2Message.PredictionMessage message = null;
			if (!player.world.isRemote && player instanceof EntityPlayer)
				message = this.predictionList.poll();

			if (message == null) {
				boolean canFire = item.canFire(player.world, player, stack);
				if (item.isDoubleWielding(player) && ((ItemUsable) player.getHeldItemOffhand().getItem()).canFire(
						player.world, player, player.getHeldItemOffhand()) && (this.lastFire > 0 || !canFire))
					this.mainHand = !this.mainHand;
				else
					this.mainHand = true;
				if (this.mainHand && !canFire)
					break;
			} else {
				
				this.mainHand = message.hand == EnumHand.MAIN_HAND;
				if (!((ItemUsable) player.getHeldItem(message.hand).getItem()).canFire(player.world, player,
						player.getHeldItem(message.hand)))
					break;
			}
			this.fire1Cool += Math.max(1,item.getFiringSpeed(stack, player));

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
						player.rotationYawHead, 1, this.mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

			item.use(this.mainHand ? stack : player.getHeldItemOffhand(), player, player.world,
					this.mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, message);

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
			if (stack.getItem() instanceof ItemWeapon) {
				this.reloadCool = 0;
				if ((this.state & 8) != 0)
					this.state -= 8;
			}
			
		}
		if ((this.state & 2) != 0 && stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active == 2)
			((ItemUsable) stack.getItem()).altFireTick(stack, player, player.world);
		while (this.fire2Cool <= 0 && shouldShoot(player, 2)) {

			// System.out.println("PLAJERRRR: "+player);
			TF2Message.PredictionMessage message = null;
			if (!player.world.isRemote && player instanceof EntityPlayer)
				message = this.predictionList.poll();

			if (item.getAltFiringSpeed(stack, player) == Short.MAX_VALUE
					|| !item.canAltFire(player.world, player, stack))
				break;
			this.fire2Cool += item.getAltFiringSpeed(stack, player);

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
				if ((this.state & 8) != 0)
					this.state -= 8;
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
}
