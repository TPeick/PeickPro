package peick.scathapro.overlay.elements;

import net.minecraft.client.util.math.MatrixStack;
import java.util.ArrayList;
import java.util.List;

public class DynamicOverlayContainer extends OverlayElement {

    public enum Direction {
        VERTICAL,
        HORIZONTAL
    }

    private final List<OverlayElement> children = new ArrayList<>();
    private final Direction direction;
    public int padding = 0;

    public DynamicOverlayContainer(int x, int y, float scale, Direction direction) {
        super(x, y, scale);
        this.direction = direction;
    }

    public DynamicOverlayContainer add(OverlayElement element) {
        children.add(element);
        return this;
    }

    public List<OverlayElement> getChildren() {
        return children;
    }

    @Override
    protected void drawSpecific(MatrixStack matrices) {
        int offsetX = 0;
        int offsetY = 0;

        for (OverlayElement child : children) {
            if (!child.isVisible()) continue;

            matrices.push();
            matrices.translate(offsetX, offsetY, 0);
            child.draw(matrices);
            matrices.pop();

            if (direction == Direction.VERTICAL) {
                offsetY += child.getScaledHeight() + child.marginBottom + padding;
            } else {
                offsetX += child.getScaledWidth() + child.marginRight + padding;
            }
        }
    }

    @Override
    public int getWidth() {
        if (children.isEmpty()) return 0;

        if (direction == Direction.HORIZONTAL) {
            int width = 0;
            for (OverlayElement child : children) {
                if (!child.isVisible()) continue;
                width += child.getScaledWidth() + child.marginRight + padding;
            }
            return Math.max(0, width - padding);
        }

        int maxWidth = 0;
        for (OverlayElement child : children) {
            if (!child.isVisible()) continue;
            maxWidth = Math.max(maxWidth, child.getScaledWidth());
        }
        return maxWidth;
    }

    @Override
    public int getHeight() {
        if (children.isEmpty()) return 0;

        if (direction == Direction.VERTICAL) {
            int height = 0;
            for (OverlayElement child : children) {
                if (!child.isVisible()) continue;
                height += child.getScaledHeight() + child.marginBottom + padding;
            }
            return Math.max(0, height - padding);
        }

        int maxHeight = 0;
        for (OverlayElement child : children) {
            if (!child.isVisible()) continue;
            maxHeight = Math.max(maxHeight, child.getScaledHeight());
        }
        return maxHeight;
    }
}