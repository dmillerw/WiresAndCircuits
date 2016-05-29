package me.dmillerw.circuit.block;

import me.dmillerw.circuit.block.cable.BlockCable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dmillerw
 */
public class BlockRegistry {

    public static BlockCable cable;

    public static void initialize() {
        cable = new BlockCable();
    }

    @SideOnly(Side.CLIENT)
    public static void initializeBlockModels() {
        cable.initializeBlockModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initializeItemModels() {
        cable.initializeItemModel();
    }
}
