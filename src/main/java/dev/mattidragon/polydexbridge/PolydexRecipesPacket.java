package dev.mattidragon.polydexbridge;

import dev.mattidragon.polydexbridge.data.BridgeRecipe;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record PolydexRecipesPacket(List<BridgeRecipe> recipes) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, PolydexRecipesPacket> CODEC 
            = PacketCodec.tuple(BridgeRecipe.CODEC.collect(PacketCodecs.toList()), PolydexRecipesPacket::recipes, PolydexRecipesPacket::new);
    public static final Id<PolydexRecipesPacket> ID = new Id<>(Identifier.of(PolydexBridge.MOD_ID, "sync_recipes"));
    
    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
