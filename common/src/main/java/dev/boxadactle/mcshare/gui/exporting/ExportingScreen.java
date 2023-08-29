package dev.boxadactle.mcshare.gui.exporting;

import dev.boxadactle.boxlib.config.gui.BConfigHelper;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.mcshare.MCShare;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.nio.file.Path;

public class ExportingScreen extends Screen {

    Screen parent;

    boolean isFinished;
    boolean isError;
    String error;

    Button finish;
    Button openFile;

    Path path;

    protected ExportingScreen(Screen parent, LevelStorageSource.LevelStorageAccess access, Path filePath) {
        super(Component.translatable("screen.mcshare.exporting", access.getLevelId()));

        this.parent = parent;

        path = filePath;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, i, j, f);

        if (!isError)
            guiGraphics.drawCenteredString(font, isFinished ? Component.translatable("message.mcshare.finished.export") : title, width / 2, height / 2 - 60, GuiUtils.WHITE);
        else {
            guiGraphics.drawCenteredString(font, Component.translatable("message.mcshare.finished.export.error"), width / 2, height / 2 - 60, GuiUtils.WHITE);
            guiGraphics.drawCenteredString(font, error, width, height / 2 - 45, GuiUtils.DARK_RED);
        }
    }

    private void openFile(Button b) {
        Util.getPlatform().openFile(path.getParent().toFile());
    }

    public void setFinished() {
        isFinished = true;

        activateButtons(true);
    }

    public void setErrored(String error) {
        isError = true;
        this.error = error;

        activateButtons(false);
    }

    private void activateButtons(boolean bl) {
        int number = BConfigHelper.buttonWidth(BConfigHelper.ButtonType.SMALL);
        addRenderableWidget(Button.builder(GuiUtils.DONE, b -> onClose())
                .bounds(width / 2 - number - 1, height / 2 + 40, number, 20)
                .build()
        );

        addRenderableWidget(Button.builder(Component.translatable("button.mcshare.open"), this::openFile)
                .bounds(width / 2 + 1, height / 2 + 40, number, 20)
                .build()
        ).active = bl;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return isFinished || isError;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}