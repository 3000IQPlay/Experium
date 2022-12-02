package dev._3000IQPlay.experium.features.gui.components.items.buttons;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.Button;
import dev._3000IQPlay.experium.features.modules.client.ClickGui;
import dev._3000IQPlay.experium.features.modules.client.HUD;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.MathUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import dev._3000IQPlay.experium.util.Util;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class BooleanButton
        extends Button {
    private final Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 40;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
            int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)), Experium.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)), Experium.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            RenderUtil.drawGradientRect(this.x, this.y, (float)this.width + 7.4f, (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)) : color) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        } else {
            int booleanColor = ColorUtil.toRGBA(ClickGui.getInstance().booleanC.getValue().getRed(), ClickGui.getInstance().booleanC.getValue().getGreen(), ClickGui.getInstance().booleanC.getValue().getBlue(), ClickGui.getInstance().booleanC.getValue().getAlpha());
            int booleanColorHovering = ColorUtil.toRGBA(ClickGui.getInstance().booleanC.getValue().getRed(), ClickGui.getInstance().booleanC.getValue().getGreen(), ClickGui.getInstance().booleanC.getValue().getBlue(), ClickGui.getInstance().moduleMainC.getValue().getAlpha());
            RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, this.getState() ? (booleanColor) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        }
        Experium.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float)ExperiumGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
		if (ClickGui.getInstance().sideSettings.getValue().booleanValue()) {
            int sideColor = ColorUtil.toRGBA(ClickGui.getInstance().sideLineC.getValue().getRed(), ClickGui.getInstance().sideLineC.getValue().getGreen(), ClickGui.getInstance().sideLineC.getValue().getBlue(), ClickGui.getInstance().sideLineC.getValue().getAlpha());
            RenderUtil.drawRect(this.x, this.y, this.x + 1.0f, this.y + (float)this.height + 1.0f, sideColor);
        }
        if (ClickGui.getInstance().enableSwitch.getValue().booleanValue()) {
            Experium.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float)ExperiumGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
			RenderUtil.drawRect(this.x + 85.0f, this.y + 5.0f, this.x + (float)this.width + 3.5f, this.y + (float)this.height - 2.0f, this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().sbC.getValue().getRed(), ClickGui.getInstance().sbC.getValue().getGreen(), ClickGui.getInstance().sbC.getValue().getBlue(), ClickGui.getInstance().sbC.getValue().getAlpha()) : ColorUtil.toRGBA(ClickGui.getInstance().sbC.getValue().getRed(), ClickGui.getInstance().sbC.getValue().getGreen(), ClickGui.getInstance().sbC.getValue().getBlue(), ClickGui.getInstance().sbC.getValue().getAlpha()));
            if (this.getState()) {
                RenderUtil.drawRect(this.x + 93.0f, this.y + 6.0f, this.x + (float)this.width + 2.5f, this.y + (float)this.height - 3.0f, this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().seC.getValue().getRed(), ClickGui.getInstance().seC.getValue().getGreen(), ClickGui.getInstance().seC.getValue().getBlue(), ClickGui.getInstance().seC.getValue().getAlpha()) : ColorUtil.toRGBA(ClickGui.getInstance().seC.getValue().getRed(), ClickGui.getInstance().seC.getValue().getGreen(), ClickGui.getInstance().seC.getValue().getBlue(), ClickGui.getInstance().seC.getValue().getAlpha()));
            } else {
                RenderUtil.drawRect(this.x + 86.0f, this.y + 6.0f, this.x + (float)this.width - 4.7f, this.y + (float)this.height - 3.0f, this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().sdC.getValue().getRed(), ClickGui.getInstance().sdC.getValue().getGreen(), ClickGui.getInstance().sdC.getValue().getBlue(), ClickGui.getInstance().sdC.getValue().getAlpha()) : ColorUtil.toRGBA(ClickGui.getInstance().sdC.getValue().getRed(), ClickGui.getInstance().sdC.getValue().getGreen(), ClickGui.getInstance().sdC.getValue().getBlue(), ClickGui.getInstance().sdC.getValue().getAlpha()));
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.setValue((Boolean)this.setting.getValue() == false);
    }

    @Override
    public boolean getState() {
        return (Boolean)this.setting.getValue();
    }
}
