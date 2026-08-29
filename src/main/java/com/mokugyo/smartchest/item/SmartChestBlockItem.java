package com.mokugyo.smartchest.item;

import com.mokugyo.smartchest.client.SmartChestItemStackRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class SmartChestBlockItem extends BlockItem {

    public SmartChestBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SmartChestRender.INSTANCE);
    }

    public static final class SmartChestRender implements IClientItemExtensions {

        public static final SmartChestRender INSTANCE = new SmartChestRender();

        private SmartChestItemStackRenderer renderer;

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (this.renderer == null) {
                this.renderer = new SmartChestItemStackRenderer();
            }
            return this.renderer;
        }
    }
}
