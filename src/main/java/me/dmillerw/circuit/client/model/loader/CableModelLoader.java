package me.dmillerw.circuit.client.model.loader;

import me.dmillerw.circuit.block.cable.BlockCable;
import me.dmillerw.circuit.client.model.CableModel;
import me.dmillerw.circuit.lib.ModInfo;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * @author dmillerw
 */
public class CableModelLoader implements ICustomModelLoader {

    public static final CableModel CABLE_MODEL = new CableModel();

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (modelLocation.getResourceDomain().equals("minecraft"))
            return false;

        final String domain = modelLocation.getResourceDomain();
        final String path = modelLocation.getResourcePath();

        if (!domain.equals(ModInfo.ID))
            return false;

        return path.equals(BlockCable.NAME);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return CABLE_MODEL;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
