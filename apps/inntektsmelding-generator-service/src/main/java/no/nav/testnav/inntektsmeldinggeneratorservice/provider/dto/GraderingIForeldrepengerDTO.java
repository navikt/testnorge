package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GraderingIForeldrepenger;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class GraderingIForeldrepengerDTO implements ToXmlElement<GraderingIForeldrepenger> {
    @JsonProperty
    private PeriodeDTO periode;
    @JsonProperty
    private Integer arbeidstidprosent;

    @Override
    public GraderingIForeldrepenger toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        GraderingIForeldrepenger xmlGraderingIForeldrepenger = factory.createGraderingIForeldrepenger();

        if (periode != null) {
            xmlGraderingIForeldrepenger.setPeriode(factory.createGraderingIForeldrepengerPeriode(
                    periode.toXmlElement()
            ));
        }

        xmlGraderingIForeldrepenger.setArbeidstidprosent(factory.createGraderingIForeldrepengerArbeidstidprosent(
                arbeidstidprosent != null ? BigInteger.valueOf(arbeidstidprosent) : null
        ));
        return xmlGraderingIForeldrepenger;
    }

    static List<GraderingIForeldrepenger> convert(List<GraderingIForeldrepengerDTO> list) {
        return list.stream().map(GraderingIForeldrepengerDTO::toXmlElement)
                .toList();
    }
}