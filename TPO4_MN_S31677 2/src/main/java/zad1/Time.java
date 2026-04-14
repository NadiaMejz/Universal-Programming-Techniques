/**
 *
 *  @author Mejza Nadia S31677
 *
 */

package zad1;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Time {
    public static String getResult(String dateStr1, String dateStr2) {
        try {
            if (dateStr1.contains("T") || dateStr2.contains("T")) {
                return handleDateTime(dateStr1, dateStr2);
            } else {
                return handleDate(dateStr1, dateStr2);
            }
        } catch (Exception e) {
            return "Invalid date format";
        }
    }

    private static String handleDate(String dateStr1, String dateStr2) {
        LocalDate start = LocalDate.parse(dateStr1);
        LocalDate end = LocalDate.parse(dateStr2);
        Period period = calculatePeriod(start, end);
        long days = ChronoUnit.DAYS.between(start, end);

        return formatResult(start, end, period, days, -1, -1);
    }

    private static String handleDateTime(String dateStr1, String dateStr2) {
        LocalDateTime startDateTime = LocalDateTime.parse(dateStr1, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDateTime = LocalDateTime.parse(dateStr2, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Duration duration = Duration.between(startDateTime, endDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        Period period = calculatePeriod(startDate, endDate);
        long days = ChronoUnit.DAYS.between(startDate, endDate);

        return formatResult(startDate, endDate, period, days, hours, minutes);
    }

    private static Period calculatePeriod(LocalDate start, LocalDate end) {
        int years = 0;
        LocalDate temp = start;
        while (temp.plusYears(1).isBefore(end) || temp.plusYears(1).isEqual(end)) {
            years++;
            temp = temp.plusYears(1);
        }

        int months = 0;
        while (temp.plusMonths(1).isBefore(end) || temp.plusMonths(1).isEqual(end)) {
            months++;
            temp = temp.plusMonths(1);
        }

        int days = (int) ChronoUnit.DAYS.between(temp, end);
        return Period.of(years, months, days);
    }

    private static String formatResult(LocalDate start, LocalDate end, Period period, long days, long hours, long minutes) {
        String startDateStr = formatDate(start);
        String endDateStr = formatDate(end);
        String daysWeeks = String.format(" - mija: %d dni, tygodni %.2f", days, days / 7.0);
        String calendar = String.format(" - kalendarzowo: %s", formatPeriod(period));

        if (hours != -1) {
            return String.format("Od %s godz. %s do %s godz. %s\n%s\n - godzin: %d, minut: %d\n%s",
                    startDateStr, formatTime(start), endDateStr, formatTime(end), daysWeeks, hours, minutes * 60, calendar);
        } else {
            return String.format("Od %s do %s\n%s\n%s", startDateStr, endDateStr, daysWeeks, calendar);
        }
    }

    private static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", new Locale("pl")));
    }

    private static String formatTime(LocalDate date) {
        return date.atStartOfDay().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static String formatPeriod(Period period) {
        List<String> parts = new ArrayList<>();
        if (period.getYears() > 0) parts.add(period.getYears() + " " + pluralize(period.getYears(), "rok", "lata", "lat"));
        if (period.getMonths() > 0) parts.add(period.getMonths() + " " + pluralize(period.getMonths(), "miesiąc", "miesiące", "miesięcy"));
        if (period.getDays() > 0) parts.add(period.getDays() + " " + pluralize(period.getDays(), "dzień", "dni", "dni"));
        return String.join(", ", parts);
    }

    private static String pluralize(int n, String singular, String plural, String genitive) {
        if (n == 1) return singular;
        if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) return plural;
        return genitive;
    }
}