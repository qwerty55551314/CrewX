package crewx.module.modules;

import crewx.CrewX;
import crewx.module.Module;
import crewx.util.ItemUtil;
import crewx.util.TeamUtil;
import crewx.property.properties.BooleanProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class GhostHand extends Module {
    public final BooleanProperty teamsOnly = new BooleanProperty("team-only", true);
    public final BooleanProperty ignoreWeapons = new BooleanProperty("ignore-weapons", false);

    public GhostHand() {
        super("GhostHand", false);
    }

    public boolean shouldSkip(Entity entity) {
        AntiBot antiBot = (AntiBot) CrewX.moduleManager.modules.get(AntiBot.class);
        return entity instanceof EntityPlayer
                && !(antiBot.isEnabled() && antiBot.isBot((EntityPlayer) entity))
                && (!this.teamsOnly.getValue() || TeamUtil.isSameTeam((EntityPlayer) entity))
                && (!this.ignoreWeapons.getValue() || !ItemUtil.hasRawUnbreakingEnchant());
    }
}