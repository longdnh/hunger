package com.engineering_lab.hunger.invitation.application.port;

import java.util.Optional;

import com.engineering_lab.hunger.invitation.domain.model.InvitationDomain;

public interface InvitationRepositoryPort {

    InvitationDomain save(InvitationDomain invitation);

    Optional<InvitationDomain> findByTokenHashForUpdate(String tokenHash);
}
