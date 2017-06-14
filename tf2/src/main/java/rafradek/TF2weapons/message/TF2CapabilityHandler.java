package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2Message.CapabilityMessage;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2CapabilityHandler implements IMessageHandler<TF2Message.CapabilityMessage, IMessage> {

	@Override
	public IMessage onMessage(final CapabilityMessage message, MessageContext ctx) {
		if (ctx.side.isClient())
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Entity ent = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
					if (ent != null && ent.hasCapability(TF2weapons.WEAPONS_CAP, null)) {
						WeaponsCapability cap = ent.getCapability(TF2weapons.WEAPONS_CAP, null);
						if (cap.healTarget != message.healTarget && message.healTarget > 0) {
							SoundEvent sound = ItemFromData.getSound(
									((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND),
									PropertyType.HEAL_START_SOUND);
							ClientProxy.playWeaponSound((EntityLivingBase) ent, sound, false, 0,
									((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND));
						}

						cap.healTarget = message.healTarget;
						cap.critTime = message.critTime;
						cap.collectedHeads = message.heads;
					}
				}
			});
		/*
		 * if(ent !=null){ ent.getEntityData().setTag("TF2", message.tag); }
		 */
		else {
			ctx.getServerHandler().playerEntity.getCapability(TF2weapons.WEAPONS_CAP,
					null).healTarget = message.healTarget;
			message.entityID = ctx.getServerHandler().playerEntity.getEntityId();
			TF2weapons.sendTracking(message, ctx.getServerHandler().playerEntity);
		}
		return null;
	}

}
