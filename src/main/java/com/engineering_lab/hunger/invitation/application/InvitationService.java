package com.engineering_lab.hunger.invitation.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.invitation.application.exception.InvitationException;
import com.engineering_lab.hunger.invitation.application.port.InvitationPolicyPort;
import com.engineering_lab.hunger.invitation.application.port.InvitationRepositoryPort;
import com.engineering_lab.hunger.invitation.application.port.InvitationTokenGeneratorPort;
import com.engineering_lab.hunger.invitation.application.result.AcceptedInvitationResult;
import com.engineering_lab.hunger.invitation.application.result.CreatedInvitationResult;
import com.engineering_lab.hunger.invitation.application.result.GeneratedInvitationTokenResult;
import com.engineering_lab.hunger.invitation.domain.model.InvitationDomain;
import com.engineering_lab.hunger.membership.application.port.MembershipRepositoryPort;
import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.membership.domain.model.MembershipRole;
import com.engineering_lab.hunger.user.application.UserService;
import com.engineering_lab.hunger.user.application.port.UserRepositoryPort;
import com.engineering_lab.hunger.user.application.result.UserResult;
import com.engineering_lab.hunger.user.domain.model.UserDomain;

@Service
public class InvitationService {
    private final InvitationRepositoryPort invitationRepository;
    private final InvitationTokenGeneratorPort tokenGenerator;
    private final InvitationPolicyPort policy;
    private final MembershipRepositoryPort membershipRepository;
    private final UserRepositoryPort userRepository;
    private final UserService userService;
    private final Clock clock;

    public InvitationService(
            InvitationRepositoryPort invitationRepository,
            InvitationTokenGeneratorPort tokenGenerator,
            InvitationPolicyPort policy,
            MembershipRepositoryPort membershipRepository,
            UserRepositoryPort userRepository,
            UserService userService,
            Clock clock) {
        this.invitationRepository = invitationRepository;
        this.tokenGenerator = tokenGenerator;
        this.policy = policy;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.clock = clock;
    }

    @Transactional
    public CreatedInvitationResult create(
            UUID creatorUserId, UUID tenantId, String email, MembershipRole role) {
        requireCompanyAdmin(creatorUserId, tenantId);
        if (role != MembershipRole.MEMBER && role != MembershipRole.GUEST) {
            throw InvitationException.invalid();
        }
        String validatedEmail;
        try {
            validatedEmail = Validator.requireEmail(email);
        } catch (IllegalArgumentException exception) {
            throw InvitationException.invalid();
        }
        if (validatedEmail.length() > 100) throw InvitationException.invalid();
        GeneratedInvitationTokenResult token = tokenGenerator.generate();
        Instant now = clock.instant();
        Instant expiresAt = now.plus(policy.invitationTtl());
        InvitationDomain saved = invitationRepository.save(InvitationDomain.create(
                tenantId, creatorUserId, validatedEmail, role, token.hash(), now, expiresAt));
        return new CreatedInvitationResult(
                saved.getId(), tenantId, saved.getEmail(), role, token.value(), expiresAt);
    }

    @Transactional
    public AcceptedInvitationResult register(
            String rawToken, String name, String password) {
        InvitationDomain invitation = activeInvitation(rawToken);
        if (userRepository.existsByNormalizedEmail(invitation.getNormalizedEmail())) {
            throw InvitationException.accountExists();
        }
        UserResult user = userService.registerInvitedUser(
                name, invitation.getEmail(), password);
        createMembership(user.userId(), invitation);
        invitation.accept(clock.instant());
        invitationRepository.save(invitation);
        return new AcceptedInvitationResult(user, invitation.getTenantId(), invitation.getRole());
    }

    @Transactional
    public AcceptedInvitationResult accept(UUID userId, String rawToken) {
        InvitationDomain invitation = activeInvitation(rawToken);
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(InvitationException::invalid);
        if (!user.getNormalizedEmail().equals(invitation.getNormalizedEmail())) {
            throw InvitationException.invalid();
        }
        createMembership(userId, invitation);
        invitation.accept(clock.instant());
        invitationRepository.save(invitation);
        return new AcceptedInvitationResult(
                UserResult.from(user), invitation.getTenantId(), invitation.getRole());
    }

    private InvitationDomain activeInvitation(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) throw InvitationException.invalid();
        InvitationDomain invitation = invitationRepository
                .findByTokenHashForUpdate(tokenGenerator.hash(rawToken))
                .orElseThrow(InvitationException::invalid);
        if (!invitation.isActive(clock.instant())) throw InvitationException.invalid();
        return invitation;
    }

    private void requireCompanyAdmin(UUID userId, UUID tenantId) {
        MembershipDomain membership = membershipRepository
                .findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(InvitationException::adminRequired);
        if (!membership.grantsAccess() || membership.getRole() != MembershipRole.ADMIN) {
            throw InvitationException.adminRequired();
        }
    }

    private void createMembership(UUID userId, InvitationDomain invitation) {
        if (membershipRepository.findByUserIdAndTenantId(userId, invitation.getTenantId()).isPresent()) {
            throw InvitationException.membershipExists();
        }
        membershipRepository.save(MembershipDomain.create(
                userId, invitation.getTenantId(), invitation.getRole(), clock.instant()));
    }
}
