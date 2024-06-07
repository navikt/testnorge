package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Arbeidsforhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.AvtaltFerieListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.GraderingIForeldrepengerListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.UtsettelseAvForeldrepengerListe;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO implements ToXmlElement<Arbeidsforhold> {
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
    public Arbeidsforhold toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        Arbeidsforhold xmlArbeidsforhold = factory.createArbeidsforhold();

        if (utsettelseAvForeldrepengerListe != null) {
            UtsettelseAvForeldrepengerListe xmlUtsettelseAvForeldrepengerListe = factory.createUtsettelseAvForeldrepengerListe();
            xmlUtsettelseAvForeldrepengerListe.withUtsettelseAvForeldrepenger(UtsettelseAvForeldrepengerDTO.convert(utsettelseAvForeldrepengerListe));
            xmlArbeidsforhold.setUtsettelseAvForeldrepengerListe(
                    factory.createArbeidsforholdUtsettelseAvForeldrepengerListe(xmlUtsettelseAvForeldrepengerListe)
            );
        }

        if (graderingIForeldrepengerListe != null) {
            GraderingIForeldrepengerListe xmlGraderingIForeldrepengerListe = factory.createGraderingIForeldrepengerListe();
            xmlGraderingIForeldrepengerListe.withGraderingIForeldrepenger(GraderingIForeldrepengerDTO.convert(graderingIForeldrepengerListe));
            xmlArbeidsforhold.setGraderingIForeldrepengerListe(
                    factory.createArbeidsforholdGraderingIForeldrepengerListe(xmlGraderingIForeldrepengerListe)
            );
        }

        if (avtaltFerieListe != null) {
            AvtaltFerieListe xmlAvtaltFerieListe = factory.createAvtaltFerieListe();
            xmlAvtaltFerieListe.withAvtaltFerie(PeriodeDTO.convert(avtaltFerieListe));
            xmlArbeidsforhold.setAvtaltFerieListe(factory.createArbeidsforholdAvtaltFerieListe(xmlAvtaltFerieListe));
        }

        if (beregnetInntekt != null) {
            xmlArbeidsforhold.setBeregnetInntekt(factory.createArbeidsforholdBeregnetInntekt(beregnetInntekt.toXmlElement()));
        }

        if (Strings.isNotBlank(arbeidsforholdId)) {
            xmlArbeidsforhold.setArbeidsforholdId(factory.createArbeidsforholdArbeidsforholdId(arbeidsforholdId));
        }

        xmlArbeidsforhold.setFoersteFravaersdag(factory.createArbeidsforholdFoersteFravaersdag(foersteFravaersdag));

        return xmlArbeidsforhold;
    }
}