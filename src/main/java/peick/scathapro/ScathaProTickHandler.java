package peick.scathapro;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ScathaProTickHandler {

    private final Overlay overlay;

    public ScathaProTickHandler(Overlay overlay) {
        this.overlay = overlay;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            overlay.tick();
        });
    }
}
