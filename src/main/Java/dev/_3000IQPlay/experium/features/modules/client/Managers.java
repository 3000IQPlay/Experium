package dev._3000IQPlay.experium.features.modules.client;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.ClientEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Managers
        extends Module {
    private static Managers INSTANCE = new Managers();
    public Setting<Boolean> betterFrames = this.register(new Setting<Boolean>("BetterMaxFPS", false));
    public Setting<Integer> betterFPS = this.register(new Setting<Object>("MaxFPS", Integer.valueOf(300), Integer.valueOf(30), Integer.valueOf(1000), v -> this.betterFrames.getValue()));
    public Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", true));
    public Setting<Integer> textRadarUpdates = this.register(new Setting<Integer>("TRUpdates", 500, 0, 1000));
    public Setting<Integer> respondTime = this.register(new Setting<Integer>("SeverTime", 500, 0, 1000));
    public Setting<Integer> moduleListUpdates = this.register(new Setting<Integer>("ALUpdates", 1000, 0, 1000));
    public Setting<Float> holeRange = this.register(new Setting<Float>("HoleRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(256.0f)));
    public Setting<Integer> holeUpdates = this.register(new Setting<Integer>("HoleUpdates", 100, 0, 1000));
    public Setting<Integer> holeSync = this.register(new Setting<Integer>("HoleSync", 10000, 1, 10000));
    public Setting<Boolean> safety = this.register(new Setting<Boolean>("SafetyPlayer", false));
    public Setting<Integer> safetyCheck = this.register(new Setting<Integer>("SafetyCheck", 50, 1, 150));
    public Setting<Integer> safetySync = this.register(new Setting<Integer>("SafetySync", 250, 1, 10000));
    public Setting<ThreadMode> holeThread = this.register(new Setting<ThreadMode>("HoleThread", ThreadMode.WHILE));
    public Setting<Boolean> speed = this.register(new Setting<Boolean>("Speed", true));
    public Setting<Boolean> oneDot15 = this.register(new Setting<Boolean>("1.15", false));
    public Setting<Boolean> tRadarInv = this.register(new Setting<Boolean>("TRadarInv", true));
    public Setting<Boolean> unfocusedCpu = this.register(new Setting<Boolean>("UnfocusedCPU", false));
    public Setting<Integer> cpuFPS = this.register(new Setting<Object>("UnfocusedFPS", Integer.valueOf(60), Integer.valueOf(1), Integer.valueOf(60), v -> this.unfocusedCpu.getValue()));
    public Setting<Integer> baritoneTimeOut = this.register(new Setting<Integer>("Baritone", 5, 1, 20));
    public Setting<Boolean> oneChunk = this.register(new Setting<Boolean>("OneChunk", false));

    public Managers() {
        super("Management", "ClientManagement", Module.Category.CLIENT, false, false, true);
        this.setInstance();
    }

    public static Managers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Managers();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2) {
            if (this.oneChunk.getPlannedValue().booleanValue()) {
                Managers.mc.gameSettings.renderDistanceChunks = 1;
            }
            if (event.getSetting() != null && this.equals(event.getSetting().getFeature())) {
                if (event.getSetting().equals(this.holeThread)) {
                    Experium.holeManager.settingChanged();
                }
            }
        }
    }

    public enum ThreadMode {
        POOL,
        WHILE,
        NONE
    }
}
