package me.dave.itempools.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleItemMeta {
    private final String displayName;
    private final List<String> lore;
    private final Integer customModelData;
    private final Boolean enchanted;

    public SimpleItemMeta(@Nullable String displayName, @Nullable List<String> lore, @Nullable Integer customModelData, @Nullable Boolean enchanted) {
        this.displayName = displayName;
        this.lore = lore;
        this.customModelData = customModelData;
        this.enchanted = enchanted;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean isSimilar(ItemMeta itemMeta) {
        if (displayName != null && !itemMeta.getDisplayName().equals(displayName)) {
            return false;
        }

        if (lore != null && (itemMeta.getLore() == null || !itemMeta.getLore().equals(lore))) {
            return false;
        }

        if (customModelData != null && itemMeta.getCustomModelData() != customModelData) {
            return false;
        }

        if (enchanted != null && itemMeta.hasEnchants() != enchanted) {
            return false;
        }

        return true;
    }

    public static SimpleItemMeta create(ConfigurationSection configurationSection) {
        String displayName = configurationSection.getString("display-name");
        List<String> lore = configurationSection.contains("lore") ? configurationSection.getStringList("lore") : null;
        Integer customModelData = configurationSection.contains("custom-model-data") ? configurationSection.getInt("custom-model-data") : null;
        Boolean enchanted = configurationSection.contains("enchanted") ? configurationSection.getBoolean("enchanted") : null;

        return new SimpleItemMeta(displayName, lore, customModelData, enchanted);
    }
}
