package com.engineering_lab.hunger.common.security.jwt;

  import static org.junit.jupiter.api.Assertions.assertEquals;
  import static org.junit.jupiter.api.Assertions.assertNotNull;

  import java.io.IOException;
  import java.time.Duration;
  import java.time.Instant;
  import java.util.List;
  import java.util.UUID;

  import org.junit.jupiter.api.BeforeEach;
  import org.junit.jupiter.api.Test;
  import org.springframework.core.io.ClassPathResource;
  import org.springframework.security.oauth2.jwt.Jwt;
  import org.springframework.security.oauth2.jwt.JwtClaimsSet;
  import org.springframework.security.oauth2.jwt.JwtDecoder;
  import org.springframework.security.oauth2.jwt.JwtEncoder;
  import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

  class JwtConfigurationTest {

      private static final String ISSUER = "https://hunger.local";
      private static final String AUDIENCE = "hunger-api";

      private static final UUID USER_ID = UUID.fromString(
              "01890f9a-6b7c-7def-8123-456789abcdef"
      );

      private static final UUID SESSION_ID = UUID.fromString(
              "01890f9a-6b7c-7def-9234-56789abcdef0"
      );

      private JwtEncoder encoder;
      private JwtDecoder decoder;

      @BeforeEach
      void setUp() throws IOException {
          JwtProperties properties = new JwtProperties(
                  ISSUER,
                  AUDIENCE,
                  Duration.ofMinutes(10),
                  new ClassPathResource(
                          "keys/test-access-token-private.pem"
                  ),
                  new ClassPathResource(
                          "keys/test-access-token-public.pem"
                  )
          );

          JwtConfiguration configuration =
                  new JwtConfiguration();

          var publicKey = configuration.jwtPublicKey(properties);
          var privateKey = configuration.jwtPrivateKey(properties);

          encoder = configuration.jwtEncoder(
                  publicKey,
                  privateKey
          );

          decoder = configuration.jwtDecoder(
                  publicKey,
                  properties
          );
      }

      @Test
      void encodesAndDecodesAccessTokenUsingConfiguredKeyPair() {
          Instant issuedAt = Instant.now();
          Instant expiresAt = issuedAt.plusSeconds(600);

          JwtClaimsSet claims = JwtClaimsSet.builder()
                  .issuer(ISSUER)
                  .audience(List.of(AUDIENCE))
                  .subject(USER_ID.toString())
                  .issuedAt(issuedAt)
                  .expiresAt(expiresAt)
                  .claim("sid", SESSION_ID.toString())
                  .build();

          Jwt encoded = encoder.encode(
                  JwtEncoderParameters.from(claims)
          );

          Jwt decoded = decoder.decode(
                  encoded.getTokenValue()
          );

          assertNotNull(encoded.getTokenValue());
          assertEquals(ISSUER, decoded.getIssuer().toString());
          assertEquals(List.of(AUDIENCE), decoded.getAudience());
          assertEquals(USER_ID.toString(), decoded.getSubject());
          assertEquals(
                  SESSION_ID.toString(),
                  decoded.getClaimAsString("sid")
          );
      }
  }
