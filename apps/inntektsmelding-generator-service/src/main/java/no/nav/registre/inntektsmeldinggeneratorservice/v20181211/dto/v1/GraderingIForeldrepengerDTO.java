package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLGraderingIForeldrepenger;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;


@Value
@NoArgsConstructor(force = true)
public class GraderingIForeldrepengerDTO implements ToXmlElement<XMLGraderingIForeldrepenger> {
    @JsonProperty
    private PeriodeDTO periode;
    @JsonProperty
    private Integer arbeidstidprosent;

    @Override
    public XMLGraderingIForeldrepenger toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLGraderingIForeldrepenger xmlGraderingIForeldrepenger = factory.createXMLGraderingIForeldrepenger();

        if (periode != null) {
            xmlGraderingIForeldrepenger.setPeriode(factory.createXMLGraderingIForeldrepengerPeriode(
                    periode.toXmlElement()
            ));
        }

        xmlGraderingIForeldrepenger.setArbeidstidprosent(factory.createXMLGraderingIForeldrepengerArbeidstidprosent(
                arbeidstidprosent != null ? BigInteger.valueOf(arbeidstidprosent) : null
        ));
        return xmlGraderingIForeldrepenger;
    }

    static List<XMLGraderingIForeldrepenger> convert(List<GraderingIForeldrepengerDTO> list) {
        return list.stream().map(GraderingIForeldrepengerDTO::toXmlElement).collect(Collectors.toList());
    }
}