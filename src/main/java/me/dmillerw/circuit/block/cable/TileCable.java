package me.dmillerw.circuit.block.cable;

import me.dmillerw.circuit.util.BitMask;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

/**
 * @author dmillerw
 */
public class TileCable extends TileEntity {

    private static final String TAG_CONNECTION_MAP = "ConnectionMap";

    private boolean[] connectionMap = new boolean[6];

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);

        BitMask bitMask = new BitMask();
        for (int i=0; i<connectionMap.length; i++) {
            if (connectionMap[i]) bitMask.enable(i);
        }
        tag.setInteger(TAG_CONNECTION_MAP, bitMask.get());

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        BitMask bitMask = new BitMask(compound.getInteger(TAG_CONNECTION_MAP));
        for (int i=0; i<connectionMap.length; i++) {
            connectionMap[i] = bitMask.isEnabled(i);
        }
    }

    public boolean isConnected(EnumFacing facing) {
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
