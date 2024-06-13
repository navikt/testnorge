package no.nav.skattekortservice.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Arbeidsgiver;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@XmlRootElement(name = "skattekortTilArbeidsgiver",
        namespace = "urn:no:skatteetaten:fastsetting:formueinntekt:forskudd:skattekorttilarbeidsgiver:v3")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkattekortTilArbeidsgiver", propOrder = {
        "arbeidsgiver"
})
public class SkattekortRequest {

    @XmlElement(required = true)
    private List<Arbeidsgiver> arbeidsgiver;

    public List<Arbeidsgiver> getArbeidsgiver() {

        if (isNull(arbeidsgiver)) {
            arbeidsgiver = new ArrayList<>();
        }
        return arbeidsgiver;
    }
}

