package net.foulest.fstaff.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Foulest
 * @project FStaff
 */
@Getter
@AllArgsConstructor
public class Report {

    private final String UUID;
    private final String reason;
    private final String targetName;
    private final String reporterName;
    private final long timestamp;
}
