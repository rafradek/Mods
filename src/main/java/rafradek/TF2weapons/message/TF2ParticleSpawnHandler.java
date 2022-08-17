package rafradek.TF2weapons.message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.client.particle.EnumTF2Particles;
import rafradek.TF2weapons.message.TF2Message.ParticleSpawnMessage;

public class TF2ParticleSpawnHandler implements IMessageHandler<ParticleSpawnMessage, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(final ParticleSpawnMessage message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask( () -> {
			IParticleFactory factory = ClientProxy.particleFactories.get(EnumTF2Particles.values()[message.id]);
			for(int i = 0; i < message.count; i++) {
				ParticleSpawnMessage message2 = message;
				Particle particle = factory.createParticle(message.id, Minecraft.getMinecraft().world, message.x, message.y, message.z, message.offsetX, message.offsetY, message.offsetZ, message.params);
				ClientProxy.spawnParticle(Minecraft.getMinecraft().world, particle);
			}
		}
		);
		return null;
	}

}
