package com.terraformers.smolder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.terraformers.smolder.Smolder;
import com.terraformers.smolder.biome.SmolderBiome;
import com.terraformers.smolder.biome.SmolderWrappedBiome;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public final class SmolderBiomeRegistry
{
	private static final List<List<SmolderBiome>> BIOMES = new ArrayList<List<SmolderBiome>>();
	private static final Map<SmolderBiome, SmolderBiome> EDGES = Maps.newHashMap();
	private static final Map<SmolderBiome, List<SmolderBiome>> SUBBIOMES = Maps.newHashMap();
	private static final Map<SmolderBiome, SmolderBiome> PARENTS = Maps.newHashMap();
	private static final float[] WEIGHTS = new float[64]; // 64 is 256/4 - maximum biome rows in world
	
	public static final SmolderBiome NETHER_WASTES_BIOME = registerBiome(Biomes.NETHER_WASTES);
	public static final SmolderBiome CRIMSON_FOREST_BIOME = registerBiome(Biomes.CRIMSON_FOREST);
	public static final SmolderBiome WARPED_FOREST_BIOME = registerBiome(Biomes.WARPED_FOREST);
	public static final SmolderBiome SOUL_SAND_VALLEY_BIOME = registerBiome(Biomes.SOUL_SAND_VALLEY);
	
	/**
	 * Used to put non-smolder biomes to registry. Puts them into wrappers with default values;
	 * @param biome - a {@link Biome} to register.
	 */
	public static SmolderBiome registerBiome(Biome biome)
	{
		return registerBiome(new SmolderWrappedBiome(biome));
	}

	/**
	 * Put biome into registry.
	 * @param biome - a {@link SmolderBiome} to register.
	 */
	public static SmolderBiome registerBiome(SmolderBiome biome)
	{
		register(biome);
		for (int section = biome.getMinHeight(); section <= biome.getMaxHeight(); section++)
		{
			BIOMES.get(section).add(biome);
			WEIGHTS[section] = biome.mutateWeight(section, WEIGHTS[section]);
		}
		return biome;
	}

	/**
	 * Register and attach an edge biome to specified biome.
	 * @param edge - a {@link SmolderBiome} that is edge, will be registered;
	 * @param parent - a {@link SmolderBiome} that is parent of it.
	 */
	public static void addEdgeBiome(SmolderBiome edge, SmolderBiome parent)
	{
		register(edge);
		EDGES.put(parent, edge);
		PARENTS.put(edge, parent);
	}
	
	/**
	 * Register and attach a sub-biome to specified biome.
	 * @param subbiome - a {@link SmolderBiome} that is subbiome, will be registered;
	 * @param parent - a {@link SmolderBiome} that is parent of it.
	 */
	public static void addSubBiome(SmolderBiome subbiome, SmolderBiome parent)
	{
		register(subbiome);
		List<SmolderBiome> subbiomes = SUBBIOMES.get(subbiome);
		if (subbiomes == null)
		{
			subbiomes = new ArrayList<SmolderBiome>();
			SUBBIOMES.put(parent, subbiomes);
		}
		subbiomes.add(subbiome);
		parent.addSubWeight(subbiome.getWeight());
		PARENTS.put(subbiome, parent);
	}
	
	/**
	 * Return a list of all registered biomes.
	 * @return {@link List} of {@link SmolderBiome}.
	 */
	public static List<Biome> getBiomes()
	{
		List<SmolderBiome> biomes = new ArrayList<SmolderBiome>();
		BIOMES.forEach((list) -> {
			list.forEach((biome -> {
				if (!biomes.contains(biome))
					biomes.add(biome);
			}));
		});
		return ImmutableList.copyOf(biomes);
	}
	
	/**
	 * Returns an edge for biome or null;
	 * @param biome - parent {@link SmolderBiome};
	 * @return {@link SmolderBiome} edge.
	 */
	public static SmolderBiome getEdge(SmolderBiome biome)
	{
		return EDGES.get(biome);
	}
	
	/**
	 * Returns a sub-biomes for biome or null;
	 * @param biome - parent {@link SmolderBiome};
	 * @return {@link List} of {@link SmolderBiome} sub-biomes.
	 */
	public static List<SmolderBiome> getSubBiomes(SmolderBiome biome)
	{
		return SUBBIOMES.get(biome);
	}
	
	private static void register(SmolderBiome biome)
	{
		Registry.register(Registry.BIOME, new Identifier(Smolder.MOD_ID, biome.getRegistryName()), biome);
	}
	
	/**
	 * Registers biome from another mod, used in callback.
	 * @param i - raw ID;
	 * @param id - {@link Identifier} of biome;
	 * @param biome - {@link Biome} itself.
	 */
	public static void registerModBiome(int i, Identifier id, Biome biome)
	{
		registerBiome(biome);
	}
	
	/**
	 * Returns random {@link SmolderBiome}.
	 * @param random - {@link Random};
	 * @return weighted {@link SmolderBiome}.
	 */
	public static SmolderBiome getRandomBiome(int section, Random random)
	{
		float w = random.nextFloat() * WEIGHTS[section];
		for (SmolderBiome biome: BIOMES.get(section))
			if (biome.getWeight(section) <= w)
				return biome;
		return NETHER_WASTES_BIOME;
	}
	
	public static SmolderBiome getSubBiome(SmolderBiome parent, Random random)
	{
		List<SmolderBiome> subbiomes = SUBBIOMES.get(parent);
		float w = parent.getSubWeight(random);
		for (SmolderBiome biome: subbiomes)
			if (biome.getWeight() <= w)
				return biome;
		return parent;
	}
	
	/**
	 * Returns parent of biome or null
	 * @param biome - {@link SmolderBiome};
	 * @return parent {@link SmolderBiome}.
	 */
	public static SmolderBiome getParent(SmolderBiome biome)
	{
		return PARENTS.get(biome);
	}
	
	public static boolean isSameBiome(SmolderBiome source, SmolderBiome compare)
	{
		SmolderBiome parent;
		return compare == source || (((parent = getParent(compare)) != null) && parent == source);
	}
	
	static
	{
		for (int i = 0; i < 64; i++)
			BIOMES.add(new ArrayList<SmolderBiome>());
	}
}
