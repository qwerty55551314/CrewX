package crewx.module.modules.combat;
import crewx.module.modules.combat.*;
import crewx.module.modules.movement.*;
import crewx.module.modules.render.*;
import crewx.module.modules.player.*;
import crewx.module.modules.misc.*;

import crewx.event.EventTarget;
import crewx.event.types.EventType;
import crewx.events.LivingUpdateEvent;
import crewx.events.PacketEvent;
import crewx.events.Render3DEvent;
import crewx.events.TickEvent;
import crewx.module.Module;
import crewx.property.properties.FloatProperty;
import crewx.mixin.IAccessorMinecraft;
import crewx.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class TickBase extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public final FloatProperty lagRange = new FloatProperty("lag-range", 8.0F, 5.0F, 15.0F);

    private Mode mode = Mode.NONE;
    private long time, balance;
    private double range, distance;
    private Entity target;

    public TickBase() {
        super("TickBase", false);
    }

    @EventTarget
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!isEnabled() || mode == Mode.REDUCING) return;

        target = getTarget(20);
        if (target == null) return;

        distance = RotationUtil.distanceToEntity(target);
        double range = distance;

        if (range > 3 && balance >= 50 && mode == Mode.BASING) {
            balance -= 50;
            ((IAccessorMinecraft) mc).getTimer().elapsedTicks += 1;
        } else {
            if (balance != 0) {
            }
            balance = 0;
            mode = Mode.NONE;
        }

        if (range < lagRange.getValue().doubleValue() && this.range >= lagRange.getValue().doubleValue() && mode == Mode.NONE) {
            mode = Mode.REDUCING;
            time = System.currentTimeMillis();
            balance = 0;
        }

        this.range = range;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (!isEnabled()) return;
        if (mode == Mode.REDUCING && event.getType() == EventType.RECEIVE) {
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (!isEnabled() || event.getType() != EventType.PRE) return;
        if (target == null || mc.thePlayer == null) return;
        distance = RotationUtil.distanceToEntity(target);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (!isEnabled() || mode != Mode.REDUCING || target == null) return;

        if (distance <= 4 || System.currentTimeMillis() - time >= ((range / (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.36 : 0.25)) * 25) + 25) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1;
            mode = Mode.BASING;
            balance = System.currentTimeMillis() - time;
            return;
        }

        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 0;
    }

    private Entity getTarget(int range) {
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && entity != mc.thePlayer && !entity.isDead) {
                double dist = mc.thePlayer.getDistanceToEntity(entity);
                if (dist <= range && dist < nearestDistance) {
                    nearestDistance = dist;
                    nearest = entity;
                }
            }
        }
        return nearest;
    }

    @Override
    public void onDisabled() {
        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1;
        mode = Mode.NONE;
        balance = 0;
        time = 0;
        target = null;
    }

    private enum Mode {
        REDUCING, BASING, NONE
    }
}
