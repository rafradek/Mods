package rafradek.TF2weapons.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import atomicstryker.dynamiclights.client.DynamicLights;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.DynamicTexture;
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
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.gui.GuiContracts;
import rafradek.TF2weapons.client.gui.inventory.GuiWearables;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.mercenary.EntitySpy;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.inventory.InventoryWearables;
import rafradek.TF2weapons.item.IItemOverlay;
import rafradek.TF2weapons.item.IItemSlotNumber;
import rafradek.TF2weapons.item.ItemBackpack;
import rafradek.TF2weapons.item.ItemCloak;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemJetpack;
import rafradek.TF2weapons.item.ItemMedigun;
import rafradek.TF2weapons.item.ItemMeleeWeapon;
import rafradek.TF2weapons.item.ItemMinigun;
import rafradek.TF2weapons.item.ItemPDA;
import rafradek.TF2weapons.item.ItemParachute;
import rafradek.TF2weapons.item.ItemSniperRifle;
import rafradek.TF2weapons.item.ItemToken;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.item.ItemWrench;
import rafradek.TF2weapons.lightsource.MuzzleFlashLightSource;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.TF2Util;

public class TF2EventsClient {
	private static boolean alreadypressed;
	private static boolean alreadypressedalt;
	private static boolean alreadypressedreload;
	public static boolean moveEntities;
	public static float tickTime = 0;
	public static ArrayList<MuzzleFlashLightSource> muzzleFlashes = new ArrayList<>();
	public static TextureAtlasSprite[] pelletIcon;
	public static int ticksPressedReload;
	private static FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
	private static final DynamicTexture TEXTURE_BRIGHTNESS = new DynamicTexture(16, 16);
	public static TextureAtlasSprite skinIcon;
	public static TextureAtlasSprite bisonIcon;
	private long profileStartTime = -1;
	private int profileStartTick;
	public static Vec3d armLpos;
	public static Vec3d armRpos;
	public static Vec3d armLang;
	public static Vec3d armRang;
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
		// if(event.getMap().getGlTextureId()==1){
		// System.out.println("Registered icon:
		// "+event.getMap().getGlTextureId());
		pelletIcon = new TextureAtlasSprite[16];
		for (int i = 0; i < 16; i++) {
			ResourceLocation location = new ResourceLocation(TF2weapons.MOD_ID, "items/tracer"+(i+1));
			try {
				Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(TF2weapons.MOD_ID, "textures/items/tracer"+(i+1)+".png"));
				pelletIcon[i] = event.getMap().registerSprite(location);
			} catch (IOException e) {

			}

		}
		bisonIcon = event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/bison"));

		skinIcon = event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "misc/skin"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/ammo_belt_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/refill_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/weapon_empty_0"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/weapon_empty_1"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/weapon_empty_2"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/token_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/robot_part_1_1_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/robot_part_2_1_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/robot_part_3_1_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/robot_part_1_2_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/robot_part_2_2_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/robot_part_3_2_empty"));
		event.getMap().registerSprite(new ResourceLocation(TF2weapons.MOD_ID, "items/robot_part_1_3_empty"));


		// }
	}

	@SubscribeEvent
	public void keyJumpPress(InputEvent.KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getMinecraft();
		if (minecraft.currentScreen == null) {
			ItemStack stack = minecraft.player.getHeldItemMainhand();
			if (minecraft.gameSettings.keyBindJump.isPressed() && !minecraft.player.onGround) {
				ItemStack chest = ItemBackpack.getBackpack(minecraft.player);
				if(WeaponsCapability.get(minecraft.player).airJumps < WeaponsCapability.get(minecraft.player).getMaxAirJumps()) {
					minecraft.player.jump();
					float speedmult=minecraft.player.getAIMoveSpeed() * (minecraft.player.isSprinting() ? 2 : 1);
					Vec3d moveDir = TF2Util.getMovementVector(minecraft.player);
					minecraft.player.motionX=moveDir.x * speedmult;
					minecraft.player.motionZ=moveDir.y * speedmult;
					minecraft.player.getCapability(TF2weapons.WEAPONS_CAP, null).airJumps += 1;

					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(23));
				}
				else if (!minecraft.player.capabilities.isCreativeMode && !minecraft.player.isInWater()
						&& chest.getItem() instanceof ItemJetpack && TF2Attribute.getModifier("Jetpack Item", chest, 0f, minecraft.player) != 0f
						&& ((ItemJetpack)chest.getItem()).canActivate(chest, minecraft.player)) {
					//((ItemJetpack)chest.getItem()).activateJetpack(chest, minecraft.player, true);
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(30));
				}
				else if (ItemToken.allowUse(minecraft.player, "soldier") && chest.getItem() instanceof ItemParachute) {
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(25));
				}
			}

			if (stack.getItem() instanceof IItemSlotNumber && ((IItemSlotNumber) stack.getItem()).catchSlotHotkey(stack, minecraft.player)) {
				int sel=this.getPressedHotbarKey(minecraft.gameSettings.keyBindsHotbar);
				//KeyBinding.setKeyBindState(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey(), false);
				if (sel != -1) {
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(sel+100));
					((IItemSlotNumber) stack.getItem()).onSlotSelection(stack, minecraft.player, sel);
				}
			}
			else if(ClientProxy.reload.isKeyDown()) {
				int sel=this.getPressedHotbarKey(minecraft.gameSettings.keyBindsHotbar);
				//KeyBinding.setKeyBindState(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey(), false);
				if (sel != -1)
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(sel+110));
			}
			else if (stack.getItem() instanceof ItemUsable && ((ItemUsable)stack.getItem()).stopSlotSwitch(stack, minecraft.player))
				this.getPressedHotbarKey(minecraft.gameSettings.keyBindsHotbar);

			/*if (minecraft.currentScreen == null && minecraft.gameSettings.keyBindPickBlock.isKeyDown() && minecraft.player.getHeldItemMainhand().getItem() instanceof ItemWeapon
					&& TF2Attribute.getModifier("Knockback Rage", minecraft.player.getHeldItemMainhand(), 0, null) != 0) {
				TF2weapons.network.sendToServer(new TF2Message.ActionMessage(26));
			}*/
			if (minecraft.player != null && !minecraft.player.getHeldItemMainhand().isEmpty())
				if (minecraft.player.getHeldItemMainhand().getItem() instanceof ItemUsable) {
					keyPressUpdate(minecraft.gameSettings.keyBindAttack.isKeyDown(), minecraft.gameSettings.keyBindUseItem.isKeyDown());
				}
		}
		if (minecraft.gameSettings.showDebugProfilerChart && this.profileStartTime == -1) {
			this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
			this.profileStartTick = (int) Minecraft.getMinecraft().world.getTotalWorldTime();
		}
		else if (this.profileStartTime != -1) {
			long i = MinecraftServer.getCurrentTimeMillis();
			int j = (int) Minecraft.getMinecraft().world.getTotalWorldTime();
			long k = i - this.profileStartTime;
			int l = j - this.profileStartTick;
			this.saveProfilerResults(k, l);
			this.profileStartTime = -1;
		}
	}

	private void saveProfilerResults(long timeSpan, int tickSpan)
	{
		File file1 = new File("./debug/profile-results-client-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
		file1.getParentFile().mkdirs();
		Writer writer = null;

		try
		{
			writer = new OutputStreamWriter(new FileOutputStream(file1), StandardCharsets.UTF_8);
			writer.write(this.getProfilerResults(timeSpan, tickSpan, Minecraft.getMinecraft().mcProfiler));
		}
		catch (Throwable throwable)
		{
			TF2weapons.LOGGER.error("Could not save profiler results to {}", file1, throwable);
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}
	}

	private String getProfilerResults(long timeSpan, int tickSpan, Profiler server)
	{
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("---- Minecraft Profiler Results ----\n");
		stringbuilder.append("// ");
		stringbuilder.append(getWittyComment());
		stringbuilder.append("\n\n");
		stringbuilder.append("Time span: ").append(timeSpan).append(" ms\n");
		stringbuilder.append("Tick span: ").append(tickSpan).append(" ticks\n");
		stringbuilder.append("// This is approximately ").append(String.format("%.2f", (float)tickSpan / ((float)timeSpan / 1000.0F))).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
		stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
		this.appendProfilerResults(0, "root", stringbuilder, server);
		stringbuilder.append("--- END PROFILE DUMP ---\n\n");
		return stringbuilder.toString();
	}

	private void appendProfilerResults(int depth, String sectionName, StringBuilder builder, Profiler server)
	{
		List<Profiler.Result> list = server.getProfilingData(sectionName);

		if (list != null && list.size() >= 3)
		{
			for (int i = 1; i < list.size(); ++i)
			{
				Profiler.Result profiler$result = list.get(i);
				builder.append(String.format("[%02d] ", depth));

				for (int j = 0; j < depth; ++j)
				{
					builder.append("|   ");
				}

				builder.append(profiler$result.profilerName).append(" - ").append(String.format("%.2f", profiler$result.usePercentage)).append("%/").append(String.format("%.2f", profiler$result.totalUsePercentage)).append("%\n");

				if (!"unspecified".equals(profiler$result.profilerName))
				{
					try
					{
						this.appendProfilerResults(depth + 1, sectionName + "." + profiler$result.profilerName, builder, server);
					}
					catch (Exception exception)
					{
						builder.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
					}
				}
			}
		}
	}

	/**
	 * Get a random witty comment
	 */
	private static String getWittyComment()
	{
		String[] astring = new String[] {"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

		try
		{
			return astring[(int)(System.nanoTime() % (long)astring.length)];
		}
		catch (Throwable var2)
		{
			return "Witty comment unavailable :(";
		}
	}

	public int getPressedHotbarKey(KeyBinding[] keys) {
		int sel=-1;
		for(int i=0;i<keys.length;i++) {
			if(keys[i].isKeyDown()) {
				sel=i;
				keys[i].isPressed();
				break;
			}
		}
		return sel;
	}
	@SubscribeEvent
	public void mousePress(MouseEvent event) {
		if (event.getButton() != -1) {
			Minecraft minecraft = Minecraft.getMinecraft();
			if (minecraft.currentScreen == null && minecraft.player != null && !minecraft.player.getHeldItemMainhand().isEmpty())
				if (minecraft.player.getHeldItemMainhand().getItem() instanceof ItemUsable) {
					KeyBinding.setKeyBindState(event.getButton() - 100, event.isButtonstate());
					keyPressUpdate(minecraft.gameSettings.keyBindAttack.isKeyDown(), minecraft.gameSettings.keyBindUseItem.isKeyDown());
				}
		}
		if (event.getDwheel() != 0) {
			Minecraft minecraft = Minecraft.getMinecraft();
			if (minecraft.currentScreen == null && minecraft.player != null) {
				ItemStack stack = minecraft.player.getHeldItemMainhand();
				if (stack.getItem() instanceof ItemUsable && ((ItemUsable)stack.getItem()).stopSlotSwitch(stack, minecraft.player))
					minecraft.player.inventory.changeCurrentItem(-event.getDwheel());
			}

		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		// TF2weapons.syncConfig();
		if (eventArgs.getModID().equals("rafradek_tf2_weapons")) {
			TF2ConfigVars.createConfig(true);
			if(Minecraft.getMinecraft().player != null)
				TF2weapons.network.sendToServer(new TF2Message.InitClientMessage(TF2weapons.conf));
		}
	}

	public void keyPressUpdate(boolean attackKeyDown, boolean altAttackKeyDown) {
		keyPressUpdate(attackKeyDown, altAttackKeyDown, false);
	}

	public static void keyPressUpdate() {
		keyPressUpdate(Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown(), Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown(), true);
	}
	public static void keyPressUpdate(boolean attackKeyDown, boolean altAttackKeyDown, boolean force) {
		Minecraft minecraft = Minecraft.getMinecraft();

		boolean changed = force;
		ItemStack item = minecraft.player.getHeldItemMainhand();
		//System.out.println("Gui: "+(minecraft.currentScreen!=null));
		if (attackKeyDown && !alreadypressed) {
			changed = true;
			alreadypressed = true;
		}
		if (!attackKeyDown && alreadypressed) {
			changed = true;
			alreadypressed = false;
		}
		if (altAttackKeyDown && !alreadypressedalt) {
			changed = true;
			alreadypressedalt = true;
		}
		if (!altAttackKeyDown && alreadypressedalt) {
			changed = true;
			alreadypressedalt = false;
		}
		if (reloadPressed(minecraft) && !alreadypressedreload) {
			changed = true;
			alreadypressedreload = true;
		}
		if (alreadypressedreload && !reloadPressed(minecraft)) {
			changed = true;
			alreadypressedreload = false;
		}
		if (changed && minecraft.currentScreen == null) {
			EntityLivingBase player = minecraft.player;
			WeaponsCapability cap = minecraft.player.getCapability(TF2weapons.WEAPONS_CAP, null);
			int oldState = cap.state & 3;
			int plus = cap.state & 8;
			int state = getActionType(attackKeyDown, altAttackKeyDown) + plus;
			cap.state = state;
			int stateOverride = ((ItemUsable) item.getItem()).getStateOverride(item, player, state);
			if (item != null && item.getItem() instanceof ItemUsable && oldState != (getActionType(attackKeyDown, altAttackKeyDown) & 3)
					&& item.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active == 2) {
				if ((oldState & 2) < (state & 2)) {
					//cap.setSecondaryCooldown(EnumHand.OFF_HAND, ((ItemUsable) item.getItem()).getAltFiringSpeed(item, player) / 2);
					cap.stateDo(player, item, EnumHand.MAIN_HAND, stateOverride);
					((ItemUsable) item.getItem()).startUse(item, player, player.world, oldState, state & 3);
				} else if ((oldState & 2) > (state & 2)) {
					((ItemUsable) item.getItem()).endUse(item, player, player.world, oldState, state & 3);
				}
				if ((oldState & 1) < (state & 1)) {
					//cap.setPrimaryCooldown(EnumHand.OFF_HAND, ((ItemUsable) item.getItem()).getFiringSpeed(item, player) / 2);
					cap.stateDo(player, item, EnumHand.MAIN_HAND, stateOverride);
					((ItemUsable) item.getItem()).startUse(item, player, player.world, oldState, state & 3);
				} else if ((oldState & 1) > (state & 1)) {
					((ItemUsable) item.getItem()).endUse(item, player, player.world, oldState, state & 3);
				}
			}
			TF2weapons.network.sendToServer(new TF2Message.ActionMessage(getActionType(attackKeyDown, altAttackKeyDown)));
		}
	}

	@SubscribeEvent
	public void clientTickEnd(TickEvent.ClientTickEvent event) {

		Minecraft minecraft = Minecraft.getMinecraft();

		if (event.phase == TickEvent.Phase.END) {

			if (minecraft.player != null && minecraft.currentScreen != null) {
				WeaponsCapability.get(minecraft.player).setPrimaryCooldown(Math.max(WeaponsCapability.get(minecraft.player).getPrimaryCooldown(), 250));
			}



			Iterator<EntityLivingBase> soundIterator = ClientProxy.soundsToStart.keySet().iterator();
			while (soundIterator.hasNext()) {
				//EntityLivingBase living = soundIterator.next();

				soundIterator.remove();
			}
			if(TF2ConfigVars.dynamicLights){
				removeSource();
			}
			// ItemUsable.tick(true);
		}
	}

	/*@SubscribeEvent
	public void entityConstructing(final EntityEvent.EntityConstructing event) {

		if (event.getEntity() instanceof EntityPlayerSP) {
			int[][] tex = new int[1][skinIcon.getIconWidth()*skinIcon.getIconHeight()];

			for (int x = 0; x < skinIcon.getIconWidth(); x++) {
				for (int y = 0; y < skinIcon.getIconHeight(); y++) {
					tex[0][x+skinIcon.getIconWidth()*y] = ((EntityLivingBase) event.getEntity()).getRNG().nextInt();
				}
			}

			TextureUtil.uploadTextureMipmap(tex, skinIcon.getIconWidth(), skinIcon.getIconHeight(), skinIcon.getOriginX(), skinIcon.getOriginY(), false, false);
		}
	}*/

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

	public static boolean reloadPressed(Minecraft minecraft) {
		return ClientProxy.reload.isKeyDown() || (minecraft.player != null && TF2ConfigVars.autoReload && minecraft.player.getHeldItemMainhand().getMaxDamage() != 0);
	}

	public static int getActionType(boolean attackKeyDown, boolean altAttackKeyDown) {
		int value = 0;
		ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
		boolean swap = TF2ConfigVars.swapAttackButton && !(stack.getItem() instanceof ItemMeleeWeapon);
		boolean allow = true;
		if (Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK ) {
			BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
			if (ClientProxy.interactingBlocks.contains(Minecraft.getMinecraft().world.getBlockState(pos).getBlock().getClass()))
				allow = false;
		}

		if ((!swap && attackKeyDown) || (swap && altAttackKeyDown && allow)) {
			value++;
		}
		if ((swap && attackKeyDown) || (!swap && altAttackKeyDown && allow)) {
			value += 2;
		}
		if (reloadPressed(Minecraft.getMinecraft())) {
			value += 4;
		}
		return value;
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

			if (event.getGui() instanceof GuiIngameMenu)
				event.getButtonList().add(new GuiButton(125362351,event.getGui().width / 2 - 40, event.getGui().height - 40, 80, 20, "Contracts"));
			//ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();

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
			Minecraft.getMinecraft().player.closeScreen();
		}
		else if (event.getGui() instanceof GuiMerchant && event.getButton().id == 7579) {
			TF2weapons.network.sendToServer(new TF2Message.ActionMessage(18));
		}

		if (event.getGui() instanceof GuiIngameMenu)
			if (event.getButton().id == 125362351) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiContracts());
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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		Entity mouseTarget = Minecraft.getMinecraft().objectMouseOver != null ? Minecraft.getMinecraft().objectMouseOver.entityHit : null;
		if (event.getType() == ElementType.HELMET) {
			if (player.getActiveItemStack().getItem() instanceof ItemWrench && player.getItemInUseCount() < 770){


			}
			if ((player.getCapability(TF2weapons.PLAYER_CAP, null).newContracts || player.getCapability(TF2weapons.PLAYER_CAP, null).newRewards)){
				String line1;
				String line2;
				if( player.getCapability(TF2weapons.PLAYER_CAP, null).newRewards) {
					line1="You can claim your contract reward";
					line2="Contracts can be accessed in the pause menu";
				}
				else {
					line1="You have a new contract";
					line2="Contracts can be accessed in the pause menu";
				}
				gui.getFontRenderer().drawString(line1, event.getResolution().getScaledWidth()-
						gui.getFontRenderer().getStringWidth(line1), 50, 0xFFFFFF);
				gui.getFontRenderer().drawString(line2, event.getResolution().getScaledWidth()-
						gui.getFontRenderer().getStringWidth(line2), 65, 0xFFFFFF);
			}
			if (ClientProxy.reload.isKeyDown()){

				int commands = 5;
				for (int i = 0; i < commands; i++) {
					String line = I18n.format("gui.command."+(i+1));
					gui.getFontRenderer().drawString(line, 5, (event.getResolution().getScaledHeight() - commands*15)/2 + i * 15, 0xFFFFFF);
				}
			}
			if (player.getActivePotionEffect(TF2weapons.uber)!=null){
				GlStateManager.disableDepth();
				GlStateManager.depthMask(false);
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				ClientProxy.setColor(TF2Util.getTeamColor(player), 1f, 0, 0f, 1f);
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.VIGNETTE);
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
		}
		if (event.getType() == ElementType.HOTBAR) {
			ItemStack pda = TF2Util.getFirstItem(Minecraft.getMinecraft().player.inventory, stackL -> stackL.getItem() instanceof ItemPDA);
			if (TF2EventsCommon.sentryView != null && !pda.isEmpty() && pda.hasTagCompound() && pda.getTagCompound().getBoolean("ShowHud")) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
				TF2PlayerCapability plcap = TF2PlayerCapability.get(player);
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.75f, 0.75f, 1f);

				if (plcap.getSentryView().getSize() > 0)
					TF2EventsCommon.sentryView.readEntityFromNBT(plcap.getSentryView());
				else
					TF2EventsCommon.sentryView.setHealth(0);

				GlStateManager.translate(4, 4, 0);
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
				TF2EventsCommon.sentryView.renderGUI(renderer, tessellator, player, width, height, Minecraft.getMinecraft().ingameGUI);

				if (plcap.getDispenserView().getSize() > 0)
					TF2EventsCommon.dispenserView.readEntityFromNBT(plcap.getDispenserView());
				else {
					TF2EventsCommon.dispenserView.setHealth(0);
				}

				GlStateManager.translate(0, 68, 0);
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
				TF2EventsCommon.dispenserView.renderGUI(renderer, tessellator, player, width, height, Minecraft.getMinecraft().ingameGUI);

				if (plcap.getTeleporterAView().getSize() > 0)
					TF2EventsCommon.teleporterAView.readEntityFromNBT(plcap.getTeleporterAView());
				else
					TF2EventsCommon.teleporterAView.setHealth(0);

				GlStateManager.translate(0, 52, 0);
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
				TF2EventsCommon.teleporterAView.renderGUI(renderer, tessellator, player, width, height, Minecraft.getMinecraft().ingameGUI);

				if (plcap.getTeleporterBView().getSize() > 0)
					TF2EventsCommon.teleporterBView.readEntityFromNBT(plcap.getTeleporterBView());
				else
					TF2EventsCommon.teleporterBView.setHealth(0);

				GlStateManager.translate(0, 52, 0);
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
				TF2EventsCommon.teleporterBView.renderGUI(renderer, tessellator, player, width, height, Minecraft.getMinecraft().ingameGUI);
				GlStateManager.popMatrix();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			if (!held.isEmpty()
					&& held.getItem() instanceof IItemOverlay) {
				if(((IItemOverlay)held.getItem()).showInfoBox(held, player)) {
					Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.healingTexture);

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

					GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
					renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(event.getResolution().getScaledWidth() - 76, event.getResolution().getScaledHeight() - 18, 0.0D).tex(0.5D, 0.265625D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() - 12, event.getResolution().getScaledHeight() - 18, 0.0D).tex(1.0D, 0.265625D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() - 12, event.getResolution().getScaledHeight() - 52, 0.0D).tex(1.0D, 0.53125D).endVertex();
					renderer.pos(event.getResolution().getScaledWidth() - 76, event.getResolution().getScaledHeight() - 52, 0.0D).tex(0.5D, 0.53125D).endVertex();
					tessellator.draw();
					String[] text=((IItemOverlay)held.getItem()).getInfoBoxLines(held, player);
					gui.drawString(gui.getFontRenderer(), text[0],
							event.getResolution().getScaledWidth() - 66, event.getResolution().getScaledHeight() - 48, 16777215);
					gui.drawString(gui.getFontRenderer(), text[1],
							event.getResolution().getScaledWidth() - 66, event.getResolution().getScaledHeight() - 30, 16777215);
					GL11.glDepthMask(true);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				}
				((IItemOverlay)held.getItem()).drawOverlay(held, player, Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), event.getResolution());
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
			Entity healTarget = player.world.getEntityByID(cap.getHealTarget());


			if (healTarget == null && TF2Util.isOnSameTeam(mouseTarget, player) && !(mouseTarget instanceof EntityBuilding))
				healTarget = mouseTarget;

			if (healTarget != null && healTarget instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) healTarget;

				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.healingTexture);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);

				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
				// gui.drawTexturedModalRect(event.getResolution().getScaledWidth()/2-64,
				// event.getResolution().getScaledHeight()/2+35, 0, 0, 128, 40);
				setColorTeam(player,0.7f);

				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 62, event.getResolution().getScaledHeight() / 2 + 70, 0.0D).tex(0.0D, 1D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 + 62, event.getResolution().getScaledHeight() / 2 + 70, 0.0D).tex(0.01D, 1D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 + 62, event.getResolution().getScaledHeight() / 2 + 40, 0.0D).tex(0.01D, 0.99D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 62, event.getResolution().getScaledHeight() / 2 + 40, 0.0D).tex(0.0D, 0.99D).endVertex();
				tessellator.draw();

				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 64, event.getResolution().getScaledHeight() / 2 + 72, 0.0D).tex(0.0D, 0.265625D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 + 64, event.getResolution().getScaledHeight() / 2 + 72, 0.0D).tex(1.0D, 0.265625D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 + 64, event.getResolution().getScaledHeight() / 2 + 38, 0.0D).tex(1.0D, 0.0D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 64, event.getResolution().getScaledHeight() / 2 + 38, 0.0D).tex(0.0D, 0.0D).endVertex();
				tessellator.draw();
				float overheal = 1f + living.getAbsorptionAmount() / living.getMaxHealth();
				if (overheal > 1f) {
					GlStateManager.color(1.0F, 1.0F, 1.0F, 0.4F);
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
				GlStateManager.color(0.12F, 0.12F, 0.12F, 1F);
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 58.3, event.getResolution().getScaledHeight() / 2 + 66.4, 0.0D).tex(0.0D, 0.59375D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 35.7, event.getResolution().getScaledHeight() / 2 + 66.4, 0.0D).tex(0.28125D, 0.59375D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 35.7, event.getResolution().getScaledHeight() / 2 + 43.6, 0.0D).tex(0.28125D, 0.3125D).endVertex();
				renderer.pos(event.getResolution().getScaledWidth() / 2 - 58.3, event.getResolution().getScaledHeight() / 2 + 43.6, 0.0D).tex(0.0D, 0.3125D).endVertex();
				tessellator.draw();
				float health = living.getHealth() / living.getMaxHealth();
				if (health > 0.33f) {
					GlStateManager.color(0.9F, 0.9F, 0.9F, 1F);
				} else {
					GlStateManager.color(0.85F, 0.0F, 0.0F, 1F);
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
				String text = "";
				if (player.world.getEntityByID(cap.getHealTarget()) != null) {
					text = "Healing:";
				}
				else if (living.getHeldItemMainhand().getItem() instanceof ItemMedigun) {
					text = "Ubercharge: " + Math.round(living.getHeldItemMainhand().getTagCompound().getFloat("ubercharge")*100f) + "%";
				}
				gui.drawString(gui.getFontRenderer(), text, event.getResolution().getScaledWidth() / 2 - 28,
						event.getResolution().getScaledHeight() / 2 + 42, 16777215);
				gui.drawString(gui.getFontRenderer(), living.getDisplayName().getFormattedText(),
						event.getResolution().getScaledWidth() / 2 - 28, event.getResolution().getScaledHeight() / 2 + 54, 16777215);

				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}

			if (mouseTarget != null && mouseTarget instanceof EntityBuilding
					&& TF2Util.isOnSameTeam(player, mouseTarget)) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
				EntityBuilding building = (EntityBuilding) mouseTarget;
				GlStateManager.translate(width/2-72, height/2+52-building.getGuiHeight()/2, 0);
				building.renderGUI(renderer, tessellator, player, width, height, gui);
				GlStateManager.translate(-width/2+72, -height/2-52+building.getGuiHeight()/2, 0);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
			renderBeam(event.getEntityPlayer(), event.getPartialRenderTick(), 0.04f);
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
		if (WeaponsCapability.get(player).isInvisible() ||(player.getCapability(TF2weapons.WEAPONS_CAP, null).isCharging() &&player.getHeldItemMainhand().getItem() instanceof ItemSniperRifle) ) {

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
	public static void renderBeam(EntityLivingBase ent, float partialTicks, float size) {
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
				GlStateManager.color(1.0F, 0.0F, 0.0F, 0.23F);
			} else {
				GlStateManager.color(0.0F, 0.0F, 1.0F, 0.23F);
			}*/
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(-size, -size, 0).endVertex();
			renderer.pos(size, size, 0).endVertex();
			renderer.pos(size, size, fullDist).endVertex();
			renderer.pos(-size, -size, fullDist).endVertex();
			tessellator.draw();
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(-size, -size, fullDist).endVertex();
			renderer.pos(size, size, fullDist).endVertex();
			renderer.pos(size, size, 0).endVertex();
			renderer.pos(-size, -size, 0).endVertex();
			tessellator.draw();
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(size, -size, 0).endVertex();
			renderer.pos(-size, size, 0).endVertex();
			renderer.pos(-size, size, fullDist).endVertex();
			renderer.pos(size, -size, fullDist).endVertex();
			tessellator.draw();
			renderer.begin(7, DefaultVertexFormats.POSITION);
			renderer.pos(size, -size, fullDist).endVertex();
			renderer.pos(-size, size, fullDist).endVertex();
			renderer.pos(-size, size, 0).endVertex();
			renderer.pos(size, -size, 0).endVertex();
			tessellator.draw();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
			if (minecraft.currentScreen == null && minecraft.player != null && minecraft.player.getHeldItemMainhand() != null)
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
			renderBeam(Minecraft.getMinecraft().player, event.getPartialTicks(), 0.04f);
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

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void renderLivingEntity(RenderLivingEvent.Pre<EntityLivingBase> event) {

		if (!event.getEntity().isEntityAlive())
			return;

		ClientProxy.renderCritGlow=0;

		if (!(event.getEntity() instanceof EntityPlayer || event.getEntity() instanceof EntityTF2Character))
			return;

		if (!event.getEntity().getHeldItemMainhand().isEmpty()){
			ClientProxy.renderCritGlow=TF2Util.calculateCritPre(event.getEntity().getHeldItemMainhand(),event.getEntity())*16+TF2Util.getTeamColorNumber(event.getEntity());
		}
		else{
			ClientProxy.renderCritGlow=0;
		}

		if (event.getEntity().hasCapability(TF2weapons.INVENTORY_CAP, null)) {
			InventoryWearables cap = event.getEntity().getCapability(TF2weapons.INVENTORY_CAP, null);
			for (int i = 0; i < cap.getSizeInventory(); i++) {
				ItemStack hat = cap.getStackInSlot(i);
				if (hat.getItem() instanceof ItemFromData && (((ItemFromData)hat.getItem()).getVisibilityFlags(hat, event.getEntity()) & 1) == 1) {
					cap.origHead = event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
					if (event.getEntity() instanceof EntityPlayer)
						((EntityPlayer)event.getEntity()).inventory.armorInventory.set(3, ItemStack.EMPTY);
					else
						event.getEntity().setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
					break;
				}
			}
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
				//Team team = event.getEntity().getTeam();
				//if (team == event.getEntity().world.getScoreboard().getTeam("RED")) {
				ClientProxy.setColor(TF2Util.getTeamColor(event.getEntity()), 0.7f * (1 - (float) visTick / 20), 0f, 0.17f, 1f);
				//} else if (team == event.getEntity().world.getScoreboard().getTeam("BLU")) {
				//	GlStateManager.color(0.17F, 0.17F, 1.0F, 0.7f * (1 - (float) visTick / 20));
				//} else {
				//	GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7f * (1 - (float) visTick / 20));
				//}
			}
		} else if (event.getEntity() instanceof EntityPlayer && event.getEntity().getHeldItem(EnumHand.MAIN_HAND) != null
				&& event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemUsable
				&& !(event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemMeleeWeapon) && event.getRenderer().getMainModel() instanceof ModelBiped) {
			((ModelBiped) event.getRenderer().getMainModel()).rightArmPose = ((event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).state & 3) > 0)
					|| event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).isCharging() ? ModelBiped.ArmPose.BOW_AND_ARROW : ModelBiped.ArmPose.ITEM;
		}

		if (event.getEntity().getActivePotionEffect(TF2weapons.uber)!=null){
			// GlStateManager.disableLighting();
			int i = TF2Util.getTeamColor(event.getEntity());
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			GlStateManager.enableTexture2D();
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.enableTexture2D();
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			brightnessBuffer.position(0);
			//float f1 = (float)(i >> 24 & 255) / 255.0F;
			float f2 = (float)(i >> 16 & 255) / 255.0F;
			float f3 = (float)(i >> 8 & 255) / 255.0F;
			float f4 = (float)(i & 255) / 255.0F;
			brightnessBuffer.put(f2);
			brightnessBuffer.put(f3);
			brightnessBuffer.put(f4);
			brightnessBuffer.put(0.5f);
			brightnessBuffer.flip();
			GlStateManager.glTexEnv(8960, 8705, brightnessBuffer);
			GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
			GlStateManager.enableTexture2D();
			GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

			//ClientProxy.setColor(TF2Util.getTeamColor(event.getEntity()), 1f, 0f, 0.33f, 1f);
		}
		if (event.getRenderer() != ClientProxy.disguiseRender && event.getRenderer() != ClientProxy.disguiseRenderPlayer
				&& event.getRenderer() != ClientProxy.disguiseRenderPlayerSmall && WeaponsCapability.get(event.getEntity()).isDisguised()) {
			float partialTicks = tickTime;

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

					Minecraft.getMinecraft().getRenderManager().renderEntity(entToRender, event.getX(), event.getY(), event.getZ(), event.getEntity().rotationYaw, partialTicks, false);
					event.setCanceled(true);
				}
			} else if (event.getEntity() instanceof AbstractClientPlayer && WeaponsCapability.get(event.getEntity()).getDisguiseType().startsWith("P:"))
				if ("slim".equals(event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).skinType)) {
					render = (RenderLivingBase<EntityLivingBase>) ClientProxy.disguiseRenderPlayerSmall;
				} else {
					render = (RenderLivingBase<EntityLivingBase>) ClientProxy.disguiseRenderPlayer;
				}
			if (render != null) {
				render.doRender(event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntity().rotationYaw, partialTicks);
				event.setCanceled(true);
			}
		}

	}

	@SubscribeEvent
	public void renderLivingPostEntity(RenderLivingEvent.Post<EntityLivingBase> event) {
		if (event.getRenderer().getRenderManager().isDebugBoundingBox() && !event.getEntity().isInvisible() && !Minecraft.getMinecraft().isReducedDebug()){
			GlStateManager.depthMask(false);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			AxisAlignedBB head=TF2Util.getHead(event.getEntity()).offset(-event.getEntity().posX, -event.getEntity().posY, -event.getEntity().posZ);
			RenderGlobal.drawBoundingBox(head.minX + event.getX(), head.minY + event.getY(), head.minZ + event.getZ(), head.maxX + event.getX(), head.maxY + event.getY(), head.maxZ + event.getZ(), 1.0F, 0.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}
		if (!(event.getEntity() instanceof EntityPlayer || event.getEntity() instanceof EntityTF2Character))
			return;

		if (event.getEntity().hasCapability(TF2weapons.INVENTORY_CAP, null)) {
			InventoryWearables cap = event.getEntity().getCapability(TF2weapons.INVENTORY_CAP, null);
			if (!cap.origHead.isEmpty()) {
				if (event.getEntity() instanceof EntityPlayer)
					((EntityPlayer)event.getEntity()).inventory.armorInventory.set(3, cap.origHead);
				else
					event.getEntity().setItemStackToSlot(EntityEquipmentSlot.HEAD, cap.origHead);
				cap.origHead = ItemStack.EMPTY;
			}
		}

		ClientProxy.renderCritGlow=0;
		if (event.getEntity().getActivePotionEffect(TF2weapons.uber)!=null) {
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			GlStateManager.enableTexture2D();
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
			GlStateManager.disableTexture2D();
			GlStateManager.bindTexture(0);
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			//GlStateManager.color(1.0F, 1F, 1.0F, 1F);
		}
		// GlStateManager.enableLighting();
		if (event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
			GL11.glDisable(GL11.GL_BLEND);
			GlStateManager.color(1.0F, 1F, 1.0F, 1F);
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

	/*protected static boolean setBrightness(float partialTicks, boolean combineTextures)
    {
        float f = entitylivingbaseIn.getBrightness();
        int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
        boolean flag = (i >> 24 & 255) > 0;
        boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;

        if (!flag && !flag1)
        {
            return false;
        }
        else if (!flag && !combineTextures)
        {
            return false;
        }
        else
        {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
            brightnessBuffer.position(0);

            if (flag1)
            {
                brightnessBuffer.put(1.0F);
                brightnessBuffer.put(0.0F);
                brightnessBuffer.put(0.0F);
                brightnessBuffer.put(0.5F);
            }
            else
            {
                float f1 = (float)(i >> 24 & 255) / 255.0F;
                float f2 = (float)(i >> 16 & 255) / 255.0F;
                float f3 = (float)(i >> 8 & 255) / 255.0F;
                float f4 = (float)(i & 255) / 255.0F;
                brightnessBuffer.put(f2);
                brightnessBuffer.put(f3);
                brightnessBuffer.put(f4);
                brightnessBuffer.put(1.0F - f1);
            }

            brightnessBuffer.flip();
            GlStateManager.glTexEnv(8960, 8705, brightnessBuffer);
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
            GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            return true;
        }
    }

    protected void unsetBrightness()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
        GlStateManager.disableTexture2D();
        GlStateManager.bindTexture(0);
        GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }*/

}
