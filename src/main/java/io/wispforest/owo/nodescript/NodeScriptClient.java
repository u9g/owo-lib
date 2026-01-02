package io.wispforest.owo.nodescript;

import io.wispforest.owo.nodescript.ui.NodeEditorScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side initialization for the NodeScript system.
 */
public class NodeScriptClient {

    private static KeyMapping OPEN_EDITOR_KEY;

    /**
     * Initialize the NodeScript client systems.
     * Should be called during client initialization.
     */
    public static void initialize() {
        // Initialize the script manager
        NodeScriptManager.INSTANCE.initialize();

        // Register keybinding
        OPEN_EDITOR_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.owo.nodescript_editor",
            GLFW.GLFW_KEY_N,
            "key.categories.owo"
        ));

        // Register tick event to check keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_EDITOR_KEY.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new NodeEditorScreen());
                }
            }
        });
    }

    /**
     * Open the node editor screen.
     */
    public static void openEditor() {
        Minecraft.getInstance().setScreen(new NodeEditorScreen());
    }
}
