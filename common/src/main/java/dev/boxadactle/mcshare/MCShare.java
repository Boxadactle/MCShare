package dev.boxadactle.mcshare;

import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.ModLogger;
import dev.boxadactle.boxlib.util.function.EmptyMethod;
import dev.boxadactle.mcshare.gui.importing.WorldImportScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class MCShare
{
	public static final String MOD_NAME = "MCShare";
	public static final String MOD_ID = "mcshare";
	public static final String MOD_VERSION = "1.0.0";
	public static final String VERSION_STRING = MOD_NAME + " v" + MOD_VERSION;

	public static final ModLogger LOGGER = new ModLogger(MOD_NAME);

	public static final int LIST_SHIFT = 30;
	public static final int BUTTONS_SIZE = 120;
	public static final int BUTTON_PADDING = 3;

	public static final String WORLD_EXTENSION = ".world";

	public static void init() {
		LOGGER.info("Sucessfully initialized %s", VERSION_STRING);
	}

	public static Path getWorldFolder() {
		return Path.of(ClientUtils.getClient().gameDirectory.getAbsolutePath(), "saves");
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
