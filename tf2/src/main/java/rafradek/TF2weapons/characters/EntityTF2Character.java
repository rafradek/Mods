package rafradek.TF2weapons.characters;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.ai.EntityAIFollowTrader;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAISeek;
import rafradek.TF2weapons.characters.ai.EntityAIUseRangedWeapon;
import rafradek.TF2weapons.decoration.ItemWearable;
import rafradek.TF2weapons.pages.Contract;
import rafradek.TF2weapons.pages.Contract.Objective;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.projectiles.EntityProjectileSimple;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.WeaponsCapability;
import rafradek.TF2weapons.weapons.ItemMeleeWeapon;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.ItemUsable;

public class EntityTF2Character extends EntityCreature implements IMob, IMerchant, IEntityTF2{

	public float[] lastRotation;
	public boolean jump;
	public boolean friendly;
	public boolean ranged;
	public EntityAIUseRangedWeapon attack;
	public EntityAINearestChecked findplayer = new EntityAINearestChecked(this, EntityLivingBase.class, true, false,
			this.getEntitySelector(), true);
	protected EntityAIAttackMelee attackMeele = new EntityAIAttackMelee(this, 1.1F, false);
	public EntityAIWander wander;
	public int ammoLeft;
	public boolean unlimitedAmmo;
	public boolean natural;
	private boolean noAmmo;
	public boolean alert;
	public static int nextEntTeam = -1;
	public EntityPlayer trader;
	public EntityPlayer lastTrader;
	public Map<EntityPlayer, Integer> tradeCount;

	public NonNullList<ItemStack> loadout;
	// public int heldWeaponSlot;

	public int followTicks;
	public MerchantRecipeList tradeOffers;
	public double[] targetPrevPos = new double[9];
	private static final DataParameter<Byte> VIS_TEAM = EntityDataManager.createKey(EntityTF2Character.class,
			DataSerializers.BYTE);
	private static final DataParameter<Byte> DIFFICULTY = EntityDataManager.createKey(EntityTF2Character.class,
			DataSerializers.BYTE);
	public float rotation;
	public int traderFollowTicks;
	public int usedSlot=-1;
	public int bannerTeam=-1;
	private UUID followID;
	public int tradeLevel;
	public EntityTF2Character(World p_i1738_1_) {
		super(p_i1738_1_);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(6, new EntityAIFollowTrader(this));
		this.tasks.addTask(6, wander = new EntityAIWander(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityTF2Character.class, 8.0F));
		this.tasks.addTask(7, new EntityAISeek(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, findplayer);
		// this.lookHelper=new
		// this.motionSensitivity=4;
		this.rotation = 17;
		this.lastRotation = new float[20];
		this.loadout = NonNullList.withSize(5,ItemStack.EMPTY);
		this.inventoryHandsDropChances[0] = 0;
		this.inventoryArmorDropChances[0] = 0.25f;
		if (p_i1738_1_ != null) {
			// this.getHeldItem(EnumHand.MAIN_HAND).getTagCompound().setTag("Attributes",
			// (NBTTagCompound)
			// ((ItemUsable)this.getHeldItem(EnumHand.MAIN_HAND).getItem()).buildInAttributes.copy());

			this.attack = new EntityAIUseRangedWeapon(this, 1.0F, 20.0F);
			this.setCombatTask(true);
		}
		this.tradeCount = new HashMap<>();

		/*
		 * for (int i = 0; i < this.e.length; ++i) {
		 * this.equipmentDropChances[i] = 0.0F; }
		 */
		// TODO Auto-generated constructor stub
	}

	/*
	 * public EntityLookHelper getLookHelper() { return this.lookHelper; }
	 */
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(TF2weapons.itemPlacer, 1, 1);
	}

	protected void addWeapons() {
		String className = this.getClass().getSimpleName().substring(6).toLowerCase();
		// System.out.println("Class name: "+className);
		this.loadout.set(0, ItemFromData.getRandomWeaponOfSlotMob(className, 0, this.rand, false, true));
		this.loadout.set(1, ItemFromData.getRandomWeaponOfSlotMob(className, 1, this.rand, false, true));
		this.loadout.set(2, ItemFromData.getRandomWeaponOfSlotMob(className, 2, this.rand, false, true));
		if(this.rand.nextInt(9) == 0) {
			this.tradeLevel=1;
			ItemStack hat=ItemFromData.getRandomWeaponOfSlotMob(className, 9, this.rand, false, true);
			if(!hat.isEmpty() && this.rand.nextInt(9) == 0) {
				hat.getTagCompound().setByte("UEffect", (byte) this.rand.nextInt(10));
				this.inventoryArmorDropChances[0] = 0.35f;
				this.tradeLevel=2;
				TF2Attribute.upgradeItemStack(this.loadout.get(0), Math.min(1600, 640+(int) (this.world.getWorldTime() / 2000)),
						rand);
			}
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, hat);
			if(this.world.getWorldTime() > 48000)
			TF2Attribute.upgradeItemStack(this.loadout.get(0), Math.min(800, 232+(int) (this.world.getWorldTime() / 4000)),
					rand);
		}
	}

	/*
	 * public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn){
	 * if(slotIn==EntityEquipmentSlot.MAINHAND){
	 * //System.out.println("Held item: "+this.loadout[this.heldWeaponSlot]);
	 * return this.loadout[this.heldWeaponSlot]; } else{ return
	 * super.getItemStackFromSlot(slotIn); } }
	 */
	/*
	 * public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable
	 * ItemStack stack) { if(slotIn==EntityEquipmentSlot.MAINHAND){
	 * this.loadout[this.heldWeaponSlot]=stack; } else{
	 * super.setItemStackToSlot(slotIn, stack); } }
	 */
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(VIS_TEAM, (byte) this.rand.nextInt(2));
		this.dataManager.register(DIFFICULTY, (byte) 0);
	}

	public int getEntTeam() {
		return this.dataManager.get(VIS_TEAM);
	}

	public int getDiff() {
		return this.dataManager.get(DIFFICULTY);
	}

	public void setEntTeam(int team) {
		this.dataManager.set(VIS_TEAM, (byte) team);
	}

	public void setDiff(int diff) {
		this.dataManager.set(DIFFICULTY, (byte) diff);
	}

	public int getAmmo() {
		// TODO Auto-generated method stub
		return ammoLeft;
	}

	@Override
	public void setAttackTarget(EntityLivingBase target) {

		super.setAttackTarget(target);
		if (this.isTrading())
			this.setCustomer(null);
		if (!this.alert)
			for (EntityTF2Character ent : this.world.getEntitiesWithinAABB(EntityTF2Character.class,
					new AxisAlignedBB(this.posX - 15, this.posY - 6, this.posZ - 15, this.posX + 15, this.posY + 6,
							this.posZ + 15)))
				if (TF2weapons.isOnSameTeam(this, ent) && !TF2weapons.isOnSameTeam(this, target)
						&& (ent.getAttackTarget() == null || ent.getAttackTarget().isDead)) {
					ent.alert = true;
					ent.setAttackTarget(target);
					ent.alert = false;
				}
	}

	public void useAmmo(int amount) {
		if (!this.unlimitedAmmo)
			this.ammoLeft -= amount;

	}

	public float getAttributeModifier(String attribute) {
		if (this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityPlayer)
			if (attribute.equals("Knockback"))
				return this.getDiff() == 1 ? 0.4f : (this.getDiff() == 3 ? 0.75f : 0.55f);
			else if (attribute.equals("Fire Rate"))
				return this.getDiff() == 1 ? 1.9f : (this.getDiff() == 3 ? 1.2f : 1.55f);
			else if (attribute.equals("Spread")) {
				float base = this.getDiff() == 1 ? 1.9f : (this.getDiff() == 3 ? 1.2f : 1.55f);
				return base;
			}
			else if (attribute.equals("Damage") && this.getHeldItemMainhand().getItem() instanceof ItemMeleeWeapon)
				return 1.25f;
		return 1f;
	}

	@Override
	public void onLivingUpdate() {

		long nanoTimeStart=System.nanoTime();
		super.onLivingUpdate();
		this.updateArmSwingProgress();
		ItemStack hat=this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (!hat.isEmpty() && hat.getItem() instanceof ItemWearable) {
			((ItemWearable)hat.getItem()).onUpdateWearing(hat, this.world, this);
		}
		if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive())
			this.setAttackTarget(null);
		if (!this.friendly && this.getAttackTarget() instanceof EntityTF2Character
				&& TF2weapons.isOnSameTeam(this, this.getAttackTarget()))
			this.setAttackTarget(null);
		if (this.jump && this.onGround)
			this.jump();
		if ((this.getCapability(TF2weapons.WEAPONS_CAP, null).state & 4) == 0)
			this.getCapability(TF2weapons.WEAPONS_CAP, null).state += 4;
		if (!this.noAmmo && this.getAttackTarget() != null && Math.abs(this.rotationYaw
				- this.rotationYawHead) > 60/*
											 * TF2ActionHandler.playerAction.get
											 * ().get(this)!=null&&(
											 * TF2ActionHandler.playerAction.get
											 * ().get(this)&3)>0
											 */)
			if (this.rotationYawHead - this.rotationYaw > 60)
				this.rotationYaw = this.rotationYawHead + 60;
			else
				this.rotationYaw = this.rotationYawHead - 60;
		if (!this.world.isRemote) {
			this.setDiff(this.world.getDifficulty().getDifficultyId());
			if (this.isTrading() && (this.trader.getDistanceSqToEntity(trader) > 100 || !this.isEntityAlive()))
				this.setCustomer(null);
			if (this.ammoLeft <= 0 && !this.noAmmo && 
					(this.getHeldItemMainhand().getItem() instanceof ItemUsable && ((ItemUsable)this.getHeldItemMainhand().getItem()).getAmmoType(this.getHeldItemMainhand()) != 0)) {
				this.switchSlot(2);
				this.noAmmo = true;
			}
			if(this.traderFollowTicks>0){
				this.traderFollowTicks--;
				if(this.followID!=null && this.lastTrader==null){
					this.lastTrader=this.world.getPlayerEntityByUUID(this.followID);
				}
			}
		}
		if (this.getHeldItem(EnumHand.MAIN_HAND) != null
				&& this.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemUsable)
			this.getHeldItem(EnumHand.MAIN_HAND).getItem().onUpdate(getHeldItem(EnumHand.MAIN_HAND), world, this, 0,
					true);

		for (int i = 19; i > 0; i--)
			this.lastRotation[i] = this.lastRotation[i - 1];
		this.lastRotation[0] = (float) Math.sqrt((this.rotationYawHead - this.prevRotationYawHead)
				* (this.rotationYawHead - this.prevRotationYawHead)
				+ (this.rotationPitch - this.prevRotationPitch) * (this.rotationPitch - this.prevRotationPitch));
		if(!this.world.isRemote)
			TF2EventsCommon.tickTimeMercUpdate[TF2weapons.server.getTickCounter()%20]+=System.nanoTime()-nanoTimeStart;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance p_180482_1_, IEntityLivingData p_110161_1_) {
		super.onInitialSpawn(p_180482_1_, p_110161_1_);
		if (p_110161_1_ == null) {
			p_110161_1_ = new TF2CharacterAdditionalData();
			((TF2CharacterAdditionalData) p_110161_1_).natural = true;
			if (nextEntTeam >= 0) {
				((TF2CharacterAdditionalData) p_110161_1_).team = nextEntTeam;
				nextEntTeam = -1;
			}
			
			if(this.bannerTeam!=-1){
				((TF2CharacterAdditionalData) p_110161_1_).team = this.bannerTeam;
			}
			else{
				List<EntityTF2Character> list = this.world.getEntitiesWithinAABB(EntityTF2Character.class,
						this.getEntityBoundingBox().grow(40, 4.0D, 40), null);
				
				boolean found=false;
				for(EntityTF2Character ent:list){
					if(ent.traderFollowTicks<=0){
						found=true;
						((TF2CharacterAdditionalData) p_110161_1_).team = list.get(0).getEntTeam();
						break;
					}
				}
				if(!found)
					((TF2CharacterAdditionalData) p_110161_1_).team = this.rand.nextInt(2);
			}
		}
		if (p_110161_1_ instanceof TF2CharacterAdditionalData) {
			this.natural = (((TF2CharacterAdditionalData) p_110161_1_).natural);
			this.setEntTeam(((TF2CharacterAdditionalData) p_110161_1_).team);
		}
		if (this.natural) {

		}
		this.addWeapons();
		this.switchSlot(this.getDefaultSlot());
		if(!this.world.getGameRules().getBoolean("doTF2AI")) {
			this.tasks.taskEntries.clear();
			this.targetTasks.taskEntries.clear();
		}
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("damageModifier", TF2weapons.damageMultiplier-1, 2));
		this.setHealth(this.getMaxHealth());
		return p_110161_1_;
	}

	public int getDefaultSlot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onDeath(DamageSource s) {
		if (s.getTrueSource() != null && s.getTrueSource() instanceof EntityPlayerMP && !TF2weapons.isOnSameTeam(this, s.getTrueSource())) {
			EntityPlayerMP player=(EntityPlayerMP) s.getTrueSource();
			if(s.getTrueSource().getTeam() != null) {
				player.addStat(TF2Achievements.KILLED_MERC);
				if(player.getStatFile().readStat(TF2Achievements.KILLED_MERC)>=5 && player.getStatFile().readStat(TF2Achievements.CONTRACT_DAY)==0/*player.getCapability(TF2weapons.PLAYER_CAP, null).nextContractDay == -1*/)
					player.addStat(TF2Achievements.CONTRACT_DAY, (int) (this.world.getWorldTime()/24000+1));
			}
			//player.addStat(TF2Achievements.FIRST_ENCOUNTER);
			
		}
		super.onDeath(s);
	}

	public void setCombatTask(boolean ranged) {
		this.ranged = ranged;
		this.tasks.removeTask(this.attack);
		this.tasks.removeTask(this.attackMeele);
		this.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
		// System.out.println(TF2ActionHandler.playerAction.get(this.world.isRemote).size());

		if (ranged)
			this.tasks.addTask(4, this.attack);
		else
			this.tasks.addTask(4, this.attackMeele);
	}

	public Predicate<EntityLivingBase> getEntitySelector() {
		return living ->
		{
				return ((living.getTeam() != null) && !TF2weapons.isOnSameTeam(EntityTF2Character.this, living))
						&& (!(living instanceof EntityTF2Character && TF2weapons.naturalCheck.equals("Never"))
								|| (!((EntityTF2Character) living).natural || !natural));

		};
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);

		this.ammoLeft = par1NBTTagCompound.getShort("Ammo");
		this.unlimitedAmmo = par1NBTTagCompound.getBoolean("UnlimitedAmmo");
		this.setEntTeam(par1NBTTagCompound.getByte("Team"));
		this.natural = par1NBTTagCompound.getBoolean("Natural");

		NBTTagList list = (NBTTagList) par1NBTTagCompound.getTag("Loadout");

		if(list != null){
			for (int i = 0; i < list.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("Slot");
				this.loadout.set(j, new ItemStack(nbttagcompound));
			}
		}
		if (par1NBTTagCompound.hasKey("Offers")) {
			this.tradeOffers = new MerchantRecipeList();
			this.tradeOffers.readRecipiesFromTags(par1NBTTagCompound.getCompoundTag("Offers"));
		}
		this.switchSlot(par1NBTTagCompound.getByte("Slot"));
		
		if(par1NBTTagCompound.hasKey("FollowTrader")){
			this.followID=par1NBTTagCompound.getUniqueId("FollowTrader");
			this.traderFollowTicks=par1NBTTagCompound.getInteger("FollowTraderTicks");
		}
		
		if(!this.world.getGameRules().getBoolean("doTF2AI")) {
			this.tasks.taskEntries.clear();
			this.targetTasks.taskEntries.clear();
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!(player.getHeldItemMainhand() != null
				&& player.getHeldItemMainhand().getItem() instanceof ItemMonsterPlacerPlus)
				&& (this.getAttackTarget() == null || this.friendly) && this.isEntityAlive() && !this.isTrading()
				&& !this.isChild() && !player.isSneaking()) {
			if (this.world.isRemote && player.getTeam() == null
					&& ((this.getCapability(TF2weapons.WEAPONS_CAP, null).state & 1) == 0 || this.friendly)
					&& !player.isCreative())
				ClientProxy.displayScreenConfirm("Choose a team to interact",
						"Visit the Mann Co. Store located in a village");
			if (!this.world.isRemote && (TF2weapons.isOnSameTeam(this, player) || player.isCreative())
					&& (this.tradeOffers == null || !this.tradeOffers.isEmpty())) {
				this.setCustomer(player);
				player.displayVillagerTradeGui(this);
			}

			player.addStat(StatList.TALKED_TO_VILLAGER);
			return true;
		} else
			return super.processInteract(player, hand);
	}

	public boolean isTrading() {
		// TODO Auto-generated method stub
		return this.trader != null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setShort("Ammo", (short) this.ammoLeft);
		par1NBTTagCompound.setBoolean("UnlimitedAmmo", this.unlimitedAmmo);
		par1NBTTagCompound.setByte("Team", (byte) this.getEntTeam());
		par1NBTTagCompound.setBoolean("Natural", this.natural);
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < this.loadout.size(); i++) {
			ItemStack itemstack = this.loadout.get(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		par1NBTTagCompound.setTag("Loadout", list);
		if (this.tradeOffers != null)
			par1NBTTagCompound.setTag("Offers", this.tradeOffers.getRecipiesAsTags());
		par1NBTTagCompound.setByte("Slot", (byte) this.usedSlot);
		if(this.lastTrader!=null){
			par1NBTTagCompound.setUniqueId("TraderFollow", this.lastTrader.getUniqueID());
			par1NBTTagCompound.setInteger("TraderFollowTicks", this.traderFollowTicks);
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		if (TF2EventsCommon.isSpawnEvent(world) || detectBanner())
			return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();

		boolean validLight = this.isValidLightLevel();
		Chunk chunk = this.world.getChunkFromBlockCoords(
				new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ)));
		boolean spawnDay = this.rand.nextInt(32) == 0 && chunk.getRandomWithSeed(987234911L).nextInt(10) == 0;

		if (!spawnDay && !validLight)
			return false;
		int time = (int) Math.min((this.world.getWorldInfo().getWorldTime() / 24000), 4);

		return (time == 4 || this.rand.nextInt(4) < time) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL
				&& super.getCanSpawnHere();
	}

	public boolean detectBanner(){
		Iterator<BlockPos> iterator=this.world.getCapability(TF2weapons.WORLD_CAP, null).banners.iterator();
		while(iterator.hasNext()){
			BlockPos pos=iterator.next();
			if(pos.distanceSq(this.getPosition())<1200){
				TileEntity banner=this.world.getTileEntity(pos);
				if(banner != null && banner instanceof TileEntityBanner){
					boolean fast=false;
					for(BannerPattern pattern: TF2EventsCommon.getPatterns((TileEntityBanner)banner)){
						if(pattern==TF2weapons.redPattern)
							this.bannerTeam=0;
						else if(pattern==TF2weapons.bluPattern)
							this.bannerTeam=1;
						else if(pattern==TF2weapons.fastSpawn)
							fast=true;
					}
					return fast && pos.distanceSq(this.getPosition())<512;
				}
				else{
					iterator.remove();
					return false;
				}
			}
		}
		return false;
	}
	
	protected boolean isValidLightLevel() {
		BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);

		if (this.world.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32))
			return false;
		else {
			int i = this.world.getLightFromNeighbors(blockpos);

			if (this.world.isThundering()) {
				int j = this.world.getSkylightSubtracted();
				this.world.setSkylightSubtracted(10);
				i = this.world.getLightFromNeighbors(blockpos);
				this.world.setSkylightSubtracted(j);
			}

			return i <= 4 + this.rand.nextInt(4);
		}
	}
	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL)
			this.setDead();
	}

	@Override
	public Team getTeam() {
		return this.getEntTeam() == 0 ? this.world.getScoreboard().getTeam("RED")
				: this.world.getScoreboard().getTeam("BLU");
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
	}

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.recentlyHit > 0 && source == DamageSource.MAGIC)
			this.recentlyHit=Math.max(20, this.recentlyHit);
		if (this.isEntityInvulnerable(source))
			return false;
		else if (super.attackEntityFrom(source, amount)) {
			Entity entity = source.getTrueSource();
			return this.getRidingEntity() != entity && this.getRidingEntity() != entity ? true : true;
		} else
			return false;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_HOSTILE_HURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_HOSTILE_DEATH;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
		int i = 0;

		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
					((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag) {
			if (i > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).knockBack(this, i * 0.5F, MathHelper.sin(this.rotationYaw * 0.017453292F),
						(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0)
				entityIn.setFire(j * 4);

			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				ItemStack itemstack = this.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

				if (!itemstack.isEmpty() && itemstack1 != null && itemstack.getItem() instanceof ItemAxe
						&& itemstack1.getItem() == Items.SHIELD) {
					float f1 = 0.25F + EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
						this.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}

	@Override
	protected boolean canDropLoot() {
		return true;
	}

	@Override
	public int getTalkInterval() {
		return 220;
	}

	@Override
	protected float getSoundPitch() {
		return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.08F + 1.5F
				: (this.rand.nextFloat() - this.rand.nextFloat()) * 0.08F + 1.0F;
	}

	public float getMotionSensitivity() {
		return this.getDiff() == 1 ? 0.18f : (this.getDiff() == 3 ? 0.07f : 0.11f);
	}

	public void onShot() {

	}

	@Override
	protected boolean canDespawn() {
		return this.natural && !TF2EventsCommon.isSpawnEvent(world);
	}

	/*
	 * @Nullable protected ResourceLocation getLootTable() { return
	 * TF2weapons.lootTF2Character; }
	 */
	@Override
	public void setCustomer(EntityPlayer player) {
		this.trader = player;
	}

	@Override
	public EntityPlayer getCustomer() {
		// TODO Auto-generated method stub
		return this.trader;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		// TODO Auto-generated method stub
		if (this.tradeOffers == null)
			makeOffers();
		return tradeOffers;
	}

	public void makeOffers() {
		this.tradeOffers = new MerchantRecipeList();
		int weaponCount = 1 + this.rand.nextInt(2);
		for (int i = 0; i < weaponCount; i++) {

			boolean buyItem = this.rand.nextBoolean();
			int slot = getValidSlots()[this.rand.nextInt(getValidSlots().length)];
			String className = this.getClass().getSimpleName().substring(6).toLowerCase();
			ItemStack item = ItemFromData.getRandomWeaponOfSlotMob(className, slot, this.getRNG(), false, false);
			if(!item.isEmpty()) {
				ItemStack metal = new ItemStack(TF2weapons.itemTF2,
						Math.max(1, ItemFromData.getData(item).getInt(PropertyType.COST) / 9), 3);
				this.tradeOffers.add(new MerchantRecipe(buyItem ? item : metal, ItemStack.EMPTY, buyItem ? metal : item, 0, 1));
			}
		}
		int hatCount = this.rand.nextInt(2+this.tradeLevel);

		for (int i = 0; i < hatCount; i++) {

			boolean buyItem = this.rand.nextBoolean();
			ItemStack item = ItemFromData.getRandomWeaponOfClass("cosmetic", this.rand, false);
			int cost = Math.max(1, ItemFromData.getData(item).getInt(PropertyType.COST));
			if(i==0 && this.tradeLevel==2) {
				((ItemWearable)item.getItem()).applyRandomEffect(item, rand);
				cost*=6+this.rand.nextInt(6);
			}
			ItemStack metal = new ItemStack(TF2weapons.itemTF2, cost / 18, 5);
			ItemStack metal2 = new ItemStack(TF2weapons.itemTF2, this.rand.nextInt(3), 4);
			if (metal2.getCount()==0)
				metal2 = ItemStack.EMPTY;

			this.tradeOffers.add(
					new MerchantRecipe(buyItem ? item : metal, buyItem ? ItemStack.EMPTY : metal2, buyItem ? metal : item, 0, 1));
		}
		
		if(this.tradeLevel>=1) {
			boolean buyKey = this.rand.nextBoolean();
			ItemStack item = new ItemStack(TF2weapons.itemTF2, 1, 7);
			ItemStack metal = new ItemStack(TF2weapons.itemTF2, 4, 5);
			ItemStack metal2 = new ItemStack(TF2weapons.itemTF2, this.rand.nextInt(3), 4);
			if (metal2.getCount()==0)
				metal2 = ItemStack.EMPTY;

			this.tradeOffers.add(
					new MerchantRecipe(buyKey ? item : metal, buyKey ? ItemStack.EMPTY : metal2, buyKey ? metal : item, 0, 1));
		}
	}

	@Override
	public void setRecipes(MerchantRecipeList recipeList) {
		this.tradeOffers = recipeList;

	}

	public int[] getValidSlots() {
		return new int[] { 0, 1, 2 };
	}
	public float[] getDropChance() {
		return new float[] { 0.08f, 0.15f, 0.15f };
	}

	@Override
	public void useRecipe(MerchantRecipe recipe) {
		recipe.incrementToolUses();
		if (recipe.getItemToBuy().getItem() instanceof ItemWeapon) {
			this.loadout.set(ItemFromData.getData(recipe.getItemToBuy()).getInt(PropertyType.SLOT), recipe.getItemToBuy());
			this.switchSlot(0);
		}

		else if(recipe.getItemToBuy().getItem() instanceof ItemWearable) {
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, recipe.getItemToBuy());
		}
		this.livingSoundTime = -this.getTalkInterval();
		this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
		int i = 3 + this.rand.nextInt(4);

		this.lastTrader = this.trader;
		this.tradeCount.put(this.trader,
				this.tradeCount.containsKey(trader) ? this.tradeCount.get(this.trader) + 1 : 1);
		this.traderFollowTicks = Math.min(4800, this.tradeCount.get(this.trader) * 250 + 350);
		if (recipe.getRewardsExp())
			this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + 0.5D, this.posZ, i));

	}

	public void switchSlot(int slot) {
		
		this.setHeldItem(EnumHand.MAIN_HAND, this.loadout.get(slot));
		
		if(slot != this.usedSlot && this.loadout.get(slot).getItem()!=null){
			WeaponData data=ItemFromData.getData(this.loadout.get(slot));
			this.attack.explosive=TF2Attribute.EXPLOSIVE.apply(this.loadout.get(slot));
			this.attack.projSpeed=TF2Attribute.getModifier("Proj Speed", this.loadout.get(slot), data.getFloat(PropertyType.PROJECTILE_SPEED), this);
			this.attack.fireAtFeet= slot==0 && this instanceof EntitySoldier ?TF2Attribute.getModifier("Explosion Radius", this.loadout.get(slot), 1, this):0;
			this.attack.setRange(data.getFloat(PropertyType.EFFICIENT_RANGE));
			if(this.loadout.get(slot).getItem() instanceof ItemProjectileWeapon) {
				String projName=data.getString(PropertyType.PROJECTILE);
				try {
					EntityProjectileBase proj=MapList.projectileClasses.get(projName)
							.getConstructor(World.class, EntityLivingBase.class, EnumHand.class)
							.newInstance(world, this, EnumHand.MAIN_HAND);
					this.attack.gravity=(float) proj.getGravity();
				} catch (Exception e) {
					
				}
			}
			/*if(projName.equals("grenade") || projName.equals("sticky")){
				this.attack.gravity=0.0381f;
			}
			else if(projName.equals("flare")){
				this.attack.gravity=0.019f;
			}*/
			this.getCapability(TF2weapons.WEAPONS_CAP, null).fire1Cool=400;
		}
		this.usedSlot=slot;
}

	@Override
	public void verifySellingItem(ItemStack stack) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 4;
	}

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		EntityLivingBase attacker=this.getAttackingEntity();
		if ((attacker instanceof EntityPlayer || (attacker instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwner() != null 
				&& ((IEntityOwnable)attacker).getOwner() instanceof EntityPlayer))
				&& attacker.getTeam() != null && TF2weapons.isEnemy(attacker, this))
			for(int i=0;i<loadout.size();i++){
				ItemStack stack=loadout.get(i);
				if (!stack.isEmpty() && stack.getItem() instanceof ItemFromData
						&& this.rand.nextFloat() <= this.getDropChance()[i]
								* (1 + lootingModifier * 0.4f)) {
					stack.setCount( 1);
					if (stack.getItem() instanceof ItemFromData && TF2EventsCommon.isSpawnEvent(world))
						TF2Attribute
								.upgradeItemStack(stack,
								(int) (Math.min(320, world.getWorldTime() / 4000)
								+ rand.nextInt((int) Math.min(720, world.getWorldTime() / 2000))),
								rand);
					this.entityDropItem(stack, 0);
				}
			}
		else{
			Arrays.fill(this.inventoryArmorDropChances,0f);
		}
		super.dropEquipment(wasRecentlyHit, lootingModifier);
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return world;
	}

	@Override
	public BlockPos getPos() {
		// TODO Auto-generated method stub
		return getPos();
	}
	
	public WeaponsCapability getWepCapability() {
		return this.getCapability(TF2weapons.WEAPONS_CAP, null);
	}
	
	@Override
	protected int getExperiencePoints(EntityPlayer player)
    {
		if(TF2weapons.isOnSameTeam(player, this))
			return 0;
		else
			return super.getExperiencePoints(player);
    }
	/*
	 * @Override public void writeSpawnData(ByteBuf buffer) { PacketBuffer
	 * packet=new PacketBuffer(buffer); for(int i=0;i<this.loadout.length;i++){
	 * packet.writeByte(i); packet.writeItemStackToBuffer(this.loadout[i]); } }
	 * 
	 * @Override public void readSpawnData(ByteBuf additionalData) {
	 * PacketBuffer packet=new PacketBuffer(additionalData);
	 * while(packet.readableBytes()>0){ try {
	 * this.loadout[packet.readByte()]=packet.readItemStackFromBuffer(); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } } }
	 */
}
