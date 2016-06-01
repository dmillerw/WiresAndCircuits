package me.dmillerw.circuit.block.cpu;

import me.dmillerw.circuit.item.ItemRegistry;
import me.dmillerw.circuit.lib.ModInfo;
import me.dmillerw.circuit.lib.TabCircuit;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class BlockCPU extends Block implements ITileEntityProvider {

    public static final String NAME = "cpu";

    public BlockCPU() {
        super(Material.IRON);

        setHardness(1F);
        setResistance(1F);

        setCreativeTab(TabCircuit.INSTANCE);

        setUnlocalizedName(NAME);
        setRegistryName(NAME);

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TileCPU.class, ModInfo.prefix(NAME));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileCPU cpu = (TileCPU) worldIn.getTileEntity(pos);
            if (cpu != null) {
                cpu.analyze();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && heldItem.getItem() == ItemRegistry.confGun) {
            TileCPU cpu = (TileCPU) worldIn.getTileEntity(pos);
            playerIn.addChatComponentMessage(new TextComponentString("Connected Cables: " + cpu.cables.size()));
        }
        return heldItem.getItem() == ItemRegistry.confGun;
    }

    /* MODEL HANDLING */

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

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

    /* TILE HANDLING */

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (hasTileEntity(state)) {
            worldIn.removeTileEntity(pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCPU();
    }
}
