package dev._3000IQPlay.experium.features.modules.client;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.ClientEvent;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.Util;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
	private final Setting<Settings> setting = this.register(new Setting<Settings>("Page", Settings.Main));
	public Setting<String> prefix = this.register(new Setting<String>("Prefix", ".").setRenderName(true));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> rainbowRolling = this.register(new Setting<Object>("RollingRainbow", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Misc && this.colorSync.getValue() != false && Colors.INSTANCE.rainbow.getValue() != false));
	public Setting<Integer> guiWidth = this.register(new Setting<Integer>("GuiWidth", 113, 90, 115, v -> this.setting.getValue() == Settings.Misc));
	public Setting<SliderType> sliderType = this.register(new Setting<SliderType>("SliderType", SliderType.Line, v -> this.setting.getValue() == Settings.Sliders));
	public Setting<Color> sliderC = register(new Setting<Color>("SliderColor", new Color(40, 192, 255, 255), v -> this.setting.getValue() == Settings.Sliders));

    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", false, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Boolean> outlineNew = this.register(new Setting<Boolean>("OutlineNew", true, v -> this.setting.getValue() == Settings.Lines));
    public Setting<Float> outlineThickness = this.register(new Setting<Float>("LineThickness", Float.valueOf(2.0f), Float.valueOf(0.5f), Float.valueOf(5.0f), v -> this.setting.getValue() == Settings.Lines && this.outlineNew.getValue()));
	public Setting<Color> outlineC = register(new Setting<Color>("OutlineColor", new Color(40, 192, 255, 255), v -> this.setting.getValue() == Settings.Lines && this.outlineNew.getValue()));
	
	public Setting<Boolean> shader = this.register(new Setting<Boolean>("Shader", true, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Integer> shaderRadius = this.register(new Setting<Object>("ShaderRadius", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(10), v -> this.setting.getValue() == Settings.Lines && this.shader.getValue()));
	public Setting<Color> shaderC = register(new Setting<Color>("ShaderColor", new Color(40, 192, 255, 255), v -> this.setting.getValue() == Settings.Lines && this.shader.getValue()));
	
	public Setting<Boolean> sideSettings = this.register(new Setting<Boolean>("SideLine", true, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Color> sideLineC = register(new Setting<Color>("SideLineColor", new Color(40, 192, 255, 255), v -> this.setting.getValue() == Settings.Lines && this.sideSettings.getValue()));
	
    public Setting<Boolean> snowing = this.register(new Setting<Boolean>("Snowing", false, v -> this.setting.getValue() == Settings.Background));
	
	public Setting<Boolean> enableSwitch = this.register(new Setting<Boolean>("Switch", true, v -> this.setting.getValue() == Settings.Booleans));
	public Setting<Color> booleanC = register(new Setting<Color>("BooleanColor", new Color(40, 40, 40, 0), v -> this.setting.getValue() == Settings.Booleans));
	
	public Setting<Color> sbC = register(new Setting<Color>("SwitchBackgroundColor", new Color(21, 21, 21, 200), v -> this.setting.getValue() == Settings.Booleans && this.enableSwitch.getValue()));
	public Setting<Color> seC = register(new Setting<Color>("SwitchEnableColor", new Color(40, 192, 255, 200), v -> this.setting.getValue() == Settings.Booleans && this.enableSwitch.getValue()));
	public Setting<Color> sdC = register(new Setting<Color>("SwitchDisableColor", new Color(100, 100, 100, 200), v -> this.setting.getValue() == Settings.Booleans && this.enableSwitch.getValue()));

	public Setting<Boolean> categoryDots = this.register(new Setting<Boolean>("CategoryDots", true, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> categoryTextCenter = this.register(new Setting<Boolean>("CategoryTextCenter", true, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> buttonTextCenter = this.register(new Setting<Boolean>("ButtonTextCenter", false, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Boolean> moduleDescription = this.register(new Setting<Boolean>("Description", true, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> blurEffect = this.register(new Setting<Boolean>("Blur", false, v -> this.setting.getValue() == Settings.Background));
	
	public Setting<Boolean> guiBackground = this.register(new Setting<Boolean>("GuiBackground", true, v -> this.setting.getValue() == Settings.Background));
	public Setting<Color> gbC = register(new Setting<Color>("GuiBackgroundColor", new Color(0, 0, 0, 30), v -> this.setting.getValue() == Settings.Background && this.guiBackground.getValue()));
	
	public Setting<Boolean> moduleSeperate = this.register(new Setting<Boolean>("ModuleSeperateLine", false, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Color> moduleSeperateC = register(new Setting<Color>("ModuleOutlineColor", new Color(-1), v -> this.setting.getValue() == Settings.Lines && this.moduleSeperate.getValue()));
	
	public Setting<Boolean> moduleOutline = this.register(new Setting<Boolean>("ModuleOutline", true, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Color> moduleOutlineC = register(new Setting<Color>("ModuleOutlineColor", new Color(0, 0, 0, 255), v -> this.setting.getValue() == Settings.Lines && this.moduleOutline.getValue()));
	
	public Setting<Color> topC = register(new Setting<Color>("CategoryColor", new Color(40, 192, 255, 90), v -> this.setting.getValue() == Settings.Main));
	public Setting<Color> moduleEnableC = register(new Setting<Color>("ModuleMainColor", new Color(40, 40, 40, 255), v -> this.setting.getValue() == Settings.Main));
	public Setting<Color> moduleMainC = register(new Setting<Color>("ModuleEnableColor", new Color(40, 40, 40, 0), v -> this.setting.getValue() == Settings.Main));
	public Setting<Integer> hoverAlpha = register(new Setting<Integer>("HoverAlpha", 170, 0, 255, v -> this.setting.getValue() == Settings.Main));
	
	public Setting<Color> textEnableC = register(new Setting<Color>("EnabledTextColor", new Color(40, 192, 255), v -> this.setting.getValue() == Settings.FontC));
	public Setting<Color> textDisabledC = register(new Setting<Color>("DisabledTextColor", new Color(255, 255, 255), v -> this.setting.getValue() == Settings.FontC));
	
	public Setting<Boolean> scroll = this.register(new Setting<Boolean>("Scroll", true, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Integer> scrollval = this.register(new Setting<Integer>("Scroll Speed", 10, 1, 30, v -> this.setting.getValue() == Settings.Misc && this.scroll.getValue()));
    public Setting<Boolean> customFov = this.register(new Setting<Boolean>("CustomFov", false, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Float> fov = this.register(new Setting<Object>("Fov", Float.valueOf(135.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f), v -> this.setting.getValue() == Settings.Misc && this.customFov.getValue()));
	public Setting<Boolean> gear = this.register(new Setting<Boolean>("Gears", false, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Boolean> openCloseChange = this.register(new Setting<Boolean>("Open/Close", true, v -> this.setting.getValue() == Settings.Misc));
    public Setting<String> open = this.register(new Setting<Object>("Open:", "+", v -> this.setting.getValue() == Settings.Misc && this.openCloseChange.getValue()).setRenderName(true));
    public Setting<String> close = this.register(new Setting<Object>("Close:", "-", v -> this.setting.getValue() == Settings.Misc && this.openCloseChange.getValue()).setRenderName(true));
    public Setting<String> moduleButton = this.register(new Setting<Object>("Buttons:", "", v -> this.setting.getValue() == Settings.Misc && this.openCloseChange.getValue() == false).setRenderName(true));
	public Setting<Color> sideStringC = register(new Setting<Color>("SideStringColor", new Color(255, 255, 255, 255), v -> this.setting.getValue() == Settings.Misc && this.openCloseChange.getValue() || this.gear.getValue()));
	
	public Setting<Boolean> frameSettings = this.register(new Setting<Boolean>("FrameSetting", true, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Color> frameC = register(new Setting<Color>("FrameColor", new Color(40, 192, 255, 200), v -> this.setting.getValue() == Settings.Lines && this.frameSettings.getValue()));
	
	public Setting<Boolean> gradiant = this.register(new Setting<Boolean>("Gradiant", true, v -> this.setting.getValue() == Settings.Background));
	public Setting<Color> gradiantC = register(new Setting<Color>("GradiantColor", new Color(40, 192, 255, 120), v -> this.setting.getValue() == Settings.Background && this.gradiant.getValue()));
	
    public Setting<Boolean> particles = this.register(new Setting<Boolean>("Particles", true, v -> this.setting.getValue() == Settings.Background));
	public Setting<Integer> particleLength = this.register(new Setting<Integer>("ParticleLength", 45, 0, 300, v -> this.setting.getValue() == Settings.Background && this.particles.getValue()));
	public Setting<Color> particleC = register(new Setting<Color>("ParticleColor", new Color(40, 192, 255, 180), v -> this.setting.getValue() == Settings.Background && this.gradiant.getValue()));
	public float hue;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        this.setInstance();
		this.setBind(54);
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.customFov.getValue().booleanValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Experium.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to \u00a7a" + Experium.commandManager.getPrefix());
            }
            Experium.colorManager.setColor(this.moduleMainC.getPlannedValue().getRed(), this.moduleMainC.getPlannedValue().getGreen(), this.moduleMainC.getPlannedValue().getBlue(), this.moduleMainC.getPlannedValue().getAlpha());
        }
    }
	
    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(new ExperiumGui());
        if (this.blurEffect.getValue()) {
            ClickGui.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }

    @Override
    public void onLoad() {
        if (this.colorSync.getValue().booleanValue()) {
            Experium.colorManager.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), this.moduleMainC.getValue().getAlpha());
        } else {
            Experium.colorManager.setColor(this.moduleMainC.getPlannedValue().getRed(), this.moduleMainC.getPlannedValue().getGreen(), this.moduleMainC.getPlannedValue().getBlue(), this.moduleMainC.getValue().getAlpha());
        }
        Experium.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof ExperiumGui)) {
            this.disable();
            if (mc.entityRenderer.getShaderGroup() != null) { 
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
	        }
        }
    }

    @Override
    public void onDisable() {
        if (ClickGui.mc.currentScreen instanceof ExperiumGui) {
            Util.mc.displayGuiScreen(null);
        }
    }
	
	public enum Settings {
		Main,
        Sliders,
        Booleans,
        Lines,
		Background,
		FontC,
		Misc;
	}
	
	public enum SliderType {
		Fill,
		Line;
	}
}
