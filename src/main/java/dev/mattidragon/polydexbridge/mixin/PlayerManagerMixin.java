package dev.mattidragon.polydexbridge.mixin;

import dev.mattidragon.polydexbridge.PolydexBridge;
import eu.pb4.polydex.impl.PolydexImpl;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SynchronizeRecipesS2CPacket;<init>(Ljava/util/Collection;)V"))
    private void syncPolyRecipesOnJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        if (PolydexImpl.isReady()) {
            ServerPlayNetworking.send(player, PolydexBridge.createPacket(player));
        }
    }
}
