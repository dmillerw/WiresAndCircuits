package me.dmillerw.circuit.block.cable;

import me.dmillerw.circuit.block.BlockRegistry;
import me.dmillerw.circuit.lib.property.EnumConnectionType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author dmillerw
 */
public class TileCable extends TileEntity {

    private EnumConnectionType[] connectionMap = new EnumConnectionType[6];
    public TileCable() {
        Arrays.fill(connectionMap, EnumConnectionType.NONE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);

        for (int i=0; i<connectionMap.length; i++) {
            compound.setInteger("connectionMap#" + i, connectionMap[i].ordinal());
        }

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        for (int i=0; i<connectionMap.length; i++) {
            connectionMap[i] = EnumConnectionType.getValues()[compound.getInteger("connectionMap#" + i)];
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        worldObj.markBlockRangeForRenderUpdate(pos, pos);
    }

    public void updateState() {
        if (!worldObj.isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                final IBlockState state = worldObj.getBlockState(pos.offset(facing));

                if (state.getBlock() == BlockRegistry.cable) {
                    connectionMap[facing.ordinal()] = EnumConnectionType.CABLE;
                } else if (state.getBlock() == Blocks.GLASS) {
                    connectionMap[facing.ordinal()] = EnumConnectionType.BLOCK;
                } else {
                    connectionMap[facing.ordinal()] = EnumConnectionType.NONE;
                }
            }
        }
        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
    }

    public boolean isConnected(EnumFacing facing) {
        return getConnectionType(facing) != EnumConnectionType.NONE;
    }

    public EnumConnectionType getConnectionType(EnumFacing facing) {
        return connectionMap[facing.ordinal()];
    }

    public IBlockState getRenderBlockstate(IBlockState state) {
        IExtendedBlockState eState = (IExtendedBlockState) state;

        for (int i=0; i<connectionMap.length; i++) {
            eState = eState.withProperty(BlockCable.PROPERTIES[i], connectionMap[i]);
        }

        return eState;
    }
}
