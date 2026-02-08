package peick.scathapro.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import peick.scathapro.gui.OverlaySettingsScreen;
import peick.scathapro.ScathaPro;
import net.minecraft.client.MinecraftClient;

public class ScathaProCommand {

    public static void register() {
        ClientCommandManager.DISPATCHER.register(
                ClientCommandManager.literal("scathapro")
                        .executes(context -> {
                            MinecraftClient.getInstance().setScreen(new OverlaySettingsScreen(ScathaPro.getInstance()));
                            return 1;
                        })
        );
    }
}