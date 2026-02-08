package peick.scathapro.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import peick.scathapro.ScathaPro;
import peick.scathapro.overlay.Overlay;

public class OverlayHandler {

    private static Overlay overlay;

    public static void init(ScathaPro mod) {
        overlay = new Overlay(mod);

        // Tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
            if (!ScathaPro.CONFIG.getBoolean("overlayEnabled")) return;
                overlay.tick();
            }
        });

        // Render event
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (overlay != null) {
                overlay.render(drawContext, tickDelta);
            }
        });
    }

    public static Overlay getOverlay() {
        return overlay;
    }
}