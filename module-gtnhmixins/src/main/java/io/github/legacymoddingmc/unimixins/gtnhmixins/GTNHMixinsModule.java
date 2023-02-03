package io.github.legacymoddingmc.unimixins.gtnhmixins;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.versioning.ComparableVersion;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GTNHMixinsModule {

    private static final Logger LOGGER = LogManager.getLogger("unimixin-gtnhmixins");

    public static void init() {
        if(!skipIntegrityChecks()) {
            checkComponentIntegrity();
        }
        registerASMRemapPackage("com.gtnewhorizon.mixinextras");
    }
    
    public static boolean isMixinExtrasEnabled() {
        String requiredVersion = "0.8.5";
        String mixinVersion = (String)Launch.blackboard.get("mixin.initialised");
        if(mixinVersion != null && new ComparableVersion(mixinVersion).compareTo(new ComparableVersion(requiredVersion)) >= 0) {
            LOGGER.debug("Intializing MixinExtras");
            return true;
        } else if(skipIntegrityChecks()){
            LOGGER.warn("Skipping MixinExtras because Mixin version (" + mixinVersion + ") is lower than the required (" + requiredVersion + ")");
            return false;
        } else {
            throw new RuntimeException("Cannot load MixinExtras because Mixin version (" + mixinVersion + ") is lower than the required (" + requiredVersion + ")");
        }
    }

    private static void registerASMRemapPackage(String pkg) {
        try {
            Class.forName("io.github.legacymoddingmc.unimixins.compat.asm.ASMRemapperTransformer").getMethod("registerPackage", String.class).invoke(null, pkg);
        } catch(Exception e) {
            LOGGER.warn("Failed to register package " + pkg + " for ASM remapping, probably because the compat module is missing. " + e);
        }
    }

    private static boolean skipIntegrityChecks() {
        return Boolean.parseBoolean(System.getProperty("unimixins.skipIntegrityChecks", "false"));
    }

    private static void checkComponentIntegrity() {
        List<String> missingComponents = new ArrayList<>();
        
        // This is actually redundant; without Mixin, the mod can't even load since its tweaker class is missing
        if(!classExists("org.spongepowered.asm.launch.MixinBootstrap")) {
            missingComponents.add("Mixin");
        }
        if(!classExists("ru.timeconqueror.spongemixins.SpongeMixins")) {
            missingComponents.add("SpongeMixins");
        }
        
        if(!missingComponents.isEmpty()) {
            LOGGER.error("The following missing components were detected: " + missingComponents);
            LOGGER.error("Please obtain mods which provide them. See the readme inside the mod jar or the mod's GitHub page for more details.");
            throw new RuntimeException("Missing components detected");
        }
    }

    public static boolean classExists(String string) {
        return GTNHMixinsModule.class.getResource("/" + string.replaceAll("\\.", "/") + ".class") != null;
    }
    
    

}
