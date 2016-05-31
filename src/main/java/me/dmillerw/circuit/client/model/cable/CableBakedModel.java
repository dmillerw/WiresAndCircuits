package me.dmillerw.circuit.client.model.cable;

import com.google.common.base.Function;
import me.dmillerw.circuit.block.cable.BlockCable;
import me.dmillerw.circuit.lib.ModInfo;
import me.dmillerw.circuit.lib.property.EnumConnectionType;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author dmillerw
 */
public class CableBakedModel implements IBakedModel {

    private TextureAtlasSprite spriteCable;
    private TextureAtlasSprite spriteCableEnd;
    
    private VertexFormat format;

    public CableBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        this.spriteCable = bakedTextureGetter.apply(new ResourceLocation(ModInfo.ID, "blocks/cable"));
        this.spriteCableEnd = bakedTextureGetter.apply(new ResourceLocation(ModInfo.ID, "blocks/cable_end"));
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, Vec3d vertex, float u, float v, TextureAtlasSprite sprite) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)vertex.xCoord, (float)vertex.yCoord, (float)vertex.zCoord, 1.0f);
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
        return createQuad(v1, v2, v3, v4, sprite, 0);
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int rotateUV) {
        Vec3d normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        normal = normal.normalize().rotatePitch(180).rotateYaw(180);

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);

        switch (rotateUV) {
            case 3: {
                putVertex(builder, normal, v1, 16, 0, sprite);
                putVertex(builder, normal, v2, 16, 16, sprite);
                putVertex(builder, normal, v3, 0, 16, sprite);
                putVertex(builder, normal, v4, 0, 0, sprite);
                break;
            }
            case 2: {
                putVertex(builder, normal, v1, 16, 16, sprite);
                putVertex(builder, normal, v2, 0, 16, sprite);
                putVertex(builder, normal, v3, 0, 0, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
            case 1: {
                putVertex(builder, normal, v1, 0, 16, sprite);
                putVertex(builder, normal, v2, 0, 0, sprite);
                putVertex(builder, normal, v3, 16, 16, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
            default: {
                putVertex(builder, normal, v1, 0, 0, sprite);
                putVertex(builder, normal, v2, 0, 16, sprite);
                putVertex(builder, normal, v3, 16, 16, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
        }

        return builder.build();
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {

        if (side != null) {
            return Collections.emptyList();
        }

        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        EnumConnectionType north = extendedBlockState.getValue(BlockCable.NORTH);
        EnumConnectionType south = extendedBlockState.getValue(BlockCable.SOUTH);
        EnumConnectionType west = extendedBlockState.getValue(BlockCable.WEST);
        EnumConnectionType east = extendedBlockState.getValue(BlockCable.EAST);
        EnumConnectionType up = extendedBlockState.getValue(BlockCable.UP);
        EnumConnectionType down = extendedBlockState.getValue(BlockCable.DOWN);

        List<BakedQuad> quads = new ArrayList<>();
        double cableSize = .4;

        double connectorSize = .3;
        double connectorDepth = .2;

        // Y - 1 - 0
        if (up.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    new Vec3d(1 - cableSize, 1, cableSize),
                    new Vec3d(1 - cableSize, 1, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    spriteCable));

            quads.add(createQuad(
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(cableSize, 1, 1 - cableSize),
                    new Vec3d(cableSize, 1, cableSize),
                    new Vec3d(cableSize, 1 - cableSize, cableSize),
                    spriteCable));

            quads.add(createQuad(
                    new Vec3d(cableSize, 1, cableSize),
                    new Vec3d(1 - cableSize, 1, cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    new Vec3d(cableSize, 1 - cableSize, cableSize),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1, 1 - cableSize),
                    new Vec3d(cableSize, 1, 1 - cableSize),
                    spriteCable, 2));

            if (up.renderConnector()) {
                // CONNECTOR
                quads.add(createQuad(
                        new Vec3d(1 - connectorSize, 1 - connectorDepth, connectorSize),
                        new Vec3d(1 - connectorSize, 1, connectorSize),
                        new Vec3d(1 - connectorSize, 1, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, 1 - connectorDepth, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize, 1 - connectorDepth, 1 - connectorSize),
                        new Vec3d(connectorSize, 1, 1 - connectorSize),
                        new Vec3d(connectorSize, 1, connectorSize),
                        new Vec3d(connectorSize, 1 - connectorDepth, connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize,     1, connectorSize),
                        new Vec3d(1 - connectorSize, 1, connectorSize),
                        new Vec3d(1 - connectorSize, 1 - connectorDepth, connectorSize),
                        new Vec3d(connectorSize,     1 - connectorDepth, connectorSize),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize,     1 - connectorDepth, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, 1 - connectorDepth, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, 1, 1 - connectorSize),
                        new Vec3d(connectorSize,     1, 1 - connectorSize),
                        spriteCableEnd, 2));

                // CONNECTOR CAP
                quads.add(createQuad(
                        new Vec3d(connectorSize, 1 - connectorDepth, connectorSize),
                        new Vec3d(1 - connectorSize, 1 - connectorDepth, connectorSize),
                        new Vec3d(1 - connectorSize, 1 - connectorDepth, 1 - connectorSize),
                        new Vec3d(connectorSize, 1 - connectorDepth, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize, 0.999, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, 0.999, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, 0.999, connectorSize),
                        new Vec3d(connectorSize, 0.999, connectorSize),
                        spriteCableEnd));
            }
        } else {
            quads.add(createQuad(
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    new Vec3d(cableSize, 1 - cableSize, cableSize),
                    spriteCableEnd));
        }

        // Y - 0 - 1
        if (down.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(1 - cableSize, 0, cableSize),
                    new Vec3d(1 - cableSize, cableSize, cableSize),
                    new Vec3d(1 - cableSize, cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 0, 1 - cableSize),
                    spriteCable));

            quads.add(createQuad(
                    new Vec3d(cableSize, 0, 1 - cableSize),
                    new Vec3d(cableSize, cableSize, 1 - cableSize),
                    new Vec3d(cableSize, cableSize, cableSize),
                    new Vec3d(cableSize, 0, cableSize),
                    spriteCable));

            quads.add(createQuad(
                    new Vec3d(cableSize, cableSize, cableSize),
                    new Vec3d(1 - cableSize, cableSize, cableSize),
                    new Vec3d(1 - cableSize, 0, cableSize),
                    new Vec3d(cableSize, 0, cableSize),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(cableSize, 0, 1 - cableSize),
                    new Vec3d(1 - cableSize, 0, 1 - cableSize),
                    new Vec3d(1 - cableSize, cableSize, 1 - cableSize),
                    new Vec3d(cableSize, cableSize, 1 - cableSize),
                    spriteCable, 2));

            if (down.renderConnector()) {
                // CONNECTOR
                quads.add(createQuad(
                        new Vec3d(1 - connectorSize, 0, connectorSize),
                        new Vec3d(1 - connectorSize, connectorDepth, connectorSize),
                        new Vec3d(1 - connectorSize, connectorDepth, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, 0, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize, 0, 1 - connectorSize),
                        new Vec3d(connectorSize, connectorDepth, 1 - connectorSize),
                        new Vec3d(connectorSize, connectorDepth, connectorSize),
                        new Vec3d(connectorSize, 0, connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorDepth, connectorSize),
                        new Vec3d(1 - connectorSize, connectorDepth, connectorSize),
                        new Vec3d(1 - connectorSize, 0, connectorSize),
                        new Vec3d(connectorSize, 0, connectorSize),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize, 0, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, 0, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, connectorDepth, 1 - connectorSize),
                        new Vec3d(connectorSize, connectorDepth, 1 - connectorSize),
                        spriteCableEnd, 2));

                // CONNECTOR CAP
                quads.add(createQuad(
                        new Vec3d(connectorSize, 0.001, connectorSize),
                        new Vec3d(1 - connectorSize, 0.001, connectorSize),
                        new Vec3d(1 - connectorSize, 0.001, 1 - connectorSize),
                        new Vec3d(connectorSize, 0.001, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorDepth, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, connectorDepth, 1 - connectorSize),
                        new Vec3d(1 - connectorSize, connectorDepth, connectorSize),
                        new Vec3d(connectorSize, connectorDepth, connectorSize),
                        spriteCableEnd));
            }
        } else {
            quads.add(createQuad(new Vec3d(cableSize, cableSize, cableSize), new Vec3d(1 - cableSize, cableSize, cableSize), new Vec3d(1 - cableSize, cableSize, 1 - cableSize), new Vec3d(cableSize, cableSize, 1 - cableSize), spriteCableEnd));
        }

        // X - 0 - 1
        if (east.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1, 1 - cableSize, cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    spriteCable));

            quads.add(createQuad(
                    new Vec3d(1 - cableSize, cableSize, cableSize),
                    new Vec3d(1, cableSize, cableSize),
                    new Vec3d(1, cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, cableSize, 1 - cableSize),
                    spriteCable));

            quads.add(createQuad(
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    new Vec3d(1, 1 - cableSize, cableSize),
                    new Vec3d(1, cableSize, cableSize),
                    new Vec3d(1 - cableSize, cableSize, cableSize),
                    spriteCable));

            quads.add(createQuad(
                    new Vec3d(1 - cableSize, cableSize, 1 - cableSize),
                    new Vec3d(1, cableSize, 1 - cableSize),
                    new Vec3d(1, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    spriteCable));

            if (east.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(1 - connectorDepth, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(1, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(1, 1 - connectorSize, connectorSize),
                        new Vec3d(1 - connectorDepth, 1 - connectorSize, connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(1 - connectorDepth, connectorSize, connectorSize),
                        new Vec3d(1, connectorSize, connectorSize),
                        new Vec3d(1, connectorSize, 1 - connectorSize),
                        new Vec3d(1 - connectorDepth, connectorSize, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(1 - connectorDepth, 1 - connectorSize, connectorSize),
                        new Vec3d(1, 1 - connectorSize, connectorSize),
                        new Vec3d(1, connectorSize, connectorSize),
                        new Vec3d(1 - connectorDepth, connectorSize, connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(1 - connectorDepth, connectorSize, 1 - connectorSize),
                        new Vec3d(1, connectorSize, 1 - connectorSize),
                        new Vec3d(1, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(1 - connectorDepth, 1 - connectorSize, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(0.999, connectorSize, connectorSize),
                        new Vec3d(0.999, 1 - connectorSize, connectorSize),
                        new Vec3d(0.999, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(0.999, connectorSize, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(1 - connectorDepth, connectorSize, 1 - connectorSize),
                        new Vec3d(1 - connectorDepth, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(1 - connectorDepth, 1 - connectorSize, connectorSize),
                        new Vec3d(1 - connectorDepth, connectorSize, connectorSize),
                        spriteCableEnd));
            }
        } else {
            quads.add(createQuad(new Vec3d(1 - cableSize, cableSize, cableSize), new Vec3d(1 - cableSize, 1 - cableSize, cableSize), new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize), new Vec3d(1 - cableSize, cableSize, 1 - cableSize), spriteCableEnd));
        }

        if (west.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(0, 1 - cableSize, 1 - cableSize), 
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize), 
                    new Vec3d(cableSize, 1 - cableSize, cableSize), 
                    new Vec3d(0, 1 - cableSize, cableSize), 
                    spriteCable));
            
            quads.add(createQuad(
                    new Vec3d(0, cableSize, cableSize), 
                    new Vec3d(cableSize, cableSize, cableSize), 
                    new Vec3d(cableSize, cableSize, 1 - cableSize), 
                    new Vec3d(0, cableSize, 1 - cableSize), 
                    spriteCable));
            
            quads.add(createQuad(
                    new Vec3d(0, 1 - cableSize, cableSize), 
                    new Vec3d(cableSize, 1 - cableSize, cableSize), 
                    new Vec3d(cableSize, cableSize, cableSize), 
                    new Vec3d(0, cableSize, cableSize), 
                    spriteCable));
            
            quads.add(createQuad(
                    new Vec3d(0, cableSize, 1 - cableSize), 
                    new Vec3d(cableSize, cableSize, 1 - cableSize), 
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize), 
                    new Vec3d(0, 1 - cableSize, 1 - cableSize), 
                    spriteCable));

            if (west.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(0, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(connectorDepth, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(connectorDepth, 1 - connectorSize, connectorSize),
                        new Vec3d(0, 1 - connectorSize, connectorSize),
                        spriteCableEnd));
                
                quads.add(createQuad(
                        new Vec3d(0, connectorSize, connectorSize),
                        new Vec3d(connectorDepth, connectorSize, connectorSize),
                        new Vec3d(connectorDepth, connectorSize, 1 - connectorSize),
                        new Vec3d(0, connectorSize, 1 - connectorSize),
                        spriteCableEnd));
                
                quads.add(createQuad(
                        new Vec3d(0, 1 - connectorSize, connectorSize),
                        new Vec3d(connectorDepth, 1 - connectorSize, connectorSize),
                        new Vec3d(connectorDepth, connectorSize, connectorSize),
                        new Vec3d(0, connectorSize, connectorSize),
                        spriteCableEnd));
                
                quads.add(createQuad(
                        new Vec3d(0, connectorSize, 1 - connectorSize),
                        new Vec3d(connectorDepth, connectorSize, 1 - connectorSize),
                        new Vec3d(connectorDepth, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(0, 1 - connectorSize, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorDepth, connectorSize, connectorSize),
                        new Vec3d(connectorDepth, 1 - connectorSize, connectorSize),
                        new Vec3d(connectorDepth, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(connectorDepth, connectorSize, 1 - connectorSize),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(0.001, connectorSize, 1 - connectorSize),
                        new Vec3d(0.001, 1 - connectorSize, 1 - connectorSize),
                        new Vec3d(0.001, 1 - connectorSize, connectorSize),
                        new Vec3d(0.001, connectorSize, connectorSize),
                        spriteCableEnd));
            }
        } else {
            quads.add(createQuad(new Vec3d(cableSize, cableSize, 1 - cableSize), new Vec3d(cableSize, 1 - cableSize, 1 - cableSize), new Vec3d(cableSize, 1 - cableSize, cableSize), new Vec3d(cableSize, cableSize, cableSize), spriteCableEnd));
        }

        if (north.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(cableSize, 1 - cableSize, cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 0),
                    new Vec3d(cableSize, 1 - cableSize, 0),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(cableSize, cableSize, 0),
                    new Vec3d(1 - cableSize, cableSize, 0),
                    new Vec3d(1 - cableSize, cableSize, cableSize),
                    new Vec3d(cableSize, cableSize, cableSize),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(1 - cableSize, cableSize, 0),
                    new Vec3d(1 - cableSize, 1 - cableSize, 0),
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    new Vec3d(1 - cableSize, cableSize, cableSize),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(cableSize, cableSize, cableSize),
                    new Vec3d(cableSize, 1 - cableSize, cableSize),
                    new Vec3d(cableSize, 1 - cableSize, 0),
                    new Vec3d(cableSize, cableSize, 0),
                    spriteCable, 2));

            if (north.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(connectorSize, 1 - connectorSize, connectorDepth),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, connectorDepth),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, 0),
                        new Vec3d(connectorSize, 1 - connectorSize, 0),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorSize, 0),
                        new Vec3d(1 - connectorSize, connectorSize, 0),
                        new Vec3d(1 - connectorSize, connectorSize, connectorDepth),
                        new Vec3d(connectorSize, connectorSize, connectorDepth),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(1 - connectorSize, connectorSize, 0),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, 0),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, connectorDepth),
                        new Vec3d(1 - connectorSize, connectorSize, connectorDepth),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorSize, connectorDepth),
                        new Vec3d(connectorSize, 1 - connectorSize, connectorDepth),
                        new Vec3d(connectorSize, 1 - connectorSize, 0),
                        new Vec3d(connectorSize, connectorSize, 0),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorSize, connectorDepth),
                        new Vec3d(1 - connectorSize, connectorSize, connectorDepth),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, connectorDepth),
                        new Vec3d(connectorSize, 1 - connectorSize, connectorDepth),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize, 1 - connectorSize, 0.001),
                        new Vec3d(1 - connectorSize, 1 - connectorSize,  0.001),
                        new Vec3d(1 - connectorSize, connectorSize,  0.001),
                        new Vec3d(connectorSize, connectorSize,  0.001),
                        spriteCableEnd));
            }
        } else {
            quads.add(createQuad(
                    new Vec3d(cableSize, 1 - cableSize, cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, cableSize),
                    new Vec3d(1 - cableSize, cableSize, cableSize),
                    new Vec3d(cableSize, cableSize, cableSize),
                    spriteCableEnd));
        }

        if (south.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(cableSize, 1 - cableSize, 1),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(cableSize, cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, cableSize, 1),
                    new Vec3d(cableSize, cableSize, 1),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(1 - cableSize, cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1),
                    new Vec3d(1 - cableSize, cableSize, 1),
                    spriteCable, 2));

            quads.add(createQuad(
                    new Vec3d(cableSize, cableSize, 1),
                    new Vec3d(cableSize, 1 - cableSize, 1),
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(cableSize, cableSize, 1 - cableSize),
                    spriteCable, 2));

            if (south.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(connectorSize, 1 - connectorSize, 1),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, 1),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, 1 - connectorDepth),
                        new Vec3d(connectorSize, 1 - connectorSize, 1 - connectorDepth),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorSize, 1 - connectorDepth),
                        new Vec3d(1 - connectorSize, connectorSize, 1 - connectorDepth),
                        new Vec3d(1 - connectorSize, connectorSize, 1),
                        new Vec3d(connectorSize, connectorSize, 1),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(1 - connectorSize, connectorSize, 1 - connectorDepth),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, 1 - connectorDepth),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, 1),
                        new Vec3d(1 - connectorSize, connectorSize, 1),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorSize, 1),
                        new Vec3d(connectorSize, 1 - connectorSize, 1),
                        new Vec3d(connectorSize, 1 - connectorSize, 1 - connectorDepth),
                        new Vec3d(connectorSize, connectorSize, 1 - connectorDepth),
                        spriteCableEnd, 2));

                quads.add(createQuad(
                        new Vec3d(connectorSize, 1 - connectorSize, 1 - connectorDepth),
                        new Vec3d(1 - connectorSize, 1 - connectorSize, 1 - connectorDepth),
                        new Vec3d(1 - connectorSize, connectorSize, 1 - connectorDepth),
                        new Vec3d(connectorSize, connectorSize, 1 - connectorDepth),
                        spriteCableEnd));

                quads.add(createQuad(
                        new Vec3d(connectorSize, connectorSize,  0.999),
                        new Vec3d(1 - connectorSize, connectorSize,  0.999),
                        new Vec3d(1 - connectorSize, 1 - connectorSize,  0.999),
                        new Vec3d(connectorSize, 1 - connectorSize, 0.999),
                        spriteCableEnd));
            }
        } else {
            quads.add(createQuad(
                    new Vec3d(cableSize, cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, cableSize, 1 - cableSize),
                    new Vec3d(1 - cableSize, 1 - cableSize, 1 - cableSize),
                    new Vec3d(cableSize, 1 - cableSize, 1 - cableSize),
                    spriteCableEnd));
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
        return spriteCable;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
}
