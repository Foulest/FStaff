package net.foulest.fstaff.reports;

import lombok.Getter;
import net.foulest.fstaff.events.ReportEvent;
import net.foulest.fstaff.utils.DatabaseUtil;
import net.foulest.fstaff.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Foulest
 * @project FStaff
 */
public class ReportManager {

    @Getter
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    @Getter
    private static final List<Report> reports = new ArrayList<>();

    public static void createReport(String reason, String targetName, String reporterName) {
        if (isAlreadyReported(targetName, reporterName)) {
            deleteReport(targetName, reporterName, "Console");
        }

        Report report = new Report(UUID.randomUUID().toString(), reason, targetName, reporterName, System.currentTimeMillis());
        Bukkit.getPluginManager().callEvent(new ReportEvent(report));

        reports.add(report);

        DatabaseUtil.update("INSERT INTO `Reports` (uuid, reason, targetName, reporterName, timestamp)" +
                " VALUES ('" + report.getUUID() + "', '" + report.getReason() + "', '" + report.getTargetName()
                + "', '" + report.getReporterName() + "', " + report.getTimestamp() + ")");
    }

    public static void deleteReport(String targetName, String reporterName, String deleterName) {
        List<Report> reportsToRemove = new ArrayList<>();

        for (Report report : reports) {
            if (report.getTargetName().equals(ChatColor.stripColor(targetName))
                    && report.getReporterName().equals(ChatColor.stripColor(reporterName))) {
                DatabaseUtil.update("DELETE FROM `Reports` WHERE uuid='" + report.getUUID() + "'");
                reportsToRemove.add(report);
            }
        }

        if (!reportsToRemove.isEmpty()) {
            for (Report report : reportsToRemove) {
                MessageUtil.messagePlayer(Bukkit.getConsoleSender(), "&7[&fReport&7] &a" + report.getTargetName()
                        + "&e's report has been deleted by &a" + deleterName + "&e.");
                reports.remove(report);
            }
        }
    }

    public static boolean isAlreadyReported(String targetName, String reporterName) {
        for (Report report : reports) {
            if (report.getTargetName().equalsIgnoreCase(targetName)
                    && report.getReporterName().equalsIgnoreCase(reporterName)) {
                return true;
            }
        }

        return false;
    }

    public static void loadReports() {
        ResultSet result;

        try (Connection connection = DatabaseUtil.hikari.getConnection();
             PreparedStatement select = connection.prepareStatement("SELECT * FROM Reports")) {
            result = select.executeQuery();

            while (result.next()) {
                reports.add(new Report(result.getString("uuid"),
                        result.getString("reason"),
                        result.getString("targetName"),
                        result.getString("reporterName"),
                        result.getLong("timestamp")));
            }

        } catch (SQLException ignored) {
        }
    }
}
