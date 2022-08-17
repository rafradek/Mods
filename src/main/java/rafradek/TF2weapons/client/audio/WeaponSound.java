package rafradek.TF2weapons.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.util.WeaponData;

public class WeaponSound extends MovingSound {
	public EntityLivingBase entity;
	public int type;
	public WeaponData conf;
	public boolean endsnextTick;
	public WeaponSound playOnEnd;

	public WeaponSound(SoundEvent p_i45104_1_, EntityLivingBase entity, int type, WeaponData conf) {
		super(p_i45104_1_, SoundCategory.NEUTRAL);
		this.type = type;
		this.entity = entity;
		this.conf = conf;
		this.volume = entity instanceof EntityPlayer ? TF2ConfigVars.gunVolume : TF2ConfigVars.mercenaryVolume;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if (this.endsnextTick)
			this.setDone();
		this.xPosF = (float) entity.posX;
		this.yPosF = (float) entity.posY;
		this.zPosF = (float) entity.posZ;
		if (/*
			 * !(entity instanceof EntityPlayer &&
			 * ((EntityPlayer)entity).inventory.currentItem != slot)
			 */ItemFromData.getData(entity.getHeldItem(EnumHand.MAIN_HAND)) != conf || this.entity.isDead)
			this.setDone();
	}

	public void setDone() {
		ClientProxy.fireSounds.remove(entity);
		if (this.playOnEnd != null) {
			Minecraft.getMinecraft().getSoundHandler().playSound(playOnEnd);
			ClientProxy.fireSounds.put(entity, playOnEnd);
		}
		this.donePlaying = true;
	}
}
