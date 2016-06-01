package me.dmillerw.circuit.block.cpu;

import me.dmillerw.circuit.block.core.BlockTileCore;
import me.dmillerw.circuit.block.core.TileCore;
import me.dmillerw.circuit.lib.ModInfo;
import me.dmillerw.circuit.lib.property.EnumCPUState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dmillerw
 */
public class BlockCPU extends BlockTileCore {

    public static final String NAME = "cpu";

    public static final PropertyEnum<EnumCPUState> CPU_STATE = PropertyEnum.create("state", EnumCPUState.class);

    public BlockCPU() {
        super(Material.IRON);

        setDefaultState(getBlockState().getBaseState().withProperty(CPU_STATE, EnumCPUState.OFF));
    }

    @Override
    public String getBlockName() {
        return NAME;
    }

    @Override
    public Class<? extends TileCore> getTile() {
        return TileCPU.class;
    }

    /* MODEL HANDLING */
    @SideOnly(Side.CLIENT)
    public void initializeBlockModel() {
        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(ModInfo.ID + ":cpu");
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public void initializeItemModel() {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(ModInfo.ID, "cpu"));
        ModelResourceLocation resourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, resourceLocation);
    }
    /* END MODEL HANDLING */

    /* STATE HANDLING */
    @Override
    public BlockStateContainer getBlockState() {
        return new BlockStateContainer(this, CPU_STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CPU_STATE, EnumCPUState.getValues()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CPU_STATE).ordinal();
    }
    /* END STATE HANDLING */

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileCPU cpu = (TileCPU) worldIn.getTileEntity(pos);
            if (cpu != null) cpu.reanalayze();
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileCPU cpu = (TileCPU) worldIn.getTileEntity(pos);
            if (cpu != null) cpu.destroy();
        }
    }
}
