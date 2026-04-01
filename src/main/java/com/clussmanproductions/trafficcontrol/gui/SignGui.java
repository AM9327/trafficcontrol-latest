package com.clussmanproductions.trafficcontrol.gui;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.network.PacketUpdateSign;
import com.clussmanproductions.trafficcontrol.signs.Sign;
import com.clussmanproductions.trafficcontrol.signs.SignRepository;
import com.clussmanproductions.trafficcontrol.tileentity.SignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignGui extends Screen {

    private final SignBlockEntity signBE;
    private final List<Sign> filteredSigns = new ArrayList<>();
    private EditBox searchBox;
    private int scrollOffset = 0;
    private UUID selectedSignId;

    private static final int PANEL_WIDTH = 160;
    private static final int THUMB_SIZE = 32;
    private static final int THUMB_PADDING = 4;
    private static final int SCROLLBAR_WIDTH = 8;

    private boolean isDraggingScrollbar = false;

    public SignGui(SignBlockEntity signBE) {
        super(Component.literal("Sign Configuration"));
        this.signBE = signBE;
        this.selectedSignId = signBE.getSignId();
    }

    public static void open(SignBlockEntity signBE) {
        Minecraft.getInstance().setScreen(new SignGui(signBE));
    }

    @Override
    protected void init() {
        SignRepository repo = ModTrafficControl.SIGN_REPO;
        if (!repo.isInitialized()) {
            repo.init(Minecraft.getInstance().getResourceManager());
        }

        int listX = width - PANEL_WIDTH - 4;
        searchBox = new EditBox(font, listX, height - 24, PANEL_WIDTH, 18,
                Component.literal("Search..."));
        searchBox.setHint(Component.literal("Search..."));
        searchBox.setResponder(text -> {
            scrollOffset = 0;
            updateFilteredSigns();
        });
        addRenderableWidget(searchBox);

        updateFilteredSigns();
    }

    private void updateFilteredSigns() {
        filteredSigns.clear();
        String query = searchBox != null ? searchBox.getValue().toLowerCase() : "";
        for (Sign sign : ModTrafficControl.SIGN_REPO.getAllSigns()) {
            if (query.isEmpty() || sign.getName().toLowerCase().contains(query)) {
                filteredSigns.add(sign);
            }
        }
    }

    private int getCols() {
        int gridWidth = PANEL_WIDTH - SCROLLBAR_WIDTH - 2;
        int cols = gridWidth / (THUMB_SIZE + THUMB_PADDING);
        return Math.max(1, cols);
    }

    private int getVisibleRows() {
        int listHeight = height - 32;
        return listHeight / (THUMB_SIZE + THUMB_PADDING);
    }

    private int getTotalRows() {
        int cols = getCols();
        return (filteredSigns.size() + cols - 1) / cols;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int listX = width - PANEL_WIDTH - 4;
        int listY = 4;
        int listHeight = height - 32;
        int leftPanelWidth = listX - 8;
        int leftCenter = leftPanelWidth / 2;

        // --- Left panel: sign preview ---
        Sign currentSign = getCurrentSign();
        if (currentSign != null) {
            graphics.drawCenteredString(font, currentSign.getName(), leftCenter, 10, 0xFFFF00);

            String typeText = ModTrafficControl.SIGN_REPO.getFriendlyTypeName(currentSign.getType());
            if (typeText != null) {
                graphics.drawCenteredString(font, typeText, leftCenter, 22, 0xAAAAAA);
            }

            int previewSize = Math.min(leftPanelWidth - 20, height - 80);
            int previewX = leftCenter - previewSize / 2;
            int previewY = 36;

            Identifier frontTex = currentSign.getFrontTexture();
            ensureTextureLoaded(frontTex);
            graphics.blit(RenderPipelines.GUI_TEXTURED, frontTex, previewX, previewY, 0, 0,
                    previewSize, previewSize, previewSize, previewSize);

            if (currentSign.getNote() != null && !currentSign.getNote().isEmpty()) {
                int noteY = previewY + previewSize + 4;
                List<net.minecraft.util.FormattedCharSequence> lines =
                        font.split(Component.literal(currentSign.getNote()), leftPanelWidth - 10);
                for (int i = 0; i < Math.min(lines.size(), 3); i++) {
                    graphics.drawString(font, lines.get(i), 5, noteY + i * 10, 0xCCCCCC);
                }
            }
        } else {
            graphics.drawCenteredString(font, "Click a sign in the list", leftCenter, height / 2 - 10, 0x999999);
            graphics.drawCenteredString(font, "to select it", leftCenter, height / 2 + 2, 0x999999);
        }

        // --- Right panel: sign grid background ---
        graphics.fill(listX - 2, listY - 2, width - 2, listY + listHeight + 2, 0x80000000);

        int cols = getCols();
        int visibleRows = getVisibleRows();
        int totalRows = getTotalRows();
        String hoveredName = null;

        for (int i = 0; i < filteredSigns.size(); i++) {
            int row = i / cols - scrollOffset;
            int col = i % cols;
            if (row < 0 || row >= visibleRows) continue;

            int thumbX = listX + col * (THUMB_SIZE + THUMB_PADDING);
            int thumbY = listY + row * (THUMB_SIZE + THUMB_PADDING);

            Sign sign = filteredSigns.get(i);

            // Green highlight for selected sign (2px border)
            if (sign.getID().equals(selectedSignId)) {
                graphics.fill(thumbX - 2, thumbY - 2, thumbX + THUMB_SIZE + 2, thumbY + THUMB_SIZE + 2, 0xFF00FF00);
            }

            // Blue highlight on hover (2px border)
            boolean hovered = mouseX >= thumbX && mouseX < thumbX + THUMB_SIZE &&
                    mouseY >= thumbY && mouseY < thumbY + THUMB_SIZE;
            if (hovered) {
                graphics.fill(thumbX - 2, thumbY - 2, thumbX + THUMB_SIZE + 2, thumbY + THUMB_SIZE + 2, 0xFF4444FF);
                hoveredName = sign.getName();
            }

            Identifier tex = sign.getFrontTexture();
            ensureTextureLoaded(tex);
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex, thumbX, thumbY, 0, 0,
                    THUMB_SIZE, THUMB_SIZE, THUMB_SIZE, THUMB_SIZE);
        }

        // Draw hovered sign name at bottom of grid
        if (hoveredName != null) {
            graphics.drawString(font, hoveredName, listX, listY + listHeight - 10, 0xFFFFFF);
        }

        // --- Scrollbar ---
        int scrollTrackX = width - SCROLLBAR_WIDTH - 4;
        int scrollTrackY = listY;
        int scrollTrackHeight = listHeight;

        // Track background
        graphics.fill(scrollTrackX, scrollTrackY, scrollTrackX + SCROLLBAR_WIDTH, scrollTrackY + scrollTrackHeight, 0xFF202020);

        // Handle
        if (totalRows > visibleRows) {
            int handleHeight = Math.max(15, scrollTrackHeight * visibleRows / totalRows);
            int maxScrollOffset = totalRows - visibleRows;
            int maxHandleTop = scrollTrackHeight - handleHeight;
            int handleTop = scrollTrackY + (maxScrollOffset > 0 ? scrollOffset * maxHandleTop / maxScrollOffset : 0);
            graphics.fill(scrollTrackX, handleTop, scrollTrackX + SCROLLBAR_WIDTH, handleTop + handleHeight, 0xFF808080);
        } else {
            // Full-height handle when all signs fit
            graphics.fill(scrollTrackX, scrollTrackY, scrollTrackX + SCROLLBAR_WIDTH, scrollTrackY + scrollTrackHeight, 0xFF606060);
        }

        // Render widgets (searchBox) via super
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isDoubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();

        int listX = width - PANEL_WIDTH - 4;
        int listY = 4;
        int listHeight = height - 32;
        int cols = getCols();
        int scrollTrackX = width - SCROLLBAR_WIDTH - 4;

        // Check scrollbar click
        if (mouseX >= scrollTrackX && mouseX < scrollTrackX + SCROLLBAR_WIDTH &&
                mouseY >= listY && mouseY < listY + listHeight) {
            isDraggingScrollbar = true;
            updateScrollFromMouseY(mouseY, listY, listHeight);
            return true;
        }

        // Check sign grid clicks
        if (mouseX >= listX && mouseX < scrollTrackX && mouseY >= listY && mouseY < listY + listHeight) {
            int col = (int) (mouseX - listX) / (THUMB_SIZE + THUMB_PADDING);
            int row = (int) (mouseY - listY) / (THUMB_SIZE + THUMB_PADDING) + scrollOffset;

            if (col >= 0 && col < cols) {
                int index = row * cols + col;
                if (index >= 0 && index < filteredSigns.size()) {
                    Sign clicked = filteredSigns.get(index);
                    selectedSignId = clicked.getID();
                    signBE.setSignId(selectedSignId);
                    signBE.clearTextLines();
                    this.setFocused(null);
                    return true;
                }
            }
        }

        // Let super handle searchBox focus and other widget events
        if (super.mouseClicked(event, isDoubleClick)) {
            return true;
        }

        // Click on empty space: unfocus searchBox
        this.setFocused(null);
        return false;
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            int listY = 4;
            int listHeight = height - 32;
            updateScrollFromMouseY(event.y(), listY, listHeight);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        if (isDraggingScrollbar) {
            isDraggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    private void updateScrollFromMouseY(double mouseY, int listY, int listHeight) {
        int totalRows = getTotalRows();
        int visibleRows = getVisibleRows();
        int maxScrollOffset = Math.max(0, totalRows - visibleRows);

        double fraction = (mouseY - listY) / listHeight;
        fraction = Math.max(0, Math.min(1, fraction));
        scrollOffset = (int) Math.round(fraction * maxScrollOffset);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalRows = getTotalRows();
        int visibleRows = getVisibleRows();

        scrollOffset -= (int) scrollY;
        scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, totalRows - visibleRows)));
        return true;
    }

    @Override
    public void onClose() {
        if (selectedSignId != null) {
            List<String> texts = new ArrayList<>(signBE.getTextLines());
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                connection.send(new PacketUpdateSign(signBE.getBlockPos(), selectedSignId, texts));
            }
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private Sign getCurrentSign() {
        if (selectedSignId == null) return null;
        return ModTrafficControl.SIGN_REPO.getSignByID(selectedSignId);
    }

    private void ensureTextureLoaded(Identifier location) {
        var texManager = Minecraft.getInstance().getTextureManager();
        if (texManager.getTexture(location) == null) {
            texManager.register(location, new SimpleTexture(location));
        }
    }
}
