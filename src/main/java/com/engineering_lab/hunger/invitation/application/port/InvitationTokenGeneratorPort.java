package com.engineering_lab.hunger.invitation.application.port;

import com.engineering_lab.hunger.invitation.application.result.GeneratedInvitationTokenResult;

public interface InvitationTokenGeneratorPort {

    GeneratedInvitationTokenResult generate();

    String hash(String rawToken);
}
