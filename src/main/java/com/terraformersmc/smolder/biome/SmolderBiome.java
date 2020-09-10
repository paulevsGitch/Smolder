package com.terraformersmc.smolder.biome;

import com.terraformersmc.smolder.config.Config;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class SmolderBiome {
	private static int globalID = 0;
	
	private final float size;
	private float weight;
	private final int id;
	private final Biome biome;
	
	public SmolderBiome(Identifier id, BiomeDefenition settings) {
		String group = String.format("biome.%s.%s", id.getNamespace(), id.getPath());
		
		this.id = globalID++;
		this.size = Config.getFloat(group, "size", settings.getSize());
		this.weight = Config.getFloat(group, "weight", settings.getWeight());
		this.biome = settings.build();
	}
	
	public Biome getBiome() {
		return biome;
	}

	public float getSize() {
		return size;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public float addWeight(float weight) {
		this.weight += weight;
		return this.weight;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		SmolderBiome biome = (SmolderBiome) obj;
		return biome == null ? false : biome.id == id;
	}
}
