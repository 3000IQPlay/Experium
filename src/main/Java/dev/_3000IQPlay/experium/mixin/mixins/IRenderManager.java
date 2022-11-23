package dev._3000IQPlay.experium.mixin.mixins.accessors;

import net.minecraft.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderManager.class})
public interface IRenderManager {
    @Accessor(value="renderPosX")
    public double getRenderPosX();

    @Accessor(value="renderPosY")
    public double getRenderPosY();

    @Accessor(value="renderPosZ")
    public double getRenderPosZ();
}