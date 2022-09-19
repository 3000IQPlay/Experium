package dev._3000IQPlay.experium.features.gui.components;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.Feature;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.gui.components.items.Item;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.Button;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.ModuleButton;
import dev._3000IQPlay.experium.features.modules.client.ClickGui;
import dev._3000IQPlay.experium.features.modules.client.Colors;
import dev._3000IQPlay.experium.features.modules.client.HUD;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.MathUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import dev._3000IQPlay.experium.util.Util;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.opengl.GL11;

public class Component
        extends Feature {
    private final ArrayList<Item> items = new ArrayList();
    private final ResourceLocation dots = new ResourceLocation("textures/dots.png");
    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    private boolean hidden = false;

    public Component(String name, int x, int y, boolean open) {
        super(name);
        this.x = x;
        this.y = y;
        this.width = ClickGui.getInstance().guiWidth.getValue().intValue();
        this.height = 19;
        this.open = open;
        this.setupItems();
    }

    public void setupItems() {
    }

    private void drag(int mouseX, int mouseY) {
        if (!this.drag) {
            return;
        }
        this.x = this.x2 + mouseX;
        this.y = this.y2 + mouseY;
    }

    private void drawOutline(float thickness, int color) {
        float totalItemHeight = 0.0f;
        if (this.open) {
            totalItemHeight = this.getTotalItemHeight() - 2.0f;
        }
        RenderUtil.drawLine(this.x, (float)this.y - 1.5f, this.x, (float)(this.y + this.height) + totalItemHeight, thickness, ColorUtil.toRGBA(ClickGui.getInstance().o_red.getValue(), ClickGui.getInstance().o_green.getValue(), ClickGui.getInstance().o_blue.getValue(), ClickGui.getInstance().o_alpha.getValue()));
        RenderUtil.drawLine(this.x, (float)this.y - 1.5f, this.x + this.width, (float)this.y - 1.5f, thickness, ColorUtil.toRGBA(ClickGui.getInstance().o_red.getValue(), ClickGui.getInstance().o_green.getValue(), ClickGui.getInstance().o_blue.getValue(), ClickGui.getInstance().o_alpha.getValue()));
        RenderUtil.drawLine(this.x + this.width, (float)this.y - 1.5f, this.x + this.width, (float)(this.y + this.height) + totalItemHeight, thickness, ColorUtil.toRGBA(ClickGui.getInstance().o_red.getValue(), ClickGui.getInstance().o_green.getValue(), ClickGui.getInstance().o_blue.getValue(), ClickGui.getInstance().o_alpha.getValue()));
        RenderUtil.drawLine(this.x, (float)(this.y + this.height) + totalItemHeight, this.x + this.width, (float)(this.y + this.height) + totalItemHeight, thickness, ColorUtil.toRGBA(ClickGui.getInstance().o_red.getValue(), ClickGui.getInstance().o_green.getValue(), ClickGui.getInstance().o_blue.getValue(), ClickGui.getInstance().o_alpha.getValue()));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drag(mouseX, mouseY);
        float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0f : 0.0f;
        int color = -7829368;
        if (this.open) {
            RenderUtil.drawRect(this.x, (float)this.y + 14.0f, this.x + this.width, (float)(this.y + this.height) + totalItemHeight, 0);
            if (ClickGui.getInstance().outlineNew.getValue().booleanValue()) {
                this.drawOutline(ClickGui.getInstance().outlineThickness.getValue().floatValue(), color);
            }
        }
        if (ClickGui.getInstance().devSettings.getValue().booleanValue()) {
            color = ClickGui.getInstance().colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toARGB(ClickGui.getInstance().topRed.getValue(), ClickGui.getInstance().topGreen.getValue(), ClickGui.getInstance().topBlue.getValue(), ClickGui.getInstance().topAlpha.getValue());
            int n = color;
        }
        if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue() && ClickGui.getInstance().colorSync.getValue().booleanValue() && Colors.INSTANCE.rainbow.getValue().booleanValue()) {
            RenderUtil.drawGradientRect((float)this.x, (float)this.y - 1.5f, (float)this.width, (float)(this.height - 4), (int)HUD.getInstance().colorMap.get(MathUtil.clamp(this.y, 0, this.renderer.scaledHeight)), (int)HUD.getInstance().colorMap.get(MathUtil.clamp(this.y + this.height - 4, 0, this.renderer.scaledHeight)));
        } else {
            RenderUtil.drawRect(this.x, (float)this.y - 1.5f, this.x + this.width, this.y + this.height - 6, color);
        }
        if (ClickGui.getInstance().frameSettings.getValue().booleanValue()) {
            int n = color = ClickGui.getInstance().colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toARGB(ClickGui.getInstance().frameRed.getValue(), ClickGui.getInstance().frameGreen.getValue(), ClickGui.getInstance().frameBlue.getValue(), ClickGui.getInstance().frameAlpha.getValue());
            RenderUtil.drawRect(this.x, (float)this.y + 11.0f, this.x + this.width, this.y + this.height - 6, ClickGui.getInstance().colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor().getRGB() : ColorUtil.toARGB(ClickGui.getInstance().frameRed.getValue(), ClickGui.getInstance().frameGreen.getValue(), ClickGui.getInstance().frameBlue.getValue(), ClickGui.getInstance().frameAlpha.getValue()));
        }
        if (this.open) {
            RenderUtil.drawRect(this.x, (float)this.y + 12.5f, this.x + this.width, (float)(this.y + this.height) + totalItemHeight, ColorUtil.toRGBA(ClickGui.getInstance().b_red.getValue(), ClickGui.getInstance().b_green.getValue(), ClickGui.getInstance().b_blue.getValue(), ClickGui.getInstance().b_alpha.getValue()));
        }
        if (this.open) {
            RenderUtil.drawRect(this.x, (float)this.y + 12.5f, this.x + this.width, (float)(this.y + this.height) + totalItemHeight, 0);
            if (ClickGui.getInstance().outline.getValue().booleanValue()) {
                if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
                    GlStateManager.shadeModel((int)7425);
                    GL11.glBegin((int)1);
                    Color currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp(this.y, 0, this.renderer.scaledHeight)));
                    GL11.glColor4f((float)((float)currentColor.getRed() / 255.0f), (float)((float)currentColor.getGreen() / 255.0f), (float)((float)currentColor.getBlue() / 255.0f), (float)((float)currentColor.getAlpha() / 255.0f));
                    GL11.glVertex3f((float)(this.x + this.width), (float)((float)this.y - 1.5f), (float)0.0f);
                    GL11.glVertex3f((float)this.x, (float)((float)this.y - 1.5f), (float)0.0f);
                    GL11.glVertex3f((float)this.x, (float)((float)this.y - 1.5f), (float)0.0f);
                    float currentHeight = (float)this.getHeight() - 1.5f;
                    for (Item item : this.getItems()) {
                        currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp((int)((float)this.y + (currentHeight += (float)item.getHeight() + 1.5f)), 0, this.renderer.scaledHeight)));
                        GL11.glColor4f((float)((float)currentColor.getRed() / 255.0f), (float)((float)currentColor.getGreen() / 255.0f), (float)((float)currentColor.getBlue() / 255.0f), (float)((float)currentColor.getAlpha() / 255.0f));
                        GL11.glVertex3f((float)this.x, (float)((float)this.y + currentHeight), (float)0.0f);
                        GL11.glVertex3f((float)this.x, (float)((float)this.y + currentHeight), (float)0.0f);
                    }
                    currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp((int)((float)(this.y + this.height) + totalItemHeight), 0, this.renderer.scaledHeight)));
                    GL11.glColor4f((float)((float)currentColor.getRed() / 255.0f), (float)((float)currentColor.getGreen() / 255.0f), (float)((float)currentColor.getBlue() / 255.0f), (float)((float)currentColor.getAlpha() / 255.0f));
                    GL11.glVertex3f((float)(this.x + this.width), (float)((float)(this.y + this.height) + totalItemHeight), (float)0.0f);
                    GL11.glVertex3f((float)(this.x + this.width), (float)((float)(this.y + this.height) + totalItemHeight), (float)0.0f);
                    for (Item item : this.getItems()) {
                        currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp((int)((float)this.y + (currentHeight -= (float)item.getHeight() + 1.5f)), 0, this.renderer.scaledHeight)));
                        GL11.glColor4f((float)((float)currentColor.getRed() / 255.0f), (float)((float)currentColor.getGreen() / 255.0f), (float)((float)currentColor.getBlue() / 255.0f), (float)((float)currentColor.getAlpha() / 255.0f));
                        GL11.glVertex3f((float)(this.x + this.width), (float)((float)this.y + currentHeight), (float)0.0f);
                        GL11.glVertex3f((float)(this.x + this.width), (float)((float)this.y + currentHeight), (float)0.0f);
                    }
                    GL11.glVertex3f((float)(this.x + this.width), (float)this.y, (float)0.0f);
                    GL11.glEnd();
                    GlStateManager.shadeModel((int)7424);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                } else {
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
                    GlStateManager.shadeModel((int)7425);
                    GL11.glBegin((int)2);
                    Color outlineColor = ClickGui.getInstance().colorSync.getValue() != false ? new Color(Colors.INSTANCE.getCurrentColorHex()) : new Color(Experium.colorManager.getColorAsIntFullAlpha());
                    GL11.glColor4f((float)outlineColor.getRed(), (float)outlineColor.getGreen(), (float)outlineColor.getBlue(), (float)outlineColor.getAlpha());
                    GL11.glVertex3f((float)this.x, (float)((float)this.y - 1.5f), (float)0.0f);
                    GL11.glVertex3f((float)(this.x + this.width), (float)((float)this.y - 1.5f), (float)0.0f);
                    GL11.glVertex3f((float)(this.x + this.width), (float)((float)(this.y + this.height) + totalItemHeight), (float)0.0f);
                    GL11.glVertex3f((float)this.x, (float)((float)(this.y + this.height) + totalItemHeight), (float)0.0f);
                    GL11.glEnd();
                    GlStateManager.shadeModel((int)7424);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                }
            }
        }
		if (ClickGui.getInstance().categoryTextCenter.getValue().booleanValue()) {
            Experium.textManager.drawStringWithShadow(this.getName(), (float)this.x + (float)(this.width / 2) - (float)(this.renderer.getStringWidth(this.getName()) / 2), (float)this.y - 4.0f - (float)ExperiumGui.getClickGui().getTextOffset(), -1);
        } else {
			Experium.textManager.drawStringWithShadow(this.getName(), (float)this.x + 3.0f, (float)this.y - 4.0f - (float)ExperiumGui.getClickGui().getTextOffset(), -1);
        }
        if (this.open) {
            if (ClickGui.getInstance().categoryDots.getValue().booleanValue()) {
                mc.getTextureManager().bindTexture(this.dots);
                ModuleButton.drawCompleteImage((float)this.x - 5.5f + (float)this.width - 12.0f, (float)this.y - 8.0f - (float)ExperiumGui.getClickGui().getTextOffset(), 12, 11);
            }
            float y = (float)(this.getY() + this.getHeight()) - 3.0f;
            for (Item item : this.getItems()) {
                if (item.isHidden()) continue;
                item.setLocation((float)this.x + 2.0f, y);
                item.setWidth(this.getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += (float)item.getHeight() + 1.5f;
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            ExperiumGui.getClickGui().getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
            });
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            return;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button) {
        this.items.add(button);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isOpen() {
        return this.open;
    }

    public final ArrayList<Item> getItems() {
        return this.items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0f;
        for (Item item : this.getItems()) {
            height += (float)item.getHeight() + 1.5f;
        }
        return height;
    }
}
