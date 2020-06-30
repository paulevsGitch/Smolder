package com.terraformers.smolder.api;

import java.util.ArrayList;

import com.terraformers.smolder.world.SmolderBiomeData;

import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

public final class SmolderBiomes {
	private SmolderBiomes() {

	}

	public static void addNetherBiome(Biome biome, double weight) {
		addNetherBiome(biome, weight, false);
	}

	public static void addNetherBiome(Biome biome, double weight, boolean avoidOverride) {
		// Avoid overriding biomes
		// This is used with automatic biome detection.
		if (avoidOverride && SmolderBiomeData.biomePicker.getBiomes().contains(biome)) {
			return;
		}

		SmolderBiomeData.biomePicker.add(biome, weight);
	}

	public static void addEdgeBiome(Biome base, Biome edge, int size) {
		// create list if it doesn't exist
		if (!SmolderBiomeData.edgeBiomes.containsKey(size)) {
			SmolderBiomeData.edgeBiomes.put(size, new ArrayList<>());
		}

		SmolderBiomeData.edgeBiomes.get(size).add(new Pair<>(base, edge));
	}
}
