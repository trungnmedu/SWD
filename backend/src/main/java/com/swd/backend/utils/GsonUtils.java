package com.swd.backend.utils;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GsonUtils {
    @Bean
    public Gson getGson() {
        return new Gson();
    }
}
