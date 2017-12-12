package rafradek.TF2weapons.building;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemWrench;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntityDispenser extends EntityBuilding {

	public int reloadTimer;
	public int giveAmmoTimer;
	public List<EntityLivingBase> dispenserTarget;
	private static final DataParameter<Integer> METAL = EntityDataManager.createKey(EntityDispenser.class,
			DataSerializers.VARINT);

	public EntityDispenser(World worldIn) {
		super(worldIn);
		this.setSize(1f, 1.1f);
		this.dispenserTarget = new ArrayList<>();
	}

	public EntityDispenser(World worldIn, EntityLivingBase living) {
		super(worldIn, living);
		this.setSize(1f, 1.1f);
		this.dispenserTarget = new ArrayList<>();
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.isDisabled()) {
			this.dispenserTarget.clear();
			return;
		}

		List<EntityLivingBase> targetList = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
				this.getEntityBoundingBox().grow(2, 1.5d, 2), new Predicate<EntityLivingBase>() {

					@Override
					public boolean apply(EntityLivingBase input) {

						return !(input instanceof EntityBuilding) && EntityDispenser.this != input
								&& ((getOwner() != null && WeaponsCapability.get(getOwner()).dispenserPlayer && input instanceof EntityPlayer
										&& input.getTeam() == null)
										|| TF2Util.isOnSameTeam(EntityDispenser.this, input));
					}

				});
		if (!this.world.isRemote) {
			this.reloadTimer--;
			if (this.reloadTimer <= 0 && this.getMetal() < 400) {
				int metalAmount = TF2ConfigVars.fastMetalProduction ? 30 : 21;
				this.setMetal(Math.min(400, this.getMetal() + metalAmount + this.getLevel() * (metalAmount / 3)));
				// System.out.println("MetalGenerated "+this.getMetal());
				this.playSound(TF2Sounds.MOB_DISPENSER_GENERATE_METAL, 1.55f, 1f);
				this.reloadTimer = TF2ConfigVars.fastMetalProduction ? 100 : 200;
			}
			this.giveAmmoTimer--;

			for (EntityLivingBase living : targetList) {
				int level = this.getLevel();
				living.heal(0.025f + 0.025f * level);
				if (this.giveAmmoTimer == 0) {
					if (living instanceof EntityEngineer || living instanceof EntityPlayer) {
						int metal = living.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal();
						int metalUse = Math.min(30 + this.getLevel() * 10,
								Math.min(200 - metal, this.getMetal()));
						this.setMetal(this.getMetal() - metalUse);
						living.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(metal + metalUse);
					}
					if (living instanceof EntityTF2Character) {
						((EntityTF2Character)living).restoreAmmo(0.1f+this.getLevel()*0.1f);
					
					}
					ItemStack heldItem = living.getHeldItem(EnumHand.MAIN_HAND);
					if (!heldItem.isEmpty()
							&& heldItem.getItem().isRepairable()
							&& heldItem.getItemDamage() != 0 && !TF2ConfigVars.repairBlacklist.contains(heldItem.getItem().getRegistryName())) {

						float repairMult = TF2ConfigVars.dispenserRepair;
						NBTTagList list = living.getHeldItem(EnumHand.MAIN_HAND).getEnchantmentTagList();
						if (list != null) {
							for (int i = 0; i < list.tagCount(); i++)
								repairMult -= list.getCompoundTagAt(i).getShort("lvl") * TF2ConfigVars.dispenserRepair / 15f;
							if (repairMult <= TF2ConfigVars.dispenserRepair / 3f)
								repairMult = 1f;
						}
						int metalUse = Math.min(15 + this.getLevel() * 10,
								Math.min(
										(int) (living.getHeldItem(EnumHand.MAIN_HAND).getItemDamage() / repairMult) + 1,
										this.getMetal()));
						this.setMetal(this.getMetal() - metalUse);
						living.getHeldItem(EnumHand.MAIN_HAND).setItemDamage(
								living.getHeldItem(EnumHand.MAIN_HAND).getItemDamage() - (int) (metalUse * repairMult));

						if (living instanceof EntityPlayerMP)
							((EntityPlayerMP) living).updateHeldItem();
					}
				}
				Tuple<Integer, ItemStack> cloak = ItemCloak.searchForWatches(living);
				if (!cloak.getSecond().isEmpty() || (!living.getHeldItem(EnumHand.MAIN_HAND).isEmpty()
						&& living.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemCloak)) {
					if (cloak.getSecond().isEmpty())
						cloak=new Tuple<>(-1,living.getHeldItemMainhand());

					if(TF2Attribute.getModifier("No External Cloak", cloak.getSecond(), 0, living) == 0) {
						cloak.getSecond().setItemDamage(Math.max(cloak.getSecond().getItemDamage() - (2 + this.getLevel()), 0));
						if (living instanceof EntityPlayerMP)
							((EntityPlayerMP) living).connection.sendPacket(
									new SPacketSetSlot(-2, cloak.getFirst(), cloak.getSecond()));
					}
				}
				if (this.dispenserTarget != null && !this.dispenserTarget.contains(living))
					this.playSound(TF2Sounds.MOB_DISPENSER_HEAL, 0.75f, 1f);
			}
			if (this.giveAmmoTimer <= 0)
				this.giveAmmoTimer = 20;
		}
		this.dispenserTarget = targetList;
	}

	@Override
	public SoundEvent getSoundNameForState(int state) {
		switch (state) {
		case 0:
			return TF2Sounds.MOB_DISPENSER_IDLE;
		default:
			return super.getSoundNameForState(state);
		}
	}

	public static boolean isNearDispenser(World world, final EntityLivingBase living) {
		List<EntityDispenser> targetList = world.getEntitiesWithinAABB(EntityDispenser.class,
				living.getEntityBoundingBox().grow(2.5D, 2D, 2.5D), new Predicate<EntityDispenser>() {

					@Override
					public boolean apply(EntityDispenser input) {

						return !input.isDisabled() && input.dispenserTarget != null
								&& input.dispenserTarget.contains(living);
					}

				});
		return !targetList.isEmpty();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(METAL, 0);
	}

	public int getMetal() {
		return this.dataManager.get(METAL);
	}

	public void setMetal(int amount) {
		this.dataManager.set(METAL, amount);
	}

	@Override
	public void upgrade() {
		super.upgrade();
		this.setMetal(this.getMetal() + 25);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_DISPENSER_DEATH;
	}

	public int getIronDrop() {
		return 0 + this.getLevel();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setShort("Metal", (short) this.getMetal());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);

		this.setMetal(par1NBTTagCompound.getShort("Metal"));
	}
	
	@SideOnly(Side.CLIENT)
	public void renderGUI(BufferBuilder renderer, Tessellator tessellator, EntityPlayer player, int width, int height, GuiIngame gui) {
        // GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
        // gui.drawTexturedModalRect(event.getResolution().getScaledWidth()/2-64,
        // event.getResolution().getScaledHeight()/2+35, 0, 0, 128, 40);
		ClientProxy.setColor(TF2Util.getTeamColor(player), 0.7f, 0, 0.25f, 0.8f);
        gui.drawTexturedModalRect(20, 2, 0, 112,124, 44);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
        gui.drawTexturedModalRect(0, 0, 0, 0, 144, 48);
        /*renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(event.getResolution().getScaledWidth() / 2 - 72, event.getResolution().getScaledHeight() / 2 + 76, 0.0D).tex(0.0D, 0.1875D).endVertex();
        renderer.pos(event.getResolution().getScaledWidth() / 2 + 72, event.getResolution().getScaledHeight() / 2 + 76, 0.0D).tex(0.5625D, 0.1875D).endVertex();
        renderer.pos(event.getResolution().getScaledWidth() / 2 + 72, event.getResolution().getScaledHeight() / 2 + 28, 0.0D).tex(0.5625D, 0D).endVertex();
        renderer.pos(event.getResolution().getScaledWidth() / 2 - 72, event.getResolution().getScaledHeight() / 2 + 28, 0.0D).tex(0.0D, 0D).endVertex();
        tessellator.draw();*/


        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(19, 48, 0.0D).tex(0.75D, 0.75D).endVertex();
        renderer.pos(65, 48, 0.0D).tex(0.9375D, 0.75D).endVertex();
        renderer.pos(65, 0, 0.0D).tex(0.9375D, 0.5625D).endVertex();
        renderer.pos(19, 0, 0.0D).tex(0.75D, 0.5625D).endVertex();
        tessellator.draw();

        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(67, 22, 0.0D).tex(0.9375D, 0.1875D).endVertex();
        renderer.pos(83, 22, 0.0D).tex(1D, 0.1875D).endVertex();
        renderer.pos(83, 6, 0.0D).tex(1D, 0.125D).endVertex();
        renderer.pos(67, 6, 0.0D).tex(0.9375D, 0.125D).endVertex();
        tessellator.draw();

        double imagePos = this.getLevel() == 1 ? 0.3125D : this.getLevel() == 2 ? 0.375D : 0.4375D;
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(50, 18, 0.0D).tex(0.9375D, 0.0625D + imagePos).endVertex();
        renderer.pos(66, 18, 0.0D).tex(1D, 0.0625D + imagePos).endVertex();
        renderer.pos(66, 2, 0.0D).tex(1D, imagePos).endVertex();
        renderer.pos(50, 2, 0.0D).tex(0.9375D, imagePos).endVertex();
        tessellator.draw();

        if (this.getLevel() < 3) {
            renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            renderer.pos(67, 42, 0.0D).tex(0.9375D, 0.125D).endVertex();
            renderer.pos(83, 42, 0.0D).tex(1D, 0.125D).endVertex();
            renderer.pos(83, 26, 0.0D).tex(1D, 0.0625).endVertex();
            renderer.pos(67, 26, 0.0D).tex(0.9375D, 0.0625).endVertex();
            tessellator.draw();
        }
        float health = this.getHealth() / this.getMaxHealth();
        if (health > 0.33f) {
            GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
        } else {
            GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        for (int i = 0; i < health * 8; i++) {

            renderer.begin(7, DefaultVertexFormats.POSITION);
            renderer.pos(19, 39 - i * 5, 0.0D).endVertex();
            renderer.pos(9, 39 - i * 5, 0.0D).endVertex();
            renderer.pos(9, 43 - i * 5, 0.0D).endVertex();
            renderer.pos(19, 43 - i * 5, 0.0D).endVertex();
            tessellator.draw();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
        renderer.begin(7, DefaultVertexFormats.POSITION);
        renderer.pos(85, 21, 0.0D).endVertex();
        renderer.pos(140, 21, 0.0D).endVertex();
        renderer.pos(140, 7, 0.0D).endVertex();
        renderer.pos(85, 7, 0.0D).endVertex();
        tessellator.draw();

        if (this.getLevel() < 3) {
            renderer.begin(7, DefaultVertexFormats.POSITION);
            renderer.pos(85, 41, 0.0D).endVertex();
            renderer.pos(140, 41, 0.0D).endVertex();
            renderer.pos(140, 27, 0.0D).endVertex();
            renderer.pos(85, 27, 0.0D).endVertex();
            tessellator.draw();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
        renderer.begin(7, DefaultVertexFormats.POSITION);
        renderer.pos(85, 21, 0.0D).endVertex();
        renderer.pos(85 + this.getMetal() * 0.1375D, 21, 0.0D).endVertex();
        renderer.pos(85 + this.getMetal() * 0.1375D, 7, 0.0D).endVertex();
        renderer.pos(85, 7, 0.0D).endVertex();
        tessellator.draw();

        if (this.getLevel() < 3) {
            renderer.begin(7, DefaultVertexFormats.POSITION);
            renderer.pos(85, 41, 0.0D).endVertex();
            renderer.pos(85 + this.getProgress() * 0.275D, 41, 0.0D)
                    .endVertex();
            renderer.pos(85 + this.getProgress() * 0.275D, 27, 0.0D)
                    .endVertex();
            renderer.pos(85, 27, 0.0D).endVertex();
            tessellator.draw();
        }
	}
}
