package peick.scathapro.overlay.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import peick.scathapro.ScathaPro;

public class OverlayImage extends OverlayElement {

    private Identifier texture;
    private int width;
    private int height;

    public OverlayImage(String path, int x, int y, int width, int height, float scale) {
        super(x, y, scale);
        this.texture = Identifier.of("scathapro", path);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void drawSpecific(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        DrawContext ctx = DrawContext.of(client, matrices);

        float opacity = (float) ScathaPro.CONFIG.getDouble("overlayOpacity");

        ctx.setShaderColor(1f, 1f, 1f, opacity);
        ctx.drawTexture(texture, 0, 0, 0, 0, width, height, width, height);
        ctx.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }
}