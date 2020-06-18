package no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.andreokonomytelser.AnnenOkonomYtelseTiltak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Forvalter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyttVedtakTiltak extends NyttVedtak {

    @JsonAlias({ "LAG_OPPGAVE", "lagOppgave" })
    private String lagOppgave;

    @JsonAlias({ "ANDRE_OKONOM_YTELSER", "andreOkonomYtelser" })
    private List<AnnenOkonomYtelseTiltak> andreOkonomYtelser;

    @JsonAlias({ "TILTAK_PROSENTDELTID", "tiltakProsentDeltid" })
    private Double tiltakProsentDeltid;

    @JsonAlias({ "TILTAK_VEDTAK", "tiltakVedtak" })
    private String tiltakVedtak;

    @JsonAlias({ "TILTAK_YTELSE", "tiltakYtelse" })
    private String tiltakYtelse;

    @JsonAlias({ "TILTAK_KARAKTERISTIKK", "tiltakskarakteristikk" })
    private String tiltakskarakteristikk;

    @JsonAlias({ "AKTIVITETSKODE", "aktivitetkode" })
    private String aktivitetkode;

    @JsonAlias({ "BESKRIVELSE", "beskrivelse" })
    private String beskrivelse;

    @JsonAlias({ "ALTERNATIV_MOTTAKER", "alternativMottaker" })
    private Forvalter alternativMottaker;

    @JsonAlias({ "ANT_DAGER_UTBETALING", "antDagerUtbetaling" })
    private Integer antDagerUtbetaling;

    @JsonAlias({"AARSAK_KODE", "aarsakKode"})
    private String aarsakKode;

    @JsonAlias({ "DATO", "dato" })
    private LocalDate dato;

    @JsonAlias({"DELTAKERSTATUSKODE", "deltakerstatusKode"})
    private String deltakerstatusKode;

    @JsonAlias({"KOMMENTAR","kommentar"})
    private String kommentar;

    @JsonAlias({ "TILTAKSKODE", "tiltakskode" })
    private String tiltakskode;
}
