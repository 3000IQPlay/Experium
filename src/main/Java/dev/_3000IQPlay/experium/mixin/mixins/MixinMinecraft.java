package dev._3000IQPlay.experium.mixin.mixins;

import javax.annotation.Nullable;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.modules.client.Managers;
import dev._3000IQPlay.experium.features.modules.player.MultiTask;
import dev._3000IQPlay.experium.features.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.crash.CrashReport;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = {Minecraft.class})
public abstract class MixinMinecraft {
    @Shadow
    public abstract void displayGuiScreen(@Nullable GuiScreen var1);

    @Inject(method = {"runTickKeyboard"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 0)}, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onRunTickKeyboard(CallbackInfo ci, int i) {
        if (Keyboard.getEventKeyState() && Experium.moduleManager != null) {
            Experium.moduleManager.onKeyPressed(i);
        }
    }

    @Inject(method = {"getLimitFramerate"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void getLimitFramerateHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        try {
            if (Managers.getInstance().unfocusedCpu.getValue().booleanValue() && !Display.isActive()) {
                callbackInfoReturnable.setReturnValue(Managers.getInstance().cpuFPS.getValue());
            }
        } catch (NullPointerException nullPointerException) {
            // empty catch block
        }
    }

    @Redirect(method = {"runGameLoop"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;sync(I)V", remap = false))
    public void syncHook(int maxFps) {
        if (Managers.getInstance().betterFrames.getValue().booleanValue()) {
            Display.sync((int) Managers.getInstance().betterFPS.getValue());
        } else {
            Display.sync((int) maxFps);
        }
    }

    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReportHook(Minecraft minecraft, CrashReport crashReport) {
        this.unload();
    }

    @Redirect(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;doVoidFogParticles(III)V"))
    public void doVoidFogParticlesHook(WorldClient world, int x, int y, int z) {
        NoRender.getInstance().doVoidFogParticles(x, y, z);
    }

    @Inject(method = {"shutdown"}, at = {@At(value = "HEAD")})
    public void shutdownHook(CallbackInfo info) {
        this.unload();
    }

    private void unload() {
        System.out.println("Shutting down: saving configuration");
        Experium.onUnload();
        System.out.println("Configuration saved.");
    }

    @Redirect(method = {"sendClickBlockToController"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
        return !MultiTask.getInstance().isOn() && playerSP.isHandActive();
    }

    @Redirect(method = {"rightClickMouse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0))
    private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
        return !MultiTask.getInstance().isOn() && playerControllerMP.getIsHittingBlock();
    }
}

