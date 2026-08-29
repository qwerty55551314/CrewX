package crewx.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityList;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.network.NetHandlerPlayClient", priority = 10000)
public abstract class MixinNetHandlerPlayClient {
    @Inject(method = "handleSpawnMob", at = @At("HEAD"), cancellable = true)
    private void crewx$handleSpawnMob(S0FPacketSpawnMob packet, CallbackInfo callbackInfo) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null || packet == null || !isValidSpawn(packet)) {
            callbackInfo.cancel();
        }
    }

    private static boolean isValidSpawn(S0FPacketSpawnMob packet) {
        int entityType = packet.getEntityType();
        if (entityType < 0 || EntityList.getClassFromID(entityType) == null) {
            return false;
        }
        return packet.getEntityID() != 0;
    }
}
