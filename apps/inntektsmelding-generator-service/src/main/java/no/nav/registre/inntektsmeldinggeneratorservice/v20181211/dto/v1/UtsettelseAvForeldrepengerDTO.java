package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLUtsettelseAvForeldrepenger;

import java.util.List;
import java.util.stream.Collectors;

@Value
@NoArgsConstructor(force = true)
public class UtsettelseAvForeldrepengerDTO implements ToXmlElement<XMLUtsettelseAvForeldrepenger> {

    @JsonProperty
    private PeriodeDTO periode;
    @JsonProperty
    private String aarsakTilUtsettelse;

    @Override
    public XMLUtsettelseAvForeldrepenger toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLUtsettelseAvForeldrepenger xmlUtsettelseAvForeldrepenger = factory.createXMLUtsettelseAvForeldrepenger();
        xmlUtsettelseAvForeldrepenger.setPeriode(factory.createXMLUtsettelseAvForeldrepengerPeriode(
                periode != null ? periode.toXmlElement() : null
        ));
        xmlUtsettelseAvForeldrepenger.setAarsakTilUtsettelse(factory.createXMLUtsettelseAvForeldrepengerAarsakTilUtsettelse(
                aarsakTilUtsettelse
        ));
        return xmlUtsettelseAvForeldrepenger;
    }

    static List<XMLUtsettelseAvForeldrepenger> convert(List<UtsettelseAvForeldrepengerDTO> list){
        return list.stream().map(UtsettelseAvForeldrepengerDTO::toXmlElement).collect(Collectors.toList());
    }
}
