package net.mokugyo.smartchest.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.mokugyo.smartchest.registry.ModMenus;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.minecraft.world.SimpleContainer;

import java.util.ArrayList;
import java.util.List;

public class SmartChestMenu extends AbstractContainerMenu {

    private final SmartChestBlockEntity blockEntity;
    private final List<SmartChestSlot> chestSlots = new ArrayList<>();
    private SmartChestSlot iconSlot;
    private int currentPage = 0;
    private String filterQuery = "";

    public SmartChestMenu(int containerId, Inventory playerInventory, SmartChestBlockEntity blockEntity) {
        super(ModMenus.SMART_CHEST_MENU.get(), containerId);
        this.blockEntity = blockEntity;

        if (blockEntity != null) {
            this.currentPage = blockEntity.getLastOpenedPage();
        }

        setupSlots(playerInventory);

        // ★ ここにあった手動の blockEntity.startOpen(...) は削除しました。
        // （Minecraftの openMenu システムが自動的に開始カウントを管理するため不要です）
    }

    public SmartChestBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public boolean isFiltering() {
        return this.filterQuery != null && !this.filterQuery.isBlank();
    }

    public void setFilterQuery(String query) {
        this.filterQuery = query;
    }

    private void setupSlots(Inventory playerInventory) {
        this.slots.clear();
        this.chestSlots.clear();

        if (blockEntity != null) {
            var handler = blockEntity.getInventory();

            // 0～53: メインチェストスロット (9x6)
            for (int row = 0; row < 6; ++row) {
                for (int col = 0; col < 9; ++col) {
                    int slotIndex = col + row * 9;
                    SmartChestSlot slot = new SmartChestSlot(this, handler, slotIndex, 8 + col * 18, 18 + row * 18);
                    this.chestSlots.add(slot);
                    this.addSlot(slot);
                }
            }

            // 54番スロット（アイコン専用）
            this.iconSlot = new SmartChestSlot(this, blockEntity.getIconInventory(), currentPage, 178, -2) {
                @Override
                public int getMaxStackSize() { return 1; }
                @Override
                public int getMaxStackSize(ItemStack stack) { return 1; }
            };
            this.addSlot(this.iconSlot);
        }

        // プレイヤーインベントリ (3x9 + ホットバー9)
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
        if (page < 0 || page >= 10) return;
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
        if (blockEntity == null) return;
        int startIndex = currentPage * 54;

        for (int i = 0; i < 54; i++) {
            if (i < this.chestSlots.size()) {
                this.chestSlots.get(i).setTargetIndex(startIndex + i, true);
            }
        }

        if (this.iconSlot != null) {
            this.iconSlot.setTargetIndex(currentPage, true);
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

            if (index < 54) {
                if (!this.moveItemStackTo(slotStack, 55, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 54) {
                if (!this.moveItemStackTo(slotStack, 55, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.isFiltering()) {
                    return ItemStack.EMPTY;
                }
                if (!this.moveItemStackTo(slotStack, 0, 54, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // ★ バニラのChestBlockEntityは親クラスのクローズ処理で管理されますが、
        // もし必要であればここで安全に stopOpen を呼び出します（サーバー側のみ）
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
        return true;
    }
}