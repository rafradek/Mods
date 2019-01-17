package rafradek.blocklauncher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFallingEnchantedBlock extends Entity implements IEntityAdditionalSpawnData {

	private boolean isBreakingAnvil;
	public boolean isPrimed;
	public int sticky;
	public boolean attackOne;
	public IBlockState block;
	public int metadata;
	public int field_145812_b;
	public boolean dropItems;
	private boolean field_145809_g;
	private int field_145815_h;
	private float field_145816_i;
	public NBTTagCompound dataTag;
	public EntityLivingBase owner;
	public float scale;
	private Entity lastAttacked;
	private float explosionSize;
	private boolean impact;
	public int fuse;
	private float dropChance;
	private boolean harmless;
	private int shrink;
	private float damage;
	public boolean isFired;
	public Block fireBlock = Blocks.FIRE;
	private boolean nogravity;
	private int tntamount;
	private float knockback;
	public boolean growing;
	public boolean mine;
	private ItemStack stack;
	public boolean fireBetter;
	public boolean isPart;
	public float health=100;

	public EntityFallingEnchantedBlock(World p_i1706_1_) {
		super(p_i1706_1_);
		this.setSize(1, 1);
		this.dropItems = true;
		this.field_145815_h = 40;
		this.field_145816_i = 2.0F;
	}

	public EntityFallingEnchantedBlock(World p_i45319_1_, double p_i45319_2_, double p_i45319_4_, double p_i45319_6_,
			IBlockState p_i45319_8_) {
		super(p_i45319_1_);

		this.field_145815_h = 40;
		this.field_145816_i = 2.0F;
		this.block = p_i45319_8_;
		this.metadata = this.block.getBlock().getMetaFromState(this.block);
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		// this.yOffset = this.height / 2.0F;
		this.setPosition(p_i45319_2_, p_i45319_4_, p_i45319_6_);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = p_i45319_2_;
		this.prevPosY = p_i45319_4_;
		this.prevPosZ = p_i45319_6_;
		this.attackOne = p_i45319_8_.getMaterial() == Material.GLASS;
	}

	public EntityFallingEnchantedBlock(World par1World, double par2, double par4, double par6, int fuse, boolean impact,
			float explosionRadius, float dropchance, boolean harmless, int tntamount) {
		this(par1World, par2, par4, par6, Blocks.TNT.getDefaultState());
		this.fuse = fuse;
		this.impact = impact;
		this.explosionSize = explosionRadius;
		this.isPrimed = true;
		this.dropChance = dropchance;
		this.harmless = harmless;
		this.tntamount = tntamount;
	}

	public void setupEntity(int mode, float scale, boolean efficient, int shrink, float damage, EntityLivingBase owner,
			boolean nogravity, float knockback) {
		this.dropItems = !efficient;
		this.nogravity = nogravity;
		this.scale = scale;
		this.owner = owner;
		this.shrink = shrink;
		this.damage = damage;
		this.setSize(scale / (shrink * shrink), scale / (shrink * shrink));
		this.sticky = mode;
		this.stack = owner.getHeldItem(EnumHand.MAIN_HAND);
		this.knockback = knockback;
		if(this.sticky==3)
			this.noClip=true;
		this.health = Math.min(80,BlockLauncher.getHardness(this.block, this.world)*1.55f);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they
	 * walk on. used for spiders and wolves to prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
	}

	/**
	 * Returns true if other Entities should be prevented from moving through
	 * this Entity.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onUpdate() {
		Block block = this.block.getBlock();
		if (this.block.getMaterial() == Material.AIR)
			this.setDead();
		else {

			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			double oldMotionX = this.motionX;
			double oldMotionY = this.motionY;
			double oldMotionZ = this.motionZ;
			float motion = (float) (this.motionX * this.motionX + this.motionY * this.motionY
					+ this.motionZ * this.motionZ);
			if (this.growing) {
				double motionn = Math.sqrt(motion);
				this.damage += motionn * 0.05f;
				this.scale = (float) Math.min(this.scale + motionn * 0.08f, 5);
			}
			// this.setSize(Math.max(width*0.93f,0.35f),
			// Math.max(height*0.93f,0.35f));
			// System.out.println(this.world.isRemote+" "+scale);
			BlockPos blockpos = new BlockPos(this);

			if (!this.world.isRemote/*
										 * &&this.block.
										 * getCollisionBoundingBoxFromPool(this.
										 * worldObj, i, j, k)!=null
										 */) {
				List<Entity> list2 = this.world.getEntitiesWithinAABBExcludingEntity(this,
						this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(10.0D,
								10.0D, 10.0D));
				if (this.mine && this.collided && list2.size() != 0)
					for (Entity ent : list2)
						if (ent instanceof EntityLivingBase && ent != this.owner
								&& ent.getEntityBoundingBox().intersects(this.getEntityBoundingBox())) {
							this.fuse = 999;
							this.setDead();
							this.explode(oldMotionX, oldMotionY, oldMotionZ);
							return;
						}
				HashMap<Entity, RayTraceResult> resultMap = new HashMap<Entity, RayTraceResult>();
				Vec3d[] startVecs = new Vec3d[1];
				Entity entity = null;
				/*
				 * startVecs[0]=new Vec3d(this.getEntityBoundingBox().minX,
				 * this.motionX<0||this.motionZ<0?this.getEntityBoundingBox().
				 * minY:this.getEntityBoundingBox().maxY,
				 * this.getEntityBoundingBox().minZ); startVecs[1]=new
				 * Vec3d(this.getEntityBoundingBox().maxX,
				 * this.motionX>0||this.motionZ<0?this.getEntityBoundingBox().
				 * minY:this.getEntityBoundingBox().maxY,
				 * this.getEntityBoundingBox().minZ); startVecs[2]=new
				 * Vec3d(this.getEntityBoundingBox().minX,
				 * this.motionX<0||this.motionZ>0?this.getEntityBoundingBox().
				 * minY:this.getEntityBoundingBox().maxY,
				 * this.getEntityBoundingBox().maxZ); startVecs[3]=new
				 * Vec3d(this.getEntityBoundingBox().maxX,
				 * this.motionX>0||this.motionZ>0?this.getEntityBoundingBox().
				 * minY:this.getEntityBoundingBox().maxY,
				 * this.getEntityBoundingBox().maxZ);
				 */
				startVecs[0] = new Vec3d(this.posX, this.posY, this.posZ);
				RayTraceResult mop = null;
				for (int c = 0; c < 1; c++) {
					RayTraceResult RayTraceResult = this.world.rayTraceBlocks(startVecs[c],
							startVecs[c].addVector(this.motionX, this.motionY, this.motionZ), false, true, false);
					Vec3d Vec3d = new Vec3d(this.posX, this.posY, this.posZ);
					Vec3d Vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY,
							this.posZ + this.motionZ);
					if (RayTraceResult != null) {
						Vec3d1 = new Vec3d(RayTraceResult.hitVec.x, RayTraceResult.hitVec.y,
								RayTraceResult.hitVec.z);
						if (this.impact) {
							this.setPosition(Vec3d1.x, Vec3d1.y, Vec3d1.z);
							this.fuse = 999;
							this.setDead();
							this.explode(oldMotionX, oldMotionY, oldMotionZ);
							return;
						}
					}

					double d0 = 0.0D;
					EntityLivingBase entitylivingbase = this.owner;

					for (int h = 0; h < list2.size(); ++h) {
						Entity entity1 = list2.get(h);

						if (entity1.canBeCollidedWith() && (entity1 != entitylivingbase)
								&& !(entity1 instanceof EntityFallingEnchantedBlock)) {
							float f = this.scale / 2;
							AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(f, f, f).offset(0, -f,
									0);
							RayTraceResult RayTraceResult1 = axisalignedbb.calculateIntercept(Vec3d, Vec3d1);

							if (RayTraceResult1 != null) {
								double d1 = Vec3d.distanceTo(RayTraceResult1.hitVec);

								if (this.attackOne && (d1 < d0 || d0 == 0.0D)) {
									entity = entity1;
									d0 = d1;
									mop = RayTraceResult1;
								} else if (!resultMap.containsKey(entity1))
									resultMap.put(entity1, RayTraceResult1);

							}
						}

					}
				}
				if (this.attackOne)
					this.attackEntity(entity, motion, mop);
				else {
					Set<Entity> iterator = resultMap.keySet();
					for (Entity entityr : iterator)
						this.attackEntity(entityr, motion, resultMap.get(entityr));
				}

				if(this.sticky==3){
					double distS=Math.sqrt(motion);
					Vec3d moveVec=new Vec3d(this.motionX,this.motionY,this.motionZ).normalize();
					AxisAlignedBB box=this.getEntityBoundingBox();
					float hardness = BlockLauncher.getHardness(this.block, this.world);
					boolean hit=false;
					for(int i=0;i<=distS && moveVec.lengthSquared()>0;i++){
						for(int oX=MathHelper.floor(box.minX);oX<box.maxX && health>0;oX++)
							for(int oY=MathHelper.floor(box.minY);oY<box.maxY && health>0;oY++)
								for(int oZ=MathHelper.floor(box.minZ);oZ<box.maxZ && health>0;oZ++){
									
									float damage=(float) Math.min((hardness * Math.pow(motion, 1D/3D) *2.5f* this.damage), this.health);
									float damageLeft=BlockLauncher.damageBlock(new BlockPos(oX, oY, oZ), this.owner, this.world, damage);
									moveVec=moveVec.scale(damageLeft/damage);
									this.motionX*=damageLeft/damage;
									this.motionY*=damageLeft/damage;
									this.motionZ*=damageLeft/damage;
									health-=damage-damageLeft;
									if(damage != damageLeft)
										hit=true;
								}
						box=box.offset(moveVec.x, moveVec.y, moveVec.z);
					}
					if(hit) {
						this.lastAttacked=null;
						for(Entity entityh:this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(this.scale*2, this.scale*2, this.scale*2), new Predicate<Entity>() {

							@Override
							public boolean apply(Entity input) {
								// TODO Auto-generated method stub
								return getDistanceSq(input)<scale*5;
							}
							
						})) {
							this.attackEntity(entityh, motion, new RayTraceResult(entityh));
						}
					}
					if(moveVec.lengthSquared()<=0){
						if(this.dropItems && this.rand.nextBoolean())
							this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.block)), 0.0F);
						this.setDead();
					}
				}
			}
			int g = -1;
			++this.field_145812_b;
			
			if (!(this.sticky == 1 && this.collided)) {
				if (!this.nogravity)
					this.motionY += 0.03999999910593033D * g;
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				double mult = this.nogravity ? 0.99 : 0.98;
				this.motionX *= mult;
				this.motionY *= mult;
				this.motionZ *= mult;

			} else {
				this.motionX *= 0D;
				this.motionZ *= 0D;
				this.motionY *= 0D;
			}

			if ((this.sticky == 1 && this.collided) || (this.sticky == 0 && this.onGround)) {
				this.motionX *= 0.699999988079071D;
				this.motionZ *= 0.699999988079071D;
				this.motionY *= -0.5D;
			}
			if (!this.world.isRemote) {
				if (this.isFired && !this.world.isAirBlock(blockpos)
						&& this.world.getBlockState(blockpos).getBlock().isReplaceable(world, blockpos)) {
					this.world.setBlockState(blockpos, this.fireBlock.getDefaultState());
					if (this.fireBetter)
						this.makeBurn(blockpos);
				}

				if (this.collided)
					if (this.impact)
						this.fuse = 0;
				
					else if (!this.isPart && !this.isDead && this.attackOne) {
						this.playSound(block.getSoundType().getBreakSound(), 1F, 1F);
						for (int a = 0; a < 16; a++) {
							EntityFallingEnchantedBlock blockF = new EntityFallingEnchantedBlock(world, this.posX,
									this.posY + 0.5, this.posZ, this.block);
							blockF.setupEntity(this.sticky, this.scale / 2, true, this.shrink, this.damage, owner,
									this.nogravity, this.knockback);
							blockF.motionX = this.rand.nextFloat() * 1 - 0.5;
							blockF.motionY = this.rand.nextFloat() * 0.4;
							blockF.motionZ = this.rand.nextFloat() * 1 - 0.5;
							blockF.isPart = true;
							this.world.spawnEntity(blockF);
						}
						this.dropItems = false;
						this.setDead();
					}
				if (!this.isPrimed) {
					if (this.nogravity && motion < 0.004d)
						this.nogravity = false;
					if ((this.sticky == 1 && this.collided) || (this.sticky != 1 && this.onGround)) {
						if ((this.sticky == 1 || motion < 0.004d)
								&& this.world.getBlockState(blockpos).getBlock() != Blocks.PISTON_EXTENSION) {
							boolean placed = false;
							if (this.dropItems)
								placed = placeBlock(blockpos) || placeBlock(blockpos.offset(EnumFacing.EAST))
										|| placeBlock(blockpos.offset(EnumFacing.WEST))
										|| placeBlock(blockpos.offset(EnumFacing.UP))
										|| placeBlock(blockpos.offset(EnumFacing.DOWN))
										|| placeBlock(blockpos.offset(EnumFacing.SOUTH))
										|| placeBlock(blockpos.offset(EnumFacing.NORTH));
							else if (this.isFired) {
								for (int a = 0; a < 6; a++) {
									BlockPos firepos = blockpos.offset(EnumFacing.getFront(a));
									if (this.world.getBlockState(firepos).getBlock().isReplaceable(world,
											firepos)) {
										this.world.setBlockState(firepos, this.fireBlock.getDefaultState());
										if (this.fireBetter)
											this.makeBurn(firepos);

									}
								}
								if (this.world.getBlockState(blockpos).getBlock().isReplaceable(world,
										blockpos)) {
									this.world.setBlockState(blockpos, this.fireBlock.getDefaultState());
									if (this.fireBetter)
										this.makeBurn(blockpos);
								}
							}
							this.setDead();
							if (this.dropItems && !this.isBreakingAnvil && !placed)
								this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.block)), 0.0F);
						}
					} else if (blockpos.getY() < 1 || blockpos.getY() > 256) {
						if (this.dropItems)
							this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.block)), 0.0F);

						this.setDead();
					}
				}

				if (Minecraft.getMinecraft().getIntegratedServer() != null
						&& Minecraft.getMinecraft().player != null) {
					EntityFallingEnchantedBlock fentity = (EntityFallingEnchantedBlock) Minecraft
							.getMinecraft().world.getEntityByID(this.getEntityId());
					if (fentity != null) {
						fentity.setVelocity(this.motionX, this.motionY, this.motionZ);
						fentity.setPosition(this.posX, this.posY, this.posZ);
						fentity.prevPosX = this.prevPosX;
						fentity.prevPosY = this.prevPosY;
						fentity.prevPosZ = this.prevPosZ;
					}
				}
			}
			if (this.isPrimed && (fuse-- <= 0 || (this.collided && !this.world.isRemote && !this.stack.isEmpty()
					&& this.stack.getTagCompound() != null && this.stack.getTagCompound().getInteger("explode") > 0))) {
				

				if (!this.world.isRemote) {
					this.explode(oldMotionX, oldMotionY, oldMotionZ);
					this.setDead();
				}
			}
		}
	}

	public void makeBurn(BlockPos pos) {
		// System.out.println("burned2");
		List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this,
				new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
		for (Entity entity : entities) {
			entity.setFire(15);
			if (entity != this.owner)
				BlockEventBus.extraBurn.put(entity, 100);
		}
	}

	@SuppressWarnings("unchecked")
	private void attackEntity(Entity entity, float motion, RayTraceResult mop) {
		Block block = this.block.getBlock();
		if (entity == null) {
			List<Entity> list2 = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
			for (int h = 0; h < list2.size(); ++h) {
				Entity entity1 = list2.get(h);
				if (entity1 != this.owner && !(entity1 instanceof EntityFallingEnchantedBlock)) {
					entity = entity1;
					break;
				}
			}

		}
		if (entity != null && mop != null) {
			double x = mop.hitVec.x;
			double y = mop.hitVec.y;
			double z = mop.hitVec.z;
			float hardness = BlockLauncher.getHardness(this.block, this.world);
			if (this.impact) {
				double maxPos = Math.max(this.motionX, Math.max(this.motionY, this.motionZ)) * 3;
				this.setPosition(x, y, z);
				this.fuse = 999;
				this.setDead();
				this.explode(this.motionX, this.motionY, this.motionZ);
				return;
			}
			if (this.lastAttacked != entity) {
				entity.hurtResistantTime = 0;
				System.out.println("Motion: "+motion+" "+Math.sqrt(motion));
				float dmg = (float) Math.min((hardness * Math.pow(motion, 1D/3D) *2.5f* this.damage), 80);
				// System.out.println("attacked: "+hardness+" "+dmg);
				if (this.owner == null)
					entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this), dmg);
				else
					entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.owner), dmg);
				if (this.isFired)
					if (!this.fireBetter)
						entity.setFire(10);
					else {
						entity.setFire(15);
						BlockEventBus.extraBurn.put(entity, 100);
					}
				if (entity instanceof EntityLivingBase)
					if (block instanceof BlockFalling && block != Blocks.ANVIL) {
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.getPotionById(15), 50, 1));
						if (entity instanceof EntityLiving) {
							((EntityLivingBase) entity)
									.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 50, 3));
							((EntityLiving) entity).setAttackTarget(null);
							((EntityLiving) entity).setLastAttackedEntity(null);
						}
					} else if (block == Blocks.WEB)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.getPotionById(2), 100, 2));
					else if (block instanceof BlockBush)
						((EntityLivingBase) entity).heal(2);
					else if (this.block.getMaterial() == Material.GLASS)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.getPotionById(19), 100, 1));
			}
			if (this.attackOne == true && !this.isDead && !this.isPart) {
				this.playSound(block.getSoundType().getBreakSound(), 1F, 1F);
				for (int i = 0; i < 16; i++) {
					EntityFallingEnchantedBlock blockF = new EntityFallingEnchantedBlock(world, x, y, z, this.block);
					blockF.setupEntity(this.sticky, this.scale / 2, true, this.shrink, this.damage / 2, owner,
							this.nogravity, this.knockback);
					blockF.motionX = this.rand.nextFloat() * 1 - 0.5;
					blockF.motionY = this.rand.nextFloat() * 0.4;
					blockF.motionZ = this.rand.nextFloat() * 1 - 0.5;
					blockF.isPart = true;
					this.world.spawnEntity(blockF);
				}
				this.dropItems = false;
				this.setDead();
			}
			this.lastAttacked = entity;
			hardness *= this.knockback;
			if(entity.motionX*entity.motionX+entity.motionY*entity.motionY+entity.motionZ*entity.motionZ<motion)
			entity.addVelocity(this.motionX * Math.min(hardness / (this.isFired ? 10 : 7), 1.6),
					this.motionY * Math.min(hardness / (this.isFired ? 10 : 7), 1.6),
					this.motionZ * Math.min(hardness / (this.isFired ? 10 : 7), 1.6));
		}
	}

	public boolean canBuildAt(BlockPos blockpos) {
		return !BlockFalling.canFallThrough(this.world.getBlockState(blockpos.offset(EnumFacing.EAST)))
				|| !BlockFalling.canFallThrough(this.world.getBlockState(blockpos.offset(EnumFacing.WEST)))
				|| !BlockFalling.canFallThrough(this.world.getBlockState(blockpos.offset(EnumFacing.UP)))
				|| !BlockFalling.canFallThrough(this.world.getBlockState(blockpos.offset(EnumFacing.DOWN)))
				|| !BlockFalling.canFallThrough(this.world.getBlockState(blockpos.offset(EnumFacing.SOUTH)))
				|| !BlockFalling.canFallThrough(this.world.getBlockState(blockpos.offset(EnumFacing.NORTH)));

	}

	public boolean placeBlock(BlockPos blockpos) {
		Block block = this.block.getBlock();
		IBlockState prevstate=this.world.getBlockState(blockpos);
		if (!this.isBreakingAnvil
				&& this.world.mayPlace(block, blockpos, true, EnumFacing.UP, (Entity) null)
				&& (!BlockFalling.canFallThrough(this.world.getBlockState(blockpos.offset(EnumFacing.DOWN)))
						|| (this.sticky == 1 && this.canBuildAt(blockpos)) || this.nogravity)
				&& this.world.setBlockState(blockpos, this.block, 3)) {
			if (this.isFired)
				for (int a = 0; a < 6; a++) {
					EnumFacing facing = EnumFacing.VALUES[a];
					BlockPos firepos = new BlockPos(blockpos.getX() + facing.getFrontOffsetX(),
							blockpos.getY() + facing.getFrontOffsetY(), blockpos.getZ() + facing.getFrontOffsetZ());
					if (this.world.getBlockState(firepos).getBlock().isReplaceable(world, firepos))
						this.world.setBlockState(firepos, this.fireBlock.getDefaultState());
				}
			if (block instanceof BlockFalling)
				((BlockFalling) block).onEndFalling(this.world, blockpos, this.block, prevstate);

			if (this.dataTag != null && block instanceof ITileEntityProvider) {
				TileEntity tileentity = this.world.getTileEntity(blockpos);

				if (tileentity != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					tileentity.writeToNBT(nbttagcompound);
					Iterator iterator = this.dataTag.getKeySet().iterator();

					while (iterator.hasNext()) {
						String s = (String) iterator.next();
						NBTBase nbtbase = this.dataTag.getTag(s);

						if (!s.equals("x") && !s.equals("y") && !s.equals("z"))
							nbttagcompound.setTag(s, nbtbase.copy());
					}

					tileentity.readFromNBT(nbttagcompound);
					tileentity.markDirty();
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		p_70014_1_.setInteger("TileID", Block.getIdFromBlock(this.block.getBlock()));
		p_70014_1_.setByte("Data", (byte) this.metadata);
		p_70014_1_.setByte("Time", (byte) this.field_145812_b);
		p_70014_1_.setBoolean("DropItem", this.dropItems);
		p_70014_1_.setBoolean("HurtEntities", this.field_145809_g);
		p_70014_1_.setFloat("FallHurtAmount", this.field_145816_i);
		p_70014_1_.setInteger("FallHurtMax", this.field_145815_h);
		p_70014_1_.setInteger("Sticky", this.sticky);
		p_70014_1_.setBoolean("NoGravity", this.nogravity);
		if (this.dataTag != null)
			p_70014_1_.setTag("TileEntityData", this.dataTag);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		this.metadata = p_70037_1_.getByte("Data") & 255;
		this.block = Block.getBlockById(p_70037_1_.getInteger("TileID")).getStateFromMeta(metadata);
		this.sticky = p_70037_1_.getInteger("Sticky");
		this.nogravity = p_70037_1_.getBoolean("NoGravity");

		this.field_145812_b = p_70037_1_.getByte("Time") & 255;

		if (p_70037_1_.hasKey("HurtEntities", 99)) {
			this.field_145809_g = p_70037_1_.getBoolean("HurtEntities");
			this.field_145816_i = p_70037_1_.getFloat("FallHurtAmount");
			this.field_145815_h = p_70037_1_.getInteger("FallHurtMax");
		} else if (this.block == Blocks.ANVIL)
			this.field_145809_g = true;

		if (p_70037_1_.hasKey("DropItem", 99))
			this.dropItems = p_70037_1_.getBoolean("DropItem");

		if (p_70037_1_.hasKey("TileEntityData", 10))
			this.dataTag = p_70037_1_.getCompoundTag("TileEntityData");

		if (this.block.getMaterial() == Material.AIR)
			this.setDead();
	}

	@Override
	public void addEntityCrashInfo(CrashReportCategory p_85029_1_) {
		super.addEntityCrashInfo(p_85029_1_);
		p_85029_1_.addCrashSection("Immitating block ID", Integer.valueOf(Block.getIdFromBlock(this.block.getBlock())));
		p_85029_1_.addCrashSection("Immitating block data", Integer.valueOf(this.metadata));
	}

	@Override
	public void move(MoverType type,double x, double y, double z) {
		if (this.noClip) {
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
			this.resetPositionToBB();
		} else {
			this.world.profiler.startSection("move");
			double d3 = this.posX;
			double d4 = this.posY;
			double d5 = this.posZ;

			if (this.isInWeb) {
				this.isInWeb = false;
				x *= 0.25D;
				y *= 0.05000000074505806D;
				z *= 0.25D;
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			double d6 = x;
			double d7 = y;
			double d8 = z;

			List list1 = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(x, y, z));
			AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
			AxisAlignedBB axisalignedbb1;

			
			for (Iterator iterator = list1.iterator(); iterator
					.hasNext(); y = axisalignedbb1.calculateYOffset(this.getEntityBoundingBox(), y))
				axisalignedbb1 = (AxisAlignedBB) iterator.next();
			double limit = y / d7;
			if (this.sticky != 1)
				this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
			boolean flag1 = this.onGround || d7 != y && d7 < 0.0D;
			AxisAlignedBB axisalignedbb2;
			Iterator iterator8;

			for (iterator8 = list1.iterator(); iterator8
					.hasNext(); x = axisalignedbb2.calculateXOffset(this.getEntityBoundingBox(), x))
				axisalignedbb2 = (AxisAlignedBB) iterator8.next();
			if (this.sticky == 1) {
				if (x / d6 < limit)
					limit = x / d6;
			} else
				this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));

			for (iterator8 = list1.iterator(); iterator8
					.hasNext(); z = axisalignedbb2.calculateZOffset(this.getEntityBoundingBox(), z))
				axisalignedbb2 = (AxisAlignedBB) iterator8.next();

			if (this.sticky == 1) {
				if (z / d8 < limit)
					limit = z / d8;
				this.setEntityBoundingBox(this.getEntityBoundingBox().offset(d6 * limit, d7 * limit, d8 * limit));
			} else
				this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));

			if (this.sticky != 1 && this.stepHeight > 0.0F && flag1 && (d6 != x || d8 != z)) {
				double d14 = x;
				double d10 = y;
				double d11 = z;
				AxisAlignedBB axisalignedbb3 = this.getEntityBoundingBox();
				this.setEntityBoundingBox(axisalignedbb);
				y = this.stepHeight;
				List list = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(d6, y, d8));
				AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
				AxisAlignedBB axisalignedbb5 = axisalignedbb4.expand(d6, 0.0D, d8);
				double d12 = y;
				AxisAlignedBB axisalignedbb6;

				for (Iterator iterator1 = list.iterator(); iterator1
						.hasNext(); d12 = axisalignedbb6.calculateYOffset(axisalignedbb5, d12))
					axisalignedbb6 = (AxisAlignedBB) iterator1.next();

				axisalignedbb4 = axisalignedbb4.offset(0.0D, d12, 0.0D);
				double d18 = d6;
				AxisAlignedBB axisalignedbb7;

				for (Iterator iterator2 = list.iterator(); iterator2
						.hasNext(); d18 = axisalignedbb7.calculateXOffset(axisalignedbb4, d18))
					axisalignedbb7 = (AxisAlignedBB) iterator2.next();

				axisalignedbb4 = axisalignedbb4.offset(d18, 0.0D, 0.0D);
				double d19 = d8;
				AxisAlignedBB axisalignedbb8;

				for (Iterator iterator3 = list.iterator(); iterator3
						.hasNext(); d19 = axisalignedbb8.calculateZOffset(axisalignedbb4, d19))
					axisalignedbb8 = (AxisAlignedBB) iterator3.next();

				axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d19);
				AxisAlignedBB axisalignedbb13 = this.getEntityBoundingBox();
				double d20 = y;
				AxisAlignedBB axisalignedbb9;

				for (Iterator iterator4 = list.iterator(); iterator4
						.hasNext(); d20 = axisalignedbb9.calculateYOffset(axisalignedbb13, d20))
					axisalignedbb9 = (AxisAlignedBB) iterator4.next();

				axisalignedbb13 = axisalignedbb13.offset(0.0D, d20, 0.0D);
				double d21 = d6;
				AxisAlignedBB axisalignedbb10;

				for (Iterator iterator5 = list.iterator(); iterator5
						.hasNext(); d21 = axisalignedbb10.calculateXOffset(axisalignedbb13, d21))
					axisalignedbb10 = (AxisAlignedBB) iterator5.next();

				axisalignedbb13 = axisalignedbb13.offset(d21, 0.0D, 0.0D);
				double d22 = d8;
				AxisAlignedBB axisalignedbb11;

				for (Iterator iterator6 = list.iterator(); iterator6
						.hasNext(); d22 = axisalignedbb11.calculateZOffset(axisalignedbb13, d22))
					axisalignedbb11 = (AxisAlignedBB) iterator6.next();

				axisalignedbb13 = axisalignedbb13.offset(0.0D, 0.0D, d22);
				double d23 = d18 * d18 + d19 * d19;
				double d13 = d21 * d21 + d22 * d22;

				if (d23 > d13) {
					x = d18;
					z = d19;
					this.setEntityBoundingBox(axisalignedbb4);
				} else {
					x = d21;
					z = d22;
					this.setEntityBoundingBox(axisalignedbb13);
				}

				y = (-this.stepHeight);
				AxisAlignedBB axisalignedbb12;

				for (Iterator iterator7 = list.iterator(); iterator7
						.hasNext(); y = axisalignedbb12.calculateYOffset(this.getEntityBoundingBox(), y))
					axisalignedbb12 = (AxisAlignedBB) iterator7.next();

				this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

				if (d14 * d14 + d11 * d11 >= x * x + z * z) {
					x = d14;
					y = d10;
					z = d11;
					this.setEntityBoundingBox(axisalignedbb3);
				}
			}
			this.world.profiler.endSection();
			this.world.profiler.startSection("rest");
			this.resetPositionToBB();
			this.collidedHorizontally = d6 != x || d8 != z;
			this.collidedVertically = d7 != y;
			this.onGround = this.collidedVertically && d7 < 0.0D;
			this.collided = this.collidedHorizontally || this.collidedVertically;
			int i = MathHelper.floor(this.posX);
			int j = MathHelper.floor(this.posY - 0.20000000298023224D);
			int k = MathHelper.floor(this.posZ);
			BlockPos blockpos = new BlockPos(i, j, k);
			IBlockState blockState = this.world.getBlockState(blockpos);
			Block block1 = blockState.getBlock();

			if (blockState.getMaterial() == Material.AIR) {
				Block block = this.world.getBlockState(blockpos.offset(EnumFacing.DOWN)).getBlock();

				if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate) {
					block1 = block;
					blockpos = blockpos.offset(EnumFacing.DOWN);
					blockState = this.world.getBlockState(blockpos);
				}
			}

			this.updateFallState(y, this.onGround, blockState, blockpos);

			if (d6 != x)
				if (this.sticky == 2)
					this.motionX = -this.motionX;
				else
					this.motionX = 0.0D;

			if (d8 != z)
				if (this.sticky == 2)
					this.motionZ = -this.motionZ;
				else
					this.motionZ = 0.0D;

			if (d7 != y)
				if (this.sticky == 2)
					this.motionY = -this.motionY;
				else
					block1.onLanded(this.world, this);

			if (this.canTriggerWalking() && this.getRidingEntity() == null) {
				double d15 = this.posX - d3;
				double d16 = this.posY - d4;
				double d17 = this.posZ - d5;

				if (block1 != Blocks.LADDER)
					d16 = 0.0D;

				if (block1 != null && this.onGround)
					block1.onEntityWalk(this.world, blockpos, this);
			}

			try {
				this.doBlockCollisions();
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
				CrashReportCategory crashreportcategory = crashreport
						.makeCategory("Entity being checked for collision");
				this.addEntityCrashInfo(crashreportcategory);
				throw new ReportedException(crashreport);
			}

			this.world.profiler.endSection();
		}
	}

	/**
	 * Resets the entity's position to the center (planar) and bottom (vertical)
	 * points of its bounding box.
	 */
	@Override
	public void resetPositionToBB() {
		this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
		this.posY = this.getEntityBoundingBox().minY;
		this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
	}

	/*
	 * protected void func_145775_I() { int i =
	 * MathHelper.floor(this.getEntityBoundingBox().minX + 0.001D); int j
	 * = MathHelper.floor(this.getEntityBoundingBox().minY + 0.001D); int
	 * k = MathHelper.floor(this.getEntityBoundingBox().minZ + 0.001D);
	 * int l = MathHelper.floor(this.getEntityBoundingBox().maxX -
	 * 0.001D); int i1 =
	 * MathHelper.floor(this.getEntityBoundingBox().maxY - 0.001D); int
	 * j1 = MathHelper.floor(this.getEntityBoundingBox().maxZ - 0.001D);
	 * 
	 * boolean first=false; if (this.world.checkChunksExist(i, j, k, l, i1,
	 * j1)) { for (int k1 = i; k1 <= l; ++k1) { for (int l1 = j; l1 <= i1; ++l1)
	 * { for (int i2 = k; i2 <= j1; ++i2) { Block block =
	 * this.world.getBlock(k1, l1, i2); if(!first){ first=true;
	 * this.placeBlock(k1, l1, i2); } try {
	 * block.onEntityCollidedWithBlock(this.world, k1, l1, i2, this); } catch
	 * (Throwable throwable) { CrashReport crashreport =
	 * CrashReport.makeCrashReport(throwable, "Colliding entity with block");
	 * CrashReportCategory crashreportcategory =
	 * crashreport.makeCategory("Block being collided with");
	 * CrashReportCategory.func_147153_a(crashreportcategory, k1, l1, i2, block,
	 * this.world.getBlockMetadata(k1, l1, i2)); throw new
	 * ReportedException(crashreport); } } } } } }
	 */
	/**
	 * Return whether this entity should be rendered as on fire.
	 */
	private void explode(double motionX, double motionY, double motionZ) {
		int i = MathHelper.floor(this.posX - this.explosionSize - 1.0D);
		int j = MathHelper.floor(this.posX + this.explosionSize + 1.0D);
		int k = MathHelper.floor(this.posY - this.explosionSize - 1.0D);
		int l1 = MathHelper.floor(this.posY + this.explosionSize + 1.0D);
		int i2 = MathHelper.floor(this.posZ - this.explosionSize - 1.0D);
		int j2 = MathHelper.floor(this.posZ + this.explosionSize + 1.0D);
		List list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(i, k, i2, j, l1, j2));
		for (int k2 = 0; k2 < list.size(); ++k2) {
			Entity entity = (Entity) list.get(k2);
			if (entity instanceof EntityTNTPrimed || entity instanceof EntityFallingEnchantedBlock) {
				double d7 = entity.getDistance(this.posX, this.posY, this.posZ) / this.explosionSize;

				if (this.explosionSize < 50 && d7 <= 1D) {
					entity.setDead();
					if (entity instanceof EntityFallingEnchantedBlock) {
						this.explosionSize += ((EntityFallingEnchantedBlock) entity).explosionSize / 2.1f;
						((EntityFallingEnchantedBlock) entity).isPrimed = false;
					} else
						this.explosionSize += 2;

				} else if (entity instanceof EntityFallingEnchantedBlock)
					if (((EntityFallingEnchantedBlock) entity).mine)
						((EntityFallingEnchantedBlock) entity).fuse *= 0;
					else
						((EntityFallingEnchantedBlock) entity).fuse *= 0.3;
			}

		}
		// System.out.println("eksplozja: "+this.explosionSize);
		EnchantedExplosion explosion = new EnchantedExplosion(this.world, this, this.posX, this.posY, this.posZ,
				this.explosionSize, dropChance, this.harmless);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		Iterator iterator = this.world.playerEntities.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();

			if (entityplayer.getDistanceSq(this.posX, this.posY, this.posZ) < 4096.0D)
				((EntityPlayerMP) entityplayer).connection
						.sendPacket(new SPacketExplosion(this.posX, this.posY, this.posZ, this.explosionSize,
								explosion.affectedBlockPositions, (Vec3d) explosion.func_77277_b().get(entityplayer)));
		}
		for (int a = 0; a < this.tntamount; a++) {
			EntityFallingEnchantedBlock block = new EntityFallingEnchantedBlock(world, this.posX, this.posY,
					this.posZ, 25 + a, false, this.explosionSize / 3, this.dropChance, this.harmless, 0);
			block.setupEntity(this.sticky, this.scale / 2, true, this.shrink, this.damage, owner, this.nogravity,
					this.knockback);
			// double maxPos=Math.max(motionX,
			// Math.max(motionY,motionZ))/*/this.explosionSize*/;
			if (this.impact)
				block.motionY += this.rand.nextFloat() * 1;
			/*
			 * block.fuse+=20; block.motionX-=motionX*0.3/maxPos;
			 * block.motionY-=motionY/maxPos; block.motionZ-=motionZ*0.3/maxPos;
			 * block.setPosition(block.posX+block.motionX*4,
			 * block.posY+block.motionY, block.posZ+block.motionZ*4);
			 * block.motionX*=this.rand.nextFloat()*0.3+0.85;
			 * block.motionY*=this.rand.nextFloat()*0.3+0.85;
			 * block.motionZ*=this.rand.nextFloat()*0.3+0.85;
			 */
			// block.nogravity=true;
			/*
			 * block.setPosition(block.posX+this.rand.nextFloat()*this.
			 * explosionSize*4-this.explosionSize*2,
			 * block.posY+this.rand.nextFloat()*this.explosionSize*4-this.
			 * explosionSize*2,
			 * block.posZ+this.rand.nextFloat()*this.explosionSize*4-this.
			 * explosionSize*2);
			 */
			else
				block.motionY += this.rand.nextFloat() * 0.5 + 0.5;
			block.motionX += this.rand.nextFloat() * 2 - 1;
			block.motionZ += this.rand.nextFloat() * 2 - 1;
			this.world.spawnEntity(block);

		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength();

		if (Double.isNaN(d0))
			d0 = 1.0D;

		d0 = d0 * 512.0D * getRenderDistanceWeight();
		return distance < d0 * d0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire() {
		return this.isFired;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeByte(this.sticky);
		buffer.writeByte((byte) this.metadata);
		buffer.writeInt(Block.getIdFromBlock(block.getBlock()));

		buffer.writeFloat(scale);
		buffer.writeShort(this.fuse);
		buffer.writeByte(this.shrink);
		buffer.writeBoolean(this.isPrimed);
		buffer.writeBoolean(this.isFired);
		buffer.writeBoolean(this.nogravity);
		buffer.writeBoolean(this.growing);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.sticky = additionalData.readByte();
		this.metadata = additionalData.readByte();
		this.block = Block.getBlockById(additionalData.readInt()).getStateFromMeta(this.metadata);

		this.scale = additionalData.readFloat();
		this.fuse = additionalData.readShort();
		this.shrink = additionalData.readByte();
		this.isPrimed = additionalData.readBoolean();
		this.isFired = additionalData.readBoolean();
		this.nogravity = additionalData.readBoolean();
		this.growing = additionalData.readBoolean();
		this.setSize(scale / (shrink * shrink), scale / (shrink * shrink));
		// this.renderDistanceWeight = 5/width;
	}
}
