package com.liuyue.igny.utils;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class RuleUtils {
    //#if MC >= 12005
    public static Boolean canSoundSuppression(String name) {
        if ("false".equalsIgnoreCase(IGNYSettings.simpleSoundSuppression)) {
            return false;
        }
        if (name == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(IGNYSettings.simpleSoundSuppression)) {
            return "声音抑制器".equals(name) || "soundSuppression".equalsIgnoreCase(name);
        }

        return Objects.equals(IGNYSettings.simpleSoundSuppression.toLowerCase(), name.toLowerCase());
    }
    //#endif

    public static Object getCarpetRulesValue(String modId, String ruleName) {
        if(IGNYServerMod.CARPET_ADDITION_MOD_IDS.contains(modId)){
            CarpetRule<?> carpetRule = CarpetServer.settingsManager.getCarpetRule(ruleName);
            if (carpetRule == null) {
                return false;
            }
            return carpetRule.value() == null ? false : carpetRule.value();
        }
        return false;
    }

    public static String getModIdFromClass(Class<?> clazz) {
        try {
            URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
            Path classPath = Path.of(location.toURI());

            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                for (Path path : mod.getOrigin().getPaths()) {
                    String id = mod.getMetadata().getId();
                    String classFileName = classPath.getFileName().toString();
                    if (path.getFileName().toString().equals(classFileName) || classFileName.contains(id)) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return "unknown";
    }
}
