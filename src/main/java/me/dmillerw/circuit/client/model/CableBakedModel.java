package me.dmillerw.circuit.client.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import me.dmillerw.circuit.block.cable.BlockCable;
import me.dmillerw.circuit.lib.ModInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Collections;
import java.util.List;

/**
 * @author dmillerw
 */
public class CableBakedModel implements IBakedModel {

    private TextureAtlasSprite sprite;
    private TextureAtlasSprite spriteEnd;

    private VertexFormat format;

    public CableBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        this.sprite = bakedTextureGetter.apply(ModInfo.resourceLocation("blocks/cable"));
        this.spriteEnd = bakedTextureGetter.apply(ModInfo.resourceLocation("blocks/cable_end"));
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u, float v) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)x, (float)y, (float)z, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite) {
        Vec3d normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        normal = normal.normalize();

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.xCoord, v1.yCoord, v1.zCoord, 0, 0);
        putVertex(builder, normal, v2.xCoord, v2.yCoord, v2.zCoord, 0, 16);
        putVertex(builder, normal, v3.xCoord, v3.yCoord, v3.zCoord, 16, 16);
        putVertex(builder, normal, v4.xCoord, v4.yCoord, v4.zCoord, 16, 0);
        return builder.build();
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null) {
            return Collections.emptyList();
        }

        IExtendedBlockState eState = (IExtendedBlockState) state;
        Boolean north = eState.getValue(BlockCable.NORTH);
        Boolean south = eState.getValue(BlockCable.SOUTH);
        Boolean west = eState.getValue(BlockCable.WEST);
        Boolean east = eState.getValue(BlockCable.EAST);
        Boolean up = eState.getValue(BlockCable.UP);
        Boolean down = eState.getValue(BlockCable.DOWN);

        List<BakedQuad> quads = Lists.newArrayList();
        double o = .4;

        if (up) {
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1, o), new Vec3d(1 - o, 1, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1, 1 - o), new Vec3d(o, 1, o), new Vec3d(o, 1 - o, o), sprite));
            quads.add(createQuad(new Vec3d(o, 1, o), new Vec3d(1 - o, 1, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(o, 1 - o, o), sprite));
            quads.add(createQuad(new Vec3d(o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1, 1 - o), new Vec3d(o, 1, 1 - o), sprite));
        } else {
            quads.add(createQuad(new Vec3d(o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, o), new Vec3d(o, 1 - o, o), spriteEnd));
        }

        if (down) {
            quads.add(createQuad(new Vec3d(1 - o, 0, o), new Vec3d(1 - o, o, o), new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, 0, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(o, 0, 1 - o), new Vec3d(o, o, 1 - o), new Vec3d(o, o, o), new Vec3d(o, 0, o), sprite));
            quads.add(createQuad(new Vec3d(o, o, o), new Vec3d(1 - o, o, o), new Vec3d(1 - o, 0, o), new Vec3d(o, 0, o), sprite));
            quads.add(createQuad(new Vec3d(o, 0, 1 - o), new Vec3d(1 - o, 0, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(o, o, 1 - o), sprite));
        } else {
            quads.add(createQuad(new Vec3d(o, o, o), new Vec3d(1 - o, o, o), new Vec3d(1 - o, o, 1 - o), new Vec3d(o, o, 1 - o), spriteEnd));
        }

        if (east) {
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1, 1 - o, 1 - o), new Vec3d(1, 1 - o, o), new Vec3d(1 - o, 1 - o, o), sprite));
            quads.add(createQuad(new Vec3d(1 - o, o, o), new Vec3d(1, o, o), new Vec3d(1, o, 1 - o), new Vec3d(1 - o, o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, o), new Vec3d(1, 1 - o, o), new Vec3d(1, o, o), new Vec3d(1 - o, o, o), sprite));
            quads.add(createQuad(new Vec3d(1 - o, o, 1 - o), new Vec3d(1, o, 1 - o), new Vec3d(1, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), sprite));
        } else {
            quads.add(createQuad(new Vec3d(1 - o, o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, o, 1 - o), spriteEnd));
        }

        if (west) {
            quads.add(createQuad(new Vec3d(0, 1 - o, 1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1 - o, o), new Vec3d(0, 1 - o, o), sprite));
            quads.add(createQuad(new Vec3d(0, o, o), new Vec3d(o, o, o), new Vec3d(o, o, 1 - o), new Vec3d(0, o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(0, 1 - o, o), new Vec3d(o, 1 - o, o), new Vec3d(o, o, o), new Vec3d(0, o, o), sprite));
            quads.add(createQuad(new Vec3d(0, o, 1 - o), new Vec3d(o, o, 1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(0, 1 - o, 1 - o), sprite));
        } else {
            quads.add(createQuad(new Vec3d(o, o, 1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1 - o, o), new Vec3d(o, o, o), spriteEnd));
        }

        if (north) {
            quads.add(createQuad(new Vec3d(o, 1 - o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, 0), new Vec3d(o, 1 - o, 0), sprite));
            quads.add(createQuad(new Vec3d(o, o, 0), new Vec3d(1 - o, o, 0), new Vec3d(1 - o, o, o), new Vec3d(o, o, o), sprite));
            quads.add(createQuad(new Vec3d(1 - o, o, 0), new Vec3d(1 - o, 1 - o, 0), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, o, o), sprite));
            quads.add(createQuad(new Vec3d(o, o, o), new Vec3d(o, 1 - o, o), new Vec3d(o, 1 - o, 0), new Vec3d(o, o, 0), sprite));
        } else {
            quads.add(createQuad(new Vec3d(o, 1 - o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, o, o), new Vec3d(o, o, o), spriteEnd));
        }
        if (south) {
            quads.add(createQuad(new Vec3d(o, 1 - o, 1), new Vec3d(1 - o, 1 - o, 1), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(o, 1 - o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(o, o, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, o, 1), new Vec3d(o, o, 1), sprite));
            quads.add(createQuad(new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1), new Vec3d(1 - o, o, 1), sprite));
            quads.add(createQuad(new Vec3d(o, o, 1), new Vec3d(o, 1 - o, 1), new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, o, 1 - o), sprite));
        } else {
            quads.add(createQuad(new Vec3d(o, o, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(o, 1 - o, 1 - o), spriteEnd));
        }

        return quads;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return sprite;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
}
