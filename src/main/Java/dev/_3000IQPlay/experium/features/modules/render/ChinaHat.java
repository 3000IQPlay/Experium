package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.util.ChinaHatUtil;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;

import java.awt.*;

public class ChinaHat
        extends Module {
    public Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255));
    public Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    public Setting<Integer> red2 = this.register(new Setting<Integer>("Red2", 135, 0, 255));
    public Setting<Integer> green2 = this.register(new Setting<Integer>("Green2", 135, 0, 255));
    public Setting<Integer> blue2 = this.register(new Setting<Integer>("Blue2", 255, 0, 255));
	public Setting<Integer> points = this.register(new Setting<Integer>("Points", 64, 4, 64));
    public Setting<Boolean> firstP = this.register(new Setting<Boolean>("FirstPerson", false));

    public ChinaHat() {
        super("ChinaHat", "Cool china hat from (GuguHack)", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent render3DEvent) {
        float f = 0.0f;
        if (ChinaHat.mc.gameSettings.thirdPersonView != 0 || this.firstP.getValue().booleanValue()) {
            for (int i = 0; i < 400; ++i) {
                f = ChinaHatUtil.getGradientOffset(new Color(this.red2.getValue(), this.green2.getValue(), this.blue2.getValue(), 255), new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), 255), (double)Math.abs(System.currentTimeMillis() / 7L - (long)(i / 2)) / 120.0).getRGB();
                if (ChinaHat.mc.player.isElytraFlying()) {
                    ChinaHatUtil.drawHat(ChinaHat.mc.player, 0.009 + (double)i * 0.0014, render3DEvent.getPartialTicks(), this.points.getValue(), 2.0f, 1.1f - (float)i * 7.85E-4f - (ChinaHat.mc.player.isSneaking() ? 0.03f : 0.03f), (int)f);
                    continue;
                }
                ChinaHatUtil.drawHat(ChinaHat.mc.player, 0.009 + (double)i * 0.0014, render3DEvent.getPartialTicks(), this.points.getValue(), 2.0f, 2.2f - (float)i * 7.85E-4f - (ChinaHat.mc.player.isSneaking() ? 0.03f : 0.03f), (int)f);
            }
        }
    }
}
