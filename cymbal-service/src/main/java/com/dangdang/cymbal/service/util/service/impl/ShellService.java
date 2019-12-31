package com.dangdang.cymbal.service.util.service.impl;

import com.dangdang.cymbal.common.util.StreamUtil;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.util.enums.AnsiblePlayBookName;
import com.dangdang.cymbal.service.util.exception.ShellExecutionException;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@Service
public class ShellService {

    private static final String BLANK_SPLITTER = " ";

    @Value("${shell.name.redis}")
    private String shellNameOfRedis;

    @Value("${shell.name.sentinel}")
    private String shellNameOfSentinel;

    @Value("${shell.name.ansible}")
    private String shellNameOfAnsible;

    public List<String> execRedisShellScript(final String... params) {
        return execShellScript(shellNameOfRedis + BLANK_SPLITTER + Joiner.on(BLANK_SPLITTER).skipNulls().join(params));
    }

    public List<String> execSentinelShellScript(final String... params) {
        return execShellScript(
                shellNameOfSentinel + BLANK_SPLITTER + Joiner.on(BLANK_SPLITTER).skipNulls().join(params));
    }

    public List<String> execAnsibleShellScript(final AnsiblePlayBookName ansiblePlayBookName, final String... params) {
        return execShellScript(
                shellNameOfAnsible + BLANK_SPLITTER + ansiblePlayBookName.getValue() + BLANK_SPLITTER + Joiner
                        .on(BLANK_SPLITTER).skipNulls().join(params));
    }

    private List<String> execShellScript(final String command) {
        try {
            log.info("Starting to run command '{}'.", command);
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            List<String> result = StreamUtil.toList(process.getInputStream(), log);

            // If result from input stream is empty, try to read result from error stream
            int exitValue = process.waitFor();
            if (exitValue != Constant.Shell.EXIT_VALUE_OK) {
                String errorResult = StreamUtils.copyToString(process.getErrorStream(), Charset.defaultCharset());
                throw new ShellExecutionException(errorResult);
            }
            log.info("Run command '{}' finished, result is '{}'.", command, result);
            return result;
        } catch (final IOException | InterruptedException e) {
            throw new ShellExecutionException(e);
        }
    }
}
