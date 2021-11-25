package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "tpsPersonData")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsServicerutineRequest {

    private TpsServiceRutine tpsServiceRutine;
}
