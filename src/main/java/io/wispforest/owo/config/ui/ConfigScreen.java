package io.wispforest.owo.config.ui;

import io.wispforest.owo.Owo;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.ui.BaseUIModelScreen;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.definitions.Surface;
import io.wispforest.owo.ui.layout.FlowLayout;
import io.wispforest.owo.ui.layout.VerticalFlowLayout;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.NumberReflection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ConfigScreen extends BaseUIModelScreen<FlowLayout> {

    private static final Map<Predicate<Option<?>>, OptionComponentFactory<?>> DEFAULT_FACTORIES = new HashMap<>();
    protected final Map<Predicate<Option<?>>, OptionComponentFactory<?>> extraFactories = new HashMap<>();

    protected final Screen parent;
    protected final ConfigWrapper<?> config;
    @SuppressWarnings("rawtypes") protected final Map<Option, OptionComponent> options = new HashMap<>();

    public ConfigScreen(ConfigWrapper<?> config, @Nullable Screen parent) {
//        super(FlowLayout.class, DataSource.asset(new Identifier("owo", "config_ui")));
        super(FlowLayout.class, DataSource.file("config_ui.xml"));
        this.parent = parent;
        this.config = config;
    }

    public ConfigScreen(ConfigWrapper<?> config) {
        this(config, null);
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    protected void build(FlowLayout rootComponent) {
        this.options.clear();

        rootComponent.childById(LabelComponent.class, "title").text(Text.translatable("text.config." + this.config.name() + ".title"));
        if (this.client.world == null) {
            rootComponent.surface(Surface.OPTIONS_BACKGROUND);
        }

        var panel = rootComponent.childById(VerticalFlowLayout.class, "config-panel");

        var containers = new HashMap<Option.Key, VerticalFlowLayout>();
        containers.put(Option.Key.ROOT, panel);

        this.config.forEachOption(option -> {
            var factory = this.applicableFactory(option);
            if (factory == null) {
                Owo.LOGGER.warn("Could not create UI component for config option {}", option);
            }

            var result = factory.make(this.model, option);
            this.options.put(option, result.optionContainer());

            var parentKey = option.key().parent();
            var container = containers.getOrDefault(
                    parentKey,
                    new OptionContainerLayout(Text.translatable("text.config." + this.config.name() + ".category." + parentKey.asString()))
            );

            if (!containers.containsKey(parentKey)) {
                containers.put(parentKey, container);
                containers.get(parentKey.parent()).child(container);
            }

            container.child(result.baseComponent());
        });
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removed() {
        this.options.forEach((option, component) -> {
            if (!component.isValid()) return;
            option.set(component.parsedValue());
        });
        super.removed();
    }

    @SuppressWarnings("rawtypes")
    protected @Nullable OptionComponentFactory applicableFactory(Option<?> option) {
        for (var predicate : this.extraFactories.keySet()) {
            if (!predicate.test(option)) continue;
            return this.extraFactories.get(predicate);
        }

        for (var predicate : DEFAULT_FACTORIES.keySet()) {
            if (!predicate.test(option)) continue;
            return DEFAULT_FACTORIES.get(predicate);
        }

        return null;
    }

    static {
        DEFAULT_FACTORIES.put(option -> NumberReflection.isNumberType(option.clazz()), OptionComponentFactory.NUMBER);
        DEFAULT_FACTORIES.put(option -> CharSequence.class.isAssignableFrom(option.clazz()), OptionComponentFactory.CHAR_SEQUENCE);
        DEFAULT_FACTORIES.put(option -> option.clazz() == Boolean.class || option.clazz() == boolean.class, OptionComponentFactory.BOOLEAN);

        UIParsing.registerFactory("config-slider", element -> new ConfigSlider());
        UIParsing.registerFactory("config-toggle-button", element -> new ConfigToggleButton());
        UIParsing.registerFactory("config-text-box", element -> new ConfigTextBox());
    }
}