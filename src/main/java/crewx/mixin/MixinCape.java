package crewx.mixin;

import crewx.CrewX;
import crewx.module.modules.render.Cape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {AbstractClientPlayer.class}, priority = 9999)
public class MixinCape {
    @Inject(
            method = {"getLocationCape"},
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if (CrewX.moduleManager == null) return;
        if ((Object) this != Minecraft.getMinecraft().thePlayer) return;
        Cape cape = (Cape) CrewX.moduleManager.modules.get(Cape.class);
        if (cape == null || !cape.isEnabled()) return;

        ResourceLocation customCape = cape.getCapeTexture();
        if (customCape != null) {
            cir.setReturnValue(customCape);
        }
    }
}
