package no.nav.testnav.libs.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.andreokonomytelser.AnnenOkonomYtelseTiltak;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.forvalter.Forvalter;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
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

    @JsonAlias({ "tiltakAdminKode", "TILTAK_ADMIN_KODE" })
    private String tiltakAdminKode;

    @JsonAlias({ "AKTIVITETSKODE", "AKTIVITETKODE", "aktivitetkode" })
    private String aktivitetkode;

    @JsonAlias({ "AKTIVITETSTATUSKODE", "aktivitetStatuskode", "aktivitetstatuskode" })
    private String aktivitetStatuskode;

    @JsonAlias({ "BESKRIVELSE", "beskrivelse" })
    private String beskrivelse;

    @JsonAlias({ "ALTERNATIV_MOTTAKER", "alternativMottaker" })
    private Forvalter alternativMottaker;

    @JsonAlias({ "ANT_DAGER_UTBETALING", "antDagerUtbetaling" })
    private Integer antDagerUtbetaling;

    @JsonAlias({ "AARSAK_KODE", "aarsakKode" })
    private String aarsakKode;

    @JsonAlias({ "DATO", "dato" })
    private LocalDate dato;

    @JsonAlias({ "DELTAKERSTATUSKODE", "deltakerstatusKode" })
    private String deltakerstatusKode;

    @JsonAlias({ "KOMMENTAR", "kommentar" })
    private String kommentar;

    @JsonAlias({ "TILTAK_KODE", "tiltakKode" })
    private String tiltakKode;

    @JsonAlias({ "TILTAK_ID", "tiltakId" })
    private Integer tiltakId;

    @JsonAlias({ "FODSELSNR", "fodselsnr" })
    private String fodselsnr;

    @JsonAlias({ "TILTAK_STATUS_KODE", "tiltakStatusKode" })
    private String tiltakStatusKode;

    @JsonAlias({ "AVBRUDD_KODE", "avbruddKode" })
    private String avbruddKode;

    @JsonIgnore
    @Override
    public RettighetType getRettighetType(){
        return RettighetType.TILTAK;
    }
}
