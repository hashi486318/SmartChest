package net.mokugyo.smartchest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.mokugyo.smartchest.SmartChest;

/**
 * Item 表示時にチェスト本体モデルを描画し、チェスト上にアイコンを置くレンダラー。
 * BlockEntityWithoutLevelRenderer を継承するため Fabric API に依存しません。
 *
 * models/item/smart_chest.json の display セクションの値を TransformType ごとに適用します。
 */
public class SmartChestItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SmartChest.MOD_ID, "textures/model/smart_chest.png");

    public SmartChestItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, TransformType transform, PoseStack matrices, MultiBufferSource buffer, int light, int overlay) {
        try {
            var modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(SmartChestModel.LAYER_LOCATION);
            SmartChestModel model = new SmartChestModel(modelPart);

            // Apply display transform values from models/item/smart_chest.json
            applyDisplayTransform(transform, matrices);

            // Render chest model
            matrices.pushPose();
            VertexConsumer v = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
            model.bottom().render(matrices, v, light, overlay);
            model.lid().render(matrices, v, light, overlay);
            model.lock().render(matrices, v, light, overlay);
            matrices.popPose();

            // Render the icon on top of the chest (use the ItemStack itself as representative)
            if (stack != null && !stack.isEmpty()) {
                matrices.pushPose();
                matrices.translate(0.5D, 0.85D, 0.5D);
                matrices.scale(0.5F, 0.5F, 0.5F);
                matrices.mulPose(Axis.XP.rotationDegrees(90.0F));

                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlay, matrices, buffer, 0);
                matrices.popPose();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void applyDisplayTransform(ItemTransforms.TransformType transform, PoseStack matrices) {
        float rx = 0f, ry = 0f, rz = 0f;
        float tx = 0f, ty = 0f, tz = 0f;
        float sx = 1f, sy = 1f, sz = 1f;

        switch (transform) {
            case GUI:
                rx = 30f; ry = 45f; rz = 0f;
                tx = 0f; ty = 0f; tz = 0f;
                sx = sy = sz = 0.625f;
                break;
            case GROUND:
                rx = 0f; ry = 0f; rz = 0f;
                tx = 0f; ty = 3f; tz = 0f;
                sx = sy = sz = 0.25f;
                break;
            case HEAD:
                rx = 0f; ry = 180f; rz = 0f;
                tx = 0f; ty = 0f; tz = 0f;
                sx = sy = sz = 1f;
                break;
            case FIXED:
                rx = 0f; ry = 180f; rz = 0f;
                tx = 0f; ty = 0f; tz = 0f;
                sx = sy = sz = 0.5f;
                break;
            case THIRD_PERSON_RIGHT_HAND:
                rx = 75f; ry = 315f; rz = 0f;
                tx = 0f; ty = 2.5f; tz = 0f;
                sx = sy = sz = 0.375f;
                break;
            case FIRST_PERSON_RIGHT_HAND:
                rx = 0f; ry = 315f; rz = 0f;
                tx = 0f; ty = 0f; tz = 0f;
                sx = sy = sz = 0.4f;
                break;
            default:
                break;
        }

        matrices.translate(tx / 16.0F, ty / 16.0F, tz / 16.0F);
        matrices.mulPose(Axis.XP.rotationDegrees(rx));
        matrices.mulPose(Axis.YP.rotationDegrees(ry));
        matrices.mulPose(Axis.ZP.rotationDegrees(rz));
        matrices.scale(sx, sy, sz);
    }
}
