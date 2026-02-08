package peick.scathapro.overlay.elements;

import net.minecraft.client.util.math.MatrixStack;
import java.util.ArrayList;
import java.util.List;

public class OverlayContainer extends OverlayElement {

    protected final List<OverlayElement> children = new ArrayList<>();

    public OverlayContainer(int x, int y, float scale) {
        super(x, y, scale);
    }

    public OverlayContainer add(OverlayElement element) {
        children.add(element);
        return this;
    }

    @Override
    protected void drawSpecific(MatrixStack matrices) {
        for (OverlayElement child : children) {
            if (child.isVisible()) {
                child.draw(matrices);
            }
        }
    }

    @Override
    public int getWidth() {
        int maxWidth = 0;

        for (OverlayElement child : children) {
            if (!child.isVisible()) continue;

            int childWidth = child.getScaledWidth() + child.marginRight;
            if (childWidth > maxWidth) {
                maxWidth = childWidth;
            }
        }

        return maxWidth;
    }

    @Override
    public int getHeight() {
        int maxHeight = 0;

        for (OverlayElement child : children) {
            if (!child.isVisible()) continue;

            int childHeight = child.getScaledHeight() + child.marginBottom;
            if (childHeight > maxHeight) {
                maxHeight = childHeight;
            }
        }

        return maxHeight;
    }
}
