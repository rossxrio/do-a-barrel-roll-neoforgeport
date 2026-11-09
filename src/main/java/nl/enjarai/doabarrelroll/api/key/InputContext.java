package nl.enjarai.doabarrelroll.api.key;

import nl.enjarai.doabarrelroll.impl.key.InputContextImpl;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public interface InputContext {
    static InputContext of(Identifier id, Supplier<Boolean> activeCondition) {
        return new InputContextImpl(id, activeCondition);
    }

    Identifier getId();

    boolean isActive();

    void addKeyBinding(KeyMapping keyBinding);

    List<KeyMapping> getKeyBindings();

    KeyMapping getKeyBinding(InputConstants.Key key);

    void updateKeysByCode();
}
