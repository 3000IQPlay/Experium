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
            RenderUtil.drawOutlineRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.9f, this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().moRed.getValue(), ClickGui.getInstance().moGreen.getValue(), ClickGui.getInstance().moBlue.getValue(), ClickGui.getInstance().moAlpha.getValue()) : ColorUtil.toRGBA(ClickGui.getInstance().moRed.getValue(), ClickGui.getInstance().moGreen.getValue(), ClickGui.getInstance().moBlue.getValue(), ClickGui.getInstance().moAlpha.getValue()));
        }
		if (ClickGui.getInstance().moduleSeperate.getValue().booleanValue()) {
			RenderUtil.drawRect(this.x + 2.0f, this.y - 0.5f, this.x + (float)this.width - 2.0f, this.y - 0.2f, this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().mosRed.getValue(), ClickGui.getInstance().mosGreen.getValue(), ClickGui.getInstance().mosBlue.getValue(), ClickGui.getInstance().mosAlpha.getValue()) : ColorUtil.toRGBA(ClickGui.getInstance().mosRed.getValue(), ClickGui.getInstance().mosGreen.getValue(), ClickGui.getInstance().mosBlue.getValue(), ClickGui.getInstance().mosAlpha.getValue()));
        }
        if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
            int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)), Experium.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue());
            int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)), Experium.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue());
            RenderUtil.drawGradientRect(this.x, this.y, (float)this.width, (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)) : color) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        } else {
            RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Experium.colorManager.getColorWithAlpha(Experium.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : Experium.colorManager.getColorWithAlpha(Experium.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        }
		if (ClickGui.getInstance().buttonTextCenter.getValue().booleanValue()) {
            Experium.textManager.drawStringWithShadow(this.getName(), (float)this.x + (float)(this.width / 2) - (float)(this.renderer.getStringWidth(this.getName()) / 2), (float)this.y - 2.0f - (float)ExperiumGui.getClickGui().getTextOffset(), this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().textRed.getValue(), ClickGui.getInstance().textGreen.getValue(), ClickGui.getInstance().textBlue.getValue(), ClickGui.getInstance().textAlpha.getValue()) : ColorUtil.toRGBA(ClickGui.getInstance().textRed2.getValue(), ClickGui.getInstance().textGreen2.getValue(), ClickGui.getInstance().textBlue2.getValue(), ClickGui.getInstance().textAlpha2.getValue()));
        } else {
			Experium.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float)ExperiumGui.getClickGui().getTextOffset(), this.getState() ? ColorUtil.toRGBA(ClickGui.getInstance().textRed.getValue(), ClickGui.getInstance().textGreen.getValue(), ClickGui.getInstance().textBlue.getValue(), ClickGui.getInstance().textAlpha.getValue()) : ColorUtil.toRGBA(ClickGui.getInstance().textRed2.getValue(), ClickGui.getInstance().textGreen2.getValue(), ClickGui.getInstance().textBlue2.getValue(), ClickGui.getInstance().textAlpha2.getValue()));
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
