package dev.boxadactle.mcshare.gui;

import dev.boxadactle.boxlib.gui.config.BOptionHelper;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.field.BStringField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.gui.config.widget.label.BLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.WorldExporter;
import dev.boxadactle.mcshare.Metadata;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.nio.file.Path;

public class WorldExportScreen extends BOptionScreen {

    LevelStorageSource.LevelStorageAccess levelAccess;

    Button export;

    Path path;
    String filename;
    PathField field;
    BStringField field2;

    boolean datapacks = false;
    boolean resourcepacks = false;

    String password = null;

    public WorldExportScreen(Screen parent, LevelStorageSource.LevelStorageAccess levelAccess) {
        super(parent);

        this.levelAccess = levelAccess;

        path = MCShare.getDesktop();

        MCShare.LOGGER.info(path);
    }

    @Override
    protected Component getName() {
        return Component.empty();
    }

    @Override
    protected void initFooter(int i, int i1) {
        int number = getButtonWidth(ButtonType.SMALL);

        export = addRenderableWidget(Button.builder(Component.translatable("button.mcshare.export.screen"), this::startExport)
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

        addConfigLine(new BCenteredLabel(Component.translatable("screen.mcshare.exportworld")));

        space();

        field = new PathField(path, v -> path = v);
        field.setMaxLength(512);
        addConfigLine(field);

        field2 = new BStringField(levelAccess.getLevelId(), s -> filename = s + Metadata.WORLD_EXTENSION);
        field2.setMaxLength(64);
        addConfigLine(field2, new BLabel(Component.literal(Metadata.WORLD_EXTENSION)));

        space();

        addConfigLine(new BCenteredLabel(Component.translatable("screen.mcshare.exportworld.settings")));

        addConfigLine(new BBooleanButton(
                "button.mcshare.export.resourcepacks",
                resourcepacks,
                n -> resourcepacks = n
        ));

        addConfigLine(new BBooleanButton(
                "button.mcshare.export.datapacks",
                datapacks,
                n -> datapacks = n
        ));

        addConfigLine(new BSpacingEntry());

        addConfigLine(new BCenteredLabel(Component.translatable("label.mcshare.password")));

        addConfigLine(new BStringField("", s -> password = s));
    }

    private void startExport(Button ignored) {
        ClientUtils.getClient().forceSetScreen(new ExportingScreen(parent, levelAccess, path));

        Path p = path.resolve(filename);

        WorldExporter.ExportOptions options = new WorldExporter.ExportOptions()
                .setExportPath(p)
                .setWorldPath(levelAccess.getLevelPath(LevelResource.ROOT))
                .setIncludeDatapacks(datapacks)
                .setIncludeResourcepacks(resourcepacks)
                .setPassword(password.isBlank() ? null : password)
                .setFinished(() -> ((ExportingScreen) ClientUtils.getCurrentScreen()).setFinished())
                .setErrored(((ExportingScreen) ClientUtils.getCurrentScreen())::setErrored);

        WorldExporter.startExport(options);
    }

    private void space() {
        addConfigLine(new BSpacingEntry());
    }

    public static class ExportingScreen extends Screen {
        Screen parent;

        boolean isFinished;
        boolean isError;
        String error;

        Path path;

        Button doneButton;
        Button openButton;

        protected ExportingScreen(Screen parent, LevelStorageSource.LevelStorageAccess access, Path filePath) {
            super(Component.translatable("screen.mcshare.exporting", access.getLevelId()));

            this.parent = parent;

            path = filePath;

            MCShare.LOGGER.info("hello screen");
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, float f) {
            super.render(guiGraphics, i, j, f);

            if (!isError)
                guiGraphics.drawCenteredString(font, isFinished ? Component.translatable("message.mcshare.finished.export") : title, width / 2, height / 2 - 60, GuiUtils.WHITE);
            else {
                guiGraphics.drawCenteredString(font, Component.translatable("message.mcshare.finished.export.error"), width / 2, height / 2 - 60, GuiUtils.WHITE);
                guiGraphics.drawCenteredString(font, error, width, height / 2 - 45, GuiUtils.DARK_RED);
            }
        }

        @Override
        protected void init() {
            int number = BOptionHelper.buttonWidth(BOptionHelper.ButtonType.SMALL);
            doneButton = addRenderableWidget(Button.builder(GuiUtils.DONE, b -> onClose())
                    .bounds(width / 2 - number - 1, height / 2 + 40, number, 20)
                    .build()
            );

            openButton = addRenderableWidget(Button.builder(Component.translatable("button.mcshare.open"), this::openFile)
                    .bounds(width / 2 + 1, height / 2 + 40, number, 20)
                    .build()
            );

            doneButton.visible = false;
            doneButton.active = false;
            openButton.visible = false;
            openButton.active = false;
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
            doneButton.visible = true;
            openButton.visible = true;

            doneButton.active = true;
            openButton.active = bl;

            MCShare.LOGGER.info("ExportingScreen buttons activated");
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
}