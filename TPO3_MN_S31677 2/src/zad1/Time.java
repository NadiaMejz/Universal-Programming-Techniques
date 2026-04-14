/**
 *
 *  @author Mejza Nadia S31677
 *
 */


package zad1;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

public class Time {
    public static String passed(String from, String to) {
        try {
            boolean fromHasTime = from.contains("T");
            boolean toHasTime = to.contains("T");

            ZonedDateTime fromZdt = parseDate(from);
            ZonedDateTime toZdt = parseDate(to);

            String formattedFrom = formatDate(fromZdt, fromHasTime);
            String formattedTo = formatDate(toZdt, toHasTime);

            long days = ChronoUnit.DAYS.between(fromZdt.toLocalDate(), toZdt.toLocalDate());
            double weeks = days / 7.0;
            String weeksFormatted = (weeks == (long) weeks) ? String.format("%d", (long) weeks) : String.format("%.2f", weeks);
            String daysStr = days == 1 ? "1 dzień" : days + " dni";

            List<String> lines = new ArrayList<>();
            lines.add("Od " + formattedFrom + " do " + formattedTo);
            lines.add("- mija: " + daysStr + ", tygodni " + weeksFormatted);

            if (fromHasTime || toHasTime) {
                Duration duration = Duration.between(fromZdt, toZdt);
                long totalMinutes = duration.toMinutes();
                long hours = totalMinutes / 60;
                lines.add("- godzin: " + hours + ", minut: " + totalMinutes);
            }

            if (days >= 1) {
                Period period = Period.between(fromZdt.toLocalDate(), toZdt.toLocalDate());
                List<String> periodParts = new ArrayList<>();
                if (period.getYears() > 0) periodParts.add(formatYears(period.getYears()));
                if (period.getMonths() > 0) periodParts.add(formatMonths(period.getMonths()));
                if (period.getDays() > 0) periodParts.add(formatDays(period.getDays()));
                if (!periodParts.isEmpty()) lines.add("- kalendarzowo: " + String.join(", ", periodParts));
            }

            return String.join("\n", lines);
        } catch (DateTimeParseException e) {
            return "*** " + e;
        }
    }

    private static ZonedDateTime parseDate(String dateStr) throws DateTimeParseException {
        if (dateStr.contains("T")) {
            LocalDateTime ldt = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return ldt.atZone(ZoneId.of("Europe/Warsaw"));
        } else {
            LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return ld.atStartOfDay(ZoneId.of("Europe/Warsaw"));
        }
    }

    private static String formatDate(ZonedDateTime zdt, boolean hasTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", new Locale("pl", "PL"));
        String datePart = zdt.format(dateFormatter);
        return hasTime ? datePart + " godz. " + zdt.format(DateTimeFormatter.ofPattern("HH:mm")) : datePart;
    }

    private static String formatYears(int years) {
        if (years == 1) return "1 rok";
        int mod100 = years % 100;
        int mod10 = years % 10;
        if ((mod10 >= 2 && mod10 <= 4) && !(mod100 >= 12 && mod100 <= 14)) return years + " lata";
        return years + " lat";
    }

    private static String formatMonths(int months) {
        if (months == 1) return "1 miesiąc";
        int mod100 = months % 100;
        int mod10 = months % 10;
        if ((mod10 >= 2 && mod10 <= 4) && !(mod100 >= 12 && mod100 <= 14)) return months + " miesiące";
        return months + " miesięcy";
    }

    private static String formatDays(int days) {
        return days == 1 ? "1 dzień" : days + " dni";
    }
}