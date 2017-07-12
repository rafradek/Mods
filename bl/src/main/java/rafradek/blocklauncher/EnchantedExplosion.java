package rafradek.blocklauncher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EnchantedExplosion extends Explosion {

	public boolean harmless;
	public float dropChance;
	/** whether or not the explosion sets fire to blocks around it */
	public boolean isFlaming;
	/** whether or not this explosion spawns smoke particles */
	public boolean isSmoking = true;
	private int field_77289_h = 16;
	private Random explosionRNG = new Random();
	public World world;
	/** A list of ChunkPositions of blocks affected by this explosion */
	public List affectedBlockPositions = new ArrayList();
	private double explosionY;
	private double explosionX;
	private double explosionZ;
	private float explosionSize;
	private Entity exploder;
	private Map field_77288_k;
	private Vec3d position;
	private static final String __OBFID = "CL_00000134";

	public EnchantedExplosion(World world, Entity exploder, double x, double y, double z, float size, float dropChance,
			boolean harmless) {
		super(world, exploder, x, y, z, size, false, true);
		this.explosionRNG = new Random();
		this.affectedBlockPositions = Lists.newArrayList();
		this.field_77288_k = Maps.newHashMap();
		this.exploder = exploder;
		this.explosionSize = size;
		this.explosionX = x;
		this.explosionY = y;
		this.explosionZ = z;
		this.position = new Vec3d(explosionX, explosionY, explosionZ);
		this.world = world;
		this.harmless = harmless;
		this.dropChance = dropChance;
	}

	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doExplosionA() {
		HashSet hashset = Sets.newHashSet();
		boolean flag = true;
		int j;
		int k;
		if (!this.harmless)
			for (int i = 0; i < 16; ++i)
				for (j = 0; j < 16; ++j)
					for (k = 0; k < 16; ++k)
						if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
							double d0 = i / 15.0F * 2.0F - 1.0F;
							double d1 = j / 15.0F * 2.0F - 1.0F;
							double d2 = k / 15.0F * 2.0F - 1.0F;
							double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
							d0 /= d3;
							d1 /= d3;
							d2 /= d3;
							float f = this.explosionSize * (0.7F + this.world.rand.nextFloat() * 0.6F);
							double d4 = this.explosionX;
							double d6 = this.explosionY;
							double d8 = this.explosionZ;

							for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
								BlockPos blockpos = new BlockPos(d4, d6, d8);
								IBlockState iblockstate = this.world.getBlockState(blockpos);

								if (iblockstate.getMaterial() != Material.AIR) {
									float f2 = this.exploder != null
											? this.exploder.getExplosionResistance(this, this.world, blockpos,
													iblockstate)
											: iblockstate.getBlock().getExplosionResistance(world, blockpos,
													(Entity) null, this);
									f -= (f2 + 0.3F) * 0.3F;
								}

								if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this,
										this.world, blockpos, iblockstate, f)))
									hashset.add(blockpos);

								d4 += d0 * 0.30000001192092896D;
								d6 += d1 * 0.30000001192092896D;
								d8 += d2 * 0.30000001192092896D;
							}
						}
		this.affectedBlockPositions.addAll(hashset);
		float f3 = this.explosionSize * 2.0F;
		j = MathHelper.floor(this.explosionX - f3 - 1.0D);
		k = MathHelper.floor(this.explosionX + f3 + 1.0D);
		int j1 = MathHelper.floor(this.explosionY - f3 - 1.0D);
		int l = MathHelper.floor(this.explosionY + f3 + 1.0D);
		int k1 = MathHelper.floor(this.explosionZ - f3 - 1.0D);
		int i1 = MathHelper.floor(this.explosionZ + f3 + 1.0D);
		List list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder,
				new AxisAlignedBB(j, j1, k1, k, l, i1));
		net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, list, f3);
		Vec3d Vec3d = new Vec3d(this.explosionX, this.explosionY, this.explosionZ);
		int livingEntities = 0;
		for (Object obj : list)
			if (obj instanceof EntityLivingBase)
				livingEntities++;
		for (int l1 = 0; l1 < list.size(); ++l1) {
			Entity entity = (Entity) list.get(l1);
			if (!entity.isImmuneToExplosions()) {
				double d4 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize
						* 0.5;

				if (d4 <= 1.0D) {
					double d5 = entity.posX - this.explosionX;
					double d6 = entity.posY + entity.getEyeHeight() - this.explosionY;
					double d7 = entity.posZ - this.explosionZ;
					double d9 = MathHelper.sqrt(d5 * d5 + d6 * d6 + d7 * d7);

					if (d9 != 0.0D) {
						d5 /= d9;
						d6 /= d9;
						d7 /= d9;
						float explMod = (this.explosionSize / 6);
						d5 *= explMod;
						d6 *= explMod;
						d7 *= explMod;
						double d10 = this.getBlockDensity(Vec3d, entity.getEntityBoundingBox());
						double d11 = (1.0D - d4) * d10;
						if (!this.harmless && !(entity instanceof EntityItem && this.dropChance > 1f))
							entity.attackEntityFrom(
									DamageSource
											.causeExplosionDamage(this),
									((int) ((d11 * d11 + d11) / 2.0D * 8.0D * (this.getExplosivePlacedBy() == entity
											? Math.min(3, this.explosionSize) : this.explosionSize) + 1.0D)));
						double res = 1.0;
						if (entity instanceof EntityLivingBase)
							res = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, d11);
						// System.out.println("d: "+ d5 * d8+" "+d6 * d8+" "+d7
						// * d8);
						entity.addVelocity(d5, d6, d7);
						// entity.addVelocity(2, 2, 2);
						if (entity instanceof EntityPlayer)
							this.func_77277_b().put(entity, new Vec3d(d5 * d11, d6 * d11, d7 * d11));
					}
				}
			}
		}
	}

	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	@Override
	public void doExplosionB(boolean p_77279_1_) {
		this.world.playSound((EntityPlayer) null, this.explosionX, this.explosionY, this.explosionZ,
				SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F,
				(1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);

		if (this.explosionSize >= 2.0F && this.isSmoking)
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY,
					this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
		else
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY,
					this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);

		Iterator iterator;
		BlockPos blockpos;

		if (this.isSmoking && !this.harmless) {
			iterator = this.affectedBlockPositions.iterator();

			while (iterator.hasNext()) {
				blockpos = (BlockPos) iterator.next();
				IBlockState state = this.world.getBlockState(blockpos);
				Block block = state.getBlock();

				if (p_77279_1_) {
					double d0 = blockpos.getX() + this.world.rand.nextFloat();
					double d1 = blockpos.getY() + this.world.rand.nextFloat();
					double d2 = blockpos.getZ() + this.world.rand.nextFloat();
					double d3 = d0 - this.explosionX;
					double d4 = d1 - this.explosionY;
					double d5 = d2 - this.explosionZ;
					double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / this.explosionSize + 0.1D);
					d7 *= this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F;
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
							(d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D,
							(d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5, new int[0]);
					this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
				}

				if (state.getMaterial() != Material.AIR) {
					if (block.canDropFromExplosion(this))
						block.dropBlockAsItemWithChance(this.world, blockpos, state,
								this.dropChance / this.explosionSize, 0);

					block.onBlockExploded(this.world, blockpos, this);
				}
			}
		}

		if (this.isFlaming) {
			iterator = this.affectedBlockPositions.iterator();

			while (iterator.hasNext()) {
				blockpos = (BlockPos) iterator.next();

				if (this.world.getBlockState(blockpos).getMaterial() == Material.AIR
						&& this.world.getBlockState(blockpos.offset(EnumFacing.DOWN)).isFullBlock()
						&& this.explosionRNG.nextInt(3) == 0)
					this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
			}
		}
	}

	public float getBlockDensity(Vec3d p_72842_1_, AxisAlignedBB p_72842_2_) {
		double d0 = 1.0D / ((p_72842_2_.maxX - p_72842_2_.minX) * 2.0D + 1.0D);
		double d1 = 1.0D / ((p_72842_2_.maxY - p_72842_2_.minY) * 2.0D + 1.0D);
		double d2 = 1.0D / ((p_72842_2_.maxZ - p_72842_2_.minZ) * 2.0D + 1.0D);

		if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
			int i = 0;
			int j = 0;

			for (float f = 0.0F; f <= 1.0F; f = (float) (f + d0))
				for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) (f1 + d1))
					for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) (f2 + d2)) {
						double d3 = p_72842_2_.minX + (p_72842_2_.maxX - p_72842_2_.minX) * f;
						double d4 = p_72842_2_.minY + (p_72842_2_.maxY - p_72842_2_.minY) * f1;
						double d5 = p_72842_2_.minZ + (p_72842_2_.maxZ - p_72842_2_.minZ) * f2;
						RayTraceResult mop = this.world.rayTraceBlocks(new Vec3d(d3, d4, d5), p_72842_1_, false,
								true, false);
						if (mop == null
								|| this.world.getBlockState(mop.getBlockPos()).getBlock() == Blocks.SNOW_LAYER)
							++i;

						++j;
					}

			return (float) i / (float) j;
		} else
			return 0.0F;
	}

	public Map func_77277_b() {
		return this.field_77288_k;
	}

	/**
	 * Returns either the entity that placed the explosive block, the entity
	 * that caused the explosion or null.
	 */

	public void func_180342_d() {
		this.affectedBlockPositions.clear();
	}

	public List func_180343_e() {
		return this.affectedBlockPositions;
	}

	@Override
	public Vec3d getPosition() {
		return this.position;
	}

	@Override
	public EntityLivingBase getExplosivePlacedBy() {
		return ((EntityFallingEnchantedBlock) this.exploder).owner;
	}
}
