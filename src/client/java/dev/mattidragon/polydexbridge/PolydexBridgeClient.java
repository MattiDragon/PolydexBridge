package dev.mattidragon.polydexbridge;

import dev.mattidragon.polydexbridge.data.BridgeRecipe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.ArrayList;
import java.util.List;

public class PolydexBridgeClient implements ClientModInitializer {
	public static final List<BridgeRecipe> RECIPES = new ArrayList<>();
	public static Runnable onPolyRecipes = () -> {};
	public static boolean waitForPoly = false;
	
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> disconnect());
		ClientConfigurationConnectionEvents.INIT.register(((handler, client) -> disconnect()));
		
		ClientConfigurationNetworking.registerGlobalReceiver(BridgeEnablePacket.ID, (packet, context) -> waitForPoly = true);
		ClientPlayNetworking.registerGlobalReceiver(PolydexRecipesPacket.ID, (packet, context) -> {
			RECIPES.clear();
			RECIPES.addAll(packet.recipes());
			onPolyRecipes.run();
		});
	}
	
	private void disconnect() {
		RECIPES.clear();
		waitForPoly = false;
	}
}