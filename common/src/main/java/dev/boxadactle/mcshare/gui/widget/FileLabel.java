package dev.boxadactle.mcshare.gui.widget;

import dev.boxadactle.boxlib.config.gui.BConfigButton;
import dev.boxadactle.boxlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class FileLabel extends BConfigButton<Object> {
    Component message;

    public FileLabel(Component message) {
        super(message, null, null);
        this.message = message;
    }

    public void render(GuiGraphics p_93657_, int mouseX, int mouseY, float delta) {
        RenderUtils.drawTextCentered(p_93657_, this.message, this.getX() + this.width / 2, this.getY() + 5);
    }

    public void onClick(double mouseX, double mouseY) {
    }

    protected Object changeValue(Object input) {
        return null;
    }

    public void updatePath(@Nullable Path path) {
        if (path != null) {
            message = Component.translatable("label.mcshare.selected", path.toFile().getAbsolutePath());
        } else {
            message = Component.translatable("label.mcshare.nofile");
        }
    }
}