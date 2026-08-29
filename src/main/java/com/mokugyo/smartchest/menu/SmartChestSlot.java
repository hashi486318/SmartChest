package com.mokugyo.smartchest.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class SmartChestSlot extends SlotItemHandler {

    private int targetIndex;

    public SmartChestSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.targetIndex = index;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public ItemStack getItem() {
        return this.getItemHandler().getStackInSlot(this.targetIndex);
    }

    @Override
    public void set(ItemStack stack) {
        if (this.getItemHandler() instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(this.targetIndex, stack);
        }
        this.setChanged();
    }

    @Override
    public ItemStack remove(int amount) {
        return this.getItemHandler().extractItem(this.targetIndex, amount, false);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !this.getItemHandler().extractItem(this.targetIndex, 1, true).isEmpty();
    }
}
