package com.dangdang.cymbal.proxy.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 *
 * @auther GeZhen
 */
@ConfigurationProperties(prefix = "proxy")
@Getter
@Setter
public class ProxyProperties {

    private Map<String, String> grafana;

    private Map<String, String> prometheus;
}
