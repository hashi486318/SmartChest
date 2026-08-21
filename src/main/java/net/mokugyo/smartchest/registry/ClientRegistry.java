package net.mokugyo.smartchest.registry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.mokugyo.smartchest.SmartChest;
import net.mokugyo.smartchest.client.SmartChestModel;
import net.mokugyo.smartchest.registry.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * クライアント専用の登録処理（Fabric API に直接依存せず、あれば利用する実装）
 *
 * Fabric の BuiltinItemRendererRegistry が存在する場合はリフレクションで呼び出して
 * アイテムレンダラーを登録します。存在しない（Neoforge 単体など）の場合は何もしません。
 */
@EventBusSubscriber(modid = SmartChest.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    @SubscribeEvent
    public static void registerItemRenderers(EntityRenderersEvent.RegisterRenderers event) {
        try {
            // Fabric の BuiltinItemRendererRegistry をリフレクションで探す
            Class<?> registryClass = Class.forName("net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry");
            Class<?> birInterface = Class.forName("net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer");

            Field instanceField = registryClass.getField("INSTANCE");
            Object registryInstance = instanceField.get(null);

            // InvocationHandler を実装するプロキシを生成
            Object rendererProxy = Proxy.newProxyInstance(
                    birInterface.getClassLoader(),
                    new Class[]{birInterface},
                    (proxy, method, args) -> {
                        if ("render".equals(method.getName())) {
                            // signature: render(ItemStack stack, ItemTransforms.TransformType transform, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay)
                            ItemStack stack = (ItemStack) args[0];
                            ItemTransforms.TransformType transform = (ItemTransforms.TransformType) args[1];
                            PoseStack matrices = (PoseStack) args[2];
                            MultiBufferSource vertexConsumers = (MultiBufferSource) args[3];
                            int light = (Integer) args[4];
                            int overlay = (Integer) args[5];

                            // Bake model layer and render (簡易描画)
                            var modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(SmartChestModel.LAYER_LOCATION);
                            SmartChestModel model = new SmartChestModel(modelPart);

                            matrices.pushPose();
                            // 中心合わせ (必要なら調整)
                            matrices.translate(0.5F, 0.5F, 0.5F);
                            matrices.translate(-0.5F, -0.5F, -0.5F);

                            model.lid().xRot = 0.0F;
                            model.lock().xRot = 0.0F;

                            ResourceLocation tex = ResourceLocation.fromNamespaceAndPath(SmartChest.MOD_ID, "textures/model/smart_chest.png");
                            VertexConsumer v = vertexConsumers.getBuffer(RenderType.entityCutout(tex));

                            model.bottom().render(matrices, v, light, overlay);
                            model.lid().render(matrices, v, light, overlay);
                            model.lock().render(matrices, v, light, overlay);

                            matrices.popPose();
                        }
                        return null;
                    }
            );

            // registry.register(Item, BuiltinItemRenderer)
            Method registerMethod = registryClass.getMethod("register", Item.class, birInterface);
            Item item = ModItems.SMART_CHEST_ITEM.get();
            registerMethod.invoke(registryInstance, item, rendererProxy);

        } catch (ClassNotFoundException e) {
            // Fabric API が無ければ何もしない（Neoforge 単体など）
            // ここでログを出すのも良いですが、ログ実装は環境に合わせて追加してください
        } catch (Throwable t) {
            // 何か失敗した場合はスタックトレースを出力しておく
            t.printStackTrace();
        }
    }
}
