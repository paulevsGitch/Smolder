package com.terraformers.smolder.biome;

import java.util.Arrays;
import java.util.Random;

import com.terraformers.smolder.config.Config;

import net.minecraft.world.biome.Biome;

public class SmolderBiome extends Biome
{
	private static int globalID = 0;
	
	private final String registryName;
	private final float size;
	private final int minHeight;
	private final int maxHeight;
	private final float[] weights;
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
		this.minHeight = clamp(Config.getInt(group, "minHeight", settings.getMinHeight()) >> 2, 0, 63);
		this.maxHeight = clamp(Config.getInt(group, "maxHeight", settings.getMaxHeight()) >> 2, 0, 63);
		
		this.weights = new float[this.maxHeight - this.minHeight + 1];
		float weight = Config.getFloat(group, "weight", settings.getWeight());
		Arrays.fill(this.weights, weight);
	}
	
	private int clamp(int x, int min, int max)
	{
		return x < min ? min : x > max ? max : x;
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

	public int getMinHeight()
	{
		return minHeight;
	}

	public int getMaxHeight()
	{
		return maxHeight;
	}
	
	public float getWeight()
	{
		return weights[minHeight];
	}

	public float getWeight(int section)
	{
		if (section < minHeight || section > maxHeight)
			return 0;
		return weights[section - minHeight];
	}
	
	public float mutateWeight(int section, float weight)
	{
		int index = section - minHeight;
		weights[index] += weight;
		return weights[index];
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
}
