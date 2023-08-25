package dev.boxadactle.mcshare.gui.exporting;

import dev.boxadactle.boxlib.config.gui.BConfigButton;
import dev.boxadactle.boxlib.config.gui.BConfigHelper;
import dev.boxadactle.boxlib.config.gui.BConfigScreen;
import dev.boxadactle.boxlib.config.gui.widget.BSpacingEntry;
import dev.boxadactle.boxlib.config.gui.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.config.gui.widget.button.BCustomButton;
import dev.boxadactle.boxlib.config.gui.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.gui.widget.FileLabel;
import dev.boxadactle.mcshare.util.WorldExporter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.nio.file.Path;

public class WorldExportScreen extends BConfigScreen {

    LevelStorageSource.LevelStorageAccess levelAccess;

    Path currentPath = null;

    Button export;

    FileLabel label;

    boolean datapacks = false;
    boolean resourcepacks = false;


    public WorldExportScreen(Screen parent, LevelStorageSource.LevelStorageAccess levelAccess) {
        super(parent);

        this.levelAccess = levelAccess;
    }

    @Override
    protected Component getName() {
        return Component.empty();
    }

    @Override
    protected void initFooter(int i, int i1) {
        int number = BConfigHelper.buttonWidth(BConfigHelper.ButtonType.SMALL);

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

        addConfigLine(new BCustomButton(Component.translatable("button.mcshare.path")) {
            @Override
            protected void buttonClicked(BConfigButton<?> bConfigButton) {
                String path = WorldExporter.showFileDialog(levelAccess.getLevelId());

                if (path != null) currentPath = Path.of(path);
            }
        });

        label = (FileLabel) addConfigLine(new FileLabel(Component.translatable("label.mcshare.nofile")));

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

    }

    private void startExport(Button ignored) {
        WorldExporter.ExportOptions options = new WorldExporter.ExportOptions()
                .setExportPath(currentPath)
                .setWorldPath(levelAccess.getLevelPath(LevelResource.ROOT))
                .setIncludeDatapacks(datapacks)
                .setIncludeResourcepacks(resourcepacks)
                .setExportingScreen(new ExportingScreen(parent, levelAccess, currentPath));

        WorldExporter.startExport(options);
    }


    @Override
    public void tick() {
        super.tick();

        export.active = currentPath != null;
        label.updatePath(currentPath);
    }

    private void space() {
        addConfigLine(new BSpacingEntry());
    }
}
