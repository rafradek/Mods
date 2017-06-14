package rafradek.TF2weapons.weapons;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;

public class GuiDisguiseKit extends GuiScreen {

	public GuiButton playerDisguise;
	private GuiTextField playerNameField;
	public EntityPlayer player;
	public EntityLivingBase[] mobs = new EntityLivingBase[7];
	public boolean needUpdating;
	public int ticksUpdate;
	public GuiDisguiseKit() {
		
	}

	@Override
	public void initGui() {
		player = new EntityOtherPlayerMP(this.mc.world, new GameProfile(null, "name"));
		mobs[0] = new EntityZombie(this.mc.world);
		mobs[1] = new EntityCreeper(this.mc.world);
		mobs[2] = new EntityEnderman(this.mc.world);
		mobs[3] = new EntitySpider(this.mc.world);
		mobs[4] = new EntityCow(this.mc.world);
		mobs[5] = new EntityPig(this.mc.world);
		mobs[6] = new EntityChicken(this.mc.world);
		Keyboard.enableRepeatEvents(true);
		this.playerNameField = new GuiTextField(6, this.fontRendererObj, this.width / 2 + 76, this.height / 2 + 70, 58,
				19);
		this.playerNameField.setMaxStringLength(64);
		this.playerNameField.setFocused(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 135, this.height / 2 - 20, 60, 20,
				I18n.format(EntityList.getTranslationName(EntityList.getKey(EntityZombie.class)), new Object[0])));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 65, this.height / 2 - 20, 60, 20,
				I18n.format(EntityList.getTranslationName(EntityList.getKey(EntityCreeper.class)), new Object[0])));
		this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height / 2 - 20, 60, 20,
				I18n.format(EntityList.getTranslationName(EntityList.getKey(EntityEnderman.class)), new Object[0])));
		this.buttonList.add(new GuiButton(7, this.width / 2 + 75, this.height / 2 - 20, 60, 20,
				I18n.format(EntityList.getTranslationName(EntityList.getKey(EntitySpider.class)), new Object[0])));
		this.buttonList.add(new GuiButton(3, this.width / 2 - 135, this.height / 2 + 90, 60, 20,
				I18n.format(EntityList.getTranslationName(EntityList.getKey(EntityCow.class)), new Object[0])));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 65, this.height / 2 + 90, 60, 20,
				I18n.format(EntityList.getTranslationName(EntityList.getKey(EntityPig.class)), new Object[0])));
		this.buttonList.add(new GuiButton(8, this.width / 2 + 5, this.height / 2 + 90, 60, 20,
				I18n.format(EntityList.getTranslationName(EntityList.getKey(EntityChicken.class)), new Object[0])));
		this.buttonList
				.add(playerDisguise = new GuiButton(5, this.width / 2 + 75, this.height / 2 + 90, 60, 20, "Player"));

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 0)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("M:Zombie"));
			if (button.id == 1)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("M:Creeper"));
			if (button.id == 2)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("M:Enderman"));
			if (button.id == 3)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("M:Cow"));
			if (button.id == 4)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("M:Pig"));
			if (button.id == 5)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("P:" + playerNameField.getText()));
			if (button.id == 7)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("M:Spider"));
			if (button.id == 8)
				TF2weapons.network.sendToServer(new TF2Message.DisguiseMessage("M:Chicken"));
			this.mc.displayGuiScreen(null);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.playerNameField.mouseClicked(mouseX, mouseY, mouseButton);

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		this.playerNameField.textboxKeyTyped(typedChar, keyCode);
		if (playerNameField.isFocused())
			this.needUpdating=true;
			
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, I18n.format("gui.disguise.info", new Object[0]),
				this.width / 2 - 5, 20, 16777215);
		for (int i = 0; i < this.mobs.length; i++)
			drawEntityOnScreen(this.width / 2 - 105 + (i % 4) * 70, this.height / 2 - 26 + 110 * (i / 4), 35, mobs[i]);
		drawEntityOnScreen(this.width / 2 + 105, this.height / 2 + 66, 35, player);
		this.playerNameField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);

	}

	public static void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, 50.0F);
		GlStateManager.scale((-scale), scale, scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		ent.renderYawOffset = 0;
		ent.rotationYaw = 0;
		ent.rotationPitch = 0;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.doRenderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen() {
		if(--this.ticksUpdate<0&&this.needUpdating&&!StringUtils.isBlank(playerNameField.getText())){
			this.needUpdating=false;
			this.ticksUpdate=12;
			TF2EventsCommon.THREAD_POOL.submit(new Runnable() {

				@Override
				public void run() {
					GameProfile profile = TileEntitySkull
							.updateGameprofile(new GameProfile(null, playerNameField.getText()));
					player = new EntityOtherPlayerMP(mc.world, profile);
					player.getDataManager().set(TF2EventsCommon.ENTITY_DISGUISED, true);
					player.getDataManager().set(TF2EventsCommon.ENTITY_DISGUISE_TYPE, "P:"+playerNameField.getText());
					final WeaponsCapability cap = player.getCapability(TF2weapons.WEAPONS_CAP, null);
					cap.skinType = DefaultPlayerSkin.getSkinType(player.getUniqueID());
					if (profile.getId() != null)
						cap.skinType = DefaultPlayerSkin.getSkinType(profile.getId());
					Minecraft.getMinecraft().getSkinManager().loadProfileTextures(profile,
							new SkinManager.SkinAvailableCallback() {
								@Override
								public void skinAvailable(Type typeIn, ResourceLocation location,
										MinecraftProfileTexture profileTexture) {
									if (typeIn == Type.SKIN) {
										if (typeIn == Type.SKIN)
											cap.skinDisguise = location;
										cap.skinType = profileTexture.getMetadata("model");

										if (cap.skinType == null)
											cap.skinType = "default";
									}
								}
							}, false);
				}

			});
		}
		// this.playerNameField.updateCursorCounter();
	}
}
