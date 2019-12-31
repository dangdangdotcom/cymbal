package com.dangdang.cymbal.common.util;

import com.dangdang.cymbal.common.exception.CymbalException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Some common method for thread.
 *
 * @author GeZhen
 */
public class ThreadUtil {

    private static int AWHILE = 1;

    public static void sleep(int seconds) {
        try {
            SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new CymbalException(e);
        }
    }

    public static void sleepAwhile() {
        ThreadUtil.sleep(AWHILE);
    }
}
