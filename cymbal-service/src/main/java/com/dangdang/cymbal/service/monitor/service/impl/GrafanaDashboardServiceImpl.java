package com.dangdang.cymbal.service.monitor.service.impl;

import com.dangdang.cymbal.common.constant.Constant;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.service.monitor.exception.MonitorException;
import com.dangdang.cymbal.service.monitor.service.DashboardService;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

// TODO Refactoring

/**
 * dashboard 的 grafana 实现
 *
 * @Author: GeZhen
 * @Date: 2018/8/10 15:32
 */
@Slf4j
@Service
public class GrafanaDashboardServiceImpl implements DashboardService {

    // dashboard中的clusterId占位符
    private final static String GRAFANA_CLUSTER_ID_PALCEHOLDER = "\\$\\{cluster_id\\}";

    // dashboard中的description占位符
    private final static String GRAFANA_CLUSTER_DESCRIPTION_PALCEHOLDER = "\\$\\{cluster_description\\}";

    // 建立dashboard的uri
    private final static String GRAFANA_ADD_DASHBOARD_URI = "api/dashboards/db";

    // 删除dashboard的uri
    private final static String GRAFANA_DEL_DASHBOARD_URI = "api/dashboards/uid";

    private final static String TOTAL_DASHBOARD_ID_SUFFIX = "_total";

    private final static int RADAR_API_RESPONSE_OK = 0;

    @Value("${monitor.grafana.api.url}")
    private String grafanaApiUrl;

    @Value("classpath:META-INF/monitor/grafana/detail_dashboard.json")
    private Resource detailDashboardTemplateResource;

    @Value("classpath:META-INF/monitor/grafana/total_dashboard.json")
    private Resource totalDashboardTemplateResource;

    @Value("${monitor.grafana.api.key}")
    private String grafanaApiKey;

    private String detailDashboardTemplate;

    private String totalDashboardTemplate;

    /**
     * 获取detail dashboard内容
     *
     * @param clusterId
     * @param clusterDescription
     * @return
     * @throws IOException
     */
    private String getDetailDashboard(String clusterId, String clusterDescription) throws IOException {
        if (detailDashboardTemplate == null) {
            detailDashboardTemplate = CharStreams
                    .toString(new InputStreamReader(detailDashboardTemplateResource.getInputStream()));
        }

        // 替换dashboardTemplate中的关键字
        return detailDashboardTemplate.replaceAll(GrafanaDashboardServiceImpl.GRAFANA_CLUSTER_ID_PALCEHOLDER, clusterId)
                .replaceAll(GrafanaDashboardServiceImpl.GRAFANA_CLUSTER_DESCRIPTION_PALCEHOLDER, clusterDescription);
    }

    /**
     * 获取total dashboard内容
     *
     * @param clusterId
     * @param clusterDescription
     * @return
     * @throws IOException
     */
    private String getTotalDashboard(String clusterId, String clusterDescription) throws IOException {
        if (totalDashboardTemplate == null) {
            totalDashboardTemplate = CharStreams
                    .toString(new InputStreamReader(totalDashboardTemplateResource.getInputStream()));
        }

        // 替换dashboardTemplate中的关键字
        return totalDashboardTemplate.replaceAll(GrafanaDashboardServiceImpl.GRAFANA_CLUSTER_ID_PALCEHOLDER, clusterId)
                .replaceAll(GrafanaDashboardServiceImpl.GRAFANA_CLUSTER_DESCRIPTION_PALCEHOLDER, clusterDescription);
    }

    public void addRedisClusterDashboard(Cluster cluster) {
        try {
            delDashboard(cluster.getClusterId());
            // add detail dashboard
            String detailDashBoard = getDetailDashboard(cluster.getClusterId(), cluster.getDescription());
            addDashboard(detailDashBoard);

            // 目前后缀写死在dashboard的json中，不够优雅
            delDashboard(cluster.getClusterId() + GrafanaDashboardServiceImpl.TOTAL_DASHBOARD_ID_SUFFIX);
            // add total dashboard
            String totalDashBoard = getTotalDashboard(cluster.getClusterId(), cluster.getDescription());
            addDashboard(totalDashBoard);
        } catch (IOException e) {
            throw new MonitorException(e);
        }
    }

    private void addDashboard(String dashboard) throws MonitorException {
        // format url
        String url = String.format("%s/%s", grafanaApiUrl, GrafanaDashboardServiceImpl.GRAFANA_ADD_DASHBOARD_URI);
        PostMethod method = new PostMethod(url);

        try {
            // request body
            method.setRequestEntity(new StringRequestEntity(dashboard, "application/json", CharEncoding.UTF_8));
            doHttpAPI(method);
        } catch (UnsupportedEncodingException e) {
            new MonitorException(e);
        }
    }

    /**
     * 删除指定dashboard
     * 目前业务特点无需抛出异常
     *
     * @param dashboardId
     * @throws MonitorException
     */
    private void delDashboard(String dashboardId) throws MonitorException {
        // format url
        String url = String
                .format("%s/%s/%s", grafanaApiUrl, GrafanaDashboardServiceImpl.GRAFANA_DEL_DASHBOARD_URI, dashboardId);
        DeleteMethod method = new DeleteMethod(url);
        try {
            doHttpAPI(method);
        } catch (MonitorException e) {
            log.warn(String.format("delete dashboard fail, dashboard id: %s", dashboardId), e);
        }
    }

    private void doHttpAPI(HttpMethod method) throws MonitorException {
        // 拼装http post请求，调用grafana http api建立dashboard
        HttpClient httpclient = new HttpClient();
        httpclient.getParams().setConnectionManagerTimeout(Constant.Http.CONN_TIMEOUT);
        httpclient.getParams().setSoTimeout(Constant.Http.SO_TIMEOUT);

        // api key
        method.setRequestHeader("Authorization", String.format("Bearer %s", grafanaApiKey));
        try {

            int statusCode = httpclient.executeMethod(method);
            // 若http请求失败
            if (statusCode != Constant.Http.STATUS_OK) {
                String responseBody = method.getResponseBodyAsString();
                throw new MonitorException(responseBody);
            }
        } catch (Exception e) {
            if (e instanceof MonitorException) {
                throw (MonitorException) e;
            } else {
                new MonitorException(e);
            }
        } finally {
            method.releaseConnection();
        }
    }

    @Override
    public void initRedisDashboard() {

    }

    @Override
    public void initNodeDashBoard() {

    }
}




