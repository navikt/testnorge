package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SamhandlerListe {

    private List<Samhandler> samhandlerListe;

    @XmlElement(name="samhandler")
    public List<Samhandler> getSamhandlerListeListe() {
        return samhandlerListe;
    }
}