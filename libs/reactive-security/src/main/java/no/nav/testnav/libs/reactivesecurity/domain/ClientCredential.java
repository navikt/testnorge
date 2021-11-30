package no.nav.testnav.libs.reactivesecurity.domain;

import lombok.Data;

@Data
public class ClientCredential {
    private final String clientId;
    private final String clientSecret;

    @Override
    public final String toString() {
        return "ClientCredential{" +
                "clientId=[HIDDEN]" +
                ", clientSecret=[HIDDEN]" +
                '}';
    }
}
