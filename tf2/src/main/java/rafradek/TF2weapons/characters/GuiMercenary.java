package rafradek.TF2weapons.characters;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityTF2Character.Order;

public class GuiMercenary extends GuiMerchant {

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/gui/container/mercenary.png");
	
	public EntityTF2Character mercenary;
	public GuiButton hireBtn;
	public GuiButton shareBtn;
	public GuiButton orderBtn;
	public InventoryPlayer inv;
	public GuiMercenary(InventoryPlayer inv, EntityTF2Character mercenary, World worldIn) {
		super(inv, mercenary, worldIn);
		this.mercenary = mercenary;
		this.inv=inv;
		this.inventorySlots=new ContainerMercenary(Minecraft.getMinecraft().player, mercenary, worldIn);
		this.xSize += 54;
		//merInv=mercenary.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		// TODO Auto-generated constructor stub
	}
	
	public void initGui() {
		super.initGui();
		this.buttonList.add(hireBtn = new GuiButton(60, this.guiLeft+(this.xSize/2)-75, this.guiTop - 25, 150, 20, "Hire mercenary (1 Australium ingot)"));
		this.buttonList.add(shareBtn = new GuiButton(62, this.guiLeft+(this.xSize/2)-75, this.guiTop + this.ySize + 5, 150, 20, "Share loot (1 Australium ingot)"));
		this.buttonList.add(orderBtn = new GuiButton(61, this.guiLeft+179, this.guiTop+ 123, 48, 20, "Order"));
		this.updateButtons();
	}
	
	public void updateButtons() {
		//hireBtn.visible = mercenary.getOwner() == null;
		if(mercenary.getOwnerId() == null) {
			hireBtn.enabled=inv.hasItemStack(new ItemStack(TF2weapons.itemTF2,1,2));
			hireBtn.displayString="Hire this mercenary (1 Australium ingot)";
			orderBtn.enabled=false;
			shareBtn.visible=false;
		}
		else if(mercenary.getOwnerId().equals(mc.player.getUniqueID())) {
			hireBtn.enabled=true;
			hireBtn.displayString="Fire this mercenary";
			orderBtn.enabled=true;
			orderBtn.displayString = this.mercenary.getOrder().toString();
			shareBtn.visible=true;
			shareBtn.enabled= !this.mercenary.isSharing() && inv.hasItemStack(new ItemStack(TF2weapons.itemTF2,1,2));
		}
		else{
			hireBtn.enabled=false;
			hireBtn.displayString=mercenary.ownerName != null ? "Hired mercenary" : "Hired by: "+mercenary.ownerName;
			orderBtn.enabled=false;
			shareBtn.visible=false;
		}
		
	}

	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button.id == 61) {
			if(this.mercenary.getOrder() == Order.FOLLOW)
				this.mercenary.setOrder(Order.HOLD);
			else
				this.mercenary.setOrder(Order.FOLLOW);
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, -100 + this.mercenary.getOrder().ordinal());
		}
		else if(button.id == 60) {
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, -128);
			if(this.mercenary.getOwnerId() == null)
				this.mercenary.setOwner(mc.player);
			else
				this.mercenary.setOwner(null);
		}
		else if(button.id == 62) {
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, -127);
			this.mercenary.setSharing(true);
		}
		this.updateButtons();
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(this.hireBtn.isMouseOver())
			this.drawHoveringText("Lost australium can be recovered at Mann Co store", mouseX, mouseY);
		else if(this.shareBtn.isMouseOver())
			this.drawHoveringText("Allows the owner to collect loot from enemies", mouseX, mouseY);
    }
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURES);
        int i = this.guiLeft + this.xSize - 54;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 176, 0, 54, 146);
        this.fontRenderer.drawString("Refill", i+7, j+80, 4210752);
        this.fontRenderer.drawString(Integer.toString(((ContainerMercenary)this.inventorySlots).primaryAmmo), i+10, j+113, 4210752);
        this.fontRenderer.drawString(Integer.toString(((ContainerMercenary)this.inventorySlots).secondaryAmmo), i+33, j+113, 4210752);
        /*MerchantRecipeList merchantrecipelist = this.m.getRecipes(this.mc.player);

        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
        {
            int k = this.selectedMerchantRecipe;

            if (k < 0 || k >= merchantrecipelist.size())
            {
                return;
            }

            MerchantRecipe merchantrecipe = (MerchantRecipe)merchantrecipelist.get(k);

            if (merchantrecipe.isRecipeDisabled())
            {
                this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableLighting();
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }*/
    }
	
}
