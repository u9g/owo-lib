package io.wispforest.owo.nodescript;

import io.wispforest.owo.Owo;
import io.wispforest.owo.nodescript.model.NodeGraph;
import io.wispforest.owo.nodescript.nodes.AllowChatEventNode;
import io.wispforest.owo.nodescript.nodes.ClientTickEventNode;
import io.wispforest.owo.nodescript.nodes.HudRenderEventNode;
import io.wispforest.owo.nodescript.nodes.KeyPressEventNode;
import io.wispforest.owo.nodescript.serialization.NodeGraphSerializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages loaded node graphs and handles event dispatching.
 */
public class NodeScriptManager {

    public static final NodeScriptManager INSTANCE = new NodeScriptManager();

    private final Map<String, NodeGraph> loadedGraphs = new ConcurrentHashMap<>();
    private boolean eventsRegistered = false;

    private NodeScriptManager() {}

    /**
     * Initialize the manager and register event handlers.
     */
    public void initialize() {
        if (eventsRegistered) return;
        eventsRegistered = true;

        // Register chat event handler
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            return handleAllowChatEvent(message.getString());
        });

        // Register HUD render event handler
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            handleHudRenderEvent(tickCounter.getGameTimeDeltaPartialTick(false));
        });

        // Register client tick event handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleClientTickEvent();
        });

        // Load any saved graphs from disk
        loadAllGraphs();
    }

    /**
     * Load a node graph and activate it for event handling.
     */
    public void loadGraph(NodeGraph graph) {
        loadedGraphs.put(graph.id(), graph);
    }

    /**
     * Unload a node graph.
     */
    public void unloadGraph(String graphId) {
        loadedGraphs.remove(graphId);
    }

    /**
     * Get all loaded graphs.
     */
    public Collection<NodeGraph> getLoadedGraphs() {
        return Collections.unmodifiableCollection(loadedGraphs.values());
    }

    /**
     * Handle the AllowChat event by executing relevant graphs.
     */
    private boolean handleAllowChatEvent(String message) {
        boolean result = true;

        for (var graph : loadedGraphs.values()) {
            for (var node : graph.getEventNodes()) {
                if (node instanceof AllowChatEventNode eventNode) {
                    var executor = new NodeGraphExecutor(graph);
                    var inputs = Map.<String, Object>of("message", message);
                    var outputs = executor.execute(eventNode, inputs);

                    // Check if the result port has a value
                    if (outputs.containsKey("result")) {
                        var nodeResult = (Boolean) outputs.get("result");
                        if (!nodeResult) {
                            result = false;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Handle the HUD render event by executing relevant graphs.
     */
    private void handleHudRenderEvent(float tickDelta) {
        for (var graph : loadedGraphs.values()) {
            for (var node : graph.getEventNodes()) {
                if (node instanceof HudRenderEventNode eventNode) {
                    var executor = new NodeGraphExecutor(graph);
                    var inputs = Map.<String, Object>of("tick_delta", tickDelta);
                    executor.execute(eventNode, inputs);
                }
            }
        }
    }

    /**
     * Handle the client tick event by executing relevant graphs.
     */
    private void handleClientTickEvent() {
        for (var graph : loadedGraphs.values()) {
            for (var node : graph.getEventNodes()) {
                if (node instanceof ClientTickEventNode eventNode) {
                    var executor = new NodeGraphExecutor(graph);
                    var inputs = Map.<String, Object>of();
                    executor.execute(eventNode, inputs);
                }
            }
        }
    }

    /**
     * Handle key press events (called externally).
     */
    public void handleKeyPressEvent(int keyCode) {
        for (var graph : loadedGraphs.values()) {
            for (var node : graph.getEventNodes()) {
                if (node instanceof KeyPressEventNode eventNode) {
                    var executor = new NodeGraphExecutor(graph);
                    var inputs = Map.<String, Object>of("key_code", keyCode);
                    executor.execute(eventNode, inputs);
                }
            }
        }
    }

    /**
     * Load all saved graphs from the nodescripts directory.
     */
    public void loadAllGraphs() {
        var scriptsPath = getScriptsPath();
        if (!Files.exists(scriptsPath)) return;

        Owo.LOGGER.info("[NodeScript] Loading graphs from {}", scriptsPath);

        try (var stream = Files.list(scriptsPath)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                .forEach(path -> {
                    try {
                        var graph = NodeGraphSerializer.loadFromFile(path);
                        if (graph != null) {
                            loadGraph(graph);
                            Owo.LOGGER.info("[NodeScript] Loaded graph '{}' from {}", graph.name(), path.getFileName());
                        }
                    } catch (IOException e) {
                        Owo.LOGGER.error("[NodeScript] Failed to load graph from {}: {}", path, e.getMessage());
                    } catch (Exception e) {
                        Owo.LOGGER.error("[NodeScript] Error parsing graph from {}: {}", path, e.getMessage());
                    }
                });
        } catch (IOException e) {
            Owo.LOGGER.error("[NodeScript] Failed to list nodescripts directory: {}", e.getMessage());
        }
    }

    private Path getScriptsPath() {
        return FabricLoader.getInstance().getGameDir().resolve("nodescripts");
    }
}
