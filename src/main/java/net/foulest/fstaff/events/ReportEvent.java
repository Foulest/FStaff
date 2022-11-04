package net.foulest.fstaff.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.foulest.fstaff.reports.Report;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Foulest
 * @project FStaff
 */
@Getter
@AllArgsConstructor
public class ReportEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Report report;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
