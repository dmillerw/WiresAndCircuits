package me.dmillerw.circuit.block.core;

import me.dmillerw.circuit.lib.TabCircuit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dmillerw
 */
public abstract class BlockCore extends Block {

    public BlockCore(Material material) {
        super(material, material.getMaterialMapColor());

        setHardness(1F);
        setResistance(1F);

        setCreativeTab(TabCircuit.INSTANCE);

        setUnlocalizedName(getBlockName());
        setRegistryName(getBlockName());

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
    }

    public abstract String getBlockName();

    @SideOnly(Side.CLIENT)
    public void initializeBlockModel() {

    }

    @SideOnly(Side.CLIENT)
    public void initializeItemModel() {

    }
}
