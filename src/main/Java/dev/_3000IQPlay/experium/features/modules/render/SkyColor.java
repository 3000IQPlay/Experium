package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class SkyColor
        extends Module {
    private static SkyColor INSTANCE = new SkyColor();
    private Setting<Integer> red = this.register(new Setting<Integer>("Red", 135, 0, 255));
    private Setting<Integer> green = this.register(new Setting<Integer>("Green", 0, 0, 255));
    private Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    private Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    private Setting<Boolean> fog = this.register(new Setting<Boolean>("Fog", true));

    public SkyColor() {
        super("SkyColor", "Changes the color of the sky", Module.Category.RENDER, false, false, false);
    }

    public static SkyColor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkyColor();
		}
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void fogColors(final EntityViewRenderEvent.FogColors event) {
        event.setRed(this.red.getValue() / 255f);
        event.setGreen(this.green.getValue() / 255f);
        event.setBlue(this.blue.getValue() / 255f);
    }

    @SubscribeEvent
    public void fog_density(final EntityViewRenderEvent.FogDensity event) {
        if (this.fog.getValue().booleanValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void onUpdate() {
        if (this.rainbow.getValue().booleanValue()) {
            doRainbow();
        }
    }

    public void doRainbow() {
        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);
        red.setValue((color_rgb_o >> 16) & 0xFF);
        green.setValue((color_rgb_o >> 8) & 0xFF);
        blue.setValue(color_rgb_o & 0xFF);
    }
}
