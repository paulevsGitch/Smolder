package com.terraformers.smolder.generator;

import java.util.Random;

import com.terraformers.smolder.api.SmolderBiomeRegistry;
import com.terraformers.smolder.biome.SmolderBiome;

public class BiomeChunk
{
	protected static final int WIDTH = 16;
	private static final int SM_WIDTH = WIDTH >> 1;
	private static final int MASK_A = SM_WIDTH - 1;
	private static final int MASK_C = WIDTH - 1;
	
	private final int sm_height;
	private final int maxY;
	private final int maskB;
	private final SmolderBiome[][][] PreBio;
	private final SmolderBiome[][][] biomes;
	
	public BiomeChunk(BiomeMap map, Random random)
	{
		sm_height = clampOne(map.maxHeight >> 1);
		maskB = sm_height - 1;
		maxY = map.maxHeight - 1;
		PreBio = new SmolderBiome[sm_height][SM_WIDTH][SM_WIDTH];
		biomes = new SmolderBiome[map.maxHeight][WIDTH][WIDTH];
		
		for (int y = 0; y < sm_height; y++)
		{
			int section = y << 1;
			for (int x = 0; x < SM_WIDTH; x++)
			{
				for (int z = 0; z < SM_WIDTH; z++)
				{
					PreBio[y][x][z] = SmolderBiomeRegistry.getRandomBiome(section, random);
				}
			}
		}
		
		for (int y = 0; y < map.maxHeight; y++)
			for (int x = 0; x < WIDTH; x++)
				for (int z = 0; z < WIDTH; z++)
				{
					SmolderBiome biome = PreBio[offsetY(y, random)][offsetXZ(x, random)][offsetXZ(z, random)];
					biome = SmolderBiomeRegistry.getSubBiome(biome, random);
					biomes[y][x][z] = biome;
				}
	}

	public SmolderBiome getBiome(int x, int y, int z)
	{
		return biomes[clamp(y)][x & MASK_C][z & MASK_C];
	}
	
	private int offsetXZ(int x, Random random)
	{
		return ((x + random.nextInt(2)) >> 1) & MASK_A;
	}
	
	private int offsetY(int y, Random random)
	{
		return ((y + random.nextInt(2)) >> 1) & maskB;
	}
	
	private int clamp(int y)
	{
		return y < 0 ? 0 : y > maxY ? maxY : y;
	}
	
	private int clampOne(int x)
	{
		return x < 1 ? 1 : x;
	}
}
