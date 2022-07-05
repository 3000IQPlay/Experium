package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.PerspectiveEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect
        extends Module {
    public Setting<Float> aspect = this.register(new Setting<Float>("Alpha", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));

    public Aspect() {
        super("Aspect", "Change ur screen aspect like fortnite.", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent perspectiveEvent) {
        perspectiveEvent.setAspect(this.aspect.getValue().floatValue());
    }
}