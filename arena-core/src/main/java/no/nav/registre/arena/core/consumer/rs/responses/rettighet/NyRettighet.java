package no.nav.registre.arena.core.consumer.rs.responses.rettighet;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.arena.domain.aap.andreokonomytelser.AndreOkonomYtelserV1;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.Saksopplysning;
import no.nav.registre.arena.domain.aap.institusjonsopphold.Institusjonsopphold;
import no.nav.registre.arena.domain.aap.medlemfolketrygden.MedlemFolketrygden;
import no.nav.registre.arena.domain.aap.periode.Periode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyRettighet {

    @JsonAlias({ "AKTFASEKODE", "aktivitetsfase" })
    private String aktivitetsfase;

    @JsonAlias({ "ANDRE_OKONOM_YTELSER", "andreOkonomYtelser" })
    private List<AndreOkonomYtelserV1> andreOkonomYtelser;

    @JsonAlias({ "AVBRUDDSKODE", "avbruddKode" })
    private String avbruddKode;

    @JsonAlias({ "BEGRUNNELSE", "begrunnelse" })
    private String begrunnelse;

    @JsonAlias({ "BESLUTTER", "beslutter" })
    private String beslutter;

    @JsonAlias({ "DATO_MOTTATT", "datoMottatt" })
    private LocalDate datoMottatt;

    @JsonAlias({ "FRA_DATO", "fraDato" })
    private LocalDate fraDato;

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

    @JsonAlias({ "FODSELSNR", "fodselsnr" })
    private String fodselsnr;

    @JsonAlias({ "GJELDENDE_KONTONR", "gjeldendeKontonr" })
    private List<Konto> gjeldendeKontonr;

    @JsonAlias({ "MILJOE", "miljoe" })
    private String miljoe;

    @JsonAlias({ "SAKSBEHANDLER", "saksbehandler" })
    private String saksbehandler;

    @JsonAlias({ "TIL_DATO", "tilDato" })
    private LocalDate tilDato;

    @JsonAlias({ "UTBETALINGSADRESSE", "utbetalingsadresse" })
    private List<Adresse> utbetalingsadresse;

    @JsonAlias({ "UTFALL", "utfall" })
    private String utfall;

    @JsonAlias({ "UTSKRIFT", "utskrift" })
    private String utskrift;

    @JsonAlias({ "VEDTAKSVARIANT", "vedtaksvariant" })
    private String vedtaksvariant;

    @JsonAlias({ "VILKAAR", "vilkaar" })
    private List<Vilkaar> vilkaar;

    @JsonAlias({ "VEDTAKTYPE", "vedtaktype" })
    private String vedtaktype;
}
