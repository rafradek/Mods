package rafradek.TF2weapons.entity.ai;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.building.EntityDispenser;
import rafradek.TF2weapons.entity.building.EntitySentry;
import rafradek.TF2weapons.entity.mercenary.EntityEngineer;
import rafradek.TF2weapons.item.ItemWrench;
import rafradek.TF2weapons.util.TF2Util;

public class EntityAISetup extends EntityAIBase {

	public EntityEngineer engineer;
	public int buildType;
	public boolean found;
	public Vec3d target;

	public EntityAISetup(EntityEngineer engineer) {
		this.engineer = engineer;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {

		if (this.engineer.isInWater() || this.engineer.getHeldItem(EnumHand.MAIN_HAND).isEmpty()
				|| this.engineer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemWrench)
			return false;
		
		boolean dispensernear = TF2Util.findAmmoSource(engineer, 16, false) != null;

		int sentryCost = EntityBuilding.getCost(0, this.engineer.loadout.getStackInSlot(2));
		int dispenserCost = EntityBuilding.getCost(1, this.engineer.loadout.getStackInSlot(2));
		buildType = (this.engineer.getWepCapability().getMetal() >= sentryCost && (this.engineer.sentry == null || this.engineer.sentry.isDead) 
				&& (dispensernear || this.engineer.getWepCapability().getMetal() >= sentryCost + dispenserCost)) ? 1
				: (this.engineer.getWepCapability().getMetal() >= dispenserCost && (this.engineer.dispenser == null || this.engineer.dispenser.isDead))
						? 2 : 0;
		// System.out.println("Promote: "+buildType);
		if (buildType > 0) {
			this.engineer.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
					new ItemStack(TF2weapons.itemBuildingBox, 1, 16 + buildType * 2 + this.engineer.getEntTeam()));
			this.engineer.getHeldItem(EnumHand.MAIN_HAND).setTagCompound(new NBTTagCompound());
		}
		return buildType > 0;
	}

	@Override
	public void resetTask() {
		this.engineer.getNavigator().clearPathEntity();
		this.engineer.switchSlot(0);
	}

	@Override
	public void updateTask() {
		if (this.buildType == 1) {
			if (this.target != null
					&& this.engineer.getDistanceSq(this.target.x, this.target.y, this.target.z) < 2) {
				this.engineer.sentry = (EntitySentry) this.spawn();
				this.engineer.getWepCapability().setMetal(this.engineer.getWepCapability().getMetal() - EntityBuilding.getCost(0, this.engineer.loadout.getStackInSlot(2)));
				return;
			}
			if (this.engineer.getNavigator().noPath()) {

				Vec3d Vec3d = RandomPositionGenerator.findRandomTarget(this.engineer, 3, 2);
				if (Vec3d != null) {
					AxisAlignedBB box = new AxisAlignedBB(Vec3d.x - 0.5, Vec3d.y, Vec3d.z - 0.5,
							Vec3d.x + 0.5, Vec3d.y + 1, Vec3d.z + 0.5);
					List<AxisAlignedBB> list = this.engineer.world.getCollisionBoxes(this.engineer, box);

					if (list.isEmpty() && !this.engineer.world.isMaterialInBB(box, Material.WATER)) {
						this.engineer.getNavigator().tryMoveToXYZ(Vec3d.x, Vec3d.y, Vec3d.z, 1);
						this.target = Vec3d;
					}
				}
				/*
				 * for(AxisAlignedBB entry:list){ System.out.println(entry); }
				 */

			}
		} else if (this.buildType == 2 && this.engineer.getNavigator().noPath()) {
			if (this.target != null
					&& this.engineer.getDistanceSq(this.target.x, this.target.y, this.target.z) < 2) {
				this.engineer.dispenser = (EntityDispenser) this.spawn();
				this.engineer.getWepCapability().setMetal(this.engineer.getWepCapability().getMetal() - EntityBuilding.getCost(1, this.engineer.loadout.getStackInSlot(2)));
				return;
			}
			if (this.engineer.getNavigator().noPath()) {

				/*Vec3d Vec3d = RandomPositionGenerator
						.findRandomTarget((this.engineer.sentry != null && !this.engineer.sentry.isDead)
								? this.engineer.sentry : this.engineer, 2, 1);*/
				Vec3d vec= RandomPositionGenerator
						.findRandomTarget(this.engineer, 2, 1);
				if (vec != null) {
					AxisAlignedBB box = new AxisAlignedBB(vec.x - 0.5, vec.y, vec.z - 0.5,
							vec.x + 0.5, vec.y + 1, vec.z + 0.5);
					List<AxisAlignedBB> list = this.engineer.world.getCollisionBoxes(this.engineer, box);
					/*
					 * for(AxisAlignedBB entry:list){ System.out.println(entry);
					 * }
					 */
					if (list.isEmpty() && !this.engineer.world.isMaterialInBB(box, Material.WATER)) {
						this.engineer.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, 1);
						this.target = vec;
					}
				}
			}
		}
	}

	public EntityBuilding spawn() {
		EntityBuilding building;
		if (buildType == 1)
			building = new EntitySentry(this.engineer.world);
		else
			building = new EntityDispenser(this.engineer.world);
		IBlockState blockTarget = this.engineer.world.getBlockState(new BlockPos(target));
		if (!blockTarget.getBlock().isPassable(this.engineer.world, new BlockPos(target)))
			building.setPosition(target.x, target.y + 1.3, target.z);
		else
			building.setPosition(target.x, target.y + 0.3, target.z);
		building.setEntTeam(this.engineer.getEntTeam());
		building.setOwner(this.engineer);
		if (building instanceof EntitySentry && TF2Attribute.getModifier("Weapon Mode", this.engineer.loadout.getStackInSlot(2), 0, this.engineer) == 2)
			((EntitySentry)building).setMini(true);
		this.engineer.world.spawnEntity(building);
		this.target = null;
		this.buildType = 0;
		this.resetTask();
		return building;
	}
}
