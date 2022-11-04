package net.foulest.fstaff.cmds;

import net.foulest.fstaff.reports.ReportManager;
import net.foulest.fstaff.reports.menu.ReasonMenu;
import net.foulest.fstaff.utils.MessageUtil;
import net.foulest.fstaff.utils.Settings;
import net.foulest.fstaff.utils.command.Command;
import net.foulest.fstaff.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Foulest
 * @project FStaff
 */
public class ReportCmd {

    private static final int COOLDOWN_TIME = Settings.config.getInt("cooldown-time");

    @Command(name = "report", description = "Report a player.", usage = "/report <player> [reason]", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player reporter = args.getPlayer();

        if (args.length() == 0) {
            MessageUtil.messagePlayer(reporter, "&cUsage: /report <player> [reason]");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArgs(0));

        // Cancels the report if the target cannot be found.
        if (target == null) {
            MessageUtil.messagePlayer(reporter, "&cPlayer not found.");
            return;
        }

        // Cancels the report if the reporter tries to report themselves.
        if (target == reporter) {
            MessageUtil.messagePlayer(reporter, "&cYou can't report yourself.");
            return;
        }

        // Cancels the report if the reporter is still on cooldown.
        if (ReportManager.getCooldowns().containsKey(reporter.getUniqueId())) {
            long secondsLeft = ((ReportManager.getCooldowns().get(reporter.getUniqueId()) / 1000) + COOLDOWN_TIME) - (System.currentTimeMillis() / 1000);

            if (secondsLeft > 0) {
                MessageUtil.messagePlayer(reporter, "&cYou are still on cooldown for %time% seconds.".replace("%time%",
                        String.valueOf(BigDecimal.valueOf((double) secondsLeft)
                                .setScale(1, RoundingMode.HALF_UP).doubleValue())));
                return;
            }
        }

        // Builds the report reason.
        StringBuilder reason = new StringBuilder();
        if (args.length() >= 2) {
            for (int i = 1; i < args.length(); i++) {
                reason.append(args.getArgs(i)).append(" ");
            }
        }

        // Cancels the report if the report reason is too big.
        if (reason.length() > 32) {
            MessageUtil.messagePlayer(reporter, "&cYour report reason is too long.");
            return;
        }

        // Handles the report.
        if (reason.length() == 0) {
            new ReasonMenu(reporter, target.getName());
        } else {
            ReportManager.createReport(reason.toString(), target.getName(), reporter.getName());
            ReportManager.getCooldowns().put(reporter.getUniqueId(), System.currentTimeMillis());
            MessageUtil.messagePlayer(reporter, "&aYour report has been submitted.");
        }
    }
}
