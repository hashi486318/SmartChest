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
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;

/**
 * SmartChest専用の描画クラス。
 *
 * バニラのChestRendererと違い、
 * ・double chest対応なし（SmartChestはシングル固定のため）
 * ・テクスチャアトラス（entity/chestアトラス）は使わず、直接1枚のPNGを描画
 *   → assets/smartchest/textures/entity/smart_chest.png をそのままバインドするだけなので、
 *     アトラス用のsprite source jsonを別途用意する必要がない（バニラより単純な構成）。
 *
 * 蓋の開閉は SmartChestBlockEntity が ChestBlockEntity を継承しているため、
 * 標準の getOpenNess(partialTick) がそのまま使える。
 */
public class SmartChestRenderer implements BlockEntityRenderer<SmartChestBlockEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmartChest.MOD_ID, "textures/entity/smart_chest.png");

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
        Direction facing = state.hasProperty(ChestBlock.FACING)
                ? state.getValue(ChestBlock.FACING)
                : Direction.NORTH;

        poseStack.pushPose();

        // ブロックの向きに合わせてモデルを回転
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        // SmartChestRenderer.java の render メソッド内

        // 蓋の開閉度合い（0.0 〜 1.0）を滑らかに補間
        float openNess = blockEntity.oLidAngle + (blockEntity.lidAngle - blockEntity.oLidAngle) * partialTick;
        // 立方イージング（お好みで調整可能）
        openNess = 1.0F - openNess;
        openNess = 1.0F - openNess * openNess * openNess;

        float lidAngle = -(openNess * 1.5707964F); // 0〜90度（ラジアン）
        model.lid().xRot = lidAngle;
        model.lock().xRot = lidAngle;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));

        model.bottom().render(poseStack, vertexConsumer, packedLight, packedOverlay);
        model.lid().render(poseStack, vertexConsumer, packedLight, packedOverlay);
        model.lock().render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}