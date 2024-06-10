package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.UtsettelseAvForeldrepenger;

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

    static List<UtsettelseAvForeldrepenger> convert(List<UtsettelseAvForeldrepengerDTO> list){
        return list.stream().map(UtsettelseAvForeldrepengerDTO::toXmlElement)
                .toList();
    }
}
