package net.mokugyo.smartchest.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SmartChestSlot extends SlotItemHandler {

    private final SmartChestMenu menu;
    private int targetIndex;
    private boolean activeSlot = true;

    public SmartChestSlot(SmartChestMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
        this.targetIndex = index;
    }

    public void setTargetIndex(int targetIndex, boolean active) {
        this.targetIndex = targetIndex;
        this.activeSlot = active;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (this.menu != null && this.menu.isFiltering()) {
            return false;
        }
        return super.mayPlace(stack);
    }

    @Override
    public ItemStack getItem() {
        if (!activeSlot) return ItemStack.EMPTY;
        return this.getItemHandler().getStackInSlot(this.targetIndex);
    }

    @Override
    public void set(ItemStack stack) {
        if (!activeSlot) return;
        if (this.getItemHandler() instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(this.targetIndex, stack);
        }
    }

    @Override
    public void setByPlayer(ItemStack stack) {
        if (!activeSlot) return;
        if (this.getItemHandler() instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(this.targetIndex, stack);
        } else {
            this.set(stack);
        }
        this.setChanged();
    }

    @Override
    public ItemStack remove(int amount) {
        if (!activeSlot) return ItemStack.EMPTY;
        return this.getItemHandler().extractItem(this.targetIndex, amount, false);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        if (!activeSlot) return false;
        return !this.getItemHandler().extractItem(this.targetIndex, 1, true).isEmpty();
    }
}