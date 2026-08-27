package crewx.mixin;

import crewx.accountmanager.gui.GuiAccountManager;
import crewx.gui.CrewXTheme;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {
    private static final int ALT_MANAGER_ID = 1337;
    private static final int QUIT_ID = 1338;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void crewx$initGui(CallbackInfo callbackInfo) {
        buttonList.clear();
        int wide = Math.min(300, Math.max(220, width / 5));
        int narrow = (wide - 12) / 2;
        int left = width / 2 - wide / 2;
        int top = height / 2 - 48;
        buttonList.add(new GuiButton(1, left, top, wide, 28, "Singleplayer"));
        buttonList.add(new GuiButton(2, left, top + 36, wide, 28, "Multiplayer"));
        buttonList.add(new GuiButton(0, left, top + 72, narrow, 28, "Options"));
        buttonList.add(new GuiButton(ALT_MANAGER_ID, left + narrow + 12, top + 72, narrow, 28, "Alts"));
        buttonList.add(new GuiButton(QUIT_ID, width - 86, height - 31, 74, 20, "Quit"));
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void crewx$drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        CrewXTheme.drawBackground(width, height);
        CrewXTheme.drawMainTitle(width, height);
        super.drawScreen(mouseX, mouseY, partialTicks);
        callbackInfo.cancel();
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void crewx$actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        if (button == null || !button.enabled) return;
        if (button.id == ALT_MANAGER_ID) {
            mc.displayGuiScreen(new GuiAccountManager(this));
            callbackInfo.cancel();
        } else if (button.id == 0) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            callbackInfo.cancel();
        } else if (button.id == QUIT_ID) {
            mc.shutdown();
            callbackInfo.cancel();
        }
    }
}
