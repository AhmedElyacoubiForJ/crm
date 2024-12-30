package edu.yacoubi.crm.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class TestAppender extends AppenderBase<ILoggingEvent> {
    private final List<ILoggingEvent> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
        events.add(event);
    }

    public boolean contains(String message, String level) {
        return events.stream()
                .anyMatch(event -> event.getFormattedMessage().contains(message) && event.getLevel().toString().equals(level));
    }

    @Override
    public String toString() {
        return "TestAppender{" + "events=" + events + '}';
    }
}

