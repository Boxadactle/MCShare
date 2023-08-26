package dev.boxadactle.mcshare;

import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.ModLogger;

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
}
