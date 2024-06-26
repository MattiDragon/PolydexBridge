package dev.mattidragon.polydexbridge.mixin.client;

import dev.emi.emi.runtime.EmiReloadManager;
import dev.mattidragon.polydexbridge.PolydexBridgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmiReloadManager.class)
public class EmiReloadManagerMixin {
    @Unique
    private static boolean polydex_bridge$emiLoaded;
    @Unique
    private static boolean polydex_bridge$polyLoaded;
    
    static {
        PolydexBridgeClient.onPolyRecipes = () -> {
            polydex_bridge$polyLoaded = true;
            if (polydex_bridge$emiLoaded) {
                EmiReloadManager.reload();
            }
        };
    }
    
    @Inject(method = "reload", at = @At("HEAD"), remap = false, cancellable = true)
    private static void makeEmiWaitForPolydex(CallbackInfo ci) {
        if (!polydex_bridge$polyLoaded) {
            polydex_bridge$emiLoaded = true;
            ci.cancel();
        }
    }
}
