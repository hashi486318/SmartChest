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

public class SmartChestModel {

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
        // lid の子要素としてロックを取得するように修正
        this.lock = this.lid.getChild("lock");
    }

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

        // lid を変数として受け取り、その中に lock を追加する
        PartDefinition lid = root.addOrReplaceChild(
                "lid",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(1.0F, 0.0F, 1.0F, 14.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 0.0F)
        );

        // lock を lid の子として追加
        lid.addOrReplaceChild(
                "lock",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        // addBoxの引数: x, y, z, sizeX, sizeY, sizeZ
                        .addBox(7.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    public ModelPart bottom() { return bottom; }
    public ModelPart lid() { return lid; }
    public ModelPart lock() { return lock; }
}