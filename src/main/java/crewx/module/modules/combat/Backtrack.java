package crewx.module.modules.combat;
import crewx.module.modules.combat.*;
import crewx.module.modules.movement.*;
import crewx.module.modules.render.*;
import crewx.module.modules.player.*;
import crewx.module.modules.misc.*;

import crewx.CrewX;
import crewx.event.EventTarget;
import crewx.event.types.EventType;
import crewx.event.types.Priority;
import crewx.events.AttackEvent;
import crewx.events.LoadWorldEvent;
import crewx.events.PacketEvent;
import crewx.events.Render3DEvent;
import crewx.events.TickEvent;
import crewx.module.Module;
import crewx.property.properties.BooleanProperty;
import crewx.property.properties.FloatProperty;
import crewx.property.properties.IntProperty;
import crewx.util.RenderUtil;
import crewx.util.TeamUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Backtrack extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public final IntProperty delay = new IntProperty("delay", 100, 10, 1000);
    public final IntProperty targetFlushDelay = new IntProperty("target-flush-delay", 100, 100, 1000);
    public final BooleanProperty distanceCheck = new BooleanProperty("distance-check", true);
    public final BooleanProperty cancelPong = new BooleanProperty("cancel-pong", true);
    public final BooleanProperty cancelKeepAlive = new BooleanProperty("cancel-keep-alive", true);
    public final BooleanProperty lineBox = new BooleanProperty("line-box", true);
    public final BooleanProperty filledBox = new BooleanProperty("filled-box", false);
    public final FloatProperty fillOpacity = new FloatProperty("fill-opacity", 45.0F, 20.0F, 100.0F);

    private final CopyOnWriteArrayList<PacketData> packets = new CopyOnWriteArrayList<PacketData>();
    private final Map<EntityLivingBase, PosData> posCache = new LinkedHashMap<EntityLivingBase, PosData>();
    private EntityLivingBase target;
    private long lastAttack;

    public Backtrack() {
        super("BackTrack", false);
        setKey(Keyboard.KEY_NONE);
    }

    @Override
    public void onDisabled() {
        target = null;
        flushPackets();
    }

    @Override
    public String[] getSuffix() {
        return new String[]{delay.getValue() + "ms"};
    }

    @EventTarget(Priority.HIGHEST)
    public void onAttack(AttackEvent event) {
        Entity attacked = event.getTarget();
        if (attacked instanceof EntityLivingBase && attacked != mc.thePlayer) {
            EntityLivingBase living = (EntityLivingBase) attacked;
            if (isValidTarget(living)) {
                target = living;
                lastAttack = System.currentTimeMillis();
            }
        }
    }

    @EventTarget(Priority.MEDIUM)
    public void onLoadWorld(LoadWorldEvent event) {
        target = null;
        flushPackets();
    }

    @EventTarget(Priority.HIGHEST)
    public void onTick(TickEvent event) {
        if (event.getType() != EventType.PRE) return;
        if (mc.thePlayer == null || mc.theWorld == null || mc.getNetHandler() == null) return;

        long currentTime = System.currentTimeMillis();
        if (target != null && currentTime - lastAttack > targetFlushDelay.getValue()) {
            target = null;
            flushPackets();
        }

        if (target != null && distanceCheck.getValue() && posCache.containsKey(target)) {
            PosData position = posCache.get(target);
            double dx = position.x - mc.thePlayer.posX;
            double dy = position.y - mc.thePlayer.posY;
            double dz = position.z - mc.thePlayer.posZ;
            double cachedDistance = dx * dx + dy * dy + dz * dz;
            if (cachedDistance < mc.thePlayer.getDistanceSq(target.posX, target.posY, target.posZ)) {
                target = null;
                flushPackets();
            }
        }

        if (target == null) return;
        for (PacketData data : packets) {
            if (currentTime - data.receiveTime < delay.getValue()) continue;
            processPacketSilent(data.packet);
            packets.remove(data);
        }
    }

    @EventTarget(Priority.MEDIUM)
    public void onRender3D(Render3DEvent event) {
        if (target == null || !lineBox.getValue() && !filledBox.getValue()) return;
        PosData position = posCache.get(target);
        if (position == null || mc.thePlayer == null) return;

        float progress = position.positionTime == 0L
                ? 1.0F
                : Math.min(1.0F, (System.currentTimeMillis() - position.positionTime) / 200.0F);
        double x = RenderUtil.lerpDouble(position.x, position.lastX, event.getPartialTicks());
        double y = RenderUtil.lerpDouble(position.y, position.lastY, event.getPartialTicks());
        double z = RenderUtil.lerpDouble(position.z, position.lastZ, event.getPartialTicks());
        AxisAlignedBB box = createBox(target, x, y, z).offset(
                -((crewx.mixin.IAccessorRenderManager) mc.getRenderManager()).getRenderPosX(),
                -((crewx.mixin.IAccessorRenderManager) mc.getRenderManager()).getRenderPosY(),
                -((crewx.mixin.IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ()
        );

        Color accent = GuiModule.getAccent();
        int red = accent.getRed();
        int green = accent.getGreen();
        int blue = accent.getBlue();
        RenderUtil.enableRenderState();
        if (lineBox.getValue()) {
            RenderUtil.drawBoundingBox(box, red, green, blue, (int) (220.0F * progress), 1.6F);
        }
        if (filledBox.getValue()) {
            RenderUtil.drawFilledBox(box, red, green, blue);
        }
        RenderUtil.disableRenderState();
    }

    @EventTarget(Priority.HIGH)
    public void onReceivePacket(PacketEvent event) {
        if (event.getType() != EventType.RECEIVE || target == null) return;

        Packet<?> packet = event.getPacket();
        if (!packet.getClass().getSimpleName().startsWith("S")) return;
        if (packet instanceof S00PacketKeepAlive && !cancelKeepAlive.getValue()) return;
        if (packet instanceof S02PacketChat || packet instanceof S29PacketSoundEffect
                || packet instanceof S06PacketUpdateHealth) return;
        if (packet instanceof S01PacketPong && !cancelPong.getValue()) return;
        if (packet instanceof S0CPacketSpawnPlayer || packet instanceof S0FPacketSpawnMob) return;

        if (packet instanceof S08PacketPlayerPosLook) {
            target = null;
            flushPackets();
            return;
        }

        if (packet instanceof S13PacketDestroyEntities) {
            S13PacketDestroyEntities destroy = (S13PacketDestroyEntities) packet;
            for (int entityId : destroy.getEntityIDs()) {
                if (target != null && entityId == target.getEntityId()) {
                    target = null;
                    flushPackets();
                    return;
                }
            }
            return;
        }

        if (packet instanceof S14PacketEntity) {
            updateRelativePosition((S14PacketEntity) packet);
            if (packet instanceof S14PacketEntity.S16PacketEntityLook) return;
        }

        if (packet instanceof S18PacketEntityTeleport) {
            updateTeleportPosition((S18PacketEntityTeleport) packet);
        }

        packets.add(new PacketData(System.currentTimeMillis(), packet));
        event.setCancelled(true);
    }

    private boolean isValidTarget(EntityLivingBase entity) {
        if (entity == null || entity.isDead) return false;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (TeamUtil.isSameTeam(player)) return false;
            AntiBot antiBot = (AntiBot) CrewX.moduleManager.modules.get(AntiBot.class);
            if (antiBot != null && antiBot.isEnabled() && antiBot.isBot(player)) return false;
        }
        return true;
    }

    private void updateRelativePosition(S14PacketEntity packet) {
        Entity entity = packet.getEntity(mc.theWorld);
        if (!(entity instanceof EntityLivingBase) || entity != target) return;

        PosData previous = posCache.get(entity);
        double lastX = previous == null ? entity.posX : previous.x;
        double lastY = previous == null ? entity.posY : previous.y;
        double lastZ = previous == null ? entity.posZ : previous.z;
        double x = lastX + packet.func_149062_c() / 32.0;
        double y = lastY + packet.func_149061_d() / 32.0;
        double z = lastZ + packet.func_149064_e() / 32.0;
        posCache.put((EntityLivingBase) entity, new PosData(x, y, z, lastX, lastY, lastZ, System.currentTimeMillis()));
    }

    private void updateTeleportPosition(S18PacketEntityTeleport packet) {
        Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());
        if (!(entity instanceof EntityLivingBase) || entity != target) return;

        PosData previous = posCache.get(entity);
        double lastX = previous == null ? entity.posX : previous.x;
        double lastY = previous == null ? entity.posY : previous.y;
        double lastZ = previous == null ? entity.posZ : previous.z;
        double x = packet.getX() / 32.0;
        double y = packet.getY() / 32.0;
        double z = packet.getZ() / 32.0;
        posCache.put((EntityLivingBase) entity, new PosData(x, y, z, lastX, lastY, lastZ, System.currentTimeMillis()));
    }

    private AxisAlignedBB createBox(EntityLivingBase entity, double x, double y, double z) {
        double halfWidth = entity.width / 2.0;
        double border = entity.getCollisionBorderSize();
        return new AxisAlignedBB(
                x - halfWidth - border, y - border, z - halfWidth - border,
                x + halfWidth + border, y + entity.height + border, z + halfWidth + border
        );
    }

    private void flushPackets() {
        PacketData data;
        while ((data = packets.isEmpty() ? null : packets.remove(0)) != null) {
            processPacketSilent(data.packet);
        }
        posCache.clear();
    }

    @SuppressWarnings("unchecked")
    private void processPacketSilent(Packet<?> packet) {
        try {
            if (mc.getNetHandler() != null) {
                ((Packet<INetHandlerPlayClient>) packet).processPacket(mc.getNetHandler());
            }
        } catch (Exception ignored) {
        }
    }

    private static final class PacketData {
        private final long receiveTime;
        private final Packet<?> packet;

        private PacketData(long receiveTime, Packet<?> packet) {
            this.receiveTime = receiveTime;
            this.packet = packet;
        }
    }

    private static final class PosData {
        private final double x;
        private final double y;
        private final double z;
        private final double lastX;
        private final double lastY;
        private final double lastZ;
        private final long positionTime;

        private PosData(double x, double y, double z, double lastX, double lastY, double lastZ, long positionTime) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.lastX = lastX;
            this.lastY = lastY;
            this.lastZ = lastZ;
            this.positionTime = positionTime;
        }
    }
}
