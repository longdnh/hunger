package com.engineering_lab.hunger.user.infra.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        TopAdminBootstrapProperties.class,
        UserProperties.class
})
public class UserConfig {
}
