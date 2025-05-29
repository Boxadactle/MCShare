package dev.boxadactle.mcshare.gui;

import dev.boxadactle.boxlib.gui.config.BOptionHelper;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.field.BStringField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.WorldImporter;
import dev.boxadactle.mcshare.Metadata;
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WorldImportScreen extends BOptionScreen {

    Button confirm;

    Path path;
    PathField field;

    String password = null;

    public WorldImportScreen(Screen parent) {
        super(parent);

        path = MCShare.getDesktop();
    }

    @Override
    protected Component getName() {
        return Component.empty();
    }

    @Override
    protected void initFooter(int i, int i1) {
        int number = getButtonWidth(ButtonType.SMALL);
        confirm = addRenderableWidget(Button.builder(Component.translatable("button.mcshare.import.screen"), this::startImport)
                .bounds(width / 2 - number - 1, height - 25, number, 20)
                .build()
        );

        addRenderableWidget(Button.builder(GuiUtils.CANCEL, b -> onClose())
                .bounds(width / 2 + 1, height - 25, number, 20)
                .build()
        );
    }

    @Override
    protected void initConfigButtons() {

        addConfigLine(new BCenteredLabel(Component.translatable("screen.mcshare.importworld")));
        addConfigLine(new BCenteredLabel(Component.translatable("label.mcshare.dragging")));

        space();

        field = new PathField(path, v -> path = v);
        field.setMaxLength(512);

        addConfigLine(field);

        addConfigLine(new BSpacingEntry());

        addConfigLine(new BCenteredLabel(Component.translatable("label.mcshare.password")));

        addConfigLine(new BStringField("", (s) -> password = s));
    }

    @Override
    public void onFilesDrop(List<Path> list) {
        for (Path p : list) {
            if (p.toString().endsWith(Metadata.WORLD_EXTENSION) || p.toString().endsWith(".zip")) {
                path = p;
                field.setValue(field.from(p));
                field.valid();
                break;
            }
        }
    }

    private void space() {
        addConfigLine(new BSpacingEntry());
    }

    private void startImport(Button b) {
        ClientUtils.getClient().forceSetScreen(new ImportingScreen(parent, path.getFileName().toString()));

        WorldImporter.ImportOptions options = new WorldImporter.ImportOptions()
                .setPath(path)
                .setPassword(password.isEmpty() ? null : password)
                .setFinished(() -> ((ImportingScreen) ClientUtils.getClient().screen).setFinished())
                .setErrored((s) -> ((ImportingScreen) ClientUtils.getClient().screen).setErrored(s));

        WorldImporter.startImport(options);
    }

    public static class ImportingScreen extends Screen {
        Screen parent;

        boolean isFinished = false;
        boolean isError = false;

        String error;

        String worldName;

        Button doneButton;
        Button playButton;

        List<String> worldFiles;

        protected ImportingScreen(Screen parent, String name) {
            super(Component.translatable("screen.mcshare.importing", name));

            worldName = name;
            this.parent = parent;

            Path worldFolder = MCShare.getWorldFolder();
            worldFiles = List.of(worldFolder.toFile().list((dir, name1) -> dir.isDirectory()));
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, float f) {
            super.render(guiGraphics, i, j, f);

            if (!isError)
                guiGraphics.drawCenteredString(font, isFinished ? Component.translatable("message.mcshare.finished.import") : title, width / 2, height / 2 - 60, GuiUtils.WHITE);
            else {
                guiGraphics.drawCenteredString(font, Component.translatable("message.mcshare.finished.import.error"), width / 2, height / 2 - 60, GuiUtils.WHITE);
                guiGraphics.drawCenteredString(font, error.trim(), width, height / 2 - 45, GuiUtils.DARK_RED);
            }
        }

        @Override
        protected void init() {
            int number = BOptionHelper.buttonWidth(BOptionHelper.ButtonType.SMALL);

            doneButton = addRenderableWidget(Button.builder(GuiUtils.DONE, b -> onClose())
                    .bounds(width / 2 - number - 1, height - 40, number, 20)
                    .build()
            );

            playButton = addRenderableWidget(Button.builder(Component.translatable("button.mcshare.play"), this::playWorld)
                    .bounds(width / 2 + 1, height - 40, number, 20)
                    .build()
            );

            doneButton.visible = false;
            doneButton.active = false;
            playButton.visible = false;
            playButton.active = false;
        }

        private void playWorld(Button button) {
            Path worldFolder = MCShare.getWorldFolder();

            // find new folder by checking if the world name exists in the worldFiles list
            for (String file : worldFolder.toFile().list((dir, name1) -> dir.isDirectory())) {
                if (!worldFiles.contains(file)) {
                    minecraft.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("message.mcshare.open")));
                    this.minecraft.createWorldOpenFlows().loadLevel(this, worldName.replace(Metadata.WORLD_EXTENSION, ""));
                    break;
                }
            }

            ClientUtils.setScreen(new SelectWorldScreen(new TitleScreen()));
            ClientUtils.showToast(
                    Component.translatable("message.mcshare.open.error"),
                    Component.translatable("message.mcshare.open.error.2")
            );
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
            doneButton.visible = true;
            playButton.visible = true;

            doneButton.active = true;
            playButton.active = bl;

            MCShare.LOGGER.info("ImportingScreen buttons activated");
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

}