package io.felux.globalholders.api;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class PlayerCache {

    private UUID player;
    private Map<String, Map<String, String>> placeholderCache;

    private static Map<UUID, PlayerCache> playerCache = Maps.newHashMap();

    public static PlayerCache getPlayer(UUID uuid) {
        if (playerCache.containsKey(uuid)) {
            return playerCache.get(uuid);
        }
        return new PlayerCache(uuid);
    }

    public PlayerCache(UUID player) {
        this.player = player;
        placeholderCache = Maps.newHashMap();
        playerCache.put(player, this);
    }

    public UUID getPlayer() {
        return player;
    }

    public Map<String, Map<String, String>> getPlaceholderCache() {
        return placeholderCache;
    }

    public String getPlaceholderFromCache(String serverId, String placeholder) {
        if(placeholderCache.get(serverId) != null) {
            return placeholderCache.get(serverId).get(placeholder);
        }

        return "";
    }
}
