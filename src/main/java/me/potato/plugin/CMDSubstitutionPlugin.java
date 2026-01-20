package me.potato.plugin;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plugin entrypoint
 */
public class CMDSubstitutionPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static CommandRegistry registry;
    public static String version;
    public static CommandConfig config;
    public static Map<SubAndArgCount, List<String>> argsToConfigList = new HashMap<>();

    public CMDSubstitutionPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log(this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        config = new CommandConfig();

        registry = this.getCommandRegistry();
        version = this.getManifest().getVersion().toString();

        this.getCommandRegistry().registerCommand(new SubCommand(this.getName(), this.getManifest().getVersion().toString()));

        for(List<String> l : config.getMappings()) {
            addSubstitution(l.get(0), l.get(1), this.getName(), false);
//            this.getCommandRegistry().registerCommand(new CommandSubstitution(l.get(1), l.get(0), this.getName(), this.getManifest().getVersion().toString()));
        }
    }


    public static SubAndArgCount getRightOne(String s, int n) {
        for(SubAndArgCount sub : argsToConfigList.keySet()) {
            if(sub.subBase.equals(s) && sub.expectedArgs == n) {
                return sub;
            }
        }

        return null;
    }

    public static void addSubstitution(String abbreviation, String originalCommand, String name, boolean modifyConfig) {
        SubAndArgCount subAndCount = new SubAndArgCount(abbreviation.split(" ")[0], (int) abbreviation.chars()
                .filter(ch -> ch == '$')
                .count());

        CommandSubstitution substitution = new CommandSubstitution(
                originalCommand,
                abbreviation.split(" ")[0],
                name,
                CMDSubstitutionPlugin.version
        );

        CMDSubstitutionPlugin.registry.registerCommand(substitution);
        if(modifyConfig) {
            List<String> mapping = CMDSubstitutionPlugin.config.addToConfig(abbreviation, originalCommand);
            CMDSubstitutionPlugin.argsToConfigList.put(subAndCount, mapping);
        } else {
            List<String> mapping = CommandConfig.SubAndArgCountToMapping(subAndCount);
            CMDSubstitutionPlugin.argsToConfigList.put(subAndCount, mapping);
        }

    }

    @Override
    protected void start() {
//        CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "start command");
    }
}