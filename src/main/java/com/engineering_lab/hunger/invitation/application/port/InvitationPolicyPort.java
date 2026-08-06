package com.engineering_lab.hunger.invitation.application.port;

import java.time.Duration;

public interface InvitationPolicyPort {
    Duration invitationTtl();
}
