package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLDelvisFravaer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class DelvisFravearDTO implements ToXmlElement<XMLDelvisFravaer> {
    @JsonProperty
    private LocalDate dato;
    @JsonProperty
    private Double timer;

    @Override
    public XMLDelvisFravaer toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLDelvisFravaer xmlDelvisFravaer = factory.createXMLDelvisFravaer();
        xmlDelvisFravaer.setDato(factory.createXMLDelvisFravaerDato(dato));
        xmlDelvisFravaer.setTimer(factory.createXMLDelvisFravaerTimer(
                timer != null ? BigDecimal.valueOf(timer) : null
        ));
        return xmlDelvisFravaer;
    }

    static List<XMLDelvisFravaer> convert(List<DelvisFravearDTO> list) {
        return list.stream().map(DelvisFravearDTO::toXmlElement)
                .toList();
    }

}