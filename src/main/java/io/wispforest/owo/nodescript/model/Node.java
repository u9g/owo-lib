package io.wispforest.owo.nodescript.model;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Base class for all nodes in the node graph.
 */
public abstract class Node {

    private final String id;
    private String name;
    private double x;
    private double y;

    private final List<NodePort> inputPorts = new ArrayList<>();
    private final List<NodePort> outputPorts = new ArrayList<>();

    private @Nullable NodeGraph graph;

    public Node(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.x = 0;
        this.y = 0;
        this.initPorts();
    }

    public Node(String id, String name, double x, double y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.initPorts();
    }

    /**
     * Initialize the input and output ports for this node.
     * Called during construction.
     */
    protected abstract void initPorts();

    /**
     * Get the type identifier for this node type.
     * Used for serialization.
     */
    public abstract String getNodeType();

    /**
     * Execute this node's logic. Returns the execution outputs as a map from port ID to value.
     */
    public abstract Map<String, Object> execute(Map<String, Object> inputs);

    protected void addInputPort(String id, String name, PortType type) {
        inputPorts.add(new NodePort(id, name, type, NodePort.PortDirection.INPUT, this));
    }

    protected void addOutputPort(String id, String name, PortType type) {
        outputPorts.add(new NodePort(id, name, type, NodePort.PortDirection.OUTPUT, this));
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

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public List<NodePort> inputPorts() {
        return Collections.unmodifiableList(this.inputPorts);
    }

    public List<NodePort> outputPorts() {
        return Collections.unmodifiableList(this.outputPorts);
    }

    public @Nullable NodePort getInputPort(String id) {
        return inputPorts.stream().filter(p -> p.id().equals(id)).findFirst().orElse(null);
    }

    public @Nullable NodePort getOutputPort(String id) {
        return outputPorts.stream().filter(p -> p.id().equals(id)).findFirst().orElse(null);
    }

    public @Nullable NodeGraph graph() {
        return this.graph;
    }

    public void setGraph(@Nullable NodeGraph graph) {
        this.graph = graph;
    }
}
