package me.dmillerw.circuit.item.tool;

import me.dmillerw.circuit.lib.TabCircuit;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author dmillerw
 */
public class ItemConfGun extends Item {

    private static final String NAME = "ConfGun";

    public ItemConfGun() {
        super();

        setMaxDamage(0);
        setMaxStackSize(1);

        setCreativeTab(TabCircuit.INSTANCE);

        setUnlocalizedName(NAME);
        setRegistryName(NAME);

        GameRegistry.register(this);
    }
}
