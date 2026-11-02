package nl.enjarai.doabarrelroll.mixin.client.key;

import net.minecraft.client.option.KeyBinding;
import nl.enjarai.doabarrelroll.api.key.InputContext;
import nl.enjarai.doabarrelroll.util.key.ContextualKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the {@link InputContext} API functional. On NeoForge, the actual "only active while flying"
 * behaviour of the flight keys is handled natively by {@link nl.enjarai.doabarrelroll.ModKeybindings#FLIGHT_CONTEXT}.
 */
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements ContextualKeyBinding {
    @Unique
    private final ArrayList<InputContext> contexts = new ArrayList<>();

    @Override
    public List<InputContext> doABarrelRoll$getContexts() {
        return contexts;
    }

    @Override
    public void doABarrelRoll$addToContext(InputContext context) {
        contexts.add(context);
    }
}
