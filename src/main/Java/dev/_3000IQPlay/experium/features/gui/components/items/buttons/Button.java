package dev._3000IQPlay.experium.features.gui.components.items.buttons;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.gui.components.Component;
import dev._3000IQPlay.experium.features.gui.components.items.Item;
import dev._3000IQPlay.experium.features.modules.client.ClickGui;
import dev._3000IQPlay.experium.features.modules.client.HUD;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.MathUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class Button
        extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().moduleOutline.getValue().booleanValue()) {
            RenderUtil.drawOutlineRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.9f, this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().moduleOutlineC.getValue().getRed(), ClickGui.getInstance().moduleOutlineC.getValue().getGreen(), ClickGui.getInstance().moduleOutlineC.getValue().getBlue(), ClickGui.getInstance().moduleOutlineC.getValue().getAlpha()) : ColorUtil.toRGBA(ClickGui.getInstance().moduleOutlineC.getValue().getRed(), ClickGui.getInstance().moduleOutlineC.getValue().getGreen(), ClickGui.getInstance().moduleOutlineC.getValue().getBlue(), ClickGui.getInstance().moduleOutlineC.getValue().getAlpha()));
        }
		if (ClickGui.getInstance().moduleSeperate.getValue().booleanValue()) {
			RenderUtil.drawRect(this.x + 2.0f, this.y - 0.5f, this.x + (float)this.width - 2.0f, this.y - 0.2f, this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().moduleSeperateC.getValue().getRed(), ClickGui.getInstance().moduleSeperateC.getValue().getGreen(), ClickGui.getInstance().moduleSeperateC.getValue().getBlue(), ClickGui.getInstance().moduleSeperateC.getValue().getAlpha()) : ColorUtil.toRGBA(ClickGui.getInstance().moduleSeperateC.getValue().getRed(), ClickGui.getInstance().moduleSeperateC.getValue().getGreen(), ClickGui.getInstance().moduleSeperateC.getValue().getBlue(), ClickGui.getInstance().moduleSeperateC.getValue().getAlpha()));
        }
        if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
            int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)), Experium.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)), Experium.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            RenderUtil.drawGradientRect(this.x, this.y, (float)this.width, (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)) : color) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        } else {
            RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Experium.colorManager.getColorWithAlpha(Experium.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha()) : Experium.colorManager.getColorWithAlpha(Experium.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        }
		if (ClickGui.getInstance().buttonTextCenter.getValue().booleanValue()) {
            Experium.textManager.drawStringWithShadow(this.getName(), (float)this.x + (float)(this.width / 2.0f) - (float)(this.renderer.getStringWidth(this.getName()) / 2.0f), (float)this.y - 2.0f - (float)ExperiumGui.getClickGui().getTextOffset(), this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().textEnableC.getValue().getRed(), ClickGui.getInstance().textEnableC.getValue().getGreen(), ClickGui.getInstance().textEnableC.getValue().getBlue(), 255) : ColorUtil.toRGBA(ClickGui.getInstance().textDisabledC.getValue().getRed(), ClickGui.getInstance().textDisabledC.getValue().getGreen(), ClickGui.getInstance().textDisabledC.getValue().getBlue(), 255));
        } else {
			Experium.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float)ExperiumGui.getClickGui().getTextOffset(), this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().textEnableC.getValue().getRed(), ClickGui.getInstance().textEnableC.getValue().getGreen(), ClickGui.getInstance().textEnableC.getValue().getBlue(), 255) : ColorUtil.toRGBA(ClickGui.getInstance().textDisabledC.getValue().getRed(), ClickGui.getInstance().textDisabledC.getValue().getGreen(), ClickGui.getInstance().textDisabledC.getValue().getBlue(), 255));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : ExperiumGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY <= this.getY() + (float)this.height;
    }
}
