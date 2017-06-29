package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemWeapon;

public class EntitySoldier extends EntityTF2Character {

	public boolean rocketJump;
	public boolean rocketJumper;
	public boolean airborne;

	public EntitySoldier(World par1World) {
		super(par1World);
		// this.rotation=90;
		this.rocketJumper = this.rand.nextBoolean();
		if (this.attack != null) {
			attack.setRange(35);
			attack.fireAtFeet = 1;
			attack.projSpeed = 1.04f;
			attack.explosive = true;
			attack.setDodge(true, this.rocketJumper);
		}
		this.ammoLeft = 20;
		this.experienceValue = 15;
		// this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
		// ItemUsable.getNewStack("Minigun"));

	}

	/*
	 * protected void addWeapons() {
	 * this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
	 * ItemFromData.getNewStack("rocketlauncher")); }
	 */
	@Override
	protected ResourceLocation getLootTable() {
		return TF2weapons.lootSoldier;
	}
	public float[] getDropChance() {
		return new float[] { 0.05f, 0.12f, 0.11f };
	}
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.35D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.275D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
	}

	@Override
	public void onLivingUpdate() {

		if(!this.world.isRemote && this.getAttackTarget() != null){
			if (this.rocketJumper && this.getHealth() > 7f
					&& !this.airborne && this.onGround && this.getHeldItem(EnumHand.MAIN_HAND).getItemDamage() == 0)
				this.rocketJump = true;
			if(this.getDiff()>1 && this.loadout.get(1).getItem() instanceof ItemWeapon){
				if(this.usedSlot==0 && this.getHeldItemMainhand().getItemDamage()==this.getHeldItemMainhand().getMaxDamage() && this.loadout.get(1).getItemDamage()!=this.loadout.get(1).getMaxDamage() && this.getDistanceSqToEntity(this.getAttackTarget())<36){
					//System.out.println("Shotgun switch");
					this.switchSlot(1);
					this.ammoLeft++;
				}
				else if(this.usedSlot==1 && (this.getHeldItemMainhand().getItemDamage()==this.getHeldItemMainhand().getMaxDamage() || this.getDistanceSqToEntity(this.getAttackTarget())>40)){
					this.switchSlot(0);
				}
			}
		}
		/*
		 * if(this.rocketJump&&this.getEntityData().getCompoundTag("TF2").
		 * getShort("reload")<=50){
		 * TF2ActionHandler.playerAction.get(this.world.isRemote).put(this,
		 * 1); this.jump=true; this.rotationYaw-= this.rotationPitch=8;
		 * this.getLookHelper().setLookPosition(this.posX,this.posY-1,this.posZ,
		 * 180, 90.0F); }
		 */
		/*
		 * if(this.rocketJump&&!this.onGround){ this.rocketJump=false;
		 * TF2ActionHandler.playerAction.get(this.world.isRemote).put(this,
		 * 0); }
		 */
		if (this.airborne)
			this.jump = false;
		super.onLivingUpdate();
		/*
		 * if(this.ammoLeft>0&&this.getAttackTarget()!=null&&this.
		 * getDistanceSqToEntity(this.getAttackTarget())<=400&&(!
		 * TF2ActionHandler.playerAction.get(this.world.isRemote).containsKey
		 * (this)||(TF2ActionHandler.playerAction.get(this.world.isRemote).
		 * get(this)&3)==0)){
		 * TF2ActionHandler.playerAction.get(this.world.isRemote).put(this,
		 * TF2ActionHandler.playerAction.get(this.world.isRemote).containsKey
		 * (this)?TF2ActionHandler.playerAction.get(this.world.isRemote).get(
		 * this)+2:2); }
		 */
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		super.fall(distance, this.airborne ? damageMultiplier * 0.35f : damageMultiplier);
		this.airborne = false;
	}

	@Override
	public void onShot() {
		if (this.rocketJump) {
			this.jump = true;
			this.rotationYawHead = this.rotationYawHead + 180;
			this.rotationPitch = 50;
			this.rocketJump = false;
			this.airborne = true;
			// this.getLookHelper().setLookPosition(this.posX,this.posY-1,this.posZ,
			// 180, 90.0F);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return TF2Sounds.MOB_SOLDIER_SAY;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound() {
		return TF2Sounds.MOB_SOLDIER_HURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_SOLDIER_DEATH;
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
			this.entityDropItem(ItemFromData.getNewStack("shotgun"), 0);
		if (this.rand.nextFloat() < 0.05f + p_70628_2_ * 0.025f)
			this.entityDropItem(ItemFromData.getNewStack("rocketlauncher"), 0);
	}
	/*
	 * @Override public float getAttributeModifier(String attribute) {
	 * if(attribute.equals("Minigun Spinup")){ return
	 * super.getAttributeModifier(attribute)*1.5f; } return
	 * super.getAttributeModifier(attribute); }
	 */
}
