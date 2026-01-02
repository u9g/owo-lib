package io.wispforest.uwu.client;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.layers.Layers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

/**
 * Example demonstrating how to add UI elements to existing screens using owo-ui Layers.
 * This example adds quick-action buttons to the chat screen for sending common commands.
 */
public class ChatScreenLayerExample {

    public static void init() {
        // Add a layer to the ChatScreen that provides quick-action buttons
        Layers.add(Containers::verticalFlow, instance -> {
            var client = MinecraftClient.getInstance();
            if (client.player == null) return;

            // Create a horizontal flow with quick command buttons
            var buttonRow = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    // Home button - sends /home command
                    .child(
                            Components.button(Text.literal("🏠 Home"), button -> {
                                sendCommand("home");
                            })
                            .horizontalSizing(Sizing.fixed(70))
                            .tooltip(Text.literal("Send /home command"))
                    )
                    // Spawn button - sends /spawn command
                    .child(
                            Components.button(Text.literal("⭐ Spawn"), button -> {
                                sendCommand("spawn");
                            })
                            .horizontalSizing(Sizing.fixed(70))
                            .tooltip(Text.literal("Send /spawn command"))
                    )
                    // Back button - sends /back command
                    .child(
                            Components.button(Text.literal("↩ Back"), button -> {
                                sendCommand("back");
                            })
                            .horizontalSizing(Sizing.fixed(70))
                            .tooltip(Text.literal("Send /back command"))
                    )
                    // TPA button - sends /tpa to nearest player (example)
                    .child(
                            Components.button(Text.literal("👤 TPA"), button -> {
                                // This just opens a message prompt - in a real mod you might show a player list
                                var player = MinecraftClient.getInstance().player;
                                if (player != null) {
                                    player.sendMessage(Text.literal("Use /tpa <player> to teleport to a player"), false);
                                }
                            })
                            .horizontalSizing(Sizing.fixed(70))
                            .tooltip(Text.literal("Teleport request help"))
                    )
                    .gap(4)
                    .padding(Insets.of(5))
                    .surface(Surface.DARK_PANEL)
                    .positioning(Positioning.relative(50, 0))
                    .margins(Insets.top(5));

            // Add the button row to the screen
            instance.adapter.rootComponent.child(buttonRow);

        }, ChatScreen.class);
    }

    /**
     * Sends a chat command (prefixed with /).
     */
    private static void sendCommand(String command) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            // Close the chat screen first
            client.setScreen(null);
            // Send the command
            client.player.networkHandler.sendChatCommand(command);
        }
    }
}
