package me.dmillerw.circuit.block.cpu;

import com.google.common.collect.Sets;
import me.dmillerw.circuit.api.IGroupOwner;
import me.dmillerw.circuit.api.Variable;
import me.dmillerw.circuit.block.BlockRegistry;
import me.dmillerw.circuit.block.cable.TileCable;
import me.dmillerw.circuit.block.core.TileCore;
import me.dmillerw.circuit.util.PathFinder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

/**
 * @author dmillerw
 */
public class TileCPU extends TileCore implements IGroupOwner {

    public Set<BlockPos> cables = Sets.newHashSet();

    private boolean analyzedOnLoad = false;

//    @Override
//    public void update() {
//        if (worldObj != null && !worldObj.isRemote && !analyzedOnLoad) {
//            reanalayze();
//            analyzedOnLoad = true;
//        }
//    }

    @Override
    public NBTTagCompound writeCustomTag(NBTTagCompound tag, boolean clientUpdate) {
        tag.setBoolean("dummy", true);
        return super.writeCustomTag(tag, clientUpdate);
    }

    @Override
    public void readCustomTag(NBTTagCompound tag, boolean clientUpdate) {
        super.readCustomTag(tag, clientUpdate);
        tag.getBoolean("dummy");
    }

    /* IGROUPOWNER */
    @Override
    public void reanalayze() {
        if (!worldObj.isRemote) {
            PathFinder cableFinder = new PathFinder(worldObj, pos);
            cableFinder.find(true, (pos, face) -> {
                IBlockState state = worldObj.getBlockState(pos);
                return state.getBlock() == BlockRegistry.cable;
            });

            Set<BlockPos> newCables = Sets.newHashSet();
            cableFinder.forEach(newCables::add);

            cables.forEach((pos) -> {
                if (!newCables.contains(pos)) {
                    TileCable cable = (TileCable) worldObj.getTileEntity(pos);
                    if (cable != null) cable.setGroupOwner(null);
                }
            });

            cables.clear();
            cables.addAll(newCables);

            cables.forEach((pos) -> {
                TileCable cable = (TileCable) worldObj.getTileEntity(pos);
                if (cable != null) cable.setGroupOwner(this);
            });
        }
    }

    public void destroy() {
        if (!worldObj.isRemote) {
            cables.forEach((pos) -> {
                TileCable cable = (TileCable) worldObj.getTileEntity(pos);
                if (cable != null) cable.setGroupOwner(null);
            });
        }
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[0];
    }

    @Override
    public void claimVariable(int index, BlockPos owner) {
        // NOOP
    }
}
