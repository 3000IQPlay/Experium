package dev._3000IQPlay.experium.features.notifications;

import dev._3000IQPlay.experium.features.modules.client.DisplayNotify;
import dev._3000IQPlay.experium.util.RenderUtil;
import dev._3000IQPlay.experium.features.notifications.BackInOutEasing;
import dev._3000IQPlay.experium.features.notifications.Animation;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Notification {
    String title = "This is a title.";
    String description = "This is a description.";
    Color color = new Color(255, 255, 255);
    boolean initialized, reversed = false;
    long duration, timePassed;
    Animation animation = new Animation(200f, false, new BackInOutEasing());

    public Notification() {
    }

    public Notification(String title, String description, Color color, long duration) {
        setTitle(title);
        setDescription(description);
        setColor(color);
        setDuration(duration);
    }

    public String getTitle() {
        return title;
    }

    public Notification setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Notification setDescription(String description) {
        this.description = description;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public Notification setColor(Color color) {
        this.color = color;
        return this;
    }

    public boolean ended() {
        return timePassed >= duration;
    }

    public Notification setDuration(long duration) {
        this.duration = duration;
        animation = new Animation(duration / 2f, false, new BackInOutEasing());
        return this;
    }

    public void render(int y) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        timePassed += 10;
        int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(getDescription()) + 50;
        if (!initialized) {
            animation.setState(true);
            initialized = !initialized;
        }
        if (initialized && timePassed >= duration / 2 && !reversed) {
            animation = new Animation(duration / 2f, false, new BackInOutEasing());
            animation.setState(true);
            reversed = true;
        }
        // get the x val
        int x = reversed ? (int) ((scaledResolution.getScaledWidth() - width) + (animation.getAnimationFactor() * width)) : (int) (scaledResolution.getScaledWidth() - (animation.getAnimationFactor() * width));
//        Command.sendMessage("" + x);
        // draw the background
        RenderUtil.drawRectangleCorrectly(x, y, width, 30, new Color(DisplayNotify.INSTANCE.bgRed.getValue(), DisplayNotify.INSTANCE.bgGreen.getValue(), DisplayNotify.INSTANCE.bgBlue.getValue(), 255).getRGB());
        RenderUtil.drawRectangleCorrectly(x, y, 5, 30, color.getRGB());
        // draw text 🆒
        Minecraft.getMinecraft().fontRenderer.drawString(ChatFormatting.BOLD + getTitle(), x + 10, y + 5, new Color(DisplayNotify.INSTANCE.tRed.getValue(), DisplayNotify.INSTANCE.tGreen.getValue(), DisplayNotify.INSTANCE.tBlue.getValue()).getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString(getDescription(), x + 10, y + 20, new Color(DisplayNotify.INSTANCE.descRed.getValue(), DisplayNotify.INSTANCE.descGreen.getValue(), DisplayNotify.INSTANCE.descBlue.getValue()).getRGB());
    }
}