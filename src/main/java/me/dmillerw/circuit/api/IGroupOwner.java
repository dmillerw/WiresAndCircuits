package me.dmillerw.circuit.api;

import net.minecraft.util.math.BlockPos;

/**
 * @author dmillerw
 */
public interface IGroupOwner {

    // Only ever accessed by cables connected to this owner
    // Cables only ever need to be able to force the owner to reanalyze its children
    // or to be able to determine what variables are needed, and claim one

    public void reanalayze();
    public Variable[] getVariables();
    public void claimVariable(int index, BlockPos owner);
}
