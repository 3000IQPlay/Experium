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
	private final Setting<Integer> boxTopAlpha = this.register(new Setting<Object>("BoxTopAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255)));
	private final Setting<Integer> boxBottomAlpha = this.register(new Setting<Object>("BoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255)));
	private final Setting<Color> tfC = this.register(new Setting<Color>("TopFill", new Color(135, 0, 255, 255)));
	private final Setting<Color> toC = this.register(new Setting<Color>("TopOutline", new Color(135, 0, 255, 255)));
	private final Setting<Color> bfC = this.register(new Setting<Color>("BottomFill", new Color(0, 255, 255, 255)));
	private final Setting<Color> boC = this.register(new Setting<Color>("BottomOutline", new Color(0, 255, 255, 255)));
	private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false));
    public Setting<Boolean> rolling = this.register(new Setting<Object>("Rolling", Boolean.valueOf(false), v -> this.colorSync.getValue()));
	private int currentAlpha = 0;

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
                RenderUtil.drawBoxESP(blockpos, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.tfC.getValue().getRed(), this.tfC.getValue().getGreen(), this.tfC.getValue().getBlue(), this.tfC.getValue().getAlpha()), true, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.toC.getValue().getRed(), this.toC.getValue().getGreen(), this.toC.getValue().getBlue(), this.toC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.boxTopAlpha.getValue(), true, 0.0, true, true, true, false, this.currentAlpha);
			    RenderUtil.drawBoxESP(blockpos, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.bfC.getValue().getRed(), this.bfC.getValue().getGreen(), this.bfC.getValue().getBlue(), this.bfC.getValue().getAlpha()), true, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.boC.getValue().getRed(), this.boC.getValue().getGreen(), this.boC.getValue().getBlue(), this.boC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.boxBottomAlpha.getValue(), true, 0.0, true, true, false, true, this.currentAlpha);
            }
        }
    }
}
