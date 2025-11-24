package no.nav.testnav.libs.securitycore.domain.azuread;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
@Getter
@EqualsAndHashCode
public class ClientCredential {

    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;

    @Override
    public final String toString() {
        return "%s{tokenEndpoint=%s,clientId=[%s],clientSecret=[%s]}"
                .formatted(
                        getClass().getSimpleName(),
                        tokenEndpoint,
                        maskClientId(clientId),
                        maskClientSecret(clientSecret)
                );
    }

    private static String maskClientId(String clientId) {
        if (!StringUtils.hasText(clientId) || clientId.length() <= 2) {
            return clientId;
        }
        var firstChar = clientId.charAt(0);
        var lastChar = clientId.charAt(clientId.length() - 1);
        return firstChar + "*".repeat(clientId.length() - 2) + lastChar;
    }

    private static String maskClientSecret(String clientSecret) {
        if (!StringUtils.hasText(clientSecret)) {
            return clientSecret; // Return as is if null or empty
        }
        return "*".repeat(clientSecret.length());
    }

}
