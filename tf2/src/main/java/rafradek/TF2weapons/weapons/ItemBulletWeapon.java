package rafradek.TF2weapons.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicates;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2DamageSource;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;
import rafradek.TF2weapons.projectiles.EntityFlame;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;

public class ItemBulletWeapon extends ItemWeapon {
	public static double lastStartX = 90;
	public static double lastStartY = 90;
	public static double lastStartZ = 90;
	public static double lastEndX = 990;
	public static double lastEndY = 900;
	public static double lastEndZ = 900;
	public static HashMap<Entity, float[]> lastShot = new HashMap<Entity, float[]>();
	public static ArrayList<RayTraceResult> lastShotClient = new ArrayList<RayTraceResult>();
	public static boolean processShotServer;
	public static EntityLivingBase dummyEnt = new EntityCreeper(null);

	public void handleShoot(EntityLivingBase living, ItemStack stack, World world, HashMap<Entity, float[]> map,
			int critical, int flags) {
		DamageSource var22 = TF2Util.causeDirectDamage(stack, living, critical);
		((TF2DamageSource)var22).addAttackFlag(flags);
		
		if (!(this instanceof ItemMeleeWeapon))
			var22.setProjectile();

		Iterator<Entity> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (!((ItemWeapon) stack.getItem()).onHit(stack, living, entity, map.get(entity)[1], critical, false))
				continue;
			Vec3d pushvec = entity.getPositionVector().subtract(living.getPositionVector()).normalize();
			
			if (map.get(entity) != null && map.get(entity)[1] != 0
					&& TF2Util.dealDamage(entity, world, living, stack, critical, map.get(entity)[1], var22)) {
				// System.out.println("Damage: "+map.get(entity)[1]);
				//distance = ((ItemBulletWeapon) stack.getItem()).getMaxRange(stack) / distance;
				/*double distX = (living.posX - entity.posX) * distance;
				double distY = (living.posY - entity.posY) * distance;
				double distZ = (living.posZ - entity.posZ) * distance;*/
				if (!stack.isEmpty()) {
					double knockbackAmount = ((ItemBulletWeapon) stack.getItem()).getWeaponKnockback(stack, living)
							* map.get(entity)[1] * 0.01625D;

					if(entity instanceof EntityLivingBase)
						knockbackAmount *= 1-((EntityLivingBase) entity).getAttributeMap().getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE)
						.getAttributeValue();
					if (knockbackAmount > 0){
						boolean flag=map.get(entity)[1] >= 3.75 && living.getCapability(TF2weapons.WEAPONS_CAP, null).fanCool<=0&&TF2Attribute.getModifier("KnockbackFAN", stack, 0, living) != 0;
						pushvec=new Vec3d(pushvec.x * knockbackAmount * (flag?2.8:1), (pushvec.y+(flag?1:0)) * knockbackAmount,
								pushvec.z * knockbackAmount * (flag?2.8:1));
						entity.addVelocity(pushvec.x,pushvec.y,pushvec.z);
						entity.isAirBorne = entity.isAirBorne || -(pushvec.y * knockbackAmount) > 0.02D;
						if(entity instanceof EntityPlayerMP)
							TF2weapons.network.sendTo(new TF2Message.VelocityAddMessage(pushvec,entity.isAirBorne), (EntityPlayerMP) entity);
					}
				}
			}
		}
		map.clear();

	}

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		if (world.isRemote && living == ClientProxy.getLocalPlayer())
			lastShotClient.clear();
		super.use(stack, living, world, hand, message);
		if (world.isRemote && living == ClientProxy.getLocalPlayer()) {
			ClientProxy.getLocalPlayer().getCapability(TF2weapons.WEAPONS_CAP, null).recoil += getData(stack)
					.getFloat(PropertyType.RECOIL);
			message.target = lastShotClient;
		} else if (!world.isRemote)
			if (living instanceof EntityPlayer) {
				// System.out.println("Shoot: "+message.readData);
				if (message.readData == null)
					return false;
				int flags=0;
				int totalCrit = TF2Util.calculateCritPre(stack, living);
				HashMap<Entity, float[]> shotInfo = new HashMap<Entity, float[]>();
				for (Object[] obj : message.readData) {
					Entity target = world.getEntityByID((Integer) obj[0]);
					if (target == null)
						continue;

					if (!shotInfo.containsKey(target) || shotInfo.get(target) == null)
						shotInfo.put(target, new float[3]);
					int critical = totalCrit;
					// System.out.println(var4.hitInfo);
					if ((Boolean) obj[1]) {
						critical = 2;
						flags+=TF2DamageSource.HEADSHOT;
					}
					critical = this.setCritical(stack, living, target, critical);
					if (critical > totalCrit)
						totalCrit = critical;
					// ItemRangedWeapon.critical=critical;
					float[] values = shotInfo.get(target);
					// System.out.println(obj[2]+" "+critical);
					values[0]++;
					values[1] += TF2Util.calculateDamage(target, world, living, stack, critical, (Float) obj[2]);
				}
				
				// living.getCapability(TF2weapons.WEAPONS_CAP,
				// null).predictionList.add(message);
				handleShoot(living, stack, world, shotInfo, totalCrit,flags);
			} else {
				handleShoot(living, stack, world, lastShot, critical,0);
				lastShot.clear();
			}
		if (living.getCapability(TF2weapons.WEAPONS_CAP, null).fanCool<=0 && TF2Attribute.getModifier("KnockbackFAN", stack, 0, living)!=0){
			living.getCapability(TF2weapons.WEAPONS_CAP, null).fanCool=30;
		}
		return true;
		// if(world.isRemote) return false;
		/*
		 * if(((!world.isRemote && (processShotServer||!(living instanceof
		 * EntityPlayer)))||(world.isRemote&&living instanceof EntityPlayer)) &&
		 * super.use(stack, living, world, hand)){
		 * //System.out.println(world.isRemote+" "+stack.getTagCompound().
		 * getShort("reload")+" "+TF2ActionHandler.playerAction.get(world.
		 * isRemote).get(living));
		 * 
		 * if(!world.isRemote && living != null&& !processShotServer) {
		 * handleShoot(living, stack, world, lastShot,critical); } else
		 * if(world.isRemote&&living==Minecraft.getMinecraft().player){
		 * 
		 * //TF2weapons.network.sendToServer(new
		 * BulletMessage(Minecraft.getMinecraft().player.inventory.
		 * currentItem,lastShotClient, hand)); //lastShotClient.clear(); }
		 * 
		 * return ; }
		 */
	}

	public boolean showTracer(ItemStack stack) {
		return true;
	}
	
	public boolean showSpecialTracer(ItemStack stack) {
		return false;
	}
	
	@Override
	public void shoot(ItemStack stack, EntityLivingBase living, World world, int critical, EnumHand hand) {

		if (getData(stack).hasProperty(PropertyType.PROJECTILE)) {
			if(!world.isRemote) {
				new ItemProjectileWeapon().shoot(stack, living, world, critical, hand);
			}
			return;
		}
		boolean removeBlocks = TF2Attribute.getModifier("Destroy Block", stack, 0, living) > 0;
		if (!world.isRemote && living instanceof EntityPlayer && !removeBlocks)
			return;
		double startX = 0;
		double startY = 0;
		double startZ = 0;

		double endX = 0;
		double endY = 0;
		double endZ = 0;

		Vec3d rand = TF2Util.radiusRandom3D(this.getWeaponSpread(stack, living), world.rand);

		// if(target==null){
		startX = living.posX;// - (double)(MathHelper.cos(living.rotationYaw /
								// 180.0F * (float)Math.PI) * 0.16F);
		startY = living.posY + living.getEyeHeight();
		startZ = living.posZ;// - (double)(MathHelper.sin(living.rotationYaw /
								// 180.0F * (float)Math.PI) * 0.16F);

		// double[] rand=TF2weapons.radiusRandom2D(this.getWeaponSpread(stack),
		// world.rand);

		// float spreadPitch = (float) (living.rotationPitch / 180 + rand[1]);
		// float spreadYaw = (float) (living.rotationYaw / 180 +
		// rand[0]*(90/Math.max(90-Math.abs(spreadPitch*180),0.0001f)));
		// System.out.println("Rot: "+living.rotationYawHead+"
		// "+living.rotationPitch);
		float spreadPitch = living.rotationPitch / 180;
		float spreadYaw = living.rotationYawHead / 180;

		endX = -MathHelper.sin(spreadYaw * (float) Math.PI) * MathHelper.cos(spreadPitch * (float) Math.PI);
		endY = (-MathHelper.sin(spreadPitch * (float) Math.PI));
		endZ = MathHelper.cos(spreadYaw * (float) Math.PI) * MathHelper.cos(spreadPitch * (float) Math.PI);

		float var9 = MathHelper.sqrt(endX * endX + endY * endY + endZ * endZ);
		// float[] ratioX= this.calculateRatioX(living.rotationYaw,
		// living.rotationPitch);
		// float[] ratioY= this.calculateRatioY(living.rotationYaw,
		// living.rotationPitch);
		// float
		// wrapAngledYaw=MathHelper.wrapAngleTo180_float(living.rotationYaw);
		// float
		// fixedYaw=Math.max(Math.abs(wrapAngledYaw),90)-Math.min(Math.abs(wrapAngledYaw),90);

		endX = (endX / var9 + rand.x)
				* getMaxRange(stack) /*
								 * +
								 * (rand[0]*ratioX[0])((fixedYaw/90)+(1-fixedYaw
								 * /90)*(-living.rotationPitch/90))*this.
								 * positive(wrapAngledYaw)*40
								 */;
		endY = (endY / var9 + rand.y)
				* getMaxRange(stack) /*
								 * +
								 * (rand[1]*ratioY[1])(0.5-Math.abs(spreadPitch)
								 * )*80*40
								 */;
		endZ = (endZ / var9 + rand.z)
				* getMaxRange(stack) /*
								 * + ((ratioX[2]>ratioY[2]?rand[0]:rand[1])*(
								 * ratioX[2]+ratioY[2]))(rand[0]*ratioX[2] +
								 * rand[1]*ratioY[2])((1-fixedYaw/90)+(fixedYaw/
								 * 90)*(-living.rotationPitch/90))*this.positive
								 * (wrapAngledYaw)*40
								 */;
		double distanceMax = getMaxRange(stack) / Math.sqrt(endX * endX + endY * endY + endZ * endZ);
		// System.out.println(ratioX[0]+" "+ratioX[1]+" "+ratioX[2]+"
		// "+ratioY[0]+" "+ratioY[1]+" "+ratioY[2]);

		endX *= distanceMax;
		endY *= distanceMax;
		endZ *= distanceMax;
		endX += startX;
		endY += startY;
		endZ += startZ;

		/*
		 * } else { startY = living.posY + (double)living.getEyeHeight() -
		 * 0.10000000149011612D; endX = target.posX - living.posX; double var8 =
		 * target.posY + (double)target.getEyeHeight() - 0.699999988079071D -
		 * startY; endZ = target.posZ - living.posZ; double var12 =
		 * (double)MathHelper.sqrt(endX * endX + endZ * endZ);
		 * 
		 * if (var12 >= 1.0E-7D) { float var14 = (float)(Math.atan2(endZ, endX)
		 * * 180.0D / Math.PI) - 90.0F; float var15 = (float)(-(Math.atan2(var8,
		 * var12) * 180.0D / Math.PI)); double var16 = endX / var12; double
		 * var18 = endZ / var12; startX=living.posX + var16; startZ=living.posZ
		 * + var18; float var20 = (float)var12 * 0.2F;
		 * 
		 * endY=var8 + (double)var20;
		 * 
		 * float var9 = MathHelper.sqrt(endX * endX + endY * endY + endZ
		 * * endZ); endX = (endX / (double)var9 + rand[0]) * getMaxRange(); endY
		 * = (endY / (double)var9 + rand[1]) * getMaxRange(); endZ = (endZ /
		 * (double)var9 + rand[2]) * getMaxRange();
		 * 
		 * double distance=getMaxRange()/Math.sqrt(Math.pow(endX,
		 * 2)+Math.pow(endY, 2)+Math.pow(endZ,2));
		 * 
		 * endX *= distance; endY *= distance; endZ *= distance; endX += startX;
		 * endY += startY; endZ += startZ; } }
		 */
		if (world.isRemote) {
			if (this.showTracer(stack)) {
				float mult = hand == EnumHand.MAIN_HAND ? 1 : -1;
				ClientProxy.spawnBulletParticle(world, living,
						startX - MathHelper.cos(living.rotationYaw / 180.0F * (float) Math.PI) * 0.16F * mult,
						startY - 0.1,
						startZ - MathHelper.sin(living.rotationYaw / 180.0F * (float) Math.PI) * 0.16F * mult, endX,
						endY, endZ, 20, this.showSpecialTracer(stack) ? 2 : critical, 0, this.showSpecialTracer(stack) ? 10000f : 1f);
			}
			if (living != Minecraft.getMinecraft().player)
				return;
		}
		// System.out.println(startX+" "+startY+" "+startZ+" "+endX+" "+endY+"
		// "+endZ);
		
		List<RayTraceResult> list = TF2Util.pierce(world, living, startX, startY, startZ, endX, endY, endZ,
				this.canHeadshot(living, stack), this.getBulletSize(stack, living), this.canPenetrate(stack, living), 
				TF2Attribute.getModifier("Destroy Projectiles", stack, 0, living) == 0 ? TF2Util.TARGETABLE : Predicates.or(target ->{
					return target instanceof EntityProjectileBase && !(target instanceof EntityFlame);
				}, TF2Util.TARGETABLE));
		for (RayTraceResult var4 : list)
			if (var4.entityHit != null) {
				float distance = 0;
				if (living != null) {
					distance = (float) TF2Util.getDistanceBox(living, var4.entityHit.posX, var4.entityHit.posY, var4.entityHit.posZ, var4.entityHit.width,var4.entityHit.height);
					//distance -= living.width / 2 + var4.entityHit.width / 2;

					//if (distance < 0)
						//distance = 0;
				}
				if (!world.isRemote && !(living instanceof EntityPlayer)) {
					if (!lastShot.containsKey(var4.entityHit) || lastShot.get(var4.entityHit) == null)
						lastShot.put(var4.entityHit, new float[3]);
					// System.out.println(var4.hitInfo);
					if (var4.hitInfo != null && var4.hitInfo instanceof Boolean && (Boolean)var4.hitInfo) {
						critical = 2;
						ItemWeapon.critical = 2;
					}
					critical = this.setCritical(stack, living, var4.entityHit, critical);
					ItemWeapon.critical = critical;
					float[] values = lastShot.get(var4.entityHit);
					values[0]++;
					values[1] += TF2Util.calculateDamage(var4.entityHit, world, living, stack, critical, distance);
					// values[2]=distance;
				} else if (world.isRemote) {
					// System.out.println(var4.hitInfo);
					var4.hitInfo = new float[] { var4.hitInfo != null && var4.hitInfo instanceof Boolean && (Boolean)var4.hitInfo ? 1 : 0, distance };
					lastShotClient.add(var4);
				}
			} else if (var4.getBlockPos() != null)
				if (world.isRemote) {
					
					ClientProxy.spawnBulletHoleParticle(world, var4);
					for(int i=0;i<2;i++)
						world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, var4.hitVec.x+var4.sideHit.getFrontOffsetX()*0.05, var4.hitVec.y+var4.sideHit.getFrontOffsetY()*0.05, var4.hitVec.z+var4.sideHit.getFrontOffsetZ()*0.05, var4.sideHit.getFrontOffsetX()*0.07*i, var4.sideHit.getFrontOffsetY()*0.07*i, var4.sideHit.getFrontOffsetZ()*0.07*i, new int[]{Block.getStateId(world.getBlockState(var4.getBlockPos()))});
					if(getData(stack).hasProperty(PropertyType.HIT_SOUND)){
						SoundEvent event = getData(stack).hasProperty(PropertyType.HIT_WORLD_SOUND)
								? getSound(stack, PropertyType.HIT_WORLD_SOUND) : getSound(stack, PropertyType.HIT_SOUND);
						world.playSound(var4.hitVec.x, var4.hitVec.y, var4.hitVec.z, event,
								SoundCategory.PLAYERS, getData(stack).getName().equals("fryingpan") ? 2f : 0.7f, 1f, false);
					}
				} else if (!world.isRemote) {
					if(TF2Attribute.getModifier("Explode Bullet", stack, 0, living)!=0){
						TF2Util.explosion(world, living, stack, living, null, var4.hitVec.x + var4.sideHit.getFrontOffsetX() * 0.05,
								var4.hitVec.y + var4.sideHit.getFrontOffsetY() * 0.05,
								var4.hitVec.z + var4.sideHit.getFrontOffsetZ() * 0.05, TF2Attribute.getModifier("Explode Bullet", stack, 0, living), 1, critical, (float) living.getPositionVector().distanceTo(var4.hitVec));
					}
					else if(removeBlocks){
						float damage = TF2Util.calculateDamage(TF2weapons.dummyEnt, world, living, stack, critical,
								(float) living.getPositionVector().distanceTo(var4.hitVec));
						if (stack.getItem() instanceof ItemSniperRifle)
							damage *= 2.52f;
						damage *= TF2Attribute.getModifier("Destroy Block", stack, 0, living);
						TF2Util.damageBlock(var4.getBlockPos(), living, world, stack, critical, damage,
								new Vec3d(endX, endY, endZ), null);
					}
				}
	}

	/*
	 * public boolean checkHeadshot(World world, Entity living, ItemStack stack,
	 * Vec3d hitVec) { double ymax=living.getEntityBoundingBox().maxY;
	 * AxisAlignedBB head=AxisAlignedBB.getBoundingBox(living.posX-0.21,
	 * ymax-0.21, living.posZ-0.21,living.posX+0.21, ymax+0.21,
	 * living.posZ+0.21);
	 * System.out.println("Trafienie: "+Math.abs(ymax-hitVec.y));
	 * 
	 * return Math.abs(ymax-hitVec.y)<0.205; }
	 */
	

	public float[] calculateRatioX(float yaw, float pitch) {
		float[] result = new float[3];
		float angledYaw = Math.abs(MathHelper.wrapDegrees(yaw));
		float distanceYaw = Math.max(angledYaw, 90) - Math.min(angledYaw, 90);
		result[0] = (distanceYaw / 90) + (1 - distanceYaw / 90) * (-pitch / 90);
		result[2] = (1 - distanceYaw / 90);// +(1-distanceYaw/90)*(-pitch/90);
		result[1] = 0;
		return result;
	}

	public float[] calculateRatioY(float yaw, float pitch) {
		float[] result = new float[3];
		float angledYaw = Math.abs(MathHelper.wrapDegrees(yaw));
		float distanceYaw = Math.max(angledYaw, 90) - Math.min(angledYaw, 90);
		result[0] = 0;
		result[2] = (distanceYaw / 90) * (-pitch / 90);
		result[1] = 1 - Math.abs(pitch) / 90;
		return result;
	}

	public float getMaxRange(ItemStack stack) {
		return 256;
	}

	public float getBulletSize(ItemStack stack, EntityLivingBase living) {
		return 0.04f/this.getWeaponPelletCount(stack, living);
	}

	public int setCritical(ItemStack stack, EntityLivingBase shooter, Entity target, int old) {
		return TF2Util.calculateCritPost(target, shooter, old, stack);
	}
}
