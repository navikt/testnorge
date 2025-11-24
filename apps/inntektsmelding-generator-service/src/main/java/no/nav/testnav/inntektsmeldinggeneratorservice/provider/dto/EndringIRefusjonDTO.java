package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.EndringIRefusjon;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class EndringIRefusjonDTO implements ToXmlElement<EndringIRefusjon> {
    @JsonProperty
    private String endringsdato;
    @JsonProperty
    private Double refusjonsbeloepPrMnd;

    @Override
    public EndringIRefusjon toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        EndringIRefusjon xmlEndringIRefusjon = factory.createEndringIRefusjon();
        xmlEndringIRefusjon.setRefusjonsbeloepPrMnd(factory.createRefusjonRefusjonsbeloepPrMnd(
                refusjonsbeloepPrMnd != null ? BigDecimal.valueOf(refusjonsbeloepPrMnd) : null
        ));
        xmlEndringIRefusjon.setEndringsdato(factory.createEndringIRefusjonEndringsdato(endringsdato));
        return xmlEndringIRefusjon;
    }

    static List<EndringIRefusjon> convert(List<EndringIRefusjonDTO> list) {
        return list.stream().map(EndringIRefusjonDTO::toXmlElement)
                .toList();
    }
}