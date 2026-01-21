package me.potato.plugin;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandContext;
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

        for(SubData l : config.getMappings()) {
            addSubstitution(l.getSub(), l.getOriginal(), this.getName(), false);
//            this.getCommandRegistry().registerCommand(new CommandSubstitution(l.get(1), l.get(0), this.getName(), this.getManifest().getVersion().toString()));
        }
    }

    public static void addSubstitution(String abbreviation, String originalCommand, String name, boolean modifyConfig) {
        CommandSubstitution substitution = new CommandSubstitution(
                originalCommand,
                abbreviation.split(" ")[0],
                name,
                CMDSubstitutionPlugin.version
        );

        if(modifyConfig) {
            CMDSubstitutionPlugin.config.addToConfig(abbreviation, originalCommand);
        }

        CMDSubstitutionPlugin.registry.registerCommand(substitution);
    }

    @Override
    protected void start() {
//        CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "start command");
    }

    // inefficient should create a map in the future
    public static SubData getCorrectSubData(String abr, CommandContext ctx) {
        List<SubData> dataList = config.getMappings();

        String abr1 = "/" + abr.split(" ")[0];
//        int params = SubData.countParams(abr1);
        boolean commandFound = false;
        int params = abr.stripTrailing().split(" ").length - 1;
        for(SubData data : dataList) {
            String abr2 = data.getSub().split(" ")[0];
            if(abr1.equals(abr2)) {
                commandFound = true;
                if(params == data.getParamCount()) {
                    return data;
                }
            }

        }

        if(!commandFound) {
            Utility.sendError(ctx, "abbreviation " + abr1 + " not found");
        } else if(commandFound) {
            Utility.sendError(ctx, abr1 + " with " + params + " parameters not found");
        }

        return null;
    }
}