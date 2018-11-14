package rafradek.TF2weapons.message;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.entity.building.EntityTeleporter;
import rafradek.TF2weapons.message.TF2Message.GuiConfigMessage;

public class TF2GuiConfigHandler implements IMessageHandler<TF2Message.GuiConfigMessage, IMessage> {

	public static HashMap<Entity, float[]> shotInfo = new HashMap<Entity, float[]>();

	@Override
	public IMessage onMessage(final GuiConfigMessage message, final MessageContext ctx) {

		TF2weapons.server.addScheduledTask(new Runnable() {

			@Override
			public void run() {

				Entity ent = ctx.getServerHandler().player.world.getEntityByID(message.entityid);
				if (ent != null && ent instanceof EntityBuilding
						&& ((EntityBuilding) ent).getOwner() == ctx.getServerHandler().player) {
					if (message.id == 127) {
						((EntityBuilding) ent).grab();
						return;
					}

					if (ent instanceof EntityTeleporter) {
						if (message.id == 0)
							((EntityTeleporter) ent).setID(MathHelper.clamp(message.value,0,EntityTeleporter.TP_PER_PLAYER-1));
						else if (message.id == 1)
							((EntityTeleporter) ent).setExit(message.value == 1);
					} else if (ent instanceof EntitySentry)
						if (message.id == 0)
							((EntitySentry) ent).setTargetInfo(message.value);
				}
			}
		});

		return null;
	}

}
