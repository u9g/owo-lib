package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that checks if a string contains another string.
 */
public class StringIncludesNode extends Node {

    public static final String TYPE = "logic:string_includes";

    public StringIncludesNode() {
        super("String Includes");
    }

    public StringIncludesNode(String id, double x, double y) {
        super(id, "String Includes", x, y);
    }

    @Override
    protected void initPorts() {
        addInputPort("string", "String", PortType.STRING);
        addInputPort("search", "Search", PortType.STRING);
        addOutputPort("result", "Result", PortType.BOOLEAN);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        String string = inputs.getOrDefault("string", "").toString();
        String search = inputs.getOrDefault("search", "").toString();
        boolean result = string.contains(search);
        return Map.of("result", result);
    }
}
