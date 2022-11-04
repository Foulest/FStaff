package net.foulest.fstaff;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import net.foulest.fstaff.cmds.*;
import net.foulest.fstaff.listeners.ReportListener;
import net.foulest.fstaff.listeners.StaffModeListener;
import net.foulest.fstaff.listeners.VanishListener;
import net.foulest.fstaff.reports.ReportManager;
import net.foulest.fstaff.utils.DatabaseUtil;
import net.foulest.fstaff.utils.MessageUtil;
import net.foulest.fstaff.utils.Settings;
import net.foulest.fstaff.utils.command.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Foulest
 * @project FStaff
 */
@Getter
public class FStaff extends JavaPlugin {

    public static FStaff instance;
    public static boolean loaded = false;
    private CommandFramework framework;

    @Override
    @SneakyThrows
    public void onEnable() {
        instance = this;
        framework = new CommandFramework(this);

        // Creates the default config.
        Bukkit.getLogger().info("[FStaff] - Loading Settings...");
        Settings.setupSettings();
        Settings.loadSettings();

        // Sets up the Hikari instance.
        Bukkit.getLogger().info("[FStaff] - Loading Database...");
        loadDatabase();

        // Loads the plugin's listeners.
        Bukkit.getLogger().info("[FStaff] - Loading Listeners...");
        loadListeners(new ReportListener(), new StaffModeListener(), new VanishListener());

        // Loads the plugin's commands.
        Bukkit.getLogger().info("[FStaff] - Loading Commands...");
        loadCommands(new MainCmd(), new ReportCmd(), new ReportMenuCmd(), new StaffModeCmd(), new VanishCmd());

        // Loads the plugin's reports.
        Bukkit.getLogger().info("[FStaff] - Loading Reports...");
        ReportManager.loadReports();

        Bukkit.getLogger().info("[FStaff] Loaded successfully.");
    }

    @Override
    public void onDisable() {
        // Saves the settings.
        Bukkit.getLogger().info("[FStaff] - Saving Settings...");
        Settings.saveSettings();

        // Closes the MySQL connection.
        Bukkit.getLogger().info("[FStaff] - Saving Database...");
        if (DatabaseUtil.hikari != null) {
            DatabaseUtil.hikari.close();
        }

        Bukkit.getLogger().info("[FStaff] Shut down successfully.");
    }

    /**
     * Loads the plugin's databases.
     */
    private void loadDatabase() {
        DatabaseUtil.hikari = new HikariDataSource();

        // Sets up the MariaDB database.
        if (Settings.usingMariaDB) {
            DatabaseUtil.hikari.setPoolName("MariaDBConnectionPool");
            DatabaseUtil.hikari.setJdbcUrl("jdbc:mariadb://" + Settings.host + ":" + Settings.port + "/" + Settings.database);
            DatabaseUtil.hikari.setDriverClassName("org.mariadb.jdbc.Driver");
            DatabaseUtil.hikari.addDataSourceProperty("user", Settings.user);
            DatabaseUtil.hikari.addDataSourceProperty("password", Settings.password);
            DatabaseUtil.hikari.addDataSourceProperty("characterEncoding", "utf8");
            DatabaseUtil.hikari.addDataSourceProperty("useUnicode", "true");

        } else {
            // Creates the SQLite database file.
            if (!Settings.storageFile.exists()) {
                try {
                    Settings.storageFile.createNewFile();
                } catch (IOException ex) {
                    MessageUtil.log(Level.WARNING, "Failed to create SQLite database. Error: " + ex.getMessage());
                }
            }

            // Sets up the SQLite database.
            DatabaseUtil.hikari.setPoolName("SQLiteConnectionPool");
            DatabaseUtil.hikari.setJdbcUrl("jdbc:sqlite:" + Settings.storageFile);
            DatabaseUtil.hikari.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        }

        // Sets up other connection flags.
        DatabaseUtil.hikari.setConnectionTestQuery("SELECT 1;");

        // Creates missing tables in the database.
        DatabaseUtil.update("CREATE TABLE IF NOT EXISTS Reports (uuid VARCHAR(255) UNIQUE, " +
                "reason VARCHAR(255) NOT NULL default '', " +
                "targetName VARCHAR(255) NOT NULL default '', " +
                "reporterName VARCHAR(255) NOT NULL default '', " +
                "timestamp BIGINT)");
    }

    /**
     * Loads the plugin's listeners.
     *
     * @param listeners Listener to load.
     */
    private void loadListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Loads the plugin's commands.
     *
     * @param commands Command to load.
     */
    private void loadCommands(Object... commands) {
        for (Object command : commands) {
            framework.registerCommands(command);
        }
    }
}
