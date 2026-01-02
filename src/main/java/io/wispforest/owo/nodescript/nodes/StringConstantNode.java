package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that provides a constant string value.
 */
public class StringConstantNode extends Node {

    public static final String TYPE = "constant:string";

    private String value = "";

    public StringConstantNode() {
        super("String Constant");
    }

    public StringConstantNode(String id, double x, double y) {
        super(id, "String Constant", x, y);
    }

    @Override
    protected void initPorts() {
        addOutputPort("value", "Value", PortType.STRING);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        return Map.of("value", this.value);
    }

    public String value() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
