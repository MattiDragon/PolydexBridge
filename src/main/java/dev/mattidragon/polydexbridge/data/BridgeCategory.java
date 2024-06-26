package dev.mattidragon.polydexbridge.data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public record BridgeCategory(Identifier id, Text name) {
    public static final PacketCodec<PacketByteBuf, BridgeCategory> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, BridgeCategory::id,
            TextCodecs.PACKET_CODEC, BridgeCategory::name,
            BridgeCategory::new
    );

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BridgeCategory that && this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
