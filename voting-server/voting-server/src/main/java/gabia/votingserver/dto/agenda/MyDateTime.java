package gabia.votingserver.dto.agenda;

import java.time.LocalDateTime;

public class MyDateTime {

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
}
