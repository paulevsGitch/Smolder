package com.terraformers.smolder.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess.Storage;
import net.minecraft.world.biome.source.BiomeAccessType;

public class SmolderBiomeAccessType implements BiomeAccessType
{
	@Override
	public Biome getBiome(long seed, int x, int y, int z, Storage storage)
	{
		return storage.getBiomeForNoiseGen(x, y, z);
	}
}
