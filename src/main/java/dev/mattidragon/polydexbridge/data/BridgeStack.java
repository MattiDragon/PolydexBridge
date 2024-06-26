package dev.mattidragon.polydexbridge.data;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BridgeStack(ItemStack stack, float chance) {
    public static final PacketCodec<RegistryByteBuf, BridgeStack> CODEC = PacketCodec.tuple(
            ItemStack.OPTIONAL_PACKET_CODEC, BridgeStack::stack,
            PacketCodecs.FLOAT, BridgeStack::chance,
            BridgeStack::new);

    public BridgeStack(ItemStack stack) {
        this(stack, 1);
    }
}
