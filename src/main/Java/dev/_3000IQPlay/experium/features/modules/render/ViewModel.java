package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.RenderItemEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ViewModel
        extends Module {
    private static ViewModel INSTANCE = new ViewModel();
    public Setting<Settings> settings = this.register(new Setting<Settings>("Settings", Settings.TRANSLATE));
    public Setting<Boolean> noEatAnimation = this.register(new Setting<Boolean>("NoEatAnimation", Boolean.valueOf(false), v -> this.settings.getValue() == Settings.TWEAKS));
    public Setting<Double> eatX = this.register(new Setting<Double>("EatX", Double.valueOf(1.0), Double.valueOf(-2.0), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.TWEAKS && this.noEatAnimation.getValue() == false));
    public Setting<Double> eatY = this.register(new Setting<Double>("EatY", Double.valueOf(1.0), Double.valueOf(-2.0), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.TWEAKS && this.noEatAnimation.getValue() == false));
    public Setting<Boolean> doBob = this.register(new Setting<Boolean>("ItemBob", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.TWEAKS));
    public Setting<Double> mainX = this.register(new Setting<Double>("MainX", Double.valueOf(0.0), Double.valueOf(-2.0), Double.valueOf(4.0), v -> this.settings.getValue() == Settings.TRANSLATE));
    public Setting<Double> mainY = this.register(new Setting<Double>("MainY", Double.valueOf(-0.0), Double.valueOf(-3.0), Double.valueOf(3.0), v -> this.settings.getValue() == Settings.TRANSLATE));
    public Setting<Double> mainZ = this.register(new Setting<Double>("MainZ", Double.valueOf(-0.0), Double.valueOf(-5.0), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.TRANSLATE));
    public Setting<Double> offX = this.register(new Setting<Double>("OffX", Double.valueOf(0.0), Double.valueOf(-2.0), Double.valueOf(4.0), v -> this.settings.getValue() == Settings.TRANSLATE));
    public Setting<Double> offY = this.register(new Setting<Double>("OffY", Double.valueOf(-0.0), Double.valueOf(-3.0), Double.valueOf(3.0), v -> this.settings.getValue() == Settings.TRANSLATE));
    public Setting<Double> offZ = this.register(new Setting<Double>("OffZ", Double.valueOf(-0.0), Double.valueOf(-5.0), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.TRANSLATE));
    public Setting<Integer> mainRotX = this.register(new Setting<Integer>("MainRotationX", Integer.valueOf(0), Integer.valueOf(-36), Integer.valueOf(36), v -> this.settings.getValue() == Settings.ROTATE));
    public Setting<Integer> mainRotY = this.register(new Setting<Integer>("MainRotationY", Integer.valueOf(0), Integer.valueOf(-36), Integer.valueOf(36), v -> this.settings.getValue() == Settings.ROTATE));
    public Setting<Integer> mainRotZ = this.register(new Setting<Integer>("MainRotationZ", Integer.valueOf(0), Integer.valueOf(-36), Integer.valueOf(36), v -> this.settings.getValue() == Settings.ROTATE));
    public Setting<Integer> offRotX = this.register(new Setting<Integer>("OffRotationX", Integer.valueOf(0), Integer.valueOf(-36), Integer.valueOf(36), v -> this.settings.getValue() == Settings.ROTATE));
    public Setting<Integer> offRotY = this.register(new Setting<Integer>("OffRotationY", Integer.valueOf(0), Integer.valueOf(-36), Integer.valueOf(36), v -> this.settings.getValue() == Settings.ROTATE));
    public Setting<Integer> offRotZ = this.register(new Setting<Integer>("OffRotationZ", Integer.valueOf(0), Integer.valueOf(-36), Integer.valueOf(36), v -> this.settings.getValue() == Settings.ROTATE));
    public Setting<Double> mainScaleX = this.register(new Setting<Double>("MainScaleX", Double.valueOf(1.0), Double.valueOf(0.1), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.SCALE));
    public Setting<Double> mainScaleY = this.register(new Setting<Double>("MainScaleY", Double.valueOf(1.0), Double.valueOf(0.1), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.SCALE));
    public Setting<Double> mainScaleZ = this.register(new Setting<Double>("MainScaleZ", Double.valueOf(1.0), Double.valueOf(0.1), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.SCALE));
    public Setting<Double> offScaleX = this.register(new Setting<Double>("OffScaleX", Double.valueOf(1.0), Double.valueOf(0.1), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.SCALE));
    public Setting<Double> offScaleY = this.register(new Setting<Double>("OffScaleY", Double.valueOf(1.0), Double.valueOf(0.1), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.SCALE));
    public Setting<Double> offScaleZ = this.register(new Setting<Double>("OffScaleZ", Double.valueOf(1.0), Double.valueOf(0.1), Double.valueOf(5.0), v -> this.settings.getValue() == Settings.SCALE));
	public Setting<Boolean> rotatexo = this.register(new Setting("RotateX", Boolean.valueOf(false)));
    public Setting<Boolean> rotateyo = this.register(new Setting("RotateY", Boolean.valueOf(false)));
    public Setting<Boolean> rotatezo = this.register(new Setting("RotateZ", Boolean.valueOf(false)));
    public Setting<Boolean> rotatex = this.register(new Setting("RotateXOff", Boolean.valueOf(false)));
    public Setting<Boolean> rotatey = this.register(new Setting("RotateYOff", Boolean.valueOf(false)));
    public Setting<Boolean> rotatez = this.register(new Setting("RotateZOff", Boolean.valueOf(false)));
    public Setting<Integer> animDelay = this.register(new Setting("RotateSpeed", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1500), v -> this.rotatex.getValue() || this.rotatey.getValue() || this.rotatez.getValue()));
	public Timer timer = new Timer();
	int rotation = -180;

    public ViewModel() {
        super("ViewModel", "Cool", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static ViewModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewModel();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onItemRender(RenderItemEvent event) {
        event.setMainX(this.mainX.getValue());
        event.setMainY(this.mainY.getValue());
        event.setMainZ(this.mainZ.getValue());
        event.setOffX(-this.offX.getValue().doubleValue());
        event.setOffY(this.offY.getValue());
        event.setOffZ(this.offZ.getValue());
        event.setMainRotX(this.mainRotX.getValue() * 5);
        event.setMainRotY(this.mainRotY.getValue() * 5);
        event.setMainRotZ(this.mainRotZ.getValue() * 5);
        event.setOffRotX(this.offRotX.getValue() * 5);
        event.setOffRotY(this.offRotY.getValue() * 5);
        event.setOffRotZ(this.offRotZ.getValue() * 5);
        event.setOffHandScaleX(this.offScaleX.getValue());
        event.setOffHandScaleY(this.offScaleY.getValue());
        event.setOffHandScaleZ(this.offScaleZ.getValue());
        event.setMainHandScaleX(this.mainScaleX.getValue());
        event.setMainHandScaleY(this.mainScaleY.getValue());
        event.setMainHandScaleZ(this.mainScaleZ.getValue());
		if (this.timer.passedMs((long)(1000 / this.animDelay.getValue()))) {
            ++this.rotation;
            if (this.rotation > 180) {
                this.rotation = -180;
            }
            this.timer.reset();
        }
        if (!(this.rotatex.getValue()).booleanValue()) {
            event.setOffRotX((double)(this.offRotX.getValue() * 5));
        } else {
            event.setOffRotX((double)this.rotation);
        }
        if (!(this.rotatey.getValue()).booleanValue()) {
            event.setOffRotY((double)(this.offRotY.getValue() * 5));
        } else {
            event.setOffRotY((double)this.rotation);
        }
        if (!(this.rotatez.getValue()).booleanValue()) {
            event.setOffRotZ((double)(this.offRotZ.getValue() * 5));
        } else {
            event.setOffRotZ((double)this.rotation);
        }
		if (!(this.rotatexo.getValue()).booleanValue()) {
            event.setMainRotX((double)(this.offRotX.getValue() * 5));
        } else {
            event.setMainRotX((double)this.rotation);
        }
        if (!(this.rotateyo.getValue()).booleanValue()) {
            event.setMainRotY((double)(this.offRotY.getValue() * 5));
        } else {
            event.setMainRotY((double)this.rotation);
        }
        if (!(this.rotatezo.getValue()).booleanValue()) {
            event.setMainRotZ((double)(this.offRotZ.getValue() * 5));
        } else {
            event.setMainRotZ((double)this.rotation);
        }
    }

    private static enum Settings {
        TRANSLATE,
        ROTATE,
        SCALE,
        TWEAKS;
    }
}
