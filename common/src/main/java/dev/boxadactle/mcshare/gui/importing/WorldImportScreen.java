package dev.boxadactle.mcshare.gui.importing;

import dev.boxadactle.boxlib.config.gui.BConfigButton;
import dev.boxadactle.boxlib.config.gui.BConfigHelper;
import dev.boxadactle.boxlib.config.gui.BConfigScreen;
import dev.boxadactle.boxlib.config.gui.widget.BSpacingEntry;
import dev.boxadactle.boxlib.config.gui.widget.button.BCustomButton;
import dev.boxadactle.boxlib.config.gui.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.gui.widget.FileLabel;
import dev.boxadactle.mcshare.util.WorldImporter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.List;

public class WorldImportScreen extends BConfigScreen {

    Button confirm;

    Path selected;

    FileLabel label;

    public WorldImportScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected Component getName() {
        return Component.empty();
    }

    @Override
    protected void initFooter(int i, int i1) {
        int number = BConfigHelper.buttonWidth(BConfigHelper.ButtonType.SMALL);
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

        space();

        ((BCustomButton)(addConfigLine(new BCustomButton(Component.translatable("button.mcshare.path")) {
            @Override
            protected void buttonClicked(BConfigButton<?> bConfigButton) {
                String path = WorldImporter.showFileDialog();

                if (path != null) selected = Path.of(path);
            }
        }))).setTooltip(Tooltip.create(Component.translatable("message.mcshare.drag")));

        label = (FileLabel) addConfigLine(new FileLabel(Component.empty()));

    }

    @Override
    public void onFilesDrop(List<Path> list) {
        super.onFilesDrop(list);

        Path file = list.get(0);

        if (file.getFileName().toString().endsWith(MCShare.WORLD_EXTENSION)) selected = file;
    }

    @Override
    public void tick() {
        super.tick();

        confirm.active = selected != null;
        label.updatePath(selected);
    }

    private void space() {
        addConfigLine(new BSpacingEntry());
    }

    private void startImport(Button b) {
        WorldImporter.ImportOptions options = new WorldImporter.ImportOptions()
                .setPath(selected)
                .setScreen(new ImportingScreen(parent, selected.getFileName().toString()));

        WorldImporter.startImport(options);
    }

}
