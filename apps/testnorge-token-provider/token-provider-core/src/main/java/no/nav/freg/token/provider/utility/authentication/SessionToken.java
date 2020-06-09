package no.nav.freg.token.provider.utility.authentication;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor class SessionToken {

    private String tokenId;
    private String successUrl;
}
