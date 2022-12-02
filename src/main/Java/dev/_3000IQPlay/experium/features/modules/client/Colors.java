package dev._3000IQPlay.experium.features.modules.client;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.ColorUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Colors
        extends Module {
    public static Colors INSTANCE;
    public Setting<Color> c = register(new Setting("Color", new Color(-1)));

    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", Boolean.valueOf(false), "Rainbow colors."));
    public Setting<Integer> rainbowSpeed = this.register(new Setting<Object>("Speed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowSaturation = this.register(new Setting<Object>("Saturation", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowBrightness = this.register(new Setting<Object>("Brightness", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public float hue;
    public Map<Integer, Integer> colorHeightMap = new HashMap<Integer, Integer>();

    public Colors() {
        super("Colors", "Universal colors.", Module.Category.CLIENT, true, false, true);
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        int colorSpeed = 101 - this.rainbowSpeed.getValue();
        float tempHue = this.hue = (float) (System.currentTimeMillis() % (long) (360 * colorSpeed)) / (360.0f * (float) colorSpeed);
        for (int i = 0; i <= 510; ++i) {
            this.colorHeightMap.put(i, Color.HSBtoRGB(tempHue, (float) this.rainbowSaturation.getValue().intValue() / 255.0f, (float) this.rainbowBrightness.getValue().intValue() / 255.0f));
            tempHue += 0.0013071896f;
        }
        if (ClickGui.getInstance().colorSync.getValue().booleanValue()) {
            Experium.colorManager.setColor(INSTANCE.getCurrentColor().getRed(), INSTANCE.getCurrentColor().getGreen(), INSTANCE.getCurrentColor().getBlue(), ClickGui.getInstance().moduleMainC.getValue().getAlpha());
        }
    }

    public int getCurrentColorHex() {
        if (this.rainbow.getValue().booleanValue()) {
            return Color.HSBtoRGB(this.hue, (float) this.rainbowSaturation.getValue().intValue() / 255.0f, (float) this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return ColorUtil.toARGB(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha());
    }

    public Color getCurrentColor() {
        if (this.rainbow.getValue().booleanValue()) {
            return Color.getHSBColor(this.hue, (float) this.rainbowSaturation.getValue().intValue() / 255.0f, (float) this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha());
    }
}
