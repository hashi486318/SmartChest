package com.mokugyo.smartchest.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import com.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import com.mokugyo.smartchest.registry.ModMenus;

import java.util.ArrayList;
import java.util.List;

public class SmartChestMenu extends AbstractContainerMenu {

    private static final int CHEST_SLOT_COUNT = SmartChestBlockEntity.PAGE_SIZE;
    private static final int ICON_SLOT_INDEX = CHEST_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_START = ICON_SLOT_INDEX + 1;

    private final SmartChestBlockEntity blockEntity;
    private final List<SmartChestSlot> chestSlots = new ArrayList<>();
    private SmartChestSlot iconSlot;
    private int currentPage = 0;

    public SmartChestMenu(int containerId, Inventory playerInventory, SmartChestBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity,
                blockEntity != null ? blockEntity.getLastOpenedPage() : 0);
    }

    public SmartChestMenu(int containerId, Inventory playerInventory, SmartChestBlockEntity blockEntity, int initialPage) {
        super(ModMenus.SMART_CHEST_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        if (initialPage >= 0 && initialPage < SmartChestBlockEntity.PAGE_COUNT) {
            this.currentPage = initialPage;
        } else if (blockEntity != null) {
            this.currentPage = blockEntity.getLastOpenedPage();
        }
        setupSlots(playerInventory);
    }

    public SmartChestBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    private void setupSlots(Inventory playerInventory) {
        this.slots.clear();
        this.chestSlots.clear();

        if (blockEntity != null) {
            var handler = blockEntity.getInventory();

            // Slots 0-53: main chest grid (9x6)
            for (int row = 0; row < 6; ++row) {
                for (int col = 0; col < 9; ++col) {
                    int slotIndex = col + row * 9;
                    SmartChestSlot slot = new SmartChestSlot(handler, slotIndex, 8 + col * 18, 18 + row * 18);
                    this.chestSlots.add(slot);
                    this.addSlot(slot);
                }
            }

            // Slot 54: page icon
            this.iconSlot = new SmartChestSlot(blockEntity.getIconInventory(), currentPage, 178, -2);
            this.addSlot(this.iconSlot);
        }

        // Player inventory (3x9 + hotbar)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }

        updateSlotIndices();
    }

    public void setPage(int page) {
        if (page < 0 || page >= SmartChestBlockEntity.PAGE_COUNT) {
            return;
        }
        this.currentPage = page;

        if (this.blockEntity != null) {
            this.blockEntity.setLastOpenedPage(page);
        }

        updateSlotIndices();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private void updateSlotIndices() {
        if (blockEntity == null) {
            return;
        }

        int startIndex = currentPage * CHEST_SLOT_COUNT;

        for (int i = 0; i < CHEST_SLOT_COUNT; i++) {
            this.chestSlots.get(i).setTargetIndex(startIndex + i);
        }

        if (this.iconSlot != null) {
            this.iconSlot.setTargetIndex(currentPage);
        }

        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index <= ICON_SLOT_INDEX) {
                if (!this.moveItemStackTo(slotStack, PLAYER_INVENTORY_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, ICON_SLOT_INDEX, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot != this.iconSlot && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (blockEntity != null && blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            blockEntity.stopOpen(player);
            BlockPos pos = blockEntity.getBlockPos();
            player.level().playSound(
                    null,
                    pos,
                    SoundEvents.CHEST_CLOSE,
                    SoundSource.BLOCKS,
                    0.5F,
                    1.0F
            );
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && !blockEntity.isRemoved()
                && Container.stillValidBlockEntity(blockEntity, player);
    }
}
