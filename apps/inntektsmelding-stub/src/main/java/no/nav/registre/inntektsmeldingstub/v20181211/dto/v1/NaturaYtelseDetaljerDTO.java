package no.nav.registre.inntektsmeldingstub.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLNaturalytelseDetaljer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Value
@NoArgsConstructor(force = true)
public class NaturaYtelseDetaljerDTO implements ToXmlElement<XMLNaturalytelseDetaljer> {
    @JsonProperty
    private String naturaytelseType;
    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private Double beloepPrMnd;

    @Override
    public XMLNaturalytelseDetaljer toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLNaturalytelseDetaljer xmlNaturalytelseDetaljer = factory.createXMLNaturalytelseDetaljer();
        xmlNaturalytelseDetaljer.setBeloepPrMnd(factory.createXMLNaturalytelseDetaljerBeloepPrMnd(
                beloepPrMnd != null ? BigDecimal.valueOf(beloepPrMnd) : null
        ));
        xmlNaturalytelseDetaljer.setFom(factory.createXMLNaturalytelseDetaljerFom(fom));
        xmlNaturalytelseDetaljer.setNaturalytelseType(factory.createXMLNaturalytelseDetaljerNaturalytelseType(
                naturaytelseType
        ));

        return xmlNaturalytelseDetaljer;
    }

    static List<XMLNaturalytelseDetaljer> covert(List<NaturaYtelseDetaljerDTO> list){
        return list.stream().map(NaturaYtelseDetaljerDTO::toXmlElement).collect(Collectors.toList());
    }

}