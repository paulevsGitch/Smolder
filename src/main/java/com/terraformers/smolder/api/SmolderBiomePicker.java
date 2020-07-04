package com.terraformers.smolder.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class SmolderBiomePicker
{
	private final List<Entry> biomeEntries = new ArrayList<>();
	private final List<Biome> biomes = new ArrayList<>();
	private double weightTotal;

	public int choose(LayerRandomnessSource rand)
	{
		if (biomeEntries.size() == 0)
		{
			throw new UnsupportedOperationException("No biomes registered!");
		}

		double randVal = random(rand);
		int i = -1;

		while (randVal >= 0)
		{
			++i;
			randVal -= biomeEntries.get(i).weight;
		}
		return Registry.BIOME.getRawId(biomeEntries.get(i).getBiome());
	}

	public void add(Biome biome, double weight)
	{
		this.biomeEntries.add(new Entry(biome, weight));
		this.biomes.add(biome);
		weightTotal += weight;
	}

	public List<Entry> getBiomeEntries()
	{
		return biomeEntries;
	}

	public List<Biome> getBiomes()
	{
		return biomes;
	}

	private double random(LayerRandomnessSource random)
	{
		return (double) random.nextInt(Integer.MAX_VALUE) * weightTotal / Integer.MAX_VALUE;
	}

	private static class Entry
	{
		private final Biome biome;
		private final double weight;

		private Entry(Biome biome, double weight)
		{
			this.biome = biome;
			this.weight = weight;
		}

		private Biome getBiome()
		{
			return biome;
		}
	}
}