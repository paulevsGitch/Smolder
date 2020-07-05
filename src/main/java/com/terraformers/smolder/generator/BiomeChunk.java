package com.terraformers.smolder.generator;

import java.util.Random;

import com.terraformers.smolder.api.SmolderBiomeRegistry;
import com.terraformers.smolder.biome.SmolderBiome;

public class BiomeChunk
{
	protected static final int WIDTH = 16;
	private static final int MASK_W = WIDTH - 1;
	protected static final int SIZE = WIDTH * WIDTH;

	private final SmolderBiome[] biomes = new SmolderBiome[SIZE];
	
	public BiomeChunk(Random random)
	{
		int[] indexes = new int[SIZE];
		
		for (int i = 0; i < SIZE; i++)
			indexes[i] = i;
		
		for (int i = 0; i < SIZE; i++)
		{
			int i2 = random.nextInt(SIZE);
			int a = indexes[i];
			indexes[i] = indexes[i2];
			indexes[i2] = a;
		}

		for (int i: indexes)
		{
			if (biomes[i] == null)
			{
				SmolderBiome biome = SmolderBiomeRegistry.getRandomBiome(random);
				float size = biome.getSize();
				float r2 = size * size;
				int x = i / WIDTH;
				int z = i & MASK_W;
				int x1 = (int) (x - size);
				int z1 = (int) (z - size);
				int x2 = (int) Math.ceil(x + size);
				int z2 = (int) Math.ceil(z + size);
				for (int bx = x1; bx <= x2; bx++)
				{
					if (bx >= 0 && bx < WIDTH)
					{
						int bx2 = (bx - x);
						bx2 *= bx2;
						for (int bz = z1; bz <= z2; bz++)
						{
							if (bz >= 0 && bz < WIDTH)
							{
								int bz2 = (bz - z);
								bz2 *= bz2;
								if (bx2 + bz2 <= r2)
								{
									int i2 = getIndex(bx, bz);
									if (biomes[i2] == null)
									{
										biomes[i2] = SmolderBiomeRegistry.getSubBiome(biome, random);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public SmolderBiome getBiome(int x, int z)
	{
		return biomes[getIndex(x & MASK_W, z & MASK_W)];
	}
	
	private int getIndex(int x, int z)
	{
		return x * WIDTH + z;
	}
}
