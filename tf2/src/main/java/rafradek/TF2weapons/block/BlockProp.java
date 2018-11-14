package rafradek.TF2weapons.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockProp extends Block {

	public static final PropertyEnum<EnumBlockType> TYPE = PropertyEnum.<EnumBlockType>create("type", EnumBlockType.class);
	
	public BlockProp(Material materialIn) {
		super(materialIn);
		// TODO Auto-generated constructor stub
	}

	public BlockProp(Material blockMaterialIn, MapColor blockMapColorIn) {
		super(blockMaterialIn, blockMapColorIn);
		// TODO Auto-generated constructor stub
	}
	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        
    }
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, EnumBlockType.values()[meta]);
    }
	public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(TYPE)).ordinal();
    }
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TYPE});
    }
	public enum EnumBlockType implements IStringSerializable{
		DIAMOND("diamond"),
		IRON("iron"),
		GOLD("gold"),
		OBSIDIAN("obsidian");
		
		private final String name;
		private EnumBlockType(String name){
			this.name=name;
		}
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}
		
	}
}
