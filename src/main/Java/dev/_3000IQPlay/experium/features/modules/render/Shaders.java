package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.shaders.impl.fill.AquaShader;
import dev._3000IQPlay.experium.util.shaders.impl.fill.CircleShader;
import dev._3000IQPlay.experium.util.shaders.impl.fill.FillShader;
import dev._3000IQPlay.experium.util.shaders.impl.fill.FlowShader;
import dev._3000IQPlay.experium.util.shaders.impl.fill.GradientShader;
import dev._3000IQPlay.experium.util.shaders.impl.fill.PhobosShader;
import dev._3000IQPlay.experium.util.shaders.impl.fill.RainbowCubeShader;
import dev._3000IQPlay.experium.util.shaders.impl.fill.SmokeShader;
import dev._3000IQPlay.experium.util.shaders.impl.outline.AquaOutlineShader;
import dev._3000IQPlay.experium.util.shaders.impl.outline.AstralOutlineShader;
import dev._3000IQPlay.experium.util.shaders.impl.outline.CircleOutlineShader;
import dev._3000IQPlay.experium.util.shaders.impl.outline.GlowShader;
import dev._3000IQPlay.experium.util.shaders.impl.outline.GradientOutlineShader;
import dev._3000IQPlay.experium.util.shaders.impl.outline.RainbowCubeOutlineShader;
import dev._3000IQPlay.experium.util.shaders.impl.outline.SmokeOutlineShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Shaders
        extends Module {
	private Setting<fillShadermode> fillShader = this.register(new Setting<fillShadermode>("Fill Shader", fillShadermode.None));
    private Setting<glowESPmode> glowESP = this.register(new Setting<glowESPmode>("Glow ESP", glowESPmode.None));
	private Setting<Crystal1> crystal = this.register(new Setting<Crystal1>("Crystals", Crystal1.None));
    private Setting<Player1> player = this.register(new Setting<Player1>("Players", Player1.None));
    private Setting<Mob1> mob = this.register(new Setting<Mob1>("Mobs", Mob1.None));
	private Setting<Itemsl> items = this.register(new Setting<Itemsl>("Items", Itemsl.None));
	private Setting<XPl> xpOrb = this.register(new Setting<Player1>("XP", XPl.None));
    private Setting<Boolean> rangeCheck = this.register(new Setting<Boolean>("Range Check", true));
    public Setting<Float> maxRange = this.register(new Setting<Object>("Max Range", Float.valueOf(35.0f), Float.valueOf(10.0f), Float.valueOf(100.0f), object -> this.rangeCheck.getValue()));
    public Setting<Float> minRange = this.register(new Setting<Object>("Min range", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), object -> this.rangeCheck.getValue()));
    private Setting<Boolean> default1 = this.register(new Setting<Boolean>("Reset Setting", false));
    private Setting<Boolean> Fpreset = this.register(new Setting<Boolean>("FutureRainbow Preset", false));
    private Setting<Boolean> fadeFill = this.register(new Setting<Boolean>("Fade Fill", Boolean.valueOf(false), bl -> this.fillShader.getValue() == fillShadermode.Astral || this.glowESP.getValue() == glowESPmode.Astral));
    private Setting<Boolean> fadeOutline = this.register(new Setting<Boolean>("FadeOL Fill", Boolean.valueOf(false), bl -> this.fillShader.getValue() == fillShadermode.Astral || this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> duplicateOutline = this.register(new Setting<Float>("duplicateOutline", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    public Setting<Float> duplicateFill = this.register(new Setting<Float>("Duplicate Fill", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    public Setting<Float> speedOutline = this.register(new Setting<Float>("Speed Outline", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(100.0f)));
    public Setting<Float> speedFill = this.register(new Setting<Float>("Speed Fill", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(100.0f)));
    public Setting<Float> quality = this.register(new Setting<Float>("Shader Quality", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    public Setting<Float> radius = this.register(new Setting<Float>("Shader Radius", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    public Setting<Float> rad = this.register(new Setting<Object>("RAD Fill", Float.valueOf(0.75f), Float.valueOf(0.0f), Float.valueOf(5.0f), object -> this.fillShader.getValue() == fillShadermode.Circle));
    public Setting<Float> PI = this.register(new Setting<Object>("PI Fill", Float.valueOf((float)Math.PI), Float.valueOf(0.0f), Float.valueOf(10.0f), object -> this.fillShader.getValue() == fillShadermode.Circle));
    public Setting<Float> saturationFill = this.register(new Setting<Object>("saturation", Float.valueOf(0.4f), Float.valueOf(0.0f), Float.valueOf(3.0f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> distfadingFill = this.register(new Setting<Object>("distfading", Float.valueOf(0.56f), Float.valueOf(0.0f), Float.valueOf(1.0f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> titleFill = this.register(new Setting<Object>("Tile", Float.valueOf(0.45f), Float.valueOf(0.0f), Float.valueOf(1.3f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> stepSizeFill = this.register(new Setting<Object>("Step Size", Float.valueOf(0.2f), Float.valueOf(0.0f), Float.valueOf(0.7f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> volumStepsFill = this.register(new Setting<Object>("Volum Steps", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> zoomFill = this.register(new Setting<Object>("Zoom", Float.valueOf(3.9f), Float.valueOf(0.0f), Float.valueOf(20.0f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> formuparam2Fill = this.register(new Setting<Object>("formuparam2", Float.valueOf(0.89f), Float.valueOf(0.0f), Float.valueOf(1.5f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> saturationOutline = this.register(new Setting<Object>("saturation", Float.valueOf(0.4f), Float.valueOf(0.0f), Float.valueOf(3.0f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Integer> maxEntities = this.register(new Setting<Integer>("Max Entities", 100, 10, 500));
    public Setting<Integer> iterationsFill = this.register(new Setting<Integer>("Iteration", Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(20), n -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Integer> redFill = this.register(new Setting<Integer>("Tick Regen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(100), n -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Integer> MaxIterFill = this.register(new Setting<Integer>("Max Iter", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(30), n -> this.fillShader.getValue() == fillShadermode.Aqua));
    public Setting<Integer> NUM_OCTAVESFill = this.register(new Setting<Integer>("NUM_OCTAVES", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(30), n -> this.fillShader.getValue() == fillShadermode.Smoke));
    public Setting<Integer> BSTARTFIll = this.register(new Setting<Integer>("BSTART", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), n -> this.fillShader.getValue() == fillShadermode.RainbowCube));
    public Setting<Integer> GSTARTFill = this.register(new Setting<Integer>("GSTART", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), n -> this.fillShader.getValue() == fillShadermode.RainbowCube));
    public Setting<Integer> RSTARTFill = this.register(new Setting<Integer>("RSTART", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), n -> this.fillShader.getValue() == fillShadermode.RainbowCube));
    public Setting<Integer> WaveLenghtFIll = this.register(new Setting<Integer>("Wave Lenght", Integer.valueOf(555), Integer.valueOf(0), Integer.valueOf(2000), n -> this.fillShader.getValue() == fillShadermode.RainbowCube));
    public Setting<Integer> volumStepsOutline = this.register(new Setting<Integer>("Volum Steps", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(10), n -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Integer> iterationsOutline = this.register(new Setting<Integer>("Iteration", Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(20), n -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Integer> MaxIterOutline = this.register(new Setting<Integer>("Max Iter", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(30), n -> this.glowESP.getValue() == glowESPmode.Aqua));
    public Setting<Integer> NUM_OCTAVESOutline = this.register(new Setting<Integer>("NUM_OCTAVES", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(30), n -> this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> BSTARTOutline = this.register(new Setting<Integer>("BSTART", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), n -> this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> GSTARTOutline = this.register(new Setting<Integer>("GSTART", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), n -> this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> RSTARTOutline = this.register(new Setting<Integer>("RSTART", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), n -> this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> alphaValue = this.register(new Setting<Integer>("Alpha Outline", 255, 0, 255));
    public Setting<Integer> WaveLenghtOutline = this.register(new Setting<Integer>("Wave Lenght", Integer.valueOf(555), Integer.valueOf(0), Integer.valueOf(2000), n -> this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> redOutline = this.register(new Setting<Integer>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(100), n -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> alphaFill = this.register(new Setting<Object>("AlphaF", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f), object -> this.fillShader.getValue() == fillShadermode.Astral || this.fillShader.getValue() == fillShadermode.Smoke));
    public Setting<Float> blueFill = this.register(new Setting<Object>("BlueF", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> greenFill = this.register(new Setting<Object>("GreenF", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), object -> this.fillShader.getValue() == fillShadermode.Astral));
    public Setting<Float> tauFill = this.register(new Setting<Object>("TAU", Float.valueOf((float)Math.PI * 2), Float.valueOf(0.0f), Float.valueOf(20.0f), object -> this.fillShader.getValue() == fillShadermode.Aqua));
    public Setting<Float> creepyFill = this.register(new Setting<Object>("Creepy", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(20.0f), object -> this.fillShader.getValue() == fillShadermode.Smoke));
    public Setting<Float> moreGradientFill = this.register(new Setting<Object>("More Gradient", Float.valueOf(1.0f), Float.valueOf(0.0f), Double.valueOf(10.0), object -> this.fillShader.getValue() == fillShadermode.Smoke));
    public Setting<Float> distfadingOutline = this.register(new Setting<Object>("distfading", Float.valueOf(0.56f), Float.valueOf(0.0f), Float.valueOf(1.0f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> titleOutline = this.register(new Setting<Object>("Tile", Float.valueOf(0.45f), Float.valueOf(0.0f), Float.valueOf(1.3f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> stepSizeOutline = this.register(new Setting<Object>("Step Size", Float.valueOf(0.19f), Float.valueOf(0.0f), Float.valueOf(0.7f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> zoomOutline = this.register(new Setting<Object>("Zoom", Float.valueOf(3.9f), Float.valueOf(0.0f), Float.valueOf(20.0f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> formuparam2Outline = this.register(new Setting<Object>("formuparam2", Float.valueOf(0.89f), Float.valueOf(0.0f), Float.valueOf(1.5f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> alphaOutline = this.register(new Setting<Object>("Alpha", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f), object -> this.glowESP.getValue() == glowESPmode.Astral || this.glowESP.getValue() == glowESPmode.Gradient));
    public Setting<Float> blueOutline = this.register(new Setting<Object>("Blue", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> greenOutline = this.register(new Setting<Object>("Green", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), object -> this.glowESP.getValue() == glowESPmode.Astral));
    public Setting<Float> tauOutline = this.register(new Setting<Object>("TAU", Float.valueOf((float)Math.PI * 2), Float.valueOf(0.0f), Float.valueOf(20.0f), object -> this.glowESP.getValue() == glowESPmode.Aqua));
    public Setting<Float> creepyOutline = this.register(new Setting<Object>("Gradient Creepy", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(20.0f), object -> this.glowESP.getValue() == glowESPmode.Gradient));
    public Setting<Float> moreGradientOutline = this.register(new Setting<Object>("More Gradient", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), object -> this.glowESP.getValue() == glowESPmode.Gradient));
    public Setting<Float> radOutline = this.register(new Setting<Object>("RAD Outline", Float.valueOf(0.75f), Float.valueOf(0.0f), Float.valueOf(5.0f), object -> this.glowESP.getValue() == glowESPmode.Circle));
    public Setting<Float> PIOutline = this.register(new Setting<Object>("PI Outline", Float.valueOf((float)Math.PI), Float.valueOf(0.0f), Float.valueOf(10.0f), object -> this.glowESP.getValue() == glowESPmode.Circle));
    public Setting<Integer> colorImgOutlinered = this.register(new Setting<Integer>("colorImgOutline Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.RainbowCube || this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> colorImgOutlinegreen = this.register(new Setting<Integer>("colorImgOutline Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.RainbowCube || this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> colorImgOutlineblue = this.register(new Setting<Integer>("colorImgOutline Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.RainbowCube || this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> colorImgOutlinealpha = this.register(new Setting<Integer>("colorImgOutline Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.RainbowCube || this.glowESP.getValue() == glowESPmode.RainbowCube));
    public Setting<Integer> thirdColorImgOutlinered = this.register(new Setting<Integer>("thirdColorImgOutline Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> thirdColorImgOutlinegreen = this.register(new Setting<Integer>("thirdColorImgOutline Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> thirdColorImgOutlineblue = this.register(new Setting<Integer>("thirdColorImgOutline Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> thirdColorImgOutlinealpha = this.register(new Setting<Integer>("thirdColorImgOutline Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> colorESPred = this.register(new Setting<Integer>("colorESP Red", 0, 0, 255));
    public Setting<Integer> colorESPgreen = this.register(new Setting<Integer>("colorESP Green", 0, 0, 255));
    public Setting<Integer> colorESPblue = this.register(new Setting<Integer>("colorESP Blue", 0, 0, 255));
    public Setting<Integer> colorESPalpha = this.register(new Setting<Integer>("colorESP Alpha", 255, 0, 255));
    public Setting<Integer> colorImgFillred = this.register(new Setting<Integer>("colorImgFill Red", 0, 0, 255));
    public Setting<Integer> colorImgFillgreen = this.register(new Setting<Integer>("colorImgFill Green", 0, 0, 255));
    public Setting<Integer> colorImgFillblue = this.register(new Setting<Integer>("colorImgFill Blue", 0, 0, 255));
    public Setting<Integer> colorImgFillalpha = this.register(new Setting<Integer>("colorImgFill Alpha", 255, 0, 255));
    public Setting<Integer> thirdColorImgFIllred = this.register(new Setting<Integer>("SmokeImgFill Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> thirdColorImgFIllgreen = this.register(new Setting<Integer>("SmokeImgFill Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> thirdColorImgFIllblue = this.register(new Setting<Integer>("SmokeFImgill Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> thirdColorImgFIllalpha = this.register(new Setting<Integer>("SmokeImgFill Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> secondColorImgFillred = this.register(new Setting<Integer>("SmokeFill Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> secondColorImgFillgreen = this.register(new Setting<Integer>("SmokeFill Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> secondColorImgFillblue = this.register(new Setting<Integer>("SmokeFill Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public Setting<Integer> secondColorImgFillalpha = this.register(new Setting<Integer>("SmokeFill Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), n -> this.fillShader.getValue() == fillShadermode.Smoke || this.glowESP.getValue() == glowESPmode.Smoke));
    public boolean renderTags = true;
    public boolean renderCape = true;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent renderGameOverlayEvent) {
        if (renderGameOverlayEvent.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (Shaders.mc.world == null || Shaders.mc.player == null) {
                return;
            }
            GlStateManager.pushMatrix();
            this.renderTags = false;
            this.renderCape = false;
            Color color = new Color(this.colorImgFillred.getValue(), this.colorImgFillgreen.getValue(), this.colorImgFillblue.getValue(), this.colorImgFillalpha.getValue());
            Color color2 = new Color(this.colorESPred.getValue(), this.colorESPgreen.getValue(), this.colorESPblue.getValue(), this.colorESPalpha.getValue());
            Color color3 = new Color(this.secondColorImgFillred.getValue(), this.secondColorImgFillgreen.getValue(), this.secondColorImgFillblue.getValue(), this.secondColorImgFillalpha.getValue());
            Color color4 = new Color(this.thirdColorImgOutlinered.getValue(), this.thirdColorImgOutlinegreen.getValue(), this.thirdColorImgOutlineblue.getValue(), this.thirdColorImgOutlinealpha.getValue());
            Color color5 = new Color(this.thirdColorImgFIllred.getValue(), this.thirdColorImgFIllgreen.getValue(), this.thirdColorImgFIllblue.getValue(), this.thirdColorImgFIllalpha.getValue());
            Color color6 = new Color(this.colorImgOutlinered.getValue(), this.colorImgOutlinegreen.getValue(), this.colorImgOutlineblue.getValue(), this.colorImgOutlinealpha.getValue());
            if (this.glowESP.getValue() != glowESPmode.None && this.fillShader.getValue() != fillShadermode.None) {
                Predicate<Boolean> predicate = this.getFill();
                switch (this.fillShader.getValue()) {
                    case Astral: {
                        FlowShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        FlowShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.redFill.getValue().floatValue(), this.greenFill.getValue().floatValue(), this.blueFill.getValue().floatValue(), this.alphaFill.getValue().floatValue(), this.iterationsFill.getValue(), this.formuparam2Fill.getValue().floatValue(), this.zoomFill.getValue().floatValue(), this.volumStepsFill.getValue().floatValue(), this.stepSizeFill.getValue().floatValue(), this.titleFill.getValue().floatValue(), this.distfadingFill.getValue().floatValue(), this.saturationFill.getValue().floatValue(), 0.0f, this.fadeFill.getValue() != false ? 1 : 0);
                        FlowShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Aqua: {
                        AquaShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        AquaShader.INSTANCE.stopDraw(color, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.MaxIterFill.getValue(), this.tauFill.getValue().floatValue());
                        AquaShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Smoke: {
                        SmokeShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        SmokeShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), color, color3, color5, this.NUM_OCTAVESFill.getValue());
                        SmokeShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case RainbowCube: {
                        RainbowCubeShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        RainbowCubeShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), color, this.WaveLenghtFIll.getValue(), this.RSTARTFill.getValue(), this.GSTARTFill.getValue(), this.BSTARTFIll.getValue());
                        RainbowCubeShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Gradient: {
                        GradientShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        GradientShader.INSTANCE.stopDraw(color2, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.moreGradientFill.getValue().floatValue(), this.creepyFill.getValue().floatValue(), this.alphaFill.getValue().floatValue(), this.NUM_OCTAVESFill.getValue());
                        GradientShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Fill: {
                        FillShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        FillShader.INSTANCE.stopDraw(color);
                        FillShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Circle: {
                        CircleShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        CircleShader.INSTANCE.stopDraw(this.duplicateFill.getValue().floatValue(), color, this.PI.getValue(), this.rad.getValue());
                        CircleShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Phobos: {
                        PhobosShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        PhobosShader.INSTANCE.stopDraw(color, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.MaxIterFill.getValue(), this.tauFill.getValue().floatValue());
                        PhobosShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                    }
                }
                switch (this.glowESP.getValue()) {
                    case Color: {
                        GlowShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        GlowShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue());
                        break;
                    }
                    case RainbowCube: {
                        RainbowCubeOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        RainbowCubeOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), color6, this.WaveLenghtOutline.getValue(), this.RSTARTOutline.getValue(), this.GSTARTOutline.getValue(), this.BSTARTOutline.getValue());
                        RainbowCubeOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Gradient: {
                        GradientOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        GradientOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.moreGradientOutline.getValue().floatValue(), this.creepyOutline.getValue().floatValue(), this.alphaOutline.getValue().floatValue(), this.NUM_OCTAVESOutline.getValue());
                        GradientOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Astral: {
                        AstralOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        AstralOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.redOutline.getValue().floatValue(), this.greenOutline.getValue().floatValue(), this.blueOutline.getValue().floatValue(), this.alphaOutline.getValue().floatValue(), this.iterationsOutline.getValue(), this.formuparam2Outline.getValue().floatValue(), this.zoomOutline.getValue().floatValue(), this.volumStepsOutline.getValue().intValue(), this.stepSizeOutline.getValue().floatValue(), this.titleOutline.getValue().floatValue(), this.distfadingOutline.getValue().floatValue(), this.saturationOutline.getValue().floatValue(), 0.0f, this.fadeOutline.getValue() != false ? 1 : 0);
                        AstralOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Aqua: {
                        AquaOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        AquaOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.MaxIterOutline.getValue(), this.tauOutline.getValue().floatValue());
                        AquaOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Circle: {
                        CircleOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        CircleOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.PIOutline.getValue().floatValue(), this.radOutline.getValue().floatValue());
                        CircleOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Smoke: {
                        SmokeOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        SmokeOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), color3, color4, this.NUM_OCTAVESOutline.getValue());
                        SmokeOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                    }
                }
            } else {
                switch (this.glowESP.getValue()) {
                    case Color: {
                        GlowShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        GlowShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue());
                        break;
                    }
                    case RainbowCube: {
                        RainbowCubeOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        RainbowCubeOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), color6, this.WaveLenghtOutline.getValue(), this.RSTARTOutline.getValue(), this.GSTARTOutline.getValue(), this.BSTARTOutline.getValue());
                        RainbowCubeOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Gradient: {
                        GradientOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        GradientOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.moreGradientOutline.getValue().floatValue(), this.creepyOutline.getValue().floatValue(), this.alphaOutline.getValue().floatValue(), this.NUM_OCTAVESOutline.getValue());
                        GradientOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Astral: {
                        AstralOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        AstralOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.redOutline.getValue().floatValue(), this.greenOutline.getValue().floatValue(), this.blueOutline.getValue().floatValue(), this.alphaOutline.getValue().floatValue(), this.iterationsOutline.getValue(), this.formuparam2Outline.getValue().floatValue(), this.zoomOutline.getValue().floatValue(), this.volumStepsOutline.getValue().intValue(), this.stepSizeOutline.getValue().floatValue(), this.titleOutline.getValue().floatValue(), this.distfadingOutline.getValue().floatValue(), this.saturationOutline.getValue().floatValue(), 0.0f, this.fadeOutline.getValue() != false ? 1 : 0);
                        AstralOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Aqua: {
                        AquaOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        AquaOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.MaxIterOutline.getValue(), this.tauOutline.getValue().floatValue());
                        AquaOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Circle: {
                        CircleOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        CircleOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), this.PIOutline.getValue().floatValue(), this.radOutline.getValue().floatValue());
                        CircleOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Smoke: {
                        SmokeOutlineShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersOutline(renderGameOverlayEvent.getPartialTicks());
                        SmokeOutlineShader.INSTANCE.stopDraw(color2, this.radius.getValue().floatValue(), this.quality.getValue().floatValue(), false, this.alphaValue.getValue(), this.duplicateOutline.getValue().floatValue(), color3, color4, this.NUM_OCTAVESOutline.getValue());
                        SmokeOutlineShader.INSTANCE.update(this.speedOutline.getValue().floatValue() / 1000.0f);
                    }
                }
                switch (this.fillShader.getValue()) {
                    case Astral: {
                        FlowShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        FlowShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.redFill.getValue().floatValue(), this.greenFill.getValue().floatValue(), this.blueFill.getValue().floatValue(), this.alphaFill.getValue().floatValue(), this.iterationsFill.getValue(), this.formuparam2Fill.getValue().floatValue(), this.zoomFill.getValue().floatValue(), this.volumStepsFill.getValue().floatValue(), this.stepSizeFill.getValue().floatValue(), this.titleFill.getValue().floatValue(), this.distfadingFill.getValue().floatValue(), this.saturationFill.getValue().floatValue(), 0.0f, this.fadeFill.getValue() != false ? 1 : 0);
                        FlowShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Aqua: {
                        AquaShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        AquaShader.INSTANCE.stopDraw(color, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.MaxIterFill.getValue(), this.tauFill.getValue().floatValue());
                        AquaShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Smoke: {
                        SmokeShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        SmokeShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), color, color3, color5, this.NUM_OCTAVESFill.getValue());
                        SmokeShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case RainbowCube: {
                        RainbowCubeShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        RainbowCubeShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), color, this.WaveLenghtFIll.getValue(), this.RSTARTFill.getValue(), this.GSTARTFill.getValue(), this.BSTARTFIll.getValue());
                        RainbowCubeShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Gradient: {
                        GradientShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        GradientShader.INSTANCE.stopDraw(color2, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.moreGradientFill.getValue().floatValue(), this.creepyFill.getValue().floatValue(), this.alphaFill.getValue().floatValue(), this.NUM_OCTAVESFill.getValue());
                        GradientShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Fill: {
                        FillShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        FillShader.INSTANCE.stopDraw(color);
                        FillShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Circle: {
                        CircleShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        CircleShader.INSTANCE.stopDraw(this.duplicateFill.getValue().floatValue(), color, this.PI.getValue(), this.rad.getValue());
                        CircleShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                        break;
                    }
                    case Phobos: {
                        PhobosShader.INSTANCE.startDraw(renderGameOverlayEvent.getPartialTicks());
                        this.renderPlayersFill(renderGameOverlayEvent.getPartialTicks());
                        PhobosShader.INSTANCE.stopDraw(color, 1.0f, 1.0f, this.duplicateFill.getValue().floatValue(), this.MaxIterFill.getValue(), this.tauFill.getValue().floatValue());
                        PhobosShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                    }
                }
            }
            this.renderTags = true;
            this.renderCape = true;
            GlStateManager.popMatrix();
        }
    }

    void renderPlayersFill(float f) {
        boolean bl = this.rangeCheck.getValue();
        double d = this.minRange.getValue().floatValue() * this.minRange.getValue().floatValue();
        double d2 = this.maxRange.getValue().floatValue() * this.maxRange.getValue().floatValue();
        AtomicInteger atomicInteger = new AtomicInteger();
        int n = this.maxEntities.getValue();
        try {
            Shaders.mc.world.loadedEntityList.stream().filter(entity -> {
                if (atomicInteger.getAndIncrement() > n) {
                    return false;
                }
                return entity instanceof EntityPlayer ? !(this.player.getValue() != Player1.Fill && this.player.getValue() != Player1.Both || entity == Shaders.mc.player && Shaders.mc.gameSettings.thirdPersonView == 0) : (entity instanceof EntityXPOrb ? this.xpOrb.getValue() == XPl.Fill || this.xpOrb.getValue() == XPl.Both : entity instanceof EntityItem ? this.items.getValue() == Itemsl.Fill || this.items.getValue() == Itemsl.Both : entity instanceof EntityCreature ? this.mob.getValue() == Mob1.Fill || this.mob.getValue() == Mob1.Both : entity instanceof EntityEnderCrystal && (this.crystal.getValue() == Crystal1.Fill || this.crystal.getValue() == Crystal1.Both));
            }).filter(entity -> {
                if (!bl) {
                    return true;
                }
                double d3 = Shaders.mc.player.getDistanceSq(entity);
                return d3 > d && d3 < d2;
            }).forEach(entity -> mc.getRenderManager().renderEntityStatic(entity, f, true));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    void renderPlayersOutline(float f) {
        boolean bl = this.rangeCheck.getValue();
        double d = this.minRange.getValue().floatValue() * this.minRange.getValue().floatValue();
        double d2 = this.maxRange.getValue().floatValue() * this.maxRange.getValue().floatValue();
        AtomicInteger atomicInteger = new AtomicInteger();
        int n = this.maxEntities.getValue();
        Shaders.mc.world.addEntityToWorld(-1000, (Entity)new EntityXPOrb((World)Shaders.mc.world, Shaders.mc.player.posX, Shaders.mc.player.posY + 1000000.0, Shaders.mc.player.posZ, 1));
        Shaders.mc.world.loadedEntityList.stream().filter(entity -> {
            if (atomicInteger.getAndIncrement() > n) {
                return false;
            }
            return entity instanceof EntityPlayer ? !(this.player.getValue() != Player1.Outline && this.player.getValue() != Player1.Both || entity == Shaders.mc.player && Shaders.mc.gameSettings.thirdPersonView == 0) : (entity instanceof EntityXPOrb ? this.xpOrb.getValue() == XPl.Outline || this.xpOrb.getValue() == XPl.Both : entity instanceof EntityItem ? this.items.getValue() == Itemsl.Outline || this.items.getValue() == Itemsl.Both : entity instanceof EntityCreature ? this.mob.getValue() == Mob1.Outline || this.mob.getValue() == Mob1.Both : entity instanceof EntityEnderCrystal && (this.crystal.getValue() == Crystal1.Outline || this.crystal.getValue() == Crystal1.Both));
        }).filter(entity -> {
            if (!bl) {
                return true;
            }
            double d3 = Shaders.mc.player.getDistanceSq(entity);
            return d3 > d && d3 < d2 || entity.getEntityId() == -1000;
        }).forEach(entity -> mc.getRenderManager().renderEntityStatic(entity, f, true));
        Shaders.mc.world.removeEntityFromWorld(-1000);
    }

    public Shaders() {
        super("Shaders", "Spawns in a fake player", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onTick() {
        if (this.Fpreset.getValue().booleanValue()) {
            this.fillShader.setValue(fillShadermode.None);
            this.glowESP.setValue(glowESPmode.Gradient);
            this.player.setValue(Player1.Outline);
            this.crystal.setValue(Crystal1.Outline);
            this.duplicateOutline.setValue(Float.valueOf(2.0f));
            this.speedOutline.setValue(Float.valueOf(30.0f));
            this.quality.setValue(Float.valueOf(0.6f));
            this.radius.setValue(Float.valueOf(1.7f));
            this.creepyOutline.setValue(Float.valueOf(1.0f));
            this.moreGradientOutline.setValue(Float.valueOf(1.0f));
            this.Fpreset.setValue(false);
        }
        if (this.default1.getValue().booleanValue()) {
            this.fillShader.setValue(fillShadermode.None);
            this.glowESP.setValue(glowESPmode.None);
            this.rangeCheck.setValue(true);
            this.maxRange.setValue(Float.valueOf(35.0f));
            this.minRange.setValue(Float.valueOf(0.0f));
            this.crystal.setValue(Crystal1.None);
            this.player.setValue(Player1.None);
            this.mob.setValue(Mob1.None);
			this.items.setValue(Itemsl.None);
            this.fadeFill.setValue(false);
            this.fadeOutline.setValue(false);
            this.duplicateOutline.setValue(Float.valueOf(1.0f));
            this.duplicateFill.setValue(Float.valueOf(1.0f));
            this.speedOutline.setValue(Float.valueOf(10.0f));
            this.speedFill.setValue(Float.valueOf(10.0f));
            this.quality.setValue(Float.valueOf(1.0f));
            this.radius.setValue(Float.valueOf(1.0f));
            this.rad.setValue(Float.valueOf(0.75f));
            this.PI.setValue(Float.valueOf((float)Math.PI));
            this.saturationFill.setValue(Float.valueOf(0.4f));
            this.distfadingFill.setValue(Float.valueOf(0.56f));
            this.titleFill.setValue(Float.valueOf(0.45f));
            this.stepSizeFill.setValue(Float.valueOf(0.2f));
            this.volumStepsFill.setValue(Float.valueOf(10.0f));
            this.zoomFill.setValue(Float.valueOf(3.9f));
            this.formuparam2Fill.setValue(Float.valueOf(0.89f));
            this.saturationOutline.setValue(Float.valueOf(0.4f));
            this.maxEntities.setValue(100);
            this.iterationsFill.setValue(4);
            this.redFill.setValue(0);
            this.MaxIterFill.setValue(5);
            this.NUM_OCTAVESFill.setValue(5);
            this.BSTARTFIll.setValue(0);
            this.GSTARTFill.setValue(0);
            this.RSTARTFill.setValue(0);
            this.WaveLenghtFIll.setValue(555);
            this.volumStepsOutline.setValue(10);
            this.iterationsOutline.setValue(4);
            this.MaxIterOutline.setValue(5);
            this.NUM_OCTAVESOutline.setValue(5);
            this.BSTARTOutline.setValue(0);
            this.GSTARTOutline.setValue(0);
            this.RSTARTOutline.setValue(0);
            this.alphaValue.setValue(255);
            this.WaveLenghtOutline.setValue(555);
            this.redOutline.setValue(0);
            this.alphaFill.setValue(Float.valueOf(1.0f));
            this.blueFill.setValue(Float.valueOf(0.0f));
            this.greenFill.setValue(Float.valueOf(0.0f));
            this.tauFill.setValue(Float.valueOf((float)Math.PI * 2));
            this.creepyFill.setValue(Float.valueOf(1.0f));
            this.moreGradientFill.setValue(Float.valueOf(1.0f));
            this.distfadingOutline.setValue(Float.valueOf(0.56f));
            this.titleOutline.setValue(Float.valueOf(0.45f));
            this.stepSizeOutline.setValue(Float.valueOf(0.19f));
            this.zoomOutline.setValue(Float.valueOf(3.9f));
            this.formuparam2Outline.setValue(Float.valueOf(0.89f));
            this.alphaOutline.setValue(Float.valueOf(1.0f));
            this.blueOutline.setValue(Float.valueOf(0.0f));
            this.greenOutline.setValue(Float.valueOf(0.0f));
            this.tauOutline.setValue(Float.valueOf(0.0f));
            this.creepyOutline.setValue(Float.valueOf(1.0f));
            this.moreGradientOutline.setValue(Float.valueOf(1.0f));
            this.radOutline.setValue(Float.valueOf(0.75f));
            this.PIOutline.setValue(Float.valueOf((float)Math.PI));
            this.default1.setValue(false);
        }
    }

    Predicate<Boolean> getFill() {
        Color color = new Color(this.colorImgFillred.getValue(), this.colorImgFillgreen.getValue(), this.colorImgFillblue.getValue(), this.colorImgFillalpha.getValue());
        Color color2 = new Color(this.colorESPred.getValue(), this.colorESPgreen.getValue(), this.colorESPblue.getValue(), this.colorESPalpha.getValue());
        Color color3 = new Color(this.secondColorImgFillred.getValue(), this.secondColorImgFillgreen.getValue(), this.secondColorImgFillblue.getValue(), this.secondColorImgFillalpha.getValue());
        Color color4 = new Color(this.thirdColorImgOutlinered.getValue(), this.thirdColorImgOutlinegreen.getValue(), this.thirdColorImgOutlineblue.getValue(), this.thirdColorImgOutlinealpha.getValue());
        Color color5 = new Color(this.thirdColorImgFIllred.getValue(), this.thirdColorImgFIllgreen.getValue(), this.thirdColorImgFIllblue.getValue(), this.thirdColorImgFIllalpha.getValue());
        Color color6 = new Color(this.colorImgOutlinered.getValue(), this.colorImgOutlinegreen.getValue(), this.colorImgOutlineblue.getValue(), this.colorImgOutlinealpha.getValue());
        Predicate<Boolean> predicate = bl -> true;
        switch (this.fillShader.getValue()) {
            case Astral: {
                predicate = bl -> {
                    FlowShader.INSTANCE.startShader(this.duplicateFill.getValue().floatValue(), this.redFill.getValue().floatValue(), this.greenFill.getValue().floatValue(), this.blueFill.getValue().floatValue(), this.alphaFill.getValue().floatValue(), this.iterationsFill.getValue(), this.formuparam2Fill.getValue().floatValue(), this.zoomFill.getValue().floatValue(), this.volumStepsFill.getValue().floatValue(), this.stepSizeFill.getValue().floatValue(), this.titleFill.getValue().floatValue(), this.distfadingFill.getValue().floatValue(), this.saturationFill.getValue().floatValue(), 0.0f, this.fadeFill.getValue() != false ? 1 : 0);
                    return true;
                };
                FlowShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                break;
            }
            case Aqua: {
                predicate = bl -> {
                    AquaShader.INSTANCE.startShader(this.duplicateFill.getValue().floatValue(), color, this.MaxIterFill.getValue(), this.tauFill.getValue().floatValue());
                    return true;
                };
                AquaShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                break;
            }
            case Smoke: {
                predicate = bl -> {
                    SmokeShader.INSTANCE.startShader(this.duplicateFill.getValue().floatValue(), color, color3, color5, this.NUM_OCTAVESFill.getValue());
                    return true;
                };
                SmokeShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                break;
            }
            case RainbowCube: {
                predicate = bl -> {
                    RainbowCubeShader.INSTANCE.startShader(this.duplicateFill.getValue().floatValue(), color, this.WaveLenghtFIll.getValue(), this.RSTARTFill.getValue(), this.GSTARTFill.getValue(), this.BSTARTFIll.getValue());
                    return true;
                };
                RainbowCubeShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                break;
            }
            case Gradient: {
                predicate = bl -> {
                    GradientShader.INSTANCE.startShader(this.duplicateFill.getValue().floatValue(), this.moreGradientFill.getValue().floatValue(), this.creepyFill.getValue().floatValue(), this.alphaFill.getValue().floatValue(), this.NUM_OCTAVESFill.getValue());
                    return true;
                };
                GradientShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                break;
            }
            case Fill: {
                Color color7 = new Color(this.colorImgFillred.getValue(), this.colorImgFillgreen.getValue(), this.colorImgFillblue.getValue(), this.colorImgFillalpha.getValue());
                predicate = bl -> {
                    FillShader.INSTANCE.startShader((float)color7.getRed() / 255.0f, (float)color7.getGreen() / 255.0f, (float)color7.getBlue() / 255.0f, (float)color7.getAlpha() / 255.0f);
                    return false;
                };
                FillShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                break;
            }
            case Circle: {
                predicate = bl -> {
                    CircleShader.INSTANCE.startShader(this.duplicateFill.getValue().floatValue(), color, this.PI.getValue(), this.rad.getValue());
                    return true;
                };
                CircleShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
                break;
            }
            case Phobos: {
                predicate = bl -> {
                    PhobosShader.INSTANCE.startShader(this.duplicateFill.getValue().floatValue(), color, this.MaxIterFill.getValue(), this.tauFill.getValue().floatValue());
                    return true;
                };
                PhobosShader.INSTANCE.update(this.speedFill.getValue().floatValue() / 1000.0f);
            }
        }
        return predicate;
    }

    public static enum fillShadermode {
        Astral,
        Aqua,
        Smoke,
        RainbowCube,
        Gradient,
        Fill,
        Circle,
        Phobos,
        None;
    }

    public static enum Player1 {
        None,
        Fill,
        Outline,
        Both;
    }

    public static enum Crystal1 {
        None,
        Fill,
        Outline,
        Both;
    }
	
	public static enum XPl {
        None,
        Fill,
        Outline,
        Both;
    }

    public static enum Mob1 {
        None,
        Fill,
        Outline,
        Both;
    }
	
	public static enum Itemsl {
        None,
        Fill,
        Outline,
        Both;
    }

    public static enum glowESPmode {
        None,
        Color,
        Astral,
        RainbowCube,
        Gradient,
        Circle,
        Smoke,
        Aqua;
    }
}
