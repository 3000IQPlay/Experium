package dev._3000IQPlay.experium.features.modules.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.ClientEvent;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiPing
        extends Module {
    private static AntiPing instance;
    public final Setting<Boolean> full = this.register(new Setting<Boolean>("Full", false));
    private final Map<String, Setting> servers = new ConcurrentHashMap<String, Setting>();
    public Setting<String> newIP = this.register(new Setting<Object>("NewServer", "Add Server...", v -> this.full.getValue() == false));
    public Setting<Boolean> showServer = this.register(new Setting<Object>("ShowServers", Boolean.valueOf(false), v -> this.full.getValue() == false));

    public AntiPing() {
        super("AntiPing", "Prevents DDoS attacks via multiplayer list.", Module.Category.PLAYER, false, false, true);
        instance = this;
    }

    public static AntiPing getInstance() {
        if (instance == null) {
            instance = new AntiPing();
        }
        return instance;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (Experium.configManager.loadingConfig || Experium.configManager.savingConfig) {
            return;
        }
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.newIP) && !this.shouldntPing(this.newIP.getPlannedValue()) && !event.getSetting().getPlannedValue().equals(event.getSetting().getDefaultValue())) {
                Setting setting = this.register(new Setting<Boolean>(this.newIP.getPlannedValue(), Boolean.valueOf(true), v -> this.showServer.getValue() != false && this.full.getValue() == false));
                this.registerServer(setting);
                Command.sendMessage("<AntiPing> Added new Server: " + this.newIP.getPlannedValue());
                event.setCanceled(true);
            } else {
                Setting setting = event.getSetting();
                if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newIP) || setting.equals(this.showServer) || setting.equals(this.full)) {
                    return;
                }
                if (setting.getValue() instanceof Boolean && !((Boolean) setting.getPlannedValue()).booleanValue()) {
                    this.servers.remove(setting.getName().toLowerCase());
                    this.unregister(setting);
                    event.setCanceled(true);
                }
            }
        }
    }

    public void registerServer(Setting setting) {
        this.servers.put(setting.getName().toLowerCase(), setting);
    }

    public boolean shouldntPing(String ip) {
        return !this.isOff() && (this.full.getValue() != false || this.servers.get(ip.toLowerCase()) != null);
    }
}

