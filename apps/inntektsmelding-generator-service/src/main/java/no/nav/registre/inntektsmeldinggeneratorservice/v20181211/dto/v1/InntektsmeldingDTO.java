package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLGjenopptakelseNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLOpphoerAvNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLPleiepengerPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLSkjemainnhold;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class InntektsmeldingDTO implements ToXmlElement<XMLInntektsmeldingM> {

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
    private LocalDate startdatoForeldrepengeperiode;
    @JsonProperty
    private List<NaturalYtelseDetaljerDTO> opphoerAvNaturalytelseListe;
    @JsonProperty
    private List<NaturalYtelseDetaljerDTO> gjenopptakelseNaturalytelseListe;
    @JsonProperty
    private List<PeriodeDTO> pleiepengerPerioder;

    @Override
    public XMLInntektsmeldingM toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLSkjemainnhold xmlSkjemainnhold = factory.createXMLSkjemainnhold();

        if (pleiepengerPerioder != null) {
            XMLPleiepengerPeriodeListe xmlPleiepengerPeriodeListe = factory.createXMLPleiepengerPeriodeListe();
            xmlPleiepengerPeriodeListe.withPeriode(PeriodeDTO.convert(pleiepengerPerioder));
            xmlSkjemainnhold.setPleiepengerPerioder(factory.createXMLSkjemainnholdPleiepengerPerioder(xmlPleiepengerPeriodeListe));
        }

        if (opphoerAvNaturalytelseListe != null) {
            XMLOpphoerAvNaturalytelseListe xmlOpphoerAvNaturalytelseListe = factory.createXMLOpphoerAvNaturalytelseListe();
            xmlOpphoerAvNaturalytelseListe.withOpphoerAvNaturalytelse(NaturalYtelseDetaljerDTO.covert(opphoerAvNaturalytelseListe));
            xmlSkjemainnhold.setOpphoerAvNaturalytelseListe(
                    factory.createXMLSkjemainnholdOpphoerAvNaturalytelseListe(xmlOpphoerAvNaturalytelseListe)
            );
        }

        if (gjenopptakelseNaturalytelseListe != null) {
            XMLGjenopptakelseNaturalytelseListe xmlGjenopptakelseNaturalytelseListe = factory.createXMLGjenopptakelseNaturalytelseListe();
            xmlGjenopptakelseNaturalytelseListe.withNaturalytelseDetaljer(NaturalYtelseDetaljerDTO.covert(gjenopptakelseNaturalytelseListe));
            xmlSkjemainnhold.setGjenopptakelseNaturalytelseListe(
                    factory.createXMLSkjemainnholdGjenopptakelseNaturalytelseListe(xmlGjenopptakelseNaturalytelseListe)
            );
        }

        xmlSkjemainnhold.setYtelse(ytelse);
        xmlSkjemainnhold.setAarsakTilInnsending(aarsakTilInnsending);
        xmlSkjemainnhold.setArbeidstakerFnr(arbeidstakerFnr);
        xmlSkjemainnhold.setNaerRelasjon(naerRelasjon);

        if (avsendersystem != null) {
            xmlSkjemainnhold.setAvsendersystem(avsendersystem.toXmlElement());
        }

        if (arbeidsforhold != null) {
            xmlSkjemainnhold.setArbeidsforhold(factory.createXMLSkjemainnholdArbeidsforhold(
                    arbeidsforhold.toXmlElement()
            ));
        }

        if (refusjon != null) {
            xmlSkjemainnhold.setRefusjon(factory.createXMLSkjemainnholdRefusjon(refusjon.toXmlElement()));
        }

        if (arbeidsgiver != null) {
            xmlSkjemainnhold.setArbeidsgiver(factory.createXMLSkjemainnholdArbeidsgiver(arbeidsgiver.toXmlElement()));
        }

        if (arbeidsgiverPrivat != null) {
            xmlSkjemainnhold.setArbeidsgiverPrivat(factory.createXMLSkjemainnholdArbeidsgiverPrivat(arbeidsgiverPrivat.toXmlElement()));
        }

        xmlSkjemainnhold.setStartdatoForeldrepengeperiode(factory.createXMLSkjemainnholdStartdatoForeldrepengeperiode(
                startdatoForeldrepengeperiode
        ));

        if (omsorgspenger != null) {
            xmlSkjemainnhold.setOmsorgspenger(factory.createXMLSkjemainnholdOmsorgspenger(omsorgspenger.toXmlElement()));
        }

        if (sykepengerIArbeidsgiverperioden != null) {
            xmlSkjemainnhold.setSykepengerIArbeidsgiverperioden(factory.createXMLSkjemainnholdSykepengerIArbeidsgiverperioden(
                    sykepengerIArbeidsgiverperioden.toXmlElement()
            ));
        }

        XMLInntektsmeldingM xmlInntektsmeldingM = factory.createXMLInntektsmeldingM();
        xmlInntektsmeldingM.setSkjemainnhold(xmlSkjemainnhold);

        return xmlInntektsmeldingM;
    }

    public JAXBElement<XMLInntektsmeldingM> toMelding() {
        ObjectFactory factory = new ObjectFactory();
        return factory.createMelding(this.toXmlElement());
    }
}
