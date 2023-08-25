package dev.boxadactle.mcshare.fabric;

import dev.boxadactle.mcshare.MCShare;
import net.fabricmc.api.ClientModInitializer;

public class MCShareFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MCShare.init();
    }
}