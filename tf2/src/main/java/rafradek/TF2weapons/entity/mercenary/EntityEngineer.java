package rafradek.TF2weapons.entity.mercenary;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.audio.TF2Sounds;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.ai.EntityAIRepair;
import rafradek.TF2weapons.entity.ai.EntityAISetup;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.building.EntityDispenser;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.item.ItemFromData;

public class EntityEngineer extends EntityTF2Character {

	public EntitySentry sentry;
	public EntityDispenser dispenser;

	public int buildCount;
	
	public EntityEngineer(World p_i1738_1_) {
		super(p_i1738_1_);
		//this.ammoLeft = 24;
		this.experienceValue = 15;
		this.rotation = 15;
		this.tasks.addTask(3, new EntityAIRepair(this, 1, 2f));
		this.tasks.addTask(5, new EntityAISetup(this));
		this.tasks.removeTask(wander);
		this.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(TF2ConfigVars.maxMetalEngineer);
		if (this.attack != null)
			attack.setRange(20);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return TF2weapons.lootEngineer;
	}

	@Override
	protected void addWeapons() {
		this.loadout.setStackInSlot(3,ItemFromData.getRandomWeaponOfSlotMob("engineer", 3, this.rand, false, true, this.noEquipment));
		super.addWeapons();
		
	}
	public float[] getDropChance() {
		return new float[] { 0.12f, 0.12f, 0.08f, 0.02f };
	}
	
	@Override
	public int[] getValidSlots() {
		return new int[] { 0, 1, 2, 3 };
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.5D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.15D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1329D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return TF2Sounds.MOB_ENGINEER_SAY;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return TF2Sounds.MOB_ENGINEER_HURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_ENGINEER_DEATH;
	}

	public void onLivingUpdate() {
		if(!this.world.isRemote &&this.ticksExisted == 3) {
			for(EntityBuilding building : this.world.getEntitiesWithinAABB(EntityBuilding.class, this.getEntityBoundingBox().grow(16), building -> {
				return building.getOwnerId() == null && building.getOwner() == null;
			})){
				if(building instanceof EntitySentry && this.sentry == null) {
					this.sentry = (EntitySentry) building;
					building.setOwner(this);
				}
				else if(building instanceof EntityDispenser && this.dispenser == null) {
					this.dispenser = (EntityDispenser) building;
					building.setOwner(this);
				}
			};
		}
		super.onLivingUpdate();
	}
	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		if (this.rand.nextFloat() < 0.10f + p_70628_2_ * 0.05f)
			this.entityDropItem(ItemFromData.getNewStack("pistol"), 0);
		if (this.rand.nextFloat() < 0.10f + p_70628_2_ * 0.05f)
			this.entityDropItem(ItemFromData.getNewStack("shotgun"), 0);
		if (this.rand.nextFloat() < 0.10f + p_70628_2_ * 0.05f)
			this.entityDropItem(ItemFromData.getNewStack("wrench"), 0);
		this.entityDropItem(
				new ItemStack(TF2weapons.itemBuildingBox, 1, 18 + this.rand.nextInt(3) * 2 + this.getEntTeam()), 0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setShort("BuildCount", (short) buildCount);
		/*if (this.sentry != null && this.sentry.isEntityAlive()) {
			NBTTagCompound sentryTag = new NBTTagCompound();
			this.sentry.writeToNBTAtomically(sentryTag);
			par1NBTTagCompound.setTag("Sentry", sentryTag);
		}
		if (this.dispenser != null && this.dispenser.isEntityAlive()) {
			NBTTagCompound dispenserTag = new NBTTagCompound();
			this.dispenser.writeToNBTAtomically(dispenserTag);
			par1NBTTagCompound.setTag("Dispenser", dispenserTag);
		}*/

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		this.buildCount = par1NBTTagCompound.getShort("BuildCount");
		/*if (par1NBTTagCompound.hasKey("Sentry") && this.sentry == null) {
			// System.out.println(par1NBTTagCompound.getCompoundTag("Sentry"));
			this.sentry = (EntitySentry) EntityList.createEntityFromNBT(par1NBTTagCompound.getCompoundTag("Sentry"), this.world);
			this.sentry.forceSpawn=true;
			this.world.spawnEntity(sentry);
		}
		// this.world.spawnEntity(sentry);
		if (par1NBTTagCompound.hasKey("Dispenser") && this.dispenser == null)
			this.dispenser = (EntityDispenser) AnvilChunkLoader.readWorldEntityPos(
					par1NBTTagCompound.getCompoundTag("Dispenser"), this.world, this.posX, this.posY, this.posZ,
					true);
		*/
		// dispenser.readFromNBT(par1NBTTagCompound.getCompoundTag("Dispenser"));
		// this.world.spawnEntity(dispenser);
	}
	
	public int getClassIndex() {
		return 5;
	}
	public boolean isAmmoFull() {
		return this.getWepCapability().getMetal() >= (hasSentryAndDispenser() ? 40 : Math.min(TF2ConfigVars.maxMetalEngineer, WeaponsCapability.MAX_METAL)) && super.isAmmoFull();
	}
	
	public boolean hasSentryAndDispenser() {
		return this.sentry != null && this.sentry.isEntityAlive() && this.dispenser != null && this.dispenser.isEntityAlive();
	}
	
	public boolean canBecomeGiant() {
		return false;
	}
}
