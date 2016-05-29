package me.dmillerw.circuit.proxy;

import me.dmillerw.circuit.block.BlockRegistry;
import me.dmillerw.circuit.item.ItemRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author dmillerw
 */
public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BlockRegistry.initialize();
        ItemRegistry.initialize();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
