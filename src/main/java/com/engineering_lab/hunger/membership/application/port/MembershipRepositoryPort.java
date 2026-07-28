package com.engineering_lab.hunger.membership.application.port;

import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;

public interface MembershipRepositoryPort {

    MembershipDomain save(MembershipDomain membership);
}
