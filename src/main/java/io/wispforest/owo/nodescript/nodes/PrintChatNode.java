package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import io.wispforest.owo.nodescript.model.PortType;

import java.util.Map;

/**
 * Node that prints a message to the chat (for debugging).
 */
public class PrintChatNode extends Node {

    public static final String TYPE = "action:print_chat";

    public PrintChatNode() {
        super("Print to Chat");
    }

    public PrintChatNode(String id, double x, double y) {
        super(id, "Print to Chat", x, y);
    }

    @Override
    protected void initPorts() {
        addInputPort("exec", "Execute", PortType.EXECUTION);
        addInputPort("message", "Message", PortType.STRING);
        addOutputPort("exec", "Execute", PortType.EXECUTION);
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {
        String message = inputs.getOrDefault("message", "").toString();
        // The actual printing is handled by the executor
        return Map.of(
            "exec", true,
            "_action", "print_chat",
            "_message", message
        );
    }
}
