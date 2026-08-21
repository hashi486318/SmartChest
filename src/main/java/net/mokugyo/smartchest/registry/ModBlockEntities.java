package net.mokugyo.smartchest.registry;

import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE, SmartChest.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmartChestBlockEntity>> SMART_CHEST_BLOCKENTITY =
            BLOCKENTITIES.register("smart_chest", () -> BlockEntityType.Builder.of(
                    SmartChestBlockEntity::new,
                    ModBlocks.SMART_CHEST_BLOCK.get()
            ).build(null));
}
