package dev.mattidragon.polydexbridge.data;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Arrays;
import java.util.List;

public record Slot(int x, int y, List<BridgeStack> stacks) {
    public static final PacketCodec<RegistryByteBuf, Slot> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, Slot::x,
            PacketCodecs.VAR_INT, Slot::y,
            BridgeStack.CODEC.collect(PacketCodecs.toList()), Slot::stacks,
            Slot::new
    );

    public Slot(int x, int y, BridgeStack... stacks) {
        this(x, y, Arrays.asList(stacks));
    }
}
