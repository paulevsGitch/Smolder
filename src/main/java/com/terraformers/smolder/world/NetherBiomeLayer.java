package com.terraformers.smolder.world;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum  NetherBiomeLayer implements InitLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int x, int y) {
		return SmolderBiomeData.biomePicker.choose(context);
	}
}
