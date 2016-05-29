package me.dmillerw.circuit.client.model.loader;

import com.google.common.collect.Maps;
import me.dmillerw.circuit.client.model.CableModel;
import me.dmillerw.circuit.lib.ModInfo;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.Map;

/**
 * @author dmillerw
 */
public class CableModelLoader implements ICustomModelLoader {

    private static Map<ResourceLocation, IModel> modelRegistry = Maps.newHashMap();

    static {
        modelRegistry.put(ModInfo.resourceLocation("models/block/cable"), new CableModel());
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        final String domain = modelLocation.getResourceDomain();
        final String path = modelLocation.getResourcePath();

        if (!domain.equals(ModInfo.ID))
            return false;

        return modelRegistry.containsKey(modelLocation);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return modelRegistry.get(modelLocation);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
