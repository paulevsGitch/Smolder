package com.terraformers.smolder.world;

import java.util.List;

import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

/**
 * Handles adding new sub biomes and edge biomes.
 */
public class BiomeAdditionLayer implements CrossSamplingLayer {
	private final List<Pair<Biome, Biome>> edgeBiomes;

	public BiomeAdditionLayer(List<Pair<Biome, Biome>> edgeBiomes) {

		this.edgeBiomes = edgeBiomes;
	}

	@Override
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		int centerBiome = -1;
		int edgeBiome = -1;
		for (Pair<Biome, Biome> edge : edgeBiomes) {
			int currentBiome = Registry.BIOME.getRawId(edge.getLeft());
			// check to see if the center has an edge
			if (center == currentBiome) {
				centerBiome = currentBiome;
				edgeBiome = Registry.BIOME.getRawId(edge.getRight());
			}
		}

		// no edge was found
		if (centerBiome == -1) {
			return center;
		}

		//edge detected
		if (n != centerBiome || e != centerBiome || s != centerBiome || w != centerBiome) {
			return edgeBiome;
		}

		return center;
	}
}
