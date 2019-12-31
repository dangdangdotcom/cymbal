package com.dangdang.cymbal.service.util;

import com.dangdang.cymbal.domain.po.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author: GeZhen
 * @Date: 2019/3/13 10:12
 */
@Slf4j
public class MailUtil {

    public static String getClusterInfoForHtmlMail(Cluster cluster) {
        StringBuilder mailContent = new StringBuilder();
        mailContent.append("<p>").append("clusterId: ").append(cluster.getClusterId()).append("</p>");
        mailContent.append("<p>").append("集群描述: ").append(cluster.getDescription()).append("</p>");
        mailContent.append("<p>").append("负责人: ").append(cluster.getUserCnName()).append("</p>");
        mailContent.append("<p>").append("主节点数量: ").append(cluster.getMasterCount()).append("</p>");
        mailContent.append("<p>").append("单节点容量: ").append(cluster.getCacheSize()).append("GB").append("</p>");
        mailContent.append("<p>").append("集群总容量: ").append(cluster.getMasterCount() * cluster.getCacheSize())
                .append("GB").append("</p>");
        mailContent.append("<p>").append("集群详情链接: ").append("<a href='")
                .append(getOpsPlatformUrl(cluster.getClusterId())).append("'>")
                .append(getOpsPlatformUrl(cluster.getClusterId())).append("</a></p>");
        return mailContent.toString();
    }

    public static String getOpsPlatformUrl(String clusterId) {
        StringBuffer url = new StringBuffer("http://ops.dangdang.com/#redis/page?");
        try {
            url.append("data=")
                    .append(URLEncoder.encode(String.format("{\"clusterId\":\"%s\"}", clusterId), CharEncoding.UTF_8));
            url.append("&title=").append(URLEncoder.encode("[\"Redis管理主界面\",\"主机管理\",\"主机详情\"]", CharEncoding.UTF_8));
        } catch (UnsupportedEncodingException e) {
            log.error("fail to encode the url", e);
        }
        return url.toString();
    }
}
