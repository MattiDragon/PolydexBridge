package dev.mattidragon.polydexbridge.data;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record BridgeRecipe(Identifier id,
                           String group,
                           List<BridgeCategory> categories,
                           ItemStack typeIcon,
                           ItemStack entryIcon,
                           @Nullable Text texture,
                           List<List<BridgeStack>> ingredients,
                           List<Slot> icons,
                           List<Slot> inputs,
                           List<Slot> outputs) {
    public static final PacketCodec<RegistryByteBuf, BridgeRecipe> CODEC = PacketCodec.of((recipe, buf) -> {
        Identifier.PACKET_CODEC.encode(buf, recipe.id);
        PacketCodecs.STRING.encode(buf, recipe.group);
        BridgeCategory.CODEC.collect(PacketCodecs.toList()).encode(buf, recipe.categories);
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.typeIcon);
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.entryIcon);
        PacketCodecs.optional(TextCodecs.PACKET_CODEC).encode(buf, Optional.ofNullable(recipe.texture));
        BridgeStack.CODEC.collect(PacketCodecs.toList()).collect(PacketCodecs.toList()).encode(buf, recipe.ingredients);
        Slot.CODEC.collect(PacketCodecs.toList()).encode(buf, recipe.icons);
        Slot.CODEC.collect(PacketCodecs.toList()).encode(buf, recipe.inputs);
        Slot.CODEC.collect(PacketCodecs.toList()).encode(buf, recipe.outputs);
    }, buf -> new BridgeRecipe(
            Identifier.PACKET_CODEC.decode(buf),
            PacketCodecs.STRING.decode(buf),
            BridgeCategory.CODEC.collect(PacketCodecs.toList()).decode(buf),
            ItemStack.OPTIONAL_PACKET_CODEC.decode(buf),
            ItemStack.OPTIONAL_PACKET_CODEC.decode(buf),
            PacketCodecs.optional(TextCodecs.PACKET_CODEC).decode(buf).orElse(null),
            BridgeStack.CODEC.collect(PacketCodecs.toList()).collect(PacketCodecs.toList()).decode(buf),
            Slot.CODEC.collect(PacketCodecs.toList()).decode(buf),
            Slot.CODEC.collect(PacketCodecs.toList()).decode(buf),
            Slot.CODEC.collect(PacketCodecs.toList()).decode(buf)
    ));
}
