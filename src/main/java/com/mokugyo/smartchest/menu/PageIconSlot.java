package com.mokugyo.smartchest.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Page-icon slot. {@code container} is the player inventory so Inventory Profiles Next
 * 1.10.x treats this as a player-side slot and skips it when sorting the chest.
 * Item access is redirected to {@code iconHandler}; the player inventory is never read or written.
 */
public class PageIconSlot extends Slot {

    private static final int UNUSED_PLAYER_SLOT_INDEX = 999;

    private final IItemHandler iconHandler;
    private int targetIndex;

    public PageIconSlot(Inventory playerInventory, IItemHandler iconHandler, int targetIndex, int x, int y) {
        super(playerInventory, UNUSED_PLAYER_SLOT_INDEX, x, y);
        this.iconHandler = iconHandler;
        this.targetIndex = targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public ItemStack getItem() {
        return this.iconHandler.getStackInSlot(this.targetIndex);
    }

    @Override
    public void set(ItemStack stack) {
        if (this.iconHandler instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(this.targetIndex, stack);
        }
        this.setChanged();
    }

    @Override
    public void setChanged() {
        // Skip super: vanilla would mark the player inventory dirty.
    }

    @Override
    public ItemStack remove(int amount) {
        return this.iconHandler.extractItem(this.targetIndex, amount, false);
    }

    @Override
    public boolean mayPickup(Player player) {
        return !this.iconHandler.extractItem(this.targetIndex, 1, true).isEmpty();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.iconHandler.isItemValid(this.targetIndex, stack);
    }

    @Override
    public int getMaxStackSize() {
        return this.iconHandler.getSlotLimit(this.targetIndex);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
    }
}
