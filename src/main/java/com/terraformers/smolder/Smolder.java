package com.terraformers.smolder;

import com.terraformers.smolder.api.SmolderBiomeRegistry;
import com.terraformers.smolder.world.SmolderBiomeSource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Smolder implements ModInitializer
{
	public static final String MOD_ID = "smolder";
	
	@Override
	public void onInitialize()
	{
		SmolderBiomeRegistry.register();
		Registry.register(Registry.BIOME_SOURCE, new Identifier(MOD_ID, "smolder_biome_source"), SmolderBiomeSource.CODEC);
		RegistryEntryAddedCallback.event(Registry.BIOME).register(SmolderBiomeRegistry::registerModBiome);
		SmolderBiomeRegistry.registerExistingBiomes();
	}
}