package org.lushplugins.itempools.goal;

import org.lushplugins.lushlib.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoalItem {
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final Integer customModelData;
    private final Boolean enchanted;

    public GoalItem(@NotNull Material material, @Nullable String displayName, @Nullable List<String> lore, @Nullable Integer customModelData, @Nullable Boolean enchanted) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.customModelData = customModelData;
        this.enchanted = enchanted;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean isValid(ItemStack item) {
        if (!item.getType().equals(material)) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (displayName != null && (itemMeta == null || !itemMeta.getDisplayName().equals(displayName))) {
            return false;
        }

        if (lore != null && (itemMeta == null || itemMeta.getLore() == null || !itemMeta.getLore().equals(lore))) {
            return false;
        }

        if (customModelData != null && (itemMeta == null || itemMeta.getCustomModelData() != customModelData)) {
            return false;
        }

        if (enchanted != null && (itemMeta == null || itemMeta.hasEnchants() != enchanted)) {
            return false;
        }

        return true;
    }

    public void save(ConfigurationSection configurationSection) {
        configurationSection.set("type", material.getKey().toString());
        configurationSection.set("meta.display-name", displayName);
        configurationSection.set("meta.lore", lore);
        configurationSection.set("meta.custom-model-data", customModelData);
        configurationSection.set("meta.enchanted", enchanted);
    }

    @Nullable
    public static GoalItem create(ConfigurationSection configurationSection) {
        String materialRaw = configurationSection.contains("type") ? configurationSection.getString("type") : configurationSection.getName();
        Material material = StringUtils.getEnum(materialRaw, Material.class).orElse(null);
        if (material == null) {
            return null;
        }

        ConfigurationSection metaSection = configurationSection.getConfigurationSection("meta");
        if (metaSection == null) {
            return new GoalItem(material, null, null, null, null);
        }

        String displayName = metaSection.contains("display-name") ? metaSection.getString("display-name") : null;
        List<String> lore = metaSection.contains("lore") ? metaSection.getStringList("lore") : null;
        Integer customModelData = metaSection.contains("custom-model-data") ? metaSection.getInt("custom-model-data") : null;
        Boolean enchanted = metaSection.contains("enchanted") ? metaSection.getBoolean("enchanted") : null;

        return new GoalItem(material, displayName, lore, customModelData, enchanted);
    }
}
