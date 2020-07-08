package com.terraformers.smolder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.terraformers.smolder.biome.SmolderBiome;
import com.terraformers.smolder.biome.SmolderWrappedBiome;
import com.terraformers.smolder.config.Config;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biomes;

public final class SmolderBiomeRegistry
{
	private static final Set<SmolderBiome> BIOMES = Sets.newHashSet();
	private static final Map<SmolderBiome, SmolderBiome> EDGES = Maps.newHashMap();
	private static final Map<SmolderBiome, List<SmolderBiome>> SUBBIOMES = Maps.newHashMap();
	private static final Map<SmolderBiome, SmolderBiome> PARENTS = Maps.newHashMap();
	private static final Map<SmolderBiome, Float> WEIGHTS = Maps.newHashMap();
	private static final boolean OVERRIDE = Config.getBoolean("generator", "allow_override_existing_biomes", true);
	private static float weight = 0;
	
	// Constants for Vanilla biomes //
	public static final SmolderBiome NETHER_WASTES_BIOME = registerBiome(Biomes.NETHER_WASTES);
	public static final SmolderBiome CRIMSON_FOREST_BIOME = registerBiome(Biomes.CRIMSON_FOREST);
	public static final SmolderBiome WARPED_FOREST_BIOME = registerBiome(Biomes.WARPED_FOREST);
	public static final SmolderBiome SOUL_SAND_VALLEY_BIOME = registerBiome(Biomes.SOUL_SAND_VALLEY);
	public static final SmolderBiome BASALT_DELTAS_BIOME = registerBiome(Biomes.BASALT_DELTAS);
	
	/**
	 * Used to put non-smolder biomes to registry. Puts them into wrappers with default values;
	 * @param biome - a {@link Biome} to register.
	 */
	public static SmolderBiome registerBiome(Biome biome)
	{
		Identifier id = Registry.BIOME.getId(biome);
		return registerBiome(id, new SmolderWrappedBiome(biome));
	}

	/**
	 * Put biome into registry.
	 * @param biome - a {@link SmolderBiome} to register.
	 */
	public static SmolderBiome registerBiome(Identifier id, SmolderBiome biome)
	{
		register(id, biome);
		if (OVERRIDE || !BIOMES.contains(biome))
		{
			BIOMES.add(biome);
			weight = biome.addWeight(weight);
		}
		return biome;
	}

	/**
	 * Register and attach an edge biome to specified biome.
	 * @param edge - a {@link SmolderBiome} that is edge, will be registered;
	 * @param parent - a {@link SmolderBiome} that is parent of it.
	 * @return edge {@link SmolderBiome}.
	 */
	public static SmolderBiome registerEdgeBiome(Identifier id, SmolderBiome edge, SmolderBiome parent)
	{
		register(id, edge);
		if (OVERRIDE || !EDGES.containsKey(parent))
		{
			EDGES.put(parent, edge);
			PARENTS.put(edge, parent);
		}
		return edge;
	}
	
	/**
	 * Register and attach a sub-biome to specified biome.
	 * @param subbiome - a {@link SmolderBiome} that is subbiome, will be registered;
	 * @param parent - a {@link SmolderBiome} that is parent of it.
	 * @return {@link SmolderBiome} sub-biome.
	 */
	public static SmolderBiome registerSubBiome(Identifier id, SmolderBiome subbiome, SmolderBiome parent)
	{
		register(id, subbiome);
		List<SmolderBiome> subbiomes = SUBBIOMES.get(parent);
		if (subbiomes == null)
		{
			subbiomes = new ArrayList<SmolderBiome>();
			SUBBIOMES.put(parent, subbiomes);
		}
		if (OVERRIDE || !subbiomes.contains(subbiome))
		{
			Float weight = WEIGHTS.get(parent);
			if (weight == null)
				weight = 1F;
			subbiomes.add(subbiome);
			weight = subbiome.addWeight(weight);
			PARENTS.put(subbiome, parent);
		}
		return subbiome;
	}
	
	/**
	 * Return a list of all registered biomes.
	 * @return {@link List} of {@link SmolderBiome}.
	 */
	public static List<SmolderBiome> getBiomes()
	{
		List<SmolderBiome> biomes = new ArrayList<SmolderBiome>();
		BIOMES.forEach((biome) -> {
			if (!biomes.contains(biome))
				biomes.add(biome);
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
	
	private static void register(Identifier id, SmolderBiome biome)
	{
		if (!(biome instanceof SmolderWrappedBiome))
			Registry.register(Registry.BIOME, id, biome);
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
	public static SmolderBiome getRandomBiome(Random random)
	{
		float w = random.nextFloat() * weight;
		for (SmolderBiome biome: BIOMES)
		{
			if (w < biome.getWeight())
				return biome;
		}
		return NETHER_WASTES_BIOME;
	}
	
	/**
	 * Returns sub-biome for specified {@link SmolderBiome} or itself.
	 * @param parent - {@link SmolderBiome};
	 * @param random - {@link Random}.
	 * @return {@link SmolderBiome} sub-biome or parent param.
	 */
	public static SmolderBiome getSubBiome(SmolderBiome parent, Random random)
	{
		List<SmolderBiome> subbiomes = SUBBIOMES.get(parent);
		if (subbiomes == null)
			return parent;
		Float weight = WEIGHTS.get(parent) * random.nextFloat();
		if (weight <= 1)
			return parent;
		for (SmolderBiome biome: subbiomes)
			if (biome.getWeight() <= weight)
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
	
	/**
	 * Check if biome is same (is itself or sub-biome with equal parent)
	 * @param source - {@link SmolderBiome}, biome itself;
	 * @param compare - {@link SmolderBiome}, comparating biome.
	 * @return true if biomes are "equal".
	 */
	public static boolean isSameBiome(SmolderBiome source, SmolderBiome compare)
	{
		SmolderBiome parent;
		return compare == source || (((parent = getParent(compare)) != null) && parent == source);
	}
	
	/**
	 * Register existing biomes on library startup. Must be called once.
	 */
	public static void registerExistingBiomes()
	{
		Registry.BIOME.forEach((biome) -> {
			if (biome.getCategory() == Category.NETHER && !(biome instanceof SmolderBiome))
				registerBiome(biome);
		});
	}
}
