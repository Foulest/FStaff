package net.foulest.fstaff.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * @author Foulest
 * @project FStaff
 */
@Getter
@AllArgsConstructor
public class StaffModeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID UUID;
    private final boolean inStaffMode;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
