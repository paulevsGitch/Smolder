import com.terraformersmc.smolder.api.SmolderBiomeRegistry;
import com.terraformersmc.smolder.biome.BiomeDefinition;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class BiomeTesting implements ModInitializer {
	@Override
	public void onInitialize() {
		SmolderBiomeRegistry.registerBiome(new Identifier("smolder", "test_biome"), new BiomeDefinition().setFogColor(0, 255, 0));
	}
}
