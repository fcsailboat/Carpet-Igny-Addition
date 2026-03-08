package com.liuyue.igny.mixins.rule.looseEnderPearlChunkLoading;

import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {
    @ModifyConstant(
            method = "tick",
            constant = @Constant(longValue = 0L)
    )
    private long ticketRefresh(long constant) {
        return 5L;
    }
}
