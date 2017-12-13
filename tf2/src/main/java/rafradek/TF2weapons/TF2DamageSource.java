package rafradek.TF2weapons;

import net.minecraft.item.ItemStack;

public interface TF2DamageSource {

	public static int BACKSTAB=1;
	public static int HEADSHOT=2;
	
	ItemStack getWeapon();
	ItemStack getWeaponOrig();
	//void onShieldBlock(EntityLivingBase living);
	int getCritical();
	void setAttackSelf();
	int getAttackFlags();
	void addAttackFlag(int flag);
	
}