package me.dmillerw.circuit.block.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class TileCore extends TileEntity {

    public NBTTagCompound writeCustomTag(NBTTagCompound tag, boolean clientUpdate) {
        return tag;
    }

    public void readCustomTag(NBTTagCompound tag, boolean clientUpdate) {

    }

    /* OVERRIDES */
    @Override
    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomTag(compound, false);
    }

    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        return writeCustomTag(compound, false);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        super.writeToNBT(tag);
        return writeCustomTag(tag, true);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.readFromNBT(pkt.getNbtCompound());
        readCustomTag(pkt.getNbtCompound(), true);
        worldObj.markBlockRangeForRenderUpdate(pos, pos);
    }
}
