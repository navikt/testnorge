package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLEndringIRefusjonsListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLRefusjon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class RefusjonDTO implements ToXmlElement<XMLRefusjon> {

    @JsonProperty
    private Double refusjonsbeloepPrMnd;
    @JsonProperty
    private LocalDate refusjonsopphoersdato;
    @JsonProperty
    private List<EndringIRefusjonDTO> endringIRefusjonListe;


    @Override
    public XMLRefusjon toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        XMLRefusjon xmlRefusjon = factory.createXMLRefusjon();

        if (endringIRefusjonListe != null) {
            XMLEndringIRefusjonsListe xmlEndringIRefusjonsListe = factory.createXMLEndringIRefusjonsListe();
            xmlEndringIRefusjonsListe.withEndringIRefusjon(EndringIRefusjonDTO.convert(endringIRefusjonListe));
            xmlRefusjon.withEndringIRefusjonListe(factory.createXMLRefusjonEndringIRefusjonListe(
                    xmlEndringIRefusjonsListe
            ));
        }

        xmlRefusjon.setRefusjonsbeloepPrMnd(factory.createXMLRefusjonRefusjonsbeloepPrMnd(
                refusjonsbeloepPrMnd != null ? BigDecimal.valueOf(refusjonsbeloepPrMnd) : null
        ));
        xmlRefusjon.setRefusjonsopphoersdato(factory.createXMLRefusjonRefusjonsopphoersdato(refusjonsopphoersdato));


        return xmlRefusjon;
    }
}
