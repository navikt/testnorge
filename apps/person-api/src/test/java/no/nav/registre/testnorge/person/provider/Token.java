package no.nav.registre.testnorge.person.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
class Token {
    LocalDateTime expires_in;
    String access_token;
}
