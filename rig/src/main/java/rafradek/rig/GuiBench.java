package rafradek.rig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Attribute;

public class GuiBench extends GuiContainer {
	public GuiBench(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		// TODO Auto-generated constructor stub
	}

	private static final ResourceLocation UPGRADES_GUI_TEXTURES = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/gui/container/upgrades.png");

	// public ItemStack[] itemsToRender;
	public GuiButton[] buttons = new GuiButton[12];
	public GuiButton[] buttonsItems = new GuiButton[5];
	public int firstIndex;
	public float scroll;
	public int tabid;
	
	public ItemStack craftingTabStack = new ItemStack(TF2weapons.itemAmmo, 1, 1);
	public ItemStack chestTabStack = new ItemStack(Blocks.CHEST);

	public int selectedItem = -1;
	
	private boolean isScrolling;

	private boolean wasClicking;

	public GuiBench(InventoryPlayer playerInv, World worldIn,
			BlockPos blockPosition) {
		super(new ContainerBench(Minecraft.getMinecraft().player, playerInv, worldIn, blockPosition));
		this.xSize = 230;
		this.ySize = 216;
		// this.itemsToRender=new ItemStack[9];
	}

	@Override
	public void initGui() {
		super.initGui();
		for (int x = 0; x < 2; x++)
			for (int y = 0; y < 3; y++) {
				this.buttonList.add(buttons[x * 2 + y * 4] = new GuiButton(x * 2 + y * 4, this.guiLeft + 81 + x * 101,
						this.guiTop + 47 + y * 30, 12, 12, "+"));
				this.buttonList.add(buttons[x * 2 + y * 4 + 1] = new GuiButton(x * 2 + y * 4 + 1,
						this.guiLeft + 94 + x * 101, this.guiTop + 47 + y * 30, 12, 12, "-"));
			}
		for (int i = 0; i < 5; i++) {
			this.buttonList.add(buttonsItems[i]=new GuiButton(12 + i, this.guiLeft + 5, this.guiTop+20 + i * 20, 120, 20, ""));
		}
		setButtons();
	}

	public void setButtons() {
		for (int i = 0; i < 12; i++) {
			GuiButton button = this.buttons[i];
			button.visible = this.selectedItem != -1 && !this.inventorySlots.getSlot(this.selectedItem).getStack().isEmpty();
		}
		for (int i = 0; i < 5; i++) {
			GuiButton button = this.buttonsItems[i];
			if (this.selectedItem != -1 && !this.inventorySlots.getSlot(this.selectedItem).getStack().isEmpty())
				button.visible = false;
			else {
				button.visible = true;
				if (i == 0) {
					button.enabled = true;
					button.displayString = "RIG";
				}
				else {
					button.enabled = false;
					button.displayString = "";
				}
			}
		}
		/*
		 * for(int i=0;i<12;i++){
		 * //System.out.println("Buttons: "+buttonsItem[i]+" "+firstIndex);
		 * if(i+firstIndex<TF2CraftingManager.INSTANCE.getRecipeList().size()){
		 * buttonsItem[i].stackToDraw=TF2CraftingManager.INSTANCE.getRecipeList(
		 * ).get(i+firstIndex).getRecipeOutput();
		 * buttonsItem[i].selected=i+firstIndex==((ContainerTF2Workbench)this.
		 * inventorySlots).currentRecipe; } else{
		 * buttonsItem[i].stackToDraw=null; buttonsItem[i].selected=false; } }
		 */
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		boolean flag = Mouse.isButtonDown(0);
		int i = this.guiLeft;
		int j = this.guiTop;
		int k = i + 209;
		int l = j + 30;
		int i1 = k + 14;
		int j1 = l + 96;

		if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
			this.isScrolling = true;

		if (!flag)
			this.isScrolling = false;

		this.wasClicking = flag;

		if (this.isScrolling) {
			int size = 6;
			this.scroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
			this.scroll = MathHelper.clamp(this.scroll, 0.0F, 1.0F);
			this.firstIndex = Math.round(this.scroll * (size - 6) / 2) * 2;
			this.setButtons();
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y) {
		drawHoveringText(textLines, x, y, fontRenderer);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id < 12)
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id + this.firstIndex * 2);
		else if (button.id < 17) {
			if (button.id == 12)
				this.selectedItem = 37;
			System.out.println(this.selectedItem);
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 64 + button.id - 12);
			setButtons();
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ItemStack stack;
		if (this.selectedItem != -1 && !(stack = this.inventorySlots.getSlot(this.selectedItem).getStack()).isEmpty()) {
			ArrayList<RIGUpgrade> upgrades = RIGUpgrade.getUpgrades(stack);
			for (int i = 0; i < 6 && i < upgrades.size(); i++) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.mc.getTextureManager().bindTexture(UPGRADES_GUI_TEXTURES);
	
				RIGUpgrade attr = upgrades.get(i + firstIndex);
	
				int xOffset = 101 * (i % 2);
				int yOffset = (i / 2) * 30;
				int currLevel = attr.getAttributeValue(stack);
				for (int j = 0; j < 6; j++)
					// System.out.println("render: "+currLevel+"
					// "+this.inventorySlots.inventorySlots.get(0).getStack());
					this.drawTexturedModalRect(9 + xOffset + j * 9, 50 + yOffset, currLevel > j ? 240 : 248, 24, 8, 8);
				this.fontRenderer.drawSplitString(attr.name, 9 + xOffset,
						32 + yOffset, 98, 16777215);
				
				ItemStack node = this.inventorySlots.inventorySlots.get(0).getStack();
				
				if (currLevel >= 6 || node.isEmpty() || ContainerBench.getValidNodeType(stack) != node.getMetadata()) {
					// System.out.println("DrawingRect");
					this.buttons[i * 2].enabled = false;
					this.buttons[i * 2 + 1].enabled = currLevel>0;
					this.drawGradientRect(8 + xOffset, 31 + yOffset, 107 + xOffset, 59 + yOffset, 0x77000000, 0x77000000);
				} else {
					this.buttons[i * 2].enabled = true;
					this.buttons[i * 2 + 1].enabled = currLevel>0;
				}
			}
		}
		this.fontRenderer.drawString(I18n.format("container.upgrades", new Object[0]), 8, 5, 4210752);
		//this.fontRenderer.drawString(I18n.format("container.currency", new Object[] { String.valueOf(expPoints) }),
		//		128, 10, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 36, this.ySize - 96 + 3,
				4210752);
		/*
		 * for(int i=0;i<12;i++){ if(this.buttonsItem[i].stackToDraw !=null &&
		 * this.buttonsItem[i].isMouseOver()){
		 * ((GuiTF2Crafting)mc.currentScreen).drawHoveringText(this.buttonsItem[
		 * i].stackToDraw.getTooltip(mc.player, false), mouseX-this.guiLeft,
		 * mouseY-this.guiTop); } }
		 */
		/*
		 * for(int i=0;i<4;i++){
		 * this.fontRendererObj.drawString(I18n.format(TF2CraftingManager.
		 * INSTANCE.getRecipeList().get(i).getRecipeOutput().getDisplayName(),
		 * new Object[0]), 10, 17+18*i, 16777215); }
		 */
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(UPGRADES_GUI_TEXTURES);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		// this.drawTab(0);
		// this.drawTab(1);

		x = this.guiLeft + 210;
		y = this.guiTop + 31;
		int k = y + 96;

		this.drawTexturedModalRect(x, y + (int) ((k - y - 17) * this.scroll), 232, 0, 12, 15);

		/*
		 * GlStateManager.enableLighting();
		 * GlStateManager.enableRescaleNormal();
		 * RenderHelper.enableGUIStandardItemLighting();
		 * this.itemRender.zLevel=0; for(x=0;x<3;x++){ for(y=0;y<3;y++){
		 * ItemStack stack=this.itemsToRender[x+y*3]; if(stack!=null){
		 * this.itemRender.renderItemIntoGUI(stack, this.guiLeft+86+18*x,
		 * this.guiTop+23+18*y); } } }
		 * RenderHelper.disableStandardItemLighting();
		 * GlStateManager.disableLighting();
		 * this.mc.getTextureManager().bindTexture(UPGRADES_GUI_TEXTURES);
		 * GlStateManager.enableBlend(); GlStateManager.color(1.0F, 1.0F, 1.0F,
		 * 0.5F);
		 */
		/*
		 * this.zLevel=120; this.drawTexturedModalRect(85+this.guiLeft,
		 * 22+this.guiTop, 85, 22, 54,54); this.zLevel=0;
		 */
		/*
		 * int currentRecipe=((ContainerTF2Workbench)this.inventorySlots).
		 * currentRecipe;
		 * if(currentRecipe>=0&&currentRecipe<TF2CraftingManager.INSTANCE.
		 * getRecipeList().size()){ IRecipe
		 * recipe=TF2CraftingManager.INSTANCE.getRecipeList().get(currentRecipe)
		 * ;
		 * 
		 * if(recipe instanceof ShapelessOreRecipe){ List<Object> input=;
		 * for(int i=0;i<((ShapelessOreRecipe)recipe).getInput().size();i++){
		 * this.itemRender.renderItemIntoGUI(((ShapelessOreRecipe)recipe).
		 * getInput().get(i), , y); } }
		 * 
		 * }
		 */
	}
	/*
	 * protected void renderItemModelIntoGUI(ItemStack stack, int x, int y,
	 * IBakedModel bakedmodel) { GlStateManager.pushMatrix();
	 * this.mc.getTextureManager().bindTexture(TextureMap.
	 * LOCATION_BLOCKS_TEXTURE);
	 * this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE
	 * ).setBlurMipmap(false, false); GlStateManager.enableRescaleNormal();
	 * GlStateManager.enableAlpha(); GlStateManager.alphaFunc(516, 0.1F);
	 * GlStateManager.enableBlend();
	 * GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
	 * GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); this.setupGuiTransform(x,
	 * y, bakedmodel.isGui3d()); bakedmodel =
	 * net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(
	 * bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
	 * GlStateManager.color(0.4F, 0.4F, 0.4F); this.itemRender.renderItem(stack,
	 * bakedmodel); GlStateManager.disableAlpha();
	 * GlStateManager.disableRescaleNormal(); GlStateManager.disableLighting();
	 * GlStateManager.popMatrix();
	 * this.mc.getTextureManager().bindTexture(TextureMap.
	 * LOCATION_BLOCKS_TEXTURE);
	 * this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE
	 * ).restoreLastBlurMipmap(); } private void setupGuiTransform(int
	 * xPosition, int yPosition, boolean isGui3d) {
	 * GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F +
	 * this.zLevel); GlStateManager.translate(8.0F, 8.0F, 0.0F);
	 * GlStateManager.scale(1.0F, -1.0F, 1.0F); GlStateManager.scale(16.0F,
	 * 16.0F, 16.0F);
	 * 
	 * if (isGui3d) { GlStateManager.enableLighting(); } else {
	 * GlStateManager.disableLighting(); } }
	 */
}