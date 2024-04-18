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
public class BankkontoUtlandRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"systemInfo", "endreGironrUtl", "opphorGironrUtl"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private EndreGironrUtl endreGironrUtl;
        private BrukerIdentifikasjon opphorGironrUtl;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"offentligIdent", "giroNrUtland", "datoGiroNr", "kodeSwift", "kodeLand",
            "bankNavn", "kodeBank", "valuta", "bankAdresse1", "bankAdresse2", "bankAdresse3"})
    public static class EndreGironrUtl {

        private String offentligIdent;
        private String giroNrUtland;
        private String datoGiroNr;
        private String kodeSwift;
        private String kodeLand;
        private String bankNavn;
        private String kodeBank;
        private String valuta;
        private String bankAdresse1;
        private String bankAdresse2;
        private String bankAdresse3;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"offentligIdent"})
    public static class BrukerIdentifikasjon {

        private String offentligIdent;
    }
}
