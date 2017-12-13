package rafradek.TF2weapons.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;

public class TF2AttackSyncHandler implements IMessageHandler<TF2Message.AttackSyncMessage, IMessage> {

	@Override
	public IMessage onMessage(TF2Message.AttackSyncMessage message, MessageContext ctx) {
		EntityPlayer player = TF2weapons.proxy.getPlayerForSide(ctx);
		//System.out.println("Time: "+(System.currentTimeMillis() - message.time));
		/*if (player != null){
			System.out.println("Delay: "+message.time);
			if(message.time>1) {
				WeaponsCapability.get(player).fire1Cool+=50*(message.time-1);
				WeaponsCapability.get(player).fire2Cool+=50*(message.time-1);
			}
		}*/
			// System.out.println("setting "+message.value);
			// TF2weapons.proxy.playReloadSound(player,stack);
		return null;
	}
}
