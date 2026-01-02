package io.wispforest.owo.nodescript.ui;

import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.core.cursor.CursorStyle;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.BraidApp;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.drag.DragArena;
import io.wispforest.owo.braid.widgets.drag.DragArenaElement;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.Flexible;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.label.LabelStyle;
import io.wispforest.owo.braid.widgets.stack.Stack;
import io.wispforest.owo.braid.widgets.textinput.TextBox;
import io.wispforest.owo.braid.widgets.textinput.TextEditingController;
import io.wispforest.owo.nodescript.NodeScriptManager;
import io.wispforest.owo.nodescript.model.*;
import io.wispforest.owo.nodescript.nodes.*;
import io.wispforest.owo.nodescript.serialization.NodeGraphSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The main node editor widget.
 */
public class NodeEditorWidget extends StatefulWidget {

    private final @Nullable NodeGraph initialGraph;

    public NodeEditorWidget(@Nullable NodeGraph initialGraph) {
        this.initialGraph = initialGraph;
    }

    public NodeEditorWidget() {
        this(null);
    }

    @Override
    public WidgetState<NodeEditorWidget> createState() {
        return new State();
    }

    public class State extends WidgetState<NodeEditorWidget> {

        private NodeGraph graph;
        private @Nullable Node selectedNode;
        private @Nullable NodePort connectingPort;
        private double connectX, connectY;

        private final TextEditingController fileNameController = new TextEditingController();

        private double canvasOffsetX = 0;
        private double canvasOffsetY = 0;

        @Override
        public void init() {
            if (widget().initialGraph != null) {
                this.graph = widget().initialGraph;
                this.fileNameController.setText(graph.name());
            } else {
                this.graph = new NodeGraph("New Graph");
                this.fileNameController.setText("new_graph");
            }
        }

        @Override
        public Widget build(BuildContext context) {
            return new Stack(
                // Background grid
                new Box(Color.rgb(0x1E1E1E)),
                // Main layout
                new Row(
                    // Left sidebar - node palette
                    new NodePaletteWidget(this::addNode),
                    // Main canvas area
                    new Flexible(
                        new Column(
                            // Toolbar
                            buildToolbar(),
                            // Canvas
                            new Flexible(
                                buildCanvas()
                            )
                        )
                    ),
                    // Right sidebar - node properties
                    buildPropertiesPanel()
                )
            );
        }

        private Widget buildToolbar() {
            return new Box(
                Color.rgb(0x333333),
                new Padding(
                    Insets.all(8),
                    new Row(
                        new Sized(
                            150.0, 20.0,
                            new TextBox(
                                fileNameController,
                                widget -> widget.placeholder(Component.literal("Graph name"))
                            )
                        ),
                        new Padding(Insets.horizontal(4)),
                        new MessageButton(
                            Component.literal("Save"),
                            this::saveGraph
                        ),
                        new Padding(Insets.horizontal(4)),
                        new MessageButton(
                            Component.literal("Load"),
                            this::loadGraph
                        ),
                        new Padding(Insets.horizontal(8)),
                        new MessageButton(
                            Component.literal("Execute"),
                            this::executeGraph
                        ),
                        new Padding(Insets.horizontal(8)),
                        new MessageButton(
                            Component.literal("Clear"),
                            this::clearGraph
                        ),
                        new Flexible(new Padding(Insets.none())),
                        new Label(
                            Component.literal("Nodes: " + graph.nodes().size() + " | Connections: " + graph.connections().size()).withColor(0x888888)
                        )
                    )
                )
            );
        }

        private Widget buildCanvas() {
            List<Widget> nodeWidgets = new ArrayList<>();

            for (var node : graph.nodes()) {
                nodeWidgets.add(
                    new DragArenaElement(
                        node.x() + canvasOffsetX,
                        node.y() + canvasOffsetY,
                        new NodeWidget(
                            node,
                            node == selectedNode,
                            () -> selectNode(node),
                            this::handlePortClick,
                            (dx, dy) -> moveNode(node, dx, dy)
                        )
                    )
                );
            }

            return new MouseArea(
                widget -> widget
                    .clickCallback((x, y, button, modifiers) -> {
                        // Deselect on canvas click
                        if (button == 0) {
                            setState(() -> {
                                selectedNode = null;
                                connectingPort = null;
                            });
                        }
                        return true;
                    })
                    .dragCallback((x, y, dx, dy) -> {
                        // Pan canvas with middle mouse
                        setState(() -> {
                            canvasOffsetX += dx;
                            canvasOffsetY += dy;
                        });
                    })
                    .cursorStyle(CursorStyle.MOVE),
                new DragArena(nodeWidgets)
            );
        }

        private Widget buildPropertiesPanel() {
            if (selectedNode == null) {
                return new Box(
                    Color.rgb(0x252526),
                    new Padding(
                        Insets.all(8),
                        new Sized(
                            150.0, null,
                            new Column(
                                new Label(
                                    LabelStyle.SHADOW,
                                    true,
                                    Component.literal("Properties")
                                ),
                                new Padding(Insets.vertical(8)),
                                new Label(
                                    Component.literal("Select a node to\nview properties").withColor(0x888888)
                                )
                            )
                        )
                    )
                );
            }

            List<Widget> propertyWidgets = new ArrayList<>();
            propertyWidgets.add(
                new Label(
                    LabelStyle.SHADOW,
                    true,
                    Component.literal("Properties")
                )
            );
            propertyWidgets.add(new Padding(Insets.vertical(4)));
            propertyWidgets.add(
                new Label(
                    Component.literal("Type: " + selectedNode.getNodeType()).withColor(0x888888)
                )
            );
            propertyWidgets.add(new Padding(Insets.vertical(4)));

            // Add node-specific property editors
            if (selectedNode instanceof StringConstantNode stringConst) {
                var controller = new TextEditingController();
                controller.setText(stringConst.value());
                controller.addListener(() -> {
                    stringConst.setValue(controller.value().text());
                    setState(() -> {});
                });

                propertyWidgets.add(new Label(Component.literal("Value:")));
                propertyWidgets.add(
                    new Sized(
                        null, 20.0,
                        new TextBox(controller, null)
                    )
                );
            } else if (selectedNode instanceof BooleanConstantNode boolConst) {
                propertyWidgets.add(
                    new Row(
                        new Label(Component.literal("Value: ")),
                        new MessageButton(
                            Component.literal(boolConst.value() ? "true" : "false"),
                            () -> {
                                boolConst.setValue(!boolConst.value());
                                setState(() -> {});
                            }
                        )
                    )
                );
            } else if (selectedNode instanceof NumberConstantNode numberConst) {
                var controller = new TextEditingController();
                controller.setText(String.valueOf(numberConst.value()));
                
                // Track if current input is valid
                final boolean[] isValid = {true};
                controller.addListener(() -> {
                    try {
                        numberConst.setValue(Double.parseDouble(controller.value().text()));
                        isValid[0] = true;
                        setState(() -> {});
                    } catch (NumberFormatException e) {
                        isValid[0] = false;
                    }
                });

                propertyWidgets.add(new Label(Component.literal("Value (number):")));
                propertyWidgets.add(
                    new Sized(
                        null, 20.0,
                        new TextBox(controller, widget -> widget.placeholder(Component.literal("Enter a number")))
                    )
                );
            }

            propertyWidgets.add(new Padding(Insets.vertical(8)));
            propertyWidgets.add(
                new MessageButton(
                    Component.literal("Delete Node"),
                    this::deleteSelectedNode
                )
            );

            return new Box(
                Color.rgb(0x252526),
                new Padding(
                    Insets.all(8),
                    new Sized(
                        150.0, null,
                        new Column(propertyWidgets)
                    )
                )
            );
        }

        private void addNode(String nodeType) {
            var node = NodeRegistry.createNode(nodeType);
            if (node == null) return;

            // Place new node at center of canvas
            node.setPosition(-canvasOffsetX + 200, -canvasOffsetY + 200);

            setState(() -> {
                graph.addNode(node);
                selectedNode = node;
            });
        }

        private void selectNode(Node node) {
            setState(() -> selectedNode = node);
        }

        private void moveNode(Node node, double dx, double dy) {
            node.setPosition(node.x() + dx, node.y() + dy);
            setState(() -> {});
        }

        private void handlePortClick(NodePort port, double x, double y) {
            setState(() -> {
                if (connectingPort == null) {
                    // Start connecting
                    connectingPort = port;
                    connectX = x;
                    connectY = y;
                } else {
                    // Try to connect
                    if (connectingPort.canConnectTo(port)) {
                        graph.connect(connectingPort, port);
                    }
                    connectingPort = null;
                }
            });
        }

        private void deleteSelectedNode() {
            if (selectedNode == null) return;
            setState(() -> {
                graph.removeNode(selectedNode);
                selectedNode = null;
            });
        }

        private void saveGraph() {
            var name = fileNameController.value().text();
            if (name.isBlank()) {
                name = "untitled";
            }
            graph.setName(name);

            var path = getScriptsPath().resolve(name + ".json");
            try {
                NodeGraphSerializer.saveToFile(graph, path);
                NodeScriptManager.INSTANCE.loadGraph(graph);

                var player = Minecraft.getInstance().player;
                if (player != null) {
                    player.displayClientMessage(
                        Component.literal("Saved and activated: " + name + ".json"),
                        true
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
                var player = Minecraft.getInstance().player;
                if (player != null) {
                    player.displayClientMessage(
                        Component.literal("Failed to save: " + e.getMessage()),
                        true
                    );
                }
            }
        }

        private void loadGraph() {
            var name = fileNameController.value().text();
            if (name.isBlank()) return;

            var path = getScriptsPath().resolve(name + ".json");
            try {
                var loaded = NodeGraphSerializer.loadFromFile(path);
                if (loaded != null) {
                    setState(() -> {
                        this.graph = loaded;
                        this.selectedNode = null;
                    });

                    var player = Minecraft.getInstance().player;
                    if (player != null) {
                        player.displayClientMessage(
                            Component.literal("Loaded: " + name + ".json"),
                            true
                        );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                var player = Minecraft.getInstance().player;
                if (player != null) {
                    player.displayClientMessage(
                        Component.literal("Failed to load: " + e.getMessage()),
                        true
                    );
                }
            }
        }

        private void executeGraph() {
            // Add graph to manager for event handling
            NodeScriptManager.INSTANCE.loadGraph(graph);

            var player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage(
                    Component.literal("Graph activated! Events will be handled."),
                    true
                );
            }
        }

        private void clearGraph() {
            setState(() -> {
                this.graph = new NodeGraph(fileNameController.value().text());
                this.selectedNode = null;
            });
        }

        private Path getScriptsPath() {
            var path = FabricLoader.getInstance().getGameDir().resolve("nodescripts");
            try {
                java.nio.file.Files.createDirectories(path);
            } catch (IOException ignored) {}
            return path;
        }
    }
}
