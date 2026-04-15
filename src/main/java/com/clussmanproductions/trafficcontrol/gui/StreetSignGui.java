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
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StreetSignGui extends Screen {

    private final StreetSignBlockEntity streetSignBE;
    private final int signCount;

    // Per-plate data (cached locally for editing)
    private final String[] line1s;
    private final String[] line2s;
    private final int[] colors;

    private int editIndex;      // which plate is active
    private int activeLine = 0; // 0 = line1, 1 = line2
    private final int[] rotations; // per-plate rotation (0-15)
    private @Nullable TextFieldHelper field1;
    private @Nullable TextFieldHelper field2;
    private int frame;

    private static final int[] SIGN_COLORS = {0xFF006400, 0xFFCC0000, 0xFF0000CC, 0xFFCCCC00};
    private static final String[] SIGN_COLOR_NAMES = {"Green", "Red", "Blue", "Yellow"};
    private static final Identifier SIGN_TEXTURE = Identifier.fromNamespaceAndPath("trafficcontrol", "textures/block/street_sign.png");
    private static final String[] DIRECTION_LABELS = {
            "N/S", "NNE/SSW", "NE/SW", "ENE/WSW",
            "E/W", "ESE/WNW", "SE/NW", "SSE/NNW",
            "N/S", "NNE/SSW", "NE/SW", "ENE/WSW",
            "E/W", "ESE/WNW", "SE/NW", "SSE/NNW"
    };

    public StreetSignGui(StreetSignBlockEntity streetSignBE) {
        super(Component.literal("Edit Street Sign"));
        this.streetSignBE = streetSignBE;
        this.signCount = Math.max(1, streetSignBE.getSignCount());
        this.editIndex = signCount - 1; // start editing newest plate

        // Cache all plate data
        this.line1s = new String[signCount];
        this.line2s = new String[signCount];
        this.colors = new int[signCount];
        this.rotations = new int[signCount];
        for (int i = 0; i < signCount; i++) {
            line1s[i] = streetSignBE.getText(i);
            line2s[i] = streetSignBE.getText2(i);
            colors[i] = streetSignBE.getColorIndex(i);
            rotations[i] = streetSignBE.getRotation(i);
        }
    }

    // Cached layout values (computed in init, used in render/mouseClicked)
    private int layoutPlateW, layoutPlateH, layoutPlateGap, layoutBaseY;

    @Override
    protected void init() {
        int cx = this.width / 2;

        rebuildFields();

        // Calculate available height and scale layout to fit
        int margin = 6;
        int btnSectionH = 20 + 6 + 20 + 6 + 20; // color row + gap + direction + gap + done
        int availableH = this.height - margin * 2 - btnSectionH - 6;

        // Default plate sizes
        int platePH = 40;
        int plateGap = 10;
        int platePW = 200;

        // Scale down plates if they don't fit
        int neededH = signCount * (platePH + plateGap);
        if (neededH > availableH) {
            // Reduce plate height and gap proportionally
            float scale = (float) availableH / neededH;
            platePH = Math.max(16, (int)(platePH * scale));
            plateGap = Math.max(2, (int)(plateGap * scale));
            platePW = Math.max(100, (int)(platePW * scale));
        }

        // Cache layout for render/mouseClicked
        layoutPlateW = platePW;
        layoutPlateH = platePH;
        layoutPlateGap = plateGap;

        int totalPlateH = signCount * (platePH + plateGap);
        int totalContentH = totalPlateH + 6 + btnSectionH;
        layoutBaseY = Math.max(margin, (this.height - totalContentH) / 2);

        int bottomOfPlates = layoutBaseY + totalPlateH;

        int btnW = 50, gap = 2;
        int rowW = 4 * btnW + 3 * gap;
        int startX = cx - rowW / 2;
        int btnY = bottomOfPlates + 6;

        // Button text colors matching sign colors (brighter for readability)
        int[] btnTextColors = {0x00AA00, 0xFF5555, 0x5555FF, 0xFFFF55};
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            Component label = Component.literal(SIGN_COLOR_NAMES[i])
                    .withStyle(style -> style.withColor(net.minecraft.network.chat.TextColor.fromRgb(btnTextColors[idx])));
            this.addRenderableWidget(Button.builder(label, b -> {
                this.colors[editIndex] = idx;
            }).bounds(startX + i * (btnW + gap), btnY, btnW, 20).build());
        }

        // Direction button (rotates the active plate only)
        this.addRenderableWidget(Button.builder(
                Component.literal(DIRECTION_LABELS[rotations[editIndex] % 16]), b -> {
            rotations[editIndex] = (rotations[editIndex] + 1) % 16;
            b.setMessage(Component.literal(DIRECTION_LABELS[rotations[editIndex] % 16]));
            // Update block entity for immediate visual feedback
            streetSignBE.setRotation(editIndex, rotations[editIndex]);
            streetSignBE.syncToClient();
        }).bounds(cx - 50, btnY + 26, 100, 20).build());

        // Done button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
            this.minecraft.setScreen(null);
        }).bounds(cx - 50, btnY + 52, 100, 20).build());
    }

    private void rebuildFields() {
        this.field1 = new TextFieldHelper(
                () -> this.line1s[editIndex],
                text -> this.line1s[editIndex] = text,
                TextFieldHelper.createClipboardGetter(this.minecraft),
                TextFieldHelper.createClipboardSetter(this.minecraft),
                text -> text.length() <= 50
        );
        this.field2 = new TextFieldHelper(
                () -> this.line2s[editIndex],
                text -> this.line2s[editIndex] = text,
                TextFieldHelper.createClipboardGetter(this.minecraft),
                TextFieldHelper.createClipboardSetter(this.minecraft),
                text -> text.length() <= 50
        );
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

        // --- All plates stacked ---
        int platePW = layoutPlateW, platePH = layoutPlateH;
        int previewBaseY = layoutBaseY;

        for (int i = signCount - 1; i >= 0; i--) {
            int py = previewBaseY + (signCount - 1 - i) * (platePH + layoutPlateGap);
            int px = cx - platePW / 2;

            // Active plate highlight border
            if (i == editIndex) {
                graphics.fill(px - 3, py - 3, px + platePW + 3, py + platePH + 3, 0xFFFFFF00);
            }
            // Sign plate texture (16x16 texture, 4 rows of 4px each)
            int texV = colors[i] * 4;
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                    SIGN_TEXTURE, px, py, 0, texV, platePW, platePH, 16, 4, 16, 16);

            int textColor = colors[i] == 3 ? 0xFF000000 : 0xFFFFFFFF;
            int lineH = this.font.lineHeight;
            int textBaseY = py + (platePH - 2 * lineH) / 2;

            // Line 1
            if (!line1s[i].isEmpty()) {
                int tw = this.font.width(line1s[i]);
                graphics.drawString(this.font, line1s[i], cx - tw / 2, textBaseY, textColor, false);
            }
            // Line 2
            if (!line2s[i].isEmpty()) {
                int tw = this.font.width(line2s[i]);
                graphics.drawString(this.font, line2s[i], cx - tw / 2, textBaseY + lineH, textColor, false);
            }

            // Cursor for active plate
            if (i == editIndex) {

                // Cursor
                boolean showCursor = this.frame / 6 % 2 == 0;
                if (showCursor) {
                    String activeText = activeLine == 0 ? line1s[i] : line2s[i];
                    TextFieldHelper field = activeField();
                    int cursorLineY = textBaseY + activeLine * lineH;
                    if (field != null) {
                        int tw = this.font.width(activeText);
                        int textX = activeText.isEmpty() ? cx : cx - tw / 2;
                        int cursorPos = field.getCursorPos();
                        String before = activeText.substring(0, Math.min(cursorPos, activeText.length()));
                        int cursorX = activeText.isEmpty() ? cx : textX + this.font.width(before);
                        if (cursorPos >= activeText.length()) {
                            graphics.drawString(this.font, "_", cursorX, cursorLineY, textColor, false);
                        } else {
                            graphics.fill(cursorX, cursorLineY - 1, cursorX + 1, cursorLineY + lineH, textColor);
                        }
                    }
                }
            }
        }

        // Color button highlight — colored border around selected button
        int cbtnW = 50, cbtnGap = 2;
        int cbtnRowW = 4 * cbtnW + 3 * cbtnGap;
        int cbtnStartX = cx - cbtnRowW / 2;
        int bottomOfPlates2 = previewBaseY + signCount * (platePH + layoutPlateGap);
        int cbtnY = bottomOfPlates2 + 6;
        int sel = colors[editIndex];
        int bx = cbtnStartX + sel * (cbtnW + cbtnGap);
        graphics.fill(bx, cbtnY + 21, bx + cbtnW, cbtnY + 24, SIGN_COLORS[sel]);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean p) {
        double mouseX = event.x();
        double mouseY = event.y();
        int cx = this.width / 2;
        int platePW = layoutPlateW, platePH = layoutPlateH;
        int previewBaseY = layoutBaseY;

        for (int i = signCount - 1; i >= 0; i--) {
            int py = previewBaseY + (signCount - 1 - i) * (platePH + layoutPlateGap);
            int px = cx - platePW / 2;
            if (mouseX >= px - 2 && mouseX <= px + platePW + 2 && mouseY >= py - 2 && mouseY <= py + platePH + 2) {
                if (i != editIndex) {
                    editIndex = i;
                    activeLine = 0;
                    rebuildWidgets();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, p);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            this.minecraft.setScreen(null);
            return true;
        }
        // Enter moves to line 2
        if (event.isConfirmation()) {
            if (activeLine < 1) {
                activeLine = 1;
            }
            return true;
        }
        // Tab cycles between plates
        if (event.isCycleFocus()) {
            editIndex = (editIndex + 1) % signCount;
            activeLine = 0;
            rebuildWidgets();
            return true;
        }
        // Up/Down switches lines within the active plate
        if (event.isUp() && activeLine > 0) {
            activeLine = 0;
            return true;
        }
        if (event.isDown() && activeLine < 1) {
            activeLine = 1;
            return true;
        }

        TextFieldHelper field = activeField();
        return field != null && field.keyPressed(event) || super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        TextFieldHelper field = activeField();
        if (field != null) {
            field.charTyped(event);
        }
        return true;
    }

    @Override
    public void removed() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            List<String> texts = new ArrayList<>();
            List<String> texts2 = new ArrayList<>();
            List<Integer> colorIndices = new ArrayList<>();
            List<Integer> rots = new ArrayList<>();
            for (int i = 0; i < signCount; i++) {
                texts.add(line1s[i].trim());
                texts2.add(line2s[i].trim());
                colorIndices.add(colors[i]);
                rots.add(rotations[i]);
            }
            connection.send(new PacketUpdateStreetSign(
                    this.streetSignBE.getBlockPos(),
                    texts, texts2, colorIndices, rots,
                    this.streetSignBE.getTextColor()));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
