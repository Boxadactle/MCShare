package dev.boxadactle.mcshare.gui;

import dev.boxadactle.boxlib.gui.config.BOptionTextField;

import java.nio.file.Path;
import java.util.function.Consumer;

public class PathField extends BOptionTextField<Path> {
    public PathField(Path value, Consumer<Path> function) {
        super(value, function);

        setMaxLength(512);
        insertText(from(value));
    }

    public void valid() {
        setInvalid(false);
    }

    @Override
    public Path to(String input) {
        try {
            Path a = Path.of(input).toAbsolutePath();

            // make sure that the path follows a valid format
            setInvalid(a.getFileName() == null);

            return a;
        } catch (Exception ignored) {
            setInvalid(true);
            return null;
        }
    }

    boolean delta = false;
    @Override
    public String from(Path input) {
        if (!delta) {
            delta = true;
            return "";
        }
        return input.toString();
    }
}