package no.nav.testnav.libs.servletsecurity.domain;

import lombok.Data;

@Data
@Deprecated
public class ClientCredential {
    final String clientId;
    final String clientSecret;

    @Override
    public final String toString() {
        return "ClientCredential{" +
                "clientId=[HIDDEN]" +
                ", clientSecret=[HIDDEN]" +
                '}';
    }
}
