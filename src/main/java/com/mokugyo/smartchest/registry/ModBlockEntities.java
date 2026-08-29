package com.mokugyo.smartchest.registry;

import com.mokugyo.smartchest.SmartChest;
import com.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SmartChest.MOD_ID);

    public static final RegistryObject<BlockEntityType<SmartChestBlockEntity>> SMART_CHEST_BLOCKENTITY =
            BLOCKENTITIES.register("smart_chest", () -> BlockEntityType.Builder.of(
                    SmartChestBlockEntity::new,
                    ModBlocks.SMART_CHEST_BLOCK.get()
            ).build(null));
}
