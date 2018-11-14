package rafradek.rig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;

public class RIGEvents {
	public static HashMap<Entity, Integer> extraBurn = new HashMap<Entity, Integer>();
	public static ArrayList<DestroyBlockEntry> destroyProgress = new ArrayList<>();

	@SubscribeEvent
	public void attachCapabilityEnt(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(RIG.MOD_ID,"rig_item"), new RIGCapabilityProvider());
		}
	}
	
	@SubscribeEvent
	public void livingUpdate(LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if(living.hasCapability(RIG.RIG_ITEM, null)) {
			ItemStack rig = living.getCapability(RIG.RIG_ITEM, null).getStackInSlot(0);
			if(rig.hasTagCompound() && living.isInWater()) {
				int extraair = 100 + RIGUpgrade.upgradesMap.get("Air").getAttributeValue(rig) * 100;
				if (living.getRNG().nextInt(300+extraair)>=300) {
					living.setAir(living.getAir()+1);
				}
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void livingAttack(LivingHurtEvent event) {
		if(event.isCanceled() || event.getSource().isUnblockable())
			return;
		if(event.getEntityLiving().hasCapability(RIG.RIG_ITEM, null)) {
			ItemStack rig=event.getEntityLiving().getCapability(RIG.RIG_ITEM, null).getStackInSlot(0);
			if(rig.hasTagCompound()) {
				int reslevel=1+rig.getTagCompound().getInteger("ResU");
				if(event.getAmount() > 1f)
					event.setAmount(Math.max(1f,event.getAmount() - reslevel * 0.5f));
				
			}
		}
	}
	
	public static class DestroyBlockEntry {
		public BlockPos pos;
		public float curDamage;
		public World world;

		public DestroyBlockEntry(BlockPos pos, World world) {
			this.world = world;
			this.pos = pos;
		}
	}
}
