package io.wispforest.uwu.client;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Example HUD components demonstrating common in-game widgets using owo-ui.
 * These HUDs are movable/draggable and show:
 * - Armor status
 * - Player coordinates
 * - Active potion effects with icons
 */
public class ExampleHuds {

    // HUD Component IDs
    private static final Identifier ARMOR_HUD_ID = Identifier.of("uwu", "armor_hud");
    private static final Identifier COORDS_HUD_ID = Identifier.of("uwu", "coords_hud");
    private static final Identifier EFFECTS_HUD_ID = Identifier.of("uwu", "effects_hud");

    // Component references for updates
    private static List<ItemComponent> armorSlots = new ArrayList<>();
    private static LabelComponent coordsLabel = null;
    private static FlowLayout effectsContainer = null;

    public static void init() {
        // Register keybinding to toggle all example HUDs
        final var toggleHudsKey = new KeyBinding("key.uwu.toggle_example_huds", GLFW.GLFW_KEY_H, "misc");
        KeyBindingHelper.registerKeyBinding(toggleHudsKey);

        // Add the HUDs initially
        addArmorHud();
        addCoordsHud();
        addEffectsHud();

        // Toggle key handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudsKey.wasPressed()) {
                if (Hud.hasComponent(ARMOR_HUD_ID)) {
                    removeAllHuds();
                } else {
                    addArmorHud();
                    addCoordsHud();
                    addEffectsHud();
                }
            }

            // Update HUD content each tick
            updateHuds();
        });
    }

    /**
     * Creates an armor HUD showing equipped armor pieces in a vertical layout.
     * The HUD is draggable and positioned in the top-left corner.
     */
    private static void addArmorHud() {
        Hud.add(ARMOR_HUD_ID, () -> {
            armorSlots.clear();

            // Create item components for each armor slot
            var helmet = Components.item(ItemStack.EMPTY).showOverlay(true);
            var chestplate = Components.item(ItemStack.EMPTY).showOverlay(true);
            var leggings = Components.item(ItemStack.EMPTY).showOverlay(true);
            var boots = Components.item(ItemStack.EMPTY).showOverlay(true);

            armorSlots.add(helmet);
            armorSlots.add(chestplate);
            armorSlots.add(leggings);
            armorSlots.add(boots);

            // Create the armor display layout
            var armorLayout = Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.literal("Armor").formatted(Formatting.GOLD)))
                    .child(helmet)
                    .child(chestplate)
                    .child(leggings)
                    .child(boots)
                    .gap(2)
                    .padding(Insets.of(5))
                    .surface(Surface.DARK_PANEL)
                    .horizontalAlignment(HorizontalAlignment.CENTER);

            // Wrap in a draggable container for movability
            return Containers.draggable(Sizing.content(), Sizing.content(), armorLayout)
                    .positioning(Positioning.absolute(5, 5));
        });
    }

    /**
     * Creates a coordinate HUD showing player X, Y, Z position.
     * The HUD is draggable and positioned at the top-center.
     */
    private static void addCoordsHud() {
        Hud.add(COORDS_HUD_ID, () -> {
            coordsLabel = Components.label(Text.literal("X: 0 Y: 0 Z: 0"));
            coordsLabel.color(Color.ofRgb(0x55FF55)); // Bright green

            var coordsLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(coordsLabel)
                    .padding(Insets.of(5))
                    .surface(Surface.DARK_PANEL);

            // Wrap in a draggable container for movability
            return Containers.draggable(Sizing.content(), Sizing.content(), coordsLayout)
                    .positioning(Positioning.relative(50, 0));
        });
    }

    /**
     * Creates a potion effects HUD showing active effects with their icons and duration.
     * The HUD is draggable and positioned in the top-right corner.
     */
    private static void addEffectsHud() {
        Hud.add(EFFECTS_HUD_ID, () -> {
            effectsContainer = Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .gap(2);

            var outerLayout = Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(Components.label(Text.literal("Effects").formatted(Formatting.LIGHT_PURPLE)))
                    .child(effectsContainer)
                    .padding(Insets.of(5))
                    .surface(Surface.DARK_PANEL);

            // Wrap in a draggable container for movability
            return Containers.draggable(Sizing.content(), Sizing.content(), outerLayout)
                    .positioning(Positioning.relative(100, 0));
        });
    }

    /**
     * Updates all HUD components with current player data.
     */
    private static void updateHuds() {
        var client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Update armor HUD
        if (!armorSlots.isEmpty() && Hud.hasComponent(ARMOR_HUD_ID)) {
            armorSlots.get(0).stack(client.player.getEquippedStack(EquipmentSlot.HEAD));
            armorSlots.get(1).stack(client.player.getEquippedStack(EquipmentSlot.CHEST));
            armorSlots.get(2).stack(client.player.getEquippedStack(EquipmentSlot.LEGS));
            armorSlots.get(3).stack(client.player.getEquippedStack(EquipmentSlot.FEET));
        }

        // Update coordinates HUD
        if (coordsLabel != null && Hud.hasComponent(COORDS_HUD_ID)) {
            var pos = client.player.getBlockPos();
            coordsLabel.text(Text.literal(String.format("X: %d  Y: %d  Z: %d", pos.getX(), pos.getY(), pos.getZ())));
        }

        // Update effects HUD
        if (effectsContainer != null && Hud.hasComponent(EFFECTS_HUD_ID)) {
            updateEffectsHud(client);
        }
    }

    /**
     * Updates the potion effects display with current active effects.
     */
    private static void updateEffectsHud(MinecraftClient client) {
        var effects = client.player.getStatusEffects();

        // Clear existing effect displays
        effectsContainer.clearChildren();

        if (effects.isEmpty()) {
            effectsContainer.child(
                    Components.label(Text.literal("None").formatted(Formatting.GRAY))
                            .horizontalTextAlignment(HorizontalAlignment.CENTER)
            );
            return;
        }

        // Add each active effect
        for (StatusEffectInstance effect : effects) {
            var effectType = effect.getEffectType().value();
            var effectName = effectType.getName();

            // Create amplifier text (I, II, III, etc.)
            var amplifier = effect.getAmplifier();
            var amplifierText = amplifier > 0 ? " " + toRoman(amplifier + 1) : "";

            // Get duration text
            var durationText = StatusEffectUtil.getDurationText(effect, 1.0f, client.world.getTickManager().getTickRate());

            // Create the effect row with icon texture and info
            var effectRow = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(
                            // Effect icon using the status effect sprite
                            Components.sprite(client.getStatusEffectSpriteManager().getSprite(effectType))
                                    .sizing(Sizing.fixed(18), Sizing.fixed(18))
                    )
                    .child(
                            Containers.verticalFlow(Sizing.content(), Sizing.content())
                                    .child(Components.label(Text.empty().append(effectName).append(Text.literal(amplifierText))
                                            .styled(style -> style.withColor(effectType.getColor()))))
                                    .child(Components.label(Text.literal("").append(durationText).formatted(Formatting.GRAY)))
                                    .gap(1)
                    )
                    .gap(4)
                    .verticalAlignment(VerticalAlignment.CENTER);

            effectsContainer.child(effectRow);
        }
    }

    private static void removeAllHuds() {
        Hud.remove(ARMOR_HUD_ID);
        Hud.remove(COORDS_HUD_ID);
        Hud.remove(EFFECTS_HUD_ID);
        armorSlots.clear();
        coordsLabel = null;
        effectsContainer = null;
    }

    /**
     * Converts a number to Roman numerals (for potion amplifier display).
     */
    private static String toRoman(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(num);
        };
    }
}
