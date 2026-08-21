package net.mokugyo.smartchest.registry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.client.SmartChestModel;
import net.mokugyo.smartchest.registry.ModItems;
import net.mokugyo.smartchest.client.SmartChestRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * クライアント専用の登録処理（アイテムの Builtin レンダラー登録）
 *
 * Neoforge (Fabric 系) 向けに BuiltinItemRendererRegistry を使って
 * SmartChest のアイテム描画をブロックエンティティ用モデルで表示します。
 */
@EventBusSubscriber(modid = SmartChest.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    @SubscribeEvent
    public static void registerItemRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // アイテム用のレンダラーを登録
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SMART_CHEST_ITEM.get(), (stack, transform, matrices, vertexConsumers, light, overlay) -> {
            // モデルの取得（ClientSetup で registerLayerDefinition 済みであること）
            var modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(SmartChestModel.LAYER_LOCATION);
            SmartChestModel model = new SmartChestModel(modelPart);

            // 軽い描画（ブロックレンダラーと同じ見た目になるよう位置調整）
            matrices.pushPose();
            // 中心に寄せて回転・スケールを調整（必要に応じて微調整してください）
            matrices.translate(0.5F, 0.5F, 0.5F);
            matrices.translate(-0.5F, -0.5F, -0.5F);

            // 蓋は閉じた状態で描画（必要ならアイテムのNBT等で角度を変えられます）
            model.lid().xRot = 0.0F;
            model.lock().xRot = 0.0F;

            ResourceLocation tex = ResourceLocation.fromNamespaceAndPath(SmartChest.MOD_ID, "textures/model/smart_chest.png");
            VertexConsumer v = vertexConsumers.getBuffer(RenderType.entityCutout(tex));

            model.bottom().render(matrices, v, light, overlay);
            model.lid().render(matrices, v, light, overlay);
            model.lock().render(matrices, v, light, overlay);

            matrices.popPose();
        });
    }
}
