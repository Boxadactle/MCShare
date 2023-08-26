package dev.boxadactle.mcshare.mixin;

import dev.boxadactle.mcshare.gui.exporting.WorldExportScreen;
import net.minecraft.client.gui.components.Button;
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
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;",
                    ordinal = 3
            ),
            index = 2
    )
    private int changeBackupButtonWidth(int i) {
        return i / 2 - 2;
    }

    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    private void addButton(CallbackInfo ci) {
        addRenderableWidget(Button.builder(
                Component.translatable("button.mcshare.export"),
                b -> minecraft.setScreen(new WorldExportScreen(this, levelAccess))
        ).bounds(width / 2 + 2, this.height / 4 + 48 + 5, 200 / 2 - 2, 20).build());
    }
}