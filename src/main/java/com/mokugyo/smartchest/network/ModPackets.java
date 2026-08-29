package com.mokugyo.smartchest.network;

import com.mokugyo.smartchest.SmartChest;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SmartChest.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        CHANNEL.registerMessage(
                0,
                ChangePagePacket.class,
                ChangePagePacket::encode,
                ChangePagePacket::decode,
                ChangePagePacket::handle
        );
    }
}
