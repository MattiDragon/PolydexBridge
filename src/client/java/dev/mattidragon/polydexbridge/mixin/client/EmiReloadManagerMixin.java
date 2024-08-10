package dev.mattidragon.polydexbridge.mixin.client;

import dev.emi.emi.runtime.EmiReloadManager;
import dev.mattidragon.polydexbridge.PolydexBridge;
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
            PolydexBridge.LOGGER.info("Polydex recipes received");
            polydex_bridge$polyLoaded = true;
            if (polydex_bridge$emiLoaded) {
                PolydexBridge.LOGGER.info("EMI already ready, proceeding with reload");
                EmiReloadManager.reload();
            } else {
                PolydexBridge.LOGGER.info("Waiting on EMI to begin reloading");
            }
        };
    }
    
    @Inject(method = "reload", at = @At("HEAD"), remap = false, cancellable = true)
    private static void makeEmiWaitForPolydex(CallbackInfo ci) {
        if (!polydex_bridge$polyLoaded && PolydexBridgeClient.waitForPoly) {
            PolydexBridge.LOGGER.info("Polydex recipes missing; delaying EMI reload");
            polydex_bridge$emiLoaded = true;
            ci.cancel();
        } else {
            polydex_bridge$polyLoaded = false;
            polydex_bridge$emiLoaded = false;
        }
    }
}
