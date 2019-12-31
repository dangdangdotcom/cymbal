package com.dangdang.cymbal.web.security.cas;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * PluginConfiguration for CAS.
 *
 * @author GeZhen
 */
@EnableConfigurationProperties(CasProperties.class)
public class CasConfiguration {

    @Autowired
    private CasProperties casProperties;

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setSendRenew(false);
        serviceProperties.setService(this.casProperties.getServiceName());
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    @Bean
    public AuthenticationManager authenticationManager(CasAuthenticationProvider provider) {
        List<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(provider);
        return new ProviderManager(providers);
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager,
            ServiceProperties serviceProperties) {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager);
        casAuthenticationFilter.setServiceProperties(serviceProperties);
        casAuthenticationFilter.setFilterProcessesUrl(casProperties.getServiceLoginUrl());
        casAuthenticationFilter.setContinueChainBeforeSuccessfulAuthentication(false);
        final SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler(
                String.format("%s?service=%s", this.casProperties.getServerLoginUrl(),
                        this.casProperties.getServiceName()));
        casAuthenticationFilter.setProxyAuthenticationFailureHandler(simpleUrlAuthenticationFailureHandler);
        casAuthenticationFilter.setAuthenticationFailureHandler(simpleUrlAuthenticationFailureHandler);
        casAuthenticationFilter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/"));
        return casAuthenticationFilter;
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(this.casProperties.getServerLoginUrl());
        entryPoint.setServiceProperties(serviceProperties);
        return entryPoint;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(this.casProperties.getServerUrlPrefix());
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(CasUserDetailService casUserDetailService,
            ServiceProperties serviceProperties, Cas20ServiceTicketValidator ticketValidator) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setKey("casProvider");
        provider.setServiceProperties(serviceProperties);
        provider.setTicketValidator(ticketValidator);
        provider.setAuthenticationUserDetailsService(casUserDetailService);
        return provider;
    }

    @Bean
    public LogoutFilter logoutFilter() {
        String logoutRedirectPath = String
                .format("%s?service=%s", this.casProperties.getServerLogoutUrl(), this.casProperties.getServiceName());
        LogoutFilter logoutFilter = new LogoutFilter(logoutRedirectPath, new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(this.casProperties.getServiceLogoutUrl());
        return logoutFilter;
    }

    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(this.casProperties.getServerUrlPrefix());
        return singleSignOutFilter;
    }
}
