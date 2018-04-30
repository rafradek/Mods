package rafradek.TF2weapons.characters;

import com.google.common.base.Predicates;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAIUseMedigun;

public class EntityMedic extends EntityTF2Character {

	//public boolean melee;

	public EntityAIUseMedigun useMedigun = new EntityAIUseMedigun(this, 1.0F, 20.0F);
	public EntityMedic(World par1World) {
		super(par1World);
		this.targetTasks.taskEntries.clear();
		this.targetTasks.addTask(1, this.findplayer = new EntityAINearestChecked(this, EntityLivingBase.class, true,
				false, Predicates.and(this::isValidTarget, target -> {
					return target.getHealth()<target.getMaxHealth() || (target instanceof EntityPlayer && target.getCapability(TF2weapons.PLAYER_CAP, null).medicCall > 0);
				}), false, true));
		this.targetTasks.addTask(2, new EntityAINearestChecked(this, EntityLivingBase.class, true,
				false, this::isValidTarget, true, false));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(4,
				new EntityAINearestChecked(this, EntityLivingBase.class, true, false, super::isValidTarget, true, false));
		this.unlimitedAmmo = true;
		//this.ammoLeft = 1;
		this.experienceValue = 15;
		this.rotation = 15;
		this.tasks.removeTask(attack);

		if (par1World != null) {
			this.tasks.addTask(4, useMedigun);
			//attack.setRange(7f);
			this.setCombatTask(true);
			this.friendly = true;
		}
		// this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
		// ItemUsable.getNewStack("Minigun"));

	}

	@Override
	protected ResourceLocation getLootTable() {
		return TF2weapons.lootMedic;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(17D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.14111D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
	}

	@Override
	public void onLivingUpdate() {

		super.onLivingUpdate();
		if (this.getCapability(TF2weapons.WEAPONS_CAP, null).getHealTarget() > 0)
			this.ignoreFrustumCheck = true;
		else
			this.ignoreFrustumCheck = false;
		if (!this.world.isRemote) {
			IAttributeInstance speed = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			for(AttributeModifier modifier : playerAttributes) {
				speed.removeModifier(modifier);
			}
			if (this.ticksExisted % 4 == 0 && this.getAttackTarget() != null && this.friendly && this.getAttackTarget() instanceof EntityPlayer) {
				
				
				playerAttributes.clear();
				for(AttributeModifier modifier : this.getAttackTarget().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifiers()) {
					if (modifier.getAmount() > 0) {
						TF2Util.addModifierSafe(this, SharedMonsterAttributes.MOVEMENT_SPEED, modifier, true);
						this.playerAttributes.add(modifier);
					}
				}
				//System.out.println("modyfikatory: "+playerAttributes.size()+" "+speed.getModifiers().size());
			}
		}
		
	}

	@Override
	public void setAttackTarget(EntityLivingBase entity) {
		this.alert = true;
		if (TF2Util.isOnSameTeam(this, entity)) {
			// System.out.println("friendly");
			if (!friendly) {
				this.friendly = true;
				this.setCombatTask(true);

			}
		} else if (entity != null && this.friendly) {
			// System.out.println("not friendly");
			this.friendly = false;
			this.setCombatTask(false);
		}
		this.switchSlot(this.getDefaultSlot());
		//System.out.println("Attack Target Set: "+entity);
		super.setAttackTarget(entity);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return TF2Sounds.MOB_MEDIC_SAY;
	}

	@Override
	public int getDefaultSlot() {
		// TODO Auto-generated method stub
		return this.friendly ? 1 : 0;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return TF2Sounds.MOB_MEDIC_HURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_MEDIC_DEATH;
	}

	/**
	 * Plays step sound at given x, y, z for the entity
	 */

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		if (this.rand.nextFloat() < 0.15f + p_70628_2_ * 0.075f)
			this.entityDropItem(ItemFromData.getNewStack("syringegun"), 0);
		if (this.rand.nextFloat() < 0.08f + p_70628_2_ * 0.03f)
			this.entityDropItem(ItemFromData.getNewStack("medigun"), 0);
	}

	@Override
	public float getAttributeModifier(String attribute) {
		if (!(this.getAttackTarget() instanceof EntityPlayer || (this.getAttackTarget() instanceof IEntityOwnable && ((IEntityOwnable) this.getAttackTarget()).getOwnerId() != null))) {
			if (attribute.equals("Heal"))
				return this.scaleWithDifficulty(0.75f, 1f);
			if (attribute.equals("Overheal"))
				return this.scaleWithDifficulty(0.55f, 1f);
		}
		return super.getAttributeModifier(attribute);
	}

	@Override
	public float getMotionSensitivity() {
		return 0f;
	}
	
	public boolean isValidTarget(EntityLivingBase target) {
		return !((target instanceof EntityMedic && target.getHealth() >= target.getMaxHealth()) || target instanceof EntityBuilding)
				&& TF2Util.isOnSameTeam(EntityMedic.this, target);
	}
	public void setCombatTask(boolean ranged) {
		this.tasks.removeTask(this.attack);
		this.tasks.removeTask(this.useMedigun);
		this.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
		// System.out.println(TF2ActionHandler.playerAction.get(this.world.isRemote).size());

		if (ranged)
			this.tasks.addTask(4, this.useMedigun);
		else
			this.tasks.addTask(4, this.attack);
	}
	
	public int getClassIndex() {
		return 6;
	}
}
