package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;

public class CrystalModify
        extends Module {
    public static CrystalModify INSTANCE;
    public Setting<modes> mode = this.register(new Setting<modes>("Mode", modes.FILL));
    public Setting<outlineModes> outlineMode = this.register(new Setting<outlineModes>("Outline Mode", outlineModes.WIRE));
    public Setting<Float> size = this.register(new Setting<Float>("Size", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    public Setting<Float> crystalSpeed = this.register(new Setting<Float>("Speed", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    public Setting<Float> crystalBounce = this.register(new Setting<Float>("Bounce", Float.valueOf(0.2f), Float.valueOf(0.1f), Float.valueOf(1.0f)));
    public Setting<Boolean> enchanted = this.register(new Setting<Boolean>("Glint", false));
    public Setting<Integer> enchantRed = this.register(new Setting<Object>("Glint Red", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.enchanted.getValue()));
    public Setting<Integer> enchantGreen = this.register(new Setting<Object>("Glint Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.enchanted.getValue()));
    public Setting<Integer> enchantBlue = this.register(new Setting<Object>("Glint Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.enchanted.getValue()));
    public Setting<Integer> enchantAlpha = this.register(new Setting<Object>("Glint Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.enchanted.getValue()));
    public Setting<Boolean> texture = this.register(new Setting<Boolean>("Texture", false));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false));
    public Setting<Integer> red = this.register(new Setting<Integer>("Red", 135, 0, 255));
    public Setting<Integer> green = this.register(new Setting<Integer>("Green", 0, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", false));
    public Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    public Setting<Integer> outlineRed = this.register(new Setting<Object>("Outline Red", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    public Setting<Integer> outlineGreen = this.register(new Setting<Object>("Outline Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    public Setting<Integer> outlineBlue = this.register(new Setting<Object>("Outline Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    public Setting<Integer> outlineAlpha = this.register(new Setting<Object>("Outline Alpha", Integer.valueOf(90), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    public Setting<Boolean> hiddenSync = this.register(new Setting<Boolean>("Hidden Sync", false));
    public Setting<Integer> hiddenRed = this.register(new Setting<Object>("Hidden Red", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.hiddenSync.getValue() == false));
    public Setting<Integer> hiddenGreen = this.register(new Setting<Object>("Hidden Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.hiddenSync.getValue() == false));
    public Setting<Integer> hiddenBlue = this.register(new Setting<Object>("Hidden Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.hiddenSync.getValue() == false));
    public Setting<Integer> hiddenAlpha = this.register(new Setting<Object>("Hidden Alpha", Integer.valueOf(90), Integer.valueOf(0), Integer.valueOf(255), v -> this.hiddenSync.getValue() == false));

    public CrystalModify() {
        super("CrystalModify", "Modifies crystal rendering in different ways", Module.Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public static enum modes {
        FILL,
        WIREFRAME;

    }

    public static enum outlineModes {
        WIRE,
        FLAT;

    }
}

