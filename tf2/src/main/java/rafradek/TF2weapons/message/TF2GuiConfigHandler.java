package rafradek.TF2weapons.message;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.gui.GuiConfigurable2;
import rafradek.TF2weapons.client.gui.inventory.GuiConfigurable;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.entity.building.EntityTeleporter;
import rafradek.TF2weapons.inventory.ContainerConfigurable;
import rafradek.TF2weapons.message.TF2Message.BuildingConfigMessage;
import rafradek.TF2weapons.message.TF2Message.GuiConfigMessage;
import rafradek.TF2weapons.tileentity.IEntityConfigurable;

public class TF2GuiConfigHandler implements IMessageHandler<TF2Message.GuiConfigMessage, IMessage> {

	public static HashMap<Entity, float[]> shotInfo = new HashMap<Entity, float[]>();

	@Override
	public IMessage onMessage(final GuiConfigMessage message, final MessageContext ctx) {

		if (ctx.side == Side.SERVER) {
			final EntityPlayerMP player = ctx.getServerHandler().player;
			((WorldServer) player.world).addScheduledTask(new Runnable() {

				@Override
				public void run() {
					TileEntity ent = player.world.getTileEntity(message.pos);
					if (ent instanceof IEntityConfigurable) {
						((IEntityConfigurable)ent).readConfig(message.tag);
					}
					/*if (player.openContainer instanceof ContainerConfigurable) {
						((ContainerConfigurable)player.openContainer).config.readConfig(message.tag);
					}*/
				}
			});
		}
		else {
			final EntityPlayer player = Minecraft.getMinecraft().player;
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {

				@Override
				public void run() {
					Minecraft.getMinecraft().displayGuiScreen(GuiConfigurable2.create(message.tag, message.pos));
					/*if (Minecraft.getMinecraft().currentScreen instanceof GuiConfigurable) {
						((GuiConfigurable)Minecraft.getMinecraft().currentScreen).tag=message.tag;
						Minecraft.getMinecraft().currentScreen.initGui();
					}*/
				}
				
			});
		}

		return null;
	}

}
