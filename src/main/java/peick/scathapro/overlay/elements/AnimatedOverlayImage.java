package peick.scathapro.overlay.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import peick.scathapro.ScathaPro;

public class AnimatedOverlayImage extends OverlayElement {

    private Identifier texture;
    private int textureWidth;
    private int textureHeight;

    private int frameCount = 1;
    private int currentFrame = 0;
    private long frameDurationMs = 100;
    private long lastFrameTime = 0;

    public AnimatedOverlayImage(int x, int y, float scale) {
        super(x, y, scale);
    }

    public void setImage(String path, int texW, int texH) {
        if (path == null) {
            this.texture = null;
            return;
        }

        this.texture = Identifier.of("scathapro", path);
        this.textureWidth = texW;
        this.textureHeight = texH;
    }

    public void setAnimation(int frameCount, long frameDurationMs) {
        this.frameCount = Math.max(1, frameCount);
        this.frameDurationMs = Math.max(1, frameDurationMs);
    }

    private void updateAnimation() {
        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= frameDurationMs) {
            currentFrame = (currentFrame + 1) % frameCount;
            lastFrameTime = now;
        }
    }

    @Override
    protected void drawSpecific(MatrixStack matrices) {
        if (texture == null) return;

        updateAnimation();

        MinecraftClient client = MinecraftClient.getInstance();
        DrawContext ctx = DrawContext.of(client, matrices);

        float opacity = (float) ScathaPro.CONFIG.getDouble("overlayOpacity");
        ctx.setShaderColor(1f, 1f, 1f, opacity);

        int frameHeight = textureHeight / frameCount;
        int frameY = currentFrame * frameHeight;

        ctx.drawTexture(texture, 0, 0, 0, frameY, textureWidth, frameHeight, textureWidth, textureHeight);

        ctx.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public int getWidth() { return textureWidth; }

    @Override
    public int getHeight() { return textureHeight / Math.max(1, frameCount); }
}