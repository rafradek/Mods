package rafradek.TF2weapons.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.item.ItemDisguiseKit;
import rafradek.TF2weapons.message.TF2Message.DisguiseMessage;

public class TF2DisguiseHandler implements IMessageHandler<TF2Message.DisguiseMessage, IMessage> {

	@Override
	public IMessage onMessage(final DisguiseMessage message, MessageContext ctx) {
		final EntityPlayerMP player = ctx.getServerHandler().player;
		((WorldServer) player.world).addScheduledTask(new Runnable() {

			@Override
			public void run() {
				ItemStack stack;
				if (((stack = player.getHeldItemMainhand()) != null && stack.getItem() instanceof ItemDisguiseKit)
						|| ((stack = player.getHeldItemOffhand()) != null
								&& stack.getItem() instanceof ItemDisguiseKit)) {
					ItemDisguiseKit.startDisguise(player, player.world, message.value);
					if (!player.capabilities.isCreativeMode)
						stack.damageItem(1, player);
				}
			}

		});
		return null;
	}

}
