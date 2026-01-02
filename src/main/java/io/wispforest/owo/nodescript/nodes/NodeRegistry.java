package io.wispforest.owo.nodescript.nodes;

import io.wispforest.owo.nodescript.model.Node;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Registry for all available node types.
 */
public class NodeRegistry {

    private static final Map<String, NodeFactory> FACTORIES = new LinkedHashMap<>();
    private static final Map<String, NodeCategory> CATEGORIES = new LinkedHashMap<>();

    static {
        // Register event nodes
        registerNode(AllowChatEventNode.TYPE, AllowChatEventNode::new, NodeCategory.EVENTS);
        registerNode(HudRenderEventNode.TYPE, HudRenderEventNode::new, NodeCategory.EVENTS);

        // Register logic nodes
        registerNode(StringIncludesNode.TYPE, StringIncludesNode::new, NodeCategory.LOGIC);
        registerNode(BooleanNotNode.TYPE, BooleanNotNode::new, NodeCategory.LOGIC);

        // Register constant nodes
        registerNode(StringConstantNode.TYPE, StringConstantNode::new, NodeCategory.CONSTANTS);
        registerNode(BooleanConstantNode.TYPE, BooleanConstantNode::new, NodeCategory.CONSTANTS);

        // Register action nodes
        registerNode(PrintChatNode.TYPE, PrintChatNode::new, NodeCategory.ACTIONS);
    }

    public static void registerNode(String type, Supplier<Node> factory, NodeCategory category) {
        FACTORIES.put(type, new NodeFactory(type, factory, category));
        CATEGORIES.computeIfAbsent(category.name(), k -> category);
    }

    public static @Nullable Node createNode(String type) {
        var factory = FACTORIES.get(type);
        return factory != null ? factory.factory().get() : null;
    }

    public static Collection<NodeFactory> getAllFactories() {
        return Collections.unmodifiableCollection(FACTORIES.values());
    }

    public static List<NodeFactory> getFactoriesByCategory(NodeCategory category) {
        return FACTORIES.values().stream()
            .filter(f -> f.category() == category)
            .toList();
    }

    public static Collection<NodeCategory> getCategories() {
        return Arrays.asList(NodeCategory.values());
    }

    public record NodeFactory(String type, Supplier<Node> factory, NodeCategory category) {
        public String displayName() {
            var node = factory.get();
            return node.name();
        }
    }

    public enum NodeCategory {
        EVENTS("Events"),
        LOGIC("Logic"),
        CONSTANTS("Constants"),
        ACTIONS("Actions");

        private final String displayName;

        NodeCategory(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }
}
