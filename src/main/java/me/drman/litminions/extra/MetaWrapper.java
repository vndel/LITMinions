package me.drman.litminions.extra;

import org.bukkit.inventory.ItemFlag;

import java.util.*;

public class MetaWrapper {
    private Map<String, Integer> enchants;
    private Map<String, Object> nbtMap;
    private Set<ItemFlag> itemFlags;
    private String displayName;
    private List<String> lore;
    private Boolean unbreakable;

    public void addEnchant(String enchant, int level) {
        if (this.enchants == null) {
            this.enchants = new HashMap();
        }

        this.enchants.put(enchant, level);
    }

    public void addItemFlags(ItemFlag... itemFlags) {
        if (this.itemFlags == null) {
            this.itemFlags = new HashSet();
        }

        this.itemFlags.addAll(Arrays.asList(itemFlags));
    }

    public boolean isUnbreakable() {
        return this.unbreakable != null && this.unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public static MetaWrapper.MetaWrapperBuilder builder() {
        return new MetaWrapper.MetaWrapperBuilder();
    }

    public MetaWrapper() {
    }

    public MetaWrapper(Map<String, Integer> enchants, Map<String, Object> nbtMap, Set<ItemFlag> itemFlags, String displayName, List<String> lore, Boolean unbreakable) {
        this.enchants = enchants;
        this.nbtMap = nbtMap;
        this.itemFlags = itemFlags;
        this.displayName = displayName;
        this.lore = lore;
        this.unbreakable = unbreakable;
    }

    public Map<String, Integer> getEnchants() {
        return this.enchants;
    }

    public void setEnchants(Map<String, Integer> enchants) {
        this.enchants = enchants;
    }

    public Map<String, Object> getNbtMap() {
        return this.nbtMap;
    }

    public Set<ItemFlag> getItemFlags() {
        return this.itemFlags;
    }

    public void setItemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public static class MetaWrapperBuilder {
        private Map<String, Integer> enchants;
        private Map<String, Object> nbtMap;
        private Set<ItemFlag> itemFlags;
        private String displayName;
        private List<String> lore;
        private Boolean unbreakable;

        MetaWrapperBuilder() {
        }

        public MetaWrapper.MetaWrapperBuilder enchants(Map<String, Integer> enchants) {
            this.enchants = enchants;
            return this;
        }

        public MetaWrapper.MetaWrapperBuilder nbtMap(Map<String, Object> nbtMap) {
            this.nbtMap = nbtMap;
            return this;
        }

        public MetaWrapper.MetaWrapperBuilder itemFlags(Set<ItemFlag> itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        public MetaWrapper.MetaWrapperBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public MetaWrapper.MetaWrapperBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public MetaWrapper.MetaWrapperBuilder unbreakable(Boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        public MetaWrapper build() {
            return new MetaWrapper(this.enchants, this.nbtMap, this.itemFlags, this.displayName, this.lore, this.unbreakable);
        }

        public String toString() {
            return "MetaWrapper.MetaWrapperBuilder(enchants=" + this.enchants + ", nbtMap=" + this.nbtMap + ", itemFlags=" + this.itemFlags + ", displayName=" + this.displayName + ", lore=" + this.lore + ", unbreakable=" + this.unbreakable + ")";
        }
    }
}
