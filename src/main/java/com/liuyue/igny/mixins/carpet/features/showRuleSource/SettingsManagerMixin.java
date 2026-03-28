package com.liuyue.igny.mixins.carpet.features.showRuleSource;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.ClassUtil;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(SettingsManager.class)
public class SettingsManagerMixin {
    @Inject(method = "parseSettingsClass", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static void parseSettingsClass(Class<?> settingsClass, CallbackInfo ci, @Local(name = "parsed") CarpetRule<?> rule) {
        ClassUtil.getModIdFromClass(settingsClass, modId ->
                IGNYSettings.modRuleTree
                .computeIfAbsent(modId, k -> new ArrayList<>())
                .add(rule.name())
        );
    }
}
