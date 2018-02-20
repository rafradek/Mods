package rafradek.TF2weapons.characters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.characters.ai.EntityAIFindDispenser;
import rafradek.TF2weapons.characters.ai.EntityAIFollowTrader;
import rafradek.TF2weapons.characters.ai.EntityAIMoveAttack;
import rafradek.TF2weapons.characters.ai.EntityAIMoveTowardsRestriction2;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAIOwnerHurt;
import rafradek.TF2weapons.characters.ai.EntityAISeek;
import rafradek.TF2weapons.characters.ai.EntityAIUseRangedWeapon;
import rafradek.TF2weapons.decoration.ItemWearable;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.WeaponsCapability;
import rafradek.TF2weapons.weapons.ItemAmmo;
import rafradek.TF2weapons.weapons.ItemBackpack;
import rafradek.TF2weapons.weapons.ItemMeleeWeapon;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.ItemUsable;

public class EntityTF2Character extends EntityCreature implements IMob, IMerchant, IEntityTF2, IEntityOwnable{

	public static final UUID SPEED_MULT_UUID = UUID.fromString("8ca1776e-72e8-4394-9d0f-0564fdec0b44");
	public float[] lastRotation;
	public boolean jump;
	public boolean friendly;
	public boolean ranged;
	public EntityAIUseRangedWeapon attack;
	public EntityAIMoveAttack moveAttack;
	public EntityAINearestChecked findplayer = new EntityAINearestChecked(this, EntityLivingBase.class, true, false,
			this::isValidTarget, true, false);
	protected EntityAIAttackMelee attackMeele = new EntityAIAttackMelee(this, 1.1F, false);
	public EntityAIWander wander;
	//public int ammoLeft;
	public boolean unlimitedAmmo;
	public boolean natural;
	private boolean noAmmo;
	public boolean alert;
	public static int nextEntTeam = -1;
	public EntityPlayer trader;
	public EntityPlayer lastTrader;
	public Map<EntityPlayer, Integer> tradeCount;
	public ItemStackHandler loadout;
	public ItemStackHandler loadoutHeld;
	public ItemStackHandler refill;
	// public int heldWeaponSlot;

	public int followTicks;
	public MerchantRecipeList tradeOffers;
	public double[] targetPrevPos = new double[9];
	private static final DataParameter<Byte> VIS_TEAM = EntityDataManager.createKey(EntityTF2Character.class,
			DataSerializers.BYTE);
	private static final DataParameter<Byte> DIFFICULTY = EntityDataManager.createKey(EntityTF2Character.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.createKey(EntityTF2Character.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Byte> ORDER = EntityDataManager.createKey(EntityTF2Character.class,
			DataSerializers.BYTE);
	private static final DataParameter<Boolean> SHARE = EntityDataManager.createKey(EntityTF2Character.class,
			DataSerializers.BOOLEAN);
	public float rotation;
	public int traderFollowTicks;
	public int usedSlot = 0;
	public int bannerTeam=-1;
	private UUID followID;
	public int tradeLevel;
	public String ownerName;
	private int[] ammoCount;
	int preferredSlot = 0;
	public float eating = 0;
	public EntityAIAvoidEntity<EntitySentry> avoidSentry;
	private int alertTime;
	private EntityLivingBase alertTarget;
	public int difficulty=0;
	public ArrayList<AttributeModifier> playerAttributes = new ArrayList<>();
	public boolean[] isEmpty;
	
	
	public EntityTF2Character(World p_i1738_1_) {
		super(p_i1738_1_);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIMoveTowardsRestriction2(this, 1.25f));
		this.tasks.addTask(1, avoidSentry = new EntityAIAvoidEntity<EntitySentry>(this, EntitySentry.class, sentry -> {
			return !TF2Util.isOnSameTeam(this, sentry) && !sentry.isDisabled() && sentry.getDistanceSqToEntity(this) < 435;
		}, 21, 1.0f, 1.0f));
		this.tasks.addTask(2, new EntityAIFollowTrader(this));
		this.tasks.addTask(3, new EntityAIFindDispenser(this, 20f));
		this.tasks.addTask(6, wander = new EntityAIWander(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityTF2Character.class, 8.0F));
		this.tasks.addTask(7, new EntityAISeek(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(5, findplayer);
		this.targetTasks.addTask(3, new EntityAIOwnerHurt(this));
		this.targetTasks.addTask(4, new EntityAINearestChecked(this, EntityLiving.class, true, false, target -> {
			return (target instanceof IMob && target.getDistanceSqToEntity(this.getOwner())<400) || target.getAttackTarget() == this.getOwner();
		}, true, false) {
			public boolean shouldExecute() {
				return ((EntityTF2Character) this.taskOwner).getOwner() != null && super.shouldExecute();
			}
		});
		// this.lookHelper=new
		// this.motionSensitivity=4;
		this.rotation = 17;
		this.lastRotation = new float[20];
		this.loadout = new ItemStackHandler(5);//NonNullList.withSize(5,ItemStack.EMPTY);
		this.loadoutHeld = new ItemStackHandler(7);
		this.refill = new ItemStackHandler(1);
		this.ammoCount = new int[3];
		this.isEmpty = new boolean[4];
		this.inventoryHandsDropChances[0] = 0;
		this.inventoryArmorDropChances[0] = 0.25f;
		//this.jumpMovementFactor = 0.1f;
		if (p_i1738_1_ != null) {
			// this.getHeldItem(EnumHand.MAIN_HAND).getTagCompound().setTag("Attributes",
			// (NBTTagCompound)
			// ((ItemUsable)this.getHeldItem(EnumHand.MAIN_HAND).getItem()).buildInAttributes.copy());

			this.tasks.addTask(4, this.attack = new EntityAIUseRangedWeapon(this, 1.0F, 20.0F));
			this.tasks.addTask(4, this.moveAttack = new EntityAIMoveAttack(this, 1.0F, 20.0F));
			//this.setCombatTask(true);
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
		this.loadout.setStackInSlot(0, ItemFromData.getRandomWeaponOfSlotMob(className, 0, this.rand, false, true));
		this.loadout.setStackInSlot(1, ItemFromData.getRandomWeaponOfSlotMob(className, 1, this.rand, false, true));
		this.loadout.setStackInSlot(2, ItemFromData.getRandomWeaponOfSlotMob(className, 2, this.rand, false, true));
		if(this.rand.nextInt(14 - this.world.getDifficulty().getDifficultyId() * 3) == 0) {
			this.tradeLevel=1;
			this.difficulty = 1;
			this.experienceValue *= 2;
			ItemStack hat=ItemFromData.getRandomWeaponOfSlotMob(className, 9, this.rand, false, true);
			if(!hat.isEmpty() && this.rand.nextInt(9) == 0) {
				hat.getTagCompound().setByte("UEffect", (byte) this.rand.nextInt(10));
				this.inventoryArmorDropChances[0] = 0.35f;
				this.tradeLevel=2;
				this.difficulty = 2;
				this.experienceValue *= 1.5;
				TF2Attribute.upgradeItemStack(this.loadout.getStackInSlot(0), Math.min(1600, 640+(int) (this.world.getWorldTime() / 2000)),
						rand);
			}
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, hat);
			if(this.world.getWorldTime() > 48000)
			TF2Attribute.upgradeItemStack(this.loadout.getStackInSlot(0), Math.min(800, 232+(int) (this.world.getWorldTime() / 4000)),
					rand);
		}
		
	}

	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		float chance = 1f + 5f * this.tradeLevel;
        if (this.rand.nextFloat() < TF2ConfigVars.armorMult * chance * difficulty.getClampedAdditionalDifficulty())
        {
            int i = this.rand.nextInt(2);
            float f = (this.world.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F) / chance;

            if (this.rand.nextFloat() < 0.095F * chance)
            {
                ++i;
            }

            if (this.rand.nextFloat() < 0.095F * chance)
            {
                ++i;
            }

            if (this.rand.nextFloat() < 0.095F * chance)
            {
                ++i;
            }

            boolean flag = true;

            for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
            {
                if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR)
                {
                    ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);

                    if (!flag && this.rand.nextFloat() < f)
                    {
                        break;
                    }

                    flag = false;

                    if (itemstack.isEmpty() && entityequipmentslot != EntityEquipmentSlot.HEAD)
                    {
                        Item item = getArmorByChance(entityequipmentslot, i);

                        if (item != null)
                        {
                            this.setItemStackToSlot(entityequipmentslot, new ItemStack(item));
                        }
                    }
                }
            }
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
		this.dataManager.register(OWNER_UUID, Optional.absent());
		this.dataManager.register(ORDER, (byte) 0);
		this.dataManager.register(SHARE, false);
	}

	public int getEntTeam() {
		return this.dataManager.get(VIS_TEAM);
	}

	public int getDiff() {
		return this.dataManager.get(DIFFICULTY) + this.difficulty;
	}

	public void setEntTeam(int team) {
		this.dataManager.set(VIS_TEAM, (byte) team);
	}

	public void setDiff(int diff) {
		this.dataManager.set(DIFFICULTY, (byte) diff);
	}

	public Order getOrder() {
		return Order.values()[this.dataManager.get(ORDER)];
	}

	public void setOrder(Order order) {
		this.dataManager.set(ORDER, (byte) order.ordinal());
	}
	
	public boolean isSharing() {
		return this.dataManager.get(SHARE);
	}

	public void setSharing(boolean share) {
		this.dataManager.set(SHARE, share);
	}
	
	public int getAmmo() {
		// TODO Auto-generated method stub
		return this.getAmmo(this.usedSlot);
	}

	@Override
	public void setAttackTarget(EntityLivingBase target) {

		super.setAttackTarget(target);
		if (this.isTrading() && target != null && this.getCustomer() != this.getOwner()) {
			this.setCustomer(null);
			//System.out.println(target);
		}
		if (!this.alert)
			for (EntityTF2Character ent : this.world.getEntitiesWithinAABB(EntityTF2Character.class,
					new AxisAlignedBB(this.posX - 15, this.posY - 6, this.posZ - 15, this.posX + 15, this.posY + 6,
							this.posZ + 15)))
				if (TF2Util.isOnSameTeam(this, ent) && !TF2Util.isOnSameTeam(this, target)
						&& (ent.getAttackTarget() == null || ent.getAttackTarget().isDead)) {
					ent.alert = true;
					ent.alertTarget=target;
					ent.alertTime=ent.ticksExisted + 8 + this.getRNG().nextInt(14);
					
				}
	}

	public void useAmmo(int amount) {
		if (!this.unlimitedAmmo)
			this.ammoCount[this.usedSlot]-=amount;

	}

	public boolean shouldScaleAttributes() {
		return this.getAttackTarget() != null && (this.getAttackTarget() instanceof EntityPlayer 
				|| this.getAttackTarget() instanceof IEntityOwnable && ((IEntityOwnable) this.getAttackTarget()).getOwnerId() != null) && this.getOwnerId() == null;
	}
	public float getAttributeModifier(String attribute) {
		if (shouldScaleAttributes())
			if (attribute.equals("Knockback"))
				return this.scaleWithDifficulty(0.4f, 1f);
			else if (attribute.equals("Fire Rate"))
				return this.scaleWithDifficulty(1.9f, 1f);
			else if (attribute.equals("Spread")) {
				return this.scaleWithDifficulty(1.9f, 1f);
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
		ItemStack backpack=this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (!backpack.isEmpty() && backpack.getItem() instanceof ItemBackpack) {
			((ItemBackpack)backpack.getItem()).onArmorTickAny(world, this, backpack);
		}
		if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive())
			this.setAttackTarget(null);
		if (!this.friendly && this.getAttackTarget() instanceof EntityTF2Character
				&& TF2Util.isOnSameTeam(this, this.getAttackTarget()))
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
		//if (this.hasHome()) {
			
		
		if (!this.world.isRemote) {
			if(this.getOrder() == Order.HOLD && !this.hasHome())
				this.setHomePosAndDistance(this.getPos(), 12);
			else if( this.getOrder() == Order.FOLLOW && this.hasHome())
				this.detachHome();
			
			this.setDiff(this.world.getDifficulty().getDifficultyId());
			if (this.isTrading() && (this.trader.getDistanceSqToEntity(trader) > 100 || !this.isEntityAlive()))
				this.setCustomer(null);
			if (this.getHeldItemMainhand().getItem() instanceof ItemUsable) {
				if(!((ItemUsable)this.getHeldItemMainhand().getItem()).isAmmoSufficient(this.getHeldItemMainhand(), this, true)) {
					if (!this.refill(this.usedSlot))
						this.switchSlot(this.getFirstSlotWithAmmo(), true);
				}
					
				//if(this.getAmmo() == 0)
				//System.out.println("IsSufficient "+ ((ItemUsable)this.getHeldItemMainhand().getItem()).isAmmoSufficient(this.getHeldItemMainhand(), this, true));
				//this.noAmmo = true;
			}
			if (this.preferredSlot != this.usedSlot) {
				this.switchSlot(this.preferredSlot);
			}
			if(this.traderFollowTicks>0){
				this.traderFollowTicks--;
				if(this.followID!=null && this.lastTrader==null){
					this.lastTrader=this.world.getPlayerEntityByUUID(this.followID);
				}
			}
			if(this.refill.getStackInSlot(0).getItem() instanceof ItemAmmo) {
				int ammoType =((ItemAmmo)this.refill.getStackInSlot(0).getItem()).getTypeInt(this.refill.getStackInSlot(0));
				for (int i=0; i < this.ammoCount.length; i++) {
					if (this.getAmmo(i) < this.getMaxAmmo(i)  &&
							ammoType == ItemFromData.getData(this.loadout.getStackInSlot(i)).getInt(PropertyType.AMMO_TYPE)) {
						if(this.refill.getStackInSlot(0).isItemStackDamageable()) {
							int oldAmmo = this.getMaxAmmo(i) - this.ammoCount[i];
							this.ammoCount[i] += Math.min((this.refill.getStackInSlot(0).getMaxDamage() - this.refill.getStackInSlot(0).getItemDamage()) * 2, this.getMaxAmmo(i) - this.ammoCount[i]);
							this.refill.getStackInSlot(0).setItemDamage(this.refill.getStackInSlot(0).getItemDamage()+oldAmmo);
							if(this.refill.getStackInSlot(0).getItemDamage() > this.refill.getStackInSlot(0).getMaxDamage())
								this.refill.setStackInSlot(0, ItemStack.EMPTY);
							
						}
						else
							this.ammoCount[i] += this.refill.extractItem(0, (this.getMaxAmmo(i) - this.ammoCount[i]) / 2, false).getCount() * 2;
					}
						
				}
			}
			else if(this.getActiveItemStack().isEmpty() && this.getAttackTarget() == null && this.getHealth()/this.getMaxHealth() < 0.7f && this.refill.getStackInSlot(0).getItem() instanceof ItemFood){
				this.setHeldItem(EnumHand.OFF_HAND, this.refill.getStackInSlot(0));
				this.setActiveHand(EnumHand.OFF_HAND);
				//this.eating = ((ItemFood)this.refill.getStackInSlot(0).getItem()).getHealAmount(this.refill.getStackInSlot(0));
			}
			
			/*if(this.eating > 0 && this.ticksExisted % 20 == 0) {
				this.heal(Math.min(3, this.eating));
				this.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20));
				this.playSound(SoundEvents.PLAYER, volume, pitch);
				this.eating -=Math.min(3, this.eating);
			}*/
			if(this.alert && this.alertTarget != null && this.alertTime == this.ticksExisted) {
				this.setAttackTarget(this.alertTarget);
				this.alert = false;
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

	public void travel(float m1, float m2, float m3) {
		float move = this.getAIMoveSpeed();
		super.travel(m1/this.getAIMoveSpeed(), m2, m3/this.getAIMoveSpeed());
	}
	protected void onItemUseFinish()
    {
        if (!this.activeItemStack.isEmpty() && this.isHandActive() && this.activeItemStack.getItemUseAction() == EnumAction.EAT)
        {
        	this.heal(((ItemFood)this.refill.getStackInSlot(0).getItem()).getHealAmount(activeItemStack));
        }
        super.onItemUseFinish();
        this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
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
					if(ent.getOwnerId() == null){
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
		this.setEquipmentBasedOnDifficulty(p_180482_1_);
		this.switchSlot(this.getDefaultSlot());
		
		int i=3;
		for(ItemStack stack : this.getArmorInventoryList()) {
			this.isEmpty[i] = stack.isEmpty();
			i--;
		}
		if(!this.world.getGameRules().getBoolean("doTF2AI")) {
			this.tasks.taskEntries.clear();
			this.targetTasks.taskEntries.clear();
		}
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("damageModifier", TF2ConfigVars.damageMultiplier-1, 2));
		this.applySpeed();
		this.setHealth(this.getMaxHealth());
		for(int j=0; j<this.ammoCount.length; j++) {
			this.ammoCount[j] = this.getMaxAmmo(j);
		}
		return p_110161_1_;
	}

	public void applySpeed() {
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_MULT_UUID);
		if (this.getOwnerId() == null)
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(SPEED_MULT_UUID,"speedModifier", TF2ConfigVars.speedMult-1, 2));
	}
	
	public int getDefaultSlot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected float getJumpUpwardsMotion() {
		return 0.5f;
	}
	
	@Override
	public void onDeath(DamageSource s) {
		if (s.getTrueSource() != null && s.getTrueSource() instanceof EntityPlayerMP && !TF2Util.isOnSameTeam(this, s.getTrueSource())) {
			EntityPlayerMP player=(EntityPlayerMP) s.getTrueSource();
			if(s.getTrueSource().getTeam() != null) {
				player.addStat(TF2Achievements.KILLED_MERC);
				if(player.getStatFile().readStat(TF2Achievements.KILLED_MERC)>=5 && player.getStatFile().readStat(TF2Achievements.CONTRACT_DAY)==0/*player.getCapability(TF2weapons.PLAYER_CAP, null).nextContractDay == -1*/)
					player.addStat(TF2Achievements.CONTRACT_DAY, (int) (this.world.getWorldTime()/24000+1));
			}
			//player.addStat(TF2Achievements.FIRST_ENCOUNTER);
			
		}
		if (this.attackingPlayer != null && TF2Util.isOnSameTeam(this, this.attackingPlayer))
			this.experienceValue = 0;
		super.onDeath(s);
	}

	

	public boolean isValidTarget(EntityLivingBase living) {
				return ((living.getTeam() != null) && !TF2Util.isOnSameTeam(EntityTF2Character.this, living))
						&& (!(living instanceof EntityTF2Character && TF2ConfigVars.naturalCheck.equals("Never"))
								|| (!((EntityTF2Character) living).natural || !natural));
	}

	

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!(player.getHeldItemMainhand() != null
				&& player.getHeldItemMainhand().getItem() instanceof ItemMonsterPlacerPlus)
				&& (this.getOwner() == player || this.getAttackTarget() == null || this.friendly) && this.isEntityAlive() && !this.isTrading()
				&& !this.isChild() && !player.isSneaking()) {
			if (this.world.isRemote && player.getTeam() == null
					&& ((this.getCapability(TF2weapons.WEAPONS_CAP, null).state & 1) == 0 || this.friendly)
					&& !player.isCreative())
				ClientProxy.displayScreenConfirm("Choose a team to interact",
						"Visit the Mann Co. Store located in a village");
			if (!this.world.isRemote && (TF2Util.isOnSameTeam(this, player) || player.isCreative())
					&& (this.tradeOffers == null || !this.tradeOffers.isEmpty())) {
				this.setCustomer(player);
				FMLNetworkHandler.openGui(player, TF2weapons.instance, 4, world, this.getEntityId(), 0, 0);
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
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		if(tag.hasKey("AmmoC"))
			this.ammoCount = tag.getIntArray("AmmoC");
		if(this.ammoCount.length == 0)
			this.ammoCount = new int[3];
		this.unlimitedAmmo = tag.getBoolean("UnlimitedAmmo");
		this.setEntTeam(tag.getByte("Team"));
		this.natural = tag.getBoolean("Natural");

		if (tag.getTag("Loadout") instanceof NBTTagList) {
			NBTTagList list = (NBTTagList) tag.getTag("Loadout");
	
			if(list != null){
				for (int i = 0; i < list.tagCount(); ++i) {
					NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
					int j = nbttagcompound.getByte("Slot");
					this.loadout.setStackInSlot(j, new ItemStack(nbttagcompound));
				}
			}
		}
		else
			this.loadout.deserializeNBT(tag.getCompoundTag("Loadout"));
		this.loadoutHeld.deserializeNBT(tag.getCompoundTag("LoadoutHeld"));
		
		if (this.loadoutHeld.getSlots()<7) {
			this.loadoutHeld.setSize(7);
		}
		
		this.refill.setStackInSlot(0, new ItemStack(tag.getCompoundTag("Refill")));
		
		if (tag.hasKey("Offers")) {
			this.tradeOffers = new MerchantRecipeList();
			this.tradeOffers.readRecipiesFromTags(tag.getCompoundTag("Offers"));
		}
		this.preferredSlot = tag.getByte("PSlot");
		this.switchSlot(tag.getByte("Slot"), true);
		if(tag.hasKey("FollowTrader")){
			this.followID=tag.getUniqueId("FollowTrader");
			this.traderFollowTicks=tag.getInteger("FollowTraderTicks");
		}
		
		if(!this.world.getGameRules().getBoolean("doTF2AI")) {
			this.tasks.taskEntries.clear();
			this.targetTasks.taskEntries.clear();
		}

		if (tag.hasUniqueId("Owner")) {
			UUID ownerID = tag.getUniqueId("Owner");
			this.dataManager.set(OWNER_UUID, Optional.of(ownerID));
			this.ownerName = tag.getString("OwnerName");
			this.getOwner();
			this.enablePersistence();
			this.setOrder(Order.values()[tag.getByte("Order")]);
			
			//this.world.getCapability(TF2weapons.WORLD_CAP, null).lostMercPos.remove(this.getOwnerId(), this.getPos());
			this.world.getCapability(TF2weapons.WORLD_CAP, null).medicMercPos.remove(this.getOwnerId(), this.getPos());
			this.world.getCapability(TF2weapons.WORLD_CAP, null).restMercPos.remove(this.getOwnerId(), this.getPos());
		}
		
		this.tradeLevel = tag.getByte("TradeLevel");
		this.difficulty = tag.getByte("Difficulty");
		byte[] empty = tag.getByteArray("Empty");
		for (int i = 0; i < empty.length; i++) {
			this.isEmpty[i] = empty[i] != 0;
		}
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		IAttributeInstance speed = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		for(AttributeModifier modifier : playerAttributes) {
			speed.removeModifier(modifier);
		}
		
		super.writeEntityToNBT(tag);
		tag.setIntArray("AmmoC", ammoCount);
		tag.setBoolean("UnlimitedAmmo", this.unlimitedAmmo);
		tag.setByte("Team", (byte) this.getEntTeam());
		tag.setBoolean("Natural", this.natural);
		/*NBTTagList list = new NBTTagList();

		for (int i = 0; i < this.loadout.getSlots(); i++) {
			ItemStack itemstack = this.loadout.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		tag.setTag("Loadout", list);*/
		tag.setTag("Loadout", this.loadout.serializeNBT());
		tag.setTag("LoadoutHeld", loadoutHeld.serializeNBT());
		tag.setTag("Refill", this.refill.getStackInSlot(0).serializeNBT());
		if (this.tradeOffers != null)
			tag.setTag("Offers", this.tradeOffers.getRecipiesAsTags());
		if(this.usedSlot == -1)
			this.usedSlot = this.getDefaultSlot();
		tag.setByte("Slot", (byte) this.usedSlot);
		tag.setByte("PSlot", (byte) this.preferredSlot);
		if(this.lastTrader!=null){
			tag.setUniqueId("TraderFollow", this.lastTrader.getUniqueID());
			tag.setInteger("TraderFollowTicks", this.traderFollowTicks);
		}
		if (this.getOwnerId() != null) {
			tag.setUniqueId("Owner", this.getOwnerId());
			tag.setString("OwnerName", this.ownerName);
			tag.setByte("Order", (byte) this.getOrder().ordinal());
			if (this.getOrder() == Order.FOLLOW)
				this.world.getCapability(TF2weapons.WORLD_CAP, null).lostMercPos.put(this.getOwnerId(), this.getPos());
			else if (this.getOrder() == Order.HOLD && this instanceof EntityMedic)
				this.world.getCapability(TF2weapons.WORLD_CAP, null).medicMercPos.put(this.getOwnerId(), this.getPos());
			else if (this.getOrder() == Order.HOLD && !(this instanceof EntityEngineer))
				this.world.getCapability(TF2weapons.WORLD_CAP, null).restMercPos.put(this.getOwnerId(), this.getPos());
		}
		tag.setByte("TradeLevel", (byte) this.tradeLevel);
		tag.setByteArray("Empty", new byte[] {(byte) (isEmpty[0]?1:0), (byte) (isEmpty[1]?1:0), (byte) (isEmpty[2]?1:0), (byte) (isEmpty[3]?1:0)});
		tag.setByte("Difficulty", (byte) this.difficulty);
		
	}

	@Override
	public boolean getCanSpawnHere() {
		if (detectBanner() || TF2EventsCommon.isSpawnEvent(world))
			return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();

		if (this.isDead) {
			return false;
		}
		
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
		//System.out.println("Bannrs: "+this.world.getCapability(TF2weapons.WORLD_CAP, null).banners.size());
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
						else if(pattern==TF2weapons.neutralPattern) {
							this.setDead();
							return false;
						}
						else if(pattern==TF2weapons.fastSpawn)
							fast=true;
					}
					return fast && pos.distanceSq(this.getPosition())<800;
				}
				else{
					iterator.remove();
					return false;
				}
			}
		}
		return false;
	}
	
	protected void damageEntity(DamageSource damageSrc, float damageAmount)
    {
        if (!this.isEntityInvulnerable(damageSrc))
        {
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, damageSrc, damageAmount);
            if (damageAmount <= 0) return;
            damageAmount = net.minecraftforge.common.ISpecialArmor.ArmorProperties.applyArmor(this, (NonNullList<ItemStack>) this.getArmorInventoryList(), damageSrc, damageAmount);
            if (damageAmount <= 0) return;
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            float f = damageAmount;
            damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, damageSrc, damageAmount);

            if (damageAmount != 0.0F)
            {
                float f1 = this.getHealth();
                this.setHealth(this.getHealth() - damageAmount);
                this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
            }
        }
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
		if(this.getOwner() != null)
			return this.getOwner().getTeam();
		else if(this.ownerName != null && !this.ownerName.isEmpty())
			return this.world.getScoreboard().getPlayersTeam(this.ownerName);
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
		return this.getOwnerId() != null ? 1000 : 320;
	}

	@Override
	protected float getSoundPitch() {
		return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.08F + 1.5F
				: (this.rand.nextFloat() - this.rand.nextFloat()) * 0.08F + 1.0F;
	}
	
	@Override
	protected float getSoundVolume() {
		return TF2ConfigVars.mercenaryVolume;
	}

	public float getMotionSensitivity() {
		return this.scaleWithDifficulty(0.18f, 0.03f);
	}

	public void onShot() {

	}

	@Override
	protected boolean canDespawn() {
		return this.natural && this.getOwnerId() == null &&!TF2EventsCommon.isSpawnEvent(world);
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
			this.loadout.setStackInSlot(ItemFromData.getData(recipe.getItemToBuy()).getInt(PropertyType.SLOT), recipe.getItemToBuy());
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
		this.switchSlot(slot, false);
	}

	public void switchSlot(int slot, boolean noAmmoSwitch) {
		ItemStack stack = this.loadout.getStackInSlot(slot);
		
		if(!noAmmoSwitch)
			this.preferredSlot=slot;
		
		if (slot != this.usedSlot && stack.getItem() instanceof ItemUsable && !((ItemUsable)stack.getItem()).isAmmoSufficient(stack, this, true))
			return;
		
		if(this.getHeldItemMainhand().getItem() instanceof ItemUsable && slot != this.usedSlot)
			((ItemUsable)this.getHeldItemMainhand().getItem()).holster(getWepCapability(), stack, this, world);
		
		this.setHeldItem(EnumHand.MAIN_HAND, stack);
		
		if(slot != this.usedSlot && !stack.isEmpty() && stack.getItem() instanceof ItemUsable){
			if(!((ItemUsable)stack.getItem()).isAmmoSufficient(stack, this, true)) 
				if (!this.refill(this.usedSlot)) {
					this.switchSlot(this.getFirstSlotWithAmmo(), true);
					return;
				}
			WeaponData data = ItemFromData.getData(stack);
			this.attack.explosive = TF2Attribute.EXPLOSIVE.apply(stack);
			this.attack.projSpeed = TF2Attribute.getModifier("Proj Speed", stack, data.getFloat(PropertyType.PROJECTILE_SPEED), this);
			this.attack.fireAtFeet = (slot == 0 && this instanceof EntitySoldier) || (slot == 1 && this instanceof EntityDemoman) ?TF2Attribute.getModifier("Explosion Radius", stack, 1, this):0;
			this.attack.setRange(data.getFloat(PropertyType.EFFICIENT_RANGE));
			this.moveAttack.setRange(data.getFloat(PropertyType.EFFICIENT_RANGE));
			((ItemUsable)stack.getItem()).draw(getWepCapability(), stack, this, world);
			if(stack.getItem() instanceof ItemProjectileWeapon) {
				String projName=data.getString(PropertyType.PROJECTILE);
				try {
					EntityProjectileBase proj=MapList.projectileClasses.get(projName)
							.getConstructor(World.class, EntityLivingBase.class, EnumHand.class)
							.newInstance(world, this, EnumHand.MAIN_HAND);
					this.attack.gravity=(float) proj.getGravityOverride();
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
		for(int i=0; i < 3; i++) {
			if(!this.loadoutHeld.getStackInSlot(i).isEmpty()) {
				this.entityDropItem(this.loadout.getStackInSlot(i), 0).setThrower(ownerName);
				this.loadout.setStackInSlot(i, this.loadoutHeld.getStackInSlot(i));
			}
		}
		for(int i = 0; i < 4; i++)
			if(!this.loadoutHeld.getStackInSlot(i+3).isEmpty()) {
				this.entityDropItem(this.getItemStackFromSlot(EntityEquipmentSlot.values()[5-i]), 0).setThrower(ownerName);
				this.setItemStackToSlot(EntityEquipmentSlot.values()[5-i], this.loadoutHeld.getStackInSlot(3));
				this.setDropChance(EntityEquipmentSlot.values()[5-i], i == 0 ? 0.25f : 0f);
			}
		if(this.getOwnerId() != null) {
			ItemStack stack=new ItemStack(TF2weapons.itemTF2, this.isSharing() ? 2 : 1, 2);
			if(this.getOwner() != null && this.getDistanceSqToEntity(this.getOwner()) < 100)
				this.entityDropItem(stack, 0);
			else {
				Map<String, MerchantRecipeList> map = this.getWorld().getCapability(TF2weapons.WORLD_CAP, null).lostItems;
				if(!map.containsKey(this.ownerName))
					map.put(ownerName, new MerchantRecipeList());
				map.get(ownerName).add(new MerchantRecipe(ItemStack.EMPTY, ItemStack.EMPTY, stack, 0, 1));
			}
		}
		EntityLivingBase attacker=this.getAttackingEntity();
		if(!this.refill.getStackInSlot(0).isEmpty() && (attacker == this.getOwner() || !TF2Util.isOnSameTeam(attacker, this))) {
			this.entityDropItem(this.refill.getStackInSlot(0), 0);
		}
		if ((attacker instanceof EntityPlayer || (attacker instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId() != null ))
				&& attacker.getTeam() != null && TF2Util.isEnemy(attacker, this))
			for(int i=0;i<loadout.getSlots();i++){
				ItemStack stack=loadout.getStackInSlot(i);
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
		else if(this.inventoryArmorDropChances[0] < 1){
			this.inventoryArmorDropChances[0]=0;
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
		return this.getPosition();
	}
	
	public WeaponsCapability getWepCapability() {
		return this.getCapability(TF2weapons.WEAPONS_CAP, null);
	}
	
	@Override
	protected int getExperiencePoints(EntityPlayer player)
    {
		if(TF2Util.isOnSameTeam(player, this))
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

	@Override
	public UUID getOwnerId() {
		// TODO Auto-generated method stub
		return this.getDataManager().get(OWNER_UUID).orNull();
	}

	@Override
	public Entity getOwner() {
		// TODO Auto-generated method stub
		if (this.getOwnerId() != null)
			return this.world.getPlayerEntityByUUID(this.getDataManager().get(OWNER_UUID).orNull());
		else
			return null;
	}
	
	public void setOwner(EntityLivingBase owner) {
		// TODO Auto-generated method stub
		if(owner == null) {
			this.ownerName = null;
			this.dataManager.set(OWNER_UUID, Optional.absent());
		}
		else if (owner instanceof EntityPlayer){
			this.ownerName = owner.getName();
			this.dataManager.set(OWNER_UUID, Optional.of(owner.getUniqueID()));
			this.enablePersistence();
		}
		
	}
	public static enum Order{
		FOLLOW,HOLD;
	}
	
	public int getMaxAmmo(int slot) {
		int ammo = ItemFromData.getData(this.loadout.getStackInSlot(slot == -1 ? this.usedSlot : slot)).getInt(PropertyType.MAX_AMMO);
		return (int) (this.getOwnerId() != null ? ammo : this.scaleWithDifficulty(0.5f, 1f) * ammo);
	}
	
	public int getAmmo(int slot) {
		return ammoCount[slot];
	}
	
	public boolean isAmmoFull() {
		if (this.unlimitedAmmo)
			return true;
		else {
			for(int i = 0; i < ammoCount.length; i++) {
				ItemStack stack = loadout.getStackInSlot(i);
				if(stack.getItem() instanceof ItemUsable && (this.ammoCount[i] < this.getMaxAmmo(i)))
					return false;
			}
		}
		return true;
	}
	
	public int getFirstSlotWithAmmo() {
		if (this.unlimitedAmmo)
			return this.getDefaultSlot();
		for(int i = 0; i < loadout.getSlots(); i++) {
			ItemStack stack = loadout.getStackInSlot(i);
			if(stack.getItem() instanceof ItemUsable) {
				if (((ItemUsable)stack.getItem()).isAmmoSufficient(stack, this, true))
					return i;
				else if (refill(i))
					return i;
			}
		}
		return 2;
	}
	
	public boolean refill(int slot) {
		if(!this.refill.getStackInSlot(0).isEmpty() && !(this.refill.getStackInSlot(0).getItem() instanceof ItemAmmo)) {
			this.refill.extractItem(0, 1, false);
			this.ammoCount[slot]=(int) (this.getMaxAmmo(slot) * 0.4f);
			return true;
		}
		return false;
	}
	public boolean restoreAmmo(float p) {
		for(int i = 0; i < ammoCount.length; i++) {
			if(loadout.getStackInSlot(i).getItem() instanceof ItemUsable) {
				int maxAmmo= getMaxAmmo(i);
				ammoCount[i] = Math.min(ammoCount[i] + (int)(maxAmmo * p),maxAmmo);
			}
		}
		return true;
	}
	public float scaleWithDifficulty(float min, float max) {
		int diff= this.getDiff();
		if (diff < 3 && !this.shouldScaleAttributes())
			diff = 3;
		return TF2Util.lerp(min, max, 1-(1f/(1<<(diff-1))));
		/*float mult = 0;
		switch (diff) {
		case 2: mult = 0.5f; break;
		case 2: mult = 0.85f; break;
		}*/
	}
}
