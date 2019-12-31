package com.dangdang.cymbal.service.monitor.service;

/**
 * @Author: GeZhen
 * @Date: 2018/8/10 15:15
 */
public interface DashboardService {

    /**
     * Init two dashboards for redis.
     * One is for redis server instance, the other is for redis cluster.
     */
    void initRedisDashboard();

    /**
     * Init dasbboard for node.
     */
    void initNodeDashBoard();
}
