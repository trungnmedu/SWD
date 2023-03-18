package com.swd.backend.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateHelper {
    public final static String VIETNAM_ZONE = "Asia/Ho_Chi_Minh";

    public static Timestamp getTimestampAtZone(String zone) {
        ZoneId zoneId = ZoneId.of(zone);
        LocalDateTime localDateTime = LocalDateTime.now(zoneId);
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp plusMinutes(Timestamp timestamp, long plusTime) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        localDateTime = localDateTime.plusMinutes(plusTime);
        return Timestamp.valueOf(localDateTime);
    }


    public static Timestamp parseFromStringToTimestamp(String input) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        Date dateParsed;
        Timestamp timestamp;
        try {
            dateParsed = format.parse(input);
            timestamp = Timestamp.from(dateParsed.toInstant());
        } catch (Exception ex) {
            return null;
        }
        return timestamp;
    }

    public static Date parseFromStringToDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        Date dateParsed;
        try {
            dateParsed = format.parse(input);
        } catch (Exception ex) {
            return null;
        }
        return dateParsed;
    }

    public static boolean isToday(Date requestDate) {
        Timestamp today = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        String todayFormatted = format.format(today);
        String dateFormatted = format.format(requestDate);
        return dateFormatted.equals(todayFormatted);
    }

    public static boolean isToday(Timestamp timestamp) {
        LocalDate now = LocalDate.now(ZoneId.of(VIETNAM_ZONE));
        LocalDate localDatefromTimestamp = parseFromTimestampToLocalDate(timestamp);
        return now.compareTo(localDatefromTimestamp) == 0;
    }

    public static boolean isToday(LocalDate date) {
        LocalDate today = LocalDate.now();
        return today.compareTo(date) == 0;
    }

    public static LocalTime getLocalTimeFromTimeStamp(Timestamp timestamp) {
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String formattedLocalTime = format.format(date);
        return LocalTime.parse(formattedLocalTime);
    }

    public static Timestamp parseTimestampAtZone(String dateInput, String zone) {
        String time = LocalTime.now(ZoneId.of(zone)).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String dateTime = dateInput + " " + time;
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.from(formatDateTime.parse(dateTime));
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp parseTimestampNonTimeAtZone(String dateInput) {
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.from(formatDateTime.parse(dateInput + " 00:00:00"));
        return Timestamp.valueOf(localDateTime);
    }


    public static LocalDate parseFromTimestampToLocalDate(Timestamp timestamp) {
        return LocalDate.ofInstant(timestamp.toInstant(), ZoneId.of(VIETNAM_ZONE));
    }
}
