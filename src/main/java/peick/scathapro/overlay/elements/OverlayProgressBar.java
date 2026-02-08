package peick.scathapro.overlay.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import peick.scathapro.ScathaPro;

public class OverlayProgressBar extends OverlayElement {

    private int width;
    private int height;
    private float progress = 0f;
    private int backgroundColor;
    private int fillColor;

    public OverlayProgressBar(int x, int y, int width, int height, float scale, int backgroundColor, int fillColor) {
        super(x, y, scale);
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.fillColor = fillColor == -1 ? 0xFFFFFFFF : fillColor;
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
    }

    @Override
    protected void drawSpecific(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        DrawContext ctx = DrawContext.of(client, matrices);

        float opacity = (float) ScathaPro.CONFIG.getDouble("overlayOpacity");
        int alpha = (int) (opacity * 255);

        int bg = (alpha << 24) | (backgroundColor & 0x00FFFFFF);
        int fill = (alpha << 24) | (fillColor & 0x00FFFFFF);

        ctx.fill(0, 0, width, height, bg);

        int fillWidth = (int) (width * progress);
        ctx.fill(0, 0, fillWidth, height, fill);
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }
}