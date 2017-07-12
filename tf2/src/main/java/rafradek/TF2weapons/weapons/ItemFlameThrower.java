package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.projectiles.EntityFlame;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.projectiles.EntityRocket;
import rafradek.TF2weapons.projectiles.EntityStickybomb;
import rafradek.TF2weapons.projectiles.EntityStickProjectile;

public class ItemFlameThrower extends ItemProjectileWeapon {

	@Override
	public boolean canAltFire(World worldObj, EntityLivingBase player, ItemStack item) {
		return super.canAltFire(worldObj, player, item) && TF2Attribute.getModifier("Cannot Airblast", item, 0, player) == 0 && item.getTagCompound().getShort("reload") <= 0;
	}

	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		return super.canFire(world, living, stack);
	}

	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return 750;
	}
	
	@Override
	public boolean startUse(ItemStack stack, EntityLivingBase living, World world, int action, int newState) {
		if (world.isRemote && (newState & 1) - (action & 1) == 1 && this.canFire(world, living, stack)) {
			SoundEvent playSound = ItemFromData.getSound(stack, PropertyType.FIRE_START_SOUND);
			ClientProxy.playWeaponSound(living, playSound, false, 2, stack);
		}
		return false;
	}

	@Override
	public boolean endUse(ItemStack stack, EntityLivingBase living, World world, int action, int newState) {
		if ((action & 1) == 1) {
			if (world.isRemote)
				// System.out.println("called"+ClientProxy.fireSounds.get(living));
				if (ClientProxy.fireSounds.get(living) != null)
					// System.out.println("called2"+ClientProxy.fireSounds.get(living).type);
					ClientProxy.fireSounds.get(living).setDone();
			// Minecraft.getMinecraft().getSoundHandler().stopSound(ClientProxy.fireSounds.get(living));
			living.playSound(ItemFromData.getSound(stack, PropertyType.FIRE_STOP_SOUND), 1f, 1f);
		}
		return false;
	}

	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		if (world.isRemote && living.getCapability(TF2weapons.WEAPONS_CAP, null).fire1Cool <= 50
				&& this.canFire(world, living, stack)) {
			if (living.getCapability(TF2weapons.WEAPONS_CAP, null).startedPress()) {
				SoundEvent playSound = ItemFromData.getSound(stack, PropertyType.FIRE_START_SOUND);
				ClientProxy.playWeaponSound(living, playSound, false, 2, stack);
			}
			if (living.isInsideOfMaterial(Material.WATER))
				world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, living.posX,
						living.posY + living.getEyeHeight() - 0.1, living.posZ, living.motionX, 0.2D + living.motionY,
						living.motionZ, new int[0]);
			else {
				ClientProxy.spawnFlameParticle(world, living, 0f, false);
				ClientProxy.spawnFlameParticle(world, living, 0.5f, false);
			}
			// System.out.println("to:
			// "+ClientProxy.fireSounds.containsKey(living));
			/*
			 * if(ClientProxy.fireSounds.containsKey(living)){
			 * System.out.println("to2: "+Minecraft.getMinecraft().
			 * getSoundHandler().isSoundPlaying(ClientProxy.fireSounds.get(
			 * living))+" "+ClientProxy.fireSounds.get(living).type); }
			 */
			if (living.getCapability(TF2weapons.WEAPONS_CAP, null).getCritTime() <= 0 && (!ClientProxy.fireSounds
					.containsKey(living)
					|| !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(ClientProxy.fireSounds.get(living))
					|| (ClientProxy.fireSounds.get(living).type != 0 && ClientProxy.fireSounds.get(living).type != 2)))
				ClientProxy.playWeaponSound(living, ItemFromData.getSound(stack, PropertyType.FIRE_LOOP_SOUND), true, 0,
						stack);
			else if (living.getCapability(TF2weapons.WEAPONS_CAP, null).getCritTime() > 0
					&& (!ClientProxy.fireSounds.containsKey(living)
							|| !Minecraft.getMinecraft().getSoundHandler()
									.isSoundPlaying(ClientProxy.fireSounds.get(living))
							|| (ClientProxy.fireSounds.get(living).type != 1))) {
				ResourceLocation playSoundCrit = new ResourceLocation(
						ItemFromData.getData(stack).getString(PropertyType.FIRE_LOOP_SOUND) + ".crit");

				ClientProxy.playWeaponSound(living, SoundEvent.REGISTRY.getObject(playSoundCrit), true, 1, stack);
			}
		}
		// System.out.println("nie");
		return false;
	}

	public static boolean isPushable(EntityLivingBase living, Entity target) {
		return !(target instanceof EntityBuilding) && !(target instanceof EntityProjectileBase && ((EntityProjectileBase)target).isPushable())
				&& !(target instanceof EntityFlame) && !(target instanceof EntityArrow && target.onGround)
				&& !(target instanceof IThrowableEntity && ((IThrowableEntity) target).getThrower() == living)
				&& !TF2weapons.isOnSameTeam(living, target);
	}

	@Override
	public float getProjectileSpeed(ItemStack stack, EntityLivingBase living) {
		float speed=super.getProjectileSpeed(stack, living);
		return speed * 0.6f + TF2Attribute.getModifier("Flame Range", stack, speed * 0.4f, living);
	}
	
	@Override
	public void altUse(ItemStack stack, EntityLivingBase living, World world) {
		
		living.getCapability(TF2weapons.WEAPONS_CAP, null).fire1Cool = 750;
		if (world.isRemote) {
			if (ClientProxy.fireSounds.get(living) != null)
				ClientProxy.fireSounds.get(living).setDone();
			// Minecraft.getMinecraft().getSoundHandler().stopSound(ClientProxy.fireSounds.get(living));
			return;
		}
		if(!(living instanceof EntityPlayer && ((EntityPlayer)living).capabilities.isCreativeMode) && ItemAmmo.getAmmoAmount(living, stack)<15)
			return;
		ItemAmmo.consumeAmmoGlobal(living, stack, 15);
		// String airblastSound=getData(stack).get("Airblast
		// Sound").getString();
		TF2weapons.playSound(living, ItemFromData.getSound(stack, PropertyType.AIRBLAST_SOUND), 1f, 1f);

		Vec3d lookVec = living.getLookVec();
		Vec3d eyeVec = new Vec3d(living.posX, living.posY + living.getEyeHeight(), living.posZ);
		eyeVec.add(lookVec);
		float size = TF2Attribute.getModifier("Flame Range", stack, 5, living);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class,
				new AxisAlignedBB(eyeVec.x - size, eyeVec.y - size, eyeVec.z - size,
						eyeVec.x + size, eyeVec.y + size, eyeVec.z + size));
		// System.out.println("aiming: "+lookVec+" "+eyeVec+" "+centerVec);
		for (Entity entity : list) {
			// System.out.println("dist: "+entity.getDistanceSq(living.posX,
			// living.posY + (double)living.getEyeHeight(), living.posZ));
			if (!isPushable(living, entity)
					|| entity.getDistanceSq(living.posX, living.posY + living.getEyeHeight(), living.posZ) > size * size
					|| !TF2weapons.lookingAt(living, 60, entity.posX, entity.posY + entity.height / 2, entity.posZ))
				continue;
			if (entity instanceof IThrowableEntity && !(entity instanceof EntityStickybomb))
				((IThrowableEntity) entity).setThrower(living);
			else if (entity instanceof EntityArrow) {
				((EntityArrow) entity).shootingEntity = living;
				((EntityArrow) entity).setDamage(((EntityArrow) entity).getDamage() * 1.35);
			}
			if (entity instanceof IProjectile) {
				IProjectile proj = (IProjectile) entity;
				float speed = (float) Math.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY
						+ entity.motionZ * entity.motionZ)
						* (0.65f + TF2Attribute.getModifier("Flame Range", stack, 0.5f, living));
				List<RayTraceResult> rayTraces = TF2weapons.pierce(world, living, eyeVec.x, eyeVec.y,
						eyeVec.z, eyeVec.x + lookVec.x * 256, eyeVec.y + lookVec.y * 256,
						eyeVec.z + lookVec.z * 256, false, 0.08f, false);
				if (!rayTraces.isEmpty() && rayTraces.get(0).hitVec != null)
					// System.out.println("hit: "+mop.hitVec);
					proj.setThrowableHeading(rayTraces.get(0).hitVec.x - entity.posX,
							rayTraces.get(0).hitVec.y - entity.posY - entity.height/2, rayTraces.get(0).hitVec.z - entity.posZ,
							speed, 0);
				else
					proj.setThrowableHeading(eyeVec.x + lookVec.x * 256 - entity.posX,
							eyeVec.y + lookVec.y * 256 - entity.posY,
							eyeVec.z + lookVec.z * 256 - entity.posZ, speed, 0);
			} else {
				double mult = (entity instanceof EntityLivingBase ? 
						1-((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue() : 0.2)
						+ TF2Attribute.getModifier("Flame Range", stack, 0.8f, living);
				entity.motionX = lookVec.x * 0.6 * mult;
				entity.motionY = (lookVec.y * 0.2 + 0.36) * mult;
				entity.motionZ = lookVec.z * 0.6 * mult;
			}
			if (entity instanceof EntityProjectileBase){
				((EntityProjectileBase) entity).reflected=true;
				((EntityProjectileBase) entity).setCritical(Math.max(((EntityProjectileBase) entity).getCritical(), 1));
				if(entity instanceof EntityRocket && ((EntityRocket)entity).shootingEntity instanceof EntityPlayer){
					living.getCapability(TF2weapons.WEAPONS_CAP, null).tickAirblasted=living.ticksExisted;
				}
			}
			if (!(entity instanceof EntityLivingBase)) {
				// String throwObjectSound=getData(stack).get("Airblast Rocket
				// Sound").getString();
				entity.playSound(ItemFromData.getSound(stack, PropertyType.AIRBLAST_ROCKET_SOUND), 1.5f, 1f);
				//System.out.println("class: " + entity.getName());
			}
			if(living instanceof EntityPlayerMP){
				((EntityPlayerMP)living).addStat(TF2Achievements.PROJECTILES_REFLECTED);
				/*if(((EntityPlayerMP)living).getStatFile().readStat(TF2Achievements.PROJECTILES_REFLECTED)>=100){
					((EntityPlayerMP)living).addStat(TF2Achievements.HOT_POTATO);
				}*/
			}
			EntityTracker tracker = ((WorldServer) world).getEntityTracker();
			tracker.sendToTrackingAndSelf(entity, new SPacketEntityVelocity(entity));
			tracker.sendToTrackingAndSelf(entity, new SPacketEntityTeleport(entity));
		}
	}
	
	public void onDealDamage(ItemStack stack, EntityLivingBase attacker, Entity target, DamageSource source, float amount) {
		super.onDealDamage(stack, attacker, target, source, amount);
		
		if(target instanceof EntityLivingBase && TF2Attribute.getModifier("Rage Crit", stack, 0, attacker)!=0 && !stack.getTagCompound().getBoolean("RageActive")){
			
			attacker.getCapability(TF2weapons.WEAPONS_CAP, null).setPhlogRage(Math.min(20, attacker.getCapability(TF2weapons.WEAPONS_CAP, null).getPhlogRage()+amount
					*(target instanceof EntityPlayer?1f:TF2weapons.isEnemy(attacker, (EntityLivingBase) target)?0.4f:0.1f)));
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(stack, par2World, par3Entity, par4, par5);
		if(stack.getTagCompound().getBoolean("RageActive")) {
			par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null).setPhlogRage(par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null).getPhlogRage()-0.17f);
			if(par5 && par3Entity.ticksExisted%5==0) {
				((EntityLivingBase) par3Entity).addPotionEffect(new PotionEffect(TF2weapons.critBoost,5));
			}
			if(par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null).getPhlogRage()<=0)
				stack.getTagCompound().setBoolean("RageActive", false);
		}
			
	}
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 40;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		//stack.getTagCompound().setFloat("Rage", 0f);
		stack.getTagCompound().setBoolean("RageActive", true);
		return stack;
	}
	
	public boolean showInfoBox(ItemStack stack, EntityPlayer player){
		return super.showInfoBox(stack, player) || TF2Attribute.getModifier("Rage Crit", stack, 0, player)!=0;
	}
	
	/*public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player){
		if(TF2Attribute.getModifier("Rage Crit", stack, 0, player)==0)
			return super.getInfoBoxLines(stack, player);
		else {
			String[] result=new String[2];
			result[0]="MMMPH";
			int focus=(int) TF2Attribute.getModifier("Focus", stack, 0, player);
			if(focus!=0){
				result[0]=result[0]+" ";
				int progress=(int) (((float)player.getCapability(TF2weapons.WEAPONS_CAP, null).focusShotTicks/(float)(70-focus*23+((ItemUsable)stack.getItem()).getFiringSpeed(stack, player)/50))*3f);
				for(int i=0;i<progress && i<3;i++){
					result[0]=result[0]+"\u2588";
				}
			}
			result[1]=(int)((stack.getTagCompound().getFloat("Rage")/20f)*100)+"%";
			return result;
		}
	}*/
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return super.showDurabilityBar(stack) || (TF2Attribute.getModifier("Rage Crit", stack, 0, null)==1 
				&& Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null).getPhlogRage() < 20f);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return TF2Attribute.getModifier("Rage Crit", stack, 0, null)==1 ? (20D - Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null).getPhlogRage())/20D 
				: super.getDurabilityForDisplay(stack);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (TF2Attribute.getModifier("Rage Crit", itemStackIn, 0, playerIn)!=0 &&playerIn.getCapability(TF2weapons.WEAPONS_CAP, null).getPhlogRage()>=20f) {
			playerIn.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
	}
}
