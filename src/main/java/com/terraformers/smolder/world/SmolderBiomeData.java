package com.terraformers.smolder.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.terraformers.smolder.api.WeightedBiomePicker;

import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

public class SmolderBiomeData {
	public static final WeightedBiomePicker biomePicker = new WeightedBiomePicker();
	public static int biomeSize = 5;
	public static final Map<Integer, List<Pair<Biome, Biome>>> edgeBiomes = new HashMap<>();
}
