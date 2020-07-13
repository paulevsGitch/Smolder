package com.terraformersmc.smolder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terraformersmc.smolder.config.Config;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "<init>*", at = @At("RETURN"))
	private void onInit(CallbackInfo info) {
		Config.save();
	}
}