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
            return timeAgoSeconds(duration);
        } else if (duration.toHours() < 1) {
            return timeAgoMinutes(duration);
        } else if (duration.toDays() < 1) {
            return timeAgoHours(duration);
        } else {
            return timeAgoDays(duration);
        }
    }

    private static String timeAgoSeconds(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds == 1) {
            return "1 second ago";
        } else {
            return String.format("%d seconds ago", seconds);
        }
    }

    private static String timeAgoMinutes(Duration duration) {
        long minutes = duration.toMinutes();
        if (minutes == 1) {
            return "1 minute ago";
        } else {
            return String.format("%d minutes ago", minutes);
        }
    }

    private static String timeAgoHours(Duration duration) {
        long hours = duration.toHours();
        if (hours == 1) {
            return "1 hour ago";
        } else {
            return String.format("%d hours ago", hours);
        }
    }

    private static String timeAgoDays(Duration duration) {
        long days = duration.toDays();
        if (days == 1) {
            return "1 day ago";
        } else {
            return String.format("%d days ago", days);
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
