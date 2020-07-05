package com.terraformers.smolder.biome;

import java.util.Random;

import com.terraformers.smolder.config.Config;

import net.minecraft.world.biome.Biome;

public class SmolderBiome extends Biome
{
	private static int globalID = 0;
	
	private final String registryName;
	private final float size;
	private float weight;
	private final int id;
	
	private float subBiomeWeight = 1;
	
	public SmolderBiome(String registryName, SmolderBiomeSettings settings)
	{
		super(settings);
		
		String namespace = registryName.substring(0, registryName.indexOf(':'));
		String name = registryName.substring(registryName.indexOf(':') + 1);
		String group = "biomes." + namespace + "." + name;
		
		this.id = globalID++;
		this.registryName = registryName;
		this.size = Config.getFloat(group, "size", settings.getSize());
		
		this.weight = Config.getFloat(group, "weight", settings.getWeight());
	}
	
	public Biome getBiome()
	{
		return this;
	}

	public String getRegistryName()
	{
		return registryName;
	}
	
	public float getSubWeight(Random random)
	{
		return random.nextFloat() * subBiomeWeight;
	}
	
	public void addSubWeight(float weight)
	{
		subBiomeWeight += weight;
	}

	public float getSize()
	{
		return size;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public float addToLayer(float weight)
	{
		this.weight += weight;
		return this.weight;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return registryName;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		SmolderBiome biome = (SmolderBiome) obj;
		return biome == null ? false : biome.id == id;
	}
}
