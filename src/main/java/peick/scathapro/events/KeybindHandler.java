package peick.scathapro.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import peick.scathapro.ScathaPro;
import peick.scathapro.gui.OverlaySettingsScreen; // ← VIGTIGT IMPORT

public class KeybindHandler {

    public static KeyBinding toggleOverlay;
    public static KeyBinding openSettings;

    public static void init(ScathaPro mod) {

        toggleOverlay = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scathapro.toggle_overlay",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "key.categories.scathapro.main"
        ));

        openSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scathapro.open_settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "key.categories.scathapro.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (toggleOverlay.wasPressed()) {
                boolean current = mod.getConfig().getBoolean("overlayEnabled");
                mod.getConfig().setBoolean("overlayEnabled", !current);
            }

            if (openSettings.wasPressed()) {
                client.setScreen(new OverlaySettingsScreen(mod));
            }
        }); // ← DENNE MANGLEDE
    }
}