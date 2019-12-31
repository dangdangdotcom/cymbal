package com.dangdang.cymbal.service.util.service;

import java.util.List;
import java.util.Map;

/**
 * Ssh client service.
 * Do something on remote node.
 *
 * @author GeZhen
 */
public interface SshClientService {

    List<String> executeShellScript(String host, String shellScriptPath, String shellScriptName, String... args);

    void createNewFile(String host, String fileName, String fileContent, String path);

    void createNewFiles(String host, String[] fileName, String[] fileContent, String[] path);

    List<String> executeCommandByRoot(String host, String command);

    List<String> executeCommand(String host, String command);

    Map<String, List<String>> executeCommands(String host, String user, String... command);
}
