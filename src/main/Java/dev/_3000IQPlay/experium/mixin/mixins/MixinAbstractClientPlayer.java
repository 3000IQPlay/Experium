package dev._3000IQPlay.experium.mixin.mixins;

import java.util.UUID;
import javax.annotation.Nullable;

import dev._3000IQPlay.experium.features.modules.render.Chams;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = {"getLocationSkin()Lnet/minecraft/util/ResourceLocation;"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void getLocationSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (Chams.getInstance().textured.getValue().booleanValue() && Chams.getInstance().isEnabled()) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/shinechams3.png"));
        }
    }
}