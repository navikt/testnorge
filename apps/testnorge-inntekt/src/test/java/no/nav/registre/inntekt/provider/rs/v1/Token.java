package no.nav.registre.inntekt.provider.rs.v1;

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
