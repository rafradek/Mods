package rafradek.TF2weapons.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class TF2ProjectileHandler implements IMessageHandler<TF2Message.PredictionMessage, IMessage> {

	// public static HashMap<Entity, ArrayList<PredictionMessage>> nextShotPos=
	// new HashMap<Entity, ArrayList<PredictionMessage>>();

	@Override
	public IMessage onMessage(final PredictionMessage message, MessageContext ctx) {
		final EntityPlayer shooter = ctx.getServerHandler().player;
		// ItemStack stack=shooter.getHeldItem(EnumHand.MAIN_HAND);
		((WorldServer) shooter.world).addScheduledTask(new Runnable() {

			@Override
			public void run() {
				shooter.getCapability(TF2weapons.WEAPONS_CAP, null).predictionList.add(message);
			}

		});
		return null;
	}

}
