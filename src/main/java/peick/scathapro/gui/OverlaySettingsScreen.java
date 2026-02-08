package peick.scathapro.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import peick.scathapro.ScathaPro;
import peick.scathapro.managers.Config;
import peick.scathapro.overlay.Overlay;
import peick.scathapro.overlay.OverlayElement;
import peick.scathapro.overlay.enums.OverlayPosition;

import java.util.HashMap;
import java.util.Map;

public class OverlaySettingsScreen extends Screen {

    private final Screen parent;
    private final ScathaPro mod;

    private final Map<ClickableWidget, OverlayElement> widgetToOverlayElement = new HashMap<>();

    // Scroll support
    private int scrollOffset = 0;
    private int maxScroll = 0;

    public OverlaySettingsScreen(Screen parent, ScathaPro mod) {
        super(Text.literal("ScathaPro Settings"));
        this.parent = parent;
        this.mod = mod;
    }

    @Override
    protected void init() {
        widgetToOverlayElement.clear();
        this.clearChildren();

        int centerX = this.width / 2;
        int y = this.height / 4 - scrollOffset;

        //
        // ============================================================
        // DYNAMIC TOGGLES (from overlay.toggleableOverlayElements)
        // ============================================================
        //

        Overlay overlay = mod.overlay;

        for (Overlay.ToggleableOverlayElement t : overlay.getToggleableElements()) {

            boolean enabled = t.isEnabled(mod);

            CheckboxWidget box = new CheckboxWidget(
                    centerX - 100, y, 20, 20,
                    Text.literal(t.elementName),
                    enabled
            );

            box.setTooltip(Tooltip.of(Text.literal(t.description)));

            box.onPress = () -> {
                boolean newValue = !t.isEnabled(mod);
                t.setEnabled(mod, newValue);
                box.setChecked(newValue);
            };

            this.addDrawableChild(box);

            widgetToOverlayElement.put(box, t.element);

            y += 24;
        }

        //
        // ============================================================
        // POSITION BUTTON
        // ============================================================
        //

        OverlayPosition currentPos =
                mod.getConfig().getEnum(Config.Key.overlayPosition, OverlayPosition.class);
        if (currentPos == null) currentPos = OverlayPosition.TOP_LEFT;

        ButtonWidget posBtn = ButtonWidget.builder(
                Text.literal("Position: " + currentPos.name()),
                button -> {
                    OverlayPosition[] values = OverlayPosition.values();
                    int next = (currentPos.ordinal() + 1) % values.length;
                    OverlayPosition newPos = values[next];

                    mod.getConfig().set(Config.Key.overlayPosition, newPos.name());
                    button.setMessage(Text.literal("Position: " + newPos.name()));
                    mod.overlay.updatePosition();
                }
        ).dimensions(centerX - 100, y + 10, 200, 20).build();

        this.addDrawableChild(posBtn);
        y += 40;

        //
        // ============================================================
        // SCALE SLIDER
        // ============================================================
        //

        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20,
                Text.literal("Scale: " + getScaleText()),
                (mod.getConfig().getDouble(Config.Key.overlayScale) - 0.5) / 1.5) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Scale: " + getScaleText()));
            }

            @Override
            protected void applyValue() {
                double scaled = 0.5 + this.value * 1.5;
                mod.getConfig().set(Config.Key.overlayScale, scaled);
                mod.overlay.updateScale();
            }
        });

        y += 40;

        //
        // ============================================================
        // RESET POSITION BUTTON
        // ============================================================
        //

        ButtonWidget resetBtn = ButtonWidget.builder(
                Text.literal("Reset Position"),
                button -> {
                    mod.getConfig().set(Config.Key.overlayPosition, "TOP_LEFT");
                    mod.getConfig().set(Config.Key.overlayX, -1D);
                    mod.getConfig().set(Config.Key.overlayY, -1D);

                    mod.overlay.updatePosition();
                    button.setMessage(Text.literal("Position Reset"));
                }
        ).dimensions(centerX - 100, y, 200, 20).build();

        resetBtn.setTooltip(Tooltip.of(Text.literal("Gendan overlayets standardplacering.")));
        this.addDrawableChild(resetBtn);

        y += 40;

        //
        // ============================================================
        // DONE BUTTON
        // ============================================================
        //

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> close()
        ).dimensions(centerX - 100, y, 200, 20).build());

        //
        // Scroll height
        //
        maxScroll = Math.max(0, y - (this.height - 80));
    }

    //
    // ============================================================
    // SCROLLING
    // ============================================================
    //

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollOffset -= amount * 20;

        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        init(); // rebuild UI with new scroll offset
        return true;
    }

    //
    // ============================================================
    // HIGHLIGHT SYSTEM
    // ============================================================
    //

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        mod.overlay.clearHighlight();

        for (ClickableWidget widget : this.children()) {
            if (widget.isMouseOver(mouseX, mouseY)) {
                OverlayElement linked = widgetToOverlayElement.get(widget);
                if (linked != null) {
                    mod.overlay.highlightElement(linked);
                }
            }
        }
    }

    //
    // ============================================================
    // HELPERS
    // ============================================================
    //

    private String getScaleText() {
        return String.format("%.2f", mod.getConfig().getDouble(Config.Key.overlayScale));
    }

    @Override
    public void close() {
        mod.getConfig().save();
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "ScathaPro Settings",
                this.width / 2, 20, 0xFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }
}
