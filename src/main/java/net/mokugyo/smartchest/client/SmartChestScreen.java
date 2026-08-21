package net.mokugyo.smartchest.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.mokugyo.smartchest.network.SortRequestPacket;
import net.neoforged.neoforge.network.PacketDistributor;
import net.mokugyo.smartchest.menu.SmartChestMenu;
import net.mokugyo.smartchest.network.ChangePagePacket;

public class SmartChestScreen extends AbstractContainerScreen<SmartChestMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public SmartChestScreen(SmartChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.addRenderableWidget(
                Button.builder(Component.literal("≡"), b -> {
                            PacketDistributor.sendToServer(SortRequestPacket.INSTANCE);
                        })
                        .bounds(x + 150, y + 4, 18, 12)
                        .build()
        );

        // 左側 5 タブ (Page 0 ~ 4)
        for (int i = 0; i < 5; i++) {
            final int page = i;
            int buttonX = x - 22;
            int buttonY = y + 18 + (i * 22);

            this.addRenderableWidget(
                    Button.builder(CommonComponents.EMPTY, b -> handleTabClick(page))
                            .bounds(buttonX, buttonY, 20, 20)
                            .build()
            );
        }

        // 右側 5 タブ (Page 5 ~ 9)
        for (int i = 0; i < 5; i++) {
            final int page = i + 5;
            int buttonX = x + this.imageWidth + 2;
            int buttonY = y + 18 + (i * 22);

            this.addRenderableWidget(
                    Button.builder(CommonComponents.EMPTY, b -> handleTabClick(page))
                            .bounds(buttonX, buttonY, 20, 20)
                            .build()
            );
        }
    }

    private void handleTabClick(int page) {
            PacketDistributor.sendToServer(new ChangePagePacket(page));
            this.menu.setPage(page);

        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(
                    SoundEvents.UI_BUTTON_CLICK.value(),
                    0.25F,
                    1.0F
            );
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // ★ スロット54の位置に合わせて枠線を描画
        Slot iconSlot = this.menu.getSlot(54);
        if (iconSlot != null) {
            int iconSlot_x = this.leftPos + iconSlot.x - 1;
            int iconSlot_y = this.topPos + iconSlot.y - 1;

            // 18x18の枠線を描画
            guiGraphics.fill(iconSlot_x, iconSlot_y, iconSlot_x + 18, iconSlot_y + 18, 0xFF373737);
            guiGraphics.fill(iconSlot_x + 1, iconSlot_y + 1, iconSlot_x + 17, iconSlot_y + 17, 0xFF8B8B8B);
        }

        // タブアイコンの描画処理
        if (this.menu.getBlockEntity() != null) {
            for (int i = 0; i < 10; i++) {
                ItemStack icon = this.menu.getBlockEntity().getPageIcon(i);
                if (!icon.isEmpty()) {
                    int bx = (i < 5) ? (x - 20) : (x + this.imageWidth + 4);
                    int by = y + 20 + ((i % 5) * 22);
                    guiGraphics.renderItem(icon, bx, by);
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}