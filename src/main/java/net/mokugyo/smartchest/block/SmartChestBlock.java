package net.mokugyo.smartchest.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.mokugyo.smartchest.registry.ModBlockEntities;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SmartChestBlock extends ChestBlock {

    public static final MapCodec<SmartChestBlock> CODEC = simpleCodec(SmartChestBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private record ClickInfo(BlockPos pos, long time) {}
    private static final Map<UUID, ClickInfo> confirmTimers = new HashMap<>();

    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

    public SmartChestBlock(Properties properties) {
        super(properties, ModBlockEntities.SMART_CHEST_BLOCKENTITY::get);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ChestBlock.TYPE, net.minecraft.world.level.block.state.properties.ChestType.SINGLE)
                .setValue(ChestBlock.WATERLOGGED, false)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public MapCodec<? extends SmartChestBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // ★ Iron Chest方式のクライアントティックをブロック側から登録する
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? createTickerHelper(blockEntityType, ModBlockEntities.SMART_CHEST_BLOCKENTITY.get(), SmartChestBlockEntity::clientTick) : null;
    }

    private static boolean isBlocked(Level level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        return level.getBlockState(abovePos).isFaceSturdy(level, abovePos, Direction.DOWN);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.isEmpty() && player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                UUID playerUUID = player.getUUID();
                long currentTime = level.getGameTime();
                ClickInfo lastClick = confirmTimers.get(playerUUID);

                if (lastClick != null && lastClick.pos().equals(pos) && (currentTime - lastClick.time() < 100)) {
                    confirmTimers.remove(playerUUID);
                    player.displayClientMessage(Component.translatable("message.smartchest.destroyed"), true);
                    level.destroyBlock(pos, true, player);
                } else {
                    confirmTimers.put(playerUUID, new ClickInfo(pos, currentTime));
                    player.sendSystemMessage(Component.translatable("message.smartchest.confirm_destroy"));
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            if (isBlocked(level, pos)) {
                return ItemInteractionResult.CONSUME;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SmartChestBlockEntity smartChest && player instanceof ServerPlayer serverPlayer) {
                // ★ ここで開閉音を鳴らすとともに、startOpenを呼び出してopenCountを増やす
                level.playSound(
                        null,
                        pos,
                        SoundEvents.CHEST_OPEN,
                        SoundSource.BLOCKS,
                        0.5F,
                        1.0F
                );
                smartChest.startOpen(player); // 追加
                serverPlayer.openMenu(smartChest, pos);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SmartChestBlockEntity chestEntity) {
                chestEntity.dropAllContents();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmartChestBlockEntity(pos, state);
    }
}