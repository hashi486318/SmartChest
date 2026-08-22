package net.mokugyo.smartchest.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import net.mokugyo.smartchest.menu.SmartChestMenu;
import net.mokugyo.smartchest.registry.ModBlockEntities;
import net.neoforged.neoforge.items.ItemStackHandler;

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
                Containers.dropItemStack(this.level, x, y, z, stack);
            }
        }

        for (int i = 0; i < this.iconInventory.getSlots(); i++) {
            ItemStack icon = this.iconInventory.getStackInSlot(i);
            if (!icon.isEmpty()) {
                Containers.dropItemStack(this.level, x, y, z, icon);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("PageIcons", iconInventory.serializeNBT(registries));
        tag.putInt("LastOpenedPage", lastOpenedPage);
        tag.putInt("OpenCount", openCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (tag.contains("PageIcons", Tag.TAG_COMPOUND)) {
            iconInventory.deserializeNBT(registries, tag.getCompound("PageIcons"));
        }
        if (tag.contains("LastOpenedPage", Tag.TAG_INT)) {
            this.lastOpenedPage = tag.getInt("LastOpenedPage");
        }
        if (tag.contains("OpenCount", Tag.TAG_INT)) {
            this.openCount = tag.getInt("OpenCount");
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
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
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        byte mask = this.clientSyncMask;
        this.clientSyncMask = SYNC_ALL;

        if ((mask & SYNC_OPEN_COUNT) != 0) {
            tag.putInt("OpenCount", openCount);
        }
        if ((mask & SYNC_PAGE_ICONS) != 0) {
            tag.put("PageIcons", iconInventory.serializeNBT(registries));
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
