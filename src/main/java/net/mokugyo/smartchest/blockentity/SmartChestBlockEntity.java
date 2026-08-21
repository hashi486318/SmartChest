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
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mokugyo.smartchest.menu.SmartChestMenu;
import net.mokugyo.smartchest.registry.ModBlockEntities;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class SmartChestBlockEntity extends ChestBlockEntity implements MenuProvider {

    public static final int TOTAL_SLOTS = 540;

    private final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler iconInventory = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private int lastOpenedPage = 0;

    // --- Iron Chest風のアニメーション用フィールド ---
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
        if (page < 0 || page >= 10) return ItemStack.EMPTY;
        return this.iconInventory.getStackInSlot(page);
    }

    public int getLastOpenedPage() {
        return this.lastOpenedPage;
    }

    public void setLastOpenedPage(int page) {
        if (page >= 0 && page < 10) {
            this.lastOpenedPage = page;
            this.setChanged();
        }
    }

    // --- クライアント側アニメーションティック（Iron Chest方式） ---
    public static void clientTick(Level level, BlockPos pos, BlockState state, SmartChestBlockEntity blockEntity) {
        blockEntity.oLidAngle = blockEntity.lidAngle;
        float speed = 0.1F;

        if (blockEntity.openCount > 0 && blockEntity.lidAngle == 0.0F) {
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CHEST_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }

        if ((blockEntity.openCount == 0 && blockEntity.lidAngle > 0.0F) || (blockEntity.openCount > 0 && blockEntity.lidAngle < 1.0F)) {
            if (blockEntity.openCount > 0) {
                blockEntity.lidAngle += speed;
                if (blockEntity.lidAngle > 1.0F) {
                    blockEntity.lidAngle = 1.0F;
                }
            } else {
                blockEntity.lidAngle -= speed;
                if (blockEntity.lidAngle < 0.0F) {
                    blockEntity.lidAngle = 0.0F;
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CHEST_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
                }
            }
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openCount++;
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openCount = Math.max(0, this.openCount - 1);
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    public void dropAllContents() {
        if (this.level != null && !this.level.isClientSide()) {
            for (int i = 0; i < this.inventory.getSlots(); i++) {
                ItemStack stack = this.inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
                }
            }
            for (int i = 0; i < this.iconInventory.getSlots(); i++) {
                ItemStack icon = this.iconInventory.getStackInSlot(i);
                if (!icon.isEmpty()) {
                    Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), icon);
                }
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
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}