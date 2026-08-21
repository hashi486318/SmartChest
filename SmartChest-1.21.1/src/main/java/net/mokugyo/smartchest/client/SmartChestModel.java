package net.mokugyo.smartchest.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.mokugyo.smartchest.SmartChest;

/**
 * SmartChest のシングルチェストモデル。
 * 形状（ボックスの寸法・位置）はバニラのチェストと同じもの。
 * バニラの ChestRenderer#createSingleBodyLayer() 相当を、
 * このMod専用のレイヤーとして登録し直したもの。
 */
public class SmartChestModel {

    // Modごとに固有のレイヤー名にしておく（バニラの "chest" と衝突させない）
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(SmartChest.MOD_ID, "smart_chest"),
                    "main"
            );

    private final ModelPart bottom;
    private final ModelPart lid;
    private final ModelPart lock;

    public SmartChestModel(ModelPart root) {
        this.bottom = root.getChild("bottom");
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
    }

    /**
     * EntityRenderersEvent.RegisterLayerDefinitions で登録する形状定義。
     * 数値はバニラのシングルチェストと同一（16x16x16のブロック内に収まる標準形状）。
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "bottom",
                CubeListBuilder.create()
                        .texOffs(0, 19)
                        .addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "lid",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(1.0F, 0.0F, 1.0F, 14.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "lock",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );

        // 64x64 のテクスチャキャンバス（バニラチェストと同じサイズ）
        return LayerDefinition.create(mesh, 64, 64);
    }

    public ModelPart bottom() {
        return bottom;
    }

    public ModelPart lid() {
        return lid;
    }

    public ModelPart lock() {
        return lock;
    }
}