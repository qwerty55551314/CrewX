package crewx.util;

import crewx.CrewX;
import crewx.event.EventTarget;
import crewx.event.types.EventType;
import crewx.events.TickEvent;
import crewx.module.Module;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.util.concurrent.atomic.AtomicBoolean;

public final class DiscordRPC extends Module {
    public static final String APPLICATION_ID = "1542689922194866226";
    private static final long UPDATE_INTERVAL_MS = 2_000L;
    private static final String LARGE_IMAGE_KEY = "crewx";

    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile boolean initialized;
    private volatile long created;
    private volatile long lastUpdate;
    private Thread callbackThread;

    public DiscordRPC() {
        super("Discord RPC", true);
    }

    @Override
    public void onEnabled() {
        this.start();
    }

    @Override
    public void onDisabled() {
        this.shutdown();
    }

    public void start() {
        if (!this.running.compareAndSet(false, true)) return;
        this.created = System.currentTimeMillis();
        try {
            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                    .setReadyEventHandler(new ReadyCallback() {
                        @Override
                        public void apply(DiscordUser user) {
                        }
                    })
                    .build();
            net.arikia.dev.drpc.DiscordRPC.discordInitialize(APPLICATION_ID, handlers, true);
            this.initialized = true;
            this.callbackThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running.get()) {
                        try {
                            net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
                            Thread.sleep(1_500L);
                        } catch (InterruptedException ignored) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Throwable ignored) {
                            break;
                        }
                    }
                }
            }, "CrewX Discord RPC Callback");
            this.callbackThread.setDaemon(true);
            this.callbackThread.start();
        } catch (Throwable ignored) {
            this.initialized = false;
            this.running.set(false);
        }
    }

    public boolean isRunning() {
        return this.running.get() && this.initialized;
    }

    public void shutdown() {
        if (!this.running.getAndSet(false)) return;
        Thread callback = this.callbackThread;
        if (callback != null) callback.interrupt();
        if (this.initialized) {
            try {
                net.arikia.dev.drpc.DiscordRPC.discordShutdown();
            } catch (Throwable ignored) {
            }
        }
        this.initialized = false;
    }

    public void update(String details, String state) {
        if (!this.isRunning()) return;
        try {
            DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(sanitize(state, "In game"));
            builder.setBigImage(LARGE_IMAGE_KEY, "CrewX");
            builder.setDetails(sanitize(details, "Playing CrewX"));
            builder.setStartTimestamps(this.created);
            net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(builder.build());
        } catch (Throwable ignored) {
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (event.getType() != EventType.POST || !this.isRunning()) return;
        long now = System.currentTimeMillis();
        if (now - this.lastUpdate < UPDATE_INTERVAL_MS) return;
        this.lastUpdate = now;
        this.updateFromGame();
    }

    private void updateFromGame() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) return;
        String server = "Singleplayer";
        ServerData data = minecraft.getCurrentServerData();
        if (data != null && data.serverIP != null && !data.serverIP.trim().isEmpty()) {
            server = data.serverIP;
        }
        server = server.trim();
        try {
            DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(sanitize(this.buildState(server), "Singleplayer"));
            builder.setBigImage(LARGE_IMAGE_KEY, "CrewX");
            String smallImageKey = this.getSmallImageKey(server);
            if (smallImageKey != null) {
                builder.setSmallImage(smallImageKey, server);
            }
            builder.setDetails("Playing CrewX");
            builder.setStartTimestamps(this.created);
            net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(builder.build());
        } catch (Throwable ignored) {
        }
    }

    private String getSmallImageKey(String server) {
        String normalized = server == null ? "" : server.toLowerCase(java.util.Locale.ROOT);
        if (normalized.contains("mush")) return "mush";
        if (normalized.contains("hypixel")) return "hypixel";
        if (normalized.contains("hylex")) return "hylex";
        if (normalized.contains("kaizen")) return "kaizen";
        return null;
    }

    private String buildState(String server) {
        int total = 0;
        int active = 0;
        if (CrewX.moduleManager != null && CrewX.moduleManager.modules != null) {
            for (Module module : CrewX.moduleManager.modules.values()) {
                if (module == null) continue;
                total++;
                if (module.isEnabled()) active++;
            }
        }
        String moduleSummary = active + "/" + total + " modules";
        return moduleSummary + " | " + sanitize(server, "Singleplayer");
    }

    private static String sanitize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        return value.length() > 128 ? value.substring(0, 125) + "..." : value;
    }
}
