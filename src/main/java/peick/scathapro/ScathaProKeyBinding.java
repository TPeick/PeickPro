package peick.scathapro.miscellaneous;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import peick.scathapro.ScathaPro;

public class ScathaProKeyBinding {

    public enum Category {
        MAIN("main"),
        PLAYER_ROTATION("playerRotation"),
        PLAYER_CONTROLS("playerControls"),
        SCREENSHOTS("screenshots");

        private final String categoryKey;

        Category(String categoryKey) {
            this.categoryKey = categoryKey;
        }

        public String getCategoryID() {
            return "key.categories." + ScathaPro.MODID + "." + categoryKey;
        }

        public String getShortNameTranslationKey() {
            return getCategoryID() + ".short";
        }
    }

    public final KeyBinding binding;
    public final Category category;

    public ScathaProKeyBinding(String id, int defaultKey, Category category) {
        this.category = category;

        this.binding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key." + ScathaPro.MODID + "." + id,
                        InputUtil.Type.KEYSYM,
                        defaultKey,
                        category.getCategoryID()
                )
        );
    }

    public boolean isPressed() {
        return binding.wasPressed();
    }
}