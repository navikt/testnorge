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
import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "sfePersonData")
public class EgenansattResponse extends EndringsmeldingResponse {

    private SfeAjourforing sfeAjourforing;

    @Data
    @XmlType(propOrder = {"systemInfo", "endreEgenAnsatt", "opphorEgenAnsatt"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private TpsServiceRoutineEndring endreEgenAnsatt;
        private TpsServiceRoutineEndring opphorEgenAnsatt;
    }
}
