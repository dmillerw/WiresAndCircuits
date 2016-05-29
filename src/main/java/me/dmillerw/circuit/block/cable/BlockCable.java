package me.dmillerw.circuit.block.cable;

import me.dmillerw.circuit.lib.ModInfo;
import me.dmillerw.circuit.lib.TabCircuit;
import me.dmillerw.circuit.lib.property.UnlistedBoolean;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dmillerw
 */
public class BlockCable extends BlockContainer {

    public static final String NAME = "cable";

    public static final UnlistedBoolean DOWN = new UnlistedBoolean("down");
    public static final UnlistedBoolean UP = new UnlistedBoolean("up");
    public static final UnlistedBoolean NORTH = new UnlistedBoolean("north");
    public static final UnlistedBoolean SOUTH = new UnlistedBoolean("south");
    public static final UnlistedBoolean WEST = new UnlistedBoolean("west");
    public static final UnlistedBoolean EAST = new UnlistedBoolean("east");

    public static final IUnlistedProperty[] PROPERTIES = new IUnlistedProperty[] {
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
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return new ModelResourceLocation(getRegistryName(), "normal");
            }
        };
        ModelLoader.setCustomStateMapper(this, ignoreState);
    }

    @SideOnly(Side.CLIENT)
    public void initializeItemModel() {
        Item itemBlock = Item.REGISTRY.getObject(ModInfo.resourceLocation(NAME));
        ModelResourceLocation resourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlock, 0, resourceLocation);
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

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCable();
    }
}
