package net.foulest.fstaff.utils;

import net.foulest.fstaff.FStaff;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;

public class Settings {

    public static File file;
    public static FileConfiguration config;
    public static String storageType;
    public static File storageFile;
    public static String storageFileName;
    public static String host;
    public static String user;
    public static String password;
    public static String database;
    public static int port;
    public static boolean usingMariaDB;
    public static boolean usingSQLite;
    public static int cooldownTime;
    public static String reportMessage;
    public static int reportMenuInvSize;
    public static boolean fillWithGlass;

    public static void setupSettings() {
        file = new File(FStaff.instance.getDataFolder(), "settings.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                MessageUtil.log(Level.WARNING, "Couldn't create the config file.");
                ex.printStackTrace();
                return;
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        config.addDefault("reports.cooldown-time", 30);
        config.addDefault("reports.report-message", "&f[&7Report&f] &a%reporter% &ereported &a%target% &efor &a%reason%");
        config.addDefault("reports.report-menu.inv-size", 27);
        config.addDefault("reports.report-menu.fill-with-glass", true);
        config.addDefault("reports.report-menu.reasons", Collections.<String>emptyList());

        config.addDefault("storage.type", "sqlite");
        config.addDefault("storage.sqlite.file", "sqlite.db");
        config.addDefault("storage.mariadb.host", "host");
        config.addDefault("storage.mariadb.user", "user");
        config.addDefault("storage.mariadb.password", "password");
        config.addDefault("storage.mariadb.database", "database");
        config.addDefault("storage.mariadb.port", 3306);
        config.options().copyDefaults(true);

        try {
            config.save(file);
        } catch (IOException ex) {
            MessageUtil.log(Level.WARNING, "Couldn't save the config file.");
        }
    }

    public static void loadSettings() {
        config = YamlConfiguration.loadConfiguration(file);

        cooldownTime = config.getInt("reports.cooldown-time");
        reportMessage = config.getString("reports.report-message");
        reportMenuInvSize = config.getInt("reports.cooldown-time");
        fillWithGlass = config.getBoolean("reports.report-menu.fill-with-glass");

        storageType = config.getString("storage.type");
        storageFileName = config.getString("storage.sqlite.file");
        storageFile = new File(FStaff.instance.getDataFolder() + "/" + config.getString("storage.sqlite.file"));
        host = config.getString("storage.mariadb.host");
        user = config.getString("storage.mariadb.user");
        password = config.getString("storage.mariadb.password");
        database = config.getString("storage.mariadb.database");
        port = config.getInt("storage.mariadb.port");

        switch (storageType.toLowerCase()) {
            case "mariadb":
                usingMariaDB = true;
                usingSQLite = false;
                break;

            case "sqlite":
                usingMariaDB = false;
                usingSQLite = true;
                break;
        }
    }

    public static void saveSettings() {
        config.set("reports.cooldown-time", cooldownTime);
        config.set("reports.report-message", reportMessage);
        config.set("reports.report-menu.inv-size", reportMenuInvSize);
        config.set("reports.report-menu.fill-with-glass", fillWithGlass);

        config.set("storage.type", storageType);
        config.set("storage.sqlite.file", storageFileName);
        config.set("storage.mariadb.host", host);
        config.set("storage.mariadb.user", user);
        config.set("storage.mariadb.password", password);
        config.set("storage.mariadb.database", database);
        config.set("storage.mariadb.port", port);

        try {
            config.save(file);
        } catch (IOException ex) {
            MessageUtil.log(Level.WARNING, "Couldn't save the config file.");
        }
    }
}
