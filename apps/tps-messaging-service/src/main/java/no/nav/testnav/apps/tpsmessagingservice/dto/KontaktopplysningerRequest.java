package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement(name = "sfePersonData")
public class KontaktopplysningerRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"systemInfo", "endreKontaktopplysninger"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private KontaktOpplysninger endreKontaktopplysninger;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"offentligIdent", "endringAvTelefon"})
    public static class KontaktOpplysninger {

        private String offentligIdent;
        private TelefonOpplysninger endringAvTelefon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"nyTelefon", "opphorTelefon"})
    public static class TelefonOpplysninger {

        private List<TelefonData> nyTelefon;
        private List<Telefontype> opphorTelefon;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"telefonLandkode", "telefonNr", "datoTelefon"})
    public static class TelefonData extends Telefontype {

        private String telefonLandkode;
        private String telefonNr;
        private String datoTelefon;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"typeTelefon"})
    public static class Telefontype {

        private TelefonTypeNummerDTO.TypeTelefon typeTelefon;
    }
}