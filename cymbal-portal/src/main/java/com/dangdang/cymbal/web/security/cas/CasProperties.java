package com.dangdang.cymbal.web.security.cas;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for CAS.
 * Such as CAS server info.
 *
 * @author GeZhen
 */
@Data
@ConfigurationProperties(prefix = "security.cas")
public class CasProperties {

    /**
     * CAS server's login url.
     */
    private String serverLoginUrl;

    /**
     * CAS server's logout url.
     */
    private String serverLogoutUrl;

    /**
     * CAS server's authentication url prefix.
     */
    private String serverUrlPrefix;

    /**
     * Domain name of redis platform.
     */
    private String serviceName;

    /**
     * Login url of redis platform.
     */
    private String serviceLoginUrl;

    /**
     * Logout url of redis platform.
     */
    private String serviceLogoutUrl;
}
