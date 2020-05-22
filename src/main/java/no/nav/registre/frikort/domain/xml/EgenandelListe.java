package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class EgenandelListe {

    private List<Egenandel> egenandelListe;

    @XmlElement(name = "egenandel")
    public List<Egenandel> getEgenandelListe() {
        return egenandelListe;
    }
}
