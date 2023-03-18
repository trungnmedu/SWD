package com.swd.backend.utils;

import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Lazy(value = true)
@Getter
public class TimeMappingHelper {
    private final HashMap<String, Integer> timeMapping = new HashMap<>() {{
        put("00:00", 0);
        put("00:30", 30);
        put("01:00", 60);
        put("01:30", 90);
        put("02:00", 120);
        put("02:30", 150);
        put("03:00", 180);
        put("03:30", 210);
        put("04:00", 240);
        put("04:30", 270);
        put("05:00", 300);
        put("05:30", 330);
        put("06:00", 360);
        put("06:30", 390);
        put("07:00", 420);
        put("07:30", 450);
        put("08:00", 480);
        put("08:30", 510);
        put("09:00", 540);
        put("09:30", 570);
        put("10:00", 600);
        put("10:30", 630);
        put("11:00", 660);
        put("11:30", 690);
        put("12:00", 720);
        put("12:30", 750);
        put("13:00", 780);
        put("13:30", 810);
        put("14:00", 840);
        put("14:30", 870);
        put("15:00", 900);
        put("15:30", 930);
        put("16:00", 960);
        put("16:30", 990);
        put("17:00", 1020);
        put("17:30", 1050);
        put("18:00", 1080);
        put("18:30", 1110);
        put("19:00", 1140);
        put("19:30", 1170);
        put("20:00", 1200);
        put("20:30", 1230);
        put("21:00", 1260);
        put("21:30", 1290);
        put("22:00", 1320);
        put("22:30", 1350);
        put("23:00", 1380);
        put("23:30", 1410);
    }};
}
