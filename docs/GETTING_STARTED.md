# Getting Started with oωo-lib and Braid

This guide will help you add oωo-lib (owo-lib) and braid support to your Fabric mod.

## Table of Contents

- [Build Setup](#build-setup)
- [owo-lib Features Overview](#owo-lib-features-overview)
- [Braid UI Framework](#braid-ui-framework)
- [Example: Creating a Simple Counter Screen](#example-creating-a-simple-counter-screen)
- [Hot Reload Setup for Development](#hot-reload-setup-for-development)
- [Additional Resources](#additional-resources)

---

## Build Setup

### Adding oωo-lib to your project

First, add the WispForest Maven repository and dependencies to your `build.gradle`:

```groovy
repositories {
    maven { url 'https://maven.wispforest.io' }
}

dependencies {
    // Core owo-lib dependency
    modImplementation "io.wispforest:owo-lib:${project.owo_version}"
    
    // Required if you plan to use owo-config (annotation processor for config generation)
    annotationProcessor "io.wispforest:owo-lib:${project.owo_version}"
    
    // Optional: Include owo-sentinel to allow automatic download if owo-lib is not installed
    // This is useful if you don't want to force your users to manually install owo-lib
    include "io.wispforest:owo-sentinel:${project.owo_version}"
}
```

Then add the version to your `gradle.properties`:

```properties
# Check https://maven.wispforest.io/io/wispforest/owo-lib/ for the latest version
owo_version=0.13.0-alpha.10+1.21.11
```

### Kotlin DSL

If you're using Kotlin DSL for Gradle, use this syntax instead:

```kotlin
repositories {
    maven("https://maven.wispforest.io")
}

dependencies {
    modImplementation("io.wispforest:owo-lib:${properties["owo_version"]}")
    annotationProcessor("io.wispforest:owo-lib:${properties["owo_version"]}")
    include("io.wispforest:owo-sentinel:${properties["owo_version"]}")
}
```

---

## owo-lib Features Overview

oωo-lib provides many utilities for Fabric mod development:

### 1. Registration System

Automatic registration system that reduces boilerplate code:

```java
// Traditional registration
Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("mymod", "my_item"), myItem);

// With owo-lib, you can organize items in classes with automatic registration
```

### 2. Item Groups

Create custom creative mode tabs with sub-tabs and buttons:

```java
public static final OwoItemGroup MY_GROUP = OwoItemGroup.builder(
        Identifier.fromNamespaceAndPath("mymod", "main"), 
        () -> Icon.of(Items.DIAMOND))
    .initializer(group -> {
        group.addTab(Icon.of(Items.DIAMOND), "tab_1", null, true);
        group.addTab(Icon.of(Items.EMERALD), "tab_2", null, false);
        group.addButton(ItemGroupButton.github(group, "https://github.com/your/repo"));
    })
    .build();

// Don't forget to initialize in your ModInitializer
MY_GROUP.initialize();
```

### 3. Networking

Fully automatic packet serialization with handshaking:

```java
public static final OwoNetChannel CHANNEL = OwoNetChannel.create(
    Identifier.fromNamespaceAndPath("mymod", "main")
);

// Define a record for your packet
public record MyPacket(String message, int value) {}

// Register handlers
CHANNEL.registerClientbound(MyPacket.class, (message, access) -> {
    access.player().displayClientMessage(Component.literal(message.message()), false);
});

CHANNEL.registerServerbound(MyPacket.class, (message, access) -> {
    // Handle on server
});
```

### 4. owo-config

Built-in configuration system with automatic UI generation:

```java
@Modmenu(modId = "mymod")
@Config(name = "my-config", wrapperName = "MyConfig")
public class MyConfigModel {
    @SectionHeader("general")
    public boolean enableFeature = true;
    
    @RangeConstraint(min = 0, max = 100)
    public int someValue = 50;
}
```

---

## Braid UI Framework

Braid is a declarative, reactive UI framework built on top of oωo-lib. It's inspired by Flutter's widget system and provides a powerful way to build Minecraft GUIs.

### Core Concepts

1. **Widgets**: The building blocks of your UI
2. **StatelessWidget**: A widget that doesn't change over time
3. **StatefulWidget**: A widget that can rebuild when its state changes
4. **BuildContext**: Provides access to the widget tree and shared state

### Basic Widgets

```java
// Layout widgets
new Center(child)           // Centers a child widget
new Padding(insets, child)  // Adds padding around a child
new Row(children...)        // Horizontal layout
new Column(children...)     // Vertical layout
new Stack(children...)      // Overlapping widgets

// Display widgets
new Label(Component.literal("Hello World"))
new Box(Color.RED)          // Colored box
new Panel(Panel.VANILLA_LIGHT, child)  // Minecraft-styled panel

// Interactive widgets
new Button(onClick, child)
new MessageButton(Component.literal("Click me"), onClick)
```

### Creating a StatelessWidget

```java
public class MyWidget extends StatelessWidget {
    @Override
    public Widget build(BuildContext context) {
        return new Center(
            new Panel(
                Panel.VANILLA_LIGHT,
                new Padding(
                    Insets.all(10),
                    new Label(Component.literal("Hello, Braid!"))
                )
            )
        );
    }
}
```

### Creating a StatefulWidget

```java
public class Counter extends StatefulWidget {
    @Override
    public WidgetState<Counter> createState() {
        return new CounterState();
    }
    
    public static class CounterState extends WidgetState<Counter> {
        private int count = 0;
        
        @Override
        public Widget build(BuildContext context) {
            return new Center(
                new Panel(
                    Panel.VANILLA_LIGHT,
                    new Padding(
                        Insets.all(10),
                        new MessageButton(
                            Component.literal("Count: " + this.count),
                            () -> this.setState(() -> this.count++)
                        )
                    )
                )
            );
        }
    }
}
```

### Opening a Braid Screen

```java
// From your item or keybinding handler
Minecraft.getInstance().setScreen(new BraidScreen(new MyWidget()));

// With custom settings
var settings = new BraidScreen.Settings();
settings.shouldPause = false;  // Don't pause the game
Minecraft.getInstance().setScreen(new BraidScreen(settings, new MyWidget()));
```

---

## Example: Creating a Simple Counter Screen

Here's a complete example of a counter screen with shared state:

### 1. Create the Counter State

```java
package com.example.mymod.ui;

import io.wispforest.owo.braid.widgets.sharedstate.ShareableState;

public class CounterState extends ShareableState {
    public int count = 0;
}
```

### 2. Create the Counter Label Widget

```java
package com.example.mymod.ui;

import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;

public class CounterLabel extends StatelessWidget {
    @Override
    public Widget build(BuildContext context) {
        var state = SharedState.get(context, CounterState.class);
        return Label.literal("Count: " + state.count);
    }
}
```

### 3. Create the Counter Button Widget

```java
package com.example.mymod.ui;

import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
import net.minecraft.network.chat.Component;

public class CounterButton extends StatelessWidget {
    private final int increment;
    
    public CounterButton(int increment) {
        this.increment = increment;
    }
    
    @Override
    public Widget build(BuildContext context) {
        String label = this.increment > 0 ? "+" + this.increment : String.valueOf(this.increment);
        return new MessageButton(
            Component.literal(label),
            () -> SharedState.set(context, CounterState.class, state -> state.count += this.increment)
        );
    }
}
```

### 4. Create the Main Counter Screen Widget

```java
package com.example.mymod.ui;

import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.flex.*;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;

public class CounterScreen extends StatelessWidget {
    @Override
    public Widget build(BuildContext context) {
        return new Center(
            new Panel(
                Panel.VANILLA_DARK,
                new Padding(
                    Insets.all(10),
                    new Sized(
                        70, null,
                        new SharedState<>(
                            CounterState::new,
                            new Column(
                                new Row(
                                    new Flexible(new CounterButton(-1)),
                                    new Flexible(new CounterButton(1))
                                ),
                                new Padding(
                                    Insets.top(5),
                                    new CounterLabel()
                                )
                            )
                        )
                    )
                )
            )
        );
    }
}
```

### 5. Create an Item to Open the Screen

```java
package com.example.mymod.items;

import com.example.mymod.ui.CounterScreen;
import io.wispforest.owo.braid.core.BraidScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class CounterItem extends Item {
    public CounterItem(Properties settings) {
        super(settings);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide()) {
            Minecraft.getInstance().setScreen(new BraidScreen(new CounterScreen()));
        }
        return InteractionResult.SUCCESS;
    }
}
```

---

## Hot Reload Setup for Development

Braid supports hot reload during development, allowing you to see UI changes immediately without restarting the game.

### Setting Up the Braid Reload Agent

1. Build the braid-reload-agent:
```bash
cd braid-reload-agent
./gradlew build
```

2. Add the agent to your run configuration. In your IDE's run configuration, add this JVM argument:
```
-javaagent:path/to/braid-reload-agent.jar
```

3. Enable hot reload in your IDE (e.g., IntelliJ IDEA's "Build on Save" or use the "Recompile" action)

When you modify a Widget or WidgetState class and recompile, the changes will be reflected immediately in the running game.

---

## Additional Resources

- **oωo-lib Documentation**: https://docs.wispforest.io/owo/features
- **owo-ui Guide**: https://docs.wispforest.io/owo/ui
- **owo-config Guide**: https://docs.wispforest.io/owo/config
- **GitHub Repository**: https://github.com/wisp-forest/owo-lib
- **Discord**: https://discord.gg/xrwHKktV2d (Wisp Forest Discord)

### Testmod Examples

The repository includes a comprehensive testmod with many examples:
- `src/testmod/java/io/wispforest/owo/samples/braid/` - Braid sample widgets
- `src/testmod/java/io/wispforest/uwu/` - Various oωo-lib feature examples

### Key Classes to Explore

**Braid Core:**
- `BraidScreen` - Main screen class for braid UIs
- `StatelessWidget` - Base class for stateless widgets
- `StatefulWidget` - Base class for stateful widgets
- `WidgetState` - State management for stateful widgets

**Layout Widgets:**
- `Center`, `Padding`, `Sized`, `Constrain` - Single child layouts
- `Row`, `Column` - Flex layouts
- `Stack` - Overlapping layouts
- `Grid` - Grid layouts

**Interactive Widgets:**
- `Button`, `MessageButton` - Clickable buttons
- `TextInput`, `TextBox` - Text input fields
- `Slider`, `Checkbox` - Form controls
- `Navigator` - Page navigation

**Styling:**
- `Panel` - Minecraft-styled panels
- `Box` - Colored boxes
- `Label` - Text display
