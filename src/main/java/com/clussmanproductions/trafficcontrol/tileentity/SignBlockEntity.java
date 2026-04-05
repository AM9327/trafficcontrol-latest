package com.clussmanproductions.trafficcontrol.tileentity;

import com.clussmanproductions.trafficcontrol.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignBlockEntity extends RotatableBlockEntity {

    private @Nullable UUID signId = null;
    private List<String> textLines = new ArrayList<>();

    public SignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_ENTITY.get(), pos, state);
    }

    public @Nullable UUID getSignId() { return signId; }

    public void setSignId(@Nullable UUID signId) {
        this.signId = signId;
        setChanged();
    }

    public String getTextLine(int index) {
        if (index >= 0 && index < textLines.size()) return textLines.get(index);
        return "";
    }

    public void setTextLine(int index, String text) {
        while (textLines.size() <= index) textLines.add("");
        textLines.set(index, text);
        setChanged();
    }

    public void setTextLines(List<String> lines) {
        this.textLines = new ArrayList<>(lines);
        setChanged();
    }

    public List<String> getTextLines() { return textLines; }

    public void clearTextLines() {
        textLines.clear();
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (signId != null) {
            output.putString("signId", signId.toString());
        }
        output.putInt("textLineCount", textLines.size());
        for (int i = 0; i < textLines.size(); i++) {
            output.putString("text" + i, textLines.get(i));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getString("signId").ifPresent(s -> signId = UUID.fromString(s));
        textLines.clear();
        input.getInt("textLineCount").ifPresent(count -> {
            for (int i = 0; i < count; i++) {
                final int idx = i;
                input.getString("text" + idx).ifPresent(textLines::add);
            }
        });
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (signId != null) {
            tag.putString("signId", signId.toString());
        }
        tag.putInt("textLineCount", textLines.size());
        for (int i = 0; i < textLines.size(); i++) {
            tag.putString("text" + i, textLines.get(i));
        }
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
