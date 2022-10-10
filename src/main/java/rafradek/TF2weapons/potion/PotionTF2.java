package rafradek.TF2weapons.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.util.ReflectionAccess;

public class PotionTF2 extends Potion {

	public PotionTF2(boolean isBadEffectIn, int liquidColorIn, int x, int y) {
		super(isBadEffectIn, liquidColorIn);
		this.setIconIndex(x, y);

	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		if (this == TF2weapons.bleeding)
			return duration % 10 == 0;
		return false;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		if (this == TF2weapons.bleeding && !entityLivingBaseIn.world.isRemote) {
			((WorldServer) entityLivingBaseIn.world).spawnParticle(EnumParticleTypes.REDSTONE, entityLivingBaseIn.posX,
					entityLivingBaseIn.posY + entityLivingBaseIn.height / 2, entityLivingBaseIn.posZ, 7,
					entityLivingBaseIn.width / 2, entityLivingBaseIn.height / 2, entityLivingBaseIn.width / 2, 0);
			if (entityLivingBaseIn.getRevengeTarget() instanceof EntityPlayer) {
				try {
					ReflectionAccess.entityRecentlyHit.setInt(entityLivingBaseIn, 100);
				} catch (Exception e) {}
				entityLivingBaseIn.setRevengeTarget(entityLivingBaseIn.getRevengeTarget());
			}
			entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 0.41f * (amplifier + 1));
		}
	}
}
