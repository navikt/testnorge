package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLPeriode;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Value
@NoArgsConstructor(force = true)
public class PeriodeDTO implements ToXmlElement<XMLPeriode> {
    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private LocalDate tom;

    @Override
    public XMLPeriode toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        XMLPeriode xmlPeriode = factory.createXMLPeriode();
        xmlPeriode.setFom(factory.createXMLPeriodeFom(this.fom));
        xmlPeriode.setTom(factory.createXMLPeriodeTom(this.tom));
        return xmlPeriode;
    }

    static List<XMLPeriode> convert(List<PeriodeDTO> list) {
        return list.stream().map(PeriodeDTO::toXmlElement).collect(Collectors.toList());
    }
}