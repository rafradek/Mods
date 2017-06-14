package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.TupleIntJsonSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.pages.Contract;
import rafradek.TF2weapons.pages.Contract.Objective;

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
	public int[] cachedAmmoCount=new int[15];
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
	public TF2PlayerCapability(EntityPlayer entity) {
		this.owner = entity;
		this.lostItems=new ItemStackHandler(27);
		this.nextBossTicks = (int) (entity.world.getWorldTime() + entity.getRNG().nextInt(360000));
	}

	public void tick() {
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
			if(this.sendContractsNextTick)
				for(int i=0;i<this.contracts.size();i++) {
					TF2weapons.network.sendTo(new TF2Message.ContractMessage(i, this.contracts.get(i)),(EntityPlayerMP) this.owner);
				}
			
			int contractDay;
			if(!TF2weapons.disableContracts && this.contracts.size()<2 && (contractDay=((EntityPlayerMP) this.owner).getStatFile().readStat(TF2Achievements.CONTRACT_DAY)) != 0  
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
					
					if(oldProgress<50 && contract.progress>=50) {
						contract.rewards+=1;
					}
					if(oldProgress<150 && contract.progress>=150) {
						contract.rewards+=2;
					}
					if(contract.progress>150)
						contract.progress=150;
					
					((EntityPlayerMP)this.owner).sendMessage(new TextComponentString(contract.className +" contract progress: "+contract.progress+" CP"));
					TF2weapons.network.sendTo(new TF2Message.ContractMessage(i, contract), (EntityPlayerMP) this.owner);
					break;
				}
			}
			i++;
		}
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
		if(list != null)
		for(int i=0;i<list.tagCount();i++) {
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
		
	}
}
