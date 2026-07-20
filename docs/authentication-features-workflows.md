# Authentication Features and Workflows

## 1. Mục tiêu

Authentication của Hunger phải xác định được:

1. Người dùng là ai (`userId`).
2. Phiên đăng nhập có còn hợp lệ không (`sessionId`).
3. Request đang hoạt động trong tenant nào (`tenantId`).
4. Người dùng có membership và quyền phù hợp trong tenant đó không.

Hunger sử dụng mô hình global identity: một `User` chỉ có một danh tính toàn hệ thống nhưng có thể tham gia nhiều `Tenant` thông qua `Membership`.

```text
User ── Session
  │
  └── Membership ── Tenant
          │
          └── Role / Permissions
```

Authentication và tenant authorization là hai trách nhiệm khác nhau:

- Module `identity` quản lý user, credential, email verification và session.
- Module `tenant` quản lý tenant, membership, invitation, active tenant context và tenant-scoped authorization.
- Mọi protected request phải đi qua cả authentication và authorization.

## 2. Feature backlog

### 2.1. P0 — Core authentication

Các feature bắt buộc cho phiên bản đầu tiên:

| Feature | Mô tả | Module |
| --- | --- | --- |
| Register | Đăng ký bằng email và password | `identity` |
| Email verification | Xác thực quyền sở hữu email bằng token có thời hạn và chỉ dùng một lần | `identity` |
| Login | Đăng nhập bằng email và password | `identity` |
| Logout | Thu hồi session hiện tại và xóa cookie | `identity` |
| Logout all devices | Thu hồi toàn bộ session của user | `identity` |
| Forgot password | Yêu cầu email đặt lại mật khẩu mà không làm lộ account có tồn tại hay không | `identity` |
| Reset password | Đặt mật khẩu mới bằng token ngắn hạn, single-use | `identity` |
| Change password | Đổi mật khẩu sau khi xác thực lại credential hiện tại | `identity` |
| Session management | Tạo, kiểm tra, gia hạn và thu hồi session | `identity` |
| Authentication rate limit | Hạn chế register, login và reset-password abuse | `identity` |
| Authentication audit | Ghi nhận login, logout, login failure và thay đổi credential | `identity` / `activity` |

### 2.2. P0 — Multi-tenant access

| Feature | Mô tả | Module |
| --- | --- | --- |
| Create tenant | User tạo tenant và trở thành owner | `tenant` |
| List tenants | Liệt kê các tenant mà user có active membership | `tenant` |
| Select active tenant | Chọn tenant sau khi đăng nhập | `tenant` |
| Switch tenant | Chuyển tenant context mà không cần đăng nhập lại | `tenant` |
| Resolve tenant context | Xác định tenant từ request và không tin tưởng `tenantId` do client tự khai báo | `tenant` |
| Membership validation | Kiểm tra active membership trên mọi tenant-scoped request | `tenant` |
| Role authorization | Kiểm tra role hoặc permission cho từng use case | `tenant` |
| Invite member | Owner/Admin mời user qua email và chỉ định role | `tenant` |
| Accept invitation | User chấp nhận invitation đúng email | `tenant` |
| Decline invitation | User từ chối invitation | `tenant` |
| Resend invitation | Gửi lại invitation còn hợp lệ hoặc rotate token theo policy | `tenant` |
| Revoke invitation | Owner/Admin hủy invitation chưa được sử dụng | `tenant` |
| Suspend/remove member | Thu hồi quyền truy cập tenant nhưng giữ lại lịch sử nghiệp vụ | `tenant` |
| Leave tenant | User tự rời tenant nếu không phải owner cuối cùng | `tenant` |
| Transfer ownership | Chuyển ownership trước khi owner cuối rời tenant | `tenant` |
| Revoke tenant access | Vô hiệu hóa tenant context/session liên quan ngay khi membership bị thu hồi | `tenant` / `identity` |

Các role cố định trong phiên bản đầu tiên:

| Role | Quyền tổng quát |
| --- | --- |
| `OWNER` | Quản lý tenant, ownership, member và toàn bộ project |
| `ADMIN` | Quản lý member và project, không quản lý ownership |
| `MEMBER` | Làm việc trong các project được phép truy cập |
| `GUEST` | Chỉ truy cập các project hoặc task được chỉ định |

### 2.3. P1 — Account security

| Feature | Mô tả |
| --- | --- |
| Device/session list | Hiển thị các session đang hoạt động theo thiết bị |
| Revoke individual session | User thu hồi một session cụ thể |
| Temporary login throttling | Tăng delay hoặc khóa tạm thời khi đăng nhập sai nhiều lần |
| MFA with TOTP | Xác thực bước hai bằng authenticator application |
| Recovery codes | Mã khôi phục single-use, chỉ lưu dưới dạng hash |
| Step-up authentication | Yêu cầu xác thực lại trước hành động nhạy cảm |
| Security notifications | Thông báo đổi password, email, MFA hoặc đăng nhập đáng ngờ |
| Tenant-enforced MFA | Tenant yêu cầu tất cả member sử dụng MFA |

### 2.4. P1 — Social authentication

| Feature | Mô tả |
| --- | --- |
| Google login | Đăng nhập qua Google OIDC/OAuth 2.0 |
| Microsoft login | Đăng nhập qua Microsoft OIDC/OAuth 2.0 |
| Link identity | Liên kết social identity sau khi user xác thực account hiện tại |
| Unlink identity | Gỡ provider nhưng phải giữ ít nhất một login method |
| Identity collision handling | Xử lý an toàn khi social email trùng với account hiện có |

Không tự động liên kết account chỉ dựa vào email nếu chưa xác minh được email từ provider và chưa có bước xác nhận an toàn từ user.

### 2.5. P1 — Account lifecycle

| Feature | Mô tả |
| --- | --- |
| Change email | Xác thực lại user và verify email mới trước khi cập nhật |
| Disable account | Vô hiệu hóa account và thu hồi toàn bộ session |
| Delete account | Xử lý ownership, membership và retention trước khi xóa/anonymize |
| Grace period | Cho phép khôi phục account trong khoảng thời gian cấu hình |
| Data anonymization | Xóa thông tin cá nhân nhưng giữ các record nghiệp vụ cần thiết |

### 2.6. P2 — Enterprise authentication

| Feature | Mô tả |
| --- | --- |
| Domain verification | Tenant chứng minh quyền sở hữu email domain |
| OIDC SSO | Đăng nhập qua identity provider của tenant |
| SAML SSO | Hỗ trợ enterprise identity provider dùng SAML |
| SSO discovery | Tìm SSO configuration theo email/domain |
| Enforce SSO | Bắt buộc member của tenant đăng nhập qua SSO |
| JIT provisioning | Tạo membership khi user SSO đăng nhập lần đầu |
| SCIM | Provision/deprovision user và group tự động |
| Break-glass account | Admin khẩn cấp để tránh tenant bị khóa hoàn toàn |
| Custom session policy | Tenant cấu hình idle timeout và absolute lifetime |

### 2.7. P2 — Advanced authentication

- Passkey/WebAuthn.
- Passwordless magic link.
- Personal access token.
- API key và service account.
- Support impersonation có consent, thời hạn và audit đầy đủ.
- Risk-based authentication.
- Adaptive CAPTCHA hoặc bot protection.

## 3. Workflow

### 3.1. Đăng ký và tạo tenant

```text
User nhập email + password
        ↓
Validate dữ liệu và rate limit
        ↓
Email đã tồn tại?
   ├─ Có → trả response an toàn, hướng dẫn login/reset password
   └─ Không
        ↓
Tạo User ở trạng thái PENDING_VERIFICATION
        ↓
Tạo verification token và chỉ lưu token hash
        ↓
Gửi verification email
        ↓
User mở verification link
        ↓
Token hợp lệ, chưa hết hạn và chưa được sử dụng?
   ├─ Không → từ chối hoặc cho phép gửi lại email
   └─ Có
        ↓
Activate User và consume token
        ↓
Tìm invitation đang chờ theo email
   ├─ Có → hiển thị invitation để user accept/decline
   └─ Không → hiển thị onboarding tạo tenant
        ↓
Tạo Tenant + OWNER Membership trong một transaction
        ↓
Tạo session và chọn active tenant
        ↓
Redirect vào application
```

Không tạo tenant trước khi email được verify nhằm hạn chế tenant rác.

### 3.2. Đăng nhập bằng email/password

```text
User nhập email + password
        ↓
Normalize email và áp dụng rate limit
        ↓
Tìm User/Credential
        ↓
User tồn tại, active và email đã verify?
   ├─ Không → trả generic authentication error
   └─ Có
        ↓
Password hợp lệ?
   ├─ Không → ghi failed attempt, áp dụng throttle nếu cần
   └─ Có
        ↓
MFA có được yêu cầu?
   ├─ Có → tạo pre-auth challenge → verify MFA
   └─ Không
        ↓
Rotate/tạo session
        ↓
Lấy danh sách active memberships
        ↓
Số tenant có thể truy cập
   ├─ 0 → invitation/onboarding
   ├─ 1 → chọn tenant đó
   └─ Nhiều → chọn last-used tenant hoặc tenant picker
        ↓
Ghi audit event và redirect
```

Response đăng nhập sai không phân biệt email không tồn tại, password sai hay account bị disable.

### 3.3. Đăng nhập bằng social provider

```text
User chọn Google/Microsoft
        ↓
Redirect tới provider với state + nonce + PKCE
        ↓
Provider callback
        ↓
Validate state, issuer, audience, nonce và authorization code
        ↓
Lấy provider subject và verified email
        ↓
Provider identity đã tồn tại?
   ├─ Có → đăng nhập User tương ứng
   └─ Không
        ↓
Email đã thuộc User khác?
   ├─ Không → tạo User + ProviderIdentity
   └─ Có → yêu cầu login vào account hiện tại rồi xác nhận liên kết
        ↓
Resolve memberships và active tenant
        ↓
Tạo session và ghi audit event
```

### 3.4. Mời member vào tenant

```text
Owner/Admin nhập email + role
        ↓
Authenticate session và resolve tenant
        ↓
Kiểm tra permission member.invite
        ↓
Email đã có active membership?
   ├─ Có → trả lỗi MEMBER_ALREADY_EXISTS
   └─ Không
        ↓
Invitation pending đã tồn tại?
   ├─ Có → resend hoặc rotate token theo policy
   └─ Không → tạo invitation mới
        ↓
Lưu token hash + email + role + expiresAt
        ↓
Gửi invitation email
        ↓
Ghi audit event
```

Owner/Admin không được mời role cao hơn quyền mà policy cho phép. `ADMIN` không được tạo `OWNER` membership.

### 3.5. Chấp nhận invitation

```text
User mở invitation link
        ↓
Token hợp lệ, chưa hết hạn, chưa revoke và chưa consume?
   ├─ Không → từ chối
   └─ Có
        ↓
User đã đăng nhập?
   ├─ Không → login/register rồi quay lại invitation
   └─ Có
        ↓
Verified email của User khớp invited email?
   ├─ Không → yêu cầu đăng nhập/verify đúng email
   └─ Có
        ↓
Membership đã tồn tại?
   ├─ Có → không tạo duplicate, đánh dấu flow hoàn tất
   └─ Không → tạo Membership với role được mời
        ↓
Consume invitation trong cùng transaction
        ↓
Chọn tenant làm active tenant
        ↓
Ghi audit event và redirect vào tenant
```

Workflow phải idempotent: retry accept invitation không được tạo duplicate membership.

### 3.6. Chuyển active tenant

```text
User chọn tenant
        ↓
Authenticate session
        ↓
Tìm active Membership bằng userId + tenantId
        ↓
Membership tồn tại và active?
   ├─ Không → trả 403 Forbidden
   └─ Có
        ↓
Update activeTenantId/lastUsedTenantId trong session
        ↓
Load role/permissions mới
        ↓
Redirect tới tenant
```

Client có thể đề xuất `tenantId`, nhưng server phải resolve tenant từ membership của authenticated user.

### 3.7. Xử lý protected tenant request

```text
HTTP Request
   ↓
Đọc session cookie/access token
   ↓
Session hợp lệ, chưa hết hạn và chưa revoke?
   ├─ Không → 401 Unauthorized
   └─ Có
        ↓
Resolve tenant context
        ↓
User có active Membership trong tenant?
   ├─ Không → 403 Forbidden
   └─ Có
        ↓
User có permission cho action?
   ├─ Không → 403 Forbidden
   └─ Có
        ↓
Query resource với tenantId bắt buộc
        ↓
Resource thuộc tenant và user được phép truy cập?
   ├─ Không → 404 Not Found hoặc 403 theo API policy
   └─ Có → thực thi use case
```

Không query resource chỉ bằng resource ID rồi tin rằng kết quả thuộc tenant hiện tại. Tenant scope phải xuất hiện trong repository query hoặc được bảo vệ bằng ownership path rõ ràng.

### 3.8. Quên và đặt lại mật khẩu

```text
User nhập email
        ↓
Luôn trả response giống nhau
        ↓
Nếu User hợp lệ:
  tạo reset token ngắn hạn và lưu token hash
        ↓
Gửi reset email
        ↓
User mở link và nhập password mới
        ↓
Token hợp lệ, chưa hết hạn và chưa consume?
   ├─ Không → từ chối
   └─ Có
        ↓
Validate password policy
        ↓
Update password và consume token trong một transaction
        ↓
Revoke các session cũ theo security policy
        ↓
Gửi security notification và ghi audit event
```

### 3.9. Đổi mật khẩu

```text
Authenticated User nhập current password + new password
        ↓
Step-up authentication
        ↓
Current password hợp lệ?
   ├─ Không → từ chối và ghi failed event
   └─ Có
        ↓
New password đạt policy và khác password hiện tại?
   ├─ Không → từ chối
   └─ Có
        ↓
Update password
        ↓
Revoke các session khác
        ↓
Rotate session hiện tại
        ↓
Gửi security notification và ghi audit event
```

### 3.10. Logout và thu hồi session

Logout thiết bị hiện tại:

```text
Logout request
   ↓
Revoke current session
   ↓
Clear authentication cookie
   ↓
Ghi audit event
```

Logout tất cả thiết bị:

```text
User yêu cầu logout all devices
   ↓
Step-up authentication
   ↓
Revoke toàn bộ session/refresh token của User
   ↓
Clear current authentication cookie
   ↓
Ghi audit event và gửi security notification
```

### 3.11. Suspend hoặc remove member

```text
Owner/Admin chọn member
        ↓
Authenticate và kiểm tra permission member.manage
        ↓
Target là owner cuối cùng?
   ├─ Có → từ chối, yêu cầu transfer ownership
   └─ Không
        ↓
Actor có quyền thay đổi target role này?
   ├─ Không → từ chối
   └─ Có
        ↓
Suspend/deactivate Membership
        ↓
Revoke tenant access và invalidate permission cache
        ↓
Giữ nguyên historical work của member
        ↓
Ghi audit event
```

Nếu một session dùng được cho nhiều tenant, chỉ thu hồi tenant context liên quan; không bắt buộc logout user khỏi toàn hệ thống.

### 3.12. User rời tenant

```text
User chọn Leave tenant
        ↓
Xác nhận hành động
        ↓
User là owner cuối cùng?
   ├─ Có → yêu cầu transfer ownership hoặc xóa tenant
   └─ Không
        ↓
Deactivate Membership
        ↓
Revoke tenant access
        ↓
Chọn tenant khả dụng khác hoặc quay về onboarding
        ↓
Ghi audit event
```

### 3.13. Bật MFA

```text
User chọn Enable MFA
        ↓
Step-up bằng login method hiện tại
        ↓
Sinh TOTP secret tạm thời
        ↓
Hiển thị QR code
        ↓
User nhập mã TOTP để xác nhận
        ↓
Mã hợp lệ?
   ├─ Không → không activate MFA
   └─ Có
        ↓
Activate MFA
        ↓
Sinh recovery codes và chỉ lưu hash
        ↓
Hiển thị recovery codes đúng một lần
        ↓
Ghi audit event và gửi security notification
```

### 3.14. Xóa account

```text
User yêu cầu xóa account
        ↓
Step-up authentication
        ↓
User là owner cuối cùng của tenant nào không?
   ├─ Có → yêu cầu transfer ownership hoặc xóa tenant trước
   └─ Không
        ↓
Đánh dấu account DELETION_PENDING
        ↓
Revoke toàn bộ session và credential token
        ↓
Chờ grace period nếu được cấu hình
        ↓
Delete/anonymize personal data
        ↓
Giữ historical business records theo retention policy
        ↓
Ghi audit event
```

## 4. Session policy đề xuất cho phiên bản đầu tiên

Hunger nên bắt đầu với opaque server-side session và cookie có các thuộc tính:

- `HttpOnly`.
- `Secure` trong môi trường HTTPS.
- `SameSite=Lax`.
- Session ID có entropy cao và chỉ lưu hash nếu threat model yêu cầu.
- Rotate session ID sau login, password change và privilege change.
- Có idle timeout và absolute lifetime.
- Session có thể revoke ngay lập tức.

Session nên chứa hoặc tham chiếu tối thiểu tới:

- `sessionId`.
- `userId`.
- `activeTenantId` nullable.
- `createdAt`.
- `lastSeenAt`.
- `expiresAt`.
- `revokedAt` nullable.
- Metadata thiết bị ở mức tối thiểu phục vụ security history.

Server vẫn phải kiểm tra active membership khi xử lý tenant request; `activeTenantId` trong session không thay thế membership validation.

## 5. Security rules bắt buộc

- Hash password bằng Argon2id hoặc password hashing algorithm tương đương được cấu hình an toàn.
- Verification, reset-password, invitation và recovery token phải random, có expiration, single-use và chỉ lưu hash.
- Không ghi password, raw token, session cookie hoặc provider access token vào log.
- Áp dụng CSRF protection cho các state-changing request nếu authentication dựa trên cookie.
- Rate limit theo IP và account identifier cho các authentication endpoint.
- Dùng generic response cho login và forgot-password để hạn chế account enumeration.
- Rotate session sau authentication hoặc thay đổi privilege để chống session fixation.
- Revoke session sau password reset, account disable hoặc security incident theo policy.
- Mọi role, membership và invitation change phải có audit event.
- Mọi tenant-owned query phải được scope bằng `tenantId`.
- Biết UUID của resource không đồng nghĩa với việc có quyền truy cập resource.
- Owner cuối cùng không thể rời tenant, bị remove hoặc bị demote.

## 6. Trạng thái nghiệp vụ đề xuất

### User status

```text
PENDING_VERIFICATION → ACTIVE → DISABLED
                              → DELETION_PENDING → DELETED
```

### Session status

Session status có thể được suy ra từ thời gian và các field thay vì lưu enum:

```text
ACTIVE  = revokedAt is null AND expiresAt > now
EXPIRED = expiresAt <= now
REVOKED = revokedAt is not null
```

### Membership status

```text
ACTIVE → SUSPENDED
ACTIVE → LEFT
ACTIVE → REMOVED
SUSPENDED → ACTIVE
```

### Invitation status

Invitation status có thể được suy ra:

```text
PENDING  = acceptedAt/revokedAt is null AND expiresAt > now
ACCEPTED = acceptedAt is not null
REVOKED  = revokedAt is not null
EXPIRED  = expiresAt <= now
```

## 7. HTTP result convention

| Trường hợp | HTTP status đề xuất |
| --- | --- |
| Chưa đăng nhập, session thiếu hoặc không hợp lệ | `401 Unauthorized` |
| Đã đăng nhập nhưng không có membership/permission | `403 Forbidden` |
| Resource không tồn tại hoặc cần che giấu resource tenant khác | `404 Not Found` |
| Dữ liệu đầu vào không hợp lệ | `400 Bad Request` |
| Trạng thái hiện tại xung đột với operation | `409 Conflict` |
| Login/register/reset bị rate limit | `429 Too Many Requests` |

## 8. Thứ tự triển khai đề xuất

1. Register và email verification.
2. Login, logout và server-side session.
3. Forgot, reset và change password.
4. Create tenant và owner membership.
5. Resolve active tenant và switch tenant.
6. Tenant-scoped authorization với fixed roles.
7. Invitation lifecycle.
8. Suspend/remove/leave membership và revoke tenant access.
9. Session/device management và audit events.
10. Social login.
11. MFA và recovery codes.
12. Enterprise SSO và SCIM khi có nhu cầu thực tế.

Mỗi iteration phải kiểm thử ít nhất ba nhóm case: happy path, authentication/authorization failure và cross-tenant isolation failure.
