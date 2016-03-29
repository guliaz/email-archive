package com.barley.delayQ;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DelayedEvent implements Delayed {

    @JsonProperty
    private String eventDateTime;

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    @JsonIgnore
    @Override
    public long getDelay(TimeUnit unit) {
        Date eventHappened = new Date();
        if (eventHappened != null)
            return new Date().getTime() - eventHappened.getTime();
        else
            return 0;
    }

    @JsonIgnore
    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}
