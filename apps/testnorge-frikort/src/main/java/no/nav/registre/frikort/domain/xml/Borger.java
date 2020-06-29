package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class Borger {

    private final String borgerid;

    @XmlElement
    public String getBorgerid() {
        return borgerid;
    }
}
