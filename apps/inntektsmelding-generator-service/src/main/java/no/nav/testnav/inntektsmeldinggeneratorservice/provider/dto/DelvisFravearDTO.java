package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.DelvisFravaer;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class DelvisFravearDTO implements ToXmlElement<DelvisFravaer> {
    @JsonProperty
    private String dato;
    @JsonProperty
    private Double timer;

    @Override
    public DelvisFravaer toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        DelvisFravaer xmlDelvisFravaer = factory.createDelvisFravaer();
        xmlDelvisFravaer.setDato(factory.createDelvisFravaerDato(dato));
        xmlDelvisFravaer.setTimer(factory.createDelvisFravaerTimer(
                timer != null ? BigDecimal.valueOf(timer) : null
        ));
        return xmlDelvisFravaer;
    }

    static List<DelvisFravaer> convert(List<DelvisFravearDTO> list) {
        return list.stream().map(DelvisFravearDTO::toXmlElement)
                .toList();
    }

}