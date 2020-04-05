package no.nav.registre.inntektsmeldingstub.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLDelvisFravaersListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLFravaersPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLOmsorgspenger;

import java.util.ArrayList;
import java.util.List;


@Value
@NoArgsConstructor(force = true)
public class OmsorgspenegerDTO implements ToXmlElement<XMLOmsorgspenger> {

    @JsonProperty
    private Boolean harUtbetaltPliktigeDager;
    @JsonProperty
    private List<PeriodeDTO> fravaersPerioder = new ArrayList<>();
    @JsonProperty
    private List<DelvisFravearDTO> delvisFravaersListe = new ArrayList<>();


    @Override
    public XMLOmsorgspenger toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLDelvisFravaersListe xmlDelvisFravaersListe = factory.createXMLDelvisFravaersListe();
        xmlDelvisFravaersListe.withDelvisFravaer(DelvisFravearDTO.convert(delvisFravaersListe));

        XMLFravaersPeriodeListe xmlFravaersPeriodeListe = factory.createXMLFravaersPeriodeListe();
        xmlFravaersPeriodeListe.withFravaerPeriode(PeriodeDTO.convert(fravaersPerioder));

        XMLOmsorgspenger xmlOmsorgspenger = factory.createXMLOmsorgspenger();
        xmlOmsorgspenger.setHarUtbetaltPliktigeDager(factory.createXMLOmsorgspengerHarUtbetaltPliktigeDager(
                harUtbetaltPliktigeDager
        ));
        xmlOmsorgspenger.setFravaersPerioder(factory.createXMLOmsorgspengerFravaersPerioder(xmlFravaersPeriodeListe));
        xmlOmsorgspenger.setDelvisFravaersListe(factory.createXMLOmsorgspengerDelvisFravaersListe(xmlDelvisFravaersListe));

        return xmlOmsorgspenger;
    }
}
