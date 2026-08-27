package crewx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crewx.accountmanager.AccountManager;
import crewx.command.CommandManager;
import crewx.command.commands.*;
import crewx.config.Config;
import crewx.event.EventManager;
import crewx.management.*;
import crewx.module.Module;
import crewx.module.ModuleManager;
import crewx.module.modules.*;
import crewx.property.Property;
import crewx.property.PropertyManager;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class CrewX {
    public static String clientName = "&7[&bCrewX&7]&r ";
    public static String version;
    public static RotationManager rotationManager;
    public static FloatManager floatManager;
    public static BlinkManager blinkManager;
    public static DelayManager delayManager;
    public static LagManager lagManager;
    public static PlayerStateManager playerStateManager;
    public static FriendManager friendManager;
    public static TargetManager targetManager;
    public static PropertyManager propertyManager;
    public static crewx.script.ScriptManager scriptManager;
    public static ModuleManager moduleManager;
    public static CommandManager commandManager;

    public CrewX() {
        this.init();
    }

    public void init() {
        rotationManager = new RotationManager();
        floatManager = new FloatManager();
        blinkManager = new BlinkManager();
        delayManager = new DelayManager();
        lagManager = new LagManager();
        playerStateManager = new PlayerStateManager();
        friendManager = new FriendManager();
        targetManager = new TargetManager();
        propertyManager = new PropertyManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        EventManager.register(rotationManager);
        EventManager.register(floatManager);
        EventManager.register(blinkManager);
        EventManager.register(delayManager);
        EventManager.register(lagManager);
        EventManager.register(moduleManager);
        EventManager.register(commandManager);
        moduleManager.modules.put(AimAssist.class, new AimAssist());
        moduleManager.modules.put(Backtrack.class, new Backtrack());
        moduleManager.modules.put(Overlay.class, new Overlay());
        moduleManager.modules.put(Fakelag.class, new Fakelag());
        moduleManager.modules.put(AntiAFK.class, new AntiAFK());
        moduleManager.modules.put(AntiDebuff.class, new AntiDebuff());
        moduleManager.modules.put(AntiFireball.class, new AntiFireball());
        moduleManager.modules.put(AntiObbyTrap.class, new AntiObbyTrap());
        moduleManager.modules.put(AntiObfuscate.class, new AntiObfuscate());
        moduleManager.modules.put(AntiVoid.class, new AntiVoid());
        moduleManager.modules.put(AutoClicker.class, new AutoClicker());
        moduleManager.modules.put(AutoAnduril.class, new AutoAnduril());
        moduleManager.modules.put(AutoHeal.class, new AutoHeal());
        moduleManager.modules.put(AutoTool.class, new AutoTool());
        moduleManager.modules.put(AutoRecraft.class, new AutoRecraft());
        moduleManager.modules.put(AutoRefill.class, new AutoRefill());
        moduleManager.modules.put(AutoPot.class, new AutoPot());
        moduleManager.modules.put(ThrowPot.class, new ThrowPot());
        moduleManager.modules.put(SprintReset.class, new SprintReset());
        moduleManager.modules.put(DynamicIsland.class, new DynamicIsland());
        moduleManager.modules.put(Piercing.class, new Piercing());
        moduleManager.modules.put(HackerDetector.class, new HackerDetector());
        moduleManager.modules.put(AutoSoup.class, new AutoSoup());
        moduleManager.modules.put(BedNuker.class, new BedNuker());
        moduleManager.modules.put(BedESP.class, new BedESP());
        moduleManager.modules.put(BedTracker.class, new BedTracker());
        moduleManager.modules.put(Blink.class, new Blink());
        moduleManager.modules.put(Chams.class, new Chams());
        moduleManager.modules.put(ChestESP.class, new ChestESP());
        moduleManager.modules.put(ChestStealer.class, new ChestStealer());
        moduleManager.modules.put(BridgeAssist.class, new BridgeAssist());
        moduleManager.modules.put(KnockbackDelay.class, new KnockbackDelay());
        moduleManager.modules.put(BlockHit.class, new BlockHit());
        moduleManager.modules.put(AutoHeadHitter.class, new AutoHeadHitter());
        moduleManager.modules.put(BedDefender.class, new BedDefender());
        moduleManager.modules.put(Displace.class, new Displace());
        moduleManager.modules.put(Notifications.class, new Notifications());
        moduleManager.modules.put(ESP.class, new ESP());
        moduleManager.modules.put(FastPlace.class, new FastPlace());
        moduleManager.modules.put(Freeze.class, new Freeze());
        moduleManager.modules.put(Fly.class, new Fly());
        moduleManager.modules.put(FullBright.class, new FullBright());
        moduleManager.modules.put(GhostHand.class, new GhostHand());
        moduleManager.modules.put(GuiModule.class, new GuiModule());
        moduleManager.modules.put(HitSelect.class, new HitSelect());
        moduleManager.modules.put(HUD.class, new HUD());
        moduleManager.modules.put(MoreKB.class, new MoreKB());
        moduleManager.modules.put(Indicators.class, new Indicators());
        moduleManager.modules.put(InventoryClicker.class, new InventoryClicker());
        moduleManager.modules.put(InvManager.class, new InvManager());
        moduleManager.modules.put(InvWalk.class, new InvWalk());
        moduleManager.modules.put(ItemESP.class, new ItemESP());
        moduleManager.modules.put(Jesus.class, new Jesus());
        moduleManager.modules.put(KeepSprint.class, new KeepSprint());
        moduleManager.modules.put(HitBox.class, new HitBox());
        moduleManager.modules.put(KillAura.class, new KillAura());
        moduleManager.modules.put(Criticals.class, new Criticals());
        moduleManager.modules.put(BowAimbot.class, new BowAimbot());
        moduleManager.modules.put(LagRange.class, new LagRange());
        moduleManager.modules.put(LongJump.class, new LongJump());
        moduleManager.modules.put(MCF.class, new MCF());
        moduleManager.modules.put(NameTags.class, new NameTags());
        moduleManager.modules.put(NickHider.class, new NickHider());
        moduleManager.modules.put(NoFall.class, new NoFall());
        moduleManager.modules.put(NoHitDelay.class, new NoHitDelay());
        moduleManager.modules.put(NoHurtCam.class, new NoHurtCam());
        moduleManager.modules.put(NoJumpDelay.class, new NoJumpDelay());
        moduleManager.modules.put(NoRotate.class, new NoRotate());
        moduleManager.modules.put(NoSlow.class, new NoSlow());
        moduleManager.modules.put(Radar.class, new Radar());
        moduleManager.modules.put(Reach.class, new Reach());
        moduleManager.modules.put(SafeWalk.class, new SafeWalk());
        moduleManager.modules.put(Scaffold.class, new Scaffold());
        moduleManager.modules.put(AutoBlockIn.class, new AutoBlockIn());
        moduleManager.modules.put(Spammer.class, new Spammer());
        moduleManager.modules.put(Speed.class, new Speed());
        moduleManager.modules.put(SpeedMine.class, new SpeedMine());
        moduleManager.modules.put(Sprint.class, new Sprint());
        moduleManager.modules.put(TargetHUD.class, new TargetHUD());
        moduleManager.modules.put(TargetStrafe.class, new TargetStrafe());
        moduleManager.modules.put(Tracers.class, new Tracers());
        moduleManager.modules.put(Trajectories.class, new Trajectories());
        moduleManager.modules.put(Velocity.class, new Velocity());
        moduleManager.modules.put(ViewClip.class, new ViewClip());
        moduleManager.modules.put(Wtap.class, new Wtap());
        moduleManager.modules.put(Xray.class, new Xray());
        moduleManager.modules.put(Insults.class, new Insults());
        moduleManager.modules.put(AutoChest.class, new AutoChest());
        moduleManager.modules.put(Cape.class, new Cape());
        moduleManager.modules.put(AntiBot.class, new AntiBot());
        moduleManager.modules.put(Animations.class, new Animations());
        moduleManager.modules.put(StaffDetector.class, new StaffDetector());
        moduleManager.modules.put(TickBase.class, new TickBase());
        moduleManager.modules.put(RodAimbot.class, new RodAimbot());
        moduleManager.modules.put(RemoteShop.class, new RemoteShop());
        moduleManager.modules.put(AutoRegister.class, new AutoRegister());
        commandManager.commands.add(new BindCommand());
        commandManager.commands.add(new ConfigCommand());
        commandManager.commands.add(new DenickCommand());
        commandManager.commands.add(new FriendCommand());
        commandManager.commands.add(new HelpCommand());
        commandManager.commands.add(new HideCommand());
        commandManager.commands.add(new IgnCommand());
        commandManager.commands.add(new ItemCommand());
        commandManager.commands.add(new ListCommand());
        commandManager.commands.add(new ModuleCommand());
        commandManager.commands.add(new PlayerCommand());
        commandManager.commands.add(new ScriptCommand());
        commandManager.commands.add(new ShowCommand());
        commandManager.commands.add(new TargetCommand());
        commandManager.commands.add(new ToggleCommand());
        commandManager.commands.add(new VclipCommand());
        for (Module module : moduleManager.modules.values()) {
            ArrayList<Property<?>> properties = new ArrayList<>();
            for (final Field field : module.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                final Object obj;
                try {
                    obj = field.get(module);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (obj instanceof Property<?>) {
                    ((Property<?>) obj).setOwner(module);
                    properties.add((Property<?>) obj);
                }
            }
            propertyManager.properties.put(module, properties);
            EventManager.register(module);
        }
        scriptManager = new crewx.script.ScriptManager();
        scriptManager.init();

        Config config = new Config("default", true);
        if (config.file.exists()) {
            config.load();
        }
        if (friendManager.file.exists()) {
            friendManager.load();
        }
        if (targetManager.file.exists()) {
            targetManager.load();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(config::save));

        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(CrewX.class.getResourceAsStream("/version.json")), StandardCharsets.UTF_8)) {
            JsonObject modInfo = new JsonParser().parse(reader).getAsJsonObject();
            version = modInfo.get("version").getAsString();
        } catch (Exception e) {
            version = "dev";
        }

        AccountManager.init();
    }

    public static int getCategoryForModule(Module module) {
        if (module instanceof crewx.module.modules.AimAssist ||
            module instanceof crewx.module.modules.Backtrack ||
            module instanceof crewx.module.modules.Fakelag ||
            module instanceof crewx.module.modules.AutoClicker ||
            module instanceof crewx.module.modules.KillAura ||
            module instanceof crewx.module.modules.Wtap ||
            module instanceof crewx.module.modules.Velocity ||
            module instanceof crewx.module.modules.Freeze ||
            module instanceof crewx.module.modules.Reach ||
            module instanceof crewx.module.modules.TargetStrafe ||
            module instanceof crewx.module.modules.NoHitDelay ||
            module instanceof crewx.module.modules.AntiFireball ||
            module instanceof crewx.module.modules.LagRange ||
            module instanceof crewx.module.modules.HitBox ||
            module instanceof crewx.module.modules.MoreKB ||
            module instanceof crewx.module.modules.HitSelect ||
            module instanceof crewx.module.modules.Piercing ||
            module instanceof crewx.module.modules.BlockHit ||
            module instanceof crewx.module.modules.Displace ||
            module instanceof crewx.module.modules.KnockbackDelay ||
            module instanceof crewx.module.modules.SprintReset ||
            module instanceof crewx.module.modules.Criticals ||
            module instanceof crewx.module.modules.BowAimbot ||
            module instanceof crewx.module.modules.TickBase) {
            return 0;
        }
        if (module instanceof crewx.module.modules.AntiAFK ||
            module instanceof crewx.module.modules.Fly ||
            module instanceof crewx.module.modules.Speed ||
            module instanceof crewx.module.modules.LongJump ||
            module instanceof crewx.module.modules.Sprint ||
            module instanceof crewx.module.modules.SafeWalk ||
            module instanceof crewx.module.modules.Jesus ||
            module instanceof crewx.module.modules.Blink ||
            module instanceof crewx.module.modules.NoFall ||
            module instanceof crewx.module.modules.NoSlow ||
            module instanceof crewx.module.modules.KeepSprint ||
            module instanceof crewx.module.modules.BridgeAssist ||
            module instanceof crewx.module.modules.NoJumpDelay ||
            module instanceof crewx.module.modules.AntiVoid) {
            return 1;
        }
        if (module instanceof crewx.module.modules.ESP ||
            module instanceof crewx.module.modules.Chams ||
            module instanceof crewx.module.modules.FullBright ||
            module instanceof crewx.module.modules.Tracers ||
            module instanceof crewx.module.modules.NameTags ||
            module instanceof crewx.module.modules.Xray ||
            module instanceof crewx.module.modules.TargetHUD ||
            module instanceof crewx.module.modules.Indicators ||
            module instanceof crewx.module.modules.BedESP ||
            module instanceof crewx.module.modules.ItemESP ||
            module instanceof crewx.module.modules.ViewClip ||
            module instanceof crewx.module.modules.NoHurtCam ||
            module instanceof crewx.module.modules.HUD ||
            module instanceof crewx.module.modules.GuiModule ||
            module instanceof crewx.module.modules.ChestESP ||
            module instanceof crewx.module.modules.Trajectories ||
            module instanceof crewx.module.modules.Radar ||
            module instanceof crewx.module.modules.DynamicIsland ||
            module instanceof crewx.module.modules.Notifications ||
            module instanceof crewx.module.modules.Cape ||
            module instanceof crewx.module.modules.Animations ||
            false) {
            return 2;
        }
        if (module instanceof crewx.module.modules.AutoHeal ||
            module instanceof crewx.module.modules.AutoTool ||
            module instanceof crewx.module.modules.ChestStealer ||
            module instanceof crewx.module.modules.InvManager ||
            module instanceof crewx.module.modules.InvWalk ||
            module instanceof crewx.module.modules.Scaffold ||
            module instanceof crewx.module.modules.AutoBlockIn ||
            module instanceof crewx.module.modules.SpeedMine ||
            module instanceof crewx.module.modules.FastPlace ||
            module instanceof crewx.module.modules.GhostHand ||
            module instanceof crewx.module.modules.MCF ||
            module instanceof crewx.module.modules.AntiDebuff ||
            module instanceof crewx.module.modules.AutoRecraft ||
            module instanceof crewx.module.modules.AutoRefill ||
            module instanceof crewx.module.modules.AutoSoup ||
            module instanceof crewx.module.modules.AutoHeadHitter ||
            module instanceof crewx.module.modules.BedDefender ||
            module instanceof crewx.module.modules.AutoChest ||
            module instanceof crewx.module.modules.AutoPot ||
            module instanceof crewx.module.modules.ThrowPot ||
            module instanceof crewx.module.modules.RemoteShop ||
            module instanceof crewx.module.modules.AutoRegister) {
            return 3;
        }
        if (module instanceof crewx.module.modules.Overlay) {
            return 4;
        }
        if (module instanceof crewx.script.ScriptModule) {
            return 5;
        }
        return 4;
    }
}
