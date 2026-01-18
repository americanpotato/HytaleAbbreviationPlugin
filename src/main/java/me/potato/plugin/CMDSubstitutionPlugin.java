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
import java.util.List;

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

        this.getEventRegistry().registerGlobal(PlayerSetupConnectEvent.class, CMDSubstitutionPlugin::onConnect);

        for(List<String> l : config.getMappings()) {
            this.getCommandRegistry().registerCommand(new CommandSubstitution(l.get(1), l.get(0), this.getName(), this.getManifest().getVersion().toString()));
        }

        this.getCommandRegistry().registerCommand(new AddAbbreviationCommand(this.getName(), this.getManifest().getVersion().toString()));
    }

    public static void onConnect(PlayerSetupConnectEvent event) {
        java.util.UUID uuid = event.getUuid();
        PermissionsModule.get().addUserToGroup(uuid, "abbreviations");
    }

}