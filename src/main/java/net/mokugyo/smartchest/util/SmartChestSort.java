package net.mokugyo.smartchest.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SmartChestSort {

    /**
     * 指定されたページ（54スロット分）のみを有名Mod風のルールで並べ替える
     */
    public static void sortPage(IItemHandlerModifiable inventory, int page) {
        int startSlot = page * 54;
        int slotCount = 54;

        List<ItemStack> items = new ArrayList<>();

        // 1. 指定ページのアイテムを収集してクリア
        for (int i = 0; i < slotCount; i++) {
            int slot = startSlot + i;
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                items.add(stack.copy());
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }

        // 2. スタックの結合（同じアイテムを同一スタックにまとめる）
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack stack : items) {
            boolean added = false;
            for (ItemStack existing : merged) {
                if (ItemStack.isSameItemSameComponents(existing, stack)) {
                    int maxCount = existing.getMaxStackSize();
                    int space = maxCount - existing.getCount();
                    if (space > 0) {
                        int transfer = Math.min(space, stack.getCount());
                        existing.grow(transfer);
                        stack.shrink(transfer);
                        if (stack.isEmpty()) {
                            added = true;
                            break;
                        }
                    }
                }
            }
            if (!added && !stack.isEmpty()) {
                merged.add(stack);
            }
        }

        // 3. 有名Mod風の多段階ソートロジック
        merged.sort(
                // ① Mod ID順
                Comparator.comparing((ItemStack s) -> s.getItem().builtInRegistryHolder().key().location().getNamespace())
                        // ② アイテムのレジストリ名（パス）順
                        .thenComparing(s -> s.getItem().builtInRegistryHolder().key().location().getPath())
                        // ③ カスタム名（金床などで付けられた名前）
                        .thenComparing(s -> {
                            var name = s.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
                            return name != null ? name.getString() : "";
                        })
                        // ④ ダメージ値（耐久値の減り具合）- コンポーネント経由で安全に取得
                        .thenComparing(s -> s.getOrDefault(net.minecraft.core.component.DataComponents.DAMAGE, 0))
                        // ⑤ エンチャントの有無（エンチャント付きかどうか）
                        .thenComparing(s -> s.has(net.minecraft.core.component.DataComponents.ENCHANTMENTS) ? 0 : 1)
                        // ⑥ 最後にスタック数が多い順
                        .thenComparing(s -> -s.getCount())
        );

        // 4. ページ内のスロットへ綺麗に詰め直す
        for (int i = 0; i < merged.size() && i < slotCount; i++) {
            inventory.setStackInSlot(startSlot + i, merged.get(i));
        }
    }
}