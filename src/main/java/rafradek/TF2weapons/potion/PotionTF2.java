package rafradek.TF2weapons.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import rafradek.TF2weapons.TF2weapons;

public class PotionTF2 extends Potion {

	public PotionTF2(boolean isBadEffectIn, int liquidColorIn, int x, int y) {
		super(isBadEffectIn, liquidColorIn);
		this.setIconIndex(x, y);
		
	}
	
	public boolean isReady(int duration, int amplifier) {
		if (this == TF2weapons.bleeding)
			return duration % 10 == 0;
		return false;
    }
	
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		if (this == TF2weapons.bleeding) {
			entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 0.41f * (amplifier+1));
		}
    }
}
