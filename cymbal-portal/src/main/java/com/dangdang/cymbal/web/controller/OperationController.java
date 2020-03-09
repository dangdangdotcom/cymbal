package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.service.operation.enums.RedisReplyFormat;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisOperationUtilityService;
import com.dangdang.cymbal.service.util.exception.ShellExecutionException;
import com.dangdang.cymbal.web.object.dto.InstanceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Controller of operation.
 * Such as start, stop redis server, fail over, and so on.
 *
 * @auther GeZhen
 */
@Slf4j
@Controller
public class OperationController extends BaseController {

    @Resource
    private RedisOperationUtilityService redisOperationUtilityService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @PatchMapping(value = "/startup/instances")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForInstances(#instanceIds, principal.username)")
    @ResponseBody
    public void startServers(@RequestBody Set<Integer> instanceIds) {
        redisOperationUtilityService.startup(instanceIds);
    }

    @PatchMapping(value = "/shutdown/instances")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForInstances(#instanceIds, principal.username)")
    @ResponseBody
    public void stopServers(@RequestBody List<Integer> instanceIds) {
        redisOperationUtilityService.shutdown(instanceIds);
    }

    @PatchMapping(value = "/replication/instances/{instanceId}")
    @ResponseBody
    public void slaveOf(@PathVariable Integer instanceId, @RequestBody InstanceDTO masterInstance) {
        redisOperationUtilityService
                .slaveOf(instanceId, masterInstance.getIp(), masterInstance.getPort(), masterInstance.getPassword());
    }

    @DeleteMapping(value = "/replication/instances/{instanceId}")
    @ResponseBody
    public void slaveOfNoOne(@PathVariable Integer instanceId, @RequestBody InstanceDTO masterInstance) {
        redisOperationUtilityService.slaveOfNoOne(instanceId);
    }

    @PatchMapping(value = "/forget/instances/{instanceId}")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForInstance(#instanceId, principal.username)")
    @ResponseBody
    public void forgetInstanceInCluster(@PathVariable Integer instanceId) {
        throw new UnsupportedOperationException("This api will be support soon.");
    }

    @PatchMapping(value = "/failover/instances/{instanceId}")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForInstance(#instanceId, principal.username)")
    @ResponseBody
    public ResponseEntity failover(@PathVariable Integer instanceId, @AuthenticationPrincipal Principal principal) {
        log.warn("Failover is calling to redis server '{}' by user '{}'.", instanceId, principal.getName());
        try {
            redisOperationUtilityService.failover(instanceId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (UnsupportedOperationException e) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/command/instances/{instanceId}")
    @PreAuthorize(value = "@clusterPermissionChecker.hasViewPermissionForInstance(#instanceId, principal.username)")
    @ResponseBody
    public ResponseEntity<List<String>> executeRedisCommand(@PathVariable Integer instanceId,
            @RequestBody String command, @AuthenticationPrincipal Principal principal) {
        log.info("Redis command '{}' is calling to redis server instance '{}' by user '{}'.", command, instanceId,
                principal.getName());
        try {
            List<String> result = redisClientUtilityService
                    .executeRedisCommand(instanceId, command, RedisReplyFormat.NO_RAW);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (ShellExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Arrays.asList(e.getMessage()));
        }
    }
}
