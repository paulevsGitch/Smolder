package com.terraformers.smolder;

import com.terraformers.smolder.api.SmolderBiomes;
import com.terraformers.smolder.world.SmolderBiomeSource;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

public class Smolder implements ModInitializer {

	@Override
	public void onInitialize() {
		Registry.register(Registry.BIOME_SOURCE, new Identifier("smolder", "smolder_biome_source"), SmolderBiomeSource.CODEC);

		for (Biome biome : Registry.BIOME) {
			attemptAddBiome(biome);
		}

		RegistryEntryAddedCallback.event(Registry.BIOME).register(((i, identifier, biome) -> attemptAddBiome(biome)));
	}

	private static void attemptAddBiome(Biome biome) {
		if (biome.getCategory() == Biome.Category.NETHER) {
			// add all biomes by default
			SmolderBiomes.addNetherBiome(biome, 1.0, true);
		}
	}
}
