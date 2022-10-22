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
	public Setting<RenderType> renderType = this.register(new Setting<RenderType>("RenderType", RenderType.Gradient));
	public Setting<Boolean> renderBedrockHoles = this.register(new Setting<Boolean>("RenderBedrock", true));
	public Setting<Boolean> renderObsidianHoles = this.register(new Setting<Boolean>("RenderObsidian", true));
	private final Setting<Integer> holes = this.register(new Setting<Integer>("Holes", 5, 1, 500));
	public Setting<Boolean> ownHole = this.register(new Setting<Boolean>("OwnHole", false));
	private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.renderType.getValue() == RenderType.Gradient));
	private final Setting<Integer> bBoxTopAlpha = this.register(new Setting<Object>("BedrockBoxTopAlpha", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	private final Setting<Integer> bBoxBottomAlpha = this.register(new Setting<Object>("BedrockBoxBottomAlpha", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	private final Setting<Integer> oBoxTopAlpha = this.register(new Setting<Object>("ObsidianBoxTopAlpha", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	private final Setting<Integer> oBoxBottomAlpha = this.register(new Setting<Object>("ObsidianBoxBottomAlpha", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	public Setting<Double> bHeight = this.register(new Setting<Double>("BedrockHeight", 0.0, -2.0, 2.0, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	public Setting<Double> oHeight = this.register(new Setting<Double>("ObsidianHeight", 0.0, -2.0, 2.0, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> bTopRed = this.register(new Setting<Integer>("BedrockTopRed", 135, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> bTopGreen = this.register(new Setting<Integer>("BedrockTopGreen", 0, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> bTopBlue = this.register(new Setting<Integer>("BedrockTopBlue", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> bTopAlpha = this.register(new Setting<Integer>("BedrockTopAlpha", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	private final Setting<Integer> boTopRed = this.register(new Setting<Integer>("BedrockTopOutlineRed", 135, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> boTopGreen = this.register(new Setting<Integer>("BedrockTopOutlineGreen", 0, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> boTopBlue = this.register(new Setting<Integer>("BedrockTopOutlineBlue", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> boTopAlpha = this.register(new Setting<Integer>("BedrockTopOulineAlpha", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	
	private final Setting<Integer> bBottomRed = this.register(new Setting<Integer>("BedrockBottomRed", 0, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> bBottomGreen = this.register(new Setting<Integer>("BedrockBottomGreen", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> bBottomBlue = this.register(new Setting<Integer>("BedrockBottomBlue", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> bBottomAlpha = this.register(new Setting<Integer>("BedrockBottomAlpha", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	private final Setting<Integer> boBottomRed = this.register(new Setting<Integer>("BedrockBottomOutlineRed", 0, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> boBottomGreen = this.register(new Setting<Integer>("BedrockBottomOutlineGreen", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> boBottomBlue = this.register(new Setting<Integer>("BedrockBottomOutlineBlue", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> boBottomAlpha = this.register(new Setting<Integer>("BedrockBottomOulineAlpha", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	
	private final Setting<Integer> oTopRed = this.register(new Setting<Integer>("ObsidianTopRed", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> oTopGreen = this.register(new Setting<Integer>("ObsidianTopGreen", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> oTopBlue = this.register(new Setting<Integer>("ObsidianTopBlue", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> oTopAlpha = this.register(new Setting<Integer>("ObsidianTopAlpha", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	private final Setting<Integer> ooTopRed = this.register(new Setting<Integer>("ObsidianTopOutlineRed", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> ooTopGreen = this.register(new Setting<Integer>("ObsidianTopOutlineGreen", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> ooTopBlue = this.register(new Setting<Integer>("ObsidianTopOutlineBlue", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> ooTopAlpha = this.register(new Setting<Integer>("ObsidianTopOulineAlpha", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	
	private final Setting<Integer> oBottomRed = this.register(new Setting<Integer>("ObsidianBottomRed", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> oBottomGreen = this.register(new Setting<Integer>("ObsidianBottomGreen", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> oBottomBlue = this.register(new Setting<Integer>("ObsidianBottomBlue", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> oBottomAlpha = this.register(new Setting<Integer>("ObsidianBottomAlpha", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	private final Setting<Integer> ooBottomRed = this.register(new Setting<Integer>("ObsidianBottomOutlineRed", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> ooBottomGreen = this.register(new Setting<Integer>("ObsidianBottomOutlineGreen", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> ooBottomBlue = this.register(new Setting<Integer>("ObsidianBottomOutlineBlue", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
    private final Setting<Integer> ooBottomAlpha = this.register(new Setting<Integer>("ObsidianBottomOulineAlpha", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Gradient));
	
	
	
	private final Setting<Integer> bStartRed = this.register(new Setting<Integer>("BedrockStartRed", 135, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> bStartGreen = this.register(new Setting<Integer>("BedrockStartGreen", 0, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> bStartBlue = this.register(new Setting<Integer>("BedrockStartBlue", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> bStartAlpha = this.register(new Setting<Integer>("BedrockStartAlpha", 90, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
	
	private final Setting<Integer> bEndRed = this.register(new Setting<Integer>("BedrockEndRed", 0, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> bEndGreen = this.register(new Setting<Integer>("BedrockEndGreen", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> bEndBlue = this.register(new Setting<Integer>("BedrockEndBlue", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> bEndAlpha = this.register(new Setting<Integer>("BedrockEndAlpha", 255, 0, 255, v -> this.renderBedrockHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
	
	
	private final Setting<Integer> oStartRed = this.register(new Setting<Integer>("ObsidianStartRed", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> oStartGreen = this.register(new Setting<Integer>("ObsidianStartGreen", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> oStartBlue = this.register(new Setting<Integer>("ObsidianStartBlue", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> oStartAlpha = this.register(new Setting<Integer>("ObsidianStartAlpha", 90, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
	
	private final Setting<Integer> oEndRed = this.register(new Setting<Integer>("ObsidianEndRed", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> oEndGreen = this.register(new Setting<Integer>("ObsidianEndGreen", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> oEndBlue = this.register(new Setting<Integer>("ObsidianEndBlue", 0, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
    private final Setting<Integer> oEndAlpha = this.register(new Setting<Integer>("ObsidianEndAlpha", 255, 0, 255, v -> this.renderObsidianHoles.getValue() && this.renderType.getValue() == RenderType.Glow));
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
			if (this.renderType.getValue() == RenderType.Gradient) {
			    if (this.renderBedrockHoles.getValue() && Experium.holeManager.isSafe(pos)) {
		            RenderUtil.drawBoxESP(pos, new Color(this.bTopRed.getValue(), this.bTopGreen.getValue(), this.bTopBlue.getValue(), this.bTopAlpha.getValue()), true, new Color(this.boTopRed.getValue(), this.boTopGreen.getValue(), this.boTopBlue.getValue(), this.boTopAlpha.getValue()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxTopAlpha.getValue(), true, this.bHeight.getValue(), true, true, true, false, this.currentAlpha);
				    RenderUtil.drawBoxESP(pos, new Color(this.bBottomRed.getValue(), this.bBottomGreen.getValue(), this.bBottomBlue.getValue(), this.bBottomAlpha.getValue()), true, new Color(this.boBottomRed.getValue(), this.boBottomGreen.getValue(), this.boBottomBlue.getValue(), this.boBottomAlpha.getValue()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxBottomAlpha.getValue(), true, this.bHeight.getValue(), true, true, false, true, this.currentAlpha);
	            } else if (this.renderObsidianHoles.getValue()) {
				    RenderUtil.drawBoxESP(pos, new Color(this.oTopRed.getValue(), this.oTopGreen.getValue(), this.oTopBlue.getValue(), this.oTopAlpha.getValue()), true, new Color(this.ooTopRed.getValue(), this.ooTopGreen.getValue(), this.ooTopBlue.getValue(), this.ooTopAlpha.getValue()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxTopAlpha.getValue(), true, this.oHeight.getValue(), true, true, true, false, this.currentAlpha);
				    RenderUtil.drawBoxESP(pos, new Color(this.oBottomRed.getValue(), this.oBottomGreen.getValue(), this.oBottomBlue.getValue(), this.oBottomAlpha.getValue()), true, new Color(this.ooBottomRed.getValue(), this.ooBottomGreen.getValue(), this.ooBottomBlue.getValue(), this.ooBottomAlpha.getValue()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxBottomAlpha.getValue(), true, this.oHeight.getValue(), true, true, false, true, this.currentAlpha);
			    }
			}
			if (this.renderType.getValue() == RenderType.Glow) {
				if (this.renderBedrockHoles.getValue() && Experium.holeManager.isSafe(pos)) {
					RenderUtil.drawEvenBetterGradientBox(pos, new Color(this.bStartRed.getValue(), this.bStartGreen.getValue(), this.bStartBlue.getValue(), this.bStartAlpha.getValue()), new Color(0, 0, 0, 0), new Color(this.bEndRed.getValue(), this.bEndGreen.getValue(), this.bEndBlue.getValue(), this.bEndAlpha.getValue()));
				} else if (this.renderObsidianHoles.getValue()) {
				    RenderUtil.drawEvenBetterGradientBox(pos, new Color(this.oStartRed.getValue(), this.oStartGreen.getValue(), this.oStartBlue.getValue(), this.oStartAlpha.getValue()), new Color(0, 0, 0, 0), new Color(this.oEndRed.getValue(), this.oEndGreen.getValue(), this.oEndBlue.getValue(), this.oEndAlpha.getValue()));
			    }
			}
			++drawnHoles;
        }
    }
	
	public static enum RenderType {
		Gradient,
		Glow;
	}
}
