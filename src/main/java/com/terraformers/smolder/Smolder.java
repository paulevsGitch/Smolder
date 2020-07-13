package com.terraformers.smolder;

import com.terraformers.smolder.api.SmolderBiomeRegistry;
import com.terraformers.smolder.biome.SmolderBiome;
import com.terraformers.smolder.world.SmolderBiomeSource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome.Category;

public class Smolder implements ModInitializer
{
	public static final String MOD_ID = "smolder";
	
	@Override
	public void onInitialize()
	{
		SmolderBiomeRegistry.register();
		Registry.register(Registry.BIOME_SOURCE, new Identifier(MOD_ID, "smolder_biome_source"), SmolderBiomeSource.CODEC);
		RegistryEntryAddedCallback.event(Registry.BIOME).register(SmolderBiomeRegistry::registerModBiome);
		registerExistingBiomes();
	}
	
	/**
	 * Register existing biomes on library startup. Must be called once.
	 */
	private void registerExistingBiomes()
	{
		Registry.BIOME.forEach((biome) -> {
			if (biome.getCategory() == Category.NETHER && !(biome instanceof SmolderBiome))
				SmolderBiomeRegistry.registerBiome(biome);
		});
	}
}