package peick.scathapro.overlay.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class OverlayText extends OverlayElement {

    private String text;
    private int color;

    public OverlayText(String text, int color, int x, int y, float scale) {
        super(x, y, scale);
        this.text = text;
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void drawSpecific(MatrixStack matrices) {
        if (text == null || text.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Brug DrawContext fra HUD-rendereren
        DrawContext ctx = DrawContext.of(client, matrices);

        float opacity = (float) ScathaPro.CONFIG.getDouble("overlayOpacity");
        int alpha = (int) (opacity * 255);
        int colorWithAlpha = (alpha << 24) | (color & 0x00FFFFFF);

        ctx.drawTextWithShadow(client.textRenderer, text, 0, 0, colorWithAlpha);
    }

    @Override
    public int getWidth() {
        if (text == null) return 0;
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }

    @Override
    public int getHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }
}