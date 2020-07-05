package com.terraformers.smolder.world;

import java.util.Collections;

import com.mojang.serialization.Codec;
import com.terraformers.smolder.config.Config;
import com.terraformers.smolder.generator.BiomeMap;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class SmolderBiomeSource extends BiomeSource
{
	public static final Codec<SmolderBiomeSource> CODEC = Codec.LONG.fieldOf("seed").xmap(SmolderBiomeSource::new, (source) -> source.seed).stable().codec();
	private BiomeMap map;
	private final long seed;

	public SmolderBiomeSource(long seed)
	{
		super(Collections.emptyList());
		this.seed = seed;
		int sizeXZ = Config.getInt("generator", "biome_size_xz", 200);
		this.map = new BiomeMap(seed, sizeXZ);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
	{
		Biome biome = map.getBiome(biomeX << 2, biomeZ << 2).getBiome();
		if (biomeX == 0 && biomeZ == 0)
			map.clearCache();
		return biome;
	}
	
	@Override
	public BiomeSource withSeed(long seed)
	{
		return new SmolderBiomeSource(seed);
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442()
	{
		return CODEC;
	}
}
