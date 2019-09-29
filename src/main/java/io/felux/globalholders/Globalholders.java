package io.felux.globalholders;

import io.felux.globalholders.hook.PlaceholderAPIHook;
import io.felux.globalholders.util.BungeeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Globalholders extends JavaPlugin {
//
//    private StorageHandler storageHandler;
//    private ShopManager shopManager;
//    private CommandManager commandManager;
//
//    public StorageHandler getStorageHandler() {
//        return storageHandler;
//    }
//
//    public ShopManager getShopManager() {
//        return shopManager;
//    }

    private BungeeManager bungeeManager;

    public BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    @Override
    public void onEnable() {
        this.bungeeManager = new BungeeManager(this);
        hookPlaceholderAPI();
    }

    private void hookPlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }
    }
//
//    @Override
//    public void onEnable() {
//        long start = System.currentTimeMillis();
//        saveDefaultConfig();
//        getBanner();
//
//        Common.loading("libraries");
//        LibraryLoader.loadAll(Credits.class);
//
//        Common.loading("events");
//        new JoinListener(this);
//
//        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
//        mongoLogger.setLevel(Level.SEVERE);
//        org.apache.logging.log4j.core.Logger logger;
//        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
//        logger.addFilter(new ConsoleFilter());
//        handleReload();
//
//        Common.loading("commands");
//        registerCommands();
//
//        Common.loading("shops");
//        setupShop();
//
//        Common.loading("hooks");
//        hookPlaceholderAPI();
//
//        Common.sendConsoleMessage(" ");
//        getLogger().info("Successfully enabled in " + (System.currentTimeMillis() - start) + "ms.");
//    }
//
//    private void getBanner() {
//        Common.sendConsoleMessage("&b ");
//        Common.sendConsoleMessage("&b   _____");
//        Common.sendConsoleMessage("&b  / ___/");
//        Common.sendConsoleMessage("&b / /__  " + "  &7" + getDescription().getName() + " v" + getDescription().getVersion());
//        Common.sendConsoleMessage("&b \\___/  " + "  &7Running on Bukkit - " + getServer().getName());
//        Common.sendConsoleMessage("&b ");
//        Common.sendMessage("Created by Felux.io Development.");
//        Common.sendConsoleMessage("&b ");
//    }
//
//    private void hookPlaceholderAPI() {
//        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
//            new PlaceholderAPIHook(this).register();
//            Common.loading("PlaceholderAPI hook");
//        }
//    }
//
//    @Override
//    public void onDisable() {
//        PlayerData.users.forEach(((uuid, playerData) -> getStorageHandler().pushData(uuid)));
//    }
//
//    private void setupStorage() {
//        String storageType = Objects.requireNonNull(getConfig().getString("settings.storage.type", "SQLITE")).toUpperCase();
//
//        if (!Arrays.asList("SQLITE", "MYSQL", "MONGODB").contains(storageType)) {
//            storageType = "SQLITE";
//        }
//
//        Common.loading(storageType.toLowerCase() + " storage");
//
//        switch (storageType) {
//            case "SQLITE":
//                LibraryLoader.loadAll(SQLiteHandler.class);
//                storageHandler = new SQLiteHandler(getDataFolder().getPath());
//                break;
//            case "MYSQL":
//                LibraryLoader.loadAll(MySQLHandler.class);
//                storageHandler = new MySQLHandler(
//                        getConfig().getString("settings.storage.prefix", ""),
//                        getConfig().getString("settings.storage.host", "localhost"),
//                        getConfig().getInt("settings.storage.port", 3306),
//                        getConfig().getString("settings.storage.database", "credits"),
//                        getConfig().getString("settings.storage.username", "root"),
//                        getConfig().getString("settings.storage.password", "qwerty123"));
//                break;
//            case "MONGODB":
//                LibraryLoader.loadAll(MongoDBHandler.class);
//                storageHandler = new MongoDBHandler(
//                        getConfig().getString("settings.storage.prefix", ""),
//                        getConfig().getString("settings.storage.host", "localhost"),
//                        getConfig().getInt("settings.storage.port", 27017),
//                        getConfig().getString("settings.storage.database", "credits"),
//                        getConfig().getString("settings.storage.username", ""),
//                        getConfig().getString("settings.storage.password", "")
//                );
//                break;
//        }
//    }
//
//    private void setupShop() {
//        this.shopManager = new ShopManager();
//
//        for (String itemId : getConfig().getConfigurationSection("items").getKeys(false)) {
//            String path = "items." + itemId;
//
//            int price = getConfig().getInt(path + ".cost", 1);
//
//            int limit = getConfig().getInt(path + ".limit", -1);
//
//            String itemName = getConfig().getString(path + ".item.name");
//            List<String> itemLore = getConfig().getStringList(path + ".item.lore");
//            itemLore.replaceAll(lore -> lore.replace("{cost}", price + ""));
//
//            List<String> itemActions = getConfig().getStringList(path + ".actions");
//
//            String material = getConfig().getString(path + ".item.material");
//
//            try {
//                shopManager.addItem(new ShopItem(itemId, price, limit, itemActions, new ItemBuilder(material != null ? material : "BARRIER").withName(itemName != null ? itemName : "&e" + itemId).withLore(itemLore).getItem()));
//            } catch (InvalidMaterialException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public CommandManager getCommandManager() {
//        return commandManager;
//    }
//
//    private void registerCommands() {
//        commandManager = new CommandManager(this);
//        try {
//            getCommand("credits").setExecutor(new CommandExecutor(this));
//            if (getCommand("credits").getPlugin() != this) {
//                getLogger().warning("/credits command is being handled by plugin other than " + getDescription().getName() + ". You must use /credits:credits instead.");
//            }
//        } catch (NullPointerException e) {
//            getLogger().warning("The /credits command wasn't found in the plugin.yml file.");
//        }
//    }
//
//    public void handleReload() {
//        reloadConfig();
//        Common.loading("config");
//        Lang.init(this);
//        setupStorage();
//    }
}

