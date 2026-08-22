package net.mokugyo.smartchest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.block.SmartChestBlock;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;

public class SmartChestRenderer implements BlockEntityRenderer<SmartChestBlockEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmartChest.MOD_ID, "textures/model/smart_chest.png");

    private final SmartChestModel model;

    public SmartChestRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SmartChestModel(context.bakeLayer(SmartChestModel.LAYER_LOCATION));
    }

    @Override
    public void render(SmartChestBlockEntity blockEntity,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay) {

        BlockState state = blockEntity.getBlockState();
        Direction facing = state.hasProperty(SmartChestBlock.FACING)
                ? state.getValue(SmartChestBlock.FACING)
                : Direction.NORTH;

        poseStack.pushPose();

        // Rotate model based on block facing
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        // Smoothly interpolate lid angle with cubic easing
        float openNess = blockEntity.oLidAngle + (blockEntity.lidAngle - blockEntity.oLidAngle) * partialTick;
        openNess = 1.0F - openNess;
        openNess = 1.0F - openNess * openNess * openNess;
        model.lid().xRot = -(openNess * 1.5707964F);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        model.bottom().render(poseStack, vertexConsumer, packedLight, packedOverlay);
        model.lid().render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}