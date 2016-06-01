package me.dmillerw.circuit.impl.grid;

import com.google.common.collect.Sets;
import me.dmillerw.circuit.api.grid.IGridOwner;
import me.dmillerw.circuit.block.BlockRegistry;
import me.dmillerw.circuit.block.cable.TileCable;
import me.dmillerw.circuit.block.core.TileCore;
import me.dmillerw.circuit.util.PathFinder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

/**
 * @author dmillerw
 */
public class TileGridOwner extends TileCore implements ITickable, IGridOwner {

    public Set<BlockPos> cables = Sets.newHashSet();
    private boolean analyzedOnLoad = false;

    @Override
    public void update() {
        if (worldObj != null && !worldObj.isRemote && !analyzedOnLoad) {
            reanalayze();
            analyzedOnLoad = true;
        }
    }

    @Override
    public void reanalayze() {
        if (!worldObj.isRemote) {
            PathFinder cableFinder = new PathFinder(worldObj, pos);
            cableFinder.find(true, (pos, face) -> {
                IBlockState state = worldObj.getBlockState(pos);
                return state.getBlock() == BlockRegistry.cable;
            });

            for (BlockPos p : cableFinder.getConnectedBlocks()) {
                for (EnumFacing e : EnumFacing.VALUES) {
                    if (p.offset(e).equals(getPos()))
                        continue;

                    //TODO: Somehow properly handle multiple grid-owners on the same grid
                }
            }

            Set<BlockPos> newCables = Sets.newHashSet();
            cableFinder.forEach(newCables::add);

            cables.forEach((pos) -> {
                if (!newCables.contains(pos)) {
                    TileCable cable = (TileCable) worldObj.getTileEntity(pos);
                    if (cable != null) cable.setGridOwner(null);
                }
            });

            cables.clear();
            cables.addAll(newCables);

            cables.forEach((pos) -> {
                TileCable cable = (TileCable) worldObj.getTileEntity(pos);
                if (cable != null) cable.setGridOwner(this);
            });
        }
    }

    @Override
    public void destroy() {
        if (!worldObj.isRemote) {
            cables.forEach((pos) -> {
                TileCable cable = (TileCable) worldObj.getTileEntity(pos);
                if (cable != null) cable.setGridOwner(null);
            });
        }
    }
}
