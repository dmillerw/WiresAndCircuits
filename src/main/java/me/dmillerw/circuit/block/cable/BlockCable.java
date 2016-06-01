package me.dmillerw.circuit.block.cable;

import me.dmillerw.circuit.api.grid.IGridOwner;
import me.dmillerw.circuit.block.core.BlockTileCore;
import me.dmillerw.circuit.block.core.TileCore;
import me.dmillerw.circuit.lib.ModInfo;
import me.dmillerw.circuit.lib.property.UnlistedBoolean;
import me.dmillerw.circuit.lib.property.UnlistedConnectionType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class BlockCable extends BlockTileCore {

    public static boolean ignoreNeighborUpdates = false;

    public static final double CABLE_SIZE = 0.4D;
    public static final double CONNECTOR_SIZE = 0.3D;
    public static final double CONNECTOR_DEPTH = 0.2D;

    public static final String NAME = "cable";

    public static final UnlistedBoolean CONNECTED = new UnlistedBoolean("connected");

    public static final UnlistedConnectionType DOWN = new UnlistedConnectionType("down");
    public static final UnlistedConnectionType UP = new UnlistedConnectionType("up");
    public static final UnlistedConnectionType NORTH = new UnlistedConnectionType("north");
    public static final UnlistedConnectionType SOUTH = new UnlistedConnectionType("south");
    public static final UnlistedConnectionType WEST = new UnlistedConnectionType("west");
    public static final UnlistedConnectionType EAST = new UnlistedConnectionType("east");

    public static final UnlistedConnectionType[] PROPERTIES = new UnlistedConnectionType[] {
            DOWN, UP, NORTH, SOUTH, WEST, EAST
    };

    public BlockCable() {
        super(Material.IRON);
    }

    @Override
    public String getBlockName() {
        return NAME;
    }

    @Override
    public Class<? extends TileCore> getTile() {
        return TileCable.class;
    }

    /* MODEL HANDLING */
    @SideOnly(Side.CLIENT)
    @Override
    public void initializeBlockModel() {
        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(ModInfo.ID + ":cable");
            }
        });
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeItemModel() {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(ModInfo.ID, "cable"));
        ModelResourceLocation resourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, resourceLocation);
    }
    /* END MODEL HANDLING */

    /* STATE HANDLING */
    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {
                DOWN, UP, NORTH, SOUTH, WEST, EAST, CONNECTED
        });
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileCable cable = (TileCable) world.getTileEntity(pos);
        return cable.getRenderBlockstate(state);
    }
    /* END STATE HANDLING */

    /* RENDER HANDLING */
    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }
    /* END RENDER HANDLING */

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileCable cable = (TileCable) worldIn.getTileEntity(pos);
            if (cable != null) {
                cable.updateState();
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        // There must be a better way to handle this
        if (ignoreNeighborUpdates)
            return;

        if (!worldIn.isRemote) {
            TileCable cable = (TileCable) worldIn.getTileEntity(pos);
            if (cable != null) {
                cable.updateState();

                IGridOwner owner = cable.getGridOwner();
                if (owner != null) {
                    owner.reanalayze();
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        IGridOwner owner = null;
        if (!worldIn.isRemote) {
            TileCable cable = (TileCable) worldIn.getTileEntity(pos);
            if (cable != null) {
                owner = cable.getGridOwner();
            }
        }

        super.breakBlock(worldIn, pos, state);

        if (owner != null)
            owner.reanalayze();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileCable tile = (TileCable) source.getTileEntity(pos);
        AxisAlignedBB aabb = new AxisAlignedBB(.3, .3, .3, .7, .7, .7);

        if (tile != null) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                if (tile.isConnected(facing)) {
                    aabb = aabb.addCoord(0.3 * facing.getFrontOffsetX(), 0.3 * facing.getFrontOffsetY(), 0.3 * facing.getFrontOffsetZ());
                }
            }
        }

        return aabb;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return FULL_BLOCK_AABB.expandXyz(-.25);
    }
}
