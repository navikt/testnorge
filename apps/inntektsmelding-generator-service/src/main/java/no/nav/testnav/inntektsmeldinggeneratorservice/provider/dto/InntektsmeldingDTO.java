package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.JAXBElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GjenopptakelseNaturalytelseListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.InntektsmeldingM;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.OpphoerAvNaturalytelseListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.PleiepengerPeriodeListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Skjemainnhold;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import org.apache.commons.text.CaseUtils;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@NoArgsConstructor(force = true)
public class InntektsmeldingDTO implements ToXmlElement<InntektsmeldingM> {

    @JsonProperty(required = true)
    private String ytelse;
    @JsonProperty(required = true)
    private String aarsakTilInnsending;
    @JsonProperty(required = true)
    private String arbeidstakerFnr;
    @JsonProperty(required = true)
    private boolean naerRelasjon;
    @JsonProperty
    private AvsendersystemDTO avsendersystem;
    @JsonProperty
    private ArbeidsgiverDTO arbeidsgiver;
    @JsonProperty
    private ArbeidsgiverPrivatDTO arbeidsgiverPrivat;
    @JsonProperty(required = true)
    private ArbeidsforholdDTO arbeidsforhold;
    @JsonProperty
    private RefusjonDTO refusjon;
    @JsonProperty
    private OmsorgspenegerDTO omsorgspenger;
    @JsonProperty
    private SykepengerIArbeidsgiverperiodenDTO sykepengerIArbeidsgiverperioden;
    @JsonProperty
    private String startdatoForeldrepengeperiode;
    @JsonProperty
    private List<NaturalYtelseDetaljerDTO> opphoerAvNaturalytelseListe;
    @JsonProperty
    private List<NaturalYtelseDetaljerDTO> gjenopptakelseNaturalytelseListe;
    @JsonProperty
    private List<PeriodeDTO> pleiepengerPerioder;

    @Override
    public InntektsmeldingM toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        Skjemainnhold xmlSkjemainnhold = factory.createSkjemainnhold();

        if (pleiepengerPerioder != null) {
            PleiepengerPeriodeListe xmlPleiepengerPeriodeListe = factory.createPleiepengerPeriodeListe();
            xmlPleiepengerPeriodeListe.withPeriode(PeriodeDTO.convert(pleiepengerPerioder));
            xmlSkjemainnhold.setPleiepengerPerioder(factory.createSkjemainnholdPleiepengerPerioder(xmlPleiepengerPeriodeListe));
        }

        if (opphoerAvNaturalytelseListe != null) {
            OpphoerAvNaturalytelseListe xmlOpphoerAvNaturalytelseListe = factory.createOpphoerAvNaturalytelseListe();
            xmlOpphoerAvNaturalytelseListe.withOpphoerAvNaturalytelse(NaturalYtelseDetaljerDTO.covert(opphoerAvNaturalytelseListe));
            xmlSkjemainnhold.setOpphoerAvNaturalytelseListe(
                    factory.createSkjemainnholdOpphoerAvNaturalytelseListe(xmlOpphoerAvNaturalytelseListe)
            );
        }

        if (gjenopptakelseNaturalytelseListe != null) {
            GjenopptakelseNaturalytelseListe xmlGjenopptakelseNaturalytelseListe = factory.createGjenopptakelseNaturalytelseListe();
            xmlGjenopptakelseNaturalytelseListe.withNaturalytelseDetaljer(NaturalYtelseDetaljerDTO.covert(gjenopptakelseNaturalytelseListe));
            xmlSkjemainnhold.setGjenopptakelseNaturalytelseListe(
                    factory.createSkjemainnholdGjenopptakelseNaturalytelseListe(xmlGjenopptakelseNaturalytelseListe)
            );
        }

        if (isBlank(ytelse)) xmlSkjemainnhold.setYtelse(
                null
        );
        else xmlSkjemainnhold.setYtelse(
                ytelse.contains("_") ? CaseUtils.toCamelCase(ytelse, true, '_') : ytelse
        );
        if (isBlank(aarsakTilInnsending)) xmlSkjemainnhold.setAarsakTilInnsending(
                null
        );
        else xmlSkjemainnhold.setAarsakTilInnsending(
                aarsakTilInnsending.contains("_") ? CaseUtils.toCamelCase(aarsakTilInnsending, true, '_') : aarsakTilInnsending
        );
        xmlSkjemainnhold.setArbeidstakerFnr(arbeidstakerFnr);
        xmlSkjemainnhold.setNaerRelasjon(naerRelasjon);

        if (avsendersystem != null) {
            xmlSkjemainnhold.setAvsendersystem(avsendersystem.toXmlElement());
        }

        if (arbeidsforhold != null) {
            xmlSkjemainnhold.setArbeidsforhold(factory.createSkjemainnholdArbeidsforhold(
                    arbeidsforhold.toXmlElement()
            ));
        }

        if (refusjon != null) {
            xmlSkjemainnhold.setRefusjon(factory.createSkjemainnholdRefusjon(refusjon.toXmlElement()));
        }

        if (arbeidsgiver != null) {
            xmlSkjemainnhold.setArbeidsgiver(arbeidsgiver.toXmlElement());
        }

        if (arbeidsgiverPrivat != null) {
            xmlSkjemainnhold.setArbeidsgiverPrivat(factory.createSkjemainnholdArbeidsgiverPrivat(arbeidsgiverPrivat.toXmlElement()));
        }

        xmlSkjemainnhold.setStartdatoForeldrepengeperiode(factory.createSkjemainnholdStartdatoForeldrepengeperiode(
                nonNull(startdatoForeldrepengeperiode) ? LocalDate.parse(startdatoForeldrepengeperiode) : null
        ));

        if (omsorgspenger != null) {
            xmlSkjemainnhold.setOmsorgspenger(factory.createSkjemainnholdOmsorgspenger(omsorgspenger.toXmlElement()));
        }

        if (sykepengerIArbeidsgiverperioden != null) {
            xmlSkjemainnhold.setSykepengerIArbeidsgiverperioden(factory.createSkjemainnholdSykepengerIArbeidsgiverperioden(
                    sykepengerIArbeidsgiverperioden.toXmlElement()
            ));
        }

        InntektsmeldingM xmlInntektsmeldingM = factory.createInntektsmeldingM();
        xmlInntektsmeldingM.setSkjemainnhold(xmlSkjemainnhold);

        return xmlInntektsmeldingM;
    }

    public JAXBElement<InntektsmeldingM> toMelding() {
        ObjectFactory factory = new ObjectFactory();
        return factory.createMelding(this.toXmlElement());
    }
}
