# Braid - Declarative UI Framework

Braid is a modern, declarative UI framework for Minecraft mods built into owo-lib. It provides a Flutter-like development experience with reactive state management, hot-reloading support, and a rich widget library.

## Table of Contents

- [Getting Started](#getting-started)
- [Basic Setup](#basic-setup)
- [Creating Your First Screen](#creating-your-first-screen)
- [Widgets](#widgets)
- [State Management](#state-management)
- [Hot Reloading (Development)](#hot-reloading-development)
- [Advanced Features](#advanced-features)
- [Examples](#examples)

## Getting Started

Braid is included with owo-lib, so if you already have owo-lib as a dependency, you have access to Braid!

### Prerequisites

- owo-lib 0.12.0 or higher
- Minecraft 1.20.1 or higher
- Fabric Loader

### Adding owo-lib to Your Project

If you haven't already added owo-lib to your project, add the following to your `build.gradle`:

```groovy
repositories {
    maven { url 'https://maven.wispforest.io' }
}

dependencies {
    modImplementation "io.wispforest:owo-lib:${project.owo_version}"
    // only if you plan to use owo-config
    annotationProcessor "io.wispforest:owo-lib:${project.owo_version}"
}
```

And in your `gradle.properties`:
```properties
# https://maven.wispforest.io/io/wispforest/owo-lib/
owo_version=0.12.0+1.20.1
```

Check the [releases page](https://github.com/wisp-forest/owo-lib/releases) for the latest version compatible with your Minecraft version.

## Basic Setup

### Creating a Simple Screen

The simplest way to create a Braid screen is to extend `BraidScreen`:

```java
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Center;
import io.wispforest.owo.braid.widgets.label.Label;
import net.minecraft.network.chat.Component;

public class MyFirstBraidScreen extends BraidScreen {
    public MyFirstBraidScreen() {
        super(new MyApp());
    }
    
    static class MyApp extends StatelessWidget {
        @Override
        public Widget build(BuildContext context) {
            return new Center(
                new Label(Component.literal("Hello, Braid!"))
            );
        }
    }
}
```

To open this screen:
```java
Minecraft.getInstance().setScreen(new MyFirstBraidScreen());
```

## Creating Your First Screen

Let's create a more complete example with buttons and layout:

```java
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Center;
import io.wispforest.owo.braid.widgets.basic.Padding;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.CrossAxisAlignment;
import io.wispforest.owo.braid.widgets.flex.MainAxisAlignment;
import io.wispforest.owo.braid.widgets.label.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class WelcomeScreen extends BraidScreen {
    public WelcomeScreen() {
        super(new WelcomeApp());
    }
    
    static class WelcomeApp extends StatelessWidget {
        @Override
        public Widget build(BuildContext context) {
            return new Center(
                new Padding(
                    Insets.all(20),
                    new Column(
                        MainAxisAlignment.CENTER,
                        CrossAxisAlignment.CENTER,
                        List.of(
                            new Label(Component.literal("Welcome to My Mod!")),
                            new MessageButton(
                                Component.literal("Click Me!"),
                                () -> {
                                    System.out.println("Button clicked!");
                                }
                            ),
                            new MessageButton(
                                Component.literal("Close"),
                                () -> {
                                    Minecraft.getInstance().setScreen(null);
                                }
                            )
                        )
                    )
                )
            );
        }
    }
}
```

## Widgets

Braid comes with a rich set of built-in widgets:

### Layout Widgets

- **Column**: Arranges children vertically
- **Row**: Arranges children horizontally
- **Stack**: Overlays children on top of each other
- **Center**: Centers a child widget
- **Padding**: Adds padding around a child
- **Align**: Aligns a child within available space
- **Sized**: Sets fixed dimensions for a child
- **Grid**: Arranges children in a grid layout

### Interactive Widgets

- **Button**: A clickable button with custom content
- **MessageButton**: A button with text
- **Checkbox**: A checkbox with label
- **TextBox**: Multi-line text input
- **Slider**: A value slider
- **ComboBox**: A dropdown selection
- **CyclingButton**: A button that cycles through options

### Display Widgets

- **Label**: Displays text
- **Box**: A colored box
- **Panel**: A styled panel
- **ItemStackWidget**: Displays an item stack
- **EntityWidget**: Displays an entity
- **BlockWidget**: Displays a block
- **SpriteWidget**: Displays a texture

### Container Widgets

- **ScrollableWithBars**: A scrollable container with scrollbars
- **Window**: A draggable window
- **Overlay**: Overlay content above other widgets
- **Collapsible**: Expandable/collapsible section
- **Navigator**: Navigation stack for routing

### Example: Using Different Widgets

```java
import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.core.Size;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.flex.*;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.object.ItemStackWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.List;

public class WidgetShowcase extends StatelessWidget {
    @Override
    public Widget build(BuildContext context) {
        return new Padding(
            Insets.all(10),
            new Column(
                MainAxisAlignment.START,
                CrossAxisAlignment.STRETCH,
                List.of(
                    // Title
                    new Label(Component.literal("Widget Showcase")),
                    
                    // Colored box
                    new Sized(
                        Size.fixed(100, 50),
                        new Box(Color.rgb(0xFF0000), false)
                    ),
                    
                    // Item display
                    new Sized(
                        Size.square(32),
                        new ItemStackWidget(Items.DIAMOND.getDefaultInstance())
                    ),
                    
                    // Button row
                    new Row(
                        MainAxisAlignment.CENTER,
                        CrossAxisAlignment.CENTER,
                        List.of(
                            new MessageButton(
                                Component.literal("Button 1"),
                                () -> System.out.println("Button 1 clicked")
                            ),
                            new MessageButton(
                                Component.literal("Button 2"),
                                () -> System.out.println("Button 2 clicked")
                            )
                        )
                    )
                )
            )
        );
    }
}
```

## State Management

Braid supports stateful widgets that can react to changes:

### StatefulWidget Example

```java
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.CrossAxisAlignment;
import io.wispforest.owo.braid.widgets.flex.MainAxisAlignment;
import io.wispforest.owo.braid.widgets.label.Label;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Counter extends StatefulWidget {
    @Override
    public WidgetState<Counter> createState() {
        return new CounterState();
    }
    
    static class CounterState extends WidgetState<Counter> {
        private int count = 0;
        
        @Override
        public Widget build(BuildContext context) {
            return new Column(
                MainAxisAlignment.CENTER,
                CrossAxisAlignment.CENTER,
                List.of(
                    new Label(Component.literal("Count: " + count)),
                    new MessageButton(
                        Component.literal("Increment"),
                        () -> {
                            count++;
                            markNeedsBuild(); // Trigger rebuild
                        }
                    ),
                    new MessageButton(
                        Component.literal("Decrement"),
                        () -> {
                            count--;
                            markNeedsBuild(); // Trigger rebuild
                        }
                    )
                )
            );
        }
    }
}
```

### Using ListenableValue

For reactive values that automatically trigger rebuilds:

```java
import io.wispforest.owo.braid.core.ListenableValue;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.ListenableBuilder;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.label.Label;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ReactiveCounter extends StatelessWidget {
    private final ListenableValue<Integer> counter = new ListenableValue<>(0);
    
    @Override
    public Widget build(BuildContext context) {
        return new Column(
            List.of(
                new ListenableBuilder(
                    counter,
                    (ctx, value) -> new Label(Component.literal("Count: " + value))
                ),
                new MessageButton(
                    Component.literal("Increment"),
                    () -> counter.set(counter.get() + 1)
                )
            )
        );
    }
}
```

## Hot Reloading (Development)

Braid supports hot-reloading during development, allowing you to see UI changes without restarting Minecraft!

### Setting Up Hot Reload

1. **Add the braid-reload-agent dependency** to your `build.gradle`:

```groovy
dependencies {
    // Your existing dependencies
    modImplementation "io.wispforest:owo-lib:${project.owo_version}"
    
    // Add the reload agent for development
    modLocalRuntime "io.wispforest:braid-reload-agent:0.1.0"
}
```

2. **Configure your run configuration** to use the agent. In your `build.gradle`:

```groovy
loom {
    runs {
        client {
            // Add this to enable hot reloading
            vmArg "-javaagent:${configurations.modLocalRuntime.find { it.name.startsWith("braid-reload-agent") }.absolutePath}"
        }
    }
}
```

3. **Use your IDE's hot-swap feature**:
   - In IntelliJ IDEA: Run with Debug mode, then use "Build Project" (Ctrl+F9) to apply changes
   - In Eclipse: Changes are applied automatically when saving in Debug mode

When you modify a Widget or WidgetState class and hot-swap it, Braid will automatically rebuild the affected UI!

## Advanced Features

### Navigation

Braid includes a navigation system for managing multiple screens:

```java
import io.wispforest.owo.braid.widgets.Navigator;

// Push a new route
Navigator.push(context, new DetailsPage());

// Pop the current route
Navigator.pop(context);
```

### Animations

Braid supports smooth animations:

```java
import io.wispforest.owo.braid.animation.Animation;
import io.wispforest.owo.braid.animation.Easing;
import io.wispforest.owo.braid.widgets.animated.AnimatedAlign;

// Animate alignment changes
new AnimatedAlign(
    new Animation<>(200, Easing.CUBIC),
    initialAlignment,
    new Label(Component.literal("I move!"))
)
```

### Custom Widgets

Create your own reusable widgets:

```java
public class MyCustomWidget extends StatelessWidget {
    private final String title;
    private final Runnable onPress;
    
    public MyCustomWidget(String title, Runnable onPress) {
        this.title = title;
        this.onPress = onPress;
    }
    
    @Override
    public Widget build(BuildContext context) {
        return new Panel(
            Panel.VANILLA_DARK,
            new Padding(
                Insets.all(10),
                new Column(
                    List.of(
                        new Label(Component.literal(title)),
                        new MessageButton(
                            Component.literal("Action"),
                            onPress
                        )
                    )
                )
            )
        );
    }
}
```

### Integration with owo-ui

You can embed owo-ui components in Braid:

```java
import io.wispforest.owo.braid.widgets.owoui.OwoUIWidget;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.core.Sizing;

new OwoUIWidget((context, parent) -> {
    var component = UIComponents.button(
        Component.literal("owo-ui Button")
    ).sizing(Sizing.fixed(100), Sizing.fixed(20));
    
    parent.child(component);
})
```

## Examples

### Complete Example: Settings Screen

```java
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Padding;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.checkbox.Checkbox;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.CrossAxisAlignment;
import io.wispforest.owo.braid.widgets.flex.MainAxisAlignment;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.slider.slider.MessageSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SettingsScreen extends BraidScreen {
    public SettingsScreen() {
        super(new SettingsApp());
    }
    
    static class SettingsApp extends StatefulWidget {
        @Override
        public WidgetState<SettingsApp> createState() {
            return new SettingsState();
        }
    }
    
    static class SettingsState extends WidgetState<SettingsApp> {
        private boolean enableFeature = true;
        private double volume = 0.5;
        
        @Override
        public Widget build(BuildContext context) {
            return new Padding(
                Insets.all(20),
                new Column(
                    MainAxisAlignment.START,
                    CrossAxisAlignment.STRETCH,
                    List.of(
                        new Label(Component.literal("Settings")),
                        
                        new Checkbox(
                            Component.literal("Enable Feature"),
                            enableFeature,
                            (checked) -> {
                                enableFeature = checked;
                                markNeedsBuild();
                            }
                        ),
                        
                        new Label(Component.literal("Volume: " + (int)(volume * 100) + "%")),
                        new MessageSlider(
                            Component.literal("Volume"),
                            0.0, 1.0, volume,
                            (newVolume) -> {
                                volume = newVolume;
                                markNeedsBuild();
                            }
                        ),
                        
                        new MessageButton(
                            Component.literal("Save & Close"),
                            () -> {
                                // Save settings here
                                Minecraft.getInstance().setScreen(null);
                            }
                        )
                    )
                )
            );
        }
    }
}
```

## Further Resources

- **JavaDoc**: Braid has extensive JavaDoc documentation throughout the codebase
- **Source Code**: Check out the `io.wispforest.owo.braid` package for the full API
- **Examples**: Look at the testmod in `src/testmod/java/io/wispforest/uwu` for more examples
- **owo-lib Wiki**: https://docs.wispforest.io/owo/features

## Tips and Best Practices

1. **Use StatelessWidget when possible**: Stateless widgets are simpler and more efficient
2. **Break down complex UIs**: Create small, reusable widget classes
3. **Leverage ListenableValue**: For reactive state that multiple widgets depend on
4. **Use const constructors**: When widgets don't change, they can be reused
5. **Hot reload during development**: It dramatically speeds up UI development
6. **Explore the widget library**: Braid has many built-in widgets that solve common problems

## Migration from owo-ui

If you're familiar with owo-ui, here are the key differences:

- **Declarative vs Imperative**: Braid uses a declarative approach (like Flutter) vs owo-ui's imperative approach
- **Widgets vs Components**: Braid uses Widgets that rebuild when state changes
- **Build method**: Instead of manually constructing a tree, you declare what the UI should look like
- **State management**: Braid has built-in state management with StatefulWidget and ListenableValue

Both systems can coexist in the same mod, and you can even embed owo-ui components in Braid screens using `OwoUIWidget`.
