package com.dangdang.cymbal.common.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util method for collections, suck as get first element of a list.
 *
 * @auther GeZhen
 */
public class CollectionUtil {

    public static <T> T getFirst(List<T> list) {
        Preconditions.checkNotNull(list);
        Preconditions.checkArgument(!list.isEmpty());
        return list.get(0);
    }

    public static <T> Map<T, T> toMap(List<T> list) {
        Preconditions.checkNotNull(list);
        Preconditions.checkArgument(list.size() % 2 == 0, "Size of list must be even.");
        Map<T, T> result = new HashMap<>();
        for (int i = 0; i < list.size(); i += 2) {
            result.put(list.get(i), list.get(i + 1));
        }
        return result;
    }

    public static Map<String, String> splitByColonAndToMap(List<String> list) {
        Preconditions.checkNotNull(list);
        Map<String, String> result = new HashMap<>();
        for (String each : list) {
            if (!Strings.isNullOrEmpty(each)) {
                String[] keyAndValue = each.split(":");
                result.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return result;
    }
}
