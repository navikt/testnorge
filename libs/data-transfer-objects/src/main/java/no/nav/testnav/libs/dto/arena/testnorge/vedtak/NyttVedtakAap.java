package no.nav.testnav.libs.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.testnav.libs.dto.arena.testnorge.aap.gensaksopplysninger.Saksopplysning;
import no.nav.testnav.libs.dto.arena.testnorge.aap.institusjonsopphold.Institusjonsopphold;
import no.nav.testnav.libs.dto.arena.testnorge.aap.medisinskopplysning.MedisinskOpplysning;
import no.nav.testnav.libs.dto.arena.testnorge.aap.medlemfolketrygden.MedlemFolketrygden;
import no.nav.testnav.libs.dto.arena.testnorge.aap.periode.Periode;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.andreokonomytelser.AndreOkonomYtelser;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.forvalter.Forvalter;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyttVedtakAap extends NyttVedtak {

    @JsonAlias({"ANDRE_OKONOM_YTELSER", "andreOkonomYtelser"})
    private List<AndreOkonomYtelser> andreOkonomYtelserListe;

    @JsonAlias({"AKTFASEKODE", "aktivitetsfase"})
    private String aktivitetsfase;

    @JsonAlias({"GEN_SAKSOPPLYSNINGER", "genSaksopplysninger"})
    private List<Saksopplysning> genSaksopplysninger;

    @JsonAlias({"INSTITUSJONSOPPHOLD", "institusjonsopphold"})
    private List<Institusjonsopphold> institusjonsopphold;

    @JsonAlias({"JUSTERT_FRA", "justertFra"})
    private String justertFra;

    @JsonAlias({"MEDLEM_FOLKETRYGDEN", "medlemFolketrygden"})
    private MedlemFolketrygden medlemFolketrygden;

    @JsonAlias({"PERIODE", "periode"})
    private Periode periode;

    @JsonAlias({"FORVALTER", "forvalter"})
    private Forvalter forvalter;

    @JsonAlias({"UTSKRIFT", "utskrift"})
    private String utskrift;

    @JsonAlias({"VEDTAKSVARIANT", "vedtaksvariant"})
    private String vedtaksvariant;

    @JsonAlias({"MEDISINSK_OPPLYSNING", "medisinskOpplysning"})
    private List<MedisinskOpplysning> medisinskOpplysning;

    @JsonIgnore
    @Override
    public RettighetType getRettighetType() {
        return RettighetType.AAP;
    }
}
