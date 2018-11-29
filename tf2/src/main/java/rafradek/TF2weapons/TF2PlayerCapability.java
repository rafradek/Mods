package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Achievements;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.entity.mercenary.EntityMedic;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character.Order;
import rafradek.TF2weapons.item.ItemAmmo;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemPDA;
import rafradek.TF2weapons.item.ItemWrench;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.Contract;
import rafradek.TF2weapons.util.PlayerPersistStorage;
import rafradek.TF2weapons.util.TF2Util;
import rafradek.TF2weapons.util.Contract.Objective;
import rafradek.TF2weapons.util.WeaponData.PropertyType;

public class TF2PlayerCapability implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	
	public EntityPlayer owner;
	//public ArrayList<TF2Message.PredictionMessage> predictionList = new ArrayList<TF2Message.PredictionMessage>();
	public boolean pressedStart;
	public int zombieHuntTicks;
	public boolean wornEye;
	public int cratesOpened;
	public float dodgedDmg;
	public int tickAirblasted;
	
	//public int itProtection;
	
	public ItemStackHandler lostItems;
	public HashMap<Class<? extends Entity>, Short> highestBossLevel = new HashMap<>();
	public int nextBossTicks;
	public int stickybombKilled;
	public boolean engineerKilled;
	public boolean sentryKilled;
	public boolean dispenserKilled;
	public int[] cachedAmmoCount=new int[ItemAmmo.AMMO_TYPES.length];
	public int sapperTime;
	public int headshotsRow;
	public EntityLivingBase buildingOwnerKill;
	public MovementInput lastMovementInput;
	public ArrayList<Contract> contracts= new ArrayList<>();
	public int mercenariesKilled;
	public int nextContractDay=-1;
	public boolean newContracts;
	public boolean newRewards;
	public int fastKillTimer;
	public float healed;
	public boolean sendContractsNextTick;
	public EntityLivingBase lastMedic;
	
	public short udpServerId;
	public int medicCall;
	public boolean medicCharge;
	public boolean breakBlocks;
	public boolean blockUse;
	public float robotsKilledInvasion;
	
	public NBTTagCompound carrying;
	public int carryingType;
	public int maxInvasionBeaten;
	@SuppressWarnings("unchecked")
	public Multimap<String, AttributeModifier>[] wearablesAttrib= (Multimap<String, AttributeModifier>[]) new Multimap[4];
	
	public TF2PlayerCapability(EntityPlayer entity) {
		this.owner = entity;
		this.lostItems=new ItemStackHandler(27);
		this.nextBossTicks = (int) (entity.world.getWorldTime() + entity.getRNG().nextInt(360000));
		
	}

	public void tick() {
		if(Float.isNaN(this.owner.getHealth())) {
			this.owner.setHealth(this.owner.getMaxHealth());
			this.owner.setAbsorptionAmount(0);
		}
		// System.out.println("graczin"+state);
		//ItemStack stack = owner.getHeldItem(EnumHand.MAIN_HAND);
		/*
		 * if(itemProperties.get(client).get(player)==null){
		 * itemProperties.get(client).put(player, new NBTTagCompound()); }
		 */
		// player.getEntityData().setTag("TF2", tag);
		this.zombieHuntTicks--;
		this.sapperTime--;
		
		if(this.fastKillTimer>0)
			this.fastKillTimer--;
		if(this.dodgedDmg>0&&this.owner.getActivePotionEffect(TF2weapons.bonk)==null){
			this.dodgedDmg=0;
		}
		if(!this.owner.world.isRemote) {
			
			if (this.owner.ticksExisted % 20 == 0) {
				if (medicCharge)
					this.medicCharge = false;
				
				Iterator<BlockPos> it = PlayerPersistStorage.get(this.owner).lostMercPos.iterator();
				while (it.hasNext()){
					BlockPos pos = it.next();
					ArrayList<EntityTF2Character> list = new ArrayList<>();
					this.owner.world.getChunkFromBlockCoords(pos).getEntitiesOfTypeWithinAABB(EntityTF2Character.class, new AxisAlignedBB(pos), list, test->{
						return test.getOrder() == Order.FOLLOW && owner.getUniqueID().equals(test.getOwnerId());
					});
					boolean success = false;
					for (EntityTF2Character living : list) {
						success = TF2Util.teleportSafe(living, owner);
					};
					if(success || list.isEmpty())
						it.remove();
					
				}
			}
			this.medicCall--;
				

			if(this.sendContractsNextTick)
				for(int i=0;i<this.contracts.size();i++) {
					TF2weapons.network.sendTo(new TF2Message.ContractMessage(i, this.contracts.get(i)),(EntityPlayerMP) this.owner);
				}
			
			int contractDay;
			if (!PlayerPersistStorage.get(this.owner).itemsToGive.isEmpty()) {
				ITextComponent text = new TextComponentString("You were awarded:");
				text.getStyle().setColor(TextFormatting.GOLD);
				this.owner.sendMessage(text);
				for (ItemStack stack : PlayerPersistStorage.get(this.owner).itemsToGive) {
					ItemHandlerHelper.giveItemToPlayer(owner, stack);
					this.owner.sendMessage(new TextComponentString(stack.getDisplayName()));
				}
				PlayerPersistStorage.get(this.owner).itemsToGive.clear();
				
			}
			if(!TF2ConfigVars.disableContracts && this.contracts.size()<2 && (contractDay=((EntityPlayerMP) this.owner).getStatFile().readStat(TF2Achievements.CONTRACT_DAY)) != 0  
					&&this.owner.world.getWorldTime()%24000 > 1000 && this.owner.world.getWorldTime()/24000>=contractDay) {
				String name="kill";
				do{
					switch(this.owner.getRNG().nextInt(10)) {
					case 0:name="scout"; break;
					case 1:name="pyro"; break;
					case 2:name="soldier"; break;
					case 3:name="demoman"; break;
					case 4:name="heavy"; break;
					case 5:name="engineer"; break;
					case 6:name="medic"; break;
					case 7:name="sniper"; break;
					case 8:name="spy"; break;
					default:name="kill";
					}
				} while (this.contracts.size()>0 && this.contracts.get(0).className.equals(name));
				((EntityPlayerMP) this.owner).getStatFile().unlockAchievement(this.owner,TF2Achievements.CONTRACT_DAY, (int) (this.owner.world.getWorldTime()/24000 + 7));
				Contract contract=new Contract(name, 0, this.owner.getRNG());
				this.contracts.add(contract);
				//((EntityPlayerMP)this.owner).sendMessage(new TextComponentString("f"));
				TF2weapons.network.sendTo(new TF2Message.ContractMessage(-1, contract), (EntityPlayerMP) this.owner);
			}
		}
		else if (this.owner == Minecraft.getMinecraft().player){
			((EntityPlayerSP)this.owner).connection.sendPacket(new CPacketInput(owner.moveStrafing, owner.moveForward,
					((EntityPlayerSP)this.owner).movementInput.jump, ((EntityPlayerSP)this.owner).movementInput.sneak));
		}
	}

	
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return TF2weapons.PLAYER_CAP != null && capability == TF2weapons.PLAYER_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (TF2weapons.PLAYER_CAP != null && capability == TF2weapons.PLAYER_CAP)
			return TF2weapons.PLAYER_CAP.cast(this);
		return null;
	}

	public void completeObjective(Objective objective,ItemStack stack) {
		int i=0;
		for(Contract contract:this.contracts) {
			if(contract.active)
			for(Objective objective2:contract.objectives) {
				if(objective2==objective && (contract.className.equals("kill") || ItemFromData.getData(stack).getString(PropertyType.MOB_TYPE).contains(contract.className))) {
					int oldProgress=contract.progress;
					contract.progress+=objective.getPoints();
					
					if(oldProgress<Contract.REWARD_LOW && contract.progress>=Contract.REWARD_LOW) {
						contract.rewards+=1;
					}
					if(oldProgress<Contract.REWARD_HIGH && contract.progress>=Contract.REWARD_HIGH) {
						contract.rewards+=2;
					}
					if(contract.progress>135)
						contract.progress=135;
					
					((EntityPlayerMP)this.owner).sendMessage(new TextComponentString(contract.className +" contract progress: "+contract.progress+" CP"));
					TF2weapons.network.sendTo(new TF2Message.ContractMessage(i, contract), (EntityPlayerMP) this.owner);
					break;
				}
			}
			i++;
		}
	}

	public int calculateMaxSentries() {
		ItemStack wrench = TF2Util.getBestItem(this.owner.inventory, (stack1, stack2) -> {
			float sentries1 = TF2Attribute.getModifier("Sentry Bonus", stack1, 1, this.owner);
			float sentries2 = TF2Attribute.getModifier("Sentry Bonus", stack2, 1, this.owner);
			return sentries1 > sentries2 ? 1 : sentries1 == sentries2 ? 0 : -1;
		}, stack -> stack.getItem() instanceof ItemWrench);
		ItemStack pda = TF2Util.getBestItem(this.owner.inventory, (stack1, stack2) -> {
			float sentries1 = TF2Attribute.getModifier("Extra Sentry", stack1, 0, this.owner);
			float sentries2 = TF2Attribute.getModifier("Extra Sentry", stack2, 0, this.owner);
			return sentries1 > sentries2 ? 1 : sentries1 == sentries2 ? 0 : -1;
		}, stack -> stack.getItem() instanceof ItemPDA);
		return (int) (TF2Attribute.getModifier("Sentry Bonus", wrench, 1, this.owner) * TF2Attribute.getModifier("Extra Sentry", pda, 0, this.owner));
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		//tag.setBoolean("Uber", this.owner.getDataManager().get(TF2EventBusListener.ENTITY_UBER));
		NBTTagCompound bossInfo = new NBTTagCompound();
		for (Entry<Class<? extends Entity>, Short> entry : this.highestBossLevel.entrySet())
			bossInfo.setShort(EntityList.getKey(entry.getKey()).toString(), entry.getValue());
		tag.setTag("BossInfo", bossInfo);
		tag.setInteger("NextBossTick", this.nextBossTicks);
		tag.setFloat("DodgedDmg", this.dodgedDmg);
		NBTTagList list=new NBTTagList();
		tag.setTag("Contracts", list);
		for(Contract contract:this.contracts) {
			NBTTagCompound com=new NBTTagCompound();
			com.setBoolean("Active", contract.active);
			com.setShort("Progress", (short) contract.progress);
			com.setByte("Rewards", (byte) contract.rewards);
			byte[] objs=new byte[contract.objectives.length];
			for(int i=0;i<contract.objectives.length;i++) {
				objs[i]=(byte) contract.objectives[i].ordinal();
			}
			com.setByteArray("Objectives", objs);
			com.setString("Name", contract.className);
			list.appendTag(com);
		}
		tag.setInteger("NextContractDay", this.nextContractDay);
		if (this.carrying != null) {
			tag.setTag("Carrying", this.carrying);
			tag.setByte("CarryingType", (byte) this.carryingType);
		}
		tag.setFloat("RobotsKilled", (short) this.robotsKilledInvasion);
		tag.setByte("MaxInvasionBeaten", (byte) this.maxInvasionBeaten);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagCompound bossInfo = nbt.getCompoundTag("BossInfo");
		for (String key : bossInfo.getKeySet())
			this.highestBossLevel.put(EntityList.getClass(new ResourceLocation(key)), bossInfo.getShort(key));
		this.nextBossTicks = nbt.getInteger("NextBossTick");
		this.dodgedDmg=nbt.getFloat("DodgedDmg");
		NBTTagList list=(NBTTagList) nbt.getTag("Contracts");
		this.contracts.clear();
		if(list != null)
		for(int i=0;i<list.tagCount() && i < 16;i++) {
			NBTTagCompound com=list.getCompoundTagAt(i);
			byte[] objsb=com.getByteArray("Objectives");
			Objective[] objs=new Objective[objsb.length];
			for(int j=0;j<objsb.length;j++) {
				objs[j]=Objective.values()[objsb[j]];
			}
			Contract contract=new Contract(com.getString("Name"), 0, objs);
			contract.active=com.getBoolean("Active");
			contract.progress=com.getShort("Progress");
			contract.rewards=com.getByte("Rewards");
			this.contracts.add(contract);
			//TF2weapons.network.sendTo(new TF2Message.ContractMessage(i, contract), (EntityPlayerMP) this.owner);
		}
		this.nextContractDay=nbt.getInteger("NextContractDay");
		this.carrying = (NBTTagCompound) nbt.getTag("Carrying");
		this.carryingType = nbt.getByte("CarryingType");
		this.robotsKilledInvasion = nbt.getFloat("RobotsKilled");
		this.maxInvasionBeaten = nbt.getByte("MaxInvasionBeaten");
	}

	public static TF2PlayerCapability get(EntityPlayer player) {
		return player.getCapability(TF2weapons.PLAYER_CAP, null);
	}
}
