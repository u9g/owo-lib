package io.wispforest.owo.nodescript.model;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A graph containing nodes and their connections.
 */
public class NodeGraph {

    private final String id;
    private String name;
    private final List<Node> nodes = new ArrayList<>();
    private final List<NodeConnection> connections = new ArrayList<>();

    public NodeGraph(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public NodeGraph(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> nodes() {
        return Collections.unmodifiableList(this.nodes);
    }

    public List<NodeConnection> connections() {
        return Collections.unmodifiableList(this.connections);
    }

    public void addNode(Node node) {
        node.setGraph(this);
        this.nodes.add(node);
    }

    public void removeNode(Node node) {
        // Remove all connections involving this node
        var toRemove = new ArrayList<NodeConnection>();
        for (var conn : connections) {
            if (conn.sourcePort().owner() == node || conn.targetPort().owner() == node) {
                toRemove.add(conn);
            }
        }
        toRemove.forEach(this::removeConnection);

        node.setGraph(null);
        this.nodes.remove(node);
    }

    public @Nullable Node getNode(String id) {
        return nodes.stream().filter(n -> n.id().equals(id)).findFirst().orElse(null);
    }

    public NodeConnection connect(NodePort source, NodePort target) {
        if (!source.canConnectTo(target)) {
            throw new IllegalArgumentException("Cannot connect these ports");
        }

        // Remove existing connections from these ports
        if (source.connection() != null) {
            removeConnection(source.connection());
        }
        if (target.connection() != null) {
            removeConnection(target.connection());
        }

        var connection = new NodeConnection(source, target);
        connection.apply();
        connections.add(connection);
        return connection;
    }

    public void removeConnection(NodeConnection connection) {
        connection.remove();
        connections.remove(connection);
    }

    public @Nullable NodeConnection getConnection(String id) {
        return connections.stream().filter(c -> c.id().equals(id)).findFirst().orElse(null);
    }

    /**
     * Find all event entry nodes in this graph.
     */
    public List<Node> getEventNodes() {
        return nodes.stream()
            .filter(n -> n.getNodeType().startsWith("event:"))
            .toList();
    }
}
