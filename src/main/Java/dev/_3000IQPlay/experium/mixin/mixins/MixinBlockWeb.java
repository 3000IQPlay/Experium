package dev._3000IQPlay.experium.mixin.mixins;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.modules.misc.SolidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(value={BlockWeb.class})
public class MixinBlockWeb
extends Block {
    protected MixinBlockWeb() {
        super(Material.WEB);
    }

    @Nullable
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        if (Experium.moduleManager.getModuleByClass(SolidBlock.class).isEnabled()) {
			if (SolidBlock.getInstance().web.getValue().booleanValue()) {
                return FULL_BLOCK_AABB;
			}
        }
        return NULL_AABB;
    }
}
