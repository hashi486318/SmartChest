package net.mokugyo.smartchest.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class SortRules {

    private static final Map<String, Comparator<ItemStack>> RULES = new HashMap<>();

    static {
        // --- Independent basic rules / element comparisons (::rule) ---

        // custom_name: Uses the CUSTOM_NAME component
        Comparator<ItemStack> customNameComp = Comparator.comparing(
                (ItemStack s) -> {
                    var name = s.get(DataComponents.CUSTOM_NAME);
                    return name != null ? name.getString() : "";
                },
                Comparator.nullsFirst(String::compareTo)
        );

        // creative_menu_group_index: Based on registry order as a proxy for creative tab order
        Comparator<ItemStack> creativeMenuGroupIndexComp = Comparator.comparingInt(
                s -> BuiltInRegistries.ITEM.getId(s.getItem())
        );

        // raw_id: Item ResourceLocation (namespace:path)
        Comparator<ItemStack> rawIdComp = Comparator.comparing(
                s -> BuiltInRegistries.ITEM.getKey(s.getItem()).toString()
        );

        // item_name: Hover name or item name
        Comparator<ItemStack> itemNameComp = Comparator.comparing(
                s -> s.getHoverName().getString()
        );

        // item_id: Item ID string
        Comparator<ItemStack> itemIdComp = Comparator.comparing(
                s -> BuiltInRegistries.ITEM.getKey(s.getItem()).toString()
        );

        // enchantments_score: Score derived from enchantments (e.g., sum of enchantment levels)
        Comparator<ItemStack> enchantmentsScoreComp = Comparator.comparingInt(
                (ItemStack s) -> {
                    var enchants = s.get(DataComponents.ENCHANTMENTS);
                    if (enchants == null) return 0;
                    int score = 0;
                    for (var entry : enchants.entrySet()) {
                        score += entry.getIntValue();
                    }
                    return score;
                }
        ).reversed(); // Higher scores first if required

        // damage: Uses the DAMAGE component
        Comparator<ItemStack> damageComp = Comparator.comparingInt(
                s -> s.getOrDefault(DataComponents.DAMAGE, 0)
        );

        // display_name: Item display name
        Comparator<ItemStack> displayNameComp = Comparator.comparing(
                s -> s.getHoverName().getString()
        );

        // potion_effect: Potion effect information
        Comparator<ItemStack> potionEffectComp = Comparator.comparing(
                (ItemStack s) -> {
                    var potions = s.get(DataComponents.POTION_CONTENTS);
                    if (potions == null) return "";
                    return potions.toString();
                },
                Comparator.nullsFirst(String::compareTo)
        );

        // nbt_comparator: Final fallback comparison for remaining NBT / DataComponent differences
        Comparator<ItemStack> nbtComparatorComp = Comparator.comparing(
                s -> s.toString()
        );

        // --- Composite group rules (@rule) ---

        // @default_nbt_rule
        Comparator<ItemStack> defaultNbtRule = enchantmentsScoreComp
                .thenComparing(damageComp)
                .thenComparing(displayNameComp)
                .thenComparing(potionEffectComp)
                .thenComparing(nbtComparatorComp);

        // @creative_menu_order
        Comparator<ItemStack> creativeMenuOrder = customNameComp
                .thenComparing(creativeMenuGroupIndexComp)
                .thenComparing(rawIdComp)
                .thenComparing(defaultNbtRule);

        // Register rules
        RULES.put("default", creativeMenuOrder); // Treat @default as @creative_menu_order
        RULES.put("creative_menu_order", creativeMenuOrder);
        RULES.put("item_name", itemNameComp.thenComparing(defaultNbtRule));
        RULES.put("item_id", itemIdComp.thenComparing(defaultNbtRule));
        RULES.put("raw_id", rawIdComp.thenComparing(defaultNbtRule));
        RULES.put("default_nbt_rule", defaultNbtRule);
        RULES.put("auto_refill_best", defaultNbtRule);
        RULES.put("accumulated_count_descending", (s1, s2) -> Integer.compare(s2.getCount(), s1.getCount()));
        RULES.put("accumulated_count_ascending", Comparator.comparingInt(ItemStack::getCount));
    }

    private SortRules() {}

    public static Comparator<ItemStack> getRule(String name) {
        return RULES.getOrDefault(name, RULES.get("default"));
    }
}