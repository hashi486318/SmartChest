package com.mokugyo.smartchest.registry;

import com.mokugyo.smartchest.SmartChest;
import com.mokugyo.smartchest.block.SmartChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SmartChest.MOD_ID);

    public static final RegistryObject<SmartChestBlock> SMART_CHEST_BLOCK = BLOCKS.register(
            "smart_chest",
            () -> new SmartChestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(5.0F, 20.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion())
    );
}
