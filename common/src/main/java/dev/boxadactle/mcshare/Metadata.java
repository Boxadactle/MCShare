package dev.boxadactle.mcshare;

public record Metadata(int fileVersion, String gameVersion, String folderName) {

    public static final int CURRENT_FILE_VERSION = 1;
    public static final String WORLD_EXTENSION = ".mcshare";
    public static final String METADATA_FILE_NAME = "mcshare.metadata";

}
