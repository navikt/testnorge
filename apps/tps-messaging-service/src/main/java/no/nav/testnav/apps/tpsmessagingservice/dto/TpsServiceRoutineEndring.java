package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"offentligIdent", "fom"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TpsServiceRoutineEndring {

    private String offentligIdent;
    private LocalDate fom;
}
