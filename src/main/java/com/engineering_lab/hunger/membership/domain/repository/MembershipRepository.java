package com.engineering_lab.hunger.membership.domain.repository;

import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;

public interface MembershipRepository {
    MembershipDomain save(MembershipDomain membership);
}
