package me.dmillerw.circuit.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import me.dmillerw.circuit.lib.ModInfo;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;

/**
 * @author dmillerw
 */
public class CableModel implements IModel {

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new CableBakedModel(state, format, bakedTextureGetter);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(
                ModInfo.resourceLocation("block/cable"),
                ModInfo.resourceLocation("block/cable_end")
        );
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
