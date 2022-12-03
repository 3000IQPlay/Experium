package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.RenderUtil;
import dev._3000IQPlay.experium.util.RotationUtil;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Random;

public class HoleESP
        extends Module {
    private static HoleESP INSTANCE = new HoleESP();
	public Setting<Boolean> renderBedrockHoles = this.register(new Setting<Boolean>("RenderBedrock", true));
	public Setting<Boolean> renderObsidianHoles = this.register(new Setting<Boolean>("RenderObsidian", true));
	private final Setting<Integer> holes = this.register(new Setting<Integer>("Holes", 5, 1, 500));
	public Setting<Boolean> ownHole = this.register(new Setting<Boolean>("OwnHole", false));
	private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
	private final Setting<Integer> bBoxTopAlpha = this.register(new Setting<Object>("BedrockBoxTopAlpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue()));
	private final Setting<Integer> bBoxBottomAlpha = this.register(new Setting<Object>("BedrockBoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue()));
	private final Setting<Integer> oBoxTopAlpha = this.register(new Setting<Object>("ObsidianBoxTopAlpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue()));
	private final Setting<Integer> oBoxBottomAlpha = this.register(new Setting<Object>("ObsidianBoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue()));
	public Setting<Double> bHeight = this.register(new Setting<Double>("BedrockHeight", 0.0, -2.0, 2.0, v -> this.renderBedrockHoles.getValue()));
	public Setting<Double> oHeight = this.register(new Setting<Double>("ObsidianHeight", 0.0, -2.0, 2.0, v -> this.renderObsidianHoles.getValue()));
	
	private final Setting<Color> btfC = this.register(new Setting<Color>("BedrockTopFill", new Color(135, 0, 255, 255), v -> this.renderBedrockHoles.getValue()));
	private final Setting<Color> btoC = this.register(new Setting<Color>("BedrockTopOutline", new Color(135, 0, 255, 0), v -> this.renderBedrockHoles.getValue()));
	private final Setting<Color> bbfC = this.register(new Setting<Color>("BedrockBottomFill", new Color(0, 255, 255, 255), v -> this.renderBedrockHoles.getValue()));
	private final Setting<Color> bboC = this.register(new Setting<Color>("BedrockBottomOutline", new Color(0, 255, 255, 255), v -> this.renderBedrockHoles.getValue()));
	
	
	private final Setting<Color> otfC = this.register(new Setting<Color>("ObsidianTopFill", new Color(135, 0, 255, 255), v -> this.renderObsidianHoles.getValue()));
	private final Setting<Color> otoC = this.register(new Setting<Color>("OnsidianTopOutline", new Color(135, 0, 255, 0), v -> this.renderObsidianHoles.getValue()));
	private final Setting<Color> obfC = this.register(new Setting<Color>("OutlineBottomFill", new Color(255, 0, 0, 255), v -> this.renderObsidianHoles.getValue()));
	private final Setting<Color> oboC = this.register(new Setting<Color>("OutlineBottomOutline", new Color(255, 0, 0, 255), v -> this.renderObsidianHoles.getValue()));
	private int currentAlpha = 0;

    public HoleESP() {
        super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
		int drawnHoles = 0;
	   	for (BlockPos pos : Experium.holeManager.getSortedHoles()) {
			if (drawnHoles >= this.holes.getValue()) {
			    break;
			}
		    if (pos.equals(new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) && !this.ownHole.getValue().booleanValue() || !RotationUtil.isInFov(pos)) {
				continue;
			}
		    if (this.renderBedrockHoles.getValue() && Experium.holeManager.isSafe(pos)) {
	            RenderUtil.drawBoxESP(pos, new Color(this.btfC.getValue().getRed(), this.btfC.getValue().getGreen(), this.btfC.getValue().getBlue(), this.btfC.getValue().getAlpha()), true, new Color(this.btoC.getValue().getRed(), this.btoC.getValue().getGreen(), this.btoC.getValue().getBlue(), this.btoC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxTopAlpha.getValue(), true, this.bHeight.getValue(), true, true, true, false, this.currentAlpha);
				RenderUtil.drawBoxESP(pos, new Color(this.bbfC.getValue().getRed(), this.bbfC.getValue().getGreen(), this.bbfC.getValue().getBlue(), this.bbfC.getValue().getAlpha()), true, new Color(this.bboC.getValue().getRed(), this.bboC.getValue().getGreen(), this.bboC.getValue().getBlue(), this.bboC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxBottomAlpha.getValue(), true, this.bHeight.getValue(), true, true, false, true, this.currentAlpha);
	        } else if (this.renderObsidianHoles.getValue()) {
	    	    RenderUtil.drawBoxESP(pos, new Color(this.otfC.getValue().getRed(), this.otfC.getValue().getGreen(), this.otfC.getValue().getBlue(), this.otfC.getValue().getAlpha()), true, new Color(this.otoC.getValue().getRed(), this.otoC.getValue().getGreen(), this.otoC.getValue().getBlue(), this.otoC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxTopAlpha.getValue(), true, this.oHeight.getValue(), true, true, true, false, this.currentAlpha);
			    RenderUtil.drawBoxESP(pos, new Color(this.obfC.getValue().getRed(), this.obfC.getValue().getGreen(), this.obfC.getValue().getBlue(), this.obfC.getValue().getAlpha()), true, new Color(this.oboC.getValue().getRed(), this.oboC.getValue().getGreen(), this.oboC.getValue().getBlue(), this.oboC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxBottomAlpha.getValue(), true, this.oHeight.getValue(), true, true, false, true, this.currentAlpha);
			}
			++drawnHoles;
        }
    }
}
