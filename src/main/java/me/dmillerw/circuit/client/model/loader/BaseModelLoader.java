package me.dmillerw.circuit.client.model.loader;

import me.dmillerw.circuit.client.model.cable.CableModel;
import me.dmillerw.circuit.lib.ModInfo;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * @author dmillerw
 */
public class BaseModelLoader implements ICustomModelLoader {

    public static final CableModel CABLE_MODEL = new CableModel();

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getResourceDomain().equals(ModInfo.ID))
            return false;

        final String path = modelLocation.getResourcePath();
        return path.contains("block") && path.contains("cable");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        System.out.println("LOADING FOR " + modelLocation);
        return CABLE_MODEL;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
