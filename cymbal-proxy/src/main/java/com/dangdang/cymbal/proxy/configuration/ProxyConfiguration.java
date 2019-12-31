package com.dangdang.cymbal.proxy.configuration;

import com.dangdang.cymbal.proxy.properties.ProxyProperties;
import lombok.Getter;
import lombok.Setter;
import org.mitre.dsmiley.httpproxy.URITemplateProxyServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @auther GeZhen
 */
@Configuration
@EnableConfigurationProperties(ProxyProperties.class)
@Getter
@Setter
public class ProxyConfiguration {

    private static final String PARAM_TARGET_URI = "targetUri";

    @Autowired
    private ProxyProperties proxyProperties;

    @Bean
    @ConditionalOnProperty(name = "proxy.grafana.enable", havingValue = "true")
    public ServletRegistrationBean grafanaProxyServletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new URITemplateProxyServlet(),
                "/grafana/*");
        registrationBean.setName("grafana");
        registrationBean.setInitParameters(proxyProperties.getGrafana());
        return registrationBean;
    }

    @Bean
    @ConditionalOnProperty(name = "proxy.prometheus.enable", havingValue = "true")
    public ServletRegistrationBean prometheusProxyServletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new URITemplateProxyServlet(),
                "/prometheus/*");
        registrationBean.setName("prometheus");
        registrationBean.setInitParameters(proxyProperties.getPrometheus());
        return registrationBean;
    }
}
