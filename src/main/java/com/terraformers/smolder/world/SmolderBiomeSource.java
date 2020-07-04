package com.terraformers.smolder.world;

import com.mojang.serialization.Codec;
import com.terraformers.smolder.api.SmolderBiomeRegistry;
import com.terraformers.smolder.config.Config;
import com.terraformers.smolder.generator.BiomeMap;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class SmolderBiomeSource extends BiomeSource
{
	public static Codec<SmolderBiomeSource> CODEC = Codec.LONG.fieldOf("seed").xmap(SmolderBiomeSource::new, (source) -> source.seed).stable().codec();
	private final long seed;
	private BiomeMap map;

	public SmolderBiomeSource(long seed)
	{
		super(SmolderBiomeRegistry.getBiomes());
		this.seed = seed;
		int sizeXZ = Config.getInt("generator", "biome_size_xz", 200);
		int sizeY = Config.getInt("generator", "biome_size_y", 40);
		this.map = new BiomeMap(seed, sizeXZ, sizeY);
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442()
	{
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed)
	{
		return new SmolderBiomeSource(seed);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
	{
		Biome biome = map.getBiome(biomeX << 2, biomeY << 2, biomeZ << 2).getBiome();
		if (biomeX == 0 && biomeY == 0 && biomeZ == 0)
			map.clearCache();
		return biome;
	}
}
