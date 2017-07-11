package rafradek.TF2weapons.building;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

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
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemWrench;

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
				this.getEntityBoundingBox().expand(2, 1.5d, 2), new Predicate<EntityLivingBase>() {

					@Override
					public boolean apply(EntityLivingBase input) {

						return !(input instanceof EntityBuilding) && EntityDispenser.this != input
								&& ((TF2weapons.dispenserHeal && input instanceof EntityPlayer && getTeam() == null
										&& input.getTeam() == null)
										|| TF2weapons.isOnSameTeam(EntityDispenser.this, input));
					}

				});
		if (!this.world.isRemote) {
			this.reloadTimer--;
			if (this.reloadTimer <= 0 && this.getMetal() < 400) {
				int metalAmount = TF2weapons.fastMetalProduction ? 30 : 21;
				this.setMetal(Math.min(400, this.getMetal() + metalAmount + this.getLevel() * (metalAmount / 3)));
				// System.out.println("MetalGenerated "+this.getMetal());
				this.playSound(TF2Sounds.MOB_DISPENSER_GENERATE_METAL, 1.55f, 1f);
				this.reloadTimer = TF2weapons.fastMetalProduction ? 100 : 200;
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
					if (!living.getHeldItem(EnumHand.MAIN_HAND).isEmpty()
							&& living.getHeldItem(EnumHand.MAIN_HAND).getItem().isRepairable()
							&& living.getHeldItem(EnumHand.MAIN_HAND).getItemDamage() != 0) {

						float repairMult = 3f;
						NBTTagList list = living.getHeldItem(EnumHand.MAIN_HAND).getEnchantmentTagList();
						if (list != null) {
							for (int i = 0; i < list.tagCount(); i++)
								repairMult -= list.getCompoundTagAt(i).getShort("lvl") * 0.2f;
							if (repairMult <= 1f)
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

					cloak.getSecond().setItemDamage(Math.max(cloak.getSecond().getItemDamage() - (2 + this.getLevel()), 0));
					if (living instanceof EntityPlayerMP)
						((EntityPlayerMP) living).connection.sendPacket(
								new SPacketSetSlot(-2, cloak.getFirst(), cloak.getSecond()));
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
				living.getEntityBoundingBox().expand(2.5D, 2D, 2.5D), new Predicate<EntityDispenser>() {

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
	protected SoundEvent getHurtSound() {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_DISPENSER_DEATH;
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
}
