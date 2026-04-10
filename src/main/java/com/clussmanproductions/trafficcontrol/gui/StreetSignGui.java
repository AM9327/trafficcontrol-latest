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

public class StreetSignGui extends Screen {

    private final StreetSignBlockEntity streetSignBE;
    private @Nullable TextFieldHelper field1;
    private @Nullable TextFieldHelper field2;
    private String line1;
    private String line2;
    private int activeLine = 0; // 0 = line1, 1 = line2
    private int colorIndex;
    private int frame;

    // Sign plate background colors (ARGB)
    private static final int[] SIGN_COLORS = {0xFF006400, 0xFFCC0000, 0xFF0000CC, 0xFFCCCC00};
    private static final String[] SIGN_COLOR_NAMES = {"Green", "Red", "Blue", "Yellow"};

    public StreetSignGui(StreetSignBlockEntity streetSignBE) {
        super(Component.literal("Edit Street Sign"));
        this.streetSignBE = streetSignBE;
        this.line1 = streetSignBE.getText1();
        this.line2 = streetSignBE.getText2();
        this.colorIndex = streetSignBE.getColorIndex();
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        this.field1 = new TextFieldHelper(
                () -> this.line1,
                this::setLine1,
                TextFieldHelper.createClipboardGetter(this.minecraft),
                TextFieldHelper.createClipboardSetter(this.minecraft),
                text -> text.length() <= 50
        );

        this.field2 = new TextFieldHelper(
                () -> this.line2,
                this::setLine2,
                TextFieldHelper.createClipboardGetter(this.minecraft),
                TextFieldHelper.createClipboardSetter(this.minecraft),
                text -> text.length() <= 50
        );

        // Sign color buttons
        int btnW = 50, gap = 2;
        int rowW = 4 * btnW + 3 * gap;
        int startX = cx - rowW / 2;
        int btnY = cy + 15;

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            this.addRenderableWidget(Button.builder(Component.literal(SIGN_COLOR_NAMES[i]), b -> {
                this.colorIndex = idx;
            }).bounds(startX + i * (btnW + gap), btnY, btnW, 20).build());
        }

        // Done button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
            this.minecraft.setScreen(null);
        }).bounds(cx - 50, btnY + 28, 100, 20).build());
    }

    private void setLine1(String text) {
        this.line1 = text;
        this.streetSignBE.setText1(text);
    }

    private void setLine2(String text) {
        this.line2 = text;
        this.streetSignBE.setText2(text);
    }

    private TextFieldHelper activeField() {
        return activeLine == 0 ? field1 : field2;
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
        graphics.drawCenteredString(this.font, this.title, cx, cy - 75, 0xFFFFFF);

        // --- Sign preview ---
        int pw = 200, ph = 40;
        int px = cx - pw / 2;
        int py = cy - 60;

        // White border (2px)
        graphics.fill(px - 2, py - 2, px + pw + 2, py + ph + 2, 0xFFFFFFFF);
        // Fill with sign color
        graphics.fill(px, py, px + pw, py + ph, SIGN_COLORS[this.colorIndex]);

        // Preview text on the sign
        int textColor = this.colorIndex == 3 ? 0xFF000000 : 0xFFFFFFFF;
        boolean showCursor = this.frame / 6 % 2 == 0;

        // Layout: two lines centered vertically
        int lineH = this.font.lineHeight;
        boolean has1 = !this.line1.isEmpty() || activeLine == 0;
        boolean has2 = !this.line2.isEmpty() || activeLine == 1;
        int lineCount = 2; // always show 2 lines in preview
        int totalH = lineCount * lineH;
        int baseY = py + (ph - totalH) / 2;

        // Draw line 1
        renderLine(graphics, this.line1, cx, baseY, textColor, showCursor && activeLine == 0, field1, activeLine == 0);
        // Draw line 2
        renderLine(graphics, this.line2, cx, baseY + lineH, textColor, showCursor && activeLine == 1, field2, activeLine == 1);

        // Active line indicator
        int indicatorY = baseY + activeLine * lineH;
        graphics.fill(px - 2, indicatorY - 1, px, indicatorY + lineH + 1, textColor);

        // Selected color indicator (white underline below active button)
        int btnW = 50, gap = 2;
        int rowW = 4 * btnW + 3 * gap;
        int startX = cx - rowW / 2;
        int btnY = cy + 15;
        int selX = startX + this.colorIndex * (btnW + gap);
        graphics.fill(selX, btnY + 21, selX + btnW, btnY + 23, 0xFFFFFFFF);
    }

    private void renderLine(GuiGraphics graphics, String text, int cx, int y, int textColor,
                            boolean showCursor, TextFieldHelper field, boolean isActive) {
        if (!text.isEmpty()) {
            int textWidth = this.font.width(text);
            int textX = cx - textWidth / 2;
            graphics.drawString(this.font, text, textX, y, textColor, false);

            if (showCursor && field != null) {
                int cursorPos = field.getCursorPos();
                String before = text.substring(0, Math.min(cursorPos, text.length()));
                int cursorX = textX + this.font.width(before);
                if (cursorPos >= text.length()) {
                    graphics.drawString(this.font, "_", cursorX, y, textColor, false);
                } else {
                    graphics.fill(cursorX, y - 1, cursorX + 1, y + this.font.lineHeight, textColor);
                }
            }

            if (isActive && field != null) {
                int cp = field.getCursorPos();
                int sp = field.getSelectionPos();
                if (sp != cp) {
                    int s1 = Math.min(cp, sp), s2 = Math.max(cp, sp);
                    int textWidth2 = this.font.width(text);
                    int x1 = cx - textWidth2 / 2 + this.font.width(text.substring(0, s1));
                    int x2 = cx - textWidth2 / 2 + this.font.width(text.substring(0, s2));
                    graphics.textHighlight(x1, y, x2, y + this.font.lineHeight, true);
                }
            }
        } else if (showCursor) {
            graphics.drawString(this.font, "_", cx, y, textColor, false);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            this.minecraft.setScreen(null);
            return true;
        }
        // Enter moves to line 2, or closes if already on line 2
        if (event.isConfirmation()) {
            if (activeLine < 1) {
                activeLine = 1;
            } else {
                this.minecraft.setScreen(null);
            }
            return true;
        }

        // Tab/Up/Down arrow switches active line
        if (event.isCycleFocus() || event.isDown() || event.isUp()) {
            activeLine = (activeLine + 1) % 2;
            return true;
        }

        TextFieldHelper active = activeField();
        return active != null && active.keyPressed(event) || super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        TextFieldHelper active = activeField();
        if (active != null) {
            active.charTyped(event);
        }
        return true;
    }

    @Override
    public void removed() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.send(new PacketUpdateStreetSign(
                    this.streetSignBE.getBlockPos(),
                    this.line1.trim(),
                    this.line2.trim(),
                    this.colorIndex,
                    this.streetSignBE.getTextColor()));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
