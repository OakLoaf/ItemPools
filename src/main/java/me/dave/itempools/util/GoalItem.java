package me.dave.itempools.util;

import me.dave.platyutils.utils.StringUtils;
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

    @Nullable
    public static GoalItem create(ConfigurationSection configurationSection) {
        Material material = StringUtils.getEnum(configurationSection.getName(), Material.class).orElse(null);
        if (material == null) {
            return null;
        }

        ConfigurationSection metaSection = configurationSection.getConfigurationSection("meta");
        if (metaSection == null) {
            return new GoalItem(material, null, null, null, null);
        }

        String displayName = metaSection.contains("display-name") ? metaSection.getString("display-name") : null;
        List<String> lore = metaSection.contains("lore") ? configurationSection.getStringList("lore") : null;
        Integer customModelData = metaSection.contains("custom-model-data") ? configurationSection.getInt("custom-model-data") : null;
        Boolean enchanted = metaSection.contains("enchanted") ? configurationSection.getBoolean("enchanted") : null;

        return new GoalItem(material, displayName, lore, customModelData, enchanted);
    }
}
