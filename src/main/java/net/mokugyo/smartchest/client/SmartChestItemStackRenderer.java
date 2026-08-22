package net.mokugyo.smartchest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.mokugyo.smartchest.registry.ModBlocks;

public class SmartChestItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    public SmartChestItemStackRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        SmartChestBlockEntity blockEntity = new SmartChestBlockEntity(
                BlockPos.ZERO,
                ModBlocks.SMART_CHEST_BLOCK.get().defaultBlockState()
        );

        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
