package net.mokugyo.smartchest.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SmartChestSort {

    private record SortKey(ItemStack stack, String namespace, String path, String customName, int damage, int unenchanted, int negCount) {}

    private static final Comparator<SortKey> SORT_ORDER = Comparator
            .comparing(SortKey::namespace)
            .thenComparing(SortKey::path)
            .thenComparing(SortKey::customName)
            .thenComparingInt(SortKey::damage)
            .thenComparingInt(SortKey::unenchanted)
            .thenComparingInt(SortKey::negCount);

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
        List<SortKey> keyed = new ArrayList<>(merged.size());
        for (ItemStack stack : merged) {
            var location = stack.getItem().builtInRegistryHolder().key().location();
            var name = stack.get(DataComponents.CUSTOM_NAME);
            keyed.add(new SortKey(
                    stack,
                    location.getNamespace(),
                    location.getPath(),
                    name != null ? name.getString() : "",
                    stack.getOrDefault(DataComponents.DAMAGE, 0),
                    stack.has(DataComponents.ENCHANTMENTS) ? 0 : 1,
                    -stack.getCount()
            ));
        }
        keyed.sort(SORT_ORDER);

        for (int i = 0; i < keyed.size() && i < slotCount; i++) {
            inventory.setStackInSlot(startSlot + i, keyed.get(i).stack());
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
