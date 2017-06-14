package rafradek.TF2weapons;

import net.minecraft.potion.Potion;

public class PotionTF2 extends Potion {

	public PotionTF2(boolean isBadEffectIn, int liquidColorIn, int x, int y) {
		super(isBadEffectIn, liquidColorIn);
		this.setIconIndex(x, y);
		
	}

}
