package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "sfePersonData")
public class OpphoerEgenansattRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @XmlType(propOrder = {"systemInfo", "opphorEgenAnsatt"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private TpsServiceRoutineEndring opphorEgenAnsatt;
    }
}
