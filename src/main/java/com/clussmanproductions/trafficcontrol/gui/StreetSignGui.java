package com.clussmanproductions.trafficcontrol.gui;

import com.clussmanproductions.trafficcontrol.network.PacketUpdateStreetSign;
import com.clussmanproductions.trafficcontrol.tileentity.StreetSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StreetSignGui extends Screen {

    private final StreetSignBlockEntity streetSignBE;
    private final String[] messages;
    private final int[] colors;
    private int signCount;
    private int activeSign = -1; // index of the sign being edited (-1 = none, newest is active)
    private @Nullable TextFieldHelper activeField;
    private int frame;

    private static final int[] SIGN_COLORS = {0xFF006400, 0xFFCC0000, 0xFF0000CC, 0xFFCCCC00};
    private static final String[] SIGN_COLOR_NAMES = {"Green", "Red", "Blue", "Yellow"};

    public StreetSignGui(StreetSignBlockEntity streetSignBE) {
        super(Component.literal("Edit Street Sign"));
        this.streetSignBE = streetSignBE;
        this.signCount = streetSignBE.getSignCount();
        this.messages = new String[StreetSignBlockEntity.MAX_SIGNS];
        this.colors = new int[StreetSignBlockEntity.MAX_SIGNS];
        for (int i = 0; i < signCount; i++) {
            messages[i] = streetSignBE.getText(i);
            colors[i] = streetSignBE.getColorIndex(i);
        }
        // The newest sign is the active one for editing
        activeSign = signCount - 1;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        // Text field for active sign
        if (activeSign >= 0) {
            this.activeField = new TextFieldHelper(
                    () -> this.messages[activeSign],
                    text -> {
                        this.messages[activeSign] = text;
                        this.streetSignBE.setText(activeSign, text);
                    },
                    TextFieldHelper.createClipboardGetter(this.minecraft),
                    TextFieldHelper.createClipboardSetter(this.minecraft),
                    text -> text.length() <= 50
            );
        }

        // Color buttons for active sign
        int btnW = 50, gap = 2;
        int rowW = 4 * btnW + 3 * gap;
        int startX = cx - rowW / 2;
        int btnY = cy + 40;

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            this.addRenderableWidget(Button.builder(Component.literal(SIGN_COLOR_NAMES[i]), b -> {
                if (activeSign >= 0) {
                    this.colors[activeSign] = idx;
                }
            }).bounds(startX + i * (btnW + gap), btnY, btnW, 20).build());
        }

        // Done button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
            this.minecraft.setScreen(null);
        }).bounds(cx - 50, btnY + 28, 100, 20).build());
    }

    @Override
    public void tick() {
        this.frame++;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int cx = this.width / 2;
        int cy = this.height / 2;

        // Title
        graphics.drawCenteredString(this.font, this.title, cx, cy - 80, 0xFFFFFF);

        // --- Sign preview: render all plates stacked ---
        int pw = 200, ph = 20; // per plate
        int px = cx - pw / 2;
        int totalH = signCount * ph;
        int baseY = cy - 60;

        for (int i = signCount - 1; i >= 0; i--) { // render top-to-bottom
            int py = baseY + (signCount - 1 - i) * ph;

            // White border (2px)
            graphics.fill(px - 2, py - 2, px + pw + 2, py + ph + 2, 0xFFFFFFFF);
            // Fill with sign color
            graphics.fill(px, py, px + pw, py + ph, SIGN_COLORS[this.colors[i]]);

            // Text
            int textColor = this.colors[i] == 3 ? 0xFF000000 : 0xFFFFFFFF;
            int textCenterY = py + (ph - this.font.lineHeight) / 2;

            if (!this.messages[i].isEmpty()) {
                int textWidth = this.font.width(this.messages[i]);
                int textX = cx - textWidth / 2;
                graphics.drawString(this.font, this.messages[i], textX, textCenterY, textColor, false);

                // Cursor for active sign
                if (i == activeSign && this.frame / 6 % 2 == 0 && this.activeField != null) {
                    int cursorPos = this.activeField.getCursorPos();
                    String before = this.messages[i].substring(0, Math.min(cursorPos, this.messages[i].length()));
                    int cursorX = textX + this.font.width(before);
                    if (cursorPos >= this.messages[i].length()) {
                        graphics.drawString(this.font, "_", cursorX, textCenterY, textColor, false);
                    } else {
                        graphics.fill(cursorX, textCenterY - 1, cursorX + 1, textCenterY + this.font.lineHeight, textColor);
                    }
                }
            } else if (i == activeSign && this.frame / 6 % 2 == 0) {
                graphics.drawString(this.font, "_", cx, textCenterY, textColor, false);
            }

            // Active sign indicator
            if (i == activeSign) {
                graphics.fill(px - 4, py, px - 2, py + ph, 0xFFFFFF00);
            }
        }

        // Selected color indicator
        if (activeSign >= 0) {
            int btnW = 50, gap = 2;
            int rowW = 4 * btnW + 3 * gap;
            int startX = cx - rowW / 2;
            int btnY = cy + 40;
            int selX = startX + this.colors[activeSign] * (btnW + gap);
            graphics.fill(selX, btnY + 21, selX + btnW, btnY + 23, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            this.minecraft.setScreen(null);
            return true;
        }
        if (event.isConfirmation()) {
            this.minecraft.setScreen(null);
            return true;
        }
        // Up/Down switches active sign
        if (event.isUp() && activeSign < signCount - 1) {
            activeSign++;
            rebuildField();
            return true;
        }
        if (event.isDown() && activeSign > 0) {
            activeSign--;
            rebuildField();
            return true;
        }

        return this.activeField != null && this.activeField.keyPressed(event) || super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.activeField != null) {
            this.activeField.charTyped(event);
        }
        return true;
    }

    private void rebuildField() {
        if (activeSign >= 0 && activeSign < signCount) {
            this.activeField = new TextFieldHelper(
                    () -> this.messages[activeSign],
                    text -> {
                        this.messages[activeSign] = text;
                        this.streetSignBE.setText(activeSign, text);
                    },
                    TextFieldHelper.createClipboardGetter(this.minecraft),
                    TextFieldHelper.createClipboardSetter(this.minecraft),
                    text -> text.length() <= 50
            );
        }
    }

    @Override
    public void removed() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            List<String> texts = new ArrayList<>();
            List<Integer> colorIndices = new ArrayList<>();
            for (int i = 0; i < signCount; i++) {
                texts.add(this.messages[i].trim());
                colorIndices.add(this.colors[i]);
            }
            connection.send(new PacketUpdateStreetSign(
                    this.streetSignBE.getBlockPos(),
                    texts,
                    colorIndices,
                    this.streetSignBE.getTextColor()));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
