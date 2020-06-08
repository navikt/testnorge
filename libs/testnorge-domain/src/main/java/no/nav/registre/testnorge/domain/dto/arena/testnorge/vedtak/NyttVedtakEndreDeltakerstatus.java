package no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyttVedtakEndreDeltakerstatus {

    @JsonAlias({"AARSAK_KODE", "aarsakKode"})
    private String aarsakKode;

    @JsonAlias({"AVBRUDD_KODE", "avbruddKode"})
    private String avbruddKode;

    @JsonAlias({ "DATO", "dato" })
    private LocalDate dato;

    @JsonAlias({"DELTAKERSTATUSKODE", "deltakerstatusKode"})
    private String deltakerstatusKode;

    @JsonAlias({"KOMMENTAR","kommentar"})
    private String kommentar;

    @JsonAlias({"SAKSBEHANDLER", "saksbehandler"})
    private String saksbehandler;

    @JsonAlias({ "TILTAK_KARAKTERISTIKK", "tiltakskarakteristikk" })
    private String tiltakskarakteristikk;

    @JsonAlias({ "TILTAKSKODE", "tiltakskode" })
    private String tiltakskode;
}
