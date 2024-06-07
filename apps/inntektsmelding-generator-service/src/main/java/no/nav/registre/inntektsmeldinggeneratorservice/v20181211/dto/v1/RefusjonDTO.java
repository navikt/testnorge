package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.EndringIRefusjonsListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Refusjon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class RefusjonDTO implements ToXmlElement<Refusjon> {

    @JsonProperty
    private Double refusjonsbeloepPrMnd;
    @JsonProperty
    private LocalDate refusjonsopphoersdato;
    @JsonProperty
    private List<EndringIRefusjonDTO> endringIRefusjonListe;


    @Override
    public Refusjon toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        Refusjon xmlRefusjon = factory.createRefusjon();

        if (endringIRefusjonListe != null) {
            EndringIRefusjonsListe xmlEndringIRefusjonsListe = factory.createEndringIRefusjonsListe();
            xmlEndringIRefusjonsListe.withEndringIRefusjon(EndringIRefusjonDTO.convert(endringIRefusjonListe));
            xmlRefusjon.withEndringIRefusjonListe(factory.createRefusjonEndringIRefusjonListe(
                    xmlEndringIRefusjonsListe
            ));
        }

        xmlRefusjon.setRefusjonsbeloepPrMnd(factory.createRefusjonRefusjonsbeloepPrMnd(
                refusjonsbeloepPrMnd != null ? BigDecimal.valueOf(refusjonsbeloepPrMnd) : null
        ));
        xmlRefusjon.setRefusjonsopphoersdato(factory.createRefusjonRefusjonsopphoersdato(refusjonsopphoersdato));


        return xmlRefusjon;
    }
}
