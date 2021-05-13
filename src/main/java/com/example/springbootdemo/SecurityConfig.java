package com.example.springbootdemo;


import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@KeycloakConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Order(10)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
    public static final String BANJO_SYSADMIN_ROLE = "banjo:sysadmin";

    @Autowired
    private KeycloakAuthenticationProvider keycloakAuthenticationProvider;

    private static final String[] PUBLIC_PATHS = {"/error"};

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
        .antMatchers(PUBLIC_PATHS).permitAll()
        .antMatchers("/**").authenticated()
        .and().csrf().disable()
        .logout().logoutUrl("/logout");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider(){

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
                List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

                for (String role : token.getAccount().getRoles()) {
                    grantedAuthorities.add(new KeycloakRole(role));
                }
                return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), mapAuthorities(grantedAuthorities));
            }

            private Collection<? extends GrantedAuthority> mapAuthorities(
                    Collection<? extends GrantedAuthority> authorities) {
                return authorities;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return true;
            }
        });
    }

    // Look for keycloak.* config items in Spring Boot's yml config files instead of expecting a keycloak.json file on the classpath
    /**
     * Use properties in application.properties instead of keycloak.json
     */
    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    // Override the default keycloak auth provider bean to set the SimpleAuthorityMapper, which automatically adds the ROLE_ prefix
    // to all keycloak roles as required by Spring Security.
    @Bean @Override
    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = super.keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        return keycloakAuthenticationProvider;
    }

    // must be overridden and exposed as @Bean, otherwise boot's AuthenticationManagerConfiguration will take precedence
    // and spring will enable http basic with a default password
    // https://github.com/spring-projects/spring-boot/issues/3292
    @Bean @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
