package net.fearsredemption.fearsmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ResonanceSmelterPartBlockEntity extends BlockEntity {
    private static final String CONTROLLER_X_KEY = "controller_x";
    private static final String CONTROLLER_Y_KEY = "controller_y";
    private static final String CONTROLLER_Z_KEY = "controller_z";

    private BlockPos controllerPos;

    public ResonanceSmelterPartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_SMELTER_PART, pos, state);
    }

    public BlockPos controllerPos() {
        return controllerPos;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos.immutable();
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (controllerPos != null) {
            output.putInt(CONTROLLER_X_KEY, controllerPos.getX());
            output.putInt(CONTROLLER_Y_KEY, controllerPos.getY());
            output.putInt(CONTROLLER_Z_KEY, controllerPos.getZ());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getInt(CONTROLLER_X_KEY).isPresent()
                && input.getInt(CONTROLLER_Y_KEY).isPresent()
                && input.getInt(CONTROLLER_Z_KEY).isPresent()) {
            controllerPos = new BlockPos(
                    input.getIntOr(CONTROLLER_X_KEY, 0),
                    input.getIntOr(CONTROLLER_Y_KEY, 0),
                    input.getIntOr(CONTROLLER_Z_KEY, 0)
            );
        } else {
            controllerPos = null;
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveCustomOnly(provider);
    }
}
