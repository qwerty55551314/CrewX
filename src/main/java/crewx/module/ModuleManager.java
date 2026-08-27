package crewx.module;
import crewx.CrewX;
import crewx.event.EventTarget;
import crewx.events.KeyEvent;
import crewx.module.modules.GuiModule;
import crewx.module.modules.HUD;
import crewx.util.ChatUtil;
import java.util.LinkedHashMap;
public class ModuleManager {
    public final LinkedHashMap<Object, Module> modules = new LinkedHashMap<>();

    public Module getModule(String string) {
        return this.modules.values().stream().filter(mD -> mD.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public Module getModule(Class<?> clazz){
        return this.modules.get(clazz);
    }

    @EventTarget
    public void onKey(KeyEvent event) {
        for (Module module : this.modules.values()) {
            if (module.getKey() != event.getKey()) {
                continue;
            }
            boolean shouldNotify = module.toggle();
            HUD hud = (HUD) this.modules.get(HUD.class);
            if (hud != null && shouldNotify) {
                shouldNotify = hud.toggleAlerts.getValue();
            }
            if(module instanceof GuiModule){
                shouldNotify = false;
            }
            if (shouldNotify) {
                String status = module.isEnabled() ? "&a&lON" : "&c&lOFF";
                String message = String.format("%s%s: %s&r", CrewX.clientName, module.getName(), status);
                ChatUtil.sendFormatted(message);
            }
        }
    }

}