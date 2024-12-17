package no.nav.testnav.libs.securitycore.domain.azuread;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        return "ClientCredential{clientId=[HIDDEN],clientSecret=[HIDDEN]}";
    }

}
