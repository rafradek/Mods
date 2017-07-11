package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.characters.ai.EntityAIRepair;
import rafradek.TF2weapons.characters.ai.EntityAISetup;

public class EntityEngineer extends EntityTF2Character {

	public EntitySentry sentry;
	public EntityDispenser dispenser;

	public EntityEngineer(World p_i1738_1_) {
		super(p_i1738_1_);
		this.ammoLeft = 24;
		this.experienceValue = 15;
		this.rotation = 15;
		this.tasks.addTask(3, new EntityAIRepair(this, 1, 2f));
		this.tasks.addTask(5, new EntityAISetup(this));
		this.tasks.removeTask(wander);
		this.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(500);
		if (this.attack != null)
			attack.setRange(20);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return TF2weapons.lootEngineer;
	}

	public float[] getDropChance() {
		return new float[] { 0.12f, 0.12f, 0.08f };
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.15D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
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
	protected SoundEvent getHurtSound() {
		return TF2Sounds.MOB_ENGINEER_HURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_ENGINEER_DEATH;
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
		if (this.sentry != null && this.sentry.isEntityAlive()) {
			NBTTagCompound sentryTag = new NBTTagCompound();
			this.sentry.writeToNBTAtomically(sentryTag);
			par1NBTTagCompound.setTag("Sentry", sentryTag);
		}
		if (this.dispenser != null && this.dispenser.isEntityAlive()) {
			NBTTagCompound dispenserTag = new NBTTagCompound();
			this.dispenser.writeToNBTAtomically(dispenserTag);
			par1NBTTagCompound.setTag("Dispenser", dispenserTag);
		}

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		if (par1NBTTagCompound.hasKey("Sentry"))
			// System.out.println(par1NBTTagCompound.getCompoundTag("Sentry"));
			this.sentry = (EntitySentry) AnvilChunkLoader.readWorldEntityPos(
					par1NBTTagCompound.getCompoundTag("Sentry"), this.world, this.posX, this.posY, this.posZ, true);
		// this.world.spawnEntity(sentry);
		if (par1NBTTagCompound.hasKey("Dispenser"))
			this.dispenser = (EntityDispenser) AnvilChunkLoader.readWorldEntityPos(
					par1NBTTagCompound.getCompoundTag("Dispenser"), this.world, this.posX, this.posY, this.posZ,
					true);
		// dispenser.readFromNBT(par1NBTTagCompound.getCompoundTag("Dispenser"));
		// this.world.spawnEntity(dispenser);
	}
}
