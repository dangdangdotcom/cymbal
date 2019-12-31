package com.dangdang.cymbal.job.persistence.service.impl;

import com.dangdang.cymbal.common.util.ThreadUtil;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.job.persistence.exception.PersistenceTimeoutException;
import com.dangdang.cymbal.job.persistence.service.RedisPersistenceService;
import com.dangdang.cymbal.service.operation.enums.RedisCommand;
import com.dangdang.cymbal.service.operation.enums.RedisReplyFormat;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: GeZhen
 * @Date: 2019/4/11 14:28
 */
@Slf4j
@Service
public class RedisPersistenceServiceImpl implements RedisPersistenceService {

    private static final String INFO_PERSISTENCE = "Persistence";

    private static final String INFO_PERSISTENCE_RDB_BGSAVE_IN_PROGRESS = "rdb_bgsave_in_progress";

    private static final String INFO_PERSISTENCE_RDB_LAST_BGSAVE_TIME_SEC = "rdb_last_bgsave_time_sec";

    private static final String INFO_PERSISTENCE_RDB_CURRENT_BGSAVE_TIME_SEC = "rdb_current_bgsave_time_sec";

    private static final String RDB_BGSAVE_NOT_IN_PROGRESS = "0";

    private static final int BG_SAVE_TIME_OUT_MILLIS = 1000 * 60 * 10;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @Override
    public void rdbBgSave(final InstanceBO instanceBO) {
        rdbBgSaveAsync(instanceBO);
        long startTimestamp = System.currentTimeMillis();
        long timePast = 0;
        while (timePast < BG_SAVE_TIME_OUT_MILLIS) {
            checkAndWaitForBgsaveDone(instanceBO);
            timePast = System.currentTimeMillis() - startTimestamp;
        }
        throw new PersistenceTimeoutException("Bgsave timeout: %d ms.", timePast);
    }

    @Override
    public void rdbBgSaveAsync(final InstanceBO instanceBO) {
        redisClientUtilityService.executeRedisCommand(instanceBO, RedisCommand.BGSAVE.getValue(), RedisReplyFormat.RAW);
    }

    private boolean checkAndWaitForBgsaveDone(final InstanceBO instanceBO) {
        List<String> redisInfoStrings = redisClientUtilityService.infoPersistence(instanceBO);
        Map<String, Map<String, String>> persistenceInfo = RedisUtil.parseRedisInfo(redisInfoStrings);
        if (checkBgsaveIsDone(persistenceInfo)) {
            return true;
        }
        waitForNextCheckTime(persistenceInfo);
        return false;
    }

    private boolean checkBgsaveIsDone(final Map<String, Map<String, String>> persistenceInfo) {
        String bgSaveStatus = persistenceInfo.get(INFO_PERSISTENCE).get(INFO_PERSISTENCE_RDB_BGSAVE_IN_PROGRESS);
        if (RDB_BGSAVE_NOT_IN_PROGRESS.equals(bgSaveStatus)) {
            return true;
        }
        return false;
    }

    private void waitForNextCheckTime(final Map<String, Map<String, String>> persistenceInfo) {
        int lastBgsaveTimeSec = Integer
                .valueOf(persistenceInfo.get(INFO_PERSISTENCE).get(INFO_PERSISTENCE_RDB_LAST_BGSAVE_TIME_SEC));
        int currentBgsaveTimeSec = Integer
                .valueOf(persistenceInfo.get(INFO_PERSISTENCE).get(INFO_PERSISTENCE_RDB_CURRENT_BGSAVE_TIME_SEC));
        int secondsToWait = lastBgsaveTimeSec - currentBgsaveTimeSec;
        if (secondsToWait > 0) {
            ThreadUtil.sleep(secondsToWait);
        } else {
            // default sleep 1/10 of BG_SAVE_TIME_OUT_MILLIS
            ThreadUtil.sleep(RedisPersistenceServiceImpl.BG_SAVE_TIME_OUT_MILLIS / (1000 * 10));
        }
    }
}
