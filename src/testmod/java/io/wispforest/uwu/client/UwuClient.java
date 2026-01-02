package io.wispforest.uwu.client;

import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.particles.ClientParticles;
import io.wispforest.owo.particles.systems.ParticleSystemController;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.ui.layers.Layers;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.uwu.Uwu;
import io.wispforest.uwu.network.UwuNetworkExample;
import io.wispforest.uwu.network.UwuOptionalNetExample;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public class UwuClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UwuNetworkExample.Client.init();
        UwuOptionalNetExample.Client.init();

        HandledScreens.register(Uwu.EPIC_SCREEN_HANDLER_TYPE, EpicHandledScreen::new);
//        HandledScreens.register(EPIC_SCREEN_HANDLER_TYPE, EpicHandledModelScreen::new);

        final var binding = new KeyBinding("key.uwu.hud_test", GLFW.GLFW_KEY_J, KeyBinding.Category.MISC);
        KeyBindingHelper.registerKeyBinding(binding);

        final var bindingButCooler = new KeyBinding("key.uwu.hud_test_two", GLFW.GLFW_KEY_K, KeyBinding.Category.MISC);
        KeyBindingHelper.registerKeyBinding(bindingButCooler);

        // Example: Movable Armor HUD (toggle with L key)
        final var toggleArmorHudKey = new KeyBinding("key.uwu.toggle_armor_hud", GLFW.GLFW_KEY_L, KeyBinding.Category.MISC);
        KeyBindingHelper.registerKeyBinding(toggleArmorHudKey);

        // Example: Movable Coordinate HUD (toggle with O key)
        final var toggleCoordinateHudKey = new KeyBinding("key.uwu.toggle_coordinate_hud", GLFW.GLFW_KEY_O, KeyBinding.Category.MISC);
        KeyBindingHelper.registerKeyBinding(toggleCoordinateHudKey);

        // Example: Movable Potion Effect HUD (toggle with P key)
        final var togglePotionHudKey = new KeyBinding("key.uwu.toggle_potion_hud", GLFW.GLFW_KEY_P, KeyBinding.Category.MISC);
        KeyBindingHelper.registerKeyBinding(togglePotionHudKey);

        final var hudComponentId = Identifier.of("uwu", "test_element");
        final Supplier<Component> hudComponent = () ->
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(Components.item(Items.DIAMOND.getDefaultStack()).margins(Insets.of(3)))
                        .child(Components.label(Text.literal("epic stuff in hud")))
                        .child(Components.entity(Sizing.fixed(50), EntityType.ALLAY, null))
                        .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                        .padding(Insets.of(5))
                        .surface(Surface.PANEL)
                        .margins(Insets.of(5))
                        .positioning(Positioning.relative(100, 25));

        final var coolerComponentId = Identifier.of("uwu", "test_element_two");
        final Supplier<Component> coolerComponent = () -> UIModel.load(Path.of("../src/testmod/resources/assets/uwu/owo_ui/test_element_two.xml")).expandTemplate(FlowLayout.class, "hud-element", Map.of());
        Hud.add(coolerComponentId, coolerComponent);

        // Register example HUD widgets
        final var armorHudId = Identifier.of("uwu", "armor_hud");
        final var coordinateHudId = Identifier.of("uwu", "coordinate_hud");
        final var potionHudId = Identifier.of("uwu", "potion_hud");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (binding.wasPressed()) {
                if (Hud.hasComponent(hudComponentId)) {
                    Hud.remove(hudComponentId);
                } else {
                    Hud.add(hudComponentId, hudComponent);
                }
            }

            if (bindingButCooler.wasPressed()) {
                Hud.remove(coolerComponentId);
                Hud.add(coolerComponentId, coolerComponent);

                //noinspection StatementWithEmptyBody
                while (bindingButCooler.wasPressed()) {}
            }

            // Toggle Armor HUD
            while (toggleArmorHudKey.wasPressed()) {
                if (Hud.hasComponent(armorHudId)) {
                    Hud.remove(armorHudId);
                } else {
                    registerArmorHud(armorHudId);
                }
            }

            // Toggle Coordinate HUD
            while (toggleCoordinateHudKey.wasPressed()) {
                if (Hud.hasComponent(coordinateHudId)) {
                    Hud.remove(coordinateHudId);
                } else {
                    registerCoordinateHud(coordinateHudId);
                }
            }

            // Toggle Potion Effect HUD
            while (togglePotionHudKey.wasPressed()) {
                if (Hud.hasComponent(potionHudId)) {
                    Hud.remove(potionHudId);
                } else {
                    registerPotionEffectHud(potionHudId);
                }
            }
        });

        Uwu.CHANNEL.registerClientbound(Uwu.OtherTestMessage.class, (message, access) -> {
            access.player().sendMessage(Text.of("Message '" + message.message() + "' from " + message.pos()), false);
        });

        if (Uwu.WE_TESTEN_HANDSHAKE) {
            OwoNetChannel.create(Identifier.of("uwu", "client_only_channel"));

            Uwu.CHANNEL.registerServerbound(WeirdMessage.class, (data, access) -> {
            });
            Uwu.CHANNEL.registerClientbound(WeirdMessage.class, (data, access) -> {
            });

            new ParticleSystemController(Identifier.of("uwu", "client_only_particles"));
            Uwu.PARTICLE_CONTROLLER.register(WeirdMessage.class, (world, pos, data) -> {
            });
        }

        Uwu.CUBE.setHandler((world, pos, data) -> {
            ClientParticles.setParticleCount(5);
            ClientParticles.spawnCubeOutline(ParticleTypes.END_ROD, world, pos, 1, .01f);
        });

        Layers.add(Containers::verticalFlow, instance -> {
            if (MinecraftClient.getInstance().world == null) return;

            instance.adapter.rootComponent.child(
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                            .child(Components.entity(Sizing.fixed(20), EntityType.ALLAY, null).<EntityComponent<AllayEntity>>configure(component -> {
                                component.allowMouseRotation(true)
                                        .scale(.75f);

                                component.mouseDown().subscribe((click, doubled) -> {
                                    UISounds.playInteractionSound();
                                    return true;
                                });
                            })).child(Components.textBox(Sizing.fixed(100), "allay text").<TextFieldWidget>configure(textBox -> {
                                textBox.verticalSizing(Sizing.fixed(9));
                                textBox.setDrawsBackground(false);
                            })).<FlowLayout>configure(layout -> {
                                layout.gap(5).margins(Insets.left(4)).verticalAlignment(VerticalAlignment.CENTER);

                                instance.alignComponentToWidget(widget -> {
                                    if (!(widget instanceof ButtonWidget button)) return false;
                                    return button.getMessage().getContent() instanceof TranslatableTextContent translatable && translatable.getKey().equals("gui.stats");
                                }, Layer.Instance.AnchorSide.RIGHT, 0, layout);
                            })
            );
        }, GameMenuScreen.class);

        Layers.add(Containers::verticalFlow, instance -> {
            ButtonComponent button;
            instance.adapter.rootComponent.child(
                    (button = Components.button(Text.literal(":)"), buttonComponent -> {
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("handled screen moment"), false);
                    })).verticalSizing(Sizing.fixed(12))
            );

            instance.alignComponentToHandledScreenCoordinates(button, 125, 65);
        }, InventoryScreen.class);

        // Example: Add command buttons to ChatScreen
        Layers.add(Containers::verticalFlow, instance -> {
            var commandButtons = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(Components.button(Text.literal("🏠 Home"), btn -> sendCommand("/home")))
                    .child(Components.button(Text.literal("⭐ Spawn"), btn -> sendCommand("/spawn")))
                    .child(Components.button(Text.literal("💰 Balance"), btn -> sendCommand("/balance")))
                    .gap(5)
                    .padding(Insets.of(5))
                    .surface(Surface.PANEL)
                    .positioning(Positioning.relative(50, 95));

            instance.adapter.rootComponent.child(commandButtons);
        }, ChatScreen.class);
    }

    // Example: Movable Armor HUD
    private static void registerArmorHud(Identifier hudId) {
        Hud.add(hudId, () -> {
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

    // Example: Movable Coordinate HUD
    private static void registerCoordinateHud(Identifier hudId) {
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();

            var coordinateDisplay = Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.literal("📍 Coordinates")).margins(Insets.bottom(3)))
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

            return Containers.draggable(Sizing.content(), Sizing.content(), coordinateDisplay)
                    .surface(Surface.PANEL)
                    .foreheadSize(15)
                    .positioning(Positioning.absolute(10, 60));
        });
    }

    // Example: Movable Potion Effect HUD
    private static void registerPotionEffectHud(Identifier hudId) {
        Hud.add(hudId, () -> {
            var client = MinecraftClient.getInstance();

            // Create title bar for visual indication of drag area
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

            return Containers.draggable(Sizing.content(), Sizing.content(), content)
                    .foreheadSize(12)
                    .positioning(Positioning.absolute(10, 130));
        });
    }

    // Helper method to send commands from chat screen buttons
    private static void sendCommand(String command) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.setScreen(null); // Close chat screen
            client.player.networkHandler.sendChatCommand(command.substring(1)); // Remove leading '/'
        }
    }

    public record WeirdMessage(int e) {}
}
