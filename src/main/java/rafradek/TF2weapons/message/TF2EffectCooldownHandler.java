package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.message.TF2Message.EffectCooldownMessage;

public class TF2EffectCooldownHandler implements IMessageHandler<TF2Message.EffectCooldownMessage, IMessage> {

	@Override
	public IMessage onMessage(final EffectCooldownMessage message, MessageContext ctx) {
		if (ctx.side.isClient())
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					WeaponsCapability cap =Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null);
					cap.effectsCool.put(message.name, message.time);
					//cap.critTime = message.critTime;
					//cap.collectedHeads = message.heads;
				}
			}
					);
		return null;
	}

}
