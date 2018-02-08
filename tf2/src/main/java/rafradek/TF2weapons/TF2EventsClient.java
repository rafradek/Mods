package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import atomicstryker.dynamiclights.client.DynamicLights;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.characters.EntitySpy;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.ItemToken;
import rafradek.TF2weapons.decoration.GuiWearables;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.pages.GuiContracts;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemMedigun;
import rafradek.TF2weapons.weapons.ItemMeleeWeapon;
import rafradek.TF2weapons.weapons.ItemMinigun;
import rafradek.TF2weapons.weapons.ItemParachute;
import rafradek.TF2weapons.weapons.ItemSniperRifle;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemWrench;
import rafradek.TF2weapons.weapons.MuzzleFlashLightSource;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2EventsClient {
	private boolean alreadypressed;
	private boolean alreadypressedalt;
	private boolean alreadypressedreload;
	public static boolean moveEntities;
	public static float tickTime = 0;
	public static ArrayList<MuzzleFlashLightSource> muzzleFlashes = new ArrayList<>();
	public static TextureAtlasSprite pelletIcon;
	public static int ticksPressedReload;
	
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
		// if(event.getMap().getGlTextureId()==1){
		// System.out.println("Registered icon:
		// "+event.getMap().getGlTextureId());
		pelletIcon = event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/pellet3"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/ammo_belt_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/refill_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/weapon_empty_0"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/weapon_empty_1"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/weapon_empty_2"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/token_empty"));
		// }
	}

	@SubscribeEvent
	public void keyJumpPress(InputEvent.KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getMinecraft();
		if (minecraft.currentScreen == null && minecraft.gameSettings.keyBindJump.isPressed() && !minecraft.player.onGround) {
			
			if((WeaponsCapability.get(minecraft.player).getUsedToken() == 0 || 
					minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == TF2weapons.itemScoutBoots)
				&& !minecraft.player.getCapability(TF2weapons.WEAPONS_CAP, null).doubleJumped) {
				minecraft.player.jump();
				float speedmult=minecraft.player.moveForward * minecraft.player.getAIMoveSpeed() * (minecraft.player.isSprinting() ? 2 : 1);
				Vec3d moveDir = new Vec3d(minecraft.player.moveForward , minecraft.player.moveStrafing, 0).normalize();
				minecraft.player.motionX=-MathHelper.sin(minecraft.player.rotationYaw * 0.017453292F) * speedmult;
				minecraft.player.motionZ=MathHelper.cos(minecraft.player.rotationYaw * 0.017453292F) * speedmult;
				minecraft.player.getCapability(TF2weapons.WEAPONS_CAP, null).doubleJumped = true;
				TF2weapons.network.sendToServer(new TF2Message.ActionMessage(23));
			}
			else if(minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemParachute) {
				TF2weapons.network.sendToServer(new TF2Message.ActionMessage(25));
			}
		}
		if(minecraft.currentScreen == null && minecraft.player.getHeldItemMainhand().getItem() instanceof ItemWrench
				&& minecraft.player.getItemInUseCount()<770) {
			int sel=-1;
			for(int i=0;i<minecraft.gameSettings.keyBindsHotbar.length;i++) {
				if(minecraft.gameSettings.keyBindsHotbar[i].isKeyDown()) {
					sel=i;
					break;
				}
			}
			TF2weapons.network.sendToServer(new TF2Message.ActionMessage(sel+100));
		}
		/*if (minecraft.currentScreen == null && minecraft.gameSettings.keyBindPickBlock.isKeyDown() && minecraft.player.getHeldItemMainhand().getItem() instanceof ItemWeapon 
				&& TF2Attribute.getModifier("Knockback Rage", minecraft.player.getHeldItemMainhand(), 0, null) != 0) {
			TF2weapons.network.sendToServer(new TF2Message.ActionMessage(26));
		}*/
		if (minecraft.player != null && !minecraft.player.getHeldItemMainhand().isEmpty())
			if (minecraft.player.getHeldItemMainhand().getItem() instanceof ItemUsable) {
				keyPressUpdate(minecraft.gameSettings.keyBindAttack.isKeyDown(), minecraft.gameSettings.keyBindUseItem.isKeyDown());
			}

	}
	
	@SubscribeEvent
	public void mousePress(MouseEvent event) {
		if (event.getButton() != -1) {
			Minecraft minecraft = Minecraft.getMinecraft();
			if (minecraft.player != null && !minecraft.player.getHeldItemMainhand().isEmpty())
				if (minecraft.player.getHeldItemMainhand().getItem() instanceof ItemUsable) {
					KeyBinding.setKeyBindState(event.getButton() - 100, event.isButtonstate());
					keyPressUpdate(minecraft.gameSettings.keyBindAttack.isKeyDown(), minecraft.gameSettings.keyBindUseItem.isKeyDown());
				}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		// TF2weapons.syncConfig();
		if (eventArgs.getModID().equals("rafradek_tf2_weapons")) {
			TF2ConfigVars.createConfig();
			if(Minecraft.getMinecraft().player != null)
				TF2weapons.network.sendToServer(new TF2Message.InitClientMessage(TF2weapons.conf));
		}
	}
	
	public void keyPressUpdate(boolean attackKeyDown, boolean altAttackKeyDown) {
		Minecraft minecraft = Minecraft.getMinecraft();

		boolean changed = false;
		ItemStack item = minecraft.player.getHeldItemMainhand();
		//System.out.println("Gui: "+(minecraft.currentScreen!=null));
		if (attackKeyDown && !this.alreadypressed) {
			changed = true;
			this.alreadypressed = true;
		}
		if (!attackKeyDown && this.alreadypressed) {
			changed = true;
			this.alreadypressed = false;
		}
		if (altAttackKeyDown && !this.alreadypressedalt) {
			changed = true;
			this.alreadypressedalt = true;
		}
		if (!altAttackKeyDown && this.alreadypressedalt) {
			changed = true;
			this.alreadypressedalt = false;
		}
		if (ClientProxy.reload.isKeyDown() && !this.alreadypressedreload) {
			changed = true;
			this.alreadypressedreload = true;
		}
		if (!ClientProxy.reload.isKeyDown() && this.alreadypressedreload) {
			changed = true;
			this.alreadypressedreload = false;
		}
		if (changed && minecraft.currentScreen == null) {
			EntityLivingBase player = minecraft.player;
			WeaponsCapability cap = minecraft.player.getCapability(TF2weapons.WEAPONS_CAP, null);
			int oldState = cap.state & 3;
			int plus = cap.state & 8;
			int state = this.getActionType(attackKeyDown, altAttackKeyDown) + plus;
			cap.state = state;
			if (item != null && item.getItem() instanceof ItemUsable && oldState != (this.getActionType(attackKeyDown, altAttackKeyDown) & 3)
					&& item.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active == 2) {
				if ((oldState & 2) < (state & 2)) {
					cap.stateDo(player, item);
					((ItemUsable) item.getItem()).startUse(item, player, player.world, oldState, state & 3);
				} else if ((oldState & 2) > (state & 2)) {
					((ItemUsable) item.getItem()).endUse(item, player, player.world, oldState, state & 3);
				}
				if ((oldState & 1) < (state & 1)) {
					cap.stateDo(player, item);
					((ItemUsable) item.getItem()).startUse(item, player, player.world, oldState, state & 3);
				} else if ((oldState & 1) > (state & 1)) {
					((ItemUsable) item.getItem()).endUse(item, player, player.world, oldState, state & 3);
				}
			}
			TF2weapons.network.sendToServer(new TF2Message.ActionMessage(this.getActionType(attackKeyDown, altAttackKeyDown)));
		}
	}

	@SubscribeEvent
	public void clientTickEnd(TickEvent.ClientTickEvent event) {

		Minecraft minecraft = Minecraft.getMinecraft();

		if (event.phase == TickEvent.Phase.END) {
			if(Minecraft.getMinecraft().player != null && ClientProxy.reload.isKeyDown() && Minecraft.getMinecraft().currentScreen == null) {
				if(ticksPressedReload++ > 20) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiContracts());
				}
			}
			else {
				ticksPressedReload=0;
			}
			/*
			 * Iterator<Entry<EntityLivingBase,EntityLivingBase>>
			 * iterator=fakeEntities.entrySet().iterator();
			 * while(iterator.hasNext()){
			 * Entry<EntityLivingBase,EntityLivingBase> entry=iterator.next();
			 * EntityLivingBase real=entry.getKey(); EntityLivingBase
			 * fake=entry.getValue(); fake.posX=real.posX; fake.posY=real.posY;
			 * fake.posZ=real.posZ; fake.prevPosX=real.prevPosX;
			 * fake.prevPosY=real.prevPosY; fake.prevPosZ=real.prevPosZ;
			 * fake.rotationPitch=real.rotationPitch;
			 * fake.rotationYaw=real.rotationYaw;
			 * fake.rotationYawHead=real.rotationYawHead;
			 * fake.motionX=real.motionX; fake.motionY=real.motionY;
			 * fake.motionZ=real.motionZ;
			 * //System.out.println("pos: "+fake.posX+" "+fake.posY+" "+fake.
			 * posZ); }
			 */

			Iterator<EntityLivingBase> soundIterator = ClientProxy.soundsToStart.keySet().iterator();
			while (soundIterator.hasNext()) {
				EntityLivingBase living = soundIterator.next();
				
				soundIterator.remove();
			}
			if(TF2ConfigVars.dynamicLights){
				removeSource();
			}
			// ItemUsable.tick(true);
		}
	}

	@SubscribeEvent
	public void entityConstructing(final EntityEvent.EntityConstructing event) {

		if (event.getEntity() instanceof EntityPlayerSP) {
			//System.out.println("Constructing player");
		}
	}
	
	@Optional.Method(modid = "dynamiclights")
	public static void removeSource(){
		Iterator<MuzzleFlashLightSource> iterator = muzzleFlashes.iterator();
		while (iterator.hasNext()) {
			MuzzleFlashLightSource light = iterator.next();
			light.update();
			if (light.over) {
				DynamicLights.removeLightSource(light);
				iterator.remove();
			}
		}
		
		
	}
	public int getActionType(boolean attackKeyDown, boolean altAttackKeyDown) {
		int value = 0;
		ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
		
		if (attackKeyDown) {
			value++;
		}
		if (altAttackKeyDown) {
			value += 2;
		}
		if (ClientProxy.reload.isKeyDown()) {
			value += 4;
		}
		return ((ItemUsable)stack.getItem()).getStateOverride(stack, Minecraft.getMinecraft().player, value);
	}

	@SubscribeEvent
	public void getFov(FOVUpdateEvent event) {
		if (event.getEntity().getHeldItem(EnumHand.MAIN_HAND) != null && event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemUsable)
			if (event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSniperRifle && event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).isCharging()) {
				event.setNewfov(event.getFov() * 0.55f);
			} else if (event.getEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ItemMinigun.slowdownUUID) != null) {
				event.setNewfov(event.getNewfov() * 1.4f);
			}
		int token = WeaponsCapability.get(event.getEntity()).getUsedToken();
		if (token >= 0) {
			event.setNewfov(event.getNewfov() - (ItemToken.SPEED_VALUES[token] / 2f));
		}
	}
	
	@SubscribeEvent
	public void blockDeathGui(GuiOpenEvent event) {
		if(event.getGui() instanceof GuiGameOver && WeaponsCapability.get(Minecraft.getMinecraft().player).isFeign() && Minecraft.getMinecraft().player.getHealth() > 0f) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
		if (Minecraft.getMinecraft().player != null) {
			if ((event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative || event.getGui() instanceof GuiWearables)
					&& !Minecraft.getMinecraft().player.getCapability(TF2weapons.INVENTORY_CAP, null).isEmpty()) {
				// GuiContainer gui = (GuiContainer) event.getGui();
				event.getButtonList().add(new GuiButton(97535627, event.getGui().width / 2 - 10, event.getGui().height / 2 + 95, 20, 20, "W"));
			}

			if (event.getGui() instanceof GuiMerchant)
				if (((GuiMerchant) event.getGui()).getMerchant().getDisplayName().getUnformattedText().equals(I18n.format("entity.hale.name"))) {
					event.getButtonList().add(new GuiButton(7578, event.getGui().width / 2 - 100, event.getGui().height / 2 - 110, 100, 20, "Leave Team"));
					event.getButtonList().add(new GuiButton(7579, event.getGui().width / 2, event.getGui().height / 2 - 110, 100, 20, "Recover Lost Items"));
				}
			Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null).state &= 8;
		}
	}

	@SubscribeEvent
	public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event) {

		if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative)
			if (event.getButton().id == 97535627) {
				// Minecraft.getMinecraft().displayGuiScreen(null);
				TF2weapons.network.sendToServer(new TF2Message.ShowGuiMessage(0));
			}

		if (event.getGui() instanceof GuiWearables)
			if (event.getButton().id == 97535627) {
				event.getGui().mc.displayGuiScreen(new GuiInventory(event.getGui().mc.player));
			}
		// PacketHandler.INSTANCE.sendToServer(new
		// PacketOpenNormalInventory(event.getGui().mc.player));
		if (event.getGui() instanceof GuiMerchant && event.getButton().id == 7578) {
			TF2weapons.network.sendToServer(new TF2Message.ActionMessage(29));
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
		else if (event.getGui() instanceof GuiMerchant && event.getButton().id == 7579) {
			TF2weapons.network.sendToServer(new TF2Message.ActionMessage(18));
		}
	}

	@SubscribeEvent
	public void applyRecoil(EntityViewRenderEvent.CameraSetup event) {
		if (event.getEntity().hasCapability(TF2weapons.WEAPONS_CAP, null)) {
			WeaponsCapability cap = event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null);
			event.setPitch(event.getPitch() - cap.recoil);
			if (cap.recoil > 0) {
				cap.recoil = Math.max((cap.recoil * 0.8f) - 0.06f, 0);
			}
		}
	}

	public static void setColorTeam(Entity ent,float alpha){
		ClientProxy.setColor(TF2Util.getTeamColor(ent), 0.7f, 0, 0.25f, 0.8f);
	}

	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Pre event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null)
			return;
		WeaponsCapability cap = player.getCapability(TF2weapons.WEAPONS_CAP, null);
		ItemWeapon.inHand = event.getType() != ElementType.HOTBAR;
		ClientProxy.renderCritGlow=0;
		GuiIngame gui=Minecraft.getMinecraft().ingameGUI;
		ItemStack held=player.getHeldItem(EnumHand.MAIN_HAND);
		int width=event.getResolution().getScaledWidth();
		int height=event.getResolution().getScaledHeight();
		if (event.getType() == ElementType.HELMET) {
			if (player.getActiveItemStack().getItem() instanceof ItemWrench && player.getItemInUseCount() < 770){
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder renderer = tessellator.getBuffer();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				
				Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(width/2-80, height/2-32, 64, 192, 64, 64);
				Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(width/2+16, height/2-32, 0, 192, 64, 64);
				
				gui.drawCenteredString(gui.getFontRenderer(), "(1-8)", width/2-48, height/2+40, 0xFFFFFFFF);
				gui.drawCenteredString(gui.getFontRenderer(), I18n.format("gui.selectlocation"), width/2, height/2-50, 0xFFFFFFFF);
				gui.drawCenteredString(gui.getFontRenderer(), "(9)", width/2+48, height/2+40, 0xFFFFFFFF);
				
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
			if ((player.getCapability(TF2weapons.PLAYER_CAP, null).newContracts || player.getCapability(TF2weapons.PLAYER_CAP, null).newRewards)){
				String line1;
				String line2;
				if( player.getCapability(TF2weapons.PLAYER_CAP, null).newRewards) {
					line1="You can claim your contract reward";
					line2="Hold reload key to continue";
				}
				else {
					line1="You have a new contract";
					line2="Hold reload key to view it";
				}
				gui.getFontRenderer().drawString(line1, event.getResolution().getScaledWidth()-
						gui.getFontRenderer().getStringWidth(line1), 50, 0xFFFFFF);
				gui.getFontRenderer().drawString(line2, event.getResolution().getScaledWidth()-
						gui.getFontRenderer().getStringWidth(line2), 65, 0xFFFFFF);
			}
			if (player.getActivePotionEffect(TF2weapons.uber)!=null){
				GlStateManager.disableDepth();
		        GlStateManager.depthMask(false);
		        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		        ClientProxy.setColor(TF2Util.getTeamColor(player), 1f, 0, 0f, 1f);
		        Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.VIGNETTE);
		        Tessellator tessellator = Tessellator.getInstance();
		        BufferBuilder BufferBuilder = tessellator.getBuffer();
		        BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		        BufferBuilder.pos(0.0D, (double)event.getResolution().getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
		        BufferBuilder.pos((double)event.getResolution().getScaledWidth(), (double)event.getResolution().getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
		        BufferBuilder.pos((double)event.getResolution().getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
		        BufferBuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
		        tessellator.draw();
		        GlStateManager.depthMask(true);
		        GlStateManager.enableDepth();
		        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			}
			if (held != null && held.getItem() instanceof ItemSniperRifle && cap.isCharging()) {
				// System.out.println("drawing");
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				// gui.drawTexturedModalRect(x,
				// y, textureSprite, widthIn, heightIn);
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.scopeTexture);
				double widthDrawStart = (double) (event.getResolution().getScaledWidth() - event.getResolution().getScaledHeight()) / 2;
				double widthDrawEnd = widthDrawStart + event.getResolution().getScaledHeight();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder renderer = tessellator.getBuffer();
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(widthDrawStart, event.getResolution().getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
				renderer.pos(widthDrawEnd, event.getResolution().getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
				renderer.pos(widthDrawEnd, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
				renderer.pos(widthDrawStart, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
				tessellator.draw();
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.blackTexture);
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(0, event.getResolution().getScaledHeight(), -90.0D).tex(0d, 1d).endVertex();
				renderer.pos(widthDrawStart, event.getResolution().getScaledHeight(), -90.0D).tex(1d, 1d).endVertex();
				renderer.pos(widthDrawStart, 0.0D, -90.0D).tex(1d, 0d).endVertex();
				renderer.pos(0, 0.0D, -90.0D).tex(0d, 0d).endVertex();
				tessellator.draw();
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(widthDrawEnd, event.getResolution().getScaledHeight(), -90.0D).tex(0d, 1d).endVertex();
				renderer.pos(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(), -90.0D).tex(1d, 1d).endVertex();
				renderer.pos(event.getResolution().getScaledWidth(), 0.0D, -90.0D).tex(1d, 0d).endVertex();
				renderer.pos(widthDrawEnd, 0.0D, -90.0D).tex(0d, 0d).endVertex();
				tessellator.draw();
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.chargeTexture);
				GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.7F);
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 50, (double) event.getResolution().getScaledHeight() / 2 + 15, -90.0D).tex(0d, 0.25d).endVertex();
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 100, (double) event.getResolution().getScaledHeight() / 2 + 15, -90.0D).tex(0.508d, 0.25d)
						.endVertex();
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 100, (double) event.getResolution().getScaledHeight() / 2, -90.0D).tex(0.508d, 0d).endVertex();
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 50, (double) event.getResolution().getScaledHeight() / 2, -90.0D).tex(0d, 0d).endVertex();
				tessellator.draw();
				if (cap.chargeTicks >= 20) {
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 110, (double) event.getResolution().getScaledHeight() / 2 + 18, -90.0D).tex(0d, 0.57d)
							.endVertex();
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 121, (double) event.getResolution().getScaledHeight() / 2 + 18, -90.0D).tex(0.125d, 0.57d)
							.endVertex();
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 121, (double) event.getResolution().getScaledHeight() / 2 - 3, -90.0D).tex(0.125d, 0.25d)
							.endVertex();
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 110, (double) event.getResolution().getScaledHeight() / 2 - 3, -90.0D).tex(0d, 0.25d)
							.endVertex();
					tessellator.draw();
				}
				double progress = cap.chargeTicks / ItemSniperRifle.getChargeTime(held, player);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 50, (double) event.getResolution().getScaledHeight() / 2 + 15, -90.0D).tex(0d, 0.25d).endVertex();
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 50 + progress * 50, (double) event.getResolution().getScaledHeight() / 2 + 15, -90.0D)
						.tex(progress * 0.508d, 0.25d).endVertex();
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 50 + progress * 50, (double) event.getResolution().getScaledHeight() / 2, -90.0D)
						.tex(progress * 0.508d, 0d).endVertex();
				renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 50, (double) event.getResolution().getScaledHeight() / 2, -90.0D).tex(0d, 0d).endVertex();
				tessellator.draw();
				if (progress == 1d) {
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 110, (double) event.getResolution().getScaledHeight() / 2 + 18, -90.0D).tex(0d, 0.57d)
							.endVertex();
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 121, (double) event.getResolution().getScaledHeight() / 2 + 18, -90.0D).tex(0.125d, 0.57d)
							.endVertex();
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 121, (double) event.getResolution().getScaledHeight() / 2 - 3, -90.0D).tex(0.125d, 0.25d)
							.endVertex();
					renderer.pos((double) event.getResolution().getScaledWidth() / 2 + 110, (double) event.getResolution().getScaledHeight() / 2 - 3, -90.0D).tex(0d, 0.25d)
							.endVertex();
					tessellator.draw();
				}
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
		if (event.getType() == ElementType.HOTBAR) {
			if (held != null
					&& held.getItem() instanceof ItemFromData && ((ItemFromData)held.getItem()).showInfoBox(held, player)) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.healingTexture);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder renderer = tessellator.getBuffer();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				
				ClientProxy.setColor(TF2Util.getTeamColor(player), 0.7f, 0, 0.25f, 0.8f);
				
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(event.getResolution().getScaledWidth() - 74, event.getResolution().getScaledHeight() - 20, 0.0D).tex(0.0D, 1D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 14, event.getResolution().getScaledHeight() - 20, 0.0D).tex(0.01D, 1D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 14, event.getResolution().getScaledHeight() - 50, 0.0D).tex(0.01D, 0.99D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 74, event.getResolution().getScaledHeight() - 50, 0.0D).tex(0.0D, 0.99D).endVertex();
				tessellator.draw();
				
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(event.getResolution().getScaledWidth() - 76, event.getResolution().getScaledHeight() - 18, 0.0D).tex(0.5D, 0.265625D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 12, event.getResolution().getScaledHeight() - 18, 0.0D).tex(1.0D, 0.265625D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 12, event.getResolution().getScaledHeight() - 52, 0.0D).tex(1.0D, 0.53125D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 76, event.getResolution().getScaledHeight() - 52, 0.0D).tex(0.5D, 0.53125D).endVertex();
				tessellator.draw();
				String[] text=((ItemFromData)held.getItem()).getInfoBoxLines(held, player);
				gui.drawString(gui.getFontRenderer(), text[0],
						event.getResolution().getScaledWidth() - 66, event.getResolution().getScaledHeight() - 48, 16777215);
				gui.drawString(gui.getFontRenderer(), text[1],
						event.getResolution().getScaledWidth() - 66, event.getResolution().getScaledHeight() - 30, 16777215);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				
			}
			if (player.getActivePotionEffect(TF2weapons.it) != null) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				gui.drawCenteredString(gui.getFontRenderer(), I18n.format("gui.markedit"),
						event.getResolution().getScaledWidth()/2, event.getResolution().getScaledHeight()/4, 16777215);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}
			if (player.getActivePotionEffect(TF2weapons.bombmrs) != null) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				gui.drawCenteredString(gui.getFontRenderer(), I18n.format("gui.bombmrs"),
						event.getResolution().getScaledWidth()/2, event.getResolution().getScaledHeight()/4, 16777215);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}
			if (held != null && held.getItem() instanceof ItemMedigun) {
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.healingTexture);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder renderer = tessellator.getBuffer();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				
				ClientProxy.setColor(TF2Util.getTeamColor(player), 0.7f, 0, 0.25f, 0.8f);
				
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(event.getResolution().getScaledWidth() - 138, event.getResolution().getScaledHeight() - 20, 0.0D).tex(0.0D, 1D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 14, event.getResolution().getScaledHeight() - 20, 0.0D).tex(0.01D, 1D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 14, event.getResolution().getScaledHeight() - 50, 0.0D).tex(0.01D, 0.99D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 138, event.getResolution().getScaledHeight() - 50, 0.0D).tex(0.0D, 0.99D).endVertex();
				tessellator.draw();
				
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(event.getResolution().getScaledWidth() - 140, event.getResolution().getScaledHeight() - 18, 0.0D).tex(0.0D, 0.265625D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 12, event.getResolution().getScaledHeight() - 18, 0.0D).tex(1.0D, 0.265625D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 12, event.getResolution().getScaledHeight() - 52, 0.0D).tex(1.0D, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 140, event.getResolution().getScaledHeight() - 52, 0.0D).tex(0.0D, 0.0D).endVertex();
				tessellator.draw();
	
				Entity healTarget = player.world.getEntityByID(cap.getHealTarget());
				if (healTarget != null && healTarget instanceof EntityLivingBase) {
					EntityLivingBase living = (EntityLivingBase) healTarget;
					
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
					// gui.drawTexturedModalRect(event.getResolution().getScaledWidth()/2-64,
					// event.getResolution().getScaledHeight()/2+35, 0, 0, 128, 40);
					setColorTeam(player,0.7f);
					
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 62, event.getResolution().getScaledHeight() / 2 + 70, 0.0D).tex(0.0D, 1D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 + 62, event.getResolution().getScaledHeight() / 2 + 70, 0.0D).tex(0.01D, 1D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 + 62, event.getResolution().getScaledHeight() / 2 + 40, 0.0D).tex(0.01D, 0.99D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 62, event.getResolution().getScaledHeight() / 2 + 40, 0.0D).tex(0.0D, 0.99D).endVertex();
					tessellator.draw();
					
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 64, event.getResolution().getScaledHeight() / 2 + 72, 0.0D).tex(0.0D, 0.265625D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 + 64, event.getResolution().getScaledHeight() / 2 + 72, 0.0D).tex(1.0D, 0.265625D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 + 64, event.getResolution().getScaledHeight() / 2 + 38, 0.0D).tex(1.0D, 0.0D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 64, event.getResolution().getScaledHeight() / 2 + 38, 0.0D).tex(0.0D, 0.0D).endVertex();
					tessellator.draw();
					float overheal = 1f + living.getAbsorptionAmount() / living.getMaxHealth();
					if (overheal > 1f) {
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
						renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 47 - 10 * overheal, event.getResolution().getScaledHeight() / 2 + 55 + 10 * overheal, 0.0D)
								.tex(0.0D, 0.59375D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 47 + 10 * overheal, event.getResolution().getScaledHeight() / 2 + 55 + 10 * overheal, 0.0D)
								.tex(0.28125D, 0.59375D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 47 + 10 * overheal, event.getResolution().getScaledHeight() / 2 + 55 - 10 * overheal, 0.0D)
								.tex(0.28125D, 0.3125D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 47 - 10 * overheal, event.getResolution().getScaledHeight() / 2 + 55 - 10 * overheal, 0.0D)
								.tex(0.0D, 0.3125D).endVertex();
						tessellator.draw();
					}
					GL11.glColor4f(0.12F, 0.12F, 0.12F, 1F);
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 58.3, event.getResolution().getScaledHeight() / 2 + 66.4, 0.0D).tex(0.0D, 0.59375D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 35.7, event.getResolution().getScaledHeight() / 2 + 66.4, 0.0D).tex(0.28125D, 0.59375D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 35.7, event.getResolution().getScaledHeight() / 2 + 43.6, 0.0D).tex(0.28125D, 0.3125D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 58.3, event.getResolution().getScaledHeight() / 2 + 43.6, 0.0D).tex(0.0D, 0.3125D).endVertex();
					tessellator.draw();
					float health = living.getHealth() / living.getMaxHealth();
					if (health > 0.33f) {
						GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
					} else {
						GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
					}
					int tf2health=Math.round((living.getHealth()/TF2ConfigVars.damageMultiplier)*overheal*10);
					
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 57, event.getResolution().getScaledHeight() / 2 + 65, 0.0D).tex(0.0D, 0.59375D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 37, event.getResolution().getScaledHeight() / 2 + 65, 0.0D).tex(0.28125D, 0.59375D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 37, event.getResolution().getScaledHeight() / 2 + 65 - health * 20, 0.0D)
							.tex(0.28125D, 0.59375D - 0.28125D * health).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 57, event.getResolution().getScaledHeight() / 2 + 65 - health * 20, 0.0D)
							.tex(0.0D, 0.59375D - 0.28125D * health).endVertex();
					tessellator.draw();
					
					gui.getFontRenderer().drawString(Integer.toString(tf2health), event.getResolution().getScaledWidth() / 2 - 47 - gui.getFontRenderer().getStringWidth(Integer.toString(tf2health)) / 2,
							event.getResolution().getScaledHeight() / 2 + 52, 0x101010);
					gui.drawString(gui.getFontRenderer(), "Healing:", event.getResolution().getScaledWidth() / 2 - 28,
							event.getResolution().getScaledHeight() / 2 + 42, 16777215);
					gui.drawString(gui.getFontRenderer(), living.getDisplayName().getFormattedText(),
							event.getResolution().getScaledWidth() / 2 - 28, event.getResolution().getScaledHeight() / 2 + 54, 16777215);
	
				}
	
				float uber = held.getTagCompound().getFloat("ubercharge");
				gui.drawString(gui.getFontRenderer(), "UBERCHARGE: " + Math.round(uber * 100f) + "%",
						event.getResolution().getScaledWidth() - 130, event.getResolution().getScaledHeight() - 48, 16777215);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
				renderer.begin(7, DefaultVertexFormats.POSITION);
				renderer.pos(event.getResolution().getScaledWidth() - 132, event.getResolution().getScaledHeight() - 22, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 20, event.getResolution().getScaledHeight() - 22, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 20, event.getResolution().getScaledHeight() - 36, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 132, event.getResolution().getScaledHeight() - 36, 0.0D).endVertex();
				tessellator.draw();
	
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
				renderer.begin(7, DefaultVertexFormats.POSITION);
				renderer.pos(event.getResolution().getScaledWidth() - 132, event.getResolution().getScaledHeight() - 22, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 132 + 112 * uber, event.getResolution().getScaledHeight() - 22, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 132 + 112 * uber, event.getResolution().getScaledHeight() - 36, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() - 132, event.getResolution().getScaledHeight() - 36, 0.0D).endVertex();
				tessellator.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
			
			Entity mouseTarget = Minecraft.getMinecraft().objectMouseOver != null ? Minecraft.getMinecraft().objectMouseOver.entityHit : null;
			if (mouseTarget != null && mouseTarget instanceof EntityBuilding
					&& TF2Util.isOnSameTeam(player, mouseTarget)) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder renderer = tessellator.getBuffer();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				EntityBuilding building = (EntityBuilding) mouseTarget;
				GlStateManager.translate(width/2-72, height/2+52-building.getGuiHeight()/2, 0);
				building.renderGUI(renderer, tessellator, player, width, height, gui);
				GlStateManager.translate(-width/2+72, -height/2-52+building.getGuiHeight()/2, 0);
				if (mouseTarget instanceof EntityTeleporter) {
					EntityTeleporter teleporter = (EntityTeleporter) mouseTarget;
					// GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
					// gui.drawTexturedModalRect(event.getResolution().getScaledWidth()/2-64,
					// event.getResolution().getScaledHeight()/2+35, 0, 0, 128, 40);
					if(TF2Util.getTeamColor(player)==0)
						GL11.glColor4f(0.8F, 0.25F, 0.25F, 0.7F);
					else
						GL11.glColor4f(0.25F, 0.25F, 0.8F, 0.7F);
					gui.drawTexturedModalRect(event.getResolution().getScaledWidth() / 2 - 52, event.getResolution().getScaledHeight() / 2 + 30, 0, 112,124, 44);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
					gui.drawTexturedModalRect(event.getResolution().getScaledWidth() / 2 - 72, event.getResolution().getScaledHeight() / 2 + 28, 0, 0, 144, 48);
	
					double imagePos = teleporter.isExit() ? 0.1875D : 0;
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 53, event.getResolution().getScaledHeight() / 2 + 76, 0.0D).tex(0.5625D + imagePos, 0.9375D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 5, event.getResolution().getScaledHeight() / 2 + 76, 0.0D).tex(0.75D + imagePos, 0.9375D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 5, event.getResolution().getScaledHeight() / 2 + 28, 0.0D).tex(0.75D + imagePos, 0.75D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 53, event.getResolution().getScaledHeight() / 2 + 28, 0.0D).tex(0.5625D + imagePos, 0.75D).endVertex();
					tessellator.draw();
	
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 5, event.getResolution().getScaledHeight() / 2 + 50, 0.0D).tex(0.9375D, 0.3125D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 + 11, event.getResolution().getScaledHeight() / 2 + 50, 0.0D).tex(1D, 0.3125D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 + 11, event.getResolution().getScaledHeight() / 2 + 34, 0.0D).tex(1D, 0.25D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 5, event.getResolution().getScaledHeight() / 2 + 34, 0.0D).tex(0.9375D, 0.25D).endVertex();
					tessellator.draw();
	
					imagePos = teleporter.getLevel() == 1 ? 0.3125D : teleporter.getLevel() == 2 ? 0.375D : 0.4375D;
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 22, event.getResolution().getScaledHeight() / 2 + 46, 0.0D).tex(0.9375D, 0.0625D + imagePos).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 6, event.getResolution().getScaledHeight() / 2 + 46, 0.0D).tex(1D, 0.0625D + imagePos).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 6, event.getResolution().getScaledHeight() / 2 + 30, 0.0D).tex(1D, imagePos).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() / 2 - 22, event.getResolution().getScaledHeight() / 2 + 30, 0.0D).tex(0.9375D, imagePos).endVertex();
					tessellator.draw();
	
					if (teleporter.getLevel() < 3) {
						renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 5, event.getResolution().getScaledHeight() / 2 + 70, 0.0D).tex(0.9375D, 0.125D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 11, event.getResolution().getScaledHeight() / 2 + 70, 0.0D).tex(1D, 0.125D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 11, event.getResolution().getScaledHeight() / 2 + 54, 0.0D).tex(1D, 0.0625).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 5, event.getResolution().getScaledHeight() / 2 + 54, 0.0D).tex(0.9375D, 0.0625).endVertex();
						tessellator.draw();
					}
					if (teleporter.getTPprogress() <= 0) {
						gui.drawString(gui.getFontRenderer(),
								teleporter.getTeleports() + " (ID: " + (teleporter.getID() + 1) + ")", event.getResolution().getScaledWidth() / 2 + 13,
								event.getResolution().getScaledHeight() / 2 + 38, 16777215);
					}
					float health = teleporter.getHealth() / teleporter.getMaxHealth();
					if (health > 0.33f) {
						GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
					} else {
						GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
					}
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					for (int i = 0; i < health * 8; i++) {
	
						renderer.begin(7, DefaultVertexFormats.POSITION);
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 53, event.getResolution().getScaledHeight() / 2 + 67 - i * 5, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 63, event.getResolution().getScaledHeight() / 2 + 67 - i * 5, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 63, event.getResolution().getScaledHeight() / 2 + 71 - i * 5, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 - 53, event.getResolution().getScaledHeight() / 2 + 71 - i * 5, 0.0D).endVertex();
						tessellator.draw();
					}
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
					if (teleporter.getTPprogress() > 0) {
						renderer.begin(7, DefaultVertexFormats.POSITION);
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 49, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 68, event.getResolution().getScaledHeight() / 2 + 49, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 68, event.getResolution().getScaledHeight() / 2 + 35, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 35, 0.0D).endVertex();
						tessellator.draw();
					}
					if (teleporter.getLevel() < 3) {
						renderer.begin(7, DefaultVertexFormats.POSITION);
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 69, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 68, event.getResolution().getScaledHeight() / 2 + 69, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 68, event.getResolution().getScaledHeight() / 2 + 55, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 55, 0.0D).endVertex();
						tessellator.draw();
					}
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
					if (teleporter.getTPprogress() > 0) {
						double tpProgress = (1 - ((double) teleporter.getTPprogress() / (teleporter.getLevel() == 1 ? 200 : (teleporter.getLevel() == 2 ? 100 : 60)))) * 55;
						renderer.begin(7, DefaultVertexFormats.POSITION);
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 49, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13 + tpProgress, event.getResolution().getScaledHeight() / 2 + 49, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13 + tpProgress, event.getResolution().getScaledHeight() / 2 + 35, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 35, 0.0D).endVertex();
						tessellator.draw();
					}
					if (teleporter.getLevel() < 3) {
						renderer.begin(7, DefaultVertexFormats.POSITION);
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 69, 0.0D).endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13 + teleporter.getProgress() * 0.275D, event.getResolution().getScaledHeight() / 2 + 69, 0.0D)
								.endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13 + teleporter.getProgress() * 0.275D, event.getResolution().getScaledHeight() / 2 + 55, 0.0D)
								.endVertex();
						renderer.pos(event.getResolution().getScaledWidth() / 2 + 13, event.getResolution().getScaledHeight() / 2 + 55, 0.0D).endVertex();
						tessellator.draw();
					}
				}
				/*
				 * float
				 * uber=player.getHeldItem(EnumHand.MAIN_HAND).getTagCompound().
				 * getFloat("ubercharge");
				 * gui.drawString(Minecraft.
				 * getMinecraft().ingameGUI.getFontRenderer(), "UBERCHARGE: "
				 * +Math.round(uber*100f)+"%",
				 * event.getResolution().getScaledWidth()-130,
				 * event.getResolution().getScaledHeight()-48, 16777215);
				 * GL11.glDisable(GL11.GL_TEXTURE_2D); GL11.glColor4f(1.0F, 1.0F,
				 * 1.0F, 0.33F); renderer.begin(7, DefaultVertexFormats.POSITION);
				 * renderer.pos(event.getResolution().getScaledWidth()-132,
				 * event.getResolution().getScaledHeight()-22, 0.0D).endVertex();
				 * renderer.pos(event.getResolution().getScaledWidth()-20,
				 * event.getResolution().getScaledHeight()-22, 0.0D).endVertex();
				 * renderer.pos(event.getResolution().getScaledWidth()-20,
				 * event.getResolution().getScaledHeight()-36, 0.0D).endVertex();
				 * renderer.pos(event.getResolution().getScaledWidth()-132,
				 * event.getResolution().getScaledHeight()-36, 0.0D).endVertex();
				 * tessellator.draw();
				 * 
				 * GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F); renderer.begin(7,
				 * DefaultVertexFormats.POSITION);
				 * renderer.pos(event.getResolution().getScaledWidth()-132,
				 * event.getResolution().getScaledHeight()-22, 0.0D).endVertex();
				 * renderer.pos(event.getResolution().getScaledWidth()-132+112*uber,
				 * event.getResolution().getScaledHeight()-22, 0.0D).endVertex();
				 * renderer.pos(event.getResolution().getScaledWidth()-132+112*uber,
				 * event.getResolution().getScaledHeight()-36, 0.0D).endVertex();
				 * renderer.pos(event.getResolution().getScaledWidth()-132,
				 * event.getResolution().getScaledHeight()-36, 0.0D).endVertex();
				 * tessellator.draw();
				 */
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	@SubscribeEvent
	public void renderOverlayPost(RenderGameOverlayEvent.Post event) {

		if (event.getType() == ElementType.HOTBAR) {
			ItemWeapon.inHand = true;
		}
	}

	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Pre event) {
		if (event.getEntityPlayer() != Minecraft.getMinecraft().player) {
			renderBeam(event.getEntityPlayer(), event.getPartialRenderTick());
			/*
			 * InventoryWearables
			 * inventory=event.getEntityPlayer().getCapability(TF2weapons.
			 * INVENTORY_CAP, null); for(int
			 * i=0;i<inventory.getInventoryStackLimit();i++){ ItemStack
			 * stack=inventory.getStackInSlot(i); if(stack!=null){
			 * GlStateManager.pushMatrix();
			 * event.getRenderer().getMainModel().bipedHead.postRender(0.0625f);
			 * GlStateManager.translate(0.0F, -0.25F, 0.0F);
			 * GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			 * GlStateManager.scale(0.625F, -0.625F, -0.625F);
			 * 
			 * Minecraft.getMinecraft().getItemRenderer().renderItem(event.
			 * getEntityPlayer(), stack,
			 * ItemCameraTransforms.TransformType.HEAD);
			 * GlStateManager.popMatrix(); } }
			 */
		}
	}

	@SubscribeEvent
	public void renderGui(GuiScreenEvent.DrawScreenEvent.Pre event) {

		ClientProxy.renderCritGlow=0;
		ItemWeapon.inHand = false;
	}

	@SubscribeEvent
	public void renderGui(GuiScreenEvent.DrawScreenEvent.Post event) {
		ItemWeapon.inHand = true;
	}

	@SubscribeEvent
	public void renderHand(RenderHandEvent event) {

		EntityPlayer player=Minecraft.getMinecraft().player;
		if (!player.getHeldItemMainhand().isEmpty()){
			ClientProxy.renderCritGlow=TF2Util.calculateCritPre(player.getHeldItemMainhand(),player)*16+TF2Util.getTeamColorNumber(player);
		}
		else{
			ClientProxy.renderCritGlow=0;
		}
		if (WeaponsCapability.get(player).isInvisible() ||player.getCapability(TF2weapons.WEAPONS_CAP, null).isCharging()) {
			/*
			 * GL11.glEnable(GL11.GL_BLEND); GlStateManager.clear(256);
			 * OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			 * if(Minecraft.getMinecraft().player.getEntityData().getInteger(
			 * "VisTicks")>=20){ GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75f); }
			 * else{ GL11.glColor4f(1.0F, 1.0F, 1.0F,
			 * 0.6f*(1-(float)Minecraft.getMinecraft().player.getEntityData()
			 * .getInteger("VisTicks")/20)); } try { Method
			 * method=EntityRenderer.class.getDeclaredMethod("renderHand",
			 * float.class, int.class); method.setAccessible(true);
			 * method.invoke(Minecraft.getMinecraft().entityRenderer,
			 * event.partialTicks,event.renderPass); } catch (Exception e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
			event.setCanceled(true);
		}
	}
	@SubscribeEvent
	public void renderSpecificHand(RenderSpecificHandEvent event) {
		EntityPlayer player=Minecraft.getMinecraft().player;
		if((event.getItemStack().getItem() instanceof ItemCloak && !WeaponsCapability.get(player).isFeign() 
				&& ((ItemCloak)event.getItemStack().getItem()).isFeignDeath(event.getItemStack(), player))) {
			event.setCanceled(true);
		}
	}
	public static void renderBeam(EntityLivingBase ent, float partialTicks) {
		if (!ent.hasCapability(TF2weapons.WEAPONS_CAP, null))
			return;
		// System.out.println("Drawing");
		Entity healTarget = ent.world.getEntityByID(ent.getCapability(TF2weapons.WEAPONS_CAP, null).getHealTarget());
		if (healTarget != null) {
			Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
			double cameraX = camera.prevPosX + (camera.posX - camera.prevPosX) * partialTicks;
			double cameraY = camera.prevPosY + (camera.posY - camera.prevPosY) * partialTicks;
			double cameraZ = camera.prevPosZ + (camera.posZ - camera.prevPosZ) * partialTicks;
			// System.out.println("rendering");
			double xPos1 = ent.prevPosX + (ent.posX - ent.prevPosX) * partialTicks;
			double yPos1 = ent.prevPosY + (ent.posY - ent.prevPosY) * partialTicks;
			double zPos1 = ent.prevPosZ + (ent.posZ - ent.prevPosZ) * partialTicks;
			double xPos2 = healTarget.prevPosX + (healTarget.posX - healTarget.prevPosX) * partialTicks;
			double yPos2 = healTarget.prevPosY + (healTarget.posY - healTarget.prevPosY) * partialTicks;
			double zPos2 = healTarget.prevPosZ + (healTarget.posZ - healTarget.prevPosZ) * partialTicks;
			double xDist = xPos2 - xPos1;
			double yDist = (yPos2 + (healTarget.getEntityBoundingBox().maxY - healTarget.getEntityBoundingBox().minY) / 2 + 0.1) - (yPos1 + ent.getEyeHeight() - 0.1);
			double zDist = zPos2 - zPos1;
			float f = MathHelper.sqrt(xDist * xDist + zDist * zDist);
			float fullDist = MathHelper.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder renderer = tessellator.getBuffer();
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) xPos1 - cameraX, (float) (yPos1 + ent.getEyeHeight() - 0.1) - cameraY, (float) zPos1 - cameraZ);
			GL11.glRotatef((float) (Math.atan2(xDist, zDist) * 180.0D / Math.PI), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef((float) (Math.atan2(yDist, f) * 180.0D / Math.PI) * -1, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			ClientProxy.setColor(TF2Util.getTeamColor(ent), 0.23f, 0, 0f, 1f);
			/*if (TF2Util.getTeamForDisplay(ent) == 0) {
				GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.23F);
			} else {
				GL11.glColor4f(0.0F, 0.0F, 1.0F, 0.23F);
			}*/
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(-0.04, -0.04, 0).endVertex();
			renderer.pos(0.04, 0.04, 0).endVertex();
			renderer.pos(0.04, 0.04, fullDist).endVertex();
			renderer.pos(-0.04, -0.04, fullDist).endVertex();
			tessellator.draw();
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(-0.04, -0.04, fullDist).endVertex();
			renderer.pos(0.04, 0.04, fullDist).endVertex();
			renderer.pos(0.04, 0.04, 0).endVertex();
			renderer.pos(-0.04, -0.04, 0).endVertex();
			tessellator.draw();
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(0.04, -0.04, 0).endVertex();
			renderer.pos(-0.04, 0.04, 0).endVertex();
			renderer.pos(-0.04, 0.04, fullDist).endVertex();
			renderer.pos(0.04, -0.04, fullDist).endVertex();
			tessellator.draw();
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(0.04, -0.04, fullDist).endVertex();
			renderer.pos(-0.04, 0.04, fullDist).endVertex();
			renderer.pos(-0.04, 0.04, 0).endVertex();
			renderer.pos(0.04, -0.04, 0).endVertex();
			tessellator.draw();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}

	public static float interpolateRotation(float par1, float par2, float par3) {
		float f;

		for (f = par2 - par1; f < -180.0F; f += 360.0F) {
			;
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return par1 + par3 * f;
	}

	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event) {

		tickTime = event.renderTickTime;
		Minecraft minecraft = Minecraft.getMinecraft();
		if (event.phase == Phase.END)
			if (minecraft.player != null && minecraft.player.getHeldItemMainhand() != null)
				if (minecraft.player.getHeldItemMainhand().getItem() instanceof ItemUsable) {
					Mouse.poll();
					minecraft.player.rotationYawHead = minecraft.player.rotationYaw;
					moveEntities = true;
					keyPressUpdate(Mouse.isButtonDown(minecraft.gameSettings.keyBindAttack.getKeyCode() + 100),
							Mouse.isButtonDown(minecraft.gameSettings.keyBindUseItem.getKeyCode() + 100));
					moveEntities = false;
				}
	}

	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player != null) {
			renderBeam(Minecraft.getMinecraft().player, event.getPartialTicks());
		}
	}

	@SubscribeEvent
	public void playerName(PlayerEvent.NameFormat event) {
		if(Minecraft.getMinecraft().player != null && WeaponsCapability.get(event.getEntityPlayer()).isDisguised()) {
			String username=WeaponsCapability.get(event.getEntityPlayer()).getDisguiseType().substring(2);
			
			if(TF2Util.isOnSameTeam(Minecraft.getMinecraft().player, event.getEntityPlayer())) {
				event.setDisplayname(event.getDisplayname()+" ["+username+"]");
			}
			else {
				if(WeaponsCapability.get(event.getEntityPlayer()).getDisguiseType().startsWith("M:")) {
					if(event.getEntityPlayer().getCapability(TF2weapons.WEAPONS_CAP, null).entityDisguise != null){
						event.setDisplayname(TextFormatting.RESET+event.getEntityPlayer().getCapability(TF2weapons.WEAPONS_CAP, null).entityDisguise.getDisplayName().getFormattedText());
					}
					else
						event.setDisplayname(TextFormatting.RESET+I18n.format("entity."+username+".name"));
					
				}
				else
					event.setDisplayname(ScorePlayerTeam.formatPlayerName(Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam(username), username));
			}
		}
	}
	
	@SubscribeEvent
	public void renderLivingEntity(RenderLivingEvent.Pre<EntityLivingBase> event) {

		if (!event.getEntity().isEntityAlive())
			return;
		
		ClientProxy.renderCritGlow=0;
		if (event.getRenderer().getRenderManager().isDebugBoundingBox() && !event.getEntity().isInvisible() && !Minecraft.getMinecraft().isReducedDebug()){
			GlStateManager.depthMask(false);
	        GlStateManager.disableTexture2D();
	        GlStateManager.disableLighting();
	        GlStateManager.disableCull();
	        GlStateManager.disableBlend();
	        AxisAlignedBB head=TF2Util.getHead(event.getEntity()).offset(-event.getEntity().posX, -event.getEntity().posY, -event.getEntity().posZ);
	        /*double ymax = event.getEntity().getEntityBoundingBox().maxY-event.getEntity().posY;
	        AxisAlignedBB head = new AxisAlignedBB(- 0.32, ymax - 0.24, - 0.32,  0.32, ymax + 0.48, 0.32);
	        if (event.getEntity().width > event.getEntity().height * 0.65) {
				double offsetX = MathHelper.cos((event.getEntity().rotationYaw-TF2weapons.lerp(event.getEntity().prevRotationYaw, 0, tickTime)) / 180.0F * (float) Math.PI) * event.getEntity().width / 2;
				double offsetZ = -(double) (MathHelper.sin((event.getEntity().rotationYaw-TF2weapons.lerp(event.getEntity().prevRotationYaw, 0, tickTime)) / 180.0F * (float) Math.PI) * event.getEntity().width / 2);// cos
				head.offset(offsetX, 0, offsetZ);
			}*/
	        RenderGlobal.drawBoundingBox(head.minX + event.getX(), head.minY + event.getY(), head.minZ + event.getZ(), head.maxX + event.getX(), head.maxY + event.getY(), head.maxZ + event.getZ(), 1.0F, 0.0F, 1.0F, 1.0F);
	        GlStateManager.enableTexture2D();
	        GlStateManager.enableLighting();
	        GlStateManager.enableCull();
	        GlStateManager.disableBlend();
	        GlStateManager.depthMask(true);
		}
		if (!(event.getEntity() instanceof EntityPlayer || event.getEntity() instanceof EntityTF2Character))
			return;

		if (!event.getEntity().getHeldItemMainhand().isEmpty()){
			ClientProxy.renderCritGlow=TF2Util.calculateCritPre(event.getEntity().getHeldItemMainhand(),event.getEntity())*16+TF2Util.getTeamColorNumber(event.getEntity());
		}
		else{
			ClientProxy.renderCritGlow=0;
		}
		
		int visTick = event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks;

		if (visTick > 0) {
			if(Minecraft.getMinecraft().player != null && TF2Util.isOnSameTeam(event.getEntity(),Minecraft.getMinecraft().player))
				visTick=8;
			if (visTick >= 20) {
				event.setCanceled(true);
				return;
			} else {
				// System.out.println("VisTicksRender
				// "+event.getEntity().getEntityData().getInteger("VisTicks"));
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				Team team = event.getEntity().getTeam();
				if (team == event.getEntity().world.getScoreboard().getTeam("RED")) {
					GL11.glColor4f(1.0F, 0.17F, 0.17F, 0.7f * (1 - (float) visTick / 20));
				} else if (team == event.getEntity().world.getScoreboard().getTeam("BLU")) {
					GL11.glColor4f(0.17F, 0.17F, 1.0F, 0.7f * (1 - (float) visTick / 20));
				} else {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7f * (1 - (float) visTick / 20));
				}
			}
		} else if (event.getEntity() instanceof EntityPlayer && event.getEntity().getHeldItem(EnumHand.MAIN_HAND) != null
				&& event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemUsable
				&& !(event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemMeleeWeapon) && event.getRenderer().getMainModel() instanceof ModelBiped) {
			((ModelBiped) event.getRenderer().getMainModel()).rightArmPose = ((event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).state & 3) > 0)
					|| event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).isCharging() ? ModelBiped.ArmPose.BOW_AND_ARROW : ModelBiped.ArmPose.ITEM;
		}
		
		if (event.getEntity().getActivePotionEffect(TF2weapons.uber)!=null){
			// GlStateManager.disableLighting();
			ClientProxy.setColor(TF2Util.getTeamColor(event.getEntity()), 1f, 0f, 0.33f, 1f);
		}
		if (event.getRenderer() != ClientProxy.disguiseRender && event.getRenderer() != ClientProxy.disguiseRenderPlayer
				&& event.getRenderer() != ClientProxy.disguiseRenderPlayerSmall && WeaponsCapability.get(event.getEntity()).isDisguised()) {

			
			 
			 
			// Entity camera=Minecraft.getMinecraft().getRenderViewEntity();
			float partialTicks = tickTime;/*
											 * 0;
											 * if(camera.posX-camera.prevPosX!=0
											 * ){ partialTicks=(float)
											 * ((camera.posX-event.x)/(camera.
											 * posX-camera.prevPosX)); }
											 * /"lel: "+event.x+" "+camera.
											 * posX+" "+camera.prevPosX+" "+);
											 */
			/*
			 * ModelBase model=ClientProxy.entityRenderers.get("Creeper");
			 * GlStateManager.pushMatrix(); GlStateManager.disableCull();
			 * model.swingProgress =
			 * event.getEntity().getSwingProgress(partialTicks); model.isRiding
			 * = event.getEntity().isRiding(); model.isChild =
			 * event.getEntity().isChild();
			 * 
			 * try { float f =
			 * interpolateRotation(event.getEntity().prevRenderYawOffset,
			 * event.getEntity().renderYawOffset, partialTicks); float f1 =
			 * interpolateRotation(event.getEntity().prevRotationYawHead,
			 * event.getEntity().rotationYawHead, partialTicks); float f2 = f1 -
			 * f;
			 * 
			 * if (event.getEntity().isRiding() &&
			 * event.getEntity().ridingEntity instanceof EntityLivingBase) {
			 * EntityLivingBase entitylivingbase =
			 * (EntityLivingBase)event.getEntity().ridingEntity; f =
			 * interpolateRotation(entitylivingbase.prevRenderYawOffset,
			 * entitylivingbase.renderYawOffset, partialTicks); f2 = f1 - f;
			 * float f3 = MathHelper.wrapAngleTo180_float(f2);
			 * 
			 * if (f3 < -85.0F) { f3 = -85.0F; }
			 * 
			 * if (f3 >= 85.0F) { f3 = 85.0F; }
			 * 
			 * f = f1 - f3;
			 * 
			 * if (f3 * f3 > 2500.0F) { f += f3 * 0.2F; } }
			 * 
			 * float f7 = event.getEntity().prevRotationPitch +
			 * (event.getEntity().rotationPitch -
			 * event.getEntity().prevRotationPitch) * partialTicks;
			 * GlStateManager.translate((float)event.x, (float)event.y,
			 * (float)event.z); float ticks=
			 * this.handleRotationFloat(event.getEntity(), partialTicks);
			 * this.rotateCorpse(entity, f8, f, partialTicks);
			 * GlStateManager.enableRescaleNormal(); GlStateManager.scale(-1.0F,
			 * -1.0F, 1.0F); this.preRenderCallback(entity, partialTicks); float
			 * f4 = 0.0625F; GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
			 * float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount -
			 * entity.prevLimbSwingAmount) * partialTicks; float f6 =
			 * entity.limbSwing - entity.limbSwingAmount * (1.0F -
			 * partialTicks); GlStateManager.enableAlpha();
			 * this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
			 * this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, 0.0625F,
			 * entity);
			 * 
			 * if (this.renderOutlines) { boolean flag1 =
			 * this.setScoreTeamColor(entity); this.renderModel(entity, f6, f5,
			 * f8, f2, f7, 0.0625F);
			 * 
			 * if (flag1) { this.unsetScoreTeamColor(); } } else { boolean flag
			 * = event.renderer.setDoRenderBrightness(event.getEntity(),
			 * partialTicks); M.renderModel(event.getEntity(), f6, f5, f8, f2,
			 * f7, 0.0625F);
			 * 
			 * if (flag) { event.renderer.unsetBrightness(); }
			 * 
			 * GlStateManager.depthMask(true);
			 * //event.renderer.renderLayers(event.getEntity(), f6, f5,
			 * partialTicks, f8, f2, f7, 0.0625F); //}
			 * 
			 * GlStateManager.disableRescaleNormal(); } catch (Exception
			 * exception) { //logger.error((String)"Couldn\'t render entity",
			 * (Throwable)exception); }
			 * 
			 * GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			 * GlStateManager.enableTexture2D();
			 * GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			 * GlStateManager.enableCull(); GlStateManager.popMatrix();
			 * 
			 * /*if (!event.renderer.renderOutlines) { super.doRender(entity, x,
			 * y, z, entityYaw, partialTicks); }
			 */
			RenderLivingBase<EntityLivingBase> render = null;
			if (WeaponsCapability.get(event.getEntity()).getDisguiseType().startsWith("M:")) {
				String mobType = WeaponsCapability.get(event.getEntity()).getDisguiseType().substring(2);
				EntityLivingBase entToRender=event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).entityDisguise;
				if(entToRender == null || !EntityList.getKey(entToRender).equals(new ResourceLocation(mobType))) {
					entToRender = event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).entityDisguise =
						(EntityLivingBase) EntityList.createEntityByIDFromName(new ResourceLocation(mobType), event.getEntity().world);
					if(entToRender instanceof EntityTF2Character) {
						((EntityTF2Character)entToRender).setEntTeam(1-TF2Util.getTeamColor(event.getEntity()));
					}
					if(entToRender instanceof EntityBuilding)
						((EntityBuilding)entToRender).setEntTeam(1-TF2Util.getTeamColor(event.getEntity()));
					if(entToRender instanceof EntitySpy)
						entToRender.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks=0;
				}
				if(entToRender != null) {
					entToRender.setPositionAndRotationDirect(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, event.getEntity().rotationYaw, event.getEntity().rotationPitch, 0, true);
					entToRender.prevRenderYawOffset=event.getEntity().prevRenderYawOffset;
					entToRender.rotationPitch=event.getEntity().rotationPitch;
					entToRender.prevRotationPitch=event.getEntity().prevRotationPitch;
					entToRender.prevRotationYaw=event.getEntity().prevRotationYaw;
					entToRender.rotationYawHead=event.getEntity().rotationYawHead;
					entToRender.prevRotationYawHead=event.getEntity().prevRotationYawHead;
					entToRender.renderYawOffset=event.getEntity().renderYawOffset;
					entToRender.limbSwing=event.getEntity().limbSwing;
					entToRender.limbSwingAmount=event.getEntity().limbSwingAmount;
					entToRender.prevLimbSwingAmount=event.getEntity().prevLimbSwingAmount;
					entToRender.ticksExisted=event.getEntity().ticksExisted;

					Minecraft.getMinecraft().getRenderManager().doRenderEntity(entToRender, event.getX(), event.getY(), event.getZ(), event.getEntity().rotationYaw, partialTicks, false);
					event.setCanceled(true);
				}
				/*if (ClientProxy.entityModel.containsKey(mobType)) {
					ClientProxy.disguiseRender.setRenderOptions(ClientProxy.entityModel.get(mobType), ClientProxy.textureDisguise.get(mobType));
					render = ClientProxy.disguiseRender;
				}*/
			} else if (event.getEntity() instanceof AbstractClientPlayer && WeaponsCapability.get(event.getEntity()).getDisguiseType().startsWith("P:"))
				if ("slim".equals(event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).skinType)) {
					render = ClientProxy.disguiseRenderPlayerSmall;
				} else {
					render = ClientProxy.disguiseRenderPlayer;
				}
			if (render != null) {
				render.doRender(event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntity().rotationYaw, partialTicks);
				event.setCanceled(true);
			}
		}

	}

	@SubscribeEvent
	public void renderLivingPostEntity(RenderLivingEvent.Post<EntityLivingBase> event) {
		if (!(event.getEntity() instanceof EntityPlayer || event.getEntity() instanceof EntityTF2Character))
			return;
		ClientProxy.renderCritGlow=0;
		if (event.getEntity().getActivePotionEffect(TF2weapons.uber)!=null) {
			GL11.glColor4f(1.0F, 1F, 1.0F, 1F);
		}
		// GlStateManager.enableLighting();
		if (event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1F, 1.0F, 1F);
		}
	}
	
	@SubscribeEvent
	public void addTooltip(ItemTooltipEvent event) {
		
		if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().getBoolean("Australium") && !(event.getItemStack().getItem() instanceof ItemFromData)
				&& !event.getItemStack().hasDisplayName()) {
			event.getToolTip().set(0, "Australium " + event.getToolTip().get(0));
		}
		if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().getBoolean("Strange")) {
			if (!(event.getItemStack().getItem() instanceof ItemFromData) && !event.getItemStack().hasDisplayName()) {
				event.getToolTip().set(0, TF2EventsCommon.STRANGE_TITLES[event.getItemStack().getTagCompound().getInteger("StrangeLevel")] + " " + event.getToolTip().get(0));
			}

			event.getToolTip().add("");
			if (event.getItemStack().getItem() instanceof ItemMedigun) {
				event.getToolTip().add("Ubercharges: " + event.getItemStack().getTagCompound().getInteger("Ubercharges"));
			} else if (event.getItemStack().getItem() instanceof ItemCloak) {
				event.getToolTip().add("Seconds cloaked: " + event.getItemStack().getTagCompound().getInteger("CloakTicks") / 20);
			} else {
				event.getToolTip().add("Mob kills: " + event.getItemStack().getTagCompound().getInteger("Kills"));
				event.getToolTip().add("Player kills: " + event.getItemStack().getTagCompound().getShort("PlayerKills"));
			}
		}
		
	}
	

}
