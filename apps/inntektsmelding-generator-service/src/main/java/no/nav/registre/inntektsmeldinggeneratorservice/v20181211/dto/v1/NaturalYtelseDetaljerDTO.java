package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLNaturalytelseDetaljer;
import org.apache.commons.text.CaseUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Value
@NoArgsConstructor(force = true)
public class NaturalYtelseDetaljerDTO implements ToXmlElement<XMLNaturalytelseDetaljer> {

    @JsonProperty
    private String naturalytelseType;
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
                isBlank(naturalytelseType) ? null : CaseUtils.toCamelCase(naturalytelseType, true, '_')
        ));

        return xmlNaturalytelseDetaljer;
    }

    static List<XMLNaturalytelseDetaljer> covert(List<NaturalYtelseDetaljerDTO> list) {
        return list.stream().map(NaturalYtelseDetaljerDTO::toXmlElement).collect(Collectors.toList());
    }

}