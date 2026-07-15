# Project Structure

## 1. Mục tiêu kiến trúc

Hunger được xây dựng theo hướng **modular monolith**: toàn bộ ứng dụng được triển khai như một service duy nhất, nhưng source code được chia thành các module dựa trên nghiệp vụ.

Cách tiếp cận này hướng tới các mục tiêu:

- Giữ code của từng nghiệp vụ ở gần nhau.
- Làm rõ ranh giới và dependency giữa các module.
- Cho phép phát triển, kiểm thử và refactor từng module tương đối độc lập.
- Tránh độ phức tạp vận hành của microservices khi sản phẩm chưa cần đến.
- Giữ domain model độc lập với Spring Data, Hibernate và cách dữ liệu được lưu trữ.
- Tách Domain Entity khỏi JPA Entity để persistence không chi phối business model.
- Áp dụng Clean Architecture có chủ đích, chỉ tạo abstraction tại các boundary thực sự cần thiết.

Hunger tiếp tục là modular monolith cho đến khi có bằng chứng rõ ràng rằng việc tách service giúp cải thiện ownership, khả năng scale hoặc độ tin cậy.

## 2. Nguyên tắc tổ chức source code

Source code được chia **theo business module**, không chia toàn bộ project theo technical layer.

Không sử dụng cấu trúc top-level như sau:

```text
controllers/
services/
repositories/
entities/
```

Cấu trúc trên làm code của một feature nằm rải rác ở nhiều nơi. Thay vào đó, mỗi module tự chứa controller, application service, domain model và persistence implementation của chính nó.

Các module dự kiến của Hunger:

| Module | Trách nhiệm |
| --- | --- |
| `identity` | User identity, đăng ký, đăng nhập, mật khẩu và session |
| `tenant` | Tenant, membership, invitation và tenant-scoped authorization |
| `project` | Project, task, comment và quy trình làm việc |
| `attachment` | Metadata file, quyền truy cập và object storage |
| `activity` | Lịch sử các hành động quan trọng trong tenant |
| `notification` | In-app notification, email và notification preference |

Chỉ tạo module khi đã có nghiệp vụ tương ứng. Không cần tạo sẵn tất cả module ngay từ đầu.

## 3. Cấu trúc tổng thể

```text
src/
├── main/
│   ├── java/com/engineering_lab/hunger/
│   │   ├── HungerApplication.java
│   │   ├── identity/
│   │   │   ├── api/
│   │   │   ├── application/{command,result}/
│   │   │   ├── domain/{model,repository,service,exception}/
│   │   │   ├── infrastructure/
│   │   │   │   ├── persistence/{entity,repository,mapper,adapter}/
│   │   │   │   └── security/
│   │   │   └── web/dto/
│   │   ├── tenant/
│   │   │   ├── api/
│   │   │   ├── application/{command,result}/
│   │   │   ├── domain/{model,repository,service,exception}/
│   │   │   ├── infrastructure/
│   │   │   │   └── persistence/{entity,repository,mapper,adapter}/
│   │   │   └── web/dto/
│   │   ├── project/
│   │   │   ├── api/
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   ├── infrastructure/
│   │   │   └── web/
│   │   └── shared/
│   │       ├── exception/
│   │       ├── persistence/
│   │       ├── security/
│   │       └── web/
│   └── resources/
│       ├── application.properties
│       └── db/migration/
└── test/
    └── java/com/engineering_lab/hunger/
```

Các module trước mắt:

```text
identity
├── domain/model/User
├── domain/model/UserSession
├── infrastructure/persistence/entity/UserJpaEntity
└── infrastructure/persistence/entity/UserSessionJpaEntity

tenant
├── domain/model/Tenant
├── domain/model/Membership
├── infrastructure/persistence/entity/TenantJpaEntity
├── infrastructure/persistence/entity/MembershipJpaEntity
└── Invitation (ở iteration sau)
```

Tên `Tenant` được sử dụng thống nhất thay cho `Workspace` trong source code, database và API mới.

## 4. Cấu trúc bên trong một module

Mỗi module có thể chứa năm package sau:

```text
module/
├── api/
├── application/
├── domain/
├── infrastructure/
└── web/
```

Không bắt buộc tạo package rỗng. Chỉ thêm package khi module có code thuộc trách nhiệm đó.

### 4.1. `domain`

`domain` chứa business model và business rule cốt lõi, hoàn toàn độc lập với JPA/Hibernate:

- Entity và value object.
- Enum có ý nghĩa nghiệp vụ.
- Domain service khi một rule không thuộc riêng một Entity.
- Repository port cần thiết cho application layer.
- Domain exception.

Ví dụ:

```text
tenant/domain/
├── model/
│   ├── Tenant.java
│   ├── Membership.java
│   ├── TenantId.java
│   ├── MembershipId.java
│   ├── MembershipRole.java
│   └── MembershipStatus.java
├── repository/
│   ├── TenantRepository.java
│   └── MembershipRepository.java
└── exception/
```

Domain Entity không chứa `@Entity`, `@Table`, `@Column`, `@ManyToOne` hoặc import từ `jakarta.persistence`:

```java
public class Tenant {
    private final UUID id;
    private String name;
    private String slug;

    public static Tenant create(String name, String slug) {
        return new Tenant(UUID.randomUUID(), name, slug);
    }

    public void rename(String newName) {
        // Validate and enforce business rules
        this.name = newName;
    }
}
```

Domain nên tự có identity trước khi được persist. Với UUID, factory method của domain có thể tạo ID bằng `UUID.randomUUID()`; JPA Entity không cần phụ thuộc vào `@GeneratedValue`.

Domain model không biết table name, column name, foreign key, lazy loading hoặc persistence context. Nó có thể được unit test mà không khởi động Spring.

Domain Entity nên chứa hành vi nghiệp vụ thay vì chỉ là tập hợp getter và setter:

```java
membership.changeRole(newRole);
session.revoke(revokedAt);
tenant.rename(newName);
```

Domain Entity nên kiểm soát việc thay đổi state bằng constructor, factory method và business method; không cung cấp setter công khai cho mọi field.

### 4.2. `application`

`application` chứa các use case mà hệ thống cung cấp:

- Điều phối domain object.
- Kiểm soát transaction bằng `@Transactional`.
- Gọi repository và external port.
- Chuyển command thành business operation.
- Trả result hoặc DTO độc lập với HTTP.

Ví dụ:

```text
tenant/application/
├── CreateTenantService.java
├── AddMemberService.java
├── ChangeMemberRoleService.java
├── command/
│   └── CreateTenantCommand.java
└── result/
    └── CreatedTenant.java
```

Một application service thường đại diện cho một use case:

```java
@Service
@RequiredArgsConstructor
public class CreateTenantService {

    private final TenantRepository tenantRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public UUID execute(CreateTenantCommand command) {
        Tenant tenant = Tenant.create(command.name(), command.slug());
        tenantRepository.save(tenant);

        Membership owner = Membership.createOwner(
            tenant.getId(),
            command.creatorId()
        );
        membershipRepository.save(owner);

        return tenant.getId();
    }
}
```

Application service không xử lý `HttpServletRequest`, HTTP status hoặc serialize JSON.

### 4.3. `web`

`web` là inbound adapter cho HTTP:

- REST controller.
- Request và response DTO.
- Bean Validation cho input.
- Chuyển HTTP request thành application command.
- Chuyển kết quả use case thành HTTP response.

```text
tenant/web/
├── TenantController.java
└── dto/
    ├── CreateTenantRequest.java
    └── CreateTenantResponse.java
```

Controller phải mỏng. Business rule không được đặt trong controller.

Không trả JPA Entity trực tiếp qua API. Response DTO giúp tránh:

- Làm lộ field nội bộ hoặc field nhạy cảm.
- Lazy loading exception.
- Vòng lặp JSON từ relationship hai chiều.
- API contract thay đổi ngoài ý muốn khi database model thay đổi.

### 4.4. `infrastructure`

`infrastructure` chứa implementation phụ thuộc công nghệ:

- JPA Entity và Hibernate mapping.
- Spring Data repository và custom query.
- Mapper giữa Domain Entity và JPA Entity.
- Repository adapter implement domain repository port.
- Password hashing.
- JWT hoặc token provider.
- Email provider.
- S3-compatible object storage.
- Redis và message broker khi được bổ sung.

```text
identity/infrastructure/
├── persistence/
│   ├── entity/
│   │   ├── UserJpaEntity.java
│   │   └── UserSessionJpaEntity.java
│   ├── repository/
│   │   ├── JpaUserRepository.java
│   │   └── JpaUserSessionRepository.java
│   ├── mapper/
│   │   ├── UserPersistenceMapper.java
│   │   └── UserSessionPersistenceMapper.java
│   └── adapter/
│       ├── UserRepositoryAdapter.java
│       └── UserSessionRepositoryAdapter.java
└── security/
    ├── BCryptPasswordHasher.java
    └── JwtTokenProvider.java
```

Repository interface tối thiểu mà application cần được đặt trong `domain`:

```java
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

JPA Entity chỉ mô tả persistence mapping và không chứa business behavior:

```java
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
```

Spring Data repository làm việc với JPA Entity, không làm việc trực tiếp với Domain Entity:

```java
public interface JpaUserRepository
        extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);
}
```

Persistence mapper chuyển đổi hai chiều:

```java
@Component
public class UserPersistenceMapper {
    UserJpaEntity toJpaEntity(User user) { /* ... */ }
    User toDomain(UserJpaEntity entity) { /* ... */ }
}
```

Repository adapter implement domain repository port và điều phối Spring Data cùng mapper:

```java
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = jpaRepository.save(mapper.toJpaEntity(user));
        return mapper.toDomain(saved);
    }
}
```

Application chỉ phụ thuộc `UserRepository`. Nó không biết `JpaUserRepository`, `UserJpaEntity`, mapper hoặc Hibernate tồn tại.

### 4.5. `api`

`api` là public contract để module khác sử dụng. Package này có thể chứa:

- Facade hoặc query interface.
- Command/result dùng giữa các module.
- Event contract.
- Read-only projection được công khai cho module khác.

Ví dụ module `project` cần kiểm tra quyền trong tenant:

```java
public interface TenantAccessQuery {
    boolean canCreateProject(UUID tenantId, UUID userId);
}
```

Module `project` được phép phụ thuộc vào:

```java
import com.engineering_lab.hunger.tenant.api.TenantAccessQuery;
```

Module `project` không nên phụ thuộc trực tiếp vào:

```java
import com.engineering_lab.hunger.tenant.domain.repository.MembershipRepository;
```

Quy tắc này ngăn module bên ngoài biết cách module tenant lưu trữ và xử lý dữ liệu nội bộ.

## 5. Dependency rules

Dependency flow bên trong một module:

```text
web ───────────────> application ───────────────> domain
                                                    ^
                                                    │
infrastructure ──────────────────────────────────────┘

infrastructure ──> JPA/Hibernate/Spring Data/external services
```

Các quy tắc chính:

1. `domain` không phụ thuộc Spring, JPA, Hibernate, `web` hoặc `infrastructure`.
2. `application` không biết HTTP, JSON hoặc chi tiết database.
3. `web` gọi application use case, không truy cập repository trực tiếp.
4. `infrastructure` phụ thuộc vào domain để implement repository và external port.
5. Module khác chỉ giao tiếp qua package `api` của module được gọi.
6. `shared` không được trở thành nơi chứa business logic dùng chung một cách tùy tiện.

Java package chưa tự cưỡng chế hoàn toàn các quy tắc trên. Có thể bổ sung ArchUnit test khi số lượng module và developer tăng lên.

## 6. Relationship giữa các module

Domain model không sử dụng JPA relationship. Relationship trong domain được thể hiện bằng identity của aggregate hoặc Entity liên quan:

```java
public class Membership {
    private final UUID id;
    private final UUID tenantId;
    private final UUID userId;
    private MembershipRole role;
    private MembershipStatus status;
}
```

JPA Entity có thể map các giá trị này thành column UUID trực tiếp:

```java
@Column(name = "tenant_id", nullable = false)
private UUID tenantId;

@Column(name = "user_id", nullable = false)
private UUID userId;
```

Khi một query thực sự cần JPA relationship, relationship đó chỉ tồn tại trong persistence model. Mapper vẫn chuyển kết quả về ID hoặc domain object phù hợp:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "tenant_id", nullable = false)
private TenantJpaEntity tenant;
```

Không đưa `TenantJpaEntity` hoặc Hibernate proxy vào Domain Entity. Ưu tiên mapping bằng ID nếu relationship object không mang lại giá trị rõ ràng cho query.

Database vẫn phải bảo vệ referential integrity:

```sql
ALTER TABLE memberships
ADD CONSTRAINT fk_memberships_user
FOREIGN KEY (user_id) REFERENCES users (id);
```

Cách tiếp cận tách model này:

- Giữ domain độc lập với ORM.
- Giảm coupling giữa module identity và tenant.
- Tránh cascade ngoài ý muốn.
- Tránh object graph Hibernate quá lớn.
- Giữ module boundary rõ ràng.
- Cho phép persistence model được tối ưu cho query mà không làm méo domain model.

Database vẫn là nơi cưỡng chế foreign key và unique constraint. Domain/application chịu trách nhiệm business rule; mapper và repository adapter chịu trách nhiệm đi qua ranh giới persistence.

## 7. Persistence và database migration

Hibernate và Flyway có trách nhiệm khác nhau:

- Hibernate ánh xạ JPA Entity trong `infrastructure/persistence/entity` với database và thực hiện persistence.
- Flyway quản lý lịch sử thay đổi database schema.
- Hibernate chỉ validate JPA Entity có khớp schema sau khi Flyway chạy.

Cấu hình mục tiêu:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

Migration được giữ tập trung vì ứng dụng đang sử dụng một PostgreSQL schema chung:

```text
src/main/resources/db/migration/
├── V1__create_users.sql
├── V2__create_sessions.sql
├── V3__create_tenants.sql
├── V4__create_memberships.sql
└── V5__create_invitations.sql
```

Không sửa migration đã chạy ở môi trường dùng chung. Mỗi thay đổi schema phải được thêm bằng migration mới.

Flyway migration có thể chứa foreign key xuyên module. Module boundary là ranh giới trong source code và business ownership; nó không bắt buộc mỗi module phải có database hoặc schema riêng.

Luồng persistence đầy đủ:

```text
Application service
    ↓
Domain repository port
    ↓
Repository adapter
    ↓
Domain Entity ↔ Persistence mapper ↔ JPA Entity
    ↓
Spring Data JPA / Hibernate
    ↓
PostgreSQL
```

Quy ước cho dữ liệu thời gian:

| Ý nghĩa | Domain/JPA Java type | PostgreSQL type |
| --- | --- | --- |
| Một thời điểm tuyệt đối như `createdAt`, `expiresAt` | `Instant` | `TIMESTAMPTZ` |
| Chỉ có ngày như `dueDate` | `LocalDate` | `DATE` |
| Giờ trong ngày như giờ gửi thông báo | `LocalTime` | `TIME` |
| Ngày giờ địa phương không có timezone | `LocalDateTime` | `TIMESTAMP WITHOUT TIME ZONE` |

Ưu tiên `Instant` + `TIMESTAMPTZ` cho audit timestamp, session expiry và các sự kiện đã xảy ra. Nếu nghiệp vụ cần giữ timezone do người dùng lựa chọn, lưu thêm IANA zone ID như `Asia/Ho_Chi_Minh` trong một column riêng.

## 8. Transaction boundary

Transaction thường bắt đầu tại application service:

```java
@Transactional
public UUID execute(CreateTenantCommand command) {
    // Complete one business use case atomically
}
```

Không đặt `@Transactional` tại controller. Domain Entity cũng không tự mở transaction.

Một transaction nên bao phủ một use case cần tính nguyên tử, ví dụ tạo Tenant và Membership owner phải cùng thành công hoặc cùng rollback.

Các external side effect như gửi email không nên được xem là một phần atomic của database transaction. Khi yêu cầu độ tin cậy cao hơn, sử dụng transactional outbox thay vì giữ database transaction mở trong lúc gọi external service.

## 9. Testing strategy

Test được tổ chức tương ứng với source module:

```text
src/test/java/com/engineering_lab/hunger/
├── identity/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/persistence/
│   └── web/
└── tenant/
    ├── domain/
    ├── application/
    ├── infrastructure/persistence/
    └── web/
```

Các nhóm test chính:

- Domain unit test: kiểm tra business rule, không khởi động Spring context.
- Application test: kiểm tra use case với fake/mock port khi phù hợp.
- Persistence mapper unit test: kiểm tra mapping Domain Entity và JPA Entity theo cả hai chiều.
- Repository adapter integration test: chạy với PostgreSQL/Testcontainers.
- Web integration test: kiểm tra HTTP contract và security boundary.
- Migration test: khởi tạo database mới hoàn toàn từ Flyway migrations.

Không dùng H2 làm bằng chứng duy nhất cho PostgreSQL behavior. H2 phù hợp cho test đơn giản, nhưng PostgreSQL/Testcontainers cần được dùng cho migration, constraint, index và query đặc thù PostgreSQL.

## 10. Những abstraction chưa cần thiết

Để giữ kiến trúc thực tế, chưa cần áp dụng các cấu trúc sau nếu chưa có vấn đề cụ thể:

- Tạo interface cho mọi service chỉ có một implementation.
- Tạo generic repository hoặc generic CRUD service.
- Tạo mapper ngoài các boundary cần thiết như Domain Entity và JPA Entity.
- Dùng domain event cho mọi method call nội bộ.
- Chia ngay thành nhiều Gradle subproject.
- Đưa mọi class được dùng hai lần vào `shared`.

Việc tách Domain Entity và JPA Entity là quyết định kiến trúc của project. Ngoài boundary này, chỉ tạo abstraction khi có khả năng thay đổi hoặc cần cô lập khi kiểm thử, ví dụ repository, password hasher, token provider, email sender, object storage, clock và API giao tiếp giữa module.

## 11. Khi nào nên tách Gradle module

Package-based module là đủ cho giai đoạn đầu. Có thể chuyển sang multi-module Gradle khi xuất hiện một hoặc nhiều nhu cầu:

- Package convention không còn đủ để ngăn dependency sai.
- Build time cần được tối ưu theo module.
- Một module có lifecycle hoặc nhóm sở hữu độc lập.
- Cần tái sử dụng module trong application khác.
- Cần cưỡng chế dependency boundary ở compile time.

Việc tách Gradle module không đồng nghĩa với tách microservice. Ứng dụng vẫn có thể được build và deploy thành một executable duy nhất.

## 12. Quy ước thực hành

1. Bắt đầu từ business use case, sau đó xác định module sở hữu use case đó.
2. Đặt business rule trong domain Entity hoặc domain service phù hợp.
3. Đặt orchestration và transaction trong application service.
4. Giữ controller mỏng và không trả Entity trực tiếp.
5. Chỉ public contract cần thiết qua package `api`.
6. Domain biểu diễn relationship bằng identity; JPA relationship chỉ tồn tại trong persistence model khi thực sự hữu ích.
7. Mọi database schema change phải đi qua Flyway.
8. Không sử dụng `ddl-auto=update` ngoài thử nghiệm cục bộ có chủ ý.
9. Giữ package `shared` nhỏ và không chứa nghiệp vụ riêng của module.
10. Mọi JPA Entity phải có mapper và được truy cập thông qua repository adapter; không đưa JPA Entity ra application hoặc web layer.
