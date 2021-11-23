package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement(name = "sfePersonData")
public class SikkerhetstiltakRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"systemInfo", "opprettSikkerhetsTiltak", "opphorSikkerhetsTiltak"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private Sikkerhetstiltak opprettSikkerhetsTiltak;
        private BrukerIdentifikasjon opphorSikkerhetsTiltak;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"typeSikkerhetsTiltak", "beskrSikkerhetsTiltak", "fom", "tom"})
    public static class Sikkerhetstiltak extends BrukerIdentifikasjon {

        private String typeSikkerhetsTiltak;
        private String beskrSikkerhetsTiltak;
        private String fom;
        private String tom;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"offentligIdent"})
    public static class BrukerIdentifikasjon {

        private String offentligIdent;
    }
}