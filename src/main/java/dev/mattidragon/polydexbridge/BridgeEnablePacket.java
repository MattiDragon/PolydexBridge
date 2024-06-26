package dev.mattidragon.polydexbridge;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public enum BridgeEnablePacket implements CustomPayload {
    INSTANCE;
    
    public static final Id<BridgeEnablePacket> ID = new Id<>(Identifier.of(PolydexBridge.MOD_ID, "enable"));
    public static final PacketCodec<PacketByteBuf, BridgeEnablePacket> CODEC = PacketCodec.unit(INSTANCE); 

    public static void register() {
        PayloadTypeRegistry.configurationS2C().register(ID, CODEC);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
