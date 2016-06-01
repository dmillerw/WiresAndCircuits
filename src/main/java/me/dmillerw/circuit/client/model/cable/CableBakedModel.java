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

import static me.dmillerw.circuit.block.cable.BlockCable.CABLE_SIZE;
import static me.dmillerw.circuit.block.cable.BlockCable.CONNECTOR_DEPTH;
import static me.dmillerw.circuit.block.cable.BlockCable.CONNECTOR_SIZE;

/**
 * @author dmillerw
 */
public class CableBakedModel implements IBakedModel {


    private TextureAtlasSprite spriteCableOff;
    private TextureAtlasSprite spriteCableOffEnd;
    private TextureAtlasSprite spriteCableOn;
    private TextureAtlasSprite spriteCableOnEnd;
    
    private VertexFormat format;

    public CableBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        this.spriteCableOff = bakedTextureGetter.apply(new ResourceLocation(ModInfo.ID, "blocks/cable_off"));
        this.spriteCableOffEnd = bakedTextureGetter.apply(new ResourceLocation(ModInfo.ID, "blocks/cable_end_off"));
        this.spriteCableOn = bakedTextureGetter.apply(new ResourceLocation(ModInfo.ID, "blocks/cable_on"));
        this.spriteCableOnEnd = bakedTextureGetter.apply(new ResourceLocation(ModInfo.ID, "blocks/cable_end_on"));
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

        Boolean active = extendedBlockState.getValue(BlockCable.CONNECTED);

        EnumConnectionType north = extendedBlockState.getValue(BlockCable.NORTH);
        EnumConnectionType south = extendedBlockState.getValue(BlockCable.SOUTH);
        EnumConnectionType west = extendedBlockState.getValue(BlockCable.WEST);
        EnumConnectionType east = extendedBlockState.getValue(BlockCable.EAST);
        EnumConnectionType up = extendedBlockState.getValue(BlockCable.UP);
        EnumConnectionType down = extendedBlockState.getValue(BlockCable.DOWN);

        final TextureAtlasSprite cable = active ? spriteCableOn : spriteCableOff;
        final TextureAtlasSprite end = active ? spriteCableOnEnd : spriteCableOffEnd;

        List<BakedQuad> quads = new ArrayList<>();

        // Y - 1 - 0
        if (up.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    cable));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    cable));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 1, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1, 1 - CABLE_SIZE),
                    cable, 2));

            if (up.renderConnector()) {
                // CONNECTOR
                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 1, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 1, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE,     1, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE,     1 - CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE,     1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE,     1, 1 - CONNECTOR_SIZE),
                        end, 2));

                // CONNECTOR CAP
                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 0.999, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 0.999, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 0.999, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 0.999, CONNECTOR_SIZE),
                        end));
            }
        } else {
            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    end));
        }

        // Y - 0 - 1
        if (down.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, 0, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 0, 1 - CABLE_SIZE),
                    cable));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 0, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 0, CABLE_SIZE),
                    cable));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 0, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 0, CABLE_SIZE),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 0, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 0, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    cable, 2));

            if (down.renderConnector()) {
                // CONNECTOR
                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_SIZE, 0, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 0, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 0, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 0, CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 0, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 0, CONNECTOR_SIZE),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 0, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 0, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        end, 2));

                // CONNECTOR CAP
                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 0.001, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 0.001, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, 0.001, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, 0.001, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_DEPTH, CONNECTOR_SIZE),
                        end));
            }
        } else {
            quads.add(createQuad(new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE), new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE), new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE), new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE), end));
        }

        // X - 0 - 1
        if (east.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    cable));

            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    cable));

            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    cable));

            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    cable));

            if (east.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_DEPTH, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(1, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(1, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_DEPTH, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(1, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(1, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_DEPTH, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_DEPTH, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(0.999, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(0.999, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(0.999, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(0.999, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_DEPTH, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(1 - CONNECTOR_DEPTH, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        end));
            }
        } else {
            quads.add(createQuad(new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE), new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE), new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE), new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE), end));
        }

        if (west.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(0, 1 - CABLE_SIZE, 1 - CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE), 
                    new Vec3d(0, 1 - CABLE_SIZE, CABLE_SIZE), 
                    cable));
            
            quads.add(createQuad(
                    new Vec3d(0, CABLE_SIZE, CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE), 
                    new Vec3d(0, CABLE_SIZE, 1 - CABLE_SIZE), 
                    cable));
            
            quads.add(createQuad(
                    new Vec3d(0, 1 - CABLE_SIZE, CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE), 
                    new Vec3d(0, CABLE_SIZE, CABLE_SIZE), 
                    cable));
            
            quads.add(createQuad(
                    new Vec3d(0, CABLE_SIZE, 1 - CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE), 
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE), 
                    new Vec3d(0, 1 - CABLE_SIZE, 1 - CABLE_SIZE), 
                    cable));

            if (west.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(0, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(0, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        end));
                
                quads.add(createQuad(
                        new Vec3d(0, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(0, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        end));
                
                quads.add(createQuad(
                        new Vec3d(0, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(0, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        end));
                
                quads.add(createQuad(
                        new Vec3d(0, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(0, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_DEPTH, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(CONNECTOR_DEPTH, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        end));

                quads.add(createQuad(
                        new Vec3d(0.001, CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(0.001, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE),
                        new Vec3d(0.001, 1 - CONNECTOR_SIZE, CONNECTOR_SIZE),
                        new Vec3d(0.001, CONNECTOR_SIZE, CONNECTOR_SIZE),
                        end));
            }
        } else {
            quads.add(createQuad(new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE), new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE), new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE), new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE), end));
        }

        if (north.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 0),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 0),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 0),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 0),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 0),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 0),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 0),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 0),
                    cable, 2));

            if (north.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 0),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 0),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, 0),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, 0),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, 0),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 0),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 0),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, 0),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, CONNECTOR_DEPTH),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 0.001),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE,  0.001),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE,  0.001),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE,  0.001),
                        end));
            }
        } else {
            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, CABLE_SIZE),
                    end));
        }

        if (south.renderCable()) {
            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1),
                    cable, 2));

            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    cable, 2));

            if (south.renderConnector()) {
                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, 1),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, 1),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, 1),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, 1),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        end, 2));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE, 1 - CONNECTOR_DEPTH),
                        end));

                quads.add(createQuad(
                        new Vec3d(CONNECTOR_SIZE, CONNECTOR_SIZE,  0.999),
                        new Vec3d(1 - CONNECTOR_SIZE, CONNECTOR_SIZE,  0.999),
                        new Vec3d(1 - CONNECTOR_SIZE, 1 - CONNECTOR_SIZE,  0.999),
                        new Vec3d(CONNECTOR_SIZE, 1 - CONNECTOR_SIZE, 0.999),
                        end));
            }
        } else {
            quads.add(createQuad(
                    new Vec3d(CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(1 - CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    new Vec3d(CABLE_SIZE, 1 - CABLE_SIZE, 1 - CABLE_SIZE),
                    end));
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
        return spriteCableOff;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
}
