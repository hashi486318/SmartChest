package net.mokugyo.smartchest.blockentity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * Presents one "page" (a fixed-size window) of a larger backing
 * {@link IItemHandlerModifiable} as a smaller handler with local slot
 * indices 0..pageSize-1.
 *
 * The GUI only ever talks to this class, which means the same set of
 * {@code Slot} objects can be reused for every page — switching pages is
 * just changing {@link #setPage(int)} and re-broadcasting slot contents.
 */
public class PagedItemHandler implements IItemHandlerModifiable {

    private final IItemHandlerModifiable backing;
    private final int pageSize;
    private final int pageCount;
    private int page;

    public PagedItemHandler(IItemHandlerModifiable backing, int pageSize, int pageCount) {
        this.backing = backing;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.page = 0;
    }

    public int getPage() {
        return page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPage(int page) {
        this.page = Math.floorMod(page, pageCount);
    }

    public void nextPage() {
        setPage(page + 1);
    }

    public void previousPage() {
        setPage(page - 1);
    }

    private int toAbsolute(int localSlot) {
        return page * pageSize + localSlot;
    }

    @Override
    public int getSlots() {
        return pageSize;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return backing.getStackInSlot(toAbsolute(slot));
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return backing.insertItem(toAbsolute(slot), stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return backing.extractItem(toAbsolute(slot), amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return backing.getSlotLimit(toAbsolute(slot));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return backing.isItemValid(toAbsolute(slot), stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        backing.setStackInSlot(toAbsolute(slot), stack);
    }
}
