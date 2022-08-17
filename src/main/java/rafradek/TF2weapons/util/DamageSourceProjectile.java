package rafradek.TF2weapons.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.projectile.EntityProjectileBase;

@SuppressWarnings("deprecation")
public class DamageSourceProjectile extends EntityDamageSourceIndirect implements TF2DamageSource {
	public ItemStack weapon;
	public int critical;
	public boolean notProjectile;
	public boolean selfdmg;
	private int attackFlags;
	private float power = 1;
	public DamageSourceProjectile(ItemStack weapon, Entity projectile, Entity shooter, int critical) {
		super("bullet", projectile, shooter);
		this.weapon = weapon;
		this.critical = critical;
	}

	@Override
	public boolean isDifficultyScaled() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see rafradek.TF2weapons.TF2DamageSource#getWeapon()
	 */
	@Override
	public ItemStack getWeapon() {
		return weapon;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see rafradek.TF2weapons.TF2DamageSource#getCritical()
	 */
	@Override
	public int getCritical() {
		return critical;
	}

	/*
	 * public DamageSource bypassesArmor() { this.setDamageBypassesArmor();
	 * return this; }
	 */
	/**
	 * Returns the message to be displayed on player death.
	 */
	@Override
	public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_) {
		// ItemStack itemstack = this.damageSourceEntity instanceof
		// EntityLivingBase ?
		// ((EntityLivingBase)this.damageSourceEntity).getHeldItem(EnumHand.MAIN_HAND)
		// : null;
		if (this.getTrueSource() == TF2weapons.dummyEnt)
			return new TextComponentTranslation("death.attack.explosion", p_151519_1_.getDisplayName());
		String s = "death.attack." + this.damageType;
		String s1 = s + ".item";
		return weapon != null && I18n.canTranslate(s1)
				? new TextComponentTranslation(s1,
						new Object[] { p_151519_1_.getDisplayName(), this.getTrueSource().getDisplayName(),
								weapon.getDisplayName() })
						: new TextComponentTranslation(s,
								new Object[] { p_151519_1_.getDisplayName(), this.getTrueSource().getDisplayName() });
	}

	/*
	 * public String getDeathMessage(EntityPlayer par1EntityPlayer) { return
	 * StatCollector.translateToLocalFormatted("death." + this.damageType, new
	 * Object[] {par1EntityPlayer.getDisplayName(),
	 * this.shooter.getCommandSenderName(),
	 * StatCollector.translateToLocal(this.weapon)}); }
	 */
	@Override
	public Vec3d getDamageLocation() {
		return this.isExplosion() ? null : this.damageSourceEntity.getPositionVector();
	}

	public void removeProjecileStatus() {
		this.notProjectile = true;
	}

	@Override
	public boolean isProjectile() {
		return super.isProjectile() && !this.notProjectile;
	}

	@Override
	public Entity getTrueSource(){
		return selfdmg?TF2weapons.dummyEnt:super.getTrueSource();
	}

	@Override
	public void setAttackSelf(){
		this.selfdmg=true;
	}
	@Override
	public ItemStack getWeaponOrig() {
		return this.getImmediateSource() instanceof EntityProjectileBase && !((EntityProjectileBase)this.getImmediateSource()).usedWeaponOrig.isEmpty() ?
				((EntityProjectileBase)this.getImmediateSource()).usedWeaponOrig:this.getWeapon();
	}

	@Override
	public int getAttackFlags() {
		return this.attackFlags;
	}

	@Override
	public void addAttackFlag(int flag) {
		this.attackFlags+=flag;
	}

	@Override
	public float getAttackPower() {
		return this.power;
	}

	@Override
	public void setAttackPower(float power) {
		this.power = power;
	}
	/*@Override
	public void onShieldBlock(EntityLivingBase living) {}*/

}
