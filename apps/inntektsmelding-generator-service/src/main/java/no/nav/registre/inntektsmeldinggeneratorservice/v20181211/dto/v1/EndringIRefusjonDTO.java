package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.EndringIRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class EndringIRefusjonDTO implements ToXmlElement<EndringIRefusjon> {
    @JsonProperty
    private LocalDate endringsdato;
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