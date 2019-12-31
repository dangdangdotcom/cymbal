package com.dangdang.cymbal.service.util;

import com.dangdang.cymbal.service.constant.Constant;
import com.google.common.base.Strings;
import org.apache.commons.lang.RandomStringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common util for redis.
 *
 * @auther GeZhen
 */
public class RedisUtil {

    public static String getRedisPassword(final String password) {
        if (Strings.isNullOrEmpty(password)) {
            return Constant.Strings.EMPTY_PASSWORD;
        }
        return password;
    }

    public static Map<String, Map<String, String>> parseRedisInfo(final List<String> redisInfos) {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        String infoClass = null;
        if (redisInfos != null) {
            for (String each : redisInfos) {
                if (each.startsWith("#")) {
                    infoClass = each.substring(2).trim();
                    result.put(infoClass, new HashMap<String, String>());
                } else {
                    // split info by ':', and put them into result map
                    int splitIndex = each.indexOf(":");
                    result.get(infoClass).put(each.substring(0, splitIndex), each.substring(splitIndex + 1));
                }
            }
        }
        return result;
    }

    public static String generateClusterId() {
        return RandomStringUtils.random(Constant.Redis.CLUSTER_ID_LENGTH, true, true);
    }

    public static String getSlaveOf(final String ip, final Integer port) {
        return String.format("%s:%d", ip, port);
    }
}
