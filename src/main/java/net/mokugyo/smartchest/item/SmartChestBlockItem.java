package net.mokugyo.smartchest.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.mokugyo.smartchest.client.SmartChestItemStackRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class SmartChestBlockItem extends BlockItem {

    public SmartChestBlockItem(Block block, Properties properties) {
        super(block, properties);
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