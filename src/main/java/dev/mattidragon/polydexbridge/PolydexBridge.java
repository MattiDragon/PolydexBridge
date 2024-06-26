package dev.mattidragon.polydexbridge;

import dev.mattidragon.polydexbridge.data.BridgeCategory;
import dev.mattidragon.polydexbridge.data.BridgeRecipe;
import eu.pb4.polydex.impl.PolydexImpl;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PolydexBridge implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PolydexBridge");
	public static final String MOD_ID = "polydex-bridge";

	@Override
	public void onInitialize() {
		PolydexRecipesPacket.register();
		BridgeEnablePacket.register();
		
		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			ServerConfigurationNetworking.send(handler, BridgeEnablePacket.INSTANCE);
		});
	}
	
	public static PolydexRecipesPacket createPacket(ServerPlayerEntity player) {
		var recipes = new ArrayList<BridgeRecipe>();
		for (var page : PolydexImpl.ID_TO_PAGE.values()) {
			var categories = page.categories()
					.stream()
					.filter(category -> !category.identifier().getNamespace().equals("minecraft"))
					.map(category -> new BridgeCategory(category.identifier(), category.name()))
					.toList();
			if (categories.isEmpty()) continue;

			var converter = new PageConverter(player);
			page.createPage(null, player, converter);
			recipes.add(new BridgeRecipe(
					page.identifier(),
					page.getGroup(),
					categories,
					page.typeIcon(player),
					page.entryIcon(null, player),
					page.texture(player),
					page.ingredients().stream().map(ingredient -> PageConverter.convertIngredient(ingredient, player)).toList(),
					converter.icons,
					converter.inputs,
					converter.outputs
			));
		}
		return new PolydexRecipesPacket(recipes);
	}

	public static void sendRecipes(MinecraftServer server) {
		for (var player : server.getPlayerManager().getPlayerList()) {
			if (ServerPlayNetworking.canSend(player, PolydexRecipesPacket.ID)) {
                ServerPlayNetworking.send(player, createPacket(player));
            }
		}
	}
}