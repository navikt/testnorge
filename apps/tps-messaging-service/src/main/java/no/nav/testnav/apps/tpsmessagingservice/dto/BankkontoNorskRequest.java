package no.nav.testnav.apps.tpsmessagingservice.dto;

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
@XmlRootElement(name = "sfePersonData")
public class BankkontoNorskRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"systemInfo", "endreGironrNorsk", "opphorGironrNorsk"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private EndreGironrNorsk endreGironrNorsk;
        private BrukerIdentifikasjon opphorGironrNorsk;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"offentligIdent", "giroNrNorsk", "datoGiroNrNorsk"})
    public static class EndreGironrNorsk {

        private String offentligIdent;
        private String giroNrNorsk;
        private String datoGiroNrNorsk;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"offentligIdent"})
    public static class BrukerIdentifikasjon {

        private String offentligIdent;
    }
}
