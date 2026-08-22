package net.mokugyo.smartchest.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SmartChestSort {

    private SmartChestSort() {}

    public static void sortPage(IItemHandlerModifiable inventory, int page) {
        int startSlot = page * SmartChestBlockEntity.PAGE_SIZE;
        int slotCount = SmartChestBlockEntity.PAGE_SIZE;

        List<ItemStack> items = new ArrayList<>(slotCount);

        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = inventory.getStackInSlot(startSlot + i);
            if (!stack.isEmpty()) {
                items.add(stack.copy());
                inventory.setStackInSlot(startSlot + i, ItemStack.EMPTY);
            }
        }

        List<ItemStack> merged = mergeStacks(items);

        merged.sort(
                Comparator.comparing((ItemStack stack) -> stack.getItem().builtInRegistryHolder().key().location().getNamespace())
                        .thenComparing(stack -> stack.getItem().builtInRegistryHolder().key().location().getPath())
                        .thenComparing(stack -> {
                            var name = stack.get(DataComponents.CUSTOM_NAME);
                            return name != null ? name.getString() : "";
                        })
                        .thenComparing(stack -> stack.getOrDefault(DataComponents.DAMAGE, 0))
                        .thenComparing(stack -> stack.has(DataComponents.ENCHANTMENTS) ? 0 : 1)
                        .thenComparing(stack -> -stack.getCount())
        );

        for (int i = 0; i < merged.size() && i < slotCount; i++) {
            inventory.setStackInSlot(startSlot + i, merged.get(i));
        }
    }

    private static List<ItemStack> mergeStacks(List<ItemStack> items) {
        List<ItemStack> merged = new ArrayList<>(items.size());

        outer:
        for (ItemStack stack : items) {
            for (ItemStack existing : merged) {
                if (ItemStack.isSameItemSameComponents(existing, stack)) {
                    int space = existing.getMaxStackSize() - existing.getCount();
                    if (space > 0) {
                        int transfer = Math.min(space, stack.getCount());
                        existing.grow(transfer);
                        stack.shrink(transfer);
                        if (stack.isEmpty()) {
                            continue outer;
                        }
                    }
                }
            }
            if (!stack.isEmpty()) {
                merged.add(stack);
            }
        }

        return merged;
    }
}
