package dev.boxadactle.mcshare.gui.importing;

import dev.boxadactle.boxlib.config.gui.BConfigHelper;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.mixin.CreateWorldParentAccessor;
import dev.boxadactle.mcshare.mixin.SelectWorldParentAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

public class ImportingScreen extends Screen {

    Screen parent;

    boolean isFinished = false;
    boolean isError = false;

    String error;

    String worldName;

    protected ImportingScreen(Screen parent, String name) {
        super(Component.translatable("screen.mcshare.importing", name));

        this.parent = parent;
        worldName = name;
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

    public void setFinished() {
        isFinished = true;

        activateButtons(true);
    }

    public void setErrored(String errorMessage) {
        isError = true;
        this.error = errorMessage;

        activateButtons(false);
    }

    private void activateButtons(boolean bl) {
        int number = BConfigHelper.buttonWidth(BConfigHelper.ButtonType.SMALL);

        addRenderableWidget(Button.builder(GuiUtils.DONE, b -> onClose())
                .bounds(width / 2 - number - 1, height - 40, number, 20)
                .build()
        );

        addRenderableWidget(Button.builder(Component.translatable("button.mcshare.play"), this::playWorld)
                .bounds(width / 2 + 1, height - 40, number, 20)
                .build()
        ).active = bl;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return isFinished || isError;
    }

    @Override
    public void onClose() {
        if (parent instanceof SelectWorldScreen) {
            minecraft.setScreen(new SelectWorldScreen(((SelectWorldParentAccessor) parent).getParent()));
        } else if (parent instanceof CreateWorldScreen) {
            minecraft.setScreen(new SelectWorldScreen(((CreateWorldParentAccessor) parent).getParent()));
        }
        else {
            MCShare.LOGGER.error("Parent was not an instance of {} or {}", SelectWorldScreen.class, CreateWorldScreen.class);
            minecraft.setScreen(new TitleScreen());
        }
    }

}