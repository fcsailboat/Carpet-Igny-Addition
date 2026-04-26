package com.liuyue.igny.mixins.rule.transparentNightmarishBlock;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "getExplosionResistance", at = @At("HEAD"), cancellable = true)
    private void getExplosionResistance(CallbackInfoReturnable<Float> cir) {
        if (IGNYSettings.transparentNightmarishBlock) {
            Block block = (Block) (Object) this;
            if (block.equals(Blocks.BUDDING_AMETHYST)) {
                cir.setReturnValue(1200f);
            }
        }
    }
}
