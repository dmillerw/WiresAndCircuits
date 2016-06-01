package me.dmillerw.circuit.block.core;

import me.dmillerw.circuit.lib.ModInfo;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author dmillerw
 */
public abstract class BlockTileCore extends BlockCore implements ITileEntityProvider {

    public BlockTileCore(Material material) {
        super(material);

        GameRegistry.registerTileEntity(getTile(), ModInfo.prefix(getBlockName()));
    }

    @Override
    public abstract String getBlockName();
    public abstract Class<? extends TileCore> getTile();

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return getTile().newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
