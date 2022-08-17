package rafradek.TF2weapons.item;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.entity.boss.EntityHHH;
import rafradek.TF2weapons.entity.boss.EntityMerasmus;
import rafradek.TF2weapons.entity.boss.EntityMonoculus;
import rafradek.TF2weapons.entity.boss.EntityTF2Boss;

public class ItemBossSpawner extends Item {

	public static final String[] NAMES = {"hhh","monoculus","merasmus"};

	public static BlockPattern patternHHH = FactoryBlockPattern.start().aisle("NAN","BCB", "NBN").where('A',BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.LIT_PUMPKIN)))
			.where('C',BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.OBSIDIAN))).where('B',BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.HAY_BLOCK)))
			.where('N',BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();

	public ItemBossSpawner() {
		super();
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item."+NAMES[stack.getMetadata()%NAMES.length];
	}

	@Override
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		// System.out.println(this.getCreativeTab());
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		for (int i = 0; i < NAMES.length; i++)
			par3List.add(new ItemStack(this,1,i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		tooltip.add(I18n.format("item."+NAMES[stack.getMetadata()%NAMES.length]+".desc"));
	}
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote || TF2ConfigVars.disableBossSpawnItems)
			return EnumActionResult.SUCCESS;
		EntityTF2Boss boss = null;
		long time = world.getWorldTime();
		ItemStack stack= player.getHeldItem(hand);
		TF2PlayerCapability cap = TF2PlayerCapability.get(player);
		if (time % 24000 < 13500 || time % 24000 > 21500 ) {
			player.sendMessage(new TextComponentTranslation("gui.boss.night"));
			return EnumActionResult.SUCCESS;
		}

		if (stack.getItemDamage() == 0 && cap.hhhSummonedDay < time / 24000) {
			BlockPattern.PatternHelper pattern = patternHHH.match(world, pos);
			if (pattern != null) {
				for (int j = 0; j < patternHHH.getPalmLength(); ++j)
				{
					for (int k = 0; k < patternHHH.getThumbLength(); ++k)
					{
						world.setBlockState(pattern.translateOffset(j, k, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
					}
				}
				pos = pattern.translateOffset(1, 2, 0).getPos();
				boss = new EntityHHH(world);

				cap.hhhSummonedDay = (int) (time / 24000);
			}
			else {
				player.sendMessage(new TextComponentTranslation("gui.boss.statue"));
				return EnumActionResult.SUCCESS;
			}
		}
		else if (stack.getItemDamage() == 1 && cap.monoculusSummonedDay < time / 24000) {
			if (world.getBlockState(pos).getBlock() == Blocks.PORTAL) {
				boss = new EntityMonoculus(world);
				cap.monoculusSummonedDay = (int) (time / 24000);
			}
			else {
				player.sendMessage(new TextComponentTranslation("gui.boss.portal"));
				return EnumActionResult.SUCCESS;
			}
		}
		else if (stack.getItemDamage() == 2 && cap.merasmusSummonedDay < time / 24000) {
			if (world.getBlockState(pos).getBlock() == Blocks.PORTAL) {
				boss = new EntityMerasmus(world);
				cap.merasmusSummonedDay = (int) (time / 24000);
			}
			else {
				player.sendMessage(new TextComponentTranslation("gui.boss.portal"));
				return EnumActionResult.SUCCESS;
			}
		}
		else {
			player.sendMessage(new TextComponentTranslation("gui.boss.nextnight"));
		}
		if (boss != null) {
			boss.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY() + 0.05D, (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
			boss.onInitialSpawn(world.getDifficultyForLocation(pos), null);
			boss.summoned = true;
			world.spawnEntity(boss);
			stack.shrink(1);
		}
		return EnumActionResult.SUCCESS;
	}
}
