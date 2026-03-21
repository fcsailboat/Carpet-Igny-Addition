package com.liuyue.igny.manager;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RuleChangeDataManager extends BaseDataManager<Map<String, List<RuleChangeDataManager.RuleChangeRecord>>> {
    public static final RuleChangeDataManager INSTANCE = new RuleChangeDataManager();
    private final Map<String, List<RuleChangeRecord>> inMemoryCache = new ConcurrentHashMap<>();

    @Override protected String getFileName() { return "rule_changes.json"; }
    @Override protected Type getDataType() { return new TypeToken<Map<String, List<RuleChangeRecord>>>(){}.getType(); }
    @Override public Map<String, List<RuleChangeRecord>> getDefaultData() { return new HashMap<>(); }

    @Override protected void applyData(Map<String, List<RuleChangeRecord>> data) {
        inMemoryCache.clear();
        inMemoryCache.putAll(data);
    }

    @Override public Map<String, List<RuleChangeRecord>> getCurrentData() { return inMemoryCache; }

    public void recordRuleChange(String ruleName, Object originalValue, String userInput, String sourceName, long timestamp) {
        RuleChangeRecord record = new RuleChangeRecord(originalValue, userInput, sourceName, timestamp);
        List<RuleChangeRecord> history = inMemoryCache.computeIfAbsent(ruleName, k -> new ArrayList<>());
        history.add(record);
        if (history.size() > 3) history.removeFirst();
        save();
    }

    public List<RuleChangeRecord> getLastChange(String ruleName) {
        return new ArrayList<>(inMemoryCache.getOrDefault(ruleName, Collections.emptyList()));
    }

    public record RuleChangeRecord(Object rawValue, Object userInput, String sourceName, long timestamp) {
        public String getFormattedTime() {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
        }

        public boolean isValid() {
            return rawValue != null && userInput != null && sourceName != null && !sourceName.isEmpty();
        }
    }
}