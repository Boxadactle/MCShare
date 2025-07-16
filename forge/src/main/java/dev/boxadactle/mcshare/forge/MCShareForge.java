package dev.boxadactle.mcshare.forge;

import dev.boxadactle.mcshare.MCShare;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MCShare.MOD_ID)
public class MCShareForge {
    public MCShareForge() {
        MCShare.init();
    }
}