package com.dangdang.cymbal.service.util.service;

import com.dangdang.cymbal.service.util.exception.ShellExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Test for {@link SshClientService}
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SshClientServiceTest {

    @Resource
    private SshClientService sshClientService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void executeShellScript() {
    }

    @Test(expected = ShellExecutionException.class)
    public void executeShellScriptWithWrongCommand() {
        sshClientService.executeCommand("127.0.0.1", "pwd1");
    }

    @Test
    public void createNewFile() {
    }

    @Test
    public void createNewFiles() {
    }

    @Test
    public void executeCommandByRoot() {
    }

    @Test
    public void executeCommand() {
    }

    @Test
    public void executeCommands() {
    }
}