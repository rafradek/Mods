package rafradek.TF2weapons.item;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.IEntityTF2;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;
import rafradek.TF2weapons.util.TF2Util;
import rafradek.TF2weapons.util.Contract.Objective;
import rafradek.TF2weapons.util.PropertyType;

public class ItemMedigun extends ItemUsable {

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		// if(!world.isRemote||living !=
		// Minecraft.getMinecraft().player!(TF2weapons.medigunLock&&living.getCapability(TF2weapons.WEAPONS_CAP,
		// null).healTarget>0)) return false;
		// System.out.println("View: "+var4+" "+startX+" "+startY+" "+startZ+"
		// "+startX+endX+" "+endY+" "+endZ);
		if (world.isRemote && living == Minecraft.getMinecraft().player) {
			RayTraceResult trace = this.trace(stack, living, world);
			if (world.getEntityByID(living.getCapability(TF2weapons.WEAPONS_CAP, null).getHealTarget()) == null
					&& trace != null && trace.entityHit != null && trace.entityHit instanceof EntityLivingBase
					&& !(trace.entityHit instanceof EntityBuilding)) {
				List<RayTraceResult> list = new ArrayList<RayTraceResult>();
				trace.hitInfo = new float[] { 0, 0 };
				list.add(trace);
				message.target = list;
				// System.out.println("healing:
				// "+trace.entityHit.getEntityId());
				// living.getCapability(TF2weapons.aaWEAPONS_CAP,
				// null).healTarget=trace.entityHit.getEntityId();
				// TF2weapons.network.sendToServer(new
				// TF2Message.CapabilityMessage(living));

				// ClientProxy.playWeaponSound(living,
				// ItemFromData.getSound(stack,PropertyType.HEAL_START_SOUND),
				// false, 0, stack);
			}
		} else if (!world.isRemote && message != null && message.readData != null) {
			living.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget((int) message.readData.get(0)[0]);
		}
		return true;
	}

	public void heal(ItemStack stack, EntityLivingBase living, World world, EntityLivingBase target) {
		
		if (living instanceof EntityPlayer && !((EntityPlayer) living).capabilities.isCreativeMode) {
			ItemStack stackAmmo = this.searchForAmmo(living, stack);
			if (!stackAmmo.isEmpty())
				((ItemAmmo) stackAmmo.getItem()).consumeAmmo(living, stackAmmo,
						this.getActualAmmoUse(stack, living, target.getHealth() >= target.getMaxHealth()?1:2));
			else
				return;
		}
		int lastHitTime = target.ticksExisted - target.getEntityData().getInteger("lasthit");
		float heal = this.getHealAmount(stack, living);
		if (!(target instanceof IEntityTF2))
			heal *=TF2ConfigVars.damageMultiplier;
		if (lastHitTime > 200)
			heal *= 1 + Math.min(2, (lastHitTime - 200f) / 50f);
		float overheal = heal + target.getMaxHealth() * 0.001666f;
		float ubercharge = TF2Attribute.getModifier("Uber Rate", stack, 0.00125f, living);
		if (target.getHealth() < target.getMaxHealth()) {
			overheal = (target.getHealth() + heal) - target.getMaxHealth() + 0.04f;
			target.heal(heal);
			if(living instanceof EntityPlayerMP && (target instanceof EntityTF2Character || target instanceof EntityPlayer)) {
				if((living.getCapability(TF2weapons.PLAYER_CAP, null).healed+=heal)>=20) {
					living.getCapability(TF2weapons.PLAYER_CAP, null).healed-=20;
					living.getCapability(TF2weapons.PLAYER_CAP, null).completeObjective(Objective.HEAL_20, stack);
				}
			}
		}
		if (target.getHealth() >= target.getMaxHealth()
				&& target.getAbsorptionAmount() < target.getMaxHealth() * this.getMaxOverheal(stack, living, target)) {
			target.setAbsorptionAmount(Math.min(target.getAbsorptionAmount() + overheal,
					target.getMaxHealth() * this.getMaxOverheal(stack, living, null)));
			target.getDataManager().set(TF2EventsCommon.ENTITY_OVERHEAL,
					target.getAbsorptionAmount()/*
												 * Math.max(target.getEntityData
												 * ().getFloat("overhealamount")
												 * +overheal,target.getMaxHealth
												 * ()*this.getMaxOverheal(stack,
												 * living))
												 */);
			// TF2weapons.sendTracking(new
			// TF2Message.PropertyMessage("overheal",
			// target.getAbsorptionAmount(),target),target);
		}
		
		if (target.getHealth() >= target.getMaxHealth()
				&& target.getAbsorptionAmount() >= target.getMaxHealth() * (this.getMaxOverheal(stack, living, target) - 0.075))
			ubercharge /= 2;
		if (!stack.getTagCompound().getBoolean("Activated") && stack.getTagCompound().getFloat("ubercharge") < 1) {
			stack.getTagCompound().setFloat("ubercharge",
					Math.min(1, stack.getTagCompound().getFloat("ubercharge") + ubercharge));
			if (stack.getTagCompound().getFloat("ubercharge") == 1)
				living.playSound(ItemFromData.getSound(stack, PropertyType.CHARGED_SOUND), 1.25f, 1);
		}
	}

	public RayTraceResult trace(ItemStack stack, EntityLivingBase living, World world) {
		double startX = 0;
		double startY = 0;
		double startZ = 0;

		double endX = 0;
		double endY = 0;
		double endZ = 0;

		// double[]
		// rand=TF2weapons.radiusRandom3D(this.getWeaponSpread(stack,living),
		// world.rand);

		startX = living.posX;// - (double)(MathHelper.cos(living.rotationYaw /
								// 180.0F * (float)Math.PI) * 0.16F);
		startY = living.posY + living.getEyeHeight();
		startZ = living.posZ;// - (double)(MathHelper.sin(living.rotationYaw /
								// 180.0F * (float)Math.PI) * 0.16F);

		// double[] rand=TF2weapons.radiusRandom2D(this.getWeaponSpread(stack),
		// world.rand);

		// float spreadPitch = (float) (living.rotationPitch / 180 + rand[1]);
		// float spreadYaw = (float) (living.rotationYaw / 180 +
		// rand[0]*(90/Math.max(90-Math.abs(spreadPitch*180),0.0001f)));
		// System.out.println("Rot: "+living.rotationYawHead+"
		// "+living.rotationPitch);
		float spreadPitch = living.rotationPitch / 180;
		float spreadYaw = living.rotationYawHead / 180;

		endX = -MathHelper.sin(spreadYaw * (float) Math.PI) * MathHelper.cos(spreadPitch * (float) Math.PI);
		endY = (-MathHelper.sin(spreadPitch * (float) Math.PI));
		endZ = MathHelper.cos(spreadYaw * (float) Math.PI) * MathHelper.cos(spreadPitch * (float) Math.PI);

		float var9 = MathHelper.sqrt(endX * endX + endY * endY + endZ * endZ);
		// float[] ratioX= this.calculateRatioX(living.rotationYaw,
		// living.rotationPitch);
		// float[] ratioY= this.calculateRatioY(living.rotationYaw,
		// living.rotationPitch);
		// float
		// wrapAngledYaw=MathHelper.wrapAngleTo180_float(living.rotationYaw);
		// float
		// fixedYaw=Math.max(Math.abs(wrapAngledYaw),90)-Math.min(Math.abs(wrapAngledYaw),90);

		double range = getData(stack).getFloat(PropertyType.RANGE);
		endX = (endX / var9) * range;
		endY = (endY / var9) * range;
		endZ = (endZ / var9) * range;
		List<RayTraceResult> list = TF2Util.pierce(world, living, startX, startY, startZ, startX + endX,
				startY + endY, startZ + endZ, false, 0.2f, false);
		return !list.isEmpty() && list.get(0).entityHit != null ? list.get(0) : null;

	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		if (par1ItemStack.isEmpty())
			return;
		if (par5 && !this.canFire(par2World, (EntityLivingBase) par3Entity, par1ItemStack)) {
			par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget(-1);
		}
		Potion effect=Potion.getPotionFromResourceLocation(ItemFromData.getData(par1ItemStack).getString(PropertyType.EFFECT_TYPE));
		if (!par2World.isRemote&&par1ItemStack.getTagCompound().getBoolean("Activated")) {
			par1ItemStack.getTagCompound().setFloat("ubercharge",
					Math.max(0, par1ItemStack.getTagCompound().getFloat("ubercharge") - 0.00625f));
			if(par5 && effect != null && par3Entity.ticksExisted%4==0)
				TF2Util.addAndSendEffect(((EntityLivingBase)par3Entity),new PotionEffect(effect,15));
			if (par1ItemStack.getTagCompound().getFloat("ubercharge") == 0) {

				par1ItemStack.getTagCompound().setBoolean("Activated", false);
				TF2Util.playSound(par3Entity,ItemFromData.getSound(par1ItemStack, PropertyType.UBER_STOP_SOUND), 1.5f, 1);
				((EntityLivingBase)par3Entity).removePotionEffect(effect);
				
				// TF2weapons.sendTracking(new
				// TF2Message.PropertyMessage("UberCharged",
				// (byte)0,par3Entity),par3Entity);
			}
		}
		if(par5 && !par2World.isRemote){
			Entity healTargetEnt = par2World.getEntityByID(par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null).getHealTarget());
			if(healTargetEnt != null && healTargetEnt instanceof EntityLivingBase){
				EntityLivingBase healTarget=(EntityLivingBase) healTargetEnt;
				// System.out.println("healing:
				// "+ItemUsable.itemProperties.server.get(par3Entity).getInteger("HealTarget"));
				double range = getData(par1ItemStack).getFloat(PropertyType.RANGE) + 1.6;
				if (!par2World.isRemote && healTarget != null && par3Entity.getDistanceSq(healTarget) > range * range) {
					par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget(-1);
					// TF2weapons.sendTracking(new
					// TF2Message.PropertyMessage("HealTarget",
					// -1,par3Entity),par3Entity);
				} else if (healTarget != null && healTarget instanceof EntityLivingBase) {
					
						this.heal(par1ItemStack, (EntityLivingBase) par3Entity, par2World, (EntityLivingBase) healTarget);
					if (effect != null && par1ItemStack.getTagCompound().getBoolean("Activated") && (healTarget.getActivePotionEffect(effect)==null||healTarget.ticksExisted%4==0))
						TF2Util.addAndSendEffect(healTarget,new PotionEffect(effect,15));
					// TF2weapons.sendTracking(new
					// TF2Message.PropertyMessage("UberCharged",
					// (byte)1,healTarget),healTarget);
				}
			}
		}

	}

	@Override
	public void holster(WeaponsCapability cap, ItemStack stack, EntityLivingBase living, World world) {
		cap.setHealTarget(-1);
		living.removePotionEffect(TF2weapons.uber);
		super.holster(cap, stack, living, world);
	}

	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		// TODO Auto-generated method stub
		return !(living instanceof EntityPlayer) || (((EntityPlayer) living).capabilities.isCreativeMode
				|| !this.searchForAmmo(living, stack).isEmpty());
	}

	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return true;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return true;
	}

	public float getHealAmount(ItemStack stack, EntityLivingBase living) {
		return TF2Attribute.getModifier("Heal", stack, ItemFromData.getData(stack).getFloat(PropertyType.HEAL), living);
	}

	public float getMaxOverheal(ItemStack stack, EntityLivingBase living, EntityLivingBase target) {
		if (target instanceof EntityTF2Character && ((EntityTF2Character)target).isGiant())
			return 0;
		return TF2Attribute.getModifier("Overheal", stack,
				ItemFromData.getData(stack).getFloat(PropertyType.MAX_OVERHEAL), living);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);

		tooltip.add("Charge: " + Float.toString(stack.getTagCompound().getFloat("ubercharge")));
	}

	@Override
	public boolean startUse(ItemStack stack, EntityLivingBase living, World world, int oldState, int newState) {
		if (world.isRemote && ((newState & 1) - (oldState & 1)) == 1) {
			RayTraceResult trace = this.trace(stack, living, world);
			if (trace == null || trace.entityHit == null || !(trace.entityHit instanceof EntityLivingBase))
				ClientProxy.playWeaponSound(living, ItemFromData.getSound(stack, PropertyType.NO_TARGET_SOUND), false,
						1, stack);
			// System.out.println("Stop heal");
			
			if (living.getCapability(TF2weapons.WEAPONS_CAP, null).getHealTarget() != -1) {
				living.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget(-1);
				TF2weapons.network.sendToServer(new TF2Message.CapabilityMessage(living, false));
			}

		}
		if (!world.isRemote && ((newState & 2) - (oldState & 2)) == 2
				&& stack.getTagCompound().getFloat("ubercharge") == 1f) {
			stack.getTagCompound().setBoolean("Activated", true);
			TF2Util.playSound(living,ItemFromData.getSound(stack, PropertyType.UBER_START_SOUND), 0.75f, 1);
			Entity healTargetEnt = world.getEntityByID(living.getCapability(TF2weapons.WEAPONS_CAP, null).getHealTarget());
			if (healTargetEnt instanceof EntityLivingBase) {
				((EntityLivingBase) healTargetEnt).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(ItemFromData.getData(stack).getString(PropertyType.EFFECT_TYPE)), 1));
			}
			
			if (stack.getTagCompound().getBoolean("Strange")) {
				stack.getTagCompound().setInteger("Ubercharges", stack.getTagCompound().getInteger("Ubercharges") + 1);
				TF2EventsCommon.onStrangeUpdate(stack, living);
			}
			// TF2weapons.sendTracking(new
			// TF2Message.PropertyMessage("UberCharged",
			// (byte)1,living),living);
		}
		return false;
	}

	@Override
	public boolean endUse(ItemStack stack, EntityLivingBase living, World world, int oldState, int newState) {
		if (world.isRemote && !TF2ConfigVars.medigunLock && (oldState & 1 - newState & 1) == 1) {
			living.getCapability(TF2weapons.WEAPONS_CAP, null).setHealTarget(-1);
			TF2weapons.network.sendToServer(new TF2Message.CapabilityMessage(living, false));
		}
		return false;
	}
	
	@Override
	public void drawOverlay(ItemStack stack, EntityPlayer player, Tessellator tessellator, BufferBuilder renderer, ScaledResolution resolution) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.healingTexture);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		ClientProxy.setColor(TF2Util.getTeamColor(player), 0.7f, 0, 0.25f, 0.8f);
		
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(resolution.getScaledWidth() - 138, resolution.getScaledHeight() - 20, 0.0D).tex(0.0D, 1D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 14, resolution.getScaledHeight() - 20, 0.0D).tex(0.01D, 1D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 14, resolution.getScaledHeight() - 50, 0.0D).tex(0.01D, 0.99D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 138, resolution.getScaledHeight() - 50, 0.0D).tex(0.0D, 0.99D).endVertex();
		tessellator.draw();
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(resolution.getScaledWidth() - 140, resolution.getScaledHeight() - 18, 0.0D).tex(0.0D, 0.265625D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 12, resolution.getScaledHeight() - 18, 0.0D).tex(1.0D, 0.265625D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 12, resolution.getScaledHeight() - 52, 0.0D).tex(1.0D, 0.0D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 140, resolution.getScaledHeight() - 52, 0.0D).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();


		float uber = stack.getTagCompound().getFloat("ubercharge");
		Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().ingameGUI.getFontRenderer(), "UBERCHARGE: " + Math.round(uber * 100f) + "%",
				resolution.getScaledWidth() - 130, resolution.getScaledHeight() - 48, 16777215);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.33F);
		renderer.begin(7, DefaultVertexFormats.POSITION);
		renderer.pos(resolution.getScaledWidth() - 132, resolution.getScaledHeight() - 22, 0.0D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 20, resolution.getScaledHeight() - 22, 0.0D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 20, resolution.getScaledHeight() - 36, 0.0D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 132, resolution.getScaledHeight() - 36, 0.0D).endVertex();
		tessellator.draw();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.85F);
		renderer.begin(7, DefaultVertexFormats.POSITION);
		renderer.pos(resolution.getScaledWidth() - 132, resolution.getScaledHeight() - 22, 0.0D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 132 + 112 * uber, resolution.getScaledHeight() - 22, 0.0D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 132 + 112 * uber, resolution.getScaledHeight() - 36, 0.0D).endVertex();
		renderer.pos(resolution.getScaledWidth() - 132, resolution.getScaledHeight() - 36, 0.0D).endVertex();
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
