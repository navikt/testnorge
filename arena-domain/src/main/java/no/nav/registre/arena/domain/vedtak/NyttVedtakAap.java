package no.nav.registre.arena.domain.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.vedtak.andreokonomytelser.AndreOkonomYtelser;
import no.nav.registre.arena.domain.vedtak.forvalter.Forvalter;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.Saksopplysning;
import no.nav.registre.arena.domain.aap.institusjonsopphold.Institusjonsopphold;
import no.nav.registre.arena.domain.aap.medisinskopplysning.MedisinskOpplysning;
import no.nav.registre.arena.domain.aap.medlemfolketrygden.MedlemFolketrygden;
import no.nav.registre.arena.domain.aap.periode.Periode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyttVedtakAap extends NyttVedtak {

    @JsonAlias({ "ANDRE_OKONOM_YTELSER", "andreOkonomYtelser" })
    private List<AndreOkonomYtelser> andreOkonomYtelserListe;

    @JsonAlias({ "AKTFASEKODE", "aktivitetsfase" })
    private String aktivitetsfase;

    @JsonAlias({ "GEN_SAKSOPPLYSNINGER", "genSaksopplysninger" })
    private List<Saksopplysning> genSaksopplysninger;

    @JsonAlias({ "INSTITUSJONSOPPHOLD", "institusjonsopphold" })
    private List<Institusjonsopphold> institusjonsopphold;

    @JsonAlias({ "JUSTERT_FRA", "justertFra" })
    private String justertFra;

    @JsonAlias({ "MEDLEM_FOLKETRYGDEN", "medlemFolketrygden" })
    private MedlemFolketrygden medlemFolketrygden;

    @JsonAlias({ "PERIODE", "periode" })
    private Periode periode;

    @JsonAlias({ "FORVALTER", "forvalter" })
    private Forvalter forvalter;

    @JsonAlias({ "UTSKRIFT", "utskrift" })
    private String utskrift;

    @JsonAlias({ "VEDTAKSVARIANT", "vedtaksvariant" })
    private String vedtaksvariant;

    @JsonAlias({ "MEDISINSK_OPPLYSNING", "medisinskOpplysning" })
    private List<MedisinskOpplysning> medisinskOpplysning;
}
