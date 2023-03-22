package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntekt;
import org.apache.commons.text.CaseUtils;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Value
@NoArgsConstructor(force = true)
public class InntektDTO implements ToXmlElement<XMLInntekt> {
    @JsonProperty
    private Double beloep;
    @JsonProperty
    private String aarsakVedEndring;

    @Override
    public XMLInntekt toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        XMLInntekt xmlInntekt = factory.createXMLInntekt();
        xmlInntekt.setAarsakVedEndring(factory.createXMLInntektAarsakVedEndring(
                isBlank(aarsakVedEndring) ? null : CaseUtils.toCamelCase(aarsakVedEndring, true, '_')
        ));
        xmlInntekt.setBeloep(factory.createXMLInntektBeloep(beloep != null ? BigDecimal.valueOf(beloep) : null));
        return xmlInntekt;
    }
}
