package com.engineering_lab.hunger.invitation.infra.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.engineering_lab.hunger.invitation.application.port.InvitationPolicyPort;

@ConfigurationProperties(prefix = "security.invitation")
public record InvitationProperties(Duration invitationTtl) implements InvitationPolicyPort {
    public InvitationProperties {
        if (invitationTtl == null || invitationTtl.isZero() || invitationTtl.isNegative()) {
            throw new IllegalArgumentException("invitationTtl must be positive");
        }
    }
}
