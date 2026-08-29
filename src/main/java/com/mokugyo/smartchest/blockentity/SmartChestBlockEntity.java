package com.mokugyo.smartchest.blockentity;

import com.mokugyo.smartchest.menu.SmartChestMenu;
import com.mokugyo.smartchest.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class SmartChestBlockEntity extends BlockEntity implements MenuProvider {

    public static final int PAGE_SIZE = 54;
    public static final int PAGE_COUNT = 10;
    public static final int TOTAL_SLOTS = PAGE_SIZE * PAGE_COUNT;

    private static final byte SYNC_OPEN_COUNT = 1;
    private static final byte SYNC_PAGE_ICONS = 2;
    private static final byte SYNC_ALL = SYNC_OPEN_COUNT | SYNC_PAGE_ICONS;

    private final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler iconInventory = new ItemStackHandler(PAGE_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            syncToClient(SYNC_PAGE_ICONS);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private int lastOpenedPage = 0;
    private byte clientSyncMask = SYNC_ALL;

    public float lidAngle;
    public float oLidAngle;
    public int openCount;

    public SmartChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMART_CHEST_BLOCKENTITY.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStackHandler getIconInventory() {
        return this.iconInventory;
    }

    public ItemStack getPageIcon(int page) {
        if (page < 0 || page >= PAGE_COUNT) {
            return ItemStack.EMPTY;
        }
        return this.iconInventory.getStackInSlot(page);
    }

    public int getLastOpenedPage() {
        return this.lastOpenedPage;
    }

    public void setLastOpenedPage(int page) {
        if (page >= 0 && page < PAGE_COUNT) {
            this.lastOpenedPage = page;
            this.setChanged();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, SmartChestBlockEntity blockEntity) {
        if ((blockEntity.openCount == 0 && blockEntity.lidAngle == 0.0F)
                || (blockEntity.openCount > 0 && blockEntity.lidAngle == 1.0F)) {
            blockEntity.oLidAngle = blockEntity.lidAngle;
            return;
        }

        blockEntity.oLidAngle = blockEntity.lidAngle;
        if (blockEntity.openCount > 0) {
            blockEntity.lidAngle = Math.min(1.0F, blockEntity.lidAngle + 0.1F);
        } else {
            blockEntity.lidAngle = Math.max(0.0F, blockEntity.lidAngle - 0.1F);
        }
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openCount++;
            this.setChanged();
            syncToClient(SYNC_OPEN_COUNT);
        }
    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openCount = Math.max(0, this.openCount - 1);
            this.setChanged();
            syncToClient(SYNC_OPEN_COUNT);
        }
    }

    public void dropAllContents() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        double x = this.worldPosition.getX() + 0.5D;
        double y = this.worldPosition.getY() + 0.5D;
        double z = this.worldPosition.getZ() + 0.5D;

        for (int i = 0; i < this.inventory.getSlots(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(this.level, x, y, z, stack.copy());
                this.inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        for (int i = 0; i < this.iconInventory.getSlots(); i++) {
            ItemStack icon = this.iconInventory.getStackInSlot(i);
            if (!icon.isEmpty()) {
                Containers.dropItemStack(this.level, x, y, z, icon.copy());
                this.iconInventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("PageIcons", iconInventory.serializeNBT());
        tag.putInt("LastOpenedPage", lastOpenedPage);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (tag.contains("PageIcons", Tag.TAG_COMPOUND)) {
            iconInventory.deserializeNBT(tag.getCompound("PageIcons"));
        }
        if (tag.contains("LastOpenedPage", Tag.TAG_INT)) {
            this.lastOpenedPage = tag.getInt("LastOpenedPage");
        }
        // Client update packets go through load, not a separate handleUpdateTag path on 1.20.1.
        if (tag.contains("OpenCount", Tag.TAG_INT)) {
            this.openCount = tag.getInt("OpenCount");
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide()) {
            this.openCount = 0;
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.smartchest");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SmartChestMenu(containerId, playerInventory, this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        byte mask = this.clientSyncMask;
        this.clientSyncMask = SYNC_ALL;

        if ((mask & SYNC_OPEN_COUNT) != 0) {
            tag.putInt("OpenCount", openCount);
        }
        if ((mask & SYNC_PAGE_ICONS) != 0) {
            tag.put("PageIcons", iconInventory.serializeNBT());
        }
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient(byte mask) {
        if (this.level != null && !this.level.isClientSide()) {
            this.clientSyncMask = mask;
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
