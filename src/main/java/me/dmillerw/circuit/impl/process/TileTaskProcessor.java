package me.dmillerw.circuit.impl.process;

import com.google.common.collect.Sets;
import me.dmillerw.circuit.impl.grid.TileGridOwner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.TreeSet;

/**
 * @author dmillerw
 */
public class TileTaskProcessor extends TileGridOwner {

    private TreeSet<BlockPos> instructions;

    @Override
    public void reanalayze() {
        super.reanalayze();

        instructions = Sets.newTreeSet();

        for (BlockPos pos : cables) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                IBlockState state = worldObj.getBlockState(pos.offset(facing));

                if (!state.getBlock().isAir(state, worldObj, pos)) {
                    instructions.add(pos);
                }
            }
        }
    }
}
