package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message.WearableChangeMessage;

public class TF2WearableChangeHandler implements IMessageHandler<TF2Message.WearableChangeMessage, IMessage> {

	@Override
	public IMessage onMessage(final WearableChangeMessage message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
				if (entity != null) {
					if (message.slot < 20)
						entity.getCapability(TF2weapons.INVENTORY_CAP, null).setInventorySlotContents(message.slot,
								message.stack);
					else if (entity instanceof EntityTF2Character) {
						((EntityTF2Character)entity).loadout.setStackInSlot(message.slot-20, message.stack);
					}
				}
					
			}
		});
		return null;
	}

}
