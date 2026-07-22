# JWT Configuration

## 1. Mục tiêu

Tài liệu này mô tả cách Hunger cấu hình JSON Web Token (JWT) cho access token bằng Spring Security.

Phạm vi hiện tại:

- Access token là JWT có thời hạn ngắn.
- JWT được ký bằng RSA SHA-256 (`RS256`).
- Private key chỉ dùng để ký token.
- Public key dùng để xác minh chữ ký token.
- Refresh token và `user_sessions` không thuộc phạm vi của tài liệu này.

Luồng tổng quát:

```text
Login thành công
    ↓
JwtEncoder + RSA private key
    ↓
Access JWT
    ↓
Client gửi Authorization: Bearer <token>
    ↓
JwtDecoder + RSA public key
    ↓
Spring Security tạo Authentication
```

## 2. Vì sao sử dụng RSA

RSA sử dụng hai key khác nhau:

| Key | Trách nhiệm | Nơi được phép sử dụng |
| --- | --- | --- |
| Private key | Ký access token | Thành phần phát hành token |
| Public key | Xác minh chữ ký | Resource server/API |

Private key phải được bảo vệ như một secret. Public key không phải secret và có thể được phân phối cho các service cần xác minh JWT.

Không sử dụng private key để xác minh token trong các service chỉ có trách nhiệm đọc JWT. Việc phân tách key giúp giảm phạm vi ảnh hưởng nếu một resource service bị xâm nhập.

## 3. Tạo RSA key pair cho local development

### 3.1. Chuẩn bị thư mục

Key local được đặt trong `.local/keys` và `.local/` phải có trong `.gitignore`:

```gitignore
.local/
```

Tạo thư mục:

```bash
mkdir -p .local/keys
```

### 3.2. Tạo private key

```bash
openssl genpkey \
  -algorithm RSA \
  -pkeyopt rsa_keygen_bits:2048 \
  -out .local/keys/access-token-private.pem
```

Private key được tạo ở định dạng PKCS#8:

```text
-----BEGIN PRIVATE KEY-----
...
-----END PRIVATE KEY-----
```

Giới hạn quyền đọc private key:

```bash
chmod 600 .local/keys/access-token-private.pem
```

### 3.3. Tạo public key

Sinh public key từ private key:

```bash
openssl pkey \
  -in .local/keys/access-token-private.pem \
  -pubout \
  -out .local/keys/access-token-public.pem
```

Public key có định dạng X.509 SubjectPublicKeyInfo:

```text
-----BEGIN PUBLIC KEY-----
...
-----END PUBLIC KEY-----
```

### 3.4. Kiểm tra key

Kiểm tra private key:

```bash
openssl pkey \
  -in .local/keys/access-token-private.pem \
  -check \
  -noout
```

Kết quả mong đợi:

```text
Key is valid
```

Kiểm tra public key:

```bash
openssl pkey \
  -pubin \
  -in .local/keys/access-token-public.pem \
  -text \
  -noout
```

Không đưa nội dung private key vào log, issue, tài liệu, commit hoặc tin nhắn.

## 4. Application properties

Khai báo cấu hình JWT trong `application.properties`:

```properties
security.jwt.issuer=hunger
security.jwt.audience=hunger-api
security.jwt.access-token-ttl=10m

security.jwt.private-key-location=${JWT_PRIVATE_KEY_LOCATION:file:.local/keys/access-token-private.pem}
security.jwt.public-key-location=${JWT_PUBLIC_KEY_LOCATION:file:.local/keys/access-token-public.pem}
```

Ý nghĩa:

| Property | Ý nghĩa |
| --- | --- |
| `issuer` | Định danh thành phần phát hành JWT; được ghi vào claim `iss` |
| `audience` | Định danh API nhận JWT; được ghi vào claim `aud` |
| `access-token-ttl` | Thời gian sống của access token |
| `private-key-location` | Resource chứa PKCS#8 private key |
| `public-key-location` | Resource chứa X.509 public key |

Giá trị `file:.local/...` chỉ là mặc định cho local development. Môi trường triển khai phải truyền vị trí key bằng environment variable:

```text
JWT_PRIVATE_KEY_LOCATION
JWT_PUBLIC_KEY_LOCATION
```

Ví dụ khi key được mount vào container:

```text
JWT_PRIVATE_KEY_LOCATION=file:/run/secrets/access-token-private.pem
JWT_PUBLIC_KEY_LOCATION=file:/run/secrets/access-token-public.pem
```

## 5. Bind cấu hình với `JwtProperties`

```java
package com.engineering_lab.hunger.common.security.jwt;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotNull Duration accessTokenTtl,
        @NotNull Resource privateKeyLocation,
        @NotNull Resource publicKeyLocation
) {
}
```

Spring Boot tự ánh xạ kebab-case sang camelCase, ví dụ:

```text
security.jwt.access-token-ttl → accessTokenTtl
```

Kiểu `Resource` hỗ trợ các location như:

```text
file:.local/keys/key.pem
file:/run/secrets/key.pem
classpath:keys/key.pem
```

## 6. Tạo RSA key beans

`JwtConfiguration` đăng ký `JwtProperties`:

```java
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {
    // Beans
}
```

Đọc public key X.509:

```java
@Bean
RSAPublicKey jwtPublicKey(JwtProperties properties) throws IOException {
    try (InputStream inputStream = properties
            .publicKeyLocation()
            .getInputStream()) {

        return RsaKeyConverters
                .x509()
                .convert(inputStream);
    }
}
```

Đọc private key PKCS#8:

```java
@Bean
RSAPrivateKey jwtPrivateKey(JwtProperties properties) throws IOException {
    try (InputStream inputStream = properties
            .privateKeyLocation()
            .getInputStream()) {

        return RsaKeyConverters
                .pkcs8()
                .convert(inputStream);
    }
}
```

Định dạng PEM phải khớp với converter:

```text
BEGIN PUBLIC KEY  → RsaKeyConverters.x509()
BEGIN PRIVATE KEY → RsaKeyConverters.pkcs8()
```

## 7. Cấu hình `JwtEncoder`

```java
@Bean
JwtEncoder jwtEncoder(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey
) {
    return NimbusJwtEncoder
            .withKeyPair(publicKey, privateKey)
            .build();
}
```

`JwtEncoder` được token service sử dụng để:

1. Tạo JWT claims.
2. Tạo JOSE header với thuật toán `RS256`.
3. Ký token bằng private key.

Private key không được gửi cho client và không được ghi vào JWT.

## 8. Cấu hình `JwtDecoder`

```java
@Bean
JwtDecoder jwtDecoder(
        RSAPublicKey publicKey,
        JwtProperties properties
) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withPublicKey(publicKey)
            .build();

    OAuth2TokenValidator<Jwt> defaultValidator =
            JwtValidators.createDefaultWithIssuer(
                    properties.issuer()
            );

    OAuth2TokenValidator<Jwt> audienceValidator =
            audienceValidator(properties.audience());

    decoder.setJwtValidator(
            new DelegatingOAuth2TokenValidator<>(
                    defaultValidator,
                    audienceValidator
            )
    );

    return decoder;
}
```

Audience validator:

```java
private OAuth2TokenValidator<Jwt> audienceValidator(
        String requiredAudience
) {
    return jwt -> {
        if (jwt.getAudience().contains(requiredAudience)) {
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "The required audience is missing",
                null
        );

        return OAuth2TokenValidatorResult.failure(error);
    };
}
```

Decoder có các trách nhiệm:

- Xác minh chữ ký bằng public key.
- Từ chối token hết hạn qua claim `exp`.
- Từ chối token chưa có hiệu lực qua claim `nbf` nếu claim này tồn tại.
- Kiểm tra claim `iss` bằng issuer validator.
- Kiểm tra claim `aud` bằng audience validator.

## 9. Access-token claims ban đầu

Claims tối thiểu dự kiến:

```json
{
  "iss": "hunger",
  "aud": ["hunger-api"],
  "sub": "019...user-uuid-v7",
  "sid": "019...session-uuid-v7",
  "jti": "019...token-uuid-v7",
  "iat": "2026-07-22T13:00:00Z",
  "exp": "2026-07-22T13:10:00Z"
}
```

| Claim | Mục đích |
| --- | --- |
| `iss` | Xác định Hunger là issuer |
| `aud` | Chỉ cho phép token được dùng tại Hunger API |
| `sub` | User ID; Spring ánh xạ thành `Authentication#getName()` |
| `sid` | Liên kết token với logical session trong `user_sessions` |
| `jti` | Định danh duy nhất của access token |
| `iat` | Thời điểm phát hành |
| `exp` | Thời điểm hết hạn |

Tenant, membership, role và permission chưa được đưa vào token ở giai đoạn cấu hình JWT cơ bản. Các claim đó cần được quyết định sau khi có chiến lược tenant context và authorization rõ ràng.

## 10. Tích hợp với `SecurityFilterChain`

Khi application có một bean `JwtDecoder`, cấu hình sau sẽ dùng bean đó:

```java
.oauth2ResourceServer(oauth2 -> oauth2
        .jwt(Customizer.withDefaults())
)
```

Request hợp lệ:

```http
GET /api/v1/tenants HTTP/1.1
Authorization: Bearer <access-token>
```

Spring Security thực hiện:

```text
BearerTokenAuthenticationFilter
    ↓
JwtDecoder
    ↓
Signature + claims validation
    ↓
JwtAuthenticationToken
    ↓
SecurityContextHolder
```

Không cần tạo custom `OncePerRequestFilter` chỉ để parse access JWT.

## 11. Test keys

`.local/keys` không được commit nên CI không thể sử dụng các key local. Test suite cần một trong các chiến lược riêng:

1. Đặt một RSA key pair chỉ dành cho test trong `src/test/resources`.
2. Tạo key pair động trong test configuration.

Test private key không phải production secret, nhưng phải được đặt tên và ghi chú rõ rằng nó chỉ được dùng cho automated tests.

Các test tối thiểu cần có:

- Encoder tạo được JWT và decoder đọc được token đó.
- Token có issuer sai bị từ chối.
- Token thiếu audience hoặc audience sai bị từ chối.
- Token hết hạn bị từ chối.
- Token được ký bằng private key khác bị từ chối.

## 12. Production checklist

- Không commit production private key.
- Mount key từ secret manager hoặc container secret.
- Chỉ authentication/token-issuing component được đọc private key.
- Giới hạn filesystem permission của private key.
- Không ghi raw JWT hoặc key material vào log.
- Access token có thời hạn ngắn.
- Validate ít nhất signature, `exp`, `iss` và `aud`.
- Có kế hoạch key rotation và `kid` trước khi vận hành nhiều key đồng thời.
- Không tái sử dụng test/local key trong production.

