package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLEndringIRefusjon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Value
@NoArgsConstructor(force = true)
public class EndringIRefusjonDTO implements ToXmlElement<XMLEndringIRefusjon> {
    @JsonProperty
    private LocalDate endringsdato;
    @JsonProperty
    private Double refusjonsbeloepPrMnd;

    @Override
    public XMLEndringIRefusjon toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLEndringIRefusjon xmlEndringIRefusjon = factory.createXMLEndringIRefusjon();
        xmlEndringIRefusjon.setRefusjonsbeloepPrMnd(factory.createXMLRefusjonRefusjonsbeloepPrMnd(
                refusjonsbeloepPrMnd != null ? BigDecimal.valueOf(refusjonsbeloepPrMnd) : null
        ));
        xmlEndringIRefusjon.setEndringsdato(factory.createXMLEndringIRefusjonEndringsdato(endringsdato));
        return xmlEndringIRefusjon;
    }

    static List<XMLEndringIRefusjon> convert(List<EndringIRefusjonDTO> list){
        return list.stream().map(EndringIRefusjonDTO::toXmlElement).collect(Collectors.toList());
    }
}