package nl.enjarai.doabarrelroll.compat;

import net.neoforged.fml.ModList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class Compat {
    public static final String YACL_MIN_VERSION = "3.1.0";

    public static boolean isYACLLoaded() {
        return checkModLoaded("yet_another_config_lib_v3");
    }

    public static boolean isYACLUpToDate() {
        return isModVersionAtLeast("yet_another_config_lib_v3", YACL_MIN_VERSION);
    }

    public static boolean checkModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isModVersionAtLeast(String modId, String version) {
        var parsed = new DefaultArtifactVersion(version);
        return ModList.get().getModContainerById(modId)
                .filter(modContainer -> modContainer.getModInfo().getVersion().compareTo(parsed) >= 0)
                .isPresent();
    }
}
