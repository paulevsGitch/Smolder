package com.terraformersmc.smolder.generator;

import java.util.HashMap;

import com.terraformersmc.smolder.api.SmolderBiomeRegistry;
import com.terraformersmc.smolder.biome.SmolderBiome;
import com.terraformersmc.smolder.config.Config;
import com.terraformersmc.smolder.noise.OpenSimplexNoise;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.ChunkRandom;

public final class BiomeMap {
	private static final HashMap<ChunkPos, BiomeChunk> MAPS = new HashMap<ChunkPos, BiomeChunk>();
	private static final ChunkRandom RANDOM = new ChunkRandom();
	
	private final double sizeXZ;
	private final int depth;
	private final int size;
	private final OpenSimplexNoise noiseX;
	private final OpenSimplexNoise noiseZ;
	private final double noiseScale;
	private final double noisePower;
	private final boolean useRoundedInterpol;
	
	public BiomeMap(long seed) {
		RANDOM.setSeed(seed);
		noiseX = new OpenSimplexNoise(RANDOM.nextLong());
		noiseZ = new OpenSimplexNoise(RANDOM.nextLong());
		
		sizeXZ = Config.getInt("generator", "biome_size_in_blocks", 200) / BiomeChunk.SCALE;
		
		depth = (int) Math.ceil(Math.log(sizeXZ) / Math.log(2)) - 4;
		size = 1 << depth;
		
		noiseScale = Config.getFloat("generator", "noise_scale", 0.1F);
		noisePower = Config.getFloat("generator", "noise_power", 1.0F);
		useRoundedInterpol = Config.getBoolean("generator", "use_rounded_interpolation", true);
	}
	
	public void clearCache() {
		if (MAPS.size() > 16) {
			MAPS.clear();
		}
	}
	
	private SmolderBiome getRawBiome(int bx, int bz) {
		double x = (double) bx * size / sizeXZ;
		double z = (double) bz * size / sizeXZ;
		double nx = x;
		double nz = z;
		
		double px = bx * noiseScale;
		double pz = bz * noiseScale;
		
		for (int i = 0; i < depth; i++) {
			nx = (x + noiseX.eval(px, pz) * noisePower) / 2F;
			nz = (z + noiseZ.eval(px, pz) * noisePower) / 2F;
			
			x = nx;
			z = nz;
			
			px = px / 2 + i;
			pz = pz / 2 + i;
		}
		
		bx = (int) Math.floor(x);
		bz = (int) Math.floor(z);
		if ((bx & BiomeChunk.MASK_W) == BiomeChunk.MASK_W) {
			x += (bz / 2) & 1;
		}
		if ((bz & BiomeChunk.MASK_W) == BiomeChunk.MASK_W) {
			z += (bx / 2) & 1;
		}
		
		ChunkPos cpos = new ChunkPos(MathHelper.floor((double) x / BiomeChunk.WIDTH), MathHelper.floor((double) z / BiomeChunk.WIDTH));
		BiomeChunk chunk = MAPS.get(cpos);
		if (chunk == null) {
			RANDOM.setTerrainSeed(cpos.x, cpos.z);
			chunk = new BiomeChunk(RANDOM);
			MAPS.put(cpos, chunk);
		}
		
		return useRoundedInterpol ? chunk.getBiome(x, z) : chunk.getBiome(MathHelper.floor(x), MathHelper.floor(z));
	}
	
	public SmolderBiome getBiome(int x, int z) {
		SmolderBiome biome = getRawBiome(x, z);
		SmolderBiome parent = null;
		boolean hasEdge = SmolderBiomeRegistry.getEdge(biome) != null || ((parent = SmolderBiomeRegistry.getParent(biome)) != null && SmolderBiomeRegistry.getEdge(parent) != null);
		if (hasEdge) {
			SmolderBiome search = biome;
			if (parent != null) {
				search = parent;
			}
			
			int d = (int) SmolderBiomeRegistry.getEdge(search).getSize();

			boolean edge = !SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x + d, z)) ||
					!SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x - d, z)) ||
					!SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x, z + d)) ||
					!SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x, z - d)) ||
					!SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x - d, z - d)) ||
					!SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x + d, z - d)) ||
					!SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x + d, z + d)) ||
					!SmolderBiomeRegistry.isSameBiome(search, getRawBiome(x - d, z + d));
			
			if (edge) {
				return SmolderBiomeRegistry.getEdge(search);
			}
		}
		
		return biome;
	}
}
