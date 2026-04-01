package com.liuyue.igny.mixins.easterEgg;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
//#if MC > 11904
import net.minecraft.client.gui.components.SplashRenderer;
//#endif
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
    @Shadow
    @Final
    private List<String> splashes;

    @WrapMethod(method = "getSplash")
    //#if MC > 11904
    private SplashRenderer getSplash(Operation<SplashRenderer> original)
    //#else
    //$$ private String getSplash(Operation<String> original)
    //#endif
    {
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected();
        List<String> splashes = List.copyOf(this.splashes);
        if (currentLang.contains("zh")) {
            this.splashes.add("关注六月谢谢喵！！");
        } else {
            this.splashes.add("Follow Liuyue_awa!!");
        }
        //#if MC > 11904
        SplashRenderer result = original.call();
        //#else
        //$$ String result = original.call();
        //#endif
        this.splashes.clear();
        this.splashes.addAll(splashes);
        return result;
    }
}
