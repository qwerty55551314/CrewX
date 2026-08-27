package crewx.module.modules.render;

import crewx.module.Module;
import crewx.util.notifications.NotificationManager;
import crewx.util.notifications.NotificationType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifications extends Module {
    private static Notifications INSTANCE;

    public Notifications() {
        super("Notifications", true);
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void push(String title, String message, boolean enabled) {
        NotificationManager.getManager().post(
                title,
                message == null ? "" : message,
                2500,
                enabled ? NotificationType.OKAY : NotificationType.WARNING
        );
    }

    public static void pushRaw(String title, String message) {
        NotificationManager.getManager().post(
                title,
                message == null ? "" : message,
                3000,
                NotificationType.NOTIFY
        );
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            NotificationManager.getManager().updateAndRender();
        }
    }
}
