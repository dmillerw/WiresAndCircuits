package me.dmillerw.circuit.block.cpu;

import com.google.common.collect.Sets;
import me.dmillerw.circuit.block.BlockRegistry;
import me.dmillerw.circuit.util.PathFinder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

/**
 * @author dmillerw
 */
public class TileCPU extends TileEntity {

    public Set<BlockPos> cables = Sets.newHashSet();
    private Set<BlockPos> blocks = Sets.newHashSet();

    public void analyze() {
        if (!worldObj.isRemote) {
            PathFinder cableFinder = new PathFinder(worldObj, pos);
            cableFinder.find((pos, face) -> {
                IBlockState state = worldObj.getBlockState(pos);
                return state.getBlock() == BlockRegistry.cable;
            });

            cableFinder.forEach(cables::add);
        }
    }
}
