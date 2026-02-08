package peick.scathapro.overlay.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class AnomalousDesireEffectProgressBar extends OverlayProgressBar {

    private static final int EFFECT_COLOR = 0x80FF00FF;   // semi‑transparent purple
    private static final int BACKGROUND_COLOR = 0x40000000;

    public AnomalousDesireEffectProgressBar(int x, int y, int width, int height, float scale) {
        super(x, y, width, height, scale, BACKGROUND_COLOR, EFFECT_COLOR);
    }

    @Override
    protected void drawSpecific(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        DrawContext ctx = new DrawContext(client, client.getBufferBuilders().getEntityVertexConsumers());

        // Background
        ctx.fill(0, 0, getWidth(), getHeight(), BACKGROUND_COLOR);

        // Fill (progress)
        int fillWidth = (int) (getWidth() * progress);
        ctx.fill(0, 0, fillWidth, getHeight(), EFFECT_COLOR);
    }
}