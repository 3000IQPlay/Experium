package dev._3000IQPlay.experium.mixin;

import dev._3000IQPlay.experium.Experium;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class ExperiumMixinLoader
        implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public ExperiumMixinLoader() {
        Experium.LOGGER.info("Experium mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.experium.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        Experium.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

