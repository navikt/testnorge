package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SfeSpraak {

    private NyttSprak endreSprak;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"sprakKode", "datoSprak"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NyttSprak {
        private String sprakKode;
        private String datoSprak;
    }
}