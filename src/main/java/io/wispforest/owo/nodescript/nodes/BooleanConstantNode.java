package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that provides a constant boolean value.
 */
public class BooleanConstantNode extends Node {

    public static final String TYPE = "constant:boolean";

    private boolean value = false;

    public BooleanConstantNode() {
        super("Boolean Constant");
    }

    public BooleanConstantNode(String id, double x, double y) {
        super(id, "Boolean Constant", x, y);
    }

    @Override
    protected void initPorts() {
        addOutputPort("value", "Value", PortType.BOOLEAN);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        return Map.of("value", this.value);
    }

    public boolean value() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
