package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.message.TF2Message.CapabilityMessage;
import rafradek.TF2weapons.message.TF2Message.PlayerCapabilityMessage;
import rafradek.TF2weapons.util.PropertyType;

public class TF2PlayerCapabilityHandler implements IMessageHandler<PlayerCapabilityMessage, IMessage> {

	@Override
	public IMessage onMessage(final PlayerCapabilityMessage message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Entity ent = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
				if (ent != null && ent.hasCapability(TF2weapons.WEAPONS_CAP, null)) {
					TF2PlayerCapability cap = ent.getCapability(TF2weapons.PLAYER_CAP, null);
					if(message.entries != null) {
						for(DataEntry<?> param: message.entries) {
							cap.onChangeValue(param.getKey(), param.getValue());
						}
						cap.dataManager.setEntryValues(message.entries);
					}
				}
			}
		});
		return null;
	}

}
