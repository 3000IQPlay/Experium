package dev._3000IQPlay.experium.features.modules.client;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.Util;

public class NickHider
        extends Module {
    private static NickHider instance;
    public final Setting<Boolean> changeOwn = this.register(new Setting<Boolean>("MyName", true));
    public final Setting<String> ownName = this.register(new Setting<Object>("Name", "Name here...", v -> this.changeOwn.getValue()));

    public NickHider() {
        super("NickHider", "Helps with creating media", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    public static NickHider getInstance() {
        if (instance == null) {
            instance = new NickHider();
        }
        return instance;
    }

    public static String getPlayerName() {
        if (NickHider.fullNullCheck() || !PingBypass.getInstance().isConnected()) {
            return Util.mc.getSession().getUsername();
        }
        String name = PingBypass.getInstance().getPlayerName();
        if (name == null || name.isEmpty()) {
            return Util.mc.getSession().getUsername();
        }
        return name;
    }
}

