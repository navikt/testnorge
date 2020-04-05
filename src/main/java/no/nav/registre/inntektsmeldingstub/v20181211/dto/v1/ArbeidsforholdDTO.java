package no.nav.registre.inntektsmeldingstub.v20181211.dto.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsforhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLAvtaltFerieListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLGraderingIForeldrepengerListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLUtsettelseAvForeldrepengerListe;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private List<PeriodeDTO> avtaltFerieListe = new ArrayList<>();
    @JsonProperty
    private List<UtsettelseAvForeldrepengerDTO> utsettelseAvForeldrepengerListe = new ArrayList<>();
    @JsonProperty
    private List<GraderingIForeldrepengerDTO> graderingIForeldrepengerListe = new ArrayList<>();

    @Override
    public XMLArbeidsforhold toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLUtsettelseAvForeldrepengerListe xmlUtsettelseAvForeldrepengerListe = factory.createXMLUtsettelseAvForeldrepengerListe();
        xmlUtsettelseAvForeldrepengerListe.withUtsettelseAvForeldrepenger(
                UtsettelseAvForeldrepengerDTO.convert(utsettelseAvForeldrepengerListe)
        );

        XMLGraderingIForeldrepengerListe xmlGraderingIForeldrepengerListe = factory.createXMLGraderingIForeldrepengerListe();
        xmlGraderingIForeldrepengerListe.withGraderingIForeldrepenger(
                GraderingIForeldrepengerDTO.convert(graderingIForeldrepengerListe)
        );

        XMLAvtaltFerieListe xmlAvtaltFerieListe = factory.createXMLAvtaltFerieListe();
        xmlAvtaltFerieListe.withAvtaltFerie(PeriodeDTO.convert(avtaltFerieListe));

        XMLArbeidsforhold xmlArbeidsforhold = factory.createXMLArbeidsforhold();
        if (beregnetInntekt != null) {
            xmlArbeidsforhold.setBeregnetInntekt(factory.createXMLArbeidsforholdBeregnetInntekt(beregnetInntekt.toXmlElement()));
        }
        xmlArbeidsforhold.setArbeidsforholdId(factory.createXMLArbeidsforholdArbeidsforholdId(arbeidsforholdId));
        xmlArbeidsforhold.setFoersteFravaersdag(factory.createXMLDelvisFravaerDato(foersteFravaersdag));
        xmlArbeidsforhold.setAvtaltFerieListe(factory.createXMLArbeidsforholdAvtaltFerieListe(xmlAvtaltFerieListe));
        xmlArbeidsforhold.setGraderingIForeldrepengerListe(
                factory.createXMLArbeidsforholdGraderingIForeldrepengerListe(xmlGraderingIForeldrepengerListe)
        );
        xmlArbeidsforhold.setUtsettelseAvForeldrepengerListe(
                factory.createXMLArbeidsforholdUtsettelseAvForeldrepengerListe(xmlUtsettelseAvForeldrepengerListe)
        );

        return xmlArbeidsforhold;
    }
}