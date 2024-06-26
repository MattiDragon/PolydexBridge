package dev.mattidragon.polydexbridge.mixin;

import dev.mattidragon.polydexbridge.PolydexBridge;
import eu.pb4.polydex.impl.PolydexImpl;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(PolydexImpl.class)
public class PolydexImplMixin {
    @Shadow(remap = false) 
    private static @Nullable CompletableFuture<Void> cacheBuilding;

    @Inject(method = "rebuild", at = @At("TAIL"), remap = false)
    private static void captureFuture(MinecraftServer server, CallbackInfo ci) {
        if (cacheBuilding == null) {
            PolydexBridge.LOGGER.error("Caches are not building???");
            return;
        }
        cacheBuilding.thenAcceptAsync(__ -> PolydexBridge.sendRecipes(server), server);
    }
}
