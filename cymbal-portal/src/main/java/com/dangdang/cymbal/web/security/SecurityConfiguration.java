package com.dangdang.cymbal.web.security;

import com.dangdang.cymbal.common.spi.UserProcessService;
import com.dangdang.cymbal.service.auth.service.process.impl.CASUserProcessServiceImpl;
import com.dangdang.cymbal.service.auth.service.process.impl.DbUserProcessServiceImpl;
import com.dangdang.cymbal.web.security.cas.CasProperties;
import com.dangdang.cymbal.web.security.cas.CasUserDetailService;
import com.dangdang.cymbal.web.security.db.DbUserDetailService;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * PluginConfiguration of security.
 * Support db and cas way.
 *
 * @auther GeZhen
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @ConditionalOnProperty(name = "security.cas.enable")
    @Configuration
    @EnableConfigurationProperties(CasProperties.class)
    static class CASSecurityConfiguration {

        @Resource
        private CasProperties casProperties;

        @Bean
        @ConditionalOnMissingBean
        public UserProcessService casUserProcessService() {
            return new CASUserProcessServiceImpl();
        }

        @Bean
        public SingleSignOutFilter singleSignOutFilter() {
            SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
            singleSignOutFilter.setCasServerUrlPrefix(this.casProperties.getServerUrlPrefix());
            singleSignOutFilter.setIgnoreInitConfiguration(true);
            return singleSignOutFilter;
        }

        @Bean
        public FilterRegistrationBean ssoFilterRegistrationBean(final SingleSignOutFilter singleSignOutFilter) {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            filterRegistrationBean.setFilter(singleSignOutFilter);
            filterRegistrationBean.addUrlPatterns("/*");
            filterRegistrationBean.setOrder(1);
            return filterRegistrationBean;
        }

        @Bean
        public ServiceProperties serviceProperties() {
            ServiceProperties serviceProperties = new ServiceProperties();
            serviceProperties.setSendRenew(false);
            serviceProperties.setService(this.casProperties.getServiceName());
            serviceProperties.setAuthenticateAllArtifacts(true);
            return serviceProperties;
        }

        @Bean
        public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
            return new Cas20ServiceTicketValidator(this.casProperties.getServerUrlPrefix());
        }

        @Bean
        public CasUserDetailService casUserDetailService() {
            return new CasUserDetailService();
        }

        @Bean
        public CasAuthenticationProvider casAuthenticationProvider(final CasUserDetailService casUserDetailService,
                final ServiceProperties serviceProperties, final Cas20ServiceTicketValidator ticketValidator) {
            CasAuthenticationProvider provider = new CasAuthenticationProvider();
            provider.setKey("casProvider");
            provider.setServiceProperties(serviceProperties);
            provider.setTicketValidator(ticketValidator);
            provider.setAuthenticationUserDetailsService(casUserDetailService);
            return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(final CasAuthenticationProvider provider) {
            List<AuthenticationProvider> providers = new ArrayList<>();
            providers.add(provider);
            return new ProviderManager(providers);
        }

        @Bean
        public CasAuthenticationFilter casAuthenticationFilter(final AuthenticationManager authenticationManager,
                final ServiceProperties serviceProperties) {
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
        public FilterRegistrationBean casAuthenticationFilterRegistrationBean(
                final CasAuthenticationFilter casAuthenticationFilter) {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            filterRegistrationBean.setFilter(casAuthenticationFilter);
            filterRegistrationBean.addUrlPatterns("/*");
            filterRegistrationBean.setOrder(3);
            return filterRegistrationBean;
        }

        @Bean
        public LogoutFilter logoutFilter() {
            String logoutRedirectPath = String.format("%s?service=%s", this.casProperties.getServerLogoutUrl(),
                    this.casProperties.getServiceName());
            LogoutFilter logoutFilter = new LogoutFilter(logoutRedirectPath, new SecurityContextLogoutHandler());
            logoutFilter.setFilterProcessesUrl(this.casProperties.getServiceLogoutUrl());
            return logoutFilter;
        }

        @Bean
        public FilterRegistrationBean logoutFilterRegistrationBean(final LogoutFilter logoutFilter) {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            filterRegistrationBean.setFilter(logoutFilter);
            filterRegistrationBean.addUrlPatterns("/*");
            filterRegistrationBean.setOrder(2);
            return filterRegistrationBean;
        }

        @Bean
        public CasAuthenticationEntryPoint casAuthenticationEntryPoint(final ServiceProperties serviceProperties) {
            CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
            entryPoint.setLoginUrl(this.casProperties.getServerLoginUrl());
            entryPoint.setServiceProperties(serviceProperties);
            return entryPoint;
        }
    }

    @ConditionalOnProperty(name = "security.cas.enable")
    @Configuration
    static class CasWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Resource
        private AuthenticationProvider authenticationProvider;

        @Autowired
        private CasAuthenticationEntryPoint authenticationEntryPoint;

        @Autowired
        private CasAuthenticationFilter authenticationProcessingFilter;

        @Autowired
        private LogoutFilter logoutFilter;

        @Autowired
        private SingleSignOutFilter singleSignOutFilter;

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.headers().frameOptions().disable();
            http.csrf().disable();

            http.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll().anyRequest()
                    .authenticated();

            http.logout().permitAll();

            http.exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint).and()
                    .addFilter(this.authenticationProcessingFilter)
                    .addFilterBefore(this.logoutFilter, authenticationProcessingFilter.getClass())
                    .addFilterBefore(this.singleSignOutFilter, authenticationProcessingFilter.getClass());
        }

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authenticationProvider);
        }
    }

    @ConditionalOnProperty(name = "security.cas.enable", havingValue = "false")
    @Configuration
    static class DbSecurityConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public UserProcessService dbUserProcessService() {
            return new DbUserProcessServiceImpl();
        }

        @Bean
        public DbUserDetailService dbUserDetailService() {
            return new DbUserDetailService();
        }

        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider(final DbUserDetailService dbUserDetailService) {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setUserDetailsService(dbUserDetailService);
            daoAuthenticationProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
            return daoAuthenticationProvider;
        }

        @ConditionalOnProperty(name = "security.cas.enable", havingValue = "false")
        @Configuration
        static class DbWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

            @Resource
            private AuthenticationProvider authenticationProvider;

            @Override
            protected void configure(final HttpSecurity http) throws Exception {
                http.headers().frameOptions().disable();
                http.csrf().disable();

                http.authorizeRequests().antMatchers("/login", "/logout").permitAll()
                        .antMatchers("/**").authenticated()
                        .and()
                        .formLogin().loginPage("/login").defaultSuccessUrl("/")
                        .and()
                        .logout().logoutUrl("/logout").logoutSuccessUrl("/login");

                http.logout().permitAll();
            }

            @Override
            protected void configure(final AuthenticationManagerBuilder auth) {
                auth.authenticationProvider(authenticationProvider);
            }
        }
    }
}
