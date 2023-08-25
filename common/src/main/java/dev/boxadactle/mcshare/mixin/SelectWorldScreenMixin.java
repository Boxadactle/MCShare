package dev.boxadactle.mcshare.mixin;

import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.gui.importing.WorldImportScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public class SelectWorldScreenMixin extends Screen {

    protected SelectWorldScreenMixin(Component component) {
        super(component);
    }

    // TODO: make create world button smaller and put import button next to it
    // TODO: make list lower and move the buttons to the top of the screen

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldSelectionList;<init>(Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;Lnet/minecraft/client/Minecraft;IIIIILjava/lang/String;Lnet/minecraft/client/gui/screens/worldselection/WorldSelectionList;)V",
                    ordinal = 0
            ),
            index = 4
    )
    private int makeListLower1(int i) {
        return i + MCShare.LIST_SHIFT;
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldSelectionList;<init>(Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;Lnet/minecraft/client/Minecraft;IIIIILjava/lang/String;Lnet/minecraft/client/gui/screens/worldselection/WorldSelectionList;)V",
                    ordinal = 0
            ),
            index = 5
    )

    private int makeListLower2(int i) {
        return i + MCShare.LIST_SHIFT;
    }

    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;",
                    ordinal = 0
            )
    )
    private Button.Builder overrideSelectWorldPos(Button.Builder instance, int i, int j, int k, int l) {
        return instance.bounds(width / 2 - MCShare.BUTTONS_SIZE / 2 - MCShare.BUTTONS_SIZE - MCShare.BUTTON_PADDING, 48, MCShare.BUTTONS_SIZE, l);
    }

    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;",
                    ordinal = 1
            )
    )
    private Button.Builder overrideNewWorldPos(Button.Builder instance, int i, int j, int k, int l) {
        return instance.bounds(width / 2 - MCShare.BUTTONS_SIZE / 2, 48, MCShare.BUTTONS_SIZE, l);
    }

    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    private void addButton(CallbackInfo ci) {
        addRenderableWidget(Button.builder(
                Component.translatable("button.mcshare.import"),
                b -> minecraft.setScreen(new WorldImportScreen(this))
        ).bounds(width / 2 + MCShare.BUTTONS_SIZE / 2 + MCShare.BUTTON_PADDING, 48, MCShare.BUTTONS_SIZE, 20).build());
    }

}
