package com.terraformers.smolder.generator;

import java.util.HashMap;

import com.terraformers.smolder.api.SmolderBiomeRegistry;
import com.terraformers.smolder.biome.SmolderBiome;
import com.terraformers.smolder.noise.OpenSimplexNoise;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;

public final class BiomeMap
{
	private static final HashMap<ChunkPos, BiomeChunk> MAPS = new HashMap<ChunkPos, BiomeChunk>();
	private static final ChunkRandom RANDOM = new ChunkRandom();
	
	private final double sizeXZ;
	private final int depth;
	private final int size;
	private final OpenSimplexNoise noiseX;
	private final OpenSimplexNoise noiseZ;
	
	public BiomeMap(long seed, int sizeXZ)
	{
		RANDOM.setSeed(seed);
		noiseX = new OpenSimplexNoise(RANDOM.nextLong());
		noiseZ = new OpenSimplexNoise(RANDOM.nextLong());
		this.sizeXZ = sizeXZ * 0.25;
		
		depth = (int) Math.ceil(Math.log(sizeXZ) / Math.log(2)) - 4;
		size = 1 << depth;
	}
	
	public void clearCache()
	{
		if (MAPS.size() > 16)
			MAPS.clear();
	}
	
	private SmolderBiome getRawBiome(int bx, int bz)
	{
		double x = (double) bx * size / sizeXZ;
		double z = (double) bz * size / sizeXZ;
		double nx = x;
		double nz = z;
		
		double px = bx * 0.2;
		double pz = bz * 0.2;
		
		for (int i = 0; i < depth; i++)
		{
			nx = (x + noiseX.eval(px, pz)) / 2F;
			nz = (z + noiseZ.eval(px, pz)) / 2F;
			
			x = nx;
			z = nz;
			
			px = px / 2 + i;
			pz = pz / 2 + i;
		}
		
		ChunkPos cpos = new ChunkPos(
				(int) Math.floor((double) x / BiomeChunk.WIDTH),
				(int) Math.floor((double) z / BiomeChunk.WIDTH));
		BiomeChunk chunk = MAPS.get(cpos);
		if (chunk == null)
		{
			RANDOM.setTerrainSeed(cpos.x, cpos.z);
			chunk = new BiomeChunk(RANDOM);
			MAPS.put(cpos, chunk);
		}
		
		return chunk.getBiome((int) x, (int) z);
	}
	
	public SmolderBiome getBiome(int x, int z)
	{
		SmolderBiome biome = getRawBiome(x, z);
		SmolderBiome parent = null;
		boolean hasEdge = SmolderBiomeRegistry.getEdge(biome) != null ||
				((parent = SmolderBiomeRegistry.getParent(biome)) != null &&
				SmolderBiomeRegistry.getEdge(parent) != null);
		if (hasEdge)
		{
			SmolderBiome search = biome;
			if (parent != null)
				search = parent;
			
			int d = (int) Math.ceil(search.getSize() / 4F) << 2;
			
			boolean edge = !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x + d, z));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x - d, z));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x, z + d));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x, z - d));
			
			if (edge)
			{
				return SmolderBiomeRegistry.getEdge(search);
			}
		}
		
		return biome;
	}
}
