package rafradek.TF2weapons.message;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.gui.GuiTeamSelect;
import rafradek.TF2weapons.message.TF2Message.ShowGuiMessage;
import rafradek.TF2weapons.util.TF2Util;

public class TF2ShowGuiHandler implements IMessageHandler<TF2Message.ShowGuiMessage, IMessage> {

	@Override
	public IMessage onMessage(final ShowGuiMessage message, MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			if (message.id != 99)
				FMLNetworkHandler.openGui(ctx.getServerHandler().player, TF2weapons.instance, message.id,
						ctx.getServerHandler().player.world, 0, 0, 0);
			else {

			}
		} else {
			if (message.id == 100) {
				NBTTagList listTeams = message.data.getTagList("Teams", 8);
				List<String> teams = TF2Util.NBTTagListToList(listTeams, String.class);
				int[] counts = message.data.getIntArray("Count");
				boolean[] allowed = new boolean[counts.length];
				byte[] allowedbyte = message.data.getByteArray("Allowed");
				for (int i = 0; i < allowed.length; i++) {
					allowed[i] = allowedbyte[i] == 1;
				}
				Minecraft.getMinecraft().addScheduledTask(
						() -> Minecraft.getMinecraft().displayGuiScreen(new GuiTeamSelect(teams, counts, allowed)));

			}
		}
		return null;
	}

}
