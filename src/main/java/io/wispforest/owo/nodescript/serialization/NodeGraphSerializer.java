package io.wispforest.owo.nodescript.serialization;

import com.google.gson.*;
import io.wispforest.owo.nodescript.model.*;
import io.wispforest.owo.nodescript.nodes.*;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles serialization and deserialization of node graphs.
 */
public class NodeGraphSerializer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Serialize a node graph to JSON.
     */
    public static JsonObject serialize(NodeGraph graph) {
        var json = new JsonObject();
        json.addProperty("id", graph.id());
        json.addProperty("name", graph.name());

        var nodesArray = new JsonArray();
        for (var node : graph.nodes()) {
            nodesArray.add(serializeNode(node));
        }
        json.add("nodes", nodesArray);

        var connectionsArray = new JsonArray();
        for (var conn : graph.connections()) {
            connectionsArray.add(serializeConnection(conn));
        }
        json.add("connections", connectionsArray);

        return json;
    }

    private static JsonObject serializeNode(Node node) {
        var json = new JsonObject();
        json.addProperty("id", node.id());
        json.addProperty("type", node.getNodeType());
        json.addProperty("name", node.name());
        json.addProperty("x", node.x());
        json.addProperty("y", node.y());

        // Serialize node-specific data
        if (node instanceof StringConstantNode stringConst) {
            json.addProperty("value", stringConst.value());
        } else if (node instanceof BooleanConstantNode boolConst) {
            json.addProperty("value", boolConst.value());
        }

        return json;
    }

    private static JsonObject serializeConnection(NodeConnection conn) {
        var json = new JsonObject();
        json.addProperty("id", conn.id());
        json.addProperty("sourceNodeId", conn.sourcePort().owner().id());
        json.addProperty("sourcePortId", conn.sourcePort().id());
        json.addProperty("targetNodeId", conn.targetPort().owner().id());
        json.addProperty("targetPortId", conn.targetPort().id());
        return json;
    }

    /**
     * Deserialize a node graph from JSON.
     */
    public static @Nullable NodeGraph deserialize(JsonObject json) {
        try {
            var id = json.get("id").getAsString();
            var name = json.get("name").getAsString();
            var graph = new NodeGraph(id, name);

            // Deserialize nodes
            var nodesArray = json.getAsJsonArray("nodes");
            for (var nodeElem : nodesArray) {
                var node = deserializeNode(nodeElem.getAsJsonObject());
                if (node != null) {
                    graph.addNode(node);
                }
            }

            // Deserialize connections
            var connectionsArray = json.getAsJsonArray("connections");
            for (var connElem : connectionsArray) {
                deserializeConnection(connElem.getAsJsonObject(), graph);
            }

            return graph;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static @Nullable Node deserializeNode(JsonObject json) {
        var id = json.get("id").getAsString();
        var type = json.get("type").getAsString();
        var x = json.get("x").getAsDouble();
        var y = json.get("y").getAsDouble();

        Node node = switch (type) {
            case AllowChatEventNode.TYPE -> new AllowChatEventNode(id, x, y);
            case HudRenderEventNode.TYPE -> new HudRenderEventNode(id, x, y);
            case StringIncludesNode.TYPE -> new StringIncludesNode(id, x, y);
            case BooleanNotNode.TYPE -> new BooleanNotNode(id, x, y);
            case StringConstantNode.TYPE -> new StringConstantNode(id, x, y);
            case BooleanConstantNode.TYPE -> new BooleanConstantNode(id, x, y);
            case PrintChatNode.TYPE -> new PrintChatNode(id, x, y);
            default -> null;
        };

        if (node == null) return null;

        // Deserialize node-specific data
        if (node instanceof StringConstantNode stringConst && json.has("value")) {
            stringConst.setValue(json.get("value").getAsString());
        } else if (node instanceof BooleanConstantNode boolConst && json.has("value")) {
            boolConst.setValue(json.get("value").getAsBoolean());
        }

        return node;
    }

    private static void deserializeConnection(JsonObject json, NodeGraph graph) {
        var sourceNodeId = json.get("sourceNodeId").getAsString();
        var sourcePortId = json.get("sourcePortId").getAsString();
        var targetNodeId = json.get("targetNodeId").getAsString();
        var targetPortId = json.get("targetPortId").getAsString();

        var sourceNode = graph.getNode(sourceNodeId);
        var targetNode = graph.getNode(targetNodeId);

        if (sourceNode == null || targetNode == null) return;

        var sourcePort = sourceNode.getOutputPort(sourcePortId);
        if (sourcePort == null) sourcePort = sourceNode.getInputPort(sourcePortId);

        var targetPort = targetNode.getInputPort(targetPortId);
        if (targetPort == null) targetPort = targetNode.getOutputPort(targetPortId);

        if (sourcePort != null && targetPort != null) {
            graph.connect(sourcePort, targetPort);
        }
    }

    /**
     * Save a node graph to a file.
     */
    public static void saveToFile(NodeGraph graph, Path path) throws IOException {
        var json = serialize(graph);
        var jsonString = GSON.toJson(json);
        Files.writeString(path, jsonString, StandardCharsets.UTF_8);
    }

    /**
     * Load a node graph from a file.
     */
    public static @Nullable NodeGraph loadFromFile(Path path) throws IOException {
        if (!Files.exists(path)) return null;
        var jsonString = Files.readString(path, StandardCharsets.UTF_8);
        var json = JsonParser.parseString(jsonString).getAsJsonObject();
        return deserialize(json);
    }
}
