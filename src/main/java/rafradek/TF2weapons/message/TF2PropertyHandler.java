package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemMedigun;
import rafradek.TF2weapons.message.TF2Message.PropertyMessage;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.TF2Util;

public class TF2PropertyHandler implements IMessageHandler<TF2Message.PropertyMessage, IMessage> {

	@Override
	public IMessage onMessage(final PropertyMessage message, MessageContext ctx) {

		if (ctx.side.isClient())
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					if (Minecraft.getMinecraft().world == null)
						return;
					Entity ent = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
					if (ent != null && ent.hasCapability(TF2weapons.WEAPONS_CAP, null)) {
						if (ent instanceof EntityLivingBase
								&& ((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND) != null
								&& ((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND)
										.getItem() instanceof ItemMedigun)
							if (message.name.equals("HealTarget")
									&& ent.getEntityData().getInteger("HealTarget") != message.intValue
									&& message.intValue > 0) {
								SoundEvent sound = ItemFromData.getSound(
										((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND),
										PropertyType.HEAL_START_SOUND);
								ClientProxy.playWeaponSound((EntityLivingBase) ent, sound, false, 0,
										((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND));
							}

						if (message.type == 0)
							ent.getEntityData().setInteger(message.name, message.intValue);
						else if (message.type == 1)
							ent.getEntityData().setFloat(message.name, message.floatValue);
						else if (message.type == 2)
							ent.getEntityData().setByte(message.name, message.byteValue);
						else if (message.type == 3)
							ent.getEntityData().setString(message.name, message.stringValue);
					}
				}
			});
		else {
			if (message.type == 0)
				ctx.getServerHandler().player.getEntityData().setInteger(message.name, message.intValue);
			else if (message.type == 1)
				ctx.getServerHandler().player.getEntityData().setFloat(message.name, message.floatValue);
			else if (message.type == 2)
				ctx.getServerHandler().player.getEntityData().setByte(message.name, message.byteValue);
			else if (message.type == 3)
				ctx.getServerHandler().player.getEntityData().setString(message.name, message.stringValue);
			// System.out.println("send: "+message.name+" "+message.intValue+"
			// "+message.floatValue);
			message.entityID = ctx.getServerHandler().player.getEntityId();
			TF2Util.sendTracking(message, ctx.getServerHandler().player);
		}
		return null;
	}

}
