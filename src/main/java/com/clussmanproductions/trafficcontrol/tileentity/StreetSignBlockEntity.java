package com.clussmanproductions.trafficcontrol.tileentity;

import com.clussmanproductions.trafficcontrol.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class StreetSignBlockEntity extends RotatableBlockEntity {

    public static final int MAX_SIGNS = 4;

    private final String[] texts = new String[MAX_SIGNS];   // line 1 per plate
    private final String[] texts2 = new String[MAX_SIGNS];  // line 2 per plate
    private final int[] colorIndices = new int[MAX_SIGNS]; // 0=Green, 1=Red, 2=Blue, 3=Yellow
    private final int[] rotations = new int[MAX_SIGNS];    // 0-15 per plate
    private int signCount = 0;
    private int textColor = 0xFFFFFF; // RGB (no alpha), shared across all signs
    private boolean glowingText = false;

    public StreetSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STREET_SIGN_ENTITY.get(), pos, state);
        for (int i = 0; i < MAX_SIGNS; i++) {
            texts[i] = "";
            texts2[i] = "";
            colorIndices[i] = 0;
            rotations[i] = 0;
        }
    }

    public int getSignCount() { return signCount; }

    /** Add a new sign plate. Returns the index, or -1 if full. */
    public int addSign(int colorIndex) {
        if (signCount >= MAX_SIGNS) return -1;
        int idx = signCount;
        texts[idx] = "";
        texts2[idx] = "";
        colorIndices[idx] = colorIndex;
        // New plates inherit the block's current rotation
        if (level != null && !level.isClientSide()) {
            rotations[idx] = getBlockState().getValue(
                    net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16);
        }
        signCount++;
        setChanged();
        return idx;
    }

    public String getText(int index) {
        return index >= 0 && index < MAX_SIGNS ? texts[index] : "";
    }

    public String getText2(int index) {
        return index >= 0 && index < MAX_SIGNS ? texts2[index] : "";
    }

    public void setText(int index, String text) {
        if (index >= 0 && index < MAX_SIGNS) {
            texts[index] = text;
            setChanged();
        }
    }

    public void setText2(int index, String text) {
        if (index >= 0 && index < MAX_SIGNS) {
            texts2[index] = text;
            setChanged();
        }
    }

    public int getColorIndex(int index) {
        return index >= 0 && index < MAX_SIGNS ? colorIndices[index] : 0;
    }

    public void setColorIndex(int index, int colorIndex) {
        if (index >= 0 && index < MAX_SIGNS && colorIndex >= 0 && colorIndex <= 3) {
            colorIndices[index] = colorIndex;
            setChanged();
        }
    }

    public int getRotation(int index) {
        return index >= 0 && index < MAX_SIGNS ? rotations[index] : 0;
    }

    public void setRotation(int index, int rotation) {
        if (index >= 0 && index < MAX_SIGNS) {
            rotations[index] = rotation & 15;
            setChanged();
        }
    }

    public int getTextColor() { return this.textColor; }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        setChanged();
    }

    public boolean hasGlowingText() { return glowingText; }

    public void setGlowingText(boolean glowing) {
        this.glowingText = glowing;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("signCount", signCount);
        for (int i = 0; i < signCount; i++) {
            output.putString("text" + i, texts[i]);
            output.putString("line2_" + i, texts2[i]);
            output.putInt("color" + i, colorIndices[i]);
            output.putInt("rot" + i, rotations[i]);
        }
        output.putInt("textColor", textColor);
        output.putBoolean("glowingText", glowingText);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        signCount = input.getIntOr("signCount", 0);
        // Backwards compat: always create at least 1 plate for old signs
        if (signCount == 0) {
            signCount = 1;
            input.getString("text").ifPresent(t -> texts[0] = t);
            input.getString("text1").ifPresent(t -> texts[0] = t);
            input.getInt("colorIndex").ifPresent(c -> colorIndices[0] = c);
            input.getString("text2").ifPresent(t -> {
                if (!t.isEmpty()) {
                    signCount = 2;
                    texts[1] = t;
                    colorIndices[1] = colorIndices[0];
                }
            });
        } else {
            for (int i = 0; i < signCount; i++) {
                final int idx = i;
                input.getString("text" + i).ifPresent(t -> texts[idx] = t);
                input.getString("line2_" + i).ifPresent(t -> texts2[idx] = t);
                input.getInt("color" + i).ifPresent(c -> colorIndices[idx] = c);
                input.getInt("rot" + i).ifPresent(r -> rotations[idx] = r & 15);
            }
        }
        input.getInt("textColor").ifPresent(v -> this.textColor = v);
        this.glowingText = input.getBooleanOr("glowingText", false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("signCount", signCount);
        for (int i = 0; i < signCount; i++) {
            tag.putString("text" + i, texts[i]);
            tag.putString("line2_" + i, texts2[i]);
            tag.putInt("color" + i, colorIndices[i]);
            tag.putInt("rot" + i, rotations[i]);
        }
        tag.putInt("textColor", textColor);
        tag.putBoolean("glowingText", glowingText);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
