package dev.boxadactle.mcshare.mixin;

import dev.boxadactle.mcshare.gui.WorldExportScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditWorldScreen.class)
public class EditWorldScreenMixin extends Screen {
    @Shadow @Final private LevelStorageSource.LevelStorageAccess levelAccess;

    protected EditWorldScreenMixin(Component component) {
        super(component);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    ordinal = 6
            )
    )
    private LayoutElement addButtons(LayoutElement layoutElement) {
        Button b = (Button) layoutElement;
        b.setWidth(98);

        Button export = Button.builder(
                Component.translatable("button.mcshare.export"),
                bu -> minecraft.setScreen(new WorldExportScreen(this, levelAccess))
        ).width(98).build();

        LinearLayout layout = LinearLayout.horizontal().spacing(4);

        layout.addChild(b);
        layout.addChild(export);

        return layout;
    }
}