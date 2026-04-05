package com.clussmanproductions.trafficcontrol.signs;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.UUID;

public class Sign {
    public static final UUID DEFAULT_ERROR_SIGN = UUID.fromString("153d917f-7f23-48a6-97da-a66185a9e9d6");
    public static final UUID DEFAULT_BLANK_SIGN = UUID.fromString("a71d3bd4-e886-47bd-89e3-9b69cde978ce");

    private final UUID id;
    private final Identifier frontTexture;
    private final Identifier backTexture;
    private final String name;
    private final int variant;
    private final String type;
    private final String tooltip;
    private final String note;
    private final boolean halfHeight;
    private final ImmutableList<TextLine> textLines;

    public Sign(UUID id, Identifier frontTexture, Identifier backTexture,
                String name, int variant, String type, String tooltip, String note,
                boolean halfHeight, ArrayList<TextLine> textLines) {
        this.id = id;
        this.frontTexture = frontTexture;
        this.backTexture = backTexture;
        this.name = name;
        this.variant = variant;
        this.type = type;
        this.tooltip = tooltip;
        this.note = note;
        this.halfHeight = halfHeight;
        this.textLines = ImmutableList.copyOf(textLines);
    }

    public UUID getID() { return id; }
    public String getName() { return name; }
    public int getVariant() { return variant; }
    public String getType() { return type; }
    public Identifier getFrontTexture() { return frontTexture; }
    public Identifier getBackTexture() { return backTexture; }
    public String getTooltip() { return tooltip; }
    public String getNote() { return note; }
    public boolean getHalfHeight() { return halfHeight; }
    public ImmutableList<TextLine> getTextLines() { return textLines; }

    public static class TextLine {
        private final String label;
        private final double x, y, width, xScale, yScale;
        private final int maxLength, color;
        private final SignHorizontalAlignment hAlign;
        private final SignVerticalAlignment vAlign;

        public TextLine(String label, double x, double y, double width,
                        double xScale, double yScale, int maxLength, int color,
                        SignHorizontalAlignment hAlign, SignVerticalAlignment vAlign) {
            this.label = label; this.x = x; this.y = y; this.width = width;
            this.xScale = xScale; this.yScale = yScale; this.maxLength = maxLength;
            this.color = color; this.hAlign = hAlign; this.vAlign = vAlign;
        }

        public String getLabel() { return label; }
        public double getX() { return x; }
        public double getY() { return y; }
        public double getWidth() { return width; }
        public double getScaleAdjustedWidth() { return width * (1 / getXScale()); }
        public double getXScale() { return xScale; }
        public double getYScale() { return yScale; }
        public int getMaxLength() { return maxLength; }
        public int getColor() { return color; }
        public SignHorizontalAlignment gethAlign() { return hAlign; }
        public SignVerticalAlignment getvAlign() { return vAlign; }
    }
}
