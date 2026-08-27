package crewx.mixin;

import crewx.gui.CrewXTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
    @Shadow protected int width;
    @Shadow protected int height;

    @Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    private void crewx$drawDefaultBackground(CallbackInfo callbackInfo) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu)) return;
        CrewXTheme.drawScreenBackground(width, height);
        callbackInfo.cancel();
    }
}
