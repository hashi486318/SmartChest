package net.mokugyo.smartchest.registry;

import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.block.SmartChestBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(SmartChest.MOD_ID);

    public static final DeferredBlock<SmartChestBlock> SMART_CHEST_BLOCK = BLOCKS.registerBlock(
            "smart_chest",
            SmartChestBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(3.0F, 12.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
    );
}