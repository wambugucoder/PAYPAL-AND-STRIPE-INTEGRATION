package com.gofundme.server.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ApiSecurity :WebSecurityConfigurerAdapter(){

    override fun configure(http: HttpSecurity?) {
        http?.requiresChannel()
                // SWITCHING TO HTTPS
            ?.anyRequest()
            ?.requiresSecure()
            ?.and()
            ?.csrf()?.disable()
                // STRICT AUTHORIZATION AND AUTHENTICATION
            ?.authorizeRequests()
            ?.antMatchers("/api/v1/auth/**")?.permitAll()
            ?.antMatchers()?.permitAll()
            ?.anyRequest()?.authenticated()
            ?.and()
                // JWT-TOKEN SESSION
            ?.sessionManagement()
            ?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ?.and()
                // XSS PROTECTION
            ?.headers()
            ?.contentSecurityPolicy("script-src 'self' https://trustedscripts.example.com; object-src https://trustedplugins.example.com; report-uri /csp-report-endpoint/")
    }

    override fun configure(web: WebSecurity?) {
        // ALLOW OPENAPI-ENDPOINTS TO BE USED
        web?.ignoring()?.antMatchers("/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/actuators")
    }
}