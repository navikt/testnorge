package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RsAvsendersystem {
    @JsonProperty
    private String systemnavn;
    @JsonProperty
    private String systemversjon;
    @JsonProperty
    private LocalDateTime innsendingstidspunkt;

    public RsAvsendersystem(String systemnavn, String systemversjon) {
        this.systemnavn = systemnavn;
        this.systemversjon = systemversjon;
        this.innsendingstidspunkt = LocalDateTime.now();
    }
}
