package dev.boxadactle.mcshare.mixin;

import dev.boxadactle.mcshare.MCShare;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;

@Mixin(net.minecraft.client.gui.screens.worldselection.CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    protected CreateWorldScreenMixin(Component component) {
        super(component);
    }

    // some IDEs will show this return value as an error
    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;addTabs([Lnet/minecraft/client/gui/components/tabs/Tab;)Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;",
                    ordinal = 0
            )
    )
    private Tab[] addImportTab(Tab[] args) {
        Tab[] cloned = Arrays.copyOf(args, args.length + 1);

        cloned[cloned.length - 1] = new MCShare.ImportTab(this);

        return cloned;
    }

}
