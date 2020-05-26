package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@NoArgsConstructor(force=true)
@AllArgsConstructor
@Builder
public class SamhandlerListe {

    private final List<Samhandler> samhandlerListe;

    @XmlElement(name="samhandler")
    public List<Samhandler> getSamhandlerListeListe() {
        return samhandlerListe;
    }
}