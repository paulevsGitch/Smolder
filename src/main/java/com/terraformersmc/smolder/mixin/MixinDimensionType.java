package com.terraformersmc.smolder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.terraformersmc.smolder.world.SmolderBiomeSource;

import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

@Mixin(DimensionType.class)
public class MixinDimensionType {
	/**
	 * @reason Use Smolder's biome source
	 * @author SuperCoder79
	 */
	@Overwrite
	private static ChunkGenerator createNetherGenerator(long seed) {
		return new SurfaceChunkGenerator(new SmolderBiomeSource(seed), seed, ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType());
	}
}
