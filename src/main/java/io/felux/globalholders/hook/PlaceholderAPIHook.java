package io.felux.globalholders.hook;

import io.felux.globalholders.Globalholders;
import io.felux.globalholders.api.PlayerCache;
import io.felux.globalholders.util.RegexUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private Globalholders plugin;

    public PlaceholderAPIHook(final Globalholders plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }


    public String onRequest(final OfflinePlayer player, final String identifier) {
        System.out.println("identifier = " + identifier);

        String serverId = RegexUtil.extract(identifier, "\\(.*?\\)");

        if (serverId == null) return "Unknown Server";

        String placeholder = identifier.replace(serverId + "_", "");

        String serverIdRaw = RegexUtil.extract(serverId, "([a-zA-Z_]+)");

//        plugin.getLogger().info("[SERVER] " + serverIdRaw);
//        plugin.getLogger().info("[PLACEHOLDER] " + placeholder);


        if (Bukkit.getPlayer(player.getUniqueId()) != null)
            plugin.getBungeeManager().requestPlaceholder(Bukkit.getPlayer(player.getUniqueId()), serverIdRaw, "RequestPlaceholder", placeholder);

        String placeholderResponse = PlayerCache.getPlayer(player.getUniqueId()).getPlaceholderFromCache(serverIdRaw, "%" + placeholder + "%");

        if(placeholderResponse == null) return "Unknown Placeholder";

        if (!placeholderResponse.isEmpty()) {
            return placeholderResponse;
        }

        return " ";
    }
}
