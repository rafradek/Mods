package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.message.TF2Message.CapabilityMessage;
import rafradek.TF2weapons.util.PropertyType;

public class TF2CapabilityHandler implements IMessageHandler<TF2Message.CapabilityMessage, IMessage> {

	@Override
	public IMessage onMessage(final CapabilityMessage message, MessageContext ctx) {
		if (ctx.side.isClient())
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Entity ent = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
					if (ent != null && ent.hasCapability(TF2weapons.WEAPONS_CAP, null)) {
						WeaponsCapability cap = ent.getCapability(TF2weapons.WEAPONS_CAP, null);
						int prevHealTarget=cap.getHealTarget();
						if(message.entries != null) {
							for(DataEntry<?> param: message.entries) {
								cap.onChangeValue(param.getKey(), param.getValue());
							}
							cap.dataManager.setEntryValues(message.entries);
						}
						if (prevHealTarget != cap.getHealTarget() && message.healTarget > 0) {
							SoundEvent sound = ItemFromData.getSound(
									((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND),
									PropertyType.HEAL_START_SOUND);
							ClientProxy.playWeaponSound((EntityLivingBase) ent, sound, false, 0,
									((EntityLivingBase) ent).getHeldItem(EnumHand.MAIN_HAND));
						}
						//cap.critTime = message.critTime;
						//cap.collectedHeads = message.heads;
					}
				}
			});
		/*
		 * if(ent !=null){ ent.getEntityData().setTag("TF2", message.tag); }
		 */
		else {
			ctx.getServerHandler().player.getCapability(TF2weapons.WEAPONS_CAP,
					null).setHealTarget(message.healTarget);
			/*message.entityID = ctx.getServerHandler().player.getEntityId();
			TF2weapons.sendTracking(message, ctx.getServerHandler().player);*/
		}
		return null;
	}

}
