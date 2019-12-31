package com.dangdang.cymbal.common.util;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Some common method for I/O stream.
 *
 * @auther GeZhen
 */
public class StreamUtil {

    /**
     * Read Strings to list from InputStream.
     *
     * @param inputStream stream to read
     * @return Strings from stream
     */
    public static List<String> toList(final InputStream inputStream) throws IOException {
        return toList(inputStream, null);
    }

    /**
     * Read Strings to list from InputStream and do log with info level.
     *
     * @param inputStream stream to read
     * @return Strings from stream
     */
    public static List<String> toList(final InputStream inputStream, final Logger log) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while (Objects.nonNull(line = bufferedReader.readLine())) {
            result.add(line);
            if (Objects.nonNull(log)) {
                log.info(line);
            }
        }
        return result;
    }
}
