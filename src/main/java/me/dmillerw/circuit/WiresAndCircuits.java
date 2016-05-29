package me.dmillerw.circuit;

import me.dmillerw.circuit.lib.ModInfo;
import me.dmillerw.circuit.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author dmillerw
 */
@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class WiresAndCircuits {

    @Mod.Instance(ModInfo.ID)
    public static WiresAndCircuits INSTANCE;

    @SidedProxy(
            serverSide = "me.dmillerw.circuit.proxy.CommonProxy",
            clientSide = "me.dmillerw.circuit.proxy.ClientProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }
}
