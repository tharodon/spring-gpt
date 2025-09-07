package com.example.springgpt.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        log.info("Call tool: getCurrentDateTime()");
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    @Tool(description = "Set a user alarm for the given time")
    public void setAlarm(
            @ToolParam(description = "Time in ISO-8601 format") LocalDateTime time) {
        try {
            log.info("Call tool: setAlarm({})", time);
            System.out.println("Alarm set for " + time);
        } catch (Exception e) {
            log.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
