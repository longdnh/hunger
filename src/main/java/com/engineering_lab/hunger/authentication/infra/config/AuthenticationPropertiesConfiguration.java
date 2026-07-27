package com.engineering_lab.hunger.authentication.infra.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        AuthenticationProperties.class,
        RefreshTokenCookieProperties.class
})
public class AuthenticationPropertiesConfiguration {
}
