package no.nav.testnav.libs.securitycore.texas;

import com.nimbusds.jwt.JWTParser;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Map;

import static java.util.Objects.isNull;

public class TexasJwtDecoder implements ReactiveJwtDecoder {

    private static final MappedJwtClaimSetConverter CLAIM_SET_CONVERTER = MappedJwtClaimSetConverter.withDefaults(Map.of());

    private final TexasClient texasClient;
    private final Map<String, String> identityProviderByIssuer;

    public TexasJwtDecoder(TexasClient texasClient, Map<String, String> identityProviderByIssuer) {
        this.texasClient = texasClient;
        this.identityProviderByIssuer = identityProviderByIssuer;
    }

    @Override
    public Mono<Jwt> decode(String token) {
        try {
            var parsed = JWTParser.parse(token);
            var claimsSet = parsed.getJWTClaimsSet();
            var identityProvider = isNull(claimsSet) ? null : identityProviderByIssuer.get(claimsSet.getIssuer());
            if (isNull(identityProvider)) {
                return Mono.error(new BadJwtException("Ukjent issuer"));
            }
            return texasClient
                    .introspect(identityProvider, token)
                    .map(claims -> Jwt.withTokenValue(token)
                            .headers(headers -> headers.putAll(parsed.getHeader().toJSONObject()))
                            .claims(jwtClaims -> {
                                jwtClaims.putAll(CLAIM_SET_CONVERTER.convert(claims));
                                jwtClaims.remove("active");
                            })
                            .build());
        } catch (ParseException e) {
            return Mono.error(new BadJwtException("Token kan ikke leses", e));
        }
    }
}
