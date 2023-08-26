package dev.boxadactle.mcshare.util;

import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.mcshare.MCShare;
import dev.boxadactle.mcshare.gui.importing.ImportingScreen;
import net.lingala.zip4j.ZipFile;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.file.Path;

public class WorldImporter {

    public static String showFileDialog() {

        MemoryStack s = MemoryStack.stackPush();

        PointerBuffer filters = s.mallocPointer(1);
        filters.put(s.UTF8("*.mcshare"));

        String result = TinyFileDialogs.tinyfd_openFileDialog(
                "Select World to Import",
                System.getProperty("user.home"),
                filters,
                "MCShare file",
                false
        );

        s.pop();

        return result;
    }

    public static void startImport(ImportOptions options) {
        try {

            ClientUtils.getClient().forceSetScreen(options.screen);

            MCShare.LOGGER.info("Starting extract...");

            ZipFile file = new ZipFile(options.path.toFile());

            Path p = MCShare.getWorldFolder();

            file.extractAll(p.toString());

            options.screen.setFinished(true);
        } catch (Exception e) {
            MCShare.LOGGER.error("An error occured!");
            MCShare.LOGGER.printStackTrace(e);

            options.screen.setErrored(true, e.getMessage());
        }
    }

    public static class ImportOptions {

        Path path;

        ImportingScreen screen;

        public ImportOptions() {}

        public ImportOptions setPath(Path path) {
            this.path = path;
            return this;
        }

        public ImportOptions setScreen(ImportingScreen screen) {
            this.screen = screen;
            return this;
        }
    }

}