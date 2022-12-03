package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.client.Colors;
import dev._3000IQPlay.experium.features.modules.client.HUD;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

public class BlockHighlight
        extends Module {
	private final Setting<Color> colorC = this.register(new Setting<Color>("BoxColor", new Color(40, 192, 255, 150)));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false));
    public Setting<Boolean> rolling = this.register(new Setting<Object>("Rolling", Boolean.valueOf(false), v -> this.colorSync.getValue()));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", false));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    public Setting<Boolean> customOutline = this.register(new Setting<Object>("CustomLine", Boolean.valueOf(true), v -> this.outline.getValue()));
	private final Setting<Color> outlineC = this.register(new Setting<Color>("OutlineColor", new Color(40, 192, 255, 255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ray.getBlockPos();
            if (this.rolling.getValue().booleanValue()) {
                RenderUtil.drawProperGradientBlockOutline(blockpos, new Color(HUD.getInstance().colorMap.get(0)), new Color(HUD.getInstance().colorMap.get(this.renderer.scaledHeight / 4)), new Color(HUD.getInstance().colorMap.get(this.renderer.scaledHeight / 2)), 1.0f);
            } else {
                RenderUtil.drawBoxESP(blockpos, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.colorC.getValue().getRed(), this.colorC.getValue().getGreen(), this.colorC.getValue().getBlue(), this.colorC.getValue().getAlpha()), this.customOutline.getValue(), new Color(this.outlineC.getValue().getRed(), this.outlineC.getValue().getGreen(), this.outlineC.getValue().getBlue(), this.outlineC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
            }
        }
    }
}
