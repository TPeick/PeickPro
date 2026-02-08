package peick.scathapro;

import net.fabricmc.api.ClientModInitializer;
import peick.scathapro.events.OverlayHandler;
import peick.scathapro.events.KeybindHandler;

public class ScathaProClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ScathaPro mod = ScathaPro.getInstance();

        // Registrer overlay + tick + render
        OverlayHandler.init(mod);

        // Registrer keybinds
        KeybindHandler.init(mod);
    }
}
