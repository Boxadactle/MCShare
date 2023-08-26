package dev.boxadactle.mcshare.fabric;

import dev.boxadactle.mcshare.MCShare;
import net.fabricmc.api.ModInitializer;

public class MCShareFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MCShare.init();
    }
}