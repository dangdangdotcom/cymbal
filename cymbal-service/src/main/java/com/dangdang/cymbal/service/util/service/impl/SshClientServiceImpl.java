package com.dangdang.cymbal.service.util.service.impl;

import com.dangdang.cymbal.common.util.StreamUtil;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.util.exception.ShellExecutionException;
import com.dangdang.cymbal.service.util.service.SshClientService;
import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Signal;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A ssh client service implement.
 *
 * @author GeZhen
 */
@Slf4j
@Service
public class SshClientServiceImpl implements SshClientService {

    // ssh登陆用户
    private final static String USER_REDIS = "redis";

    private final static String USER_ROOT = "root";

    // unit second
    private final static int EXECUTE_TIME_OUT = 10;

    // max ssh client in cache
    private final static int CACHE_CLIENT_MAX = 50;

    private final static int CACHE_CLIENT_TTL = 60;

    @Value("${ssh.key.path}")
    private String keyPath;

    private LoadingCache<SSHInfo, SSHClient> sshClientCache;

    private DefaultConfig defaultConfig;

    @Data
    class SSHInfo {
        private String hostName;

        private String user;

        @Override
        public boolean equals(final Object another) {
            if (another == null || !(another instanceof SSHInfo)) {
                return false;
            }

            if (this == another) {
                return true;
            }

            SSHInfo anotherSSHInfo = (SSHInfo) another;
            if (Objects.equal(hostName, anotherSSHInfo.getHostName()) && Objects
                    .equal(user, anotherSSHInfo.getUser())) {
                return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            int result = hostName != null ? hostName.hashCode() : 0;
            result = 31 * result + (user != null ? user.hashCode() : 0);
            return result;
        }
    }


    public SshClientServiceImpl() {
        defaultConfig = new DefaultConfig();

        // 免密登陆校验器
        Security.addProvider(new BouncyCastleProvider());

        // sshClient的缓存，避免每次命令都进行ssh连接的耗时
        sshClientCache = CacheBuilder.newBuilder().maximumSize(CACHE_CLIENT_MAX)
                .expireAfterAccess(CACHE_CLIENT_TTL, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<SSHInfo, SSHClient>() {

                    @Override
                    public void onRemoval(RemovalNotification<SSHInfo, SSHClient> removalNotification) {
                        try {
                            removalNotification.getValue().disconnect();
                        } catch (IOException e) {
                            log.error("Close ssh connection error.", e);
                        }

                        log.debug("Ssh client to {} is expired.", removalNotification.getKey());
                    }
                }).build(new CacheLoader<SSHInfo, SSHClient>() {

                    @Override
                    public SSHClient load(SSHInfo sshInfo) throws Exception {
                        return getSshCLient(sshInfo);
                    }
                });
    }

    private SSHClient getSshCLient(final SSHInfo sshInfo) throws IOException {
        SSHClient ssh = new SSHClient(defaultConfig);

        // 登陆验证器
        ssh.addHostKeyVerifier(new PromiscuousVerifier());

        // ?
        ssh.loadKnownHosts();

        // do connect
        ssh.connect(sshInfo.getHostName());

        // 加载免密登陆私钥
        KeyProvider keyProvider = ssh.loadKeys(keyPath);
        ssh.authPublickey(sshInfo.getUser(), keyProvider);

        return ssh;
    }

    private String getCreateNewFileShellCommand(final String fileName, final String fileContent, final String path) {
        return String.format("echo '%s' > %s/%s", fileContent, path, fileName);
    }

    @Override
    public List<String> executeShellScript(final String host, final String commandPath, final String command,
            final String... args) {
        StringBuffer commandWithArgs = new StringBuffer();
        // cd到对应目录
        commandWithArgs.append("cd ").append(commandPath).append(";");
        commandWithArgs.append("./").append(command);

        for (String each : args) {
            commandWithArgs.append(" ").append(each);
        }

        String commandStr = commandWithArgs.toString();
        return executeCommand(host, commandStr);
    }

    @Override
    public void createNewFile(final String host, final String fileName, final String fileContent, final String path) {
        executeCommand(host, getCreateNewFileShellCommand(fileName, fileContent, path));
    }

    @Override
    public void createNewFiles(final String host, final String[] fileNames, final String[] fileContents,
            final String[] paths) {
        // 三个数组的size不相同，抛出异常
        if (fileNames.length != fileContents.length || fileContents.length != paths.length) {
            throw new IllegalArgumentException(String.format(
                    "invalid arguments: length of fileNames, fileContents, paths must equal, " + "now is {} {} {}",
                    fileNames.length, fileContents.length, paths.length));
        }

        String[] commands = new String[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            commands[i] = getCreateNewFileShellCommand(fileNames[i], fileContents[i], paths[i]);
        }

        executeCommands(host, SshClientServiceImpl.USER_REDIS, commands);
    }

    @Override
    public List<String> executeCommandByRoot(final String host, final String command) {
        return executeCommands(host, USER_ROOT, command).get(command);
    }

    @Override
    public List<String> executeCommand(final String host, final String command) {
        return executeCommands(host, USER_REDIS, command).get(command);
    }

    @Override
    public Map<String, List<String>> executeCommands(final String host, final String user, final String... commands) {
        SSHClient ssh = null;
        Session session = null;
        Map<String, List<String>> results = new HashMap<>();

        try {
            // get ssh connection
            SSHInfo sshInfo = new SSHInfo();
            sshInfo.setHostName(host);
            sshInfo.setUser(user);
            ssh = this.sshClientCache.get(sshInfo);

            session = ssh.startSession();

            for (String command : commands) {
                log.info("Starting to run command '{}' on node '{}'.", command, host);
                Session.Command cmd = session.exec(command);

                // wait for execute
                cmd.join(EXECUTE_TIME_OUT, TimeUnit.SECONDS);

                // read result from input stream
                List<String> result;
                try {
                    result = StreamUtil.toList(cmd.getInputStream());
                } catch (IOException e) {
                    throw new ShellExecutionException(e, "", command, host);
                }

                // if command execute fail
                Integer exitStatus = cmd.getExitStatus();
                if (exitStatus != null && exitStatus.intValue() != Constant.Shell.EXIT_VALUE_OK) {
                    String errorResult = StreamUtils.copyToString(cmd.getErrorStream(), Charset.defaultCharset());
                    throw new ShellExecutionException("Fail to run command '%s' on node '%s' with result '%s'.",
                            command, host, errorResult);
                }

                Signal exitSignal = cmd.getExitSignal();
                if (exitSignal != null) {
                    log.warn("Command '{}' finished on node '{}' with exit signal: {}.", command, host,
                            exitSignal.toString());
                }

                log.info("Run command '{}' on node '{}' finished, result is: '{}'.", command, host, result);

                // add to results
                results.put(command, result);
            }

            return results;
        } catch (ShellExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new ShellExecutionException(e);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                log.error("Close ssh client session error.", e);
            }
        }
    }
}
