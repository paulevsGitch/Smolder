package com.terraformersmc.smolder.biome;

import com.terraformersmc.smolder.config.Config;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class SmolderBiome extends Biome {
	private static int globalID = 0;
	
	private final float size;
	private float weight;
	private final int id;
	
	public SmolderBiome(Identifier id, SmolderBiomeSettings settings) {
		super(settings);
		
		this.id = globalID++;
		
		String group = String.format("biome.%s.%s", id.getNamespace(), id.getPath());
		this.size = Config.getFloat(group, "size", settings.getSize());
		this.weight = Config.getFloat(group, "weight", settings.getWeight());
	}
	
	public Biome getBiome() {
		return this;
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
