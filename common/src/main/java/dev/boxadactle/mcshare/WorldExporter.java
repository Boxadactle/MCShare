package dev.boxadactle.mcshare;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class WorldExporter {

    private static Path createMetadataFile(String folderName) {
        Metadata metadata = new Metadata(Metadata.CURRENT_FILE_VERSION, ClientUtils.getGameVersion(), folderName);

        Gson gson = new GsonBuilder().create();

        String json = gson.toJson(metadata);
        Path metadataPath = MCShare.getTempFolder().resolve(Metadata.METADATA_FILE_NAME);

        try {
            Files.writeString(metadataPath, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MCShare.LOGGER.info("Created metadata file at " + metadataPath.toFile().getAbsolutePath());

        return metadataPath;
    }

    public static void startExport(ExportOptions options) {
        String generated = "mcshare_" + System.currentTimeMillis();
        Path metadata = createMetadataFile(generated);

        try {
            MCShare.LOGGER.info("Creating zip...");

            ZipFile zipFile = new ZipFile(options.exportPath.toFile(), options.password);

            ZipParameters parameters = new ZipParameters();
            parameters.setExcludeFileFilter((f) -> {
                boolean bl = false;

                if (!options.includeResourcepacks) bl = f.toString().contains("resources.zip");
                if (!options.includeDatapacks && !bl) bl = f.toString().contains("datapacks");

                return bl;
            });

            ZipParameters metadataParameters = new ZipParameters();
            metadataParameters.setFileNameInZip(Metadata.METADATA_FILE_NAME);

            if (options.password != null) {
                MCShare.encryption(parameters);
                MCShare.encryption(metadataParameters);
            }

            parameters.setRootFolderNameInZip(generated);
            for (File file : Objects.requireNonNull(options.worldPath.toFile().listFiles())) {
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                } else {
                    zipFile.addFile(file, parameters);
                }
            }

            zipFile.addFile(metadata.toFile(), metadataParameters);

            MCShare.LOGGER.info("Finished!");

            options.finish.run();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("locked a portion of the file")) {
                MCShare.LOGGER.warn("File has been locked, either you are on windows or this is a bug!");
                options.errored.accept("File has been locked, either Windows is being silly or this is a bug!");
            }

            else {
                MCShare.LOGGER.error("Error occurred when exporting!");
                MCShare.LOGGER.printStackTrace(e);

                options.errored.accept(e.getMessage());
            }
        }

        try {
            Files.deleteIfExists(metadata);
        } catch (Exception ignored) {}
    }

    public static class ExportOptions {

        Path exportPath;
        Path worldPath;
        boolean includeDatapacks;
        boolean includeResourcepacks;

        Runnable finish;
        Consumer<String> errored;

        char[] password = null;

        public ExportOptions() {}

        public ExportOptions setExportPath(Path exportPath) {
            if (!exportPath.endsWith(Metadata.WORLD_EXTENSION)) {
                exportPath = exportPath.resolveSibling(exportPath.getFileName() + Metadata.WORLD_EXTENSION);
            }
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

        public ExportOptions setFinished(Runnable finish) {
            this.finish = finish;
            return this;
        }

        public ExportOptions setErrored(Consumer<String> errored) {
            this.errored = errored;
            return this;
        }

        public ExportOptions setPassword(String password) {
            if (password == null || password.isBlank()) {
                this.password = null;
                return this;
            }
            this.password = password.toCharArray();
            return this;
        }
    }

}