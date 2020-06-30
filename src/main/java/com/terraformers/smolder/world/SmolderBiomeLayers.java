package com.terraformers.smolder.world;

import java.util.function.LongFunction;

import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class SmolderBiomeLayers {
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LongFunction<C> contextProvider) {
		LayerFactory<T> biomeLayer = NetherBiomeLayer.INSTANCE.create(contextProvider.apply(34L));

		// add biomes and scale up
		for (int i = SmolderBiomeData.biomeSize; i > 0; i--) {

			// get the biome data for this biome size
			if (SmolderBiomeData.edgeBiomes.containsKey(i)) {
				biomeLayer = new BiomeAdditionLayer(SmolderBiomeData.edgeBiomes.get(i)).create(contextProvider.apply(150 + i), biomeLayer);
			}

			// scale up
			biomeLayer = ScaleLayer.NORMAL.create(contextProvider.apply(100 + i), biomeLayer);
		}

		return biomeLayer;
	}

	public static BiomeLayerSampler build(long seed) {
		LayerFactory<CachingLayerSampler> layerFactory = build(salt -> new CachingLayerContext(25, seed, salt));
		return new BiomeLayerSampler(layerFactory);
	}
}
