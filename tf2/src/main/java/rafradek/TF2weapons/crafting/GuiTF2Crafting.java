package rafradek.TF2weapons.crafting;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class GuiTF2Crafting extends GuiContainer {
	private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/gui/container/cabinet.png");

	public GuiButtonToggleItem[] buttonsItem;
	public ItemStack[] itemsToRender;
	public int firstIndex;
	public float scroll;
	public int tabid;
	public ItemStack craftingTabStack = new ItemStack(TF2weapons.itemAmmo, 1, 1);
	public ItemStack chestTabStack = new ItemStack(Blocks.CHEST);

	// public TileEntityCabinet cabinet;
	private boolean isScrolling;

	private boolean wasClicking;

	public GuiTF2Crafting(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition) {
		super(new ContainerTF2Workbench(Minecraft.getMinecraft().player, playerInv, worldIn, blockPosition));
		// this.cabinet=cabinet;
		this.xSize = 176;
		this.ySize = 180;
		this.itemsToRender = new ItemStack[9];
		this.buttonsItem = new GuiButtonToggleItem[12];
	}

	@Override
	public void initGui() {
		super.initGui();
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 4; y++)
				this.buttonList.add(buttonsItem[x + y * 3] = new GuiButtonToggleItem(x + y * 3,
						this.guiLeft + 7 + x * 18, this.guiTop + 14 + y * 18, 18, 18));
		setButtons();
	}

	public void setButtons() {
		for (int i = 0; i < 12; i++)
			// System.out.println("Buttons: "+buttonsItem[i]+" "+firstIndex);
			if (i + firstIndex < TF2CraftingManager.INSTANCE.getRecipeList().size()) {
				buttonsItem[i].stackToDraw = ContainerTF2Workbench.getReplacement(this.mc.player, TF2CraftingManager.INSTANCE.getRecipeList().get(i + firstIndex)
						.getRecipeOutput().copy());
				buttonsItem[i].selected = i + firstIndex == ((ContainerTF2Workbench) this.inventorySlots).currentRecipe;
			} else {
				buttonsItem[i].stackToDraw = ItemStack.EMPTY;
				buttonsItem[i].selected = false;
			}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		boolean flag = Mouse.isButtonDown(0);
		int i = this.guiLeft;
		int j = this.guiTop;
		int k = i + 61;
		int l = j + 14;
		int i1 = k + 14;
		int j1 = l + 72;

		if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
			this.isScrolling = true;

		if (!flag)
			this.isScrolling = false;

		this.wasClicking = flag;

		if (this.isScrolling) {
			int size = TF2CraftingManager.INSTANCE.getRecipeList().size();
			this.scroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
			this.scroll = MathHelper.clamp(this.scroll, 0.0F, 1.0F);
			this.firstIndex = Math.round(this.scroll * (size - 12) / 3) * 3;
			this.setButtons();
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	protected void drawTab(int id) {
		boolean flag = id == tabid;
		boolean flag1 = true;
		int i = id;
		int j = 200 + i * 28;
		int k = 16;
		int l = this.guiLeft + 28 * i;
		int i1 = this.guiTop;
		int j1 = 32;

		if (flag)
			k += 32;

		if (i == 5)
			l = this.guiLeft + this.xSize - 28;
		else if (i > 0)
			l += i;

		if (flag1)
			i1 = i1 - 28;
		else {
			k += 64;
			i1 = i1 + (this.ySize - 4);
		}

		GlStateManager.disableLighting();
		GlStateManager.color(1F, 1F, 1F); // Forge: Reset color in case Items
											// change it.
		GlStateManager.enableBlend(); // Forge: Make sure blend is enabled else
										// tabs show a white border.
		this.drawTexturedModalRect(l, i1, j, k, 28, 32);
		this.zLevel = 100.0F;
		this.itemRender.zLevel = 100.0F;
		l = l + 6;
		i1 = i1 + 8 + (flag1 ? 1 : -1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		ItemStack itemstack = id == 0 ? craftingTabStack : chestTabStack;
		this.itemRender.renderItemAndEffectIntoGUI(itemstack, l, i1);
		this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, l, i1);
		GlStateManager.disableLighting();
		this.itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	/*@Override
	public void drawHoveringText(List<String> textLines, int x, int y) {
		drawHoveringText(textLines, x, y, fontRenderer);
	}*/

	@Override
	@SuppressWarnings("unchecked")
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id < 12) {
			int currentRecipe = button.id + this.firstIndex;
			((ContainerTF2Workbench) this.inventorySlots).currentRecipe = currentRecipe;
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, currentRecipe);
			setButtons();
			this.inventorySlots.onCraftMatrixChanged(null);
			itemsToRender = new ItemStack[9];
			if (currentRecipe >= 0 && currentRecipe < TF2CraftingManager.INSTANCE.getRecipeList().size()) {
				IRecipe recipe = TF2CraftingManager.INSTANCE.getRecipeList().get(currentRecipe);
				if (recipe instanceof AustraliumRecipe) {
					itemsToRender[0] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[1] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[2] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[3] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[5] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[6] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[7] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[8] = new ItemStack(TF2weapons.itemTF2, 1, 2);
					itemsToRender[4] = new ItemStack(TF2weapons.itemTF2, 1, 9);
				} else if (recipe instanceof JumperRecipe) {
					itemsToRender[0] = new ItemStack(Items.FEATHER);
					itemsToRender[1] = new ItemStack(Items.FEATHER);
					itemsToRender[2] = new ItemStack(Items.FEATHER);
					itemsToRender[3] = new ItemStack(Items.FEATHER);
					itemsToRender[5] = new ItemStack(Items.FEATHER);
					itemsToRender[6] = new ItemStack(Items.FEATHER);
					itemsToRender[7] = new ItemStack(Items.FEATHER);
					itemsToRender[8] = new ItemStack(Items.FEATHER);
					itemsToRender[4] = ItemFromData.getNewStack(((JumperRecipe)recipe).nameBefore);
				} else if (recipe instanceof RecipeToScrap) {
					itemsToRender[0] = new ItemStack(TF2weapons.itemTF2, 1, 9);
					itemsToRender[1] = new ItemStack(TF2weapons.itemTF2, 1, 9);
				} else if (recipe instanceof OpenCrateRecipe) {
					itemsToRender[0] = new ItemStack(TF2weapons.itemTF2, 1, 7);
					itemsToRender[1] = ItemFromData.getNewStack("crate1");
				}
				else{
					List<Ingredient> input = recipe.getIngredients();
					
					for (int i = 0; i < input.size(); i++) {
						int space = 0;
						if(recipe instanceof ShapedRecipes)
							space = (3-((ShapedRecipes)recipe).recipeWidth)*(i/((ShapedRecipes)recipe).recipeWidth);
						else if(recipe instanceof ShapedOreRecipe)
							space = (3-((ShapedOreRecipe)recipe).getWidth())*(i/((ShapedOreRecipe)recipe).getWidth());
							
						if(input.get(i).getMatchingStacks().length>0) {
							itemsToRender[i + space] = input.get(i).getMatchingStacks()[0];
							if(itemsToRender[i + space].getMetadata()==32767)
								itemsToRender[i + space].setItemDamage(0);
						}
					}
				}
			}
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 8, 5, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 3,
				4210752);
		for (int i = 0; i < 12; i++)
			if (this.buttonsItem[i].stackToDraw != null && this.buttonsItem[i].isMouseOver())
				this.renderToolTip(this.buttonsItem[i].stackToDraw, mouseX - this.guiLeft,
						mouseY - this.guiTop);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		// this.drawTab(0);
		// this.drawTab(1);

		x = this.guiLeft + 62;
		y = this.guiTop + 15;
		int k = y + 72;

		this.drawTexturedModalRect(x, y + (int) ((k - y - 17) * this.scroll), 232, 0, 12, 15);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();
		this.itemRender.zLevel = 0;
		for (x = 0; x < 3; x++)
			for (y = 0; y < 3; y++) {
				ItemStack stack = this.itemsToRender[x + y * 3];
				if (stack != null && !stack.isEmpty())
					this.itemRender.renderItemIntoGUI(stack, this.guiLeft + 86 + 18 * x, this.guiTop + 23 + 18 * y);
			}
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		this.zLevel = 120;
		this.drawTexturedModalRect(85 + this.guiLeft, 22 + this.guiTop, 85, 22, 54, 54);
		this.zLevel = 0;
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