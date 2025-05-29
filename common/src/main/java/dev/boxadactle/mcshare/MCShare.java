package dev.boxadactle.mcshare;

import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.ModLogger;
import dev.boxadactle.mcshare.gui.WorldImportScreen;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

public class MCShare {
	public static final String MOD_NAME = "MCShare";
	public static final String MOD_ID = "mcshare";
	public static final String MOD_VERSION = "1.0.0";
	public static final String VERSION_STRING = MOD_NAME + " v" + MOD_VERSION;

	public static final ModLogger LOGGER = new ModLogger(MOD_NAME);

	public static final int LIST_SHIFT = 30;
	public static final int BUTTONS_SIZE = 120;
	public static final int BUTTON_PADDING = 3;

	public static void init() {
		LOGGER.info("Sucessfully initialized %s", VERSION_STRING);
	}

	public static Path getWorldFolder() {
		return Path.of(ClientUtils.getClient().gameDirectory.getAbsolutePath(), "saves");
	}

	public static Path getTempFolder() {
		try {
			return Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath();
		} catch (Exception e) {
			LOGGER.error("Failed to get temp path", e);
			return Path.of(System.getProperty("user.home"));
		}
	}

	public static Path getDesktop() {
		try {
			return Path.of(System.getProperty("user.home"), "Desktop").toAbsolutePath();
		} catch (Exception e) {
			LOGGER.error("Failed to get desktop path", e);
			return Path.of(System.getProperty("user.home"));
		}
	}

	public static void rmrf(Path path) throws IOException {
		if (Files.exists(path)) {
			Files.walk(path)
				.sorted(Comparator.reverseOrder())
				.forEach(p -> {
					try {
						Files.delete(p);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
		}
	}

	public static String generateImport() {
        return "mcshare" + UUID.randomUUID().toString().split("-")[0];
	}

	public static boolean isNewerVersion(String version) {
		String currentVersion = ClientUtils.getGameVersion();

		if (currentVersion == null || version == null) {
			return false;
		}

		String[] currentParts = currentVersion.split("\\.");
		String[] versionParts = version.split("\\.");
		for (int i = 0; i < Math.min(currentParts.length, versionParts.length); i++) {
			int currentPart = Integer.parseInt(currentParts[i]);
			int versionPart = Integer.parseInt(versionParts[i]);

			if (currentPart < versionPart) {
				return true;
			} else if (currentPart > versionPart) {
				return false;
			}
		}

		return currentParts.length < versionParts.length;
	}

	public static void encryption(ZipParameters parameters) {
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(EncryptionMethod.AES);
		parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
	}

	public static class ImportTab extends GridLayoutTab {

		Screen parent;

		public ImportTab(Screen parent) {
			super(Component.translatable("screen.mcshare.importworld"));

			this.parent = parent;

			GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(8).createRowHelper(1);

			gridlayout$rowhelper.addChild(Button.builder(Component.translatable("button.mcshare.import"), this::openImportScreen).width(210).build());
		}

		private void openImportScreen(Button b) {
			ClientUtils.setScreen(new WorldImportScreen(parent));
		}
	}
}
