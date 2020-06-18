package io.github.yzernik.squeakand;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtil {

    public static String timeAgo(Date date) {
        LocalDateTime initialTime = toLocalDateTime(date);
        LocalDateTime finalTime = LocalDateTime.now();

        Duration duration = getDuration(initialTime, finalTime);

        if (duration.toMinutes() < 1) {
            return String.format("%d seconds ago", duration.getSeconds());
        } else if (duration.toHours() < 1) {
            return String.format("%d minutes ago", duration.toMinutes());
        } else if (duration.toDays() < 1) {
            return String.format("%d hours ago", duration.toHours());
        } else {
            return String.format("%d days ago", duration.toDays());
        }
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private static Duration getDuration(LocalDateTime initialDate, LocalDateTime finalDate) {
        return Duration.between(initialDate, finalDate);
    }

}
