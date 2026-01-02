# HUD and GUI Modification Examples

This document provides comprehensive examples for building common in-game widgets and modifying existing GUIs using owo-lib.

## Table of Contents

1. [In-Game HUD Widgets](#in-game-hud-widgets)
   - [Armor HUD](#armor-hud)
   - [Coordinate HUD](#coordinate-hud)
   - [Potion Effect HUD](#potion-effect-hud)
   - [Making HUD Widgets Movable](#making-hud-widgets-movable)
2. [Modifying Existing GUIs](#modifying-existing-guis)
   - [Adding Buttons to Chat Screen](#adding-buttons-to-chat-screen)

---

## In-Game HUD Widgets

The owo-lib `Hud` API allows you to display custom UI components on the in-game HUD. Components are rendered during the `HudRenderCallback` and must be explicitly positioned using absolute or relative positioning.

### Armor HUD

This example creates a simple armor display showing the player's current armor points.

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorHudExample {
    
    public static void register() {
        var hudId = Identifier.of("yourmod", "armor_hud");
        
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();
            
            return Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal("Armor")))
                .child(Components.item(Items.DIAMOND_CHESTPLATE.getDefaultStack()))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "N/A";
                    return String.valueOf(client.player.getArmor());
                })))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(5))
                .surface(Surface.PANEL)
                .positioning(Positioning.relative(5, 5)); // Top-left corner
        });
    }
}
```

**Key Points:**
- Use `Hud.add(Identifier, Supplier<Component>)` to add a HUD component
- Components must be explicitly positioned with `Positioning.relative()` or `Positioning.absolute()`
- Use `Surface.PANEL` for a background panel
- Use dynamic text with suppliers for real-time updates

### Coordinate HUD

This example displays the player's current coordinates in the world.

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CoordinateHudExample {
    
    public static void register() {
        var hudId = Identifier.of("yourmod", "coordinate_hud");
        
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();
            
            return Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal("Coordinates")).margins(Insets.bottom(3)))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "X: N/A";
                    return String.format("X: %.2f", client.player.getX());
                })))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "Y: N/A";
                    return String.format("Y: %.2f", client.player.getY());
                })))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "Z: N/A";
                    return String.format("Z: %.2f", client.player.getZ());
                })))
                .padding(Insets.of(7))
                .surface(Surface.PANEL)
                .positioning(Positioning.relative(5, 50)); // Left side, centered vertically
        });
    }
}
```

**Key Points:**
- Use lambda suppliers with `Text.literal(() -> ...)` for dynamic text that updates every frame
- Position with `Positioning.relative(x, y)` where values are percentages (0-100)
- Stack multiple labels vertically using `verticalFlow`

### Potion Effect HUD

This example displays all active potion effects with their durations.

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PotionEffectHudExample {
    
    public static void register() {
        var hudId = Identifier.of("yourmod", "potion_effect_hud");
        
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();
            
            // Create a simple effects counter display
            // For a more complex implementation that lists each effect individually,
            // consider using a custom component or updating children dynamically
            return Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal("Active Effects"))
                    .margins(Insets.bottom(3)))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "None";
                    
                    var effects = client.player.getStatusEffects();
                    if (effects.isEmpty()) return "None";
                    
                    return effects.size() + " effect" + (effects.size() > 1 ? "s" : "");
                })))
                .padding(Insets.of(5))
                .surface(Surface.PANEL)
                .positioning(Positioning.relative(95, 5)); // Top-right corner
        });
    }
}
```

**Key Points:**
- For complex dynamic content, consider creating custom components
- Use `Positioning.relative(95, 5)` to position in the top-right corner
- This example shows a simple effect counter; for detailed effect lists, implement custom update logic

---

## Making HUD Widgets Movable

The `DraggableContainer` allows users to drag and reposition HUD elements. This is perfect for letting players customize their HUD layout.

### Basic Draggable HUD Widget

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MovableArmorHudExample {
    
    public static void register() {
        var hudId = Identifier.of("yourmod", "movable_armor_hud");
        
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();
            
            // Create the content
            var content = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal("⚔ Armor ⚔")))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "N/A";
                    return "❤ " + client.player.getArmor() + " / 20";
                })))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(5));
            
            // Wrap in draggable container
            return Containers.draggable(Sizing.content(), Sizing.content(), content)
                .surface(Surface.PANEL)
                .positioning(Positioning.absolute(10, 10)); // Initial position
        });
    }
}
```

### Draggable Coordinate HUD with Custom Drag Handle

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MovableCoordinateHudExample {
    
    public static void register() {
        var hudId = Identifier.of("yourmod", "movable_coordinate_hud");
        
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();
            
            // Create the coordinate display
            var coordinateDisplay = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "X: N/A";
                    return String.format("X: %.1f", client.player.getX());
                })))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "Y: N/A";
                    return String.format("Y: %.1f", client.player.getY());
                })))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "Z: N/A";
                    return String.format("Z: %.1f", client.player.getZ());
                })))
                .padding(Insets.of(5));
            
            // Wrap in draggable container with a larger drag handle area
            return Containers.draggable(Sizing.content(), Sizing.content(), coordinateDisplay)
                .surface(Surface.PANEL)
                .foreheadSize(15) // Larger drag handle at the top
                .positioning(Positioning.absolute(10, 100)); // Initial position
        });
    }
}
```

**Key Points:**
- Wrap any component in `Containers.draggable()` to make it movable
- The `foreheadSize()` method controls the height of the draggable area at the top
- Users can click and drag the top portion of the widget to move it
- Use `Positioning.absolute()` for initial placement; the offset persists as the user drags

### Complete Example: Movable Potion Effect HUD

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MovablePotionHudExample {
    
    public static void register() {
        var hudId = Identifier.of("yourmod", "movable_potion_hud");
        
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();
            
            // Create title bar (visual indication of drag area)
            var titleBar = Containers.horizontalFlow(Sizing.content(), Sizing.fixed(12))
                .child(Components.label(Text.literal("⚗ Effects")))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .surface(Surface.DARK_PANEL)
                .padding(Insets.horizontal(8));
            
            // Create effects display
            var effectsDisplay = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "No effects";
                    
                    var effects = client.player.getStatusEffects();
                    if (effects.isEmpty()) return "No effects";
                    
                    return effects.size() + " active effect" + (effects.size() > 1 ? "s" : "");
                })))
                .padding(Insets.of(5));
            
            // Combine title and content
            var content = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(titleBar)
                .child(effectsDisplay)
                .surface(Surface.PANEL);
            
            // Make it draggable
            return Containers.draggable(Sizing.content(), Sizing.content(), content)
                .foreheadSize(12) // Match title bar height
                .positioning(Positioning.absolute(200, 10)); // Initial position
        });
    }
}
```

**Key Points:**
- Create a visual title bar to indicate where users can drag
- Match the `foreheadSize()` to your title bar height for intuitive dragging
- Use different surfaces (`Surface.DARK_PANEL`, `Surface.PANEL`) for visual distinction
- Stack components with `verticalFlow` for organized layouts

### Toggle HUD Widgets with Keybinds

You can allow players to toggle HUD widgets on and off:

```java
import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class YourClientMod implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Register a keybind
        var toggleArmorHudKey = new KeyBinding(
            "key.yourmod.toggle_armor_hud",
            GLFW.GLFW_KEY_H,
            "key.categories.yourmod"
        );
        KeyBindingHelper.registerKeyBinding(toggleArmorHudKey);
        
        var hudId = Identifier.of("yourmod", "armor_hud");
        
        // Handle toggle logic
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleArmorHudKey.wasPressed()) {
                if (Hud.hasComponent(hudId)) {
                    Hud.remove(hudId);
                } else {
                    // Register your HUD component here
                    MovableArmorHudExample.register();
                }
            }
        });
    }
}
```

---

## Modifying Existing GUIs

The owo-lib `Layers` API allows you to add custom UI components to existing Minecraft screens without modifying their code.

### Adding Buttons to Chat Screen

This example adds quick-access buttons to the chat screen for common commands like `/home`.

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.layers.Layers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

public class ChatScreenButtonsExample implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        Layers.add(Containers::verticalFlow, instance -> {
            // Create quick command buttons
            var homeButton = Components.button(
                Text.literal("/home"),
                button -> sendCommand("/home")
            );
            
            var spawnButton = Components.button(
                Text.literal("/spawn"),
                button -> sendCommand("/spawn")
            );
            
            var balanceButton = Components.button(
                Text.literal("/balance"),
                button -> sendCommand("/balance")
            );
            
            // Create a horizontal layout for the buttons
            var buttonRow = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(homeButton)
                .child(spawnButton)
                .child(balanceButton)
                .gap(5)
                .padding(Insets.of(5))
                .surface(Surface.PANEL);
            
            // Add to the screen's UI
            instance.adapter.rootComponent.child(buttonRow);
            
            // Position the buttons at the bottom-right of the screen
            buttonRow.positioning(Positioning.relative(95, 95));
            
        }, ChatScreen.class);
    }
    
    private void sendCommand(String command) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.networkHandler.sendChatCommand(command.substring(1)); // Remove '/'
        }
    }
}
```

**Key Points:**
- Use `Layers.add()` to add components to existing screens
- The first parameter is a function that creates the root component
- The second parameter is an initializer where you build your UI
- Specify which screen classes to modify as the third parameter
- Use `Positioning.relative()` for positioning relative to the screen

### Advanced: Aligning Buttons to Chat Input Field

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.ui.layers.Layers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AdvancedChatButtonsExample implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        Layers.add(Containers::verticalFlow, instance -> {
            // Create command buttons
            var commandButtons = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.button(Text.literal("🏠 Home"), btn -> sendCommand("/home")))
                .child(Components.button(Text.literal("⭐ Spawn"), btn -> sendCommand("/spawn")))
                .child(Components.button(Text.literal("💰 Balance"), btn -> sendCommand("/balance")))
                .child(Components.button(Text.literal("📜 Rules"), btn -> sendCommand("/rules")))
                .gap(3)
                .padding(Insets.of(3))
                .surface(Surface.PANEL);
            
            // Add to root
            instance.adapter.rootComponent.child(commandButtons);
            
            // Try to position above the chat input field
            // We'll look for the TextFieldWidget in the chat screen
            var chatField = instance.queryWidget(widget -> widget instanceof TextFieldWidget);
            
            if (chatField != null) {
                // Position the buttons above the chat field
                instance.alignComponentToWidget(
                    widget -> widget instanceof TextFieldWidget,
                    Layer.Instance.AnchorSide.TOP,
                    0, // No offset along the anchor
                    commandButtons
                );
            } else {
                // Fallback: position at bottom
                commandButtons.positioning(Positioning.relative(50, 95));
            }
            
        }, ChatScreen.class);
    }
    
    private void sendCommand(String command) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            // Close the chat screen first
            client.setScreen(null);
            // Send the command
            client.player.networkHandler.sendChatCommand(command.substring(1));
        }
    }
}
```

**Key Points:**
- Use `instance.queryWidget()` to find widgets in the target screen
- Use `instance.alignComponentToWidget()` to align your components to existing widgets
- `AnchorSide` determines which side of the widget to align to (TOP, BOTTOM, LEFT, RIGHT)
- This approach works with any screen that has widgets you can query

### Multiple Button Layouts

```java
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.layers.Layers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

public class ComplexChatButtonsExample implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        Layers.add(Containers::verticalFlow, instance -> {
            // Create a grid of command categories
            var teleportCommands = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.button(Text.literal("Home"), btn -> sendCommand("/home")))
                .child(Components.button(Text.literal("Spawn"), btn -> sendCommand("/spawn")))
                .child(Components.button(Text.literal("Back"), btn -> sendCommand("/back")))
                .gap(3);
            
            var infoCommands = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.button(Text.literal("Balance"), btn -> sendCommand("/balance")))
                .child(Components.button(Text.literal("Stats"), btn -> sendCommand("/stats")))
                .child(Components.button(Text.literal("Help"), btn -> sendCommand("/help")))
                .gap(3);
            
            var socialCommands = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.button(Text.literal("List"), btn -> sendCommand("/list")))
                .child(Components.button(Text.literal("Mail"), btn -> sendCommand("/mail")))
                .child(Components.button(Text.literal("Party"), btn -> sendCommand("/party")))
                .gap(3);
            
            // Combine into a vertical stack with labels
            var commandPanel = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal("Teleport")).margins(Insets.bottom(2)))
                .child(teleportCommands)
                .child(Components.label(Text.literal("Info")).margins(Insets.vertical(3)))
                .child(infoCommands)
                .child(Components.label(Text.literal("Social")).margins(Insets.vertical(3)))
                .child(socialCommands)
                .padding(Insets.of(8))
                .surface(Surface.PANEL);
            
            // Add to screen
            instance.adapter.rootComponent.child(commandPanel);
            
            // Position in the bottom-right corner
            commandPanel.positioning(Positioning.relative(95, 95));
            
        }, ChatScreen.class);
    }
    
    private void sendCommand(String command) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.setScreen(null); // Close chat
            client.player.networkHandler.sendChatCommand(command.substring(1));
        }
    }
}
```

**Key Points:**
- Organize buttons into logical groups with labels
- Use `gap()` for spacing between buttons
- Use `margins()` for spacing between sections
- Combine multiple layouts (`verticalFlow` containing `horizontalFlow`) for complex UIs
- Position with `Positioning.relative(95, 95)` for bottom-right placement

---

## Best Practices

### HUD Widgets

1. **Performance**: Use dynamic text suppliers sparingly. Consider caching values that don't need to update every frame.

2. **Positioning**: Use `Positioning.relative()` for percentage-based positioning that adapts to different screen sizes.

3. **Persistence**: For movable widgets, consider saving position data to a config file so positions persist across game sessions.

4. **Visibility**: Provide keybinds to toggle HUD elements on/off for user customization.

### GUI Modifications

1. **Compatibility**: Test with different GUI scales and screen resolutions.

2. **Widget Queries**: When using `queryWidget()`, make sure to handle the case where the widget isn't found.

3. **Commands**: Consider providing both instant execution and text insertion options for commands.

4. **Screen Lifecycle**: Remember that Layer instances are created when screens open, so initialization code runs each time.

---

## Complete Working Example

Here's a complete, minimal mod that implements a movable armor HUD and chat screen buttons:

```java
package com.example.hudmod;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import io.wispforest.owo.ui.layers.Layers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class HudModClient implements ClientModInitializer {
    
    private static final Identifier ARMOR_HUD_ID = Identifier.of("hudmod", "armor_hud");
    
    @Override
    public void onInitializeClient() {
        // Register keybind for toggling HUD
        var toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudmod.toggle_armor_hud",
            GLFW.GLFW_KEY_H,
            "key.categories.hudmod"
        ));
        
        // Add armor HUD (enabled by default)
        registerArmorHud();
        
        // Handle HUD toggle
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                if (Hud.hasComponent(ARMOR_HUD_ID)) {
                    Hud.remove(ARMOR_HUD_ID);
                } else {
                    registerArmorHud();
                }
            }
        });
        
        // Add chat screen buttons
        registerChatButtons();
    }
    
    private void registerArmorHud() {
        Hud.add(ARMOR_HUD_ID, () -> {
            var client = MinecraftClient.getInstance();
            
            var content = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.label(Text.literal("⚔ Armor")))
                .child(Components.label(Text.literal(() -> {
                    if (client.player == null) return "N/A";
                    return "Armor: " + client.player.getArmor();
                })))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(5));
            
            return Containers.draggable(Sizing.content(), Sizing.content(), content)
                .surface(Surface.PANEL)
                .foreheadSize(15)
                .positioning(Positioning.absolute(10, 10));
        });
    }
    
    private void registerChatButtons() {
        Layers.add(Containers::verticalFlow, instance -> {
            var buttons = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.button(Text.literal("🏠 Home"), 
                    btn -> sendCommand("/home")))
                .child(Components.button(Text.literal("⭐ Spawn"), 
                    btn -> sendCommand("/spawn")))
                .gap(5)
                .padding(Insets.of(5))
                .surface(Surface.PANEL)
                .positioning(Positioning.relative(50, 95));
            
            instance.adapter.rootComponent.child(buttons);
        }, ChatScreen.class);
    }
    
    private void sendCommand(String command) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.setScreen(null);
            client.player.networkHandler.sendChatCommand(command.substring(1));
        }
    }
}
```

This example demonstrates:
- Creating a movable HUD widget
- Toggling HUD widgets with keybinds
- Adding buttons to an existing screen (ChatScreen)
- Sending commands from button clicks

---

## Additional Resources

- [owo-ui Documentation](https://docs.wispforest.io/owo/ui)
- [owo-lib GitHub Repository](https://github.com/wisp-forest/owo-lib)
- Check the `src/testmod` directory in the owo-lib repository for more examples
