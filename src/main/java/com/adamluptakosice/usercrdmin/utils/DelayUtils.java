package com.adamluptakosice.usercrdmin.utils;

import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class DelayUtils {
    private static final Logger LOGGER = Logger.getLogger(DelayUtils.class);

    private DelayUtils() {
    }

    public static void sleep(int timeout) {

        final String threadName = Thread.currentThread().getName();
        final Long threadId = Thread.currentThread().getId();
        LOGGER.debug("Starting sleep for: %d Seconds, current Thread Name: %s and Id: %s".formatted(timeout, threadName, threadId));

        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.debug("Ending sleep for: %d Seconds, current Thread Name: %s and Id: %s".formatted(timeout, threadName, threadId));
    }

}
