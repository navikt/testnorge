package no.nav.testnav.libs.domain.dto.arena.testnorge.tiltak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tiltaksaktivitet {

    private String fodselsnr;
    private String aktivitetkode;
    private LocalDate fraDato;
    private String beskrivelse;
    private String saksbehandler;
}
