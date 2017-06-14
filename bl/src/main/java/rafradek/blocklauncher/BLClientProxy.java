package rafradek.blocklauncher;

import java.util.HashMap;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class BLClientProxy extends BLCommonProxy {
	public static HashMap<String, ModelResourceLocation> models;

	@Override
	public void registerRender() {
		models = new HashMap<String, ModelResourceLocation>();
		String[] modelPaths = new String[] { "rafradek_blocklauncher:block_cannon",
				"rafradek_blocklauncher:block_rifle", "rafradek_blocklauncher:block_shotgun",
				"rafradek_blocklauncher:block_thrower", "rafradek_blocklauncher:tnt_launcher",
				"rafradek_blocklauncher:tnt_launcher_2", "rafradek_blocklauncher:block_chaingun",
				"rafradek_blocklauncher:block_sniper", "rafradek_blocklauncher:tnt_mines" };

		for (String string : modelPaths) {
			ModelResourceLocation model = new ModelResourceLocation(string, "inventory");
			models.put(string, model);
			ModelBakery.registerItemVariants(BlockLauncher.cannon, model);
		}

		ModelLoader.setCustomMeshDefinition(BlockLauncher.cannon, new ItemMeshDefinition() {

			@Override
			public ModelResourceLocation getModelLocation(ItemStack p_178113_1_) {

				int type = BlockLauncher.cannon.getType(p_178113_1_);
				if (type == 0)
					return models.get("rafradek_blocklauncher:block_rifle");
				else if (type == 1)
					return models.get("rafradek_blocklauncher:block_cannon");
				else if (type == 2)
					return models.get("rafradek_blocklauncher:block_shotgun");
				else if (type == 3)
					return models.get("rafradek_blocklauncher:block_thrower");
				else if (type == 4)
					return models.get("rafradek_blocklauncher:block_chaingun");
				else if (type == 5)
					return models.get("rafradek_blocklauncher:block_sniper");
				else if (type == 16)
					return models.get("rafradek_blocklauncher:tnt_launcher");
				else if (type == 17)
					// if(stack.getTagCompound().getInteger("wait")<=0)
					return models.get("rafradek_blocklauncher:tnt_launcher_2");
				// else
				// return this.tntlauncherEmptyIcon;
				else if (type == 18)
					// if(stack.getTagCompound().getInteger("wait")<=0)
					return models.get("rafradek_blocklauncher:tnt_mines");
				// else
				// return this.tntlauncherEmptyIcon;
				return models.get("rafradek_blocklauncher:block_cannon");
			}

		});
		// ModelBakery.addVariantName(BlockLauncher.launchpart,
		// "rafradek_blocklauncher:launchpart","rafradek_blocklauncher:launchpart_better");
		ModelLoader.setCustomModelResourceLocation(BlockLauncher.launchpart, 0,
				new ModelResourceLocation("rafradek_blocklauncher:launchpart", "inventory"));
		ModelLoader.setCustomModelResourceLocation(BlockLauncher.launchpartBetter, 0,
				new ModelResourceLocation("rafradek_blocklauncher:launchpart_better", "inventory"));
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingEnchantedBlock.class,
				new IRenderFactory<EntityFallingEnchantedBlock>() {

					@Override
					public RenderFallingBlock createRenderFor(RenderManager manager) {
						// TODO Auto-generated method stub
						return new RenderFallingBlock(manager);
					}

				});
	}
}
