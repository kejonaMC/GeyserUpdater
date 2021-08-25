package dev.projectg.geyserupdater.bungee;

import dev.projectg.geyserupdater.common.logger.UpdaterLogger;
import dev.projectg.geyserupdater.common.util.ReflectionUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A wrapper class for {@link this#run(UpdaterLogger, Plugin)}.
 * It is wrapped to hide a {@link ScheduledTask} field, which is required for a Task which modifies the Plugin LinkedHashMap at the right moment to cancel itself.
 */
public class PluginMapModifier {

    private ScheduledTask task = null;

    private final String resultMessage = "This may result in errors and inability to apply updates during the shutdown process.";

    /**
     * Place a {@link Plugin} at the top of BungeeCord's LinkedHashMap of plugins. This will result in the plugin starting in the normal order, but being disabled last.
     * This method is meant to be called within {@link Plugin#onEnable()}
     * @param logger The Logger to use for messages
     * @param plugin The Plugin to place at the top
     */
    protected void run(UpdaterLogger logger, Plugin plugin) {
        // https://github.com/SpigotMC/BungeeCord/blob/master/proxy/src/main/java/net/md_5/bungee/BungeeCord.java
        BungeeCord bungeeCord = BungeeCord.getInstance();
        PluginManager pluginManager = bungeeCord.getPluginManager();

        Collection<?> listeners;
        LinkedHashMap<String, Plugin> plugins;
        try {
            listeners = ReflectionUtils.getFieldValue(bungeeCord, Collection.class, "listeners");
            plugins = ReflectionUtils.getFieldValue(pluginManager, LinkedHashMap.class, "plugins");
        } catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
            logger.error("Failed to get necessary private fields to modify BungeeCord's plugin list order");
            logger.error(resultMessage);
            e.printStackTrace();
            return;
        }

        // modify the plugin map once it is no longer being iterated over by BungeeCord to avoid ConcurrentModificationException.
        // onEnable() is called by BungeeCord when iterating over the plugin list, so we must modify it after bungeecord is done enabling ALL plugins
        // the listeners List is populated after all plugin enabling is finished.
        task = bungeeCord.getScheduler().schedule(plugin, () -> {
            if (bungeeCord.isRunning) {
                if (!listeners.isEmpty()) {
                    LinkedHashMap<String, Plugin> sortedPlugins = new LinkedHashMap<>();
                    String updaterName = plugin.getDescription().getName();
                    sortedPlugins.put(updaterName, plugins.get(updaterName)); // put ourselves at the very start, which means we disable last
                    sortedPlugins.putAll(plugins); // put the rest of the plugins in the default order

                    Set<String> originalOrder = null;
                    if (logger.isDebug()) {
                        originalOrder = plugins.keySet();
                    }

                    synchronized (plugins) {
                        plugins.clear();
                        plugins.putAll(sortedPlugins);
                    }

                    logger.info("Successfully modified the order of BungeeCord's plugin Map.");
                    if (logger.isDebug()) {
                        logger.debug("Original order: " + originalOrder);
                        logger.debug("New order: " + plugins.keySet());
                    }
                    task.cancel();
                }
            } else {
                logger.error("BungeeCord began shutdown before we were able to modify the plugin list order!");
                logger.error(resultMessage);
                task.cancel();
            }
        }, 2, 5, TimeUnit.SECONDS);
    }
}
