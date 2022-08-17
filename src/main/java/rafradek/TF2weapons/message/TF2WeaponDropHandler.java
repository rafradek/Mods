package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.message.TF2Message.WeaponDroppedMessage;

public class TF2WeaponDropHandler implements IMessageHandler<TF2Message.WeaponDroppedMessage, IMessage> {

	public static int size;

	@Override
	public IMessage onMessage(final WeaponDroppedMessage message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable(){

			@Override
			public void run() {
				//System.out.println("Wep drop "+message.name);
				ItemStack stack=ItemFromData.getNewStack(message.name);
				((ItemUsable)stack.getItem()).holster(Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null), stack, Minecraft.getMinecraft().player, Minecraft.getMinecraft().world);
				
			}
			
		});
		
		return null;
	}

}
