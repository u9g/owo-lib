package io.wispforest.owo.nodescript.model;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an input or output port on a node.
 */
public class NodePort {

    private final String id;
    private final String name;
    private final PortType type;
    private final PortDirection direction;
    private final Node owner;
    private @Nullable NodeConnection connection;

    public NodePort(String id, String name, PortType type, PortDirection direction, Node owner) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.direction = direction;
        this.owner = owner;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public PortType type() {
        return this.type;
    }

    public PortDirection direction() {
        return this.direction;
    }

    public Node owner() {
        return this.owner;
    }

    public @Nullable NodeConnection connection() {
        return this.connection;
    }

    public void setConnection(@Nullable NodeConnection connection) {
        this.connection = connection;
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public boolean canConnectTo(NodePort other) {
        if (this.direction == other.direction) {
            return false;
        }
        if (this.owner == other.owner) {
            return false;
        }
        return this.type.canConnectTo(other.type);
    }

    public enum PortDirection {
        INPUT,
        OUTPUT
    }
}
