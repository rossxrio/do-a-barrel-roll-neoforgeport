package nl.enjarai.doabarrelroll.impl.key;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import nl.enjarai.doabarrelroll.api.key.InputContext;
import nl.enjarai.doabarrelroll.mixin.client.key.KeyBindingAccessor;
import nl.enjarai.doabarrelroll.util.key.ContextualKeyBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class InputContextImpl implements InputContext {
    private static final List<InputContextImpl> CONTEXTS = new ReferenceArrayList<>();

    public static List<InputContextImpl> getContexts() {
        return CONTEXTS;
    }

    public static boolean contextsContain(KeyMapping binding) {
        for (var context : InputContextImpl.getContexts()) {
            if (context.getKeyBindings().contains(binding)) {
                return true;
            }
        }

        return false;
    }

    private final Identifier id;
    private final Supplier<Boolean> activeCondition;
    private final List<KeyMapping> keyBindings = new ReferenceArrayList<>();
    private final Map<InputConstants.Key, KeyMapping> bindingsByKey = new HashMap<>();
    private boolean active;

    public InputContextImpl(Identifier id, Supplier<Boolean> activeCondition) {
        this.id = id;
        this.activeCondition = activeCondition;
        CONTEXTS.add(this);
    }

    public void tick() {
        boolean active = activeCondition.get();
        if (active != this.active) {
            this.active = active;
            KeyMapping.setAll();
        }
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void addKeyBinding(KeyMapping keyBinding) {
        Objects.requireNonNull(keyBinding);
        keyBindings.add(keyBinding);
        ((ContextualKeyBinding) keyBinding).doABarrelRoll$addToContext(this);
    }

    @Override
    public List<KeyMapping> getKeyBindings() {
        return keyBindings;
    }

    @Override
    public KeyMapping getKeyBinding(InputConstants.Key key) {
        return bindingsByKey.get(key);
    }

    @Override
    public void updateKeysByCode() {
        bindingsByKey.clear();
        for (KeyMapping keyBinding : keyBindings) {
            bindingsByKey.put(((KeyBindingAccessor) keyBinding).getKey(), keyBinding);
        }
    }
}
