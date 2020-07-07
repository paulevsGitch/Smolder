package com.terraformers.smolder.biome;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class SmolderWrappedBiome extends SmolderBiome
{
	private final Biome biome;
	
	public SmolderWrappedBiome(Biome biome)
	{
		super(Registry.BIOME.getId(biome), new SmolderBiomeSettings(biome));
		this.biome = biome;
	}
	
	@Override
	public Biome getBiome()
	{
		return biome;
	}
}
