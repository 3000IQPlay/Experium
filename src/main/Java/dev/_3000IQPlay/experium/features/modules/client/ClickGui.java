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

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
	private final Setting<Settings> setting = this.register(new Setting<Settings>("Page", Settings.Main));
	public Setting<String> prefix = this.register(new Setting<String>("Prefix", ".").setRenderName(true));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> rainbowRolling = this.register(new Setting<Object>("RollingRainbow", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Misc && this.colorSync.getValue() != false && Colors.INSTANCE.rainbow.getValue() != false));
	public Setting<Integer> guiWidth = this.register(new Setting<Integer>("GuiWidth", Integer.valueOf(113), Integer.valueOf(90), Integer.valueOf(115), v -> this.setting.getValue() == Settings.Scaling));
	
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", false, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Boolean> outlineNew = this.register(new Setting<Boolean>("OutlineNew", false, v -> this.setting.getValue() == Settings.Lines));
    public Setting<Float> outlineThickness = this.register(new Setting<Float>("LineThickness", Float.valueOf(2.5f), Float.valueOf(0.5f), Float.valueOf(5.0f), v -> this.setting.getValue() == Settings.Lines && this.outlineNew.getValue()));
    public Setting<Integer> o_red = this.register(new Setting<Object>("OutlineRed", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.outlineNew.getValue()));
    public Setting<Integer> o_green = this.register(new Setting<Object>("OutlineGreen", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.outlineNew.getValue()));
    public Setting<Integer> o_blue = this.register(new Setting<Object>("OutlineBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.outlineNew.getValue()));
    public Setting<Integer> o_alpha = this.register(new Setting<Object>("OutlineAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.outlineNew.getValue()));
	
	public Setting<Boolean> sideSettings = this.register(new Setting<Boolean>("SideLine", true, v -> this.setting.getValue() == Settings.Lines));
    public Setting<Integer> sideRed = this.register(new Setting<Object>("SideLineRed", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.sideSettings.getValue()));
    public Setting<Integer> sideGreen = this.register(new Setting<Object>("SideLineGreen", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.sideSettings.getValue()));
    public Setting<Integer> sideBlue = this.register(new Setting<Object>("SideLineBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.sideSettings.getValue()));
    public Setting<Integer> sideAlpha = this.register(new Setting<Object>("SideLineAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.sideSettings.getValue()));
	
    public Setting<Boolean> snowing = this.register(new Setting<Boolean>("Snowing", false, v -> this.setting.getValue() == Settings.Background));
	
	public Setting<Boolean> enableSwitch = this.register(new Setting<Boolean>("Switch", true, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Integer> sbRed = this.register(new Setting<Object>("SwitchBackgroundRed", Integer.valueOf(21), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> sbGreen = this.register(new Setting<Object>("SwitchBackgroundGreen", Integer.valueOf(21), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> sbBlue = this.register(new Setting<Object>("SwitchBackgroundBlue", Integer.valueOf(21), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> sbAlpha = this.register(new Setting<Object>("SwitchBackgroundAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
	public Setting<Integer> seRed = this.register(new Setting<Object>("SwitchEnableRed", Integer.valueOf(102), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> seGreen = this.register(new Setting<Object>("SwitchEnableGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> seBlue = this.register(new Setting<Object>("SwitchEnabledBlue", Integer.valueOf(51), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> seAlpha = this.register(new Setting<Object>("SwitchEnableAlpha", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
	public Setting<Integer> sdRed = this.register(new Setting<Object>("SwitchDisableRed", Integer.valueOf(102), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> sdGreen = this.register(new Setting<Object>("SwitchDisableGreen", Integer.valueOf(102), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> sdBlue = this.register(new Setting<Object>("SwitchDisableBlue", Integer.valueOf(102), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
    public Setting<Integer> sdAlpha = this.register(new Setting<Object>("SwitchDisableAlpha", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Misc && this.enableSwitch.getValue()));
	
	public Setting<Boolean> categoryDots = this.register(new Setting<Boolean>("CategoryDots", true, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> categoryTextCenter = this.register(new Setting<Boolean>("CategoryTextCenter", false, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> buttonTextCenter = this.register(new Setting<Boolean>("ButtonTextCenter", false, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Boolean> moduleDescription = this.register(new Setting<Boolean>("Description", true, v -> this.setting.getValue() == Settings.Misc));
	public Setting<Boolean> blurEffect = this.register(new Setting<Boolean>("Blur", false, v -> this.setting.getValue() == Settings.Background));
	
	public Setting<Boolean> guiBackground = this.register(new Setting<Boolean>("GuiBackground", true, v -> this.setting.getValue() == Settings.Background));
	public Setting<Integer> gbRed = this.register(new Setting<Object>("GuiBackgroundRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.guiBackground.getValue()));
    public Setting<Integer> gbGreen = this.register(new Setting<Object>("GuiBackgroundGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.guiBackground.getValue()));
    public Setting<Integer> gbBlue = this.register(new Setting<Object>("GuiBackgroundBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.guiBackground.getValue()));
    public Setting<Integer> gbAlpha = this.register(new Setting<Object>("GuiBackgroundAlpha", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.guiBackground.getValue()));
	
	public Setting<Boolean> moduleSeperate = this.register(new Setting<Boolean>("ModuleSeperateLine", true, v -> this.setting.getValue() == Settings.Lines));
	public Setting<Integer> mosRed = this.register(new Setting<Object>("ModuleSeperateRed", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleSeperate.getValue()));
    public Setting<Integer> mosGreen = this.register(new Setting<Object>("ModuleSeperateGreen", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleSeperate.getValue()));
    public Setting<Integer> mosBlue = this.register(new Setting<Object>("ModuleSeperateBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleSeperate.getValue()));
    public Setting<Integer> mosAlpha = this.register(new Setting<Object>("ModuleSeperateAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleSeperate.getValue()));
	
	public Setting<Boolean> moduleOutline = this.register(new Setting<Boolean>("ModuleOutline", false, v -> this.setting.getValue() == Settings.Lines));
    public Setting<Integer> moRed = this.register(new Setting<Object>("ModuleOutlineRed", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleOutline.getValue()));
    public Setting<Integer> moGreen = this.register(new Setting<Object>("ModuleOutlineGreen", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleOutline.getValue()));
    public Setting<Integer> moBlue = this.register(new Setting<Object>("ModuleOutlineBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleOutline.getValue()));
    public Setting<Integer> moAlpha = this.register(new Setting<Object>("ModuleOutlineAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.moduleOutline.getValue()));
	
    public Setting<Boolean> scroll = this.register(new Setting<Boolean>("Scroll", true, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Integer> scrollval = this.register(new Setting<Integer>("Scroll Speed", 10, 1, 30, v -> this.setting.getValue() == Settings.Misc && this.scroll.getValue()));
	
    public Setting<Integer> red = this.register(new Setting<Integer>("EnableButtonRed", 75, 0, 255, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> green = this.register(new Setting<Integer>("EnableButtonGreen", 75, 0, 255, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> blue = this.register(new Setting<Integer>("EnableButtonBlue", 75, 0, 255, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> hoverAlpha = this.register(new Setting<Integer>("EnableAlpha", 0, 0, 255, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> alpha = this.register(new Setting<Integer>("HoverAlpha", 170, 0, 255, v -> this.setting.getValue() == Settings.Main));
	public Setting<Integer> b_red = this.register(new Setting<Integer>("ButtonBGRed", 40, 0, 255, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> b_green = this.register(new Setting<Integer>("ButtonBGGreen", 40, 0, 255, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> b_blue = this.register(new Setting<Integer>("ButtonBGBlue", 40, 0, 255, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> b_alpha = this.register(new Setting<Integer>("ButtonBGAlpha", 255, 0, 255, v -> this.setting.getValue() == Settings.Main));
	
	public Setting<Integer> textRed = this.register(new Setting<Integer>("EnabledTextRed", 135, 0, 255, v -> this.setting.getValue() == Settings.FontC));
    public Setting<Integer> textGreen = this.register(new Setting<Integer>("EnabledTextGreen", 135, 0, 255, v -> this.setting.getValue() == Settings.FontC));
    public Setting<Integer> textBlue = this.register(new Setting<Integer>("EnabledTextBlue", 255, 0, 255, v -> this.setting.getValue() == Settings.FontC));
    public Setting<Integer> textAlpha = this.register(new Setting<Integer>("EnabledTextAlpha", 255, 0, 255, v -> this.setting.getValue() == Settings.FontC));
    public Setting<Integer> textRed2 = this.register(new Setting<Integer>("DisabledTextRed", 255, 0, 255, v -> this.setting.getValue() == Settings.FontC));
    public Setting<Integer> textGreen2 = this.register(new Setting<Integer>("DisabledTextGreen", 255, 0, 255, v -> this.setting.getValue() == Settings.FontC));
    public Setting<Integer> textBlue2 = this.register(new Setting<Integer>("DisabledTextBlue", 255, 0, 255, v -> this.setting.getValue() == Settings.FontC));
    public Setting<Integer> textAlpha2 = this.register(new Setting<Integer>("DisabledTextAlpha", 255, 0, 255, v -> this.setting.getValue() == Settings.FontC));
	
    public Setting<Boolean> customFov = this.register(new Setting<Boolean>("CustomFov", false, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Float> fov = this.register(new Setting<Object>("Fov", Float.valueOf(135.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f), v -> this.setting.getValue() == Settings.Misc && this.customFov.getValue()));
	public Setting<Boolean> gear = this.register(new Setting<Boolean>("Gears", false, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Boolean> openCloseChange = this.register(new Setting<Boolean>("Open/Close", true, v -> this.setting.getValue() == Settings.Misc));
    public Setting<String> open = this.register(new Setting<Object>("Open:", "+", v -> this.setting.getValue() == Settings.Misc && this.openCloseChange.getValue()).setRenderName(true));
    public Setting<String> close = this.register(new Setting<Object>("Close:", "-", v -> this.setting.getValue() == Settings.Misc && this.openCloseChange.getValue()).setRenderName(true));
    public Setting<String> moduleButton = this.register(new Setting<Object>("Buttons:", "", v -> this.setting.getValue() == Settings.Misc && this.openCloseChange.getValue() == false).setRenderName(true));
	
    public Setting<Boolean> devSettings = this.register(new Setting<Boolean>("TopColor", true, v -> this.setting.getValue() == Settings.Main));
    public Setting<Integer> topRed = this.register(new Setting<Object>("TopRed", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Main && this.devSettings.getValue()));
    public Setting<Integer> topGreen = this.register(new Setting<Object>("TopGreen", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Main && this.devSettings.getValue()));
    public Setting<Integer> topBlue = this.register(new Setting<Object>("TopBlue", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Main && this.devSettings.getValue()));
    public Setting<Integer> topAlpha = this.register(new Setting<Object>("TopAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Main && this.devSettings.getValue()));
	
	public Setting<Boolean> frameSettings = this.register(new Setting<Boolean>("FrameSetting", true, v -> this.setting.getValue() == Settings.Lines));
    public Setting<Integer> frameRed = this.register(new Setting<Integer>("FrameRed", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.frameSettings.getValue()));
    public Setting<Integer> frameGreen = this.register(new Setting<Integer>("FrameGreen", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.frameSettings.getValue()));
    public Setting<Integer> frameBlue = this.register(new Setting<Integer>("FrameBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.frameSettings.getValue()));
    public Setting<Integer> frameAlpha = this.register(new Setting<Integer>("FrameAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Lines && this.frameSettings.getValue()));
	
	public Setting<Boolean> gradiant = this.register(new Setting<Boolean>("Gradiant", false, v -> this.setting.getValue() == Settings.Background));
    public Setting<Integer> gradiantRed = this.register(new Setting<Object>("GradiantRed", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.gradiant.getValue()));
    public Setting<Integer> gradiantGreen = this.register(new Setting<Object>("GradiantGreen", Integer.valueOf(135), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.gradiant.getValue()));
    public Setting<Integer> gradiantBlue = this.register(new Setting<Object>("GradiantBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.gradiant.getValue()));
    public Setting<Integer> gradiantAlpha = this.register(new Setting<Object>("GradiantAlpha", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Background && this.gradiant.getValue()));
	
    public Setting<Boolean> particles = this.register(new Setting<Boolean>("Particles", false, v -> this.setting.getValue() == Settings.Background));
    public Setting<Integer> particleLength = this.register(new Setting<Integer>("ParticleLength", 80, 0, 300, v -> this.setting.getValue() == Settings.Background && this.particles.getValue()));
    public Setting<Integer> particlered = this.register(new Setting<Integer>("ParticleRed", 135, 0, 255, v -> this.setting.getValue() == Settings.Background && this.particles.getValue()));
    public Setting<Integer> particlegreen = this.register(new Setting<Integer>("ParticleGreen", 135, 0, 255, v -> this.setting.getValue() == Settings.Background && this.particles.getValue()));
    public Setting<Integer> particleblue = this.register(new Setting<Integer>("ParticleBlue", 255, 0, 255, v -> this.setting.getValue() == Settings.Background && this.particles.getValue()));
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
            Experium.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
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
            Experium.colorManager.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), this.hoverAlpha.getValue());
        } else {
            Experium.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        }
        Experium.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof ExperiumGui)) {
            this.disable();
            if (mc.entityRenderer.getShaderGroup() != null)
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
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
		Lines,
		Background,
		FontC,
		Scaling,
		Misc;
	}	
}
