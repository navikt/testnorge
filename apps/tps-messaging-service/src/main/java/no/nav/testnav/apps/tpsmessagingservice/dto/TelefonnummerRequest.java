package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonnummerDTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement(name = "sfePersonData")
public class TelefonnummerRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"systemInfo", "endreTelefon", "opphorTelefon"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private TelefonOpplysninger endreTelefon;
        private BrukertypeIdentifikasjon opphorTelefon;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @XmlType(propOrder = {"telefonLandkode", "telefonNr", "datoTelefon"})
    public static class TelefonOpplysninger extends BrukertypeIdentifikasjon{

        private String telefonLandkode;
        private String telefonNr;
        private String datoTelefon;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @XmlType(propOrder = {"offentligIdent", "typeTelefon"})
    public static class BrukertypeIdentifikasjon {

        private String offentligIdent;
        private TelefonnummerDTO.TypeTelefon typeTelefon;
    }
}