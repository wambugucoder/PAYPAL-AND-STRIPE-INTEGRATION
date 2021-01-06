package com.gofundme.server.security

import com.gofundme.server.service.GoFundMeUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ApiSecurity :WebSecurityConfigurerAdapter(){
    @Autowired
    lateinit var goFundMeUserDetailsService:GoFundMeUserDetailsService

    @Autowired
    lateinit var jwtFilter: JwtFilter

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(goFundMeUserDetailsService)?.passwordEncoder(passwordEncoder())
    }

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
            ?.anyRequest()?.authenticated()
            ?.and()
                // JWT-TOKEN SESSION
            ?.sessionManagement()
            ?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ?.and()
                // XSS PROTECTION
            ?.headers()
            ?.contentSecurityPolicy("script-src 'self' https://trustedscripts.example.com; object-src https://trustedplugins.example.com; report-uri /csp-report-endpoint/")
        http?.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter::class.java)
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
    //BCRYPT CONFIGURATION BEAN
    @Bean
    fun passwordEncoder():BCryptPasswordEncoder{
        return BCryptPasswordEncoder()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}