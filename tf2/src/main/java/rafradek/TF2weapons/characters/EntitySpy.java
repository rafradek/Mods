package rafradek.TF2weapons.characters;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.ai.EntityAIAmbush;
import rafradek.TF2weapons.weapons.ItemCloak;

public class EntitySpy extends EntityTF2Character {

	public int weaponCounter;
	public int cloakCounter;

	public float prevHealth;
	public EntitySpy(World p_i1738_1_) {
		super(p_i1738_1_);
		this.ammoLeft = 16;
		this.experienceValue = 15;
		this.rotation = 20;
		this.tasks.addTask(3, new EntityAIAmbush(this));
		if (this.attack != null) {
			attack.setRange(30f);
			this.tasks.addTask(4, this.attack);
		}
		this.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks = 20;
		this.getDataManager().set(TF2EventsCommon.ENTITY_INVIS, true);
		this.getDataManager().set(TF2EventsCommon.ENTITY_DISGUISED, true);
		this.getDataManager().set(TF2EventsCommon.ENTITY_DISGUISE_TYPE, "T:Engineer");
	}

	public float[] getDropChance() {
		return new float[] { 0.1f, 0.7f, 0.08f, 0.05f};
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return TF2weapons.lootSpy;
	}

	@Override
	public int getTalkInterval() {
		return 80;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			this.prevHealth=this.getHealth();
			this.cloakCounter--;
			EntityLivingBase target = this.getAttackTarget();
			if (target != null && this.loadout.get(3).getTagCompound().getBoolean("Active")) {
				boolean useKnife = false;
				if ((this.getAITarget() != null && this.ticksExisted - this.getRevengeTimer() < 45)
						|| (useKnife = (this.getDistanceSqToEntity(target) < 13
								&& !TF2weapons.lookingAtFast(target, 105, this.posX, this.posY, this.posZ)))) {

					((ItemCloak) this.loadout.get(3).getItem()).setCloak(
							!this.getDataManager().get(TF2EventsCommon.ENTITY_INVIS), this.loadout.get(3), this,
							this.world);
					if (useKnife) {
						this.weaponCounter = 8;
						this.setCombatTask(false);
						this.cloakCounter = 36;
					} else
						this.cloakCounter = 20 + (int) ((16 - this.getDistanceToEntity(target)) * 10);
				}
				/*
				 * float x = -MathHelper.sin(target.rotationYaw / 180.0F *
				 * (float)Math.PI); float z = MathHelper.cos(target.rotationYaw
				 * / 180.0F * (float)Math.PI);
				 * this.setPosition(target.posX-x,target.posY,target.posZ-z);
				 */
			}

			//System.out.println(this.loadout.get(3));
			if (this.cloakCounter <= 0 && !this.loadout.get(3).getTagCompound().getBoolean("Active"))
				((ItemCloak) this.loadout.get(3).getItem()).setCloak(true, this.loadout.get(3), this, this.world);
			this.weaponCounter--;

			if (this.weaponCounter <= 0 && this.getAttackTarget() != null
					&& this.getDistanceSqToEntity(this.getAttackTarget()) < 4) {
				this.setCombatTask(false);
				this.weaponCounter = 8;
			} else if (this.weaponCounter <= 0 && this.getHeldItemMainhand() == this.loadout.get(2)) {
				this.setCombatTask(true);
				this.weaponCounter = 3;
			}
			if (this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityBuilding
					&& ((EntityBuilding) this.getAttackTarget()).isSapped()
					&& ((EntityBuilding) this.getAttackTarget()).getOwner() != null)
				this.setAttackTarget(((EntityBuilding) this.getAttackTarget()).getOwner());
		}
	}

	@Override
	public int[] getValidSlots() {
		return new int[] { 0, 1, 2, 3 };
	}

	@Override
	protected void addWeapons() {
		this.loadout.set(0,ItemFromData.getRandomWeaponOfSlotMob("spy", 0, this.rand, false, true));
		this.loadout.set(1,ItemFromData.getRandomWeaponOfSlotMob("spy", 1, this.rand, true, true));
		this.loadout.set(2,ItemFromData.getRandomWeaponOfSlotMob("spy", 2, this.rand, false, true));
		this.loadout.set(3,ItemFromData.getRandomWeaponOfSlotMob("spy", 3, this.rand, false, true));
		this.loadout.get(1).setCount( 64);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return TF2Sounds.MOB_SPY_SAY;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound() {
		return TF2Sounds.MOB_SPY_HURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_SPY_DEATH;
	}

	/**
	 * Plays step sound at given x, y, z for the entity
	 */
	@Override
	public void setCombatTask(boolean ranged) {
		this.ranged = ranged;
		if (ranged) {

			this.switchSlot(0);
			this.attack.setRange(30);
		} else if (this.getAttackTarget() instanceof EntityBuilding) {
			this.switchSlot(1);
			this.attack.setRange(1.9f);
		} else {
			this.switchSlot(2);
			this.attack.setRange(2.2f);
		}
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		if (this.rand.nextFloat() < 0.085f + p_70628_2_ * 0.05f)
			this.entityDropItem(ItemFromData.getNewStack("revolver"), 0);
		if (this.rand.nextFloat() < 0.06f + p_70628_2_ * 0.025f)
			this.entityDropItem(ItemFromData.getNewStack("butterflyknife"), 0);
		if (this.rand.nextFloat() < 0.05f + p_70628_2_ * 0.025f)
			this.entityDropItem(ItemFromData.getNewStack("cloak"), 0);
		if (this.rand.nextFloat() < 0.05f + p_70628_2_ * 0.025f)
			this.entityDropItem(new ItemStack(TF2weapons.itemDisguiseKit), 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.315D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}
}
