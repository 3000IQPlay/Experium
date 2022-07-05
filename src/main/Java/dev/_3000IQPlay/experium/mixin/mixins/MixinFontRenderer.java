package dev._3000IQPlay.experium.mixin.mixins;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.modules.client.FontMod;
import dev._3000IQPlay.experium.features.modules.client.HUD;
import dev._3000IQPlay.experium.features.modules.client.NickHider;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {FontRenderer.class})
public abstract class MixinFontRenderer {
    @Shadow
    protected abstract int renderString(String var1, float var2, float var3, int var4, boolean var5);

    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);

    @Inject(method = {"drawString(Ljava/lang/String;FFIZ)I"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info) {
        if (FontMod.getInstance().isOn() && FontMod.getInstance().full.getValue().booleanValue() && Experium.textManager != null) {
            float result = Experium.textManager.drawString(text, x, y, color, dropShadow);
            info.setReturnValue((int) result);
        }
    }

    @Redirect(method = {"drawString(Ljava/lang/String;FFIZ)I"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"))
    public int renderStringHook(FontRenderer fontrenderer, String text, float x, float y, int color, boolean dropShadow) {
        if (Experium.moduleManager != null && HUD.getInstance().shadow.getValue().booleanValue() && dropShadow) {
            return this.renderString(text, x - 0.5f, y - 0.5f, color, true);
        }
        return this.renderString(text, x, y, color, dropShadow);
    }

    @Redirect(method = {"renderString(Ljava/lang/String;FFIZ)I"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer renderer, String text, boolean shadow) {
        if (NickHider.getInstance().isOn() && NickHider.getInstance().changeOwn.getValue().booleanValue()) {
            this.renderStringAtPos(text.replace(NickHider.getPlayerName(), NickHider.getInstance().ownName.getValue()), shadow);
        } else {
            this.renderStringAtPos(text, shadow);
        }
    }
}

