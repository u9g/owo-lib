package io.wispforest.nodescriptexample;

import io.wispforest.owo.nodescript.ui.NodeEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * Example mod demonstrating the NodeScript visual scripting system.
 *
 * <p>This example shows how to:</p>
 * <ul>
 *   <li>Open the node editor screen with a custom keybinding</li>
 *   <li>Create visual scripts that react to Fabric events</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>Press <b>M</b> (or <b>N</b> from the main owo keybinding) to open the NodeScript editor.</p>
 *
 * <h2>Example: Block chat messages containing "spam"</h2>
 * <ol>
 *   <li>Open the editor (press M or N)</li>
 *   <li>From the "Events" category, add an "Allow Chat Event" node</li>
 *   <li>From the "Logic" category, add a "String Includes" node</li>
 *   <li>Connect "Allow Chat Event" → Message to "String Includes" → String</li>
 *   <li>From the "Constants" category, add a "String Constant" node</li>
 *   <li>Set its value to "spam" in the Properties panel</li>
 *   <li>Connect "String Constant" → Value to "String Includes" → Search</li>
 *   <li>From the "Logic" category, add a "NOT" node</li>
 *   <li>Connect "String Includes" → Result to "NOT" → Value</li>
 *   <li>Connect "NOT" → Result to "Allow Chat Event" → Result</li>
 *   <li>Click "Save" and "Execute" to activate the script</li>
 * </ol>
 *
 * <p>Now any chat message containing "spam" will be blocked!</p>
 *
 * <p><b>Note:</b> The main owo-lib already registers the N key for opening the editor.
 * This example uses M as an alternative keybinding to demonstrate custom integration.</p>
 */
public class NodeScriptExampleClient implements ClientModInitializer {

    /**
     * Keybinding to open the NodeScript editor.
     * Uses M key to avoid conflict with owo-lib's default N key binding.
     */
    private static KeyMapping OPEN_NODESCRIPT_EDITOR;

    @Override
    public void onInitializeClient() {
        // Note: The NodeScript system is already initialized by owo-lib (OwoClient).
        // The default keybinding (N) is registered there.
        // This example registers an additional keybinding (M) to demonstrate
        // how mods can integrate with the NodeScript editor.

        // Register an alternative keybinding to open the editor
        OPEN_NODESCRIPT_EDITOR = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.nodescriptexample.open_editor",
            GLFW.GLFW_KEY_M,  // Use M to avoid conflict with owo's N key
            "key.categories.nodescriptexample"
        ));

        // Handle the keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_NODESCRIPT_EDITOR.consumeClick()) {
                if (client.screen == null) {
                    // Open the NodeScript editor
                    client.setScreen(new NodeEditorScreen());

                    // Optional: Show a welcome message
                    if (client.player != null) {
                        client.player.displayClientMessage(
                            Component.literal("NodeScript Editor opened! Create visual scripts to handle events."),
                            true
                        );
                    }
                }
            }
        });
    }

    /**
     * Programmatically open the NodeScript editor.
     * Can be called from anywhere in your mod.
     */
    public static void openEditor() {
        Minecraft.getInstance().setScreen(new NodeEditorScreen());
    }
}
