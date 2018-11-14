package rafradek.TF2weapons.world.gen.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.block.BlockUpgradeStation;
import rafradek.TF2weapons.entity.mercenary.EntitySaxtonHale;

public class MannCoBuilding extends Village {

	private boolean haleSpawned;

	public MannCoBuilding() {

	}

	public MannCoBuilding(Start startPiece, int p5, Random random, StructureBoundingBox structureboundingbox,
			EnumFacing facing) {
		super(startPiece, p5);
		this.setCoordBaseMode(facing);
		this.boundingBox = structureboundingbox;
	}

	@Override
	protected void writeStructureToNBT(NBTTagCompound tagCompound) {
		super.writeStructureToNBT(tagCompound);
		tagCompound.setBoolean("HS", this.haleSpawned);

	}

	/**
	 * (abstract) Helper method to read subclass data from NBT
	 */
	protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
		super.readStructureFromNBT(tagCompound, p_143011_2_);
		this.haleSpawned = tagCompound.getBoolean("HS");
	}

	/**
	 * second Part of Structure generating, this for example places Spiderwebs,
	 * Mob Spawners, it closes Mineshafts at the end, it adds Fences...
	 */
	@Override
	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
		if (this.averageGroundLvl < 0) {
			this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

			if (this.averageGroundLvl < 0)
				return true;

			this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 15, 0);
		}

		IBlockState stone = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.ANDESITE_SMOOTH);
		IBlockState iblockstate1 = this.getBiomeSpecificBlockState(
				Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		IBlockState iblockstate2 = this.getBiomeSpecificBlockState(
				Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		IBlockState wood = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
		IBlockState upgradeStation = this.getBiomeSpecificBlockState(
				TF2weapons.blockUpgradeStation.getDefaultState().withProperty(BlockUpgradeStation.HOLDER, false));
		IBlockState upgradeStationHolder = this.getBiomeSpecificBlockState(
				TF2weapons.blockUpgradeStation.getDefaultState().withProperty(BlockUpgradeStation.HOLDER, true));
		IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
		IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 13, 16, 10, Blocks.AIR.getDefaultState(),
				Blocks.AIR.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 2, 13, 15, 10, stone, Blocks.AIR.getDefaultState(),
				false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 3, 12, 0, 9, wood, wood, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 3, 12, 4, 9, stone, stone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 3, 0, 3, 9, Blocks.GLASS_PANE.getDefaultState(),
				Blocks.GLASS_PANE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 2, 3, 13, 3, 9, Blocks.GLASS_PANE.getDefaultState(),
				Blocks.GLASS_PANE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 2, 12, 3, 2, Blocks.GLASS_PANE.getDefaultState(),
				Blocks.GLASS_PANE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 10, 12, 3, 10, Blocks.GLASS_PANE.getDefaultState(),
				Blocks.GLASS_PANE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 2, 8, 3, 2, wood, wood, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 2, 7, 2, 2, Blocks.AIR.getDefaultState(),
				Blocks.AIR.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 1, 7, 0, 1, iblockstate2, iblockstate2, false);
		this.setBlockState(worldIn, Blocks.GLOWSTONE.getDefaultState(), 7, 4, 6, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLOWSTONE.getDefaultState(), 4, 4, 6, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLOWSTONE.getDefaultState(), 10, 4, 6, structureBoundingBoxIn);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 1, 2, 7,
				upgradeStation.withProperty(BlockUpgradeStation.FACING, EnumFacing.EAST),
				upgradeStation.withProperty(BlockUpgradeStation.FACING, EnumFacing.WEST), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 5, 12, 2, 7,
				upgradeStation.withProperty(BlockUpgradeStation.FACING, EnumFacing.WEST),
				upgradeStation.withProperty(BlockUpgradeStation.FACING, EnumFacing.EAST), false);
		this.setBlockState(worldIn, upgradeStationHolder.withProperty(BlockUpgradeStation.FACING, EnumFacing.EAST), 1,
				1, 6, structureBoundingBoxIn);
		this.setBlockState(worldIn, upgradeStationHolder.withProperty(BlockUpgradeStation.FACING, EnumFacing.WEST), 12,
				1, 6, structureBoundingBoxIn);

		/*
		 * this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 9, 4,
		 * 6, iblockstate, iblockstate, false); this.fillWithBlocks(worldIn,
		 * structureBoundingBoxIn, 0, 5, 0, 9, 5, 6,
		 * Blocks.STONE_SLAB.getDefaultState(),
		 * Blocks.STONE_SLAB.getDefaultState(), false);
		 * this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 8, 5,
		 * 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(),
		 * false); this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0,
		 * 2, 3, 0, iblockstate3, iblockstate3, false);
		 * this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4,
		 * 0, iblockstate5, iblockstate5, false); this.fillWithBlocks(worldIn,
		 * structureBoundingBoxIn, 3, 1, 0, 3, 4, 0, iblockstate5, iblockstate5,
		 * false); this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6,
		 * 0, 4, 6, iblockstate5, iblockstate5, false);
		 * this.setBlockState(worldIn, iblockstate3, 3, 3, 1,
		 * structureBoundingBoxIn); this.fillWithBlocks(worldIn,
		 * structureBoundingBoxIn, 3, 1, 2, 3, 3, 2, iblockstate3, iblockstate3,
		 * false); this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3,
		 * 5, 3, 3, iblockstate3, iblockstate3, false);
		 * this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3,
		 * 5, iblockstate3, iblockstate3, false); this.fillWithBlocks(worldIn,
		 * structureBoundingBoxIn, 1, 1, 6, 5, 3, 6, iblockstate3, iblockstate3,
		 * false); this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0,
		 * 5, 3, 0, iblockstate6, iblockstate6, false);
		 * this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 9, 3,
		 * 0, iblockstate6, iblockstate6, false); this.fillWithBlocks(worldIn,
		 * structureBoundingBoxIn, 6, 1, 4, 9, 4, 6, iblockstate, iblockstate,
		 * false); this.setBlockState(worldIn,
		 * Blocks.IRON_BARS.getDefaultState(), 9, 2, 5, structureBoundingBoxIn);
		 * this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), 9, 2,
		 * 4, structureBoundingBoxIn); this.fillWithBlocks(worldIn,
		 * structureBoundingBoxIn, 7, 2, 4, 8, 2, 5,
		 * Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		 * this.setBlockState(worldIn, iblockstate, 6, 1, 3,
		 * structureBoundingBoxIn); this.setBlockState(worldIn,
		 * Blocks.FURNACE.getDefaultState(), 6, 2, 3, structureBoundingBoxIn);
		 * this.setBlockState(worldIn, Blocks.FURNACE.getDefaultState(), 6, 3,
		 * 3, structureBoundingBoxIn); this.setBlockState(worldIn,
		 * Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 8, 1, 1,
		 * structureBoundingBoxIn); this.setBlockState(worldIn,
		 * Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2,
		 * structureBoundingBoxIn); this.setBlockState(worldIn,
		 * Blocks.GLASS_PANE.getDefaultState(), 0, 2, 4,
		 * structureBoundingBoxIn); this.setBlockState(worldIn,
		 * Blocks.GLASS_PANE.getDefaultState(), 2, 2, 6,
		 * structureBoundingBoxIn); this.setBlockState(worldIn,
		 * Blocks.GLASS_PANE.getDefaultState(), 4, 2, 6,
		 * structureBoundingBoxIn); this.setBlockState(worldIn, iblockstate6, 2,
		 * 1, 4, structureBoundingBoxIn); this.setBlockState(worldIn,
		 * Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 2, 2, 4,
		 * structureBoundingBoxIn); this.setBlockState(worldIn, iblockstate3, 1,
		 * 1, 5, structureBoundingBoxIn); this.setBlockState(worldIn,
		 * iblockstate1, 2, 1, 5, structureBoundingBoxIn);
		 * this.setBlockState(worldIn, iblockstate2, 1, 1, 4,
		 * structureBoundingBoxIn);
		 */

		/*
		 * if (!this.hasMadeChest && structureBoundingBoxIn.isVecInside(new
		 * BlockPos(this.getXWithOffset(5, 5), this.getYWithOffset(1),
		 * this.getZWithOffset(5, 5)))) { this.hasMadeChest = true;
		 * this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 5, 1,
		 * 5, LootTableList.CHESTS_VILLAGE_BLACKSMITH); }
		 */
		for (int k = 0; k < 10; ++k)
			for (int j = 0; j < 13; ++j) {
				this.clearCurrentPositionBlocksUpwards(worldIn, j, 17, k, structureBoundingBoxIn);
				this.replaceAirAndLiquidDownwards(worldIn, stone, j, -1, k, structureBoundingBoxIn);
			}

		if (!this.haleSpawned) {
			EntitySaxtonHale shopkeeper = new EntitySaxtonHale(worldIn);
			shopkeeper.setLocationAndAngles(this.getXWithOffset(7, 5), this.getYWithOffset(1),
					this.getZWithOffset(7, 5), 0.0F, 0.0F);
			shopkeeper.enablePersistence();
			this.haleSpawned = true;
			worldIn.spawnEntity(shopkeeper);
		}
		return true;
	}

	@Override
	protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
		return 3;
	}

	public static class CreationHandler implements IVillageCreationHandler {
		@Override
		public PieceWeight getVillagePieceWeight(Random random, int i) {
			return new StructureVillagePieces.PieceWeight(MannCoBuilding.class, 15, 1);
		}

		@Override
		public Class<?> getComponentClass() {
			// TODO Auto-generated method stub
			return MannCoBuilding.class;
		}

		@Override
		public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces,
				Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0,
					0, 0, 14, 16, 11, facing);
			return canVillageGoDeeper(structureboundingbox)
					&& StructureComponent.findIntersecting(pieces, structureboundingbox) == null
							? new MannCoBuilding(startPiece, p5, random, structureboundingbox, facing) : null;
		}

	}
}
