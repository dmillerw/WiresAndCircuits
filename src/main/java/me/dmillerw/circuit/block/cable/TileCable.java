package me.dmillerw.circuit.block.cable;

import me.dmillerw.circuit.api.grid.IGridOwner;
import me.dmillerw.circuit.block.BlockRegistry;
import me.dmillerw.circuit.block.core.TileCore;
import me.dmillerw.circuit.lib.property.EnumConnectionType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Arrays;

/**
 * @author dmillerw
 */
public class TileCable extends TileCore {

    private boolean clientHasOwner = false;

    private IGridOwner groupOwner;

    private EnumConnectionType[] connectionMap = new EnumConnectionType[6];

    public TileCable() {
        Arrays.fill(connectionMap, EnumConnectionType.NONE);
    }

    /* NBT */
    @Override
    public NBTTagCompound writeCustomTag(NBTTagCompound tag, boolean clientUpdate) {
        super.writeCustomTag(tag, clientUpdate);

        for (int i=0; i<connectionMap.length; i++) {
            tag.setInteger("connectionMap#" + i, connectionMap[i].ordinal());
        }

        if (clientUpdate)
            tag.setBoolean("hasOwner", groupOwner != null);

        return tag;
    }

    @Override
    public void readCustomTag(NBTTagCompound tag, boolean clientUpdate) {
        super.readCustomTag(tag, clientUpdate);

        for (int i=0; i<connectionMap.length; i++) {
            connectionMap[i] = EnumConnectionType.getValues()[tag.getInteger("connectionMap#" + i)];
        }

        if (clientUpdate)
            clientHasOwner = tag.getBoolean("hasOwner");
    }

    /* STATE HANDLING */
    public void updateState() {
        if (!worldObj.isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                final IBlockState state = worldObj.getBlockState(pos.offset(facing));

                if (state.getBlock() == BlockRegistry.cable) {
                    connectionMap[facing.ordinal()] = EnumConnectionType.CABLE;
                } else if (state.getBlock() == BlockRegistry.cpu) {
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
        eState = eState.withProperty(BlockCable.CONNECTED, clientHasOwner);

        return eState;
    }

    /* OWNERSHIP */
    public IGridOwner getGridOwner() {
        return groupOwner;
    }

    public void setGridOwner(IGridOwner groupOwner) {
        this.groupOwner = groupOwner;

        // There must be a better way to handle this
        BlockCable.ignoreNeighborUpdates = true;
        markForUpdate();
        BlockCable.ignoreNeighborUpdates = false;
    }
}
