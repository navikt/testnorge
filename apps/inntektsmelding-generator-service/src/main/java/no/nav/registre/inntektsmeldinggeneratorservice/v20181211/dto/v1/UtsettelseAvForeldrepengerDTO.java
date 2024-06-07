package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.UtsettelseAvForeldrepenger;

import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class UtsettelseAvForeldrepengerDTO implements ToXmlElement<UtsettelseAvForeldrepenger> {

    @JsonProperty
    private PeriodeDTO periode;
    @JsonProperty
    private String aarsakTilUtsettelse;

    @Override
    public UtsettelseAvForeldrepenger toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        UtsettelseAvForeldrepenger xmlUtsettelseAvForeldrepenger = factory.createUtsettelseAvForeldrepenger();
        xmlUtsettelseAvForeldrepenger.setPeriode(factory.createUtsettelseAvForeldrepengerPeriode(
                periode != null ? periode.toXmlElement() : null
        ));
        xmlUtsettelseAvForeldrepenger.setAarsakTilUtsettelse(factory.createUtsettelseAvForeldrepengerAarsakTilUtsettelse(
                aarsakTilUtsettelse
        ));
        return xmlUtsettelseAvForeldrepenger;
    }

    static List<UtsettelseAvForeldrepenger> convert(List<UtsettelseAvForeldrepengerDTO> list) {
        return list.stream().map(UtsettelseAvForeldrepengerDTO::toXmlElement)
                .toList();
    }
}
