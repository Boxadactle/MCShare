package dev.boxadactle.mcshare.gui.importing;

import dev.boxadactle.boxlib.config.gui.BConfigHelper;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.mixin.ParentAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

public class ImportingScreen extends Screen {

    Screen parent;

    boolean isFinished = false;
    boolean isError = false;

    String error;

    Button finish;
    Button playWorld;

    String worldName;

    protected ImportingScreen(Screen parent, String name) {
        super(Component.translatable("screen.mcshare.importing", name));

        this.parent = parent;
        worldName = name;
    }

    @Override
    protected void init() {
        int number = BConfigHelper.buttonWidth(BConfigHelper.ButtonType.SMALL);

        finish = addRenderableWidget(Button.builder(GuiUtils.DONE, b -> onClose())
                .bounds(width / 2 - number - 1, height - 40, number, 20)
                .build()
        );

        playWorld = addRenderableWidget(Button.builder(Component.translatable("button.mcshare.play"), this::playWorld)
                .bounds(width / 2 + 1, height - 40, number, 20)
                .build()
        );
    }

    @Override
    public void tick() {
        finish.visible = isFinished;
        playWorld.visible = isFinished;
        playWorld.active = !isError;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, i, j, f);

        if (!isError)
            guiGraphics.drawCenteredString(font, isFinished ? Component.translatable("message.mcshare.finished.import") : title, width / 2, height / 2 - 60, GuiUtils.WHITE);
        else {
            guiGraphics.drawCenteredString(font, Component.translatable("message.mcshare.finished.import.error"), width / 2, height / 2 - 60, GuiUtils.WHITE);
            guiGraphics.drawCenteredString(font, error, width, height / 2 - 45, GuiUtils.DARK_RED);
        }
    }


    private void playWorld(Button button) {
        minecraft.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
        this.minecraft.createWorldOpenFlows().loadLevel(this, worldName.replace(MCShare.WORLD_EXTENSION, ""));
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void setErrored(boolean error, String errorMessage) {
        isError = error;
        this.error = errorMessage;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return isFinished;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(new SelectWorldScreen(((ParentAccessor)parent).getParent()));
    }

}