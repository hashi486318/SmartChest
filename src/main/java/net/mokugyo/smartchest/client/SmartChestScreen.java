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
import net.neoforged.neoforge.network.PacketDistributor;
import net.mokugyo.smartchest.blockentity.SmartChestBlockEntity;
import net.mokugyo.smartchest.menu.SmartChestMenu;
import net.mokugyo.smartchest.network.ChangePagePacket;

public class SmartChestScreen extends AbstractContainerScreen<SmartChestMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    private int iconBorderX;
    private int iconBorderY;
    private final int[] tabIconX = new int[SmartChestBlockEntity.PAGE_COUNT];
    private final int[] tabIconY = new int[SmartChestBlockEntity.PAGE_COUNT];
    private final Button[] tabButtons = new Button[SmartChestBlockEntity.PAGE_COUNT];

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

        // Left tabs (Pages 0-4)
        for (int i = 0; i < 5; i++) {
            final int page = i;
            this.tabButtons[page] = this.addRenderableWidget(
                    Button.builder(CommonComponents.EMPTY, b -> handleTabClick(page))
                            .bounds(x - 22, y + 18 + (i * 22), 20, 20)
                            .build()
            );
        }

        // Right tabs (Pages 5-9)
        for (int i = 0; i < 5; i++) {
            final int page = i + 5;
            this.tabButtons[page] = this.addRenderableWidget(
                    Button.builder(CommonComponents.EMPTY, b -> handleTabClick(page))
                            .bounds(x + this.imageWidth + 2, y + 18 + (i * 22), 20, 20)
                            .build()
            );
        }

        int currentPage = this.menu.getCurrentPage();
        if (currentPage >= 0 && currentPage < this.tabButtons.length && this.tabButtons[currentPage] != null) {
            this.setInitialFocus(this.tabButtons[currentPage]);
        }

        Slot iconSlot = this.menu.getSlot(SmartChestBlockEntity.PAGE_SIZE);
        this.iconBorderX = this.leftPos + iconSlot.x - 1;
        this.iconBorderY = this.topPos + iconSlot.y - 1;
        for (int i = 0; i < SmartChestBlockEntity.PAGE_COUNT; i++) {
            this.tabIconX[i] = (i < 5) ? (x - 20) : (x + this.imageWidth + 4);
            this.tabIconY[i] = y + 20 + ((i % 5) * 22);
        }
    }

    private void handleTabClick(int page) {
        PacketDistributor.sendToServer(new ChangePagePacket(page));
        this.menu.setPage(page);

        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.25F, 1.0F);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        guiGraphics.fill(this.iconBorderX, this.iconBorderY, this.iconBorderX + 18, this.iconBorderY + 18, 0xFF373737);
        guiGraphics.fill(this.iconBorderX + 1, this.iconBorderY + 1, this.iconBorderX + 17, this.iconBorderY + 17, 0xFF8B8B8B);

        if (this.menu.getBlockEntity() != null) {
            for (int i = 0; i < SmartChestBlockEntity.PAGE_COUNT; i++) {
                ItemStack icon = this.menu.getBlockEntity().getPageIcon(i);
                if (!icon.isEmpty()) {
                    guiGraphics.renderItem(icon, this.tabIconX[i], this.tabIconY[i]);
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