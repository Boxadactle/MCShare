package dev.boxadactle.mcshare;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.lingala.zip4j.ZipFile;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class WorldImporter {

    private static Metadata readMetadata(Path path) {
        try {
            String data = Files.readString(path);

            Gson gson = new GsonBuilder().create();
            Metadata metadata = gson.fromJson(data, Metadata.class);

            if (metadata == null) {
                MCShare.LOGGER.error("Metadata is null! This is likely an invalid world file!");
                return null;
            }

            return metadata;
        } catch (Exception e) {
            MCShare.LOGGER.error("Failed to read metadata from the imported world!", e);
            return null;
        }
    }

    private static void moveWorldFiles(Path extracted, ImportOptions options, @Nullable Metadata metadata) {
        try {
            Path worldPath;

            MCShare.LOGGER.info("Moving world files...");

            if (metadata != null) {
                worldPath = extracted.resolve("./" + metadata.folderName());
            } else {
                if (Files.exists(extracted.resolve("level.dat"))) {
                    worldPath = extracted;
                } else {
                    MCShare.LOGGER.warn("Provided extract is not a minecraft backup or mcshare file!");
                    worldPath = Files.list(extracted)
                            .filter(Files::isDirectory)
                            .findFirst()
                            .orElseThrow(() -> new IOException("No world folder found in the extracted files."));
                }
            }

            if (Files.exists(worldPath)) {
                Files.move(worldPath, MCShare.getWorldFolder().resolve(MCShare.generateImport()), StandardCopyOption.REPLACE_EXISTING);
                MCShare.LOGGER.info("Moved world files to " + MCShare.getWorldFolder().toAbsolutePath());

                MCShare.rmrf(extracted);

                MCShare.LOGGER.info("Finished!");
                options.finish.run();
            } else {
                throw new IOException("World folder does not exist in the extracted files: " + worldPath);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("password")) {
                MCShare.LOGGER.warn("Incorrect password provided!");
                options.errored.accept("Incorrect password provided!");
                return;
            }

            MCShare.LOGGER.error("An error occurred while moving world files!", e);
            options.errored.accept(e.getMessage());
        }
    }

    public static void startImport(ImportOptions options) {
        try {
            MCShare.LOGGER.info("Starting extract...");

            ZipFile file = new ZipFile(options.path.toFile(), options.password);

            String generated = "mcshare_" + System.currentTimeMillis();
            Path p = MCShare.getTempFolder().resolve(generated);

            MCShare.LOGGER.info("Extracting to " + p.toAbsolutePath());

            file.extractAll(p.toString());

            Path mfile = p.resolve(Metadata.METADATA_FILE_NAME);
            MCShare.LOGGER.info("Reading metadata file at " + mfile.toAbsolutePath());
            Metadata metadata = readMetadata(mfile);

            Screen parent = ClientUtils.getCurrentScreen();

            if (metadata == null) {
                ClientUtils.confirm(
                        Component.translatable("message.mcshare.external"),
                        Component.translatable("message.mcshare.anyways"),
                        () -> a(parent, () -> moveWorldFiles(p, options, null)),
                        () -> a(parent, () -> options.errored.accept(I18n.get("label.mcshare.canceled")))
                );
                return;
            } else if (metadata.fileVersion() > Metadata.CURRENT_FILE_VERSION) {
                ClientUtils.confirm(
                        Component.translatable("message.mcshare.error.newer"),
                        Component.translatable("message.mcshare.anyways"),
                        () -> a(parent, () -> moveWorldFiles(p, options, metadata)),
                        () -> a(parent, () -> options.errored.accept(I18n.get("label.mcshare.canceled")))
                );
                return;
            } else if (MCShare.isNewerVersion(metadata.gameVersion())) {
                ClientUtils.confirm(
                        Component.translatable("message.mcshare.error.newer_game", metadata.gameVersion()),
                        Component.translatable("message.mcshare.anyways"),
                        () -> a(parent, () -> moveWorldFiles(p, options, metadata)),
                        () -> a(parent, () -> options.errored.accept(I18n.get("label.mcshare.canceled")))
                );
                return;
            }

            moveWorldFiles(p, options, metadata);
        } catch (Exception e) {
            MCShare.LOGGER.error("An error occured while extracting!");
            MCShare.LOGGER.printStackTrace(e);

            options.errored.accept(e.getMessage());
        }
    }

    private static void a(Screen parent, Runnable r) {
        ClientUtils.getClient().forceSetScreen(parent);
        r.run();
    }

    public static class ImportOptions {

        Path path;

        Runnable finish;
        Consumer<String> errored;

        char[] password = null;

        public ImportOptions() {}

        public ImportOptions setPath(Path path) {
            this.path = path;
            return this;
        }

        public ImportOptions setFinished(Runnable finish) {
            this.finish = finish;
            return this;
        }

        public ImportOptions setErrored(Consumer<String> errored) {
            this.errored = errored;
            return this;
        }

        public ImportOptions setPassword(String password) {
            if (password == null || password.isBlank()) {
                this.password = null;
                return this;
            }
            this.password = password.toCharArray();
            return this;
        }
    }

}