package rafradek.TF2weapons.util;

import net.minecraft.item.ItemStack;

public interface TF2DamageSource {

	public static int BACKSTAB = 1;
	public static int HEADSHOT = 2;
	public static int SENTRY_PDA = 4;
	public static int SENTRY = 8;
	
	ItemStack getWeapon();
	ItemStack getWeaponOrig();
	//void onShieldBlock(EntityLivingBase living);
	int getCritical();
	void setAttackSelf();
	int getAttackFlags();
	void addAttackFlag(int flag);
	
	default boolean hasAttackFlag(int flag) {
		return (this.getAttackFlags() & flag) == flag;
	}
	
	float getAttackPower();
	void setAttackPower(float power);
}