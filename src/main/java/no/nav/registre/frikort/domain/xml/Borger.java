package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borger {

    private long borgerid;

    @XmlElement
    public long getBorgerid() {
        return borgerid;
    }
}
