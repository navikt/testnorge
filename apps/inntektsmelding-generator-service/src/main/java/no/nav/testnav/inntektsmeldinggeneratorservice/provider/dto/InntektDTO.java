package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Inntekt;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import org.apache.commons.text.CaseUtils;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@NoArgsConstructor(force = true)
public class InntektDTO implements ToXmlElement<Inntekt> {
    @JsonProperty
    private Double beloep;
    @JsonProperty
    private String aarsakVedEndring;

    @Override
    public Inntekt toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        Inntekt xmlInntekt = factory.createInntekt();
        if (isBlank(aarsakVedEndring)) xmlInntekt.setAarsakVedEndring(factory.createInntektAarsakVedEndring(
                null
        ));
        else xmlInntekt.setAarsakVedEndring(factory.createInntektAarsakVedEndring(
                aarsakVedEndring.contains("_") ? CaseUtils.toCamelCase(aarsakVedEndring, true, '_') : aarsakVedEndring
        ));
        xmlInntekt.setBeloep(factory.createInntektBeloep(beloep != null ? BigDecimal.valueOf(beloep) : null));
        return xmlInntekt;
    }
}
