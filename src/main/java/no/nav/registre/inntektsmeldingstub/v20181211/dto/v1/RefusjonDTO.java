package no.nav.registre.inntektsmeldingstub.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLEndringIRefusjonsListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLRefusjon;

import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class RefusjonDTO implements ToXmlElement<XMLRefusjon> {

    @JsonProperty
    private Double refusjonsbeloepPrMnd;
    @JsonProperty
    private LocalDate refusjonsopphoersdato;
    @JsonProperty
    private List<EndringIRefusjonDTO> endringIRefusjonListe = new ArrayList<>();


    @Override
    public XMLRefusjon toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLEndringIRefusjonsListe xmlEndringIRefusjonsListe = factory.createXMLEndringIRefusjonsListe();
        xmlEndringIRefusjonsListe.withEndringIRefusjon(EndringIRefusjonDTO.convert(endringIRefusjonListe));


        XMLRefusjon xmlRefusjon = factory.createXMLRefusjon();
        xmlRefusjon.setRefusjonsbeloepPrMnd(factory.createXMLRefusjonRefusjonsbeloepPrMnd(
                refusjonsbeloepPrMnd != null ? BigDecimal.valueOf(refusjonsbeloepPrMnd) : null
        ));
        xmlRefusjon.setRefusjonsopphoersdato(factory.createXMLRefusjonRefusjonsopphoersdato(refusjonsopphoersdato));
        xmlRefusjon.withEndringIRefusjonListe(factory.createXMLRefusjonEndringIRefusjonListe(
                xmlEndringIRefusjonsListe
        ));

        return xmlRefusjon;
    }
}
