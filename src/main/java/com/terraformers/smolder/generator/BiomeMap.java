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
	
	private final int sizeXZ;
	private final int sizeY;
	protected final int maxHeight;
	private final int depth;
	private final int size;
	private final OpenSimplexNoise noiseX;
	private final OpenSimplexNoise noiseY;
	private final OpenSimplexNoise noiseZ;
	
	public BiomeMap(long seed, int sizeXZ, int sizeY)
	{
		RANDOM.setSeed(seed);
		noiseX = new OpenSimplexNoise(RANDOM.nextLong());
		noiseY = new OpenSimplexNoise(RANDOM.nextLong());
		noiseZ = new OpenSimplexNoise(RANDOM.nextLong());
		this.sizeXZ = sizeXZ;
		this.sizeY = sizeY;
		maxHeight = (int) Math.ceil(128F / sizeY);
		
		depth = (int) Math.ceil(Math.log(Math.max(sizeXZ, sizeY)) / Math.log(2)) - 2;
		size = 1 << depth;
	}
	
	public void clearCache()
	{
		if (MAPS.size() > 16)
			MAPS.clear();
	}
	
	private SmolderBiome getRawBiome(int bx, int by, int bz)
	{
		double x = bx * size / sizeXZ;
		double y = by * size / sizeY;
		double z = bz * size / sizeXZ;
		double nx = x;
		double ny = y;
		double nz = z;
		
		double px = bx * 0.2;
		double py = by * 0.2;
		double pz = bz * 0.2;
		
		for (int i = 0; i < depth; i++)
		{
			nx = (x + noiseX.eval(px, pz)) / 2F;
			nz = (z + noiseZ.eval(px, pz)) / 2F;
			
			nz = (z + noiseY.eval(px, pz)) / 2F;
			
			y = ny;
			py = py / 2 + i;
			
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
			chunk = new BiomeChunk(this, RANDOM);
			MAPS.put(cpos, chunk);
		}
		
		return chunk.getBiome((int) x, (int) y, (int) z);
	}
	
	public SmolderBiome getBiome(int x, int y, int z)
	{
		SmolderBiome biome = getRawBiome(x, y > 30 ? y : 30, z);
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
			
			boolean edge = !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x + d, y, z));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x - d, y, z));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x, y, z + d));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x, y, z - d));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x - 1, y, z - 1));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x - 1, y, z + 1));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x + 1, y, z - 1));
			edge = edge || !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x + 1, y, z + 1));
			
			if (edge)
			{
				return SmolderBiomeRegistry.getEdge(search);
			}
		}
		
		return biome;
	}
}
