package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class Inntektsmelding {
    @JsonProperty
    List<NaturalytelseDetaljer> opphoerAvNaturalytelseListe;

    @JsonProperty
    List<NaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;

    @JsonProperty
    Long id;
    @JsonProperty
    String ytelse;
    @JsonProperty
    String aarsakTilInnsending;

    @JsonProperty
    Arbeidsgiver arbeidsgiver;

    @JsonProperty
    String arbeidstakerFnr;
    @JsonProperty
    Boolean naerRelasjon;

    @JsonProperty
    Arbeidsforhold arbeidsforhold;
    @JsonProperty
    Double refusjonsbeloepPrMnd;
    @JsonProperty
    LocalDate refusjonsopphoersdato;

    @JsonProperty
    List<EndringIRefusjon> endringIRefusjonListe;
    @JsonProperty
    Double sykepengerBruttoUtbetalt;
    @JsonProperty
    String sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt;

    @JsonProperty
    List<Periode> sykepengerPerioder;
    @JsonProperty
    LocalDate startdatoForeldrepengeperiode;
    @JsonProperty
    String avsendersystemNavn;
    @JsonProperty
    String avsendersystemVersjon;
    @JsonProperty
    LocalDateTime innsendingstidspunkt;
    @JsonProperty
    List<Periode> pleiepengerPeriodeListe;
    @JsonProperty
    Boolean omsorgHarUtbetaltPliktigeDager;
    @JsonProperty
    List<Periode> omsorgspengerFravaersPeriodeListe;
    @JsonProperty
    List<DelvisFravaer> omsorgspengerDelvisFravaersListe;
    @JsonProperty
    Eier eier;
}
