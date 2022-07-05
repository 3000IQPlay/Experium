package dev._3000IQPlay.experium.features.modules.client;

import java.awt.Color;
import java.util.Objects;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.util.MathUtil;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ExperiumCSGO
        extends Module {
    public ExperiumCSGO() {
        super("ExperiumCSGO", "Hot csgo watermark", Module.Category.CLIENT, true, false, false);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (ExperiumCSGO.nullCheck()) {
            return;
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            String ping = this.getPing((EntityPlayer)ExperiumCSGO.mc.player) + "ms";
            String fpsText = Minecraft.debugFPS + "fps ";
            String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toLowerCase() : ExperiumCSGO.mc.getCurrentServerData().serverIP.toLowerCase();
            String text = "Experium | " + server + " | " + ping + " | " + fpsText;
            float width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 6;
            int height = 20;
            int posX = 2;
            int posY = 2;
            RenderUtil.drawRectangleCorrectly(posX - 4, posY - 4, (int)(width + 10.0f), height + 6, ColorUtil.toRGBA(0, 0, 0, 255));
            RenderUtil.drawRectangleCorrectly(posX - 4, posY - 4, (int)(width + 11.0f), height + 7, ColorUtil.toRGBA(20, 20, 20, 255));
            ExperiumCSGO.drawRect(posX, posY, (float)posX + width + 2.0f, posY + height, new Color(30, 30, 30, 255).getRGB());
            ExperiumCSGO.drawRect((double)posX + 2.5, (double)posY + 2.5, (double)((float)posX + width) - 0.5, (double)posY + 4.5, new Color(90, 35, 136, 165).getRGB());
            ExperiumCSGO.drawGradientSideways(4.0, posY + 3, 4.0f + width / 3.0f, posY + 4, new Color(164, 205, 253, 255).getRGB(), new Color(228, 139, 255, 255).getRGB());
            ExperiumCSGO.drawGradientSideways(4.0f + width / 3.0f, posY + 3, 4.0f + width / 3.0f * 2.0f, posY + 4, new Color(216, 111, 252, 255).getRGB(), new Color(236, 93, 128, 255).getRGB());
            ExperiumCSGO.drawGradientSideways(4.0f + width / 3.0f * 2.0f, posY + 3, width / 3.0f * 3.0f + 1.0f, posY + 4, new Color(236, 93, 128, 255).getRGB(), new Color(75, 213, 149, 255).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float)(4 + posX), (float)(8 + posY), -1);
        }
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)f, (float)f1, (float)f2, (float)f3);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(left, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, top, 0.0).endVertex();
        bufferBuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float)(col1 >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(col1 >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(col1 >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(col1 & 0xFF) / 255.0f;
        float f4 = (float)(col2 >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(col2 >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(col2 >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(col2 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glShadeModel((int)7425);
        GL11.glPushMatrix();
        GL11.glBegin((int)7);
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glVertex2d((double)left, (double)top);
        GL11.glVertex2d((double)left, (double)bottom);
        GL11.glColor4f((float)f5, (float)f6, (float)f7, (float)f4);
        GL11.glVertex2d((double)right, (double)bottom);
        GL11.glVertex2d((double)right, (double)top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    private int getPing(EntityPlayer player) {
        int ping = 0;
        try {
            ping = (int)MathUtil.clamp((float)Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1.0f, 300.0f);
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return ping;
    }
}