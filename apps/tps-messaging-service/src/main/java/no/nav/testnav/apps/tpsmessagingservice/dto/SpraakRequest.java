package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@XmlRootElement(name = "sfePersonData")
public class SpraakRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"systemInfo", "endreSprak"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private EndreSpraak endreSprak;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"offentligIdent", "sprakKode", "datoSprak"})
    public static class EndreSpraak{

        private String offentligIdent;
        private String sprakKode;
        private String datoSprak;
    }
}
