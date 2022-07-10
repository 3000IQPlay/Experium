package dev._3000IQPlay.experium.mixin.mixins;

import java.awt.Color;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.modules.client.Colors;
import dev._3000IQPlay.experium.features.modules.render.CrystalModify;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {RenderEnderCrystal.class})
public abstract class MixinRenderEnderCrystal {
    private static final ResourceLocation RES_ITEM_GLINT;
    @Final
    @Shadow
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;

    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    }

    @Shadow
    public ModelBase modelEnderCrystal;
    @Shadow
    public ModelBase modelEnderCrystalNoBase;

    @Shadow
    public abstract void doRender(EntityEnderCrystal var1, double var2, double var4, double var6, float var8, float var9);

    @Redirect(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void render1(ModelBase var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if (!Experium.moduleManager.isModuleEnabled(CrystalModify.class)) {
            var1.render(var2, var3, var4, var5, var6, var7, var8);
        }
    }

    @Redirect(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V", ordinal = 1))
    private void render2(ModelBase var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if (!Experium.moduleManager.isModuleEnabled(CrystalModify.class)) {
            var1.render(var2, var3, var4, var5, var6, var7, var8);
        }
    }

    @Inject(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = {@At(value = "RETURN")}, cancellable = true)
    public void IdoRender(EntityEnderCrystal var1, double var2, double var4, double var6, float var8, float var9, CallbackInfo var10) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.fancyGraphics = false;
        if (Experium.moduleManager.isModuleEnabled(CrystalModify.class)) {
            Color outlineColor;
            GL11.glPushMatrix();
            float var14 = (float) var1.innerRotation + var9;
            GlStateManager.translate((double) var2, (double) var4, (double) var6);
            GlStateManager.scale((float) CrystalModify.INSTANCE.size.getValue().floatValue(), (float) CrystalModify.INSTANCE.size.getValue().floatValue(), (float) CrystalModify.INSTANCE.size.getValue().floatValue());
            Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
            float var15 = MathHelper.sin((float) (var14 * 0.2f)) / 2.0f + 0.5f;
            var15 += var15 * var15;
            float spinSpeed = CrystalModify.INSTANCE.crystalSpeed.getValue().floatValue();
            float bounceSpeed = CrystalModify.INSTANCE.crystalBounce.getValue().floatValue();
            if (CrystalModify.INSTANCE.texture.getValue().booleanValue()) {
                if (var1.shouldShowBottom()) {
                    this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                } else {
                    this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                }
            }
            GL11.glPushAttrib((int) 1048575);
            if (CrystalModify.INSTANCE.mode.getValue().equals((Object) CrystalModify.modes.WIREFRAME)) {
                GL11.glPolygonMode((int) 1032, (int) 6913);
            }
            if (CrystalModify.INSTANCE.blendModes.getValue().equals((Object) CrystalModify.BlendModes.Default)) {
				GL11.glBlendFunc((int) 770, (int) 771);
            }
			if (CrystalModify.INSTANCE.blendModes.getValue().equals((Object) CrystalModify.BlendModes.Brighter)) {
                GL11.glBlendFunc((int)770, (int)32772);
            }
            GL11.glDisable((int) 3008);
            GL11.glDisable((int) 3553);
            GL11.glDisable((int) 2896);
            GL11.glEnable((int) 3042);
            GL11.glLineWidth((float) 1.5f);
            GL11.glEnable((int) 2960);
            GL11.glDisable((int) 2929);
            GL11.glDepthMask((boolean) false);
            GL11.glEnable((int) 10754);
            Color visibleColor = CrystalModify.INSTANCE.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(CrystalModify.INSTANCE.red.getValue(), CrystalModify.INSTANCE.green.getValue(), CrystalModify.INSTANCE.blue.getValue());
            Color hiddenColor = CrystalModify.INSTANCE.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(CrystalModify.INSTANCE.hiddenRed.getValue(), CrystalModify.INSTANCE.hiddenGreen.getValue(), CrystalModify.INSTANCE.hiddenBlue.getValue());
            Color color = outlineColor = CrystalModify.INSTANCE.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(CrystalModify.INSTANCE.outlineRed.getValue(), CrystalModify.INSTANCE.outlineGreen.getValue(), CrystalModify.INSTANCE.outlineBlue.getValue());
            if (CrystalModify.INSTANCE.hiddenSync.getValue().booleanValue()) {
                GL11.glColor4f((float) ((float) visibleColor.getRed() / 255.0f), (float) ((float) visibleColor.getGreen() / 255.0f), (float) ((float) visibleColor.getBlue() / 255.0f), (float) ((float) CrystalModify.INSTANCE.alpha.getValue().intValue() / 255.0f));
            } else {
                GL11.glColor4f((float) ((float) hiddenColor.getRed() / 255.0f), (float) ((float) hiddenColor.getGreen() / 255.0f), (float) ((float) hiddenColor.getBlue() / 255.0f), (float) ((float) CrystalModify.INSTANCE.hiddenAlpha.getValue().intValue() / 255.0f));
            }
            if (var1.shouldShowBottom()) {
                this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            } else {
                this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            }
            GL11.glEnable((int) 2929);
            GL11.glDepthMask((boolean) true);
            GL11.glColor4f((float) ((float) visibleColor.getRed() / 255.0f), (float) ((float) visibleColor.getGreen() / 255.0f), (float) ((float) visibleColor.getBlue() / 255.0f), (float) ((float) CrystalModify.INSTANCE.alpha.getValue().intValue() / 255.0f));
            if (var1.shouldShowBottom()) {
                this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            } else {
                this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            }
            if (CrystalModify.INSTANCE.enchanted.getValue().booleanValue()) {
                mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
                GL11.glTexCoord3d((double) 1.0, (double) 1.0, (double) 1.0);
                GL11.glEnable((int) 3553);
                GL11.glBlendFunc((int) 768, (int) 771);
                GL11.glColor4f((float) ((float) CrystalModify.INSTANCE.enchantRed.getValue().intValue() / 255.0f), (float) ((float) CrystalModify.INSTANCE.enchantGreen.getValue().intValue() / 255.0f), (float) ((float) CrystalModify.INSTANCE.enchantBlue.getValue().intValue() / 255.0f), (float) ((float) CrystalModify.INSTANCE.enchantAlpha.getValue().intValue() / 255.0f));
                if (var1.shouldShowBottom()) {
                    this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                } else {
                    this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                }
                if (CrystalModify.INSTANCE.blendModes.getValue().equals((Object) CrystalModify.BlendModes.Default)) {
				    GL11.glBlendFunc((int) 768, (int) 771);
                }
			    if (CrystalModify.INSTANCE.blendModes.getValue().equals((Object) CrystalModify.BlendModes.Brighter)) {
                    GL11.glBlendFunc((int)770, (int)32772);
			    } else {
                    GL11.glBlendFunc((int)770, (int)771);	
                }
                GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
            }
            GL11.glEnable((int) 3042);
            GL11.glEnable((int) 2896);
            GL11.glEnable((int) 3553);
            GL11.glEnable((int) 3008);
            GL11.glPopAttrib();
            if (CrystalModify.INSTANCE.outline.getValue().booleanValue()) {
                if (CrystalModify.INSTANCE.outlineMode.getValue().equals((Object) CrystalModify.outlineModes.WIRE)) {
                    GL11.glPushAttrib((int) 1048575);
                    GL11.glPolygonMode((int) 1032, (int) 6913);
                    GL11.glDisable((int) 3008);
                    GL11.glDisable((int) 3553);
                    GL11.glDisable((int) 2896);
                    GL11.glEnable((int) 3042);
                    GL11.glBlendFunc((int) 770, (int) 771);
                    GL11.glLineWidth((float) CrystalModify.INSTANCE.lineWidth.getValue().floatValue());
                    GL11.glEnable((int) 2960);
                    GL11.glDisable((int) 2929);
                    GL11.glDepthMask((boolean) false);
                    GL11.glEnable((int) 10754);
                    GL11.glColor4f((float) ((float) outlineColor.getRed() / 255.0f), (float) ((float) outlineColor.getGreen() / 255.0f), (float) ((float) outlineColor.getBlue() / 255.0f), (float) ((float) CrystalModify.INSTANCE.outlineAlpha.getValue().intValue() / 255.0f));
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    GL11.glEnable((int) 2929);
                    GL11.glDepthMask((boolean) true);
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    GL11.glEnable((int) 3042);
                    GL11.glEnable((int) 2896);
                    GL11.glEnable((int) 3553);
                    GL11.glEnable((int) 3008);
                    GL11.glPopAttrib();
                } else {
                    RenderUtil.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue()));
                    RenderUtil.renderOne(CrystalModify.INSTANCE.lineWidth.getValue().floatValue());
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    RenderUtil.renderTwo();
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    RenderUtil.renderThree();
                    RenderUtil.renderFour(outlineColor);
                    RenderUtil.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue()));
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    RenderUtil.renderFive();
                    RenderUtil.setColor(Color.WHITE);
                }
            }
            GL11.glPopMatrix();
        }
    }
}

