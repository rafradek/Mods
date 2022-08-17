package rafradek.TF2weapons.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Attribute;

public class EntityGrenade extends EntityProjectileBase {

	public boolean hitGround;

	public int fuse = 46;
	private static final DataParameter<Byte> BOMB = EntityDataManager.createKey(EntityGrenade.class,
			DataSerializers.BYTE);
	public EntityGrenade(World p_i1756_1_) {
		super(p_i1756_1_);
		this.setSize(0.3f, 0.3f);
	}

	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.setSize(0.3f, 0.3f);
		int weaponmode=(int) TF2Attribute.getModifier("Weapon Mode", this.usedWeapon, 0, shooter);
		if(weaponmode==1){
			this.setBomb(1);
			this.setSize(0.7f, 0.7f);
			this.fuse=26+this.rand.nextInt(20);
			double motion=0.8f+this.rand.nextDouble()*0.55;
			this.motionX*=motion;
			this.motionY*=motion;
			this.motionZ*=motion;
		}
		else if(weaponmode==2) {
			this.setBomb(2);
			this.fuse=20-shooter.getCapability(TF2weapons.WEAPONS_CAP, null).chargeTicks;
		}
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(BOMB, (byte) 0);
	}
	
	public void setBomb(int val){
		this.dataManager.set(BOMB, (byte) val);
	}
	public int getBomb(){
		return (int)this.dataManager.get(BOMB);
	}
	@Override
	public float getPitchAddition() {
		return -3;
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {

	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		if (!this.hitGround) {
			if(getBomb()==0) {
				this.explode(mop.hitVec.x, mop.hitVec.y, mop.hitVec.z, mop.entityHit, 1);
			}
			else if (!this.hitEntities.contains(entityHit)){
				
				if (this.attackDirect(entityHit, 1, mop.hitInfo instanceof Boolean ? (Boolean)mop.hitInfo : false, mop.hitVec) && entityHit instanceof EntityLivingBase) {
					((EntityLivingBase)entityHit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,20,3));
					
				}
				
				/*if(mop.sideHit==EnumFacing.EAST || mop.sideHit==EnumFacing.WEST)
					this.onHitBlockX();
				else if(mop.sideHit==EnumFacing.NORTH || mop.sideHit==EnumFacing.SOUTH)
					this.onHitBlockZ();
				else
					this.onHitBlockY(null);*/
				this.motionX*=0.6;
				this.motionY*=0.65;
				this.motionZ*=0.6;
			}
		}
	}

	public double maxMotion() {
		return Math.max(this.motionX, Math.max(this.motionY, this.motionZ));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.fuse--;
		if (this.fuse <= 0)
			this.explode(this.posX, this.posY + this.height / 2, this.posZ, null, this.getBomb()>0?1:0.64f);
		if (this.collided) {
			this.hitGround = true;
			if (!this.world.isRemote) {
				int attr = (int) TF2Attribute.getModifier("Coll Remove", this.usedWeapon, 0, this.shootingEntity);
				if (attr == 2)
					this.explode(this.posX, this.posY, this.posZ, null, this.getBomb()>0?1:0.64f);
				if (attr == 1)
					this.setDead();
			}
		}
	}

	@Override
	public void spawnParticles(double x, double y, double z) {

	}

	@Override
	protected float getSpeed() {
		return 1.16205f;
	}

	@Override
	public double getGravity() {
		return 0.0381f;
	}

	@Override
	public boolean useCollisionBox() {
		return true;
	}

	@Override
	public void onHitBlockX() {
		this.motionX = -this.motionX * 0.18;
		this.motionY = this.motionY * 0.8;
		this.motionZ = this.motionZ * 0.8;
	}

	@Override
	public void onHitBlockY(Block block) {
		this.motionX = this.motionX * 0.8;
		this.motionY = -this.motionY * 0.18;
		this.motionZ = this.motionZ * 0.8;
	}

	@Override
	public void onHitBlockZ() {
		this.motionX = this.motionX * 0.8;
		this.motionY = this.motionY * 0.8;
		this.motionZ = -this.motionZ * 0.18;
	}
	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if(BOMB.equals(key) && this.getBomb()==1){
			this.setSize(0.7f, 0.7f);
		}
	}
}
