package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class InaktiverResponse extends ArenaResponse {

    @Builder
    public InaktiverResponse(HttpStatus status, String miljoe, String feilmelding) {
        super(status, miljoe, feilmelding);
    }
}
