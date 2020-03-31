package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Inntektsmelding {
    @JsonProperty
    private List<NaturalytelseDetaljer> opphoerAvNaturalytelseListe;

    @JsonProperty
    private List<NaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;

    @JsonProperty
    private Long id;
    @JsonProperty
    private String ytelse;
    @JsonProperty
    private String aarsakTilInnsending;

    @JsonProperty
    private Arbeidsgiver arbeidsgiver;

    @JsonProperty
    private String arbeidstakerFnr;
    @JsonProperty
    private Boolean naerRelasjon;

    @JsonProperty
    private Arbeidsforhold arbeidsforhold;
    @JsonProperty
    private Double refusjonsbeloepPrMnd;
    @JsonProperty
    private LocalDate refusjonsopphoersdato;

    @JsonProperty
    private List<EndringIRefusjon> endringIRefusjonListe;
    @JsonProperty
    private Double sykepengerBruttoUtbetalt;
    @JsonProperty
    private String sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt;

    @JsonProperty
    private List<Periode> sykepengerPerioder;
    @JsonProperty
    private LocalDate startdatoForeldrepengeperiode;
    @JsonProperty
    private String avsendersystemNavn;
    @JsonProperty
    private String avsendersystemVersjon;
    @JsonProperty
    private LocalDateTime innsendingstidspunkt;
    @JsonProperty
    private List<Periode> pleiepengerPeriodeListe;
    @JsonProperty
    private Boolean omsorgHarUtbetaltPliktigeDager;
    @JsonProperty
    private List<Periode> omsorgspengerFravaersPeriodeListe;
    @JsonProperty
    private List<DelvisFravaer> omsorgspengerDelvisFravaersListe;
    @JsonProperty
    private Eier eier;
}
