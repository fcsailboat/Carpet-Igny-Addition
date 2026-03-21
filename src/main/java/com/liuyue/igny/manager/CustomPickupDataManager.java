package com.liuyue.igny.manager;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomPickupDataManager extends BaseDataManager<Map<String, CustomPickupDataManager.PlayerSetting>> {
    public static final CustomPickupDataManager INSTANCE = new CustomPickupDataManager();
    private final Map<String, PlayerSetting> settings = new ConcurrentHashMap<>();

    @Override protected String getFileName() { return "custom_player_pickup.json"; }
    @Override protected Type getDataType() { return new TypeToken<Map<String, PlayerSetting>>(){}.getType(); }
    @Override public Map<String, PlayerSetting> getDefaultData() { return new HashMap<>(); }

    @Override protected void applyData(Map<String, PlayerSetting> data) {
        settings.clear();
        settings.putAll(data);
    }

    @Override public Map<String, PlayerSetting> getCurrentData() { return settings; }

    public PlayerSetting getOrCreate(String playerName) {
        return settings.computeIfAbsent(playerName.toLowerCase(), k -> new PlayerSetting());
    }

    public void updateAndSave(String playerName, PlayerSetting setting) {
        settings.put(playerName.toLowerCase(), setting);
        this.save();
    }

    public boolean canPickUp(String playerName, String itemId) {
        PlayerSetting setting = settings.get(playerName.toLowerCase());
        return setting == null || setting.canPickUp(itemId);
    }

    public enum Mode {
        @SerializedName("disabled") DISABLED,
        @SerializedName("whitelist") WHITELIST,
        @SerializedName("blacklist") BLACKLIST
    }

    public static class PlayerSetting {
        private Mode mode = Mode.DISABLED;
        private Set<String> items = new HashSet<>();

        public Mode getMode() { return mode; }
        public void setMode(Mode mode) { this.mode = mode; }
        public void setItems(Collection<String> newItems) { this.items = new HashSet<>(newItems); }
        public Set<String> getItems() { return items; }

        public boolean canPickUp(String itemId) {
            if (mode == Mode.DISABLED) return true;
            boolean contains = items.contains(itemId.toLowerCase());
            return (mode == Mode.WHITELIST) == contains;
        }
    }
}