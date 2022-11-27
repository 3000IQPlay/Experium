package dev._3000IQPlay.experium.mixin.mixins;

import dev._3000IQPlay.experium.features.loading.CustomSplashProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.List;

@Mixin(value={FMLClientHandler.class})
public class MixinFMLClientHandler {
    @Redirect(method={"beginMinecraftLoading"}, at=@At(value="INVOKE", target="Lnet/minecraftforge/fml/client/SplashProgress;start()V"), remap=false)
    public void startScreen(Minecraft minecraft, List<IResourcePack> resourcePackList, IReloadableResourceManager resourceManager, MetadataSerializer metaSerializer) throws IOException {
        CustomSplashProgress.start();
    }

    @Redirect(method={"haltGame"}, at=@At(value="INVOKE", target="Lnet/minecraftforge/fml/client/SplashProgress;finish()V"), remap=false)
    public void closeScreen(String message, Throwable t) {
        CustomSplashProgress.finish();
    }

    @Redirect(method={"finishMinecraftLoading"}, at=@At(value="INVOKE", target="Lnet/minecraftforge/fml/client/SplashProgress;finish()V"), remap=false)
    public void closeScreenI() {
        CustomSplashProgress.finish();
    }
}