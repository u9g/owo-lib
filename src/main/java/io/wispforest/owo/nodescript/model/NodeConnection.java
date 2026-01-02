package io.wispforest.owo.nodescript.model;

import java.util.UUID;

/**
 * Represents a connection between two node ports.
 */
public class NodeConnection {

    private final String id;
    private final NodePort sourcePort;
    private final NodePort targetPort;

    public NodeConnection(NodePort sourcePort, NodePort targetPort) {
        this.id = UUID.randomUUID().toString();
        this.sourcePort = sourcePort;
        this.targetPort = targetPort;
    }

    public NodeConnection(String id, NodePort sourcePort, NodePort targetPort) {
        this.id = id;
        this.sourcePort = sourcePort;
        this.targetPort = targetPort;
    }

    public String id() {
        return this.id;
    }

    public NodePort sourcePort() {
        return this.sourcePort;
    }

    public NodePort targetPort() {
        return this.targetPort;
    }

    /**
     * Gets the output port (the port that produces the value).
     */
    public NodePort outputPort() {
        return sourcePort.direction() == NodePort.PortDirection.OUTPUT ? sourcePort : targetPort;
    }

    /**
     * Gets the input port (the port that receives the value).
     */
    public NodePort inputPort() {
        return sourcePort.direction() == NodePort.PortDirection.INPUT ? sourcePort : targetPort;
    }

    public void apply() {
        sourcePort.setConnection(this);
        targetPort.setConnection(this);
    }

    public void remove() {
        sourcePort.setConnection(null);
        targetPort.setConnection(null);
    }
}
