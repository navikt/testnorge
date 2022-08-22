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
public class PostadresseRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"systemInfo", "endreAdresseLinjer", "opphorLinjeAdresse"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private PostAdresse endreAdresseLinjer;
        private PostAdresseOpphoer opphorLinjeAdresse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"offentligIdent", "typeAdresse", "datoAdresse", "adresse1", "adresse2", "adresse3", "postnr", "kodeLand"})
    public static class PostAdresse {

        private String offentligIdent;
        private String typeAdresse;
        private String datoAdresse;
        private String adresse1;
        private String adresse2;
        private String adresse3;
        private String postnr;
        private String kodeLand;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"offentligIdent", "typeAdresse"})
    public static class PostAdresseOpphoer {

        private String offentligIdent;
        private String typeAdresse;
    }
}
