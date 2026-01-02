package io.wispforest.owo.nodescript.ui;

import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.core.cursor.CursorStyle;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.collapsible.Collapsible;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.label.LabelStyle;
import io.wispforest.owo.braid.widgets.scroll.VerticallyScrollable;
import io.wispforest.owo.nodescript.nodes.NodeRegistry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Widget showing the palette of available nodes that can be added to the graph.
 */
public class NodePaletteWidget extends StatefulWidget {

    private final Consumer<String> onNodeSelected;

    public NodePaletteWidget(Consumer<String> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
    }

    @Override
    public WidgetState<NodePaletteWidget> createState() {
        return new State();
    }

    private class State extends WidgetState<NodePaletteWidget> {

        @Override
        public Widget build(BuildContext context) {
            List<Widget> categoryWidgets = new ArrayList<>();

            for (var category : NodeRegistry.getCategories()) {
                var factories = NodeRegistry.getFactoriesByCategory(category);
                if (factories.isEmpty()) continue;

                List<Widget> nodeButtons = new ArrayList<>();
                for (var factory : factories) {
                    nodeButtons.add(
                        new Padding(
                            Insets.vertical(2),
                            new MouseArea(
                                widget -> widget
                                    .clickCallback((x, y, button, modifiers) -> {
                                        onNodeSelected.accept(factory.type());
                                        return true;
                                    })
                                    .cursorStyle(CursorStyle.HAND),
                                new Box(
                                    Color.rgb(0x3A3D41),
                                    new Padding(
                                        Insets.all(4),
                                        new Label(
                                            Component.literal(factory.displayName())
                                        )
                                    )
                                )
                            )
                        )
                    );
                }

                categoryWidgets.add(
                    new Collapsible(
                        true,
                        new Label(
                            LabelStyle.SHADOW,
                            true,
                            Component.literal(category.displayName())
                        ),
                        new Column(nodeButtons)
                    )
                );
            }

            return new Box(
                Color.rgb(0x252526),
                new Padding(
                    Insets.all(8),
                    new Sized(
                        150.0,
                        null,
                        new Column(
                            new Label(
                                LabelStyle.SHADOW,
                                true,
                                Component.literal("Node Palette")
                            ),
                            new Padding(Insets.vertical(4)),
                            new VerticallyScrollable(
                                new Column(categoryWidgets)
                            )
                        )
                    )
                )
            );
        }
    }
}
