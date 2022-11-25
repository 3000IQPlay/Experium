package dev._3000IQPlay.experium.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.modules.client.ClickGui;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class ColorButton extends Button {

    Setting setting;
    private Color finalColor;
    boolean pickingColor = false;
    boolean pickingHue = false;
    boolean pickingAlpha = false;
    public static Tessellator tessellator;
    public static BufferBuilder builder;

    private int oldMouseX = -1;
    private int oldMouseY = -1;

    static {
        tessellator = Tessellator.getInstance();
        builder = tessellator.getBuffer();
    }

    public ColorButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        finalColor = (Color) setting.getValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().sideSettings.getValue().booleanValue()) {
            int sideColor = ColorUtil.toRGBA(ClickGui.getInstance().sideRed.getValue(), ClickGui.getInstance().sideGreen.getValue(), ClickGui.getInstance().sideBlue.getValue(), ClickGui.getInstance().sideAlpha.getValue());
            RenderUtil.drawRect(this.x, this.y, this.x + 1.0f, this.y + (float) this.height + 1.0f, sideColor);
        }
        try {
            RenderUtil.drawRect(x + width - 5, y + 2, x + width + 7, y + height - 2, finalColor.getRGB());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        RenderUtil.drawRect(x, y, x + (float) width + 7.4f, y + (float) height - 0.5f, !isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515);
        RenderUtil.drawOutlineRect(x + width - 5, y + 2, x + width + 7, y + height - 2, 0);
        Experium.textManager.drawStringWithShadow(getName(), x + 2.3f, y - 1.7f - (float) ExperiumGui.getClickGui().getTextOffset(), -1);
        if (setting.isOpen) {
            drawPicker(setting, (int) x + 4, (int) y + 15, (int) x + 4, (int) y + 103, (int) x + 4, (int) y + 95, mouseX, mouseY);
            Experium.textManager.drawStringWithShadow(isInsideCopy(mouseX, mouseY) ? ChatFormatting.UNDERLINE + "Copy" : "Copy", x + 25f, y + 113, -1);
            Experium.textManager.drawStringWithShadow(isInsidePaste(mouseX, mouseY) ? ChatFormatting.UNDERLINE + "Paste" : "Paste", x + 60f, y + 113, -1);
            setting.setValue(finalColor);

        }
    }

    @Override
    public void update() {
        setHidden(!setting.isVisible());
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            setting.isOpen = !setting.isOpen;
        }
        if (mouseButton == 0 && isInsideCopy(mouseX, mouseY) && setting.isOpen) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            String hex = String.format("#%06x", ((Color) setting.getValue()).getRGB() & 0xFFFFFF);
            StringSelection selection = new StringSelection(hex);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            Command.sendMessage("Color" + setting.getValue() + "has been successfully copied to clipboard!");
        }
        if (mouseButton == 0 && isInsidePaste(mouseX, mouseY) && setting.isOpen) {
            try {
                if (readClipboard() != null) {
                    if (Objects.requireNonNull(readClipboard()).startsWith("#")) { // hex color
                        setting.setValue(Color.decode(Objects.requireNonNull(readClipboard())));
                    } else {
                        String[] color = readClipboard().split(","); // r, g, b
                        setting.setValue(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                    }
                }
            } catch (NumberFormatException ex) {
                Command.sendMessage("Not a color format! Available color formats:");
                Command.sendMessage("RGB: (Red), (Green), (Blue)");
                Command.sendMessage("HEX: #FFFFFF - must start with a hashtag '#'");
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        pickingColor = pickingHue = pickingAlpha = false;
    }

    public boolean isInsideCopy(int mouseX, int mouseY) {
        return mouseOver((int) x + 25, (int) y + 113, (int) (x + 25) + Experium.textManager.getStringWidth("Copy"), (int) (y + 112) + Experium.textManager.getFontHeight(), mouseX, mouseY);
    }

    public boolean isInsidePaste(int mouseX, int mouseY) {
        return mouseOver((int) x + 60, (int) y + 113, (int) (x + 60) + Experium.textManager.getStringWidth("Paste"), (int) (y + 112) + Experium.textManager.getFontHeight(), mouseX, mouseY);
    }

    public void drawPicker(Setting setting, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float[] color = new float[]{
                0, 0, 0, 0
        };

        try {
            color = new float[]{
                    Color.RGBtoHSB(((Color) setting.getValue()).getRed(), ((Color) setting.getValue()).getGreen(), ((Color) setting.getValue()).getBlue(), null)[0], Color.RGBtoHSB(((Color) setting.getValue()).getRed(), ((Color) setting.getValue()).getGreen(), ((Color) setting.getValue()).getBlue(), null)[1], Color.RGBtoHSB(((Color) setting.getValue()).getRed(), ((Color) setting.getValue()).getGreen(), ((Color) setting.getValue()).getBlue(), null)[2], ((Color) setting.getValue()).getAlpha() / 255f
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int pickerWidth = 101;
        int pickerHeight = 78;
        int hueSliderWidth = pickerWidth;
        int hueSliderHeight = 7;
        int alphaSliderHeight = 7;

        if (pickingColor) {
            if (!(Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY))) {
                pickingColor = false;
            }
        }

        if (pickingHue) {
            if (!(Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY))) {
                pickingHue = false;
            }
        }

        if (pickingAlpha) {
            if (!(Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY)))
                pickingAlpha = false;
        }

        if (Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY))
            pickingColor = true;
        if (Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY))
            pickingHue = true;
        if (Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY))
            pickingAlpha = true;

        if (pickingHue) {
            float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
            color[0] = (restrictedX - (float) hueSliderX) / hueSliderWidth;
        }

        if (pickingAlpha) {
            float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + pickerWidth);
            color[3] = 1 - (restrictedX - (float) alphaSliderX) / pickerWidth;
        }

        if (pickingColor) {
            float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - (float) pickerX) / pickerWidth;
            color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
        }

        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);

        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;

        drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, color[3]);

        drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth -5, hueSliderHeight, color[0]);

        int cursorX = (int) (pickerX + color[1] * pickerWidth);
        int cursorY = (int) ((pickerY + pickerHeight) - color[2] * pickerHeight);

        RenderUtil.drawOutlineRect(cursorX - 2, cursorY - 2, cursorX - 2, cursorY - 2, 0); //cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2
        Gui.drawRect(cursorX - 2, cursorY - 2, cursorX - 2, cursorY - 2, -1); //cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2

        drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth -5, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);

        if((Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY)) || oldMouseX < 0 || oldMouseY < 0){
            oldMouseX = mouseX;
            oldMouseY = mouseY;
        }

        drawIndicator();

        finalColor = getColor(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
    }

    void drawIndicator(){
        Gui.drawRect(oldMouseX - 2, oldMouseY + 2, oldMouseX + 2, oldMouseY - 2, Color.WHITE.getRGB());
    }

    boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }

    Color getColor(Color color, float alpha) {
        float red = (float) color.getRed() / 255;
        float green = (float) color.getGreen() / 255;
        float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha);
    }

    void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_POLYGON);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2f(pickerX, pickerY);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glColor4f(red, green, blue, alpha);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glDisable(GL_ALPHA_TEST);
        glBegin(GL_POLYGON);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX, pickerY);
        glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glEnable(GL_ALPHA_TEST);
        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            RenderUtil.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                drawGradientRect(x, y + step * (height / 6f), x + width, y + (step + 1) * (height / 6f), previousStep, nextStep, false);
                step++;
            }
            int sliderMinY = (int) (y + height * hue) - 4;
            RenderUtil.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
            RenderUtil.drawOutlineRect(x, sliderMinY - 1, x + width, sliderMinY + 1, 0);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                step++;
            }
            int sliderMinX = (int) (x + (width * hue));
            RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
            RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, 0);
        }
    }

    void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = height / 2;
        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                RenderUtil.drawRect(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, 0xFFFFFFFF);
                RenderUtil.drawRect(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, 0xFF909090);

                if (squareIndex < width - checkerBoardSquareSize) {
                    int minX = x + squareIndex + checkerBoardSquareSize;
                    int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    RenderUtil.drawRect(minX, y, maxX, y + height, 0xFF909090);
                    RenderUtil.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, 0xFFFFFFFF);
                }
            }
            left = !left;
        }
        drawLeftGradientRect(x, y, x + width, y + height, new Color(red, green, blue, 1).getRGB(), 0);
        int sliderMinX = (int) (x + width - (width * alpha));
        RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, 0);
    }


    void drawGradientRect(double leftpos, double top,double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0f;
        float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        float f4 = (col1 & 0xFF) / 255.0f;
        float f5 = (col2 >> 24 & 0xFF) / 255.0f;
        float f6 = (col2 >> 16 & 0xFF) / 255.0f;
        float f7 = (col2 >> 8 & 0xFF) / 255.0f;
        float f8 = (col2 & 0xFF) / 255.0f;
        glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(leftpos, top);
        GL11.glVertex2d(leftpos, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    void drawLeftGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);
        builder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(right, top, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(left, top, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(left, bottom, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(right, bottom, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {
            float startA = (startColor >> 24 & 0xFF) / 255.0f;
            float startR = (startColor >> 16 & 0xFF) / 255.0f;
            float startG = (startColor >> 8 & 0xFF) / 255.0f;
            float startB = (startColor & 0xFF) / 255.0f;
            float endA = (endColor >> 24 & 0xFF) / 255.0f;
            float endR = (endColor >> 16 & 0xFF) / 255.0f;
            float endG = (endColor >> 8 & 0xFF) / 255.0f;
            float endB = (endColor & 0xFF) / 255.0f;
            glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);{
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        } else {
            drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
        }
    }

    int gradientColor(int color, int percentage) {
        int r = (((color & 0xFF0000) >> 16) * (100 + percentage) / 100);
        int g = (((color & 0xFF00) >> 8) * (100 + percentage) / 100);
        int b = ((color & 0xFF) * (100 + percentage) / 100);
        return new Color(r, g, b).hashCode();
    }

    void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor, boolean hovered) {
        if (hovered) {
            startColor = gradientColor(startColor, -20);
            endColor = gradientColor(endColor, -20);
        }
        float c = (float) (startColor >> 24 & 255) / 255.0F;
        float c1 = (float) (startColor >> 16 & 255) / 255.0F;
        float c2 = (float) (startColor >> 8 & 255) / 255.0F;
        float c3 = (float) (startColor & 255) / 255.0F;
        float c4 = (float) (endColor >> 24 & 255) / 255.0F;
        float c5 = (float) (endColor >> 16 & 255) / 255.0F;
        float c6 = (float) (endColor >> 8 & 255) / 255.0F;
        float c7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos(left, top, 0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(c5, c6, c7, c4).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(c5, c6, c7, c4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static String readClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException exception) {
            return null;
        }

    }

    @Override
    public int getHeight() {
        return setting.isOpen ? 124 : 14;
    }
}