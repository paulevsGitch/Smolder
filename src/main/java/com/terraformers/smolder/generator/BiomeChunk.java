package com.terraformers.smolder.generator;

import java.util.Random;

import com.terraformers.smolder.api.SmolderBiomeRegistry;
import com.terraformers.smolder.biome.SmolderBiome;

public class BiomeChunk
{
	protected static final int WIDTH = 32;
	protected static final int SCALE = 4;
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
				float size = biome.getSize() * SCALE;
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

	/**
	 * Directly takes biome from chunk, "nearest neighbor" algorithm.
	 * @param x - double coordinate
	 * @param z - double coordinate
	 * @return {@link SmolderBiome}
	 */
	public SmolderBiome getBiome(int x, int z)
	{
		return biomes[getIndex(x & MASK_W, z & MASK_W)];
	}
	
	private int getIndex(int x, int z)
	{
		return x * WIDTH + z;
	}
	
	/**
	 * Returns more "smoothed" interpolation when the direct one.
	 * @param x - double coordinate
	 * @param z - double coordinate
	 * @return {@link SmolderBiome}
	 */
	public SmolderBiome getBiome(double x, double z)
	{
		int bx = (int) Math.floor(wrap(x) + periodic(z) * 0.3);// & MASK_W;
		int bz = (int) Math.floor(wrap(z)  + periodic(x) * 0.3);// & MASK_W;
		bx = bx < 0 ? 0 : bx > MASK_W ? MASK_W : bx;
		bz = bz < 0 ? 0 : bz > MASK_W ? MASK_W : bz;
		return biomes[getIndex(bx, bz)];
	}
	
	private double wrap(double x)
	{
		return x - Math.floor(x / WIDTH) * WIDTH;
	}
	
	private double periodic(double x)
	{
		return (Math.sin(x * Math.PI));
	}
}
