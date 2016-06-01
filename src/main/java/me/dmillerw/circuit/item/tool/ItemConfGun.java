package me.dmillerw.circuit.item.tool;

import me.dmillerw.circuit.block.BlockRegistry;
import me.dmillerw.circuit.block.cable.TileCable;
import me.dmillerw.circuit.block.cpu.TileCPU;
import me.dmillerw.circuit.lib.TabCircuit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author dmillerw
 */
public class ItemConfGun extends Item {

    private static final int DEBUG = 0;
    private static final int CONFIGURE = 1;

    private static final String KEY_MODE = "Mode";

    public static int getMode(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) {
            itemStack.setTagCompound(tag = new NBTTagCompound());
        }
        return tag.getInteger(KEY_MODE);
    }

    public static void setMode(ItemStack itemStack, int mode) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) {
            itemStack.setTagCompound(tag = new NBTTagCompound());
        }

        tag.setInteger(KEY_MODE, mode);

        itemStack.setTagCompound(tag);
    }

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

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                int mode = getMode(itemStack);
                if (mode == 0)
                    mode = 1;
                else
                    mode = 0;

                player.addChatComponentMessage(new TextComponentTranslation("circuit.msg.conf.mode_" + mode));

                setMode(itemStack, mode);

                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, itemStack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            IBlockState state = world.getBlockState(pos);

            if (getMode(stack) == DEBUG) {
                if (state.getBlock() == BlockRegistry.cable) {
                    TileCable cable = (TileCable) world.getTileEntity(pos);

                    String owner = "null";
                    if (cable != null && cable.getGridOwner() != null) {
                        owner = ((TileEntity)cable.getGridOwner()).getPos().toString();
                    }

                    player.addChatComponentMessage(new TextComponentString("IGridOwner: " + owner));
                    return EnumActionResult.SUCCESS;
                } else if (state.getBlock() == BlockRegistry.cpu) {
                    TileCPU cpu = (TileCPU) world.getTileEntity(pos);
                    player.addChatComponentMessage(new TextComponentString("Cables: " + cpu.cables.size()));
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.PASS;
    }
}
