package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.item.ItemAmmo;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;

public class TF2UseHandler implements IMessageHandler<TF2Message.UseMessage, IMessage> {

	@Override
	public IMessage onMessage(TF2Message.UseMessage message, MessageContext ctx) {
		EntityPlayer player = TF2weapons.proxy.getPlayerForSide(ctx);
		if (player != null){
		ItemStack stack = player.getHeldItem(message.hand);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemUsable){
			if(message.newAmmo!=-1){
				if(TF2Attribute.getModifier("Ball Release", stack, 0, player)!=0)
					stack=ItemFromData.getNewStack("sandmanball");
				if (((ItemUsable) stack.getItem()).getAmmoType(stack) < ItemAmmo.AMMO_TYPES.length)
					player.getCapability(TF2weapons.PLAYER_CAP, null).cachedAmmoCount[((ItemUsable) stack.getItem()).getAmmoType(stack)]=message.newAmmo;
			}
			
			if (stack.getItem() instanceof ItemWeapon && ((ItemWeapon) stack.getItem()).hasClip(stack)) {
				stack.setItemDamage(message.value);
				final ItemStack fstack=stack;
				if (message.reload && message.value != 0)
				/*
				 * if(stack.getItemDamage()==0&&TF2ActionHandler.playerAction.
				 * get().get(player)!=null&&(TF2ActionHandler.playerAction.get()
				 * .get(player)&8)==0){
				 * TF2ActionHandler.playerAction.get().put(player, arg1) }
				 */
				Minecraft.getMinecraft().addScheduledTask(()->
				{
						TF2weapons.proxy.playReloadSound(player, fstack);
				});
					
			}
		}
		}
			// System.out.println("setting "+message.value);
			// TF2weapons.proxy.playReloadSound(player,stack);
		return null;
	}
}
