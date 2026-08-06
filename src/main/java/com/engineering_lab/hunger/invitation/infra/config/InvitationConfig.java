package com.engineering_lab.hunger.invitation.infra.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(InvitationProperties.class)
public class InvitationConfig {
}
