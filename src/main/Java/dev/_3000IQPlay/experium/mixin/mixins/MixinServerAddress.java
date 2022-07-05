package dev._3000IQPlay.experium.mixin.mixins;

import dev._3000IQPlay.experium.features.modules.client.PingBypass;
import dev._3000IQPlay.experium.mixin.mixins.accessors.IServerAddress;
import net.minecraft.client.multiplayer.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {ServerAddress.class})
public abstract class MixinServerAddress {
    @Redirect(method = {"fromString"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerAddress;getServerAddress(Ljava/lang/String;)[Ljava/lang/String;"))
    private static String[] getServerAddressHook(String ip) {
        PingBypass module;
        int port;
        if (ip.equals(PingBypass.getInstance().ip.getValue()) && (port = (module = PingBypass.getInstance()).getPort()) != -1) {
            return new String[]{PingBypass.getInstance().ip.getValue(), Integer.toString(port)};
        }
        return IServerAddress.getServerAddress(ip);
    }
}

