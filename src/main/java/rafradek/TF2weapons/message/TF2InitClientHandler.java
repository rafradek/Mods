package rafradek.TF2weapons.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.common.WeaponsCapability;

public class TF2InitClientHandler implements IMessageHandler<TF2Message.InitClientMessage, IMessage> {

	@Override
	public IMessage onMessage(TF2Message.InitClientMessage message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;
		((WorldServer)player.world).addScheduledTask(() -> {
			TF2PlayerCapability.get(player).breakBlocks = message.breakBlocks;
			WeaponsCapability.get(player).sentryTargets = message.sentryTargets;
			WeaponsCapability.get(player).dispenserPlayer = message.dispenserPlayer;
			WeaponsCapability.get(player).teleporterPlayer = message.teleporterPlayer;
			WeaponsCapability.get(player).teleporterEntity = message.teleporterEntity;
		});
			// System.out.println("setting "+message.value);
			// TF2weapons.proxy.playReloadSound(player,stack);
		return null;
	}
}
