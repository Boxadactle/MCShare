package dev.boxadactle.mcshare.util;

import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.gui.exporting.ExportingScreen;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.file.Path;

public class WorldExporter {

    public static String showFileDialog(String worldName) {
        MemoryStack s = MemoryStack.stackPush();

        PointerBuffer filters = s.mallocPointer(1);
        filters.put(s.UTF8("*" + MCShare.WORLD_EXTENSION));

        Path defaultPath = Path.of(System.getProperty("user.home"), worldName + MCShare.WORLD_EXTENSION);

        String result = TinyFileDialogs.tinyfd_saveFileDialog(
                "Export Minecraft World",
                defaultPath.toFile().getAbsolutePath(),
                filters,
                "MCShare File"
        );

        s.pop();

        return result;
    }

    public static void startExport(ExportOptions options) {
        try {
            ClientUtils.getClient().forceSetScreen(options.screen);

            MCShare.LOGGER.info("Creating zip...");

            ZipFile zipFile = new ZipFile(options.exportPath.toFile());

            ZipParameters parameters = new ZipParameters();
            parameters.setExcludeFileFilter((f) -> {
                boolean bl = false;

                if (!options.includeResourcepacks) bl = f.toString().contains("resources.zip");
                if (!options.includeDatapacks && !bl) bl = f.toString().contains("datapacks");

                return bl;
            });

            zipFile.addFolder(options.worldPath.toFile(), parameters);

            MCShare.LOGGER.info("Finished!");
            options.screen.setFinished();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("locked a portion of the file")) {
                MCShare.LOGGER.warn("File has been locked, either you are on windows or this is a bug!");
                options.screen.setFinished();
            }

            else {
                MCShare.LOGGER.error("Error occurred when exporting!");
                MCShare.LOGGER.printStackTrace(e);

                options.screen.setErrored(e.getMessage());
            }
        }
    }

    public static class ExportOptions {

        Path exportPath;
        Path worldPath;
        boolean includeDatapacks;
        boolean includeResourcepacks;

        ExportingScreen screen;

        public ExportOptions() {}

        public ExportOptions setExportPath(Path exportPath) {
            this.exportPath = exportPath;
            return this;
        }

        public ExportOptions setWorldPath(Path worldPath) {
            this.worldPath = worldPath;
            return this;
        }

        public ExportOptions setIncludeResourcepacks(boolean includeResourcepacks) {
            this.includeResourcepacks = includeResourcepacks;
            return this;
        }

        public ExportOptions setIncludeDatapacks(boolean includeDatapacks) {
            this.includeDatapacks = includeDatapacks;
            return this;
        }

        public ExportOptions setExportingScreen(ExportingScreen screen) {
            this.screen = screen;
            return this;
        }
    }

}