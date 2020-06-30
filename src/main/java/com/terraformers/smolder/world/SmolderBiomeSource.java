package com.terraformers.smolder.world;

import com.mojang.serialization.Codec;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

public class SmolderBiomeSource extends BiomeSource {
	public static Codec<SmolderBiomeSource> CODEC = Codec.LONG.fieldOf("seed").xmap(SmolderBiomeSource::new, (source) -> source.seed).stable().codec();
	private final long seed;
	private final BiomeLayerSampler sampler;

	public SmolderBiomeSource(long seed) {
		super(SmolderBiomeData.biomePicker.getBiomes());
		this.seed = seed;
		sampler = SmolderBiomeLayers.build(seed);
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new SmolderBiomeSource(seed);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return sampler.sample(biomeX, biomeZ);
	}
}
