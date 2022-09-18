package dev._3000IQPlay.experium.features.modules.client;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.TextUtil;

public class ModuleTools
        extends Module {
    private static ModuleTools INSTANCE;
    public Setting<Notifier> notifier = register(new Setting<Notifier>("ModuleNotifier", Notifier.EXPERIUM));
    public Setting<PopNotifier> popNotifier = register(new Setting<PopNotifier>("PopNotifier", PopNotifier.NONE));
	public Setting<TextUtil.Color> abyssColor = this.register(new Setting<TextUtil.Color>("AbyssTextColor", TextUtil.Color.AQUA, color -> this.notifier.getValue() == Notifier.ABYSS));

    public ModuleTools() {
        super("ModuleTools", "Change settings", Category.CLIENT, true, false, false);
        INSTANCE = this;
    }


    public static ModuleTools getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleTools();
        }
        return INSTANCE;
    }
	
	public static enum Notifier {
        EXPERIUM,
        FUTURE,
		DOTGOD,
        MUFFIN,
        WEATHER,
        SNOW,
        PYRO,
        CATALYST,
        KONAS,
        RUSHERHACK,
        LEGACY,
        EUROPA,
		ABYSS,
        LUIGIHACK;
    }

    public static enum PopNotifier {
        PHOBOS,
        FUTURE,
        DOTGOD,
        NONE;
    }
}
