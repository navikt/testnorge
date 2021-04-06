package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsforhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLAvtaltFerieListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLGraderingIForeldrepengerListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLUtsettelseAvForeldrepengerListe;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO implements ToXmlElement<XMLArbeidsforhold> {
    @JsonProperty
    private String arbeidsforholdId;
    @JsonProperty
    private LocalDate foersteFravaersdag;
    @JsonProperty
    private InntektDTO beregnetInntekt;
    @JsonProperty
    private List<PeriodeDTO> avtaltFerieListe;
    @JsonProperty
    private List<UtsettelseAvForeldrepengerDTO> utsettelseAvForeldrepengerListe;
    @JsonProperty
    private List<GraderingIForeldrepengerDTO> graderingIForeldrepengerListe;

    @Override
    public XMLArbeidsforhold toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        XMLArbeidsforhold xmlArbeidsforhold = factory.createXMLArbeidsforhold();

        if (utsettelseAvForeldrepengerListe != null) {
            XMLUtsettelseAvForeldrepengerListe xmlUtsettelseAvForeldrepengerListe = factory.createXMLUtsettelseAvForeldrepengerListe();
            xmlUtsettelseAvForeldrepengerListe.withUtsettelseAvForeldrepenger(UtsettelseAvForeldrepengerDTO.convert(utsettelseAvForeldrepengerListe));
            xmlArbeidsforhold.setUtsettelseAvForeldrepengerListe(
                    factory.createXMLArbeidsforholdUtsettelseAvForeldrepengerListe(xmlUtsettelseAvForeldrepengerListe)
            );
        }

        if (graderingIForeldrepengerListe != null) {
            XMLGraderingIForeldrepengerListe xmlGraderingIForeldrepengerListe = factory.createXMLGraderingIForeldrepengerListe();
            xmlGraderingIForeldrepengerListe.withGraderingIForeldrepenger(GraderingIForeldrepengerDTO.convert(graderingIForeldrepengerListe));
            xmlArbeidsforhold.setGraderingIForeldrepengerListe(
                    factory.createXMLArbeidsforholdGraderingIForeldrepengerListe(xmlGraderingIForeldrepengerListe)
            );
        }

        if (avtaltFerieListe != null) {
            XMLAvtaltFerieListe xmlAvtaltFerieListe = factory.createXMLAvtaltFerieListe();
            xmlAvtaltFerieListe.withAvtaltFerie(PeriodeDTO.convert(avtaltFerieListe));
            xmlArbeidsforhold.setAvtaltFerieListe(factory.createXMLArbeidsforholdAvtaltFerieListe(xmlAvtaltFerieListe));
        }

        if (beregnetInntekt != null) {
            xmlArbeidsforhold.setBeregnetInntekt(factory.createXMLArbeidsforholdBeregnetInntekt(beregnetInntekt.toXmlElement()));
        }

        if (Strings.isNotBlank(arbeidsforholdId)) {
            xmlArbeidsforhold.setArbeidsforholdId(factory.createXMLArbeidsforholdArbeidsforholdId(arbeidsforholdId));
        }

        xmlArbeidsforhold.setFoersteFravaersdag(factory.createXMLArbeidsforholdFoersteFravaersdag(foersteFravaersdag));

        return xmlArbeidsforhold;
    }
}