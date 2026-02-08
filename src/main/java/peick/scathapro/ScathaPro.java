package peick.scathapro;

import net.fabricmc.api.ClientModInitializer;
import peick.scathapro.events.OverlayHandler;
import peick.scathapro.events.KeybindHandler;   // ← manglende import
import peick.scathapro.managers.Config;
import peick.scathapro.managers.AlertModeManager;
import peick.scathapro.variables.ScathaVariables;

public class ScathaPro implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
    }
}
    public static final String MODID = "scathapro"; 
    public static ScathaConfig CONFIG;
    private static ScathaPro INSTANCE;

    public final Config config;
    public final ScathaVariables variables;
    public final AlertModeManager alertModeManager;

    public ScathaPro() {
        INSTANCE = this;

        this.config = new Config();
        this.variables = new ScathaVariables();
        this.alertModeManager = new AlertModeManager(this);
        ScathaProCommand.register();
    }

    @Override
    public void onInitializeClient() {
        CONFIG = new ScathaConfig(); 
        CONFIG.ensureDefaults();

        OverlayHandler.init(this);
        KeybindHandler.init(this);   // ← virker nu
    }

    public static ScathaPro getInstance() {
        return INSTANCE;
    }

    public Config getConfig() {
        return config;
    }

    public AlertModeManager getAlertModeManager() {
        return alertModeManager;
    }

    public ScathaVariables getVariables() {
        return variables;
    }

    public boolean isInCrystalHollows() {
        return true;
    }

    public boolean isScappaModeActive() {
        return false;
    }
}