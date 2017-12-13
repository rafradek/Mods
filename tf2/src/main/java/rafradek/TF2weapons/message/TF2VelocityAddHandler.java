package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.message.TF2Message.VelocityAddMessage;

public class TF2VelocityAddHandler implements IMessageHandler<TF2Message.VelocityAddMessage, IMessage> {

	@Override
	public IMessage onMessage(final VelocityAddMessage message, MessageContext ctx) {
		Minecraft.getMinecraft().player.addVelocity(message.x, message.y, message.z);
		Minecraft.getMinecraft().player.isAirBorne=message.airborne;
		
		return null;
	}

}
