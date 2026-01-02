package io.wispforest.owo.nodescript;

import io.wispforest.owo.nodescript.model.*;
import io.wispforest.owo.nodescript.nodes.BooleanConstantNode;
import io.wispforest.owo.nodescript.nodes.NumberConstantNode;
import io.wispforest.owo.nodescript.nodes.StringConstantNode;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Executes a node graph starting from a specific entry node.
 */
public class NodeGraphExecutor {

    private final NodeGraph graph;
    private final Map<String, Object> values = new HashMap<>();

    public NodeGraphExecutor(NodeGraph graph) {
        this.graph = graph;
    }

    /**
     * Execute the graph starting from the given event node with initial inputs.
     */
    public Map<String, Object> execute(Node eventNode, Map<String, Object> initialInputs) {
        values.clear();

        // Set initial values from event
        var eventOutputs = eventNode.execute(initialInputs);
        for (var entry : eventOutputs.entrySet()) {
            var port = eventNode.getOutputPort(entry.getKey());
            if (port != null) {
                values.put(getPortKey(port), entry.getValue());
            }
        }

        // Execute connected nodes recursively
        executeOutputConnections(eventNode);

        // Collect results from input ports of the event node
        Map<String, Object> results = new HashMap<>();
        for (var port : eventNode.inputPorts()) {
            var value = getInputValue(port);
            if (value != null) {
                results.put(port.id(), value);
            }
        }

        return results;
    }

    private void executeOutputConnections(Node node) {
        for (var port : node.outputPorts()) {
            if (!port.isConnected()) continue;
            if (port.type() != PortType.EXECUTION) continue;

            var conn = port.connection();
            if (conn == null) continue;

            var targetPort = conn.inputPort();
            var targetNode = targetPort.owner();

            executeNode(targetNode);
            executeOutputConnections(targetNode);
        }
    }

    private void executeNode(Node node) {
        // Gather inputs from connected ports
        Map<String, Object> inputs = new HashMap<>();
        for (var port : node.inputPorts()) {
            var value = getInputValue(port);
            if (value != null) {
                inputs.put(port.id(), value);
            }
        }

        // Execute the node
        var outputs = node.execute(inputs);

        // Handle special actions
        if (outputs.containsKey("_action")) {
            handleAction(outputs);
        }

        // Store outputs
        for (var entry : outputs.entrySet()) {
            if (entry.getKey().startsWith("_")) continue;
            var port = node.getOutputPort(entry.getKey());
            if (port != null) {
                values.put(getPortKey(port), entry.getValue());
            }
        }
    }

    /**
     * Get the constant value from a node if it's a constant node type.
     */
    private @Nullable Object getConstantValue(Node node) {
        if (node instanceof StringConstantNode stringConst) {
            return stringConst.value();
        } else if (node instanceof BooleanConstantNode boolConst) {
            return boolConst.value();
        } else if (node instanceof NumberConstantNode numberConst) {
            return numberConst.value();
        }
        return null;
    }

    private @Nullable Object getInputValue(NodePort port) {
        if (!port.isConnected()) {
            // Check if the owning node is a constant node
            return getConstantValue(port.owner());
        }

        var conn = port.connection();
        if (conn == null) return null;

        var sourcePort = conn.outputPort();

        // First check if we have a cached value
        var cachedValue = values.get(getPortKey(sourcePort));
        if (cachedValue != null) {
            return cachedValue;
        }

        // Otherwise, execute the source node (for non-execution ports)
        if (sourcePort.type() != PortType.EXECUTION) {
            var sourceNode = sourcePort.owner();

            // Special handling for constant nodes
            var constantValue = getConstantValue(sourceNode);
            if (constantValue != null) {
                return constantValue;
            }

            // Execute the node to get its value
            Map<String, Object> inputs = new HashMap<>();
            for (var inputPort : sourceNode.inputPorts()) {
                var value = getInputValue(inputPort);
                if (value != null) {
                    inputs.put(inputPort.id(), value);
                }
            }

            var outputs = sourceNode.execute(inputs);
            for (var entry : outputs.entrySet()) {
                var outPort = sourceNode.getOutputPort(entry.getKey());
                if (outPort != null) {
                    values.put(getPortKey(outPort), entry.getValue());
                }
            }

            return values.get(getPortKey(sourcePort));
        }

        return null;
    }

    private void handleAction(Map<String, Object> outputs) {
        var action = outputs.get("_action").toString();
        switch (action) {
            case "print_chat" -> {
                var message = outputs.getOrDefault("_message", "").toString();
                var player = Minecraft.getInstance().player;
                if (player != null) {
                    player.displayClientMessage(Component.literal("[NodeScript] " + message), false);
                }
            }
        }
    }

    private String getPortKey(NodePort port) {
        return port.owner().id() + ":" + port.id();
    }
}
