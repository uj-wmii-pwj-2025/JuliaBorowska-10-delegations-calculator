package uj.wmii.pwj.delegations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Calc {
    private final String pattern = "yyyy-MM-dd HH:mm z";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    private final BigDecimal secondsPerHour = BigDecimal.valueOf(60*60);
    private final BigDecimal hoursPerDay = BigDecimal.valueOf(24);
    private final static BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);

    BigDecimal calculate(String name, String start, String end, BigDecimal dailyRate) throws IllegalArgumentException {
        if (dailyRate.compareTo(BigDecimal.ZERO)<=0) {
            throw new IllegalArgumentException("ERROR: dailyRate must by grater than zero.");
        }

        ZonedDateTime startTime, endTime;
        try {
            startTime = ZonedDateTime.parse(start, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("ERROR: illegal startTime format.");
        }
        try {
            endTime = ZonedDateTime.parse(end, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("ERROR: illegal endTime format.");
        }

        if (startTime.isEqual(endTime) || startTime.isAfter(endTime)) {
            return zero;
        }

        Duration duration = Duration.between(startTime, endTime);
        BigDecimal durationSeconds = BigDecimal.valueOf(duration.getSeconds());

        BigDecimal hours = durationSeconds.divide(secondsPerHour, 0, RoundingMode.UP);
        BigDecimal fullDays = hours.divide(hoursPerDay, 0, RoundingMode.DOWN);
        BigDecimal fullDaysWage = fullDays.multiply(dailyRate);

        BigDecimal remainingHours = hours.remainder(hoursPerDay);
        BigDecimal remainingHoursWage = calculateRemaining(remainingHours, dailyRate);

        return fullDaysWage.add(remainingHoursWage);
    }

    private static BigDecimal calculateRemaining (BigDecimal hours, BigDecimal dailyRate) {
        if (hours.compareTo(BigDecimal.ZERO) == 0) {
            return zero;
        }

        BigDecimal eight = new BigDecimal("8");
        if (hours.compareTo(eight)<=0) {
            return dailyRate.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        }

        BigDecimal twelve = new BigDecimal("12");
        if (hours.compareTo(twelve)<=0) {
            return dailyRate.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        }

        return dailyRate;
    }
}
