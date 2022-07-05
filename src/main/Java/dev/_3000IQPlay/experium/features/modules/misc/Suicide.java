package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.features.modules.Module;

public class Suicide
        extends Module {
    public Suicide() {
        super("Suicide", "Auto suicide.", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        mc.player.sendChatMessage("/kill");
        toggle();
    }
}