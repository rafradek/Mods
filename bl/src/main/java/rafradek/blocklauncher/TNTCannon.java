package rafradek.blocklauncher;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TNTCannon extends Item {

	public TNTCannon() {
		this.setUnlocalizedName("blocklauncher");
		this.setCreativeTab(BlockLauncher.tabblocklauncher);
		this.setMaxStackSize(1);
		this.setMaxDamage(180);
		this.setNoRepair();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if (this.getType(stack) == 18)
			stack.getTagCompound().setInteger("explode", 2);
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World par2World, EntityPlayer player,
			EnumHand hand) {
		// System.out.println("lel "+stack.getTagCompound().getInteger("wait")+"
		// "+this.allowShot(player,stack, par2World));
		ItemStack stack=player.getHeldItem(hand);
		if (!(stack.hasTagCompound() && stack.getTagCompound().getInteger("wait") > 0)
				&& this.allowShot(player, stack, par2World)) {
			if (!this.usesBowAnimation(stack))
				this.use(stack, par2World, player, 1.8f,
						player.inventory.getStackInSlot(this.getSlotForUse(player, stack)), false);
			else
				// System.out.println("trying");
				player.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	@Override
	public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int i, boolean p_77663_5_) {
		if (!p_77663_2_.isRemote && p_77663_1_.hasTagCompound()) {
			if (p_77663_1_.getTagCompound().getInteger("explode") > 0)
				p_77663_1_.getTagCompound().setInteger("explode",
						p_77663_1_.getTagCompound().getInteger("explode") - 1);
			EntityPlayer player = (EntityPlayer) p_77663_3_;
			if (p_77663_1_.getTagCompound().getInteger("wait") > 0)
				// ((EntityPlayerMP)player).isChangingQuantityOnly=true;
				p_77663_1_.getTagCompound().setInteger("wait", p_77663_1_.getTagCompound().getInteger("wait") - 1);
			// else if (player instanceof EntityPlayerMP)
			// {
			// ((EntityPlayerMP)player).isChangingQuantityOnly=false;
			// if(p_77663_1_.getTagCompound().getInteger("wait")==0){
			// ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new
			// S2FPacketSetSlot(-1, -1, player.inventory.getItemStack()));
			// }
			// }
			if (this.isRepeatable(p_77663_1_) && i == player.inventory.currentItem
					&& p_77663_1_.getTagCompound().getInteger("repeat") > 0)
				if (p_77663_2_.getWorldTime() % 2 == 0 && this.allowShot(player, p_77663_1_, player.world)) {
					this.use(p_77663_1_, player.world, player, 1.8f,
							player.inventory.getStackInSlot(this.getSlotForUse(player, p_77663_1_)),
							!(this.getType(p_77663_1_) == 4 && p_77663_1_.getTagCompound().getInteger("repeat") == 2));
					p_77663_1_.getTagCompound().setInteger("repeat",
							p_77663_1_.getTagCompound().getInteger("repeat") - 1);
				}
		}

	}

	public boolean isRepeatable(ItemStack stack) {
		// TODO Auto-generated method stub
		return this.getType(stack) == 3 || this.getType(stack) == 4;
	}

	public int getSlotForUse(EntityPlayer player, ItemStack stack) {
		ItemStack stackToUse = player.inventory.getStackInSlot(player.inventory.currentItem + 1);
		if (stackToUse != null && stackToUse.getItem() instanceof ItemBlock
				&& this.allowBlock(stack,
						Block.getBlockFromItem(stackToUse.getItem()).getStateFromMeta(stackToUse.getMetadata()),
						player.world))
			return player.inventory.currentItem + 1;
		else
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				stackToUse = player.inventory.getStackInSlot(i);
				if (stackToUse != null && stackToUse.getItem() instanceof ItemBlock
						&& this.allowBlock(stack,
								Block.getBlockFromItem(stackToUse.getItem()).getStateFromMeta(stackToUse.getMetadata()),
								player.world))
					return i;
			}
		return -1;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityLivingBase entity, int par4) {
		if (!(entity instanceof EntityPlayer))
			return;
		int j = this.getMaxItemUseDuration(par1ItemStack) - par4;

		float f = j / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;

		if (f < 0.04D)
			return;

		if (f > 1.5F)
			f = 1.5F;
		f++;
		this.use(par1ItemStack, par2World, (EntityPlayer) entity, f * 1.8f, ((EntityPlayer) entity).inventory
				.getStackInSlot(this.getSlotForUse((EntityPlayer) entity, par1ItemStack)), false);

	}

	@Override
	public ItemStack onItemUseFinish(ItemStack par1ItemStack, World par2World, EntityLivingBase par3EntityPlayer) {
		return par1ItemStack;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 72000;
	}

	/**
	 * returns the action that specifies what animation to play when the items
	 * is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.BOW;
	}

	@SuppressWarnings("deprecation")
	public void use(ItemStack stack, World par2World, EntityPlayer player, float force, ItemStack stackToUse,
			boolean fEfficent) {
		force *= this.speedMult(stack);
		EntityFallingEnchantedBlock entity;
		float rFloat = player.getRNG().nextFloat();
		boolean efficient = fEfficent
				|| rFloat <= EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchEff, stack) * 2.5f / 10f
				|| (this.isHarmless(stack) && rFloat < 0.8f);
		boolean maineff = efficient;
		int stackSize = stackToUse.getCount();
		int waitAmount;
		Block block = Block.getBlockFromItem(stackToUse.getItem());
		IBlockState state = block.getStateFromMeta(stack.getMetadata());

		for (int i = 0; i < (this.isSpreader(stack) ? Math.min(stackSize, this.getSpreaderBlockCount(stack))
				: 1); i++) {
			if (!maineff && this.getType(stack) == 18)
				efficient = i % 2 == 0;
			if (block == Blocks.TNT && this.isActivator(stack)) {
				float radius = this.getExplosionSize(stack) * (this.biggerExplosion(stack) ? 1.4f : 1);
				int fuse = Math.max(18, (int) (50 * (this.biggerExplosion(stack) ? 1.4 : 1))) + i * 2;

				float dropChance = EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchLoot, stack) + 1;
				int tntAmount = EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchMultiple, stack) * 8;
				waitAmount = (int) (10 * this.fireRateMult(stack));
				entity = new EntityFallingEnchantedBlock(par2World, player.posX, player.posY, player.posZ, fuse,
						this.explodesOnImpact(stack), radius, dropChance, this.isHarmless(stack), tntAmount);
				if (this.getType(stack) == 18) {
					entity.fuse *= 10;
					entity.mine = true;
				}
				// entity=new EntityMinecartTNT(par2World, player.posX,
				// player.posY, player.posZ);
			} else if (!this.isActivator(stack)) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				waitAmount = (int) Math.max(BlockLauncher.getHardness(state, par2World) * this.fireRateMult(stack),
						this.fireRateMin(stack));
				entity = new EntityFallingEnchantedBlock(par2World, player.posX, player.posY, player.posZ,
						Block.getBlockFromItem(stackToUse.getItem()).getStateFromMeta(stackToUse.getItemDamage()));
				if (this.getType(stack) == 3) {
					entity.isFired = true;
					entity.fireBlock = Blocks.FIRE;
					if (EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchFire, stack) == 1)
						entity.fireBetter = true;
				}
				if (this.getType(stack) == 5)
					entity.growing = true;
				entity.preventEntitySpawning = false;
				// System.out.println(this.isSticky(stack)?1:(this.isBouncy(stack)?2:0));
			} else
				return;
			// if (player instanceof EntityPlayerMP){
			// ((EntityPlayerMP)player).isChangingQuantityOnly=true;
			// }
			int shrink = EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchShrink, stack) + 1;

			entity.setupEntity(this.isSticky(stack) ? 1 : (this.isBouncy(stack) ? 2 : this.isCrushing(stack) ? 3 : 0), this.getScale(stack),
					efficient, shrink, this.damageMult(stack), player, this.noGravity(stack),
					this.knockbackMult(stack));
			stack.getTagCompound().setInteger("wait", waitAmount);
			if (this.isRepeatable(stack) && stack.getTagCompound().getInteger("repeat") == 0)
				stack.getTagCompound().setInteger("repeat", 3);
			player.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!par2World.isRemote) {
				/*
				 * if(reversed && this.getRayTraceResultFromPlayer(par2World,
				 * player, true) != null){ double
				 * x=this.getRayTraceResultFromPlayer(par2World, player,
				 * true).blockX; double
				 * y=this.getRayTraceResultFromPlayer(par2World, player,
				 * true).blockY+0.8; double
				 * z=this.getRayTraceResultFromPlayer(par2World, player,
				 * true).blockZ; entity.setLocationAndAngles(x,y,z,
				 * player.rotationYaw, player.rotationPitch*-1); } else{
				 */
				entity.setLocationAndAngles(player.posX, player.posY + player.getEyeHeight(), player.posZ,
						player.rotationYaw, player.rotationPitch);

				if (this.getType(stack) != 5) {
					entity.posY -= 0.5D;
					entity.posX -= MathHelper.cos(entity.rotationYaw / 180.0F * (float) Math.PI) * 0.3F;
					entity.posZ -= MathHelper.sin(entity.rotationYaw / 180.0F * (float) Math.PI) * 0.3F;

				} else {
					entity.posX += -MathHelper.sin(entity.rotationYaw / 180.0F * (float) Math.PI)
							/* MathHelper.cos(0 / 180.0F * (float)Math.PI)) */ * 0.5f;
					entity.posZ += MathHelper.cos(entity.rotationYaw / 180.0F * (float) Math.PI)
							/* MathHelper.cos(0 / 180.0F * (float)Math.PI)) */ * 0.5f;
				}
				entity.setPosition(entity.posX, entity.posY, entity.posZ);
				entity.motionX = -MathHelper.sin(entity.rotationYaw / 180.0F * (float) Math.PI)
						* MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI);
				entity.motionZ = MathHelper.cos(entity.rotationYaw / 180.0F * (float) Math.PI)
						* MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI);
				entity.motionY = (-MathHelper.sin(entity.rotationPitch / 180.0F * (float) Math.PI));
				float f2 = MathHelper.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY
						+ entity.motionZ * entity.motionZ);
				Random random = new Random();
				entity.motionX /= f2;
				entity.motionY /= f2;
				entity.motionZ /= f2;
				double spread = this.getSpread(stack, i);
				entity.motionX += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * spread;
				entity.motionY += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * spread;
				entity.motionZ += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * spread;
				entity.motionX *= force;
				entity.motionY *= force;
				entity.motionZ *= force;
				float f3 = MathHelper.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
				entity.prevRotationYaw = entity.rotationYaw = (float) (Math.atan2(entity.motionX, entity.motionZ)
						* 180.0D / Math.PI);
				entity.prevRotationPitch = entity.rotationPitch = (float) (Math.atan2(entity.motionY, f3) * 180.0D
						/ Math.PI);
				par2World.spawnEntity(entity);
				// entity.setFire(99999);
				if (!player.capabilities.isCreativeMode && !efficient) {
					stackToUse.shrink(1);
					if (stackToUse.getCount() < 1)
						player.inventory.setInventorySlotContents(this.getSlotForUse(player, stack), ItemStack.EMPTY);//
				}
			}
		}
		stack.damageItem(1, player);
	}

	private float knockbackMult(ItemStack stack) {

		if (this.getType(stack) == 1)
			return 1.95f;
		else if (this.getType(stack) == 4)
			return 0.15f;
		return 1;
	}

	public float getScale(ItemStack stack) {
		float base = 1;
		if (this.getType(stack) == 0)
			base = 0.6f;
		if (this.getType(stack) == 4)
			base = 0.3f;
		if (this.getType(stack) == 5)
			base = 0.15f;
		else if (this.getType(stack) == 2 || this.getType(stack) == 3)
			base = 0.4f;
		return base * (1 + EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchShrink, stack) * 0.33f);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par2List, boolean par4) {
		if (this.isSticky(stack))
			par2List.add("Sticky");
		if (this.isBouncy(stack))
			par2List.add("Bouncing");
		if (this.isHarmless(stack))
			par2List.add("Harmless");
		if (this.isCrushing(stack))
			par2List.add("Crushing");
		if (this.firesEntireStack(stack))
			par2List.add("Super Spread");
	}

	public float getSpread(ItemStack stack, int i) {
		float base = 0.0075f;
		if (this.getType(stack) == 2)
			base = 0.012f;
		else if (this.getType(stack) == 3)
			base = 0.13f;
		else if (this.getType(stack) == 4)
			base = 0.032f;
		else if (this.getType(stack) == 18)
			base = 0.016f;
		if (this.isSpreader(stack)) {
			if (this.firesEntireStack(stack))
				base += 0.01 * this.getSpreaderBlockCount(stack);
			base += 0.01 * Math.min(i, this.getSpreaderBlockCount(stack));
		}
		return base;
	}

	public boolean noGravity(ItemStack stack) {
		return EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchGravity, stack) > 0;
	}

	public boolean isHarmless(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Harmless");
	}

	public boolean firesEntireStack(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Stack");
	}

	public boolean isSticky(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Sticky");
	}

	public boolean isBouncy(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Bouncy");
	}

	public boolean isCrushing(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Crushing");
	}
	
	public boolean isActivator(ItemStack stack) {
		return this.getType(stack) > 15;
	}

	public boolean usesBowAnimation(ItemStack stack) {
		return this.getType(stack) == 1 || this.getType(stack) == 5 || this.getType(stack) == 17;
	}

	public boolean biggerExplosion(ItemStack stack) {
		return false;
	}

	public boolean explodesOnImpact(ItemStack stack) {
		return this.getType(stack) == 17;
	}

	public boolean isSpreader(ItemStack stack) {
		return this.getType(stack) == 2 || this.getType(stack) == 18 || (this.getType(stack) == 4
				&& EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchHeavy, stack) == 2);
	}

	public int getSpreaderBlockCount(ItemStack stack) {
		if (this.firesEntireStack(stack))
			return 64;
		if (this.getType(stack) == 2)
			return 8;
		else if (this.getType(stack) == 18)
			return 4;
		else if (this.getType(stack) == 4)
			return 2;
		return 1;
	}

	public float getExplosionSize(ItemStack stack) {
		float base = 4;
		if (this.getType(stack) == 17)
			base = 3;
		else if (this.getType(stack) == 18)
			base = 2.4f;
		return base + EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchPower, stack) * base / 4;
	}

	public int getType(ItemStack stack) {
		return stack.getTagCompound() != null ? stack.getTagCompound().getInteger("Type") : 0;
	}

	public float fireRateMult(ItemStack stack) {
		float base = 1;
		if (this.getType(stack) == 0)
			base = 2.7f;
		if (this.getType(stack) == 2)
			base = 10f;
		if (this.getType(stack) == 17)
			base = 3.7f;
		if (this.getType(stack) == 18)
			base = 2f;
		return (base - EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchHeavy, stack) * 0.2f)
				* (this.isHarmless(stack) ? 0.2f : 1);
	}

	public float speedMult(ItemStack stack) {
		float base = 1;
		if (this.getType(stack) == 17)
			base= 1.5f;
		if (this.getType(stack) == 18)
			base= 1.25f;
		else if (this.getType(stack) == 3)
			base= 0.6f;
		else if (this.getType(stack) == 4)
			base= 1.25f;
		else if (this.getType(stack) == 5)
			base= 1.5f;
		if (isCrushing(stack))
			base*=1.4f;
		return base * (1 + EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchPower, stack) * 0.33f);
	}

	public int fireRateMin(ItemStack stack) {
		int base = this.getType(stack) == 1 ? 25 : (this.getType(stack) == 2 ? 15 : this.getType(stack) == 5 ? 30 : 0);
		return base - EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchHeavy, stack) * 2;
	}

	public float damageMult(ItemStack stack) {
		return isCrushing(stack)?0.55f:1f;
	}

	public boolean allowShot(EntityPlayer player, ItemStack stack, World world) {
		return this.getSlotForUse(player, stack) != -1;
	}

	public boolean allowBlock(ItemStack stack, IBlockState block, World world) {
		float speed = BlockLauncher.getHardness(block, world);
		float mult = 1 + EnchantmentHelper.getEnchantmentLevel(BlockLauncher.enchHeavy, stack) * 0.6F;
		if (this.getType(stack) == 0)
			return speed <= 2.2f * mult;
		else if (this.getType(stack) == 2)
			return speed <= 2.0f * mult;
		else if (this.getType(stack) == 1)
			return speed >= 2.2f / mult;
		else if (this.getType(stack) == 4)
			return speed <= 2.2f * mult;
		else if (this.getType(stack) == 5)
			return speed >= 2.2f * mult;
		else if (this.getType(stack) == 3)
			return block.getMaterial().getCanBurn() && block.getBlock() != Blocks.TNT;
		else if (this.getType(stack) > 15)
			return block.getBlock() == Blocks.TNT;
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		if (this.getType(stack) == 0)
			return 575;
		else if (this.getType(stack) == 1)
			return 115;
		else if (this.getType(stack) == 2)
			return 265;
		else if (this.getType(stack) == 3)
			return 1800;
		else if (this.getType(stack) == 4)
			return 4200;
		else if (this.getType(stack) == 5)
			return 500;
		else if (this.getType(stack) == 16)
			return 350;
		else if (this.getType(stack) == 17)
			return 200;
		else if (this.getType(stack) == 18)
			return 1400;
		return getMaxDamage();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		for (int i = 0; i < 19; i++) {
			if (i == 6)
				i = 16;
			ItemStack stack = new ItemStack(par1);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Type", i);
			par3List.add(stack);
			ItemStack stack2 = stack.copy();
			stack2.getTagCompound().setBoolean("Sticky", true);
			par3List.add(stack2);
			ItemStack stack3 = stack.copy();
			stack3.getTagCompound().setBoolean("Bouncy", true);
			par3List.add(stack3);
			if(i < 16){
				ItemStack stack6 = stack.copy();
				stack6.getTagCompound().setBoolean("Crushing", true);
				par3List.add(stack6);
			}
			if (this.isActivator(stack)) {
				ItemStack stack4 = stack.copy();
				stack4.getTagCompound().setBoolean("Harmless", true);
				par3List.add(stack4);
			}
			if (i == 2 || i == 18) {
				ItemStack stack5 = stack.copy();
				stack5.getTagCompound().setBoolean("Stack", true);
				par3List.add(stack5);
			}
		}
		/*
		 * ItemStack[] enchantedStacks=new ItemStack[12]; for(int i=0;
		 * i<enchantedStacks.length; i++){ enchantedStacks[i]=stack.copy();
		 * if(i==0 || i==3 || i>6){
		 * enchantedStacks[i].getTagCompound().setBoolean("Sticky", true); }
		 * if(i==1 || i>3){
		 * enchantedStacks[i].getTagCompound().setBoolean("Activator", true); }
		 * if(i==2 || i==3 || i>7){
		 * enchantedStacks[i].getTagCompound().setBoolean("BowLike", true); }
		 * if(i==4 || i==6 || i==7 || i==9 || i==11){
		 * enchantedStacks[i].getTagCompound().setBoolean("Powder", true); }
		 * if(i==5 || i==6 || i>9){
		 * enchantedStacks[i].getTagCompound().setBoolean("Glowstone", true); }
		 * par3List.add(enchantedStacks[i]); }
		 */
	}

	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		int type = this.getType(p_77667_1_);
		if (type == 0)
			return "item.blockrifle";
		else if (type == 1)
			return "item.blockcannon";
		else if (type == 2)
			return "item.shotblock";
		else if (type == 3)
			return "item.blockthrower";
		else if (type == 4)
			return "item.chainblock";
		else if (type == 5)
			return "item.sniperblock";
		else if (type == 16)
			return "item.TNTcannon";
		else if (type == 17)
			return "item.TNTlauncher";
		else if (type == 18)
			return "item.TNTmultilauncher";
		return super.getUnlocalizedName();
	}

	@Override
	public int getItemEnchantability() {
		return 10;
	}
}
