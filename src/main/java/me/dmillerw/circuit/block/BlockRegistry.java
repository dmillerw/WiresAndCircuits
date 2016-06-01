package me.dmillerw.circuit.block;

import me.dmillerw.circuit.block.cable.BlockCable;
import me.dmillerw.circuit.block.cpu.BlockCPU;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dmillerw
 */
public class BlockRegistry {

    public static BlockCable cable;
    public static BlockCPU cpu;

    public static void initialize() {
        cable = new BlockCable();
        cpu = new BlockCPU();
    }

    @SideOnly(Side.CLIENT)
    public static void initializeBlockModels() {
        cable.initializeBlockModel();
        cpu.initializeBlockModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initializeItemModels() {
        cable.initializeItemModel();
        cpu.initializeItemModel();
    }
}
