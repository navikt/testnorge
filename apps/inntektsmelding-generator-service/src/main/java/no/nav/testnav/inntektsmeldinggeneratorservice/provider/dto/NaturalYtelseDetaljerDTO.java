package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.NaturalytelseDetaljer;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import org.apache.commons.text.CaseUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@NoArgsConstructor(force = true)
public class NaturalYtelseDetaljerDTO implements ToXmlElement<NaturalytelseDetaljer> {

    @JsonProperty
    private String naturalytelseType;
    @JsonProperty
    private String fom;
    @JsonProperty
    private Double beloepPrMnd;

    @Override
    public NaturalytelseDetaljer toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        NaturalytelseDetaljer xmlNaturalytelseDetaljer = factory.createNaturalytelseDetaljer();
        xmlNaturalytelseDetaljer.setBeloepPrMnd(factory.createNaturalytelseDetaljerBeloepPrMnd(
                beloepPrMnd != null ? BigDecimal.valueOf(beloepPrMnd) : null
        ));
        xmlNaturalytelseDetaljer.setFom(factory.createNaturalytelseDetaljerFom(fom));
        if (isBlank(naturalytelseType))
            xmlNaturalytelseDetaljer.setNaturalytelseType(factory.createNaturalytelseDetaljerNaturalytelseType(
                    null
            ));
        else xmlNaturalytelseDetaljer.setNaturalytelseType(factory.createNaturalytelseDetaljerNaturalytelseType(
                naturalytelseType.contains("_") ? CaseUtils.toCamelCase(naturalytelseType, true, '_') : naturalytelseType
        ));

        return xmlNaturalytelseDetaljer;
    }

    static List<NaturalytelseDetaljer> covert(List<NaturalYtelseDetaljerDTO> list) {
        return list.stream().map(NaturalYtelseDetaljerDTO::toXmlElement)
                .toList();
    }

}