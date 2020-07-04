package com.terraformers.smolder.biome;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class SmolderBiomeSettings extends Biome.Settings
{
	private float size = SmolderBiomeData.DEF_SIZE;
	private float weight = SmolderBiomeData.DEF_WEIGHT;
	private int minHeight = SmolderBiomeData.DEF_MIN_HEIGHT;
	private int maxHeight = SmolderBiomeData.DEF_MAX_HEIGHT;
	
	public SmolderBiomeSettings()
	{
		this.configureSurfaceBuilder(SurfaceBuilder.NETHER, SurfaceBuilder.NETHER_CONFIG);
		this.precipitation(Biome.Precipitation.NONE);
		this.category(Biome.Category.NETHER);
		this.depth(0.1F);
		this.scale(0.2F);
		this.temperature(2.0F);
		this.downfall(0.0F);
		this.effects(new BiomeEffects.Builder()
				.waterColor(4159204)
				.waterFogColor(329011)
				.fogColor(3344392)
				.loopSound(SoundEvents.AMBIENT_NETHER_WASTES_LOOP)
				.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0D))
				.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111D))
				.music(MusicType.method_27283(SoundEvents.MUSIC_NETHER_NETHER_WASTES))
				.build());
		this.parent(null);
		this.noises(ImmutableList.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F)));
	}
	
	public SmolderBiomeSettings(Biome biome)
	{
		super();
		this.surfaceBuilder(biome.getSurfaceBuilder());
		this.precipitation(biome.getPrecipitation());
		this.category(biome.getCategory());
		this.depth(biome.getDepth());
		this.scale(biome.getScale());
		this.temperature(biome.getTemperature());
		this.downfall(biome.getRainfall());
		this.parent(biome.getParent());
		this.effects(biome.getEffects());
	}
	
	public SmolderBiomeSettings setSize(float size)
	{
		this.size = size;
		return this;
	}
	
	/**
	 * Set biome height limits
	 * @param minHeight - minimum biome height in blocks;
	 * @param maxHeight - maximum biome height in blocks;
	 * @return
	 */
	public SmolderBiomeSettings setHeight(int minHeight, int maxHeight)
	{
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		return this;
	}
	
	public SmolderBiomeSettings setWeight(float weight)
	{
		this.weight = weight;
		return this;
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
		return weight;
	}
}