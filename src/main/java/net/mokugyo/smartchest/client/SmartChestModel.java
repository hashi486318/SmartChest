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

    public SmartChestModel(ModelPart root) {
        this.bottom = root.getChild("bottom");
        this.lid = root.getChild("lid");
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

        PartDefinition lid = root.addOrReplaceChild(
                "lid",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(1.0F, 0.0F, 1.0F, 14.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 0.0F)
        );

        lid.addOrReplaceChild(
                "lock",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(7.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    public ModelPart bottom() { return bottom; }
    public ModelPart lid() { return lid; }
}