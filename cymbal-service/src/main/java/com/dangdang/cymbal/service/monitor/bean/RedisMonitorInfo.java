package com.dangdang.cymbal.service.monitor.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 *
 * @auther GeZhen
 */
@Builder
@Getter
@Setter
public class RedisMonitorInfo {

    private long connectedClients;

    private double instantaneousOutputKbps;

    private long keyspaceHits;

    private long keyspaceMisses;

    private float keyspaceHitPercent;

    private long usedMemory;

    private float usedMemoryPercent;

    private Date scrapeTime;
}
