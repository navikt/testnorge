package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLDelvisFravaersListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLFravaersPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLOmsorgspenger;

import java.util.List;


@Value
@NoArgsConstructor(force = true)
public class OmsorgspenegerDTO implements ToXmlElement<XMLOmsorgspenger> {

    @JsonProperty
    private Boolean harUtbetaltPliktigeDager;
    @JsonProperty
    private List<PeriodeDTO> fravaersPerioder;
    @JsonProperty
    private List<DelvisFravearDTO> delvisFravaersListe;


    @Override
    public XMLOmsorgspenger toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLOmsorgspenger xmlOmsorgspenger = factory.createXMLOmsorgspenger();
        if (delvisFravaersListe != null) {
            XMLDelvisFravaersListe xmlDelvisFravaersListe = factory.createXMLDelvisFravaersListe();
            xmlDelvisFravaersListe.withDelvisFravaer(DelvisFravearDTO.convert(delvisFravaersListe));
            xmlOmsorgspenger.setDelvisFravaersListe(factory.createXMLOmsorgspengerDelvisFravaersListe(xmlDelvisFravaersListe));
        }

        if (fravaersPerioder != null) {
            XMLFravaersPeriodeListe xmlFravaersPeriodeListe = factory.createXMLFravaersPeriodeListe();
            xmlFravaersPeriodeListe.withFravaerPeriode(PeriodeDTO.convert(fravaersPerioder));
            xmlOmsorgspenger.setFravaersPerioder(factory.createXMLOmsorgspengerFravaersPerioder(xmlFravaersPeriodeListe));
        }

        xmlOmsorgspenger.setHarUtbetaltPliktigeDager(factory.createXMLOmsorgspengerHarUtbetaltPliktigeDager(
                harUtbetaltPliktigeDager
        ));
        return xmlOmsorgspenger;
    }
}
