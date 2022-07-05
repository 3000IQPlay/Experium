package dev._3000IQPlay.experium.features.modules.client;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;

public class FriendSettings extends Module {
    private static FriendSettings INSTANCE;

    public Setting<Boolean> notify = this.register(new Setting("Notify", false));

    public FriendSettings() {
        super("FriendSettings", "Change aspects of friends", Category.CLIENT, true, false, false);
        INSTANCE = this;
    }

    public static FriendSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FriendSettings();
        }
        return INSTANCE;
    }
}