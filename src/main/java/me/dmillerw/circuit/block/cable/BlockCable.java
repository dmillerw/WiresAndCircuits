package me.dmillerw.circuit.block.cable;

import me.dmillerw.circuit.lib.ModInfo;
import me.dmillerw.circuit.lib.TabCircuit;
import me.dmillerw.circuit.lib.property.UnlistedConnectionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class BlockCable extends BlockContainer {

    public static final double CABLE_SIZE = 0.4D;
    public static final double CONNECTOR_SIZE = 0.3D;
    public static final double CONNECTOR_DEPTH = 0.2D;

    public static final String NAME = "cable";

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

        setHardness(1F);
        setResistance(1F);

        setCreativeTab(TabCircuit.INSTANCE);

        setUnlocalizedName(NAME);
        setRegistryName(NAME);

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TileCable.class, ModInfo.prefix(NAME));
    }

    /* MODEL HANDLING */

    @SideOnly(Side.CLIENT)
    public void initializeBlockModel() {
        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(ModInfo.ID + ":cable");
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public void initializeItemModel() {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(ModInfo.ID, "cable"));
        ModelResourceLocation resourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, resourceLocation);
    }

    /* END MODEL HANDLING */

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {
                DOWN, UP, NORTH, SOUTH, WEST, EAST
        });
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileCable cable = (TileCable) world.getTileEntity(pos);
        return cable.getRenderBlockstate(state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        ((TileCable)worldIn.getTileEntity(pos)).updateState();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        ((TileCable)worldIn.getTileEntity(pos)).updateState();
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

    /* RENDER HANDLING */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    /* END RENDER HANDLING */

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCable();
    }
}
