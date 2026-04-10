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

    private String text1 = "";
    private String text2 = "";
    private int colorIndex = 0; // 0=Green, 1=Red, 2=Blue, 3=Yellow
    private int textColor = 0xFFFFFF; // RGB (no alpha), default white
    private boolean glowingText = false;

    public StreetSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STREET_SIGN_ENTITY.get(), pos, state);
    }

    public String getText1() { return text1; }
    public String getText2() { return text2; }

    public void setText1(String text) {
        this.text1 = text;
        setChanged();
    }

    public void setText2(String text) {
        this.text2 = text;
        setChanged();
    }

    public int getColorIndex() { return colorIndex; }

    public void setColorIndex(int colorIndex) {
        if (colorIndex >= 0 && colorIndex <= 3) {
            this.colorIndex = colorIndex;
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
        output.putString("text1", text1);
        output.putString("text2", text2);
        output.putInt("colorIndex", colorIndex);
        output.putInt("textColor", textColor);
        output.putBoolean("glowingText", glowingText);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // Backwards compat: read old "text" key into text1
        input.getString("text").ifPresent(t -> this.text1 = t);
        input.getString("text1").ifPresent(t -> this.text1 = t);
        input.getString("text2").ifPresent(t -> this.text2 = t);
        input.getInt("colorIndex").ifPresent(i -> this.colorIndex = i);
        input.getInt("textColor").ifPresent(v -> this.textColor = v);
        this.glowingText = input.getBooleanOr("glowingText", false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putString("text1", text1);
        tag.putString("text2", text2);
        tag.putInt("colorIndex", colorIndex);
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
