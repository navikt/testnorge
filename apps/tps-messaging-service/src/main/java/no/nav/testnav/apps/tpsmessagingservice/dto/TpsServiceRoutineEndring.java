package no.nav.testnav.apps.tpsmessagingservice.dto;

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
@XmlType(propOrder = {"serviceRutinenavn", "offentligIdent", "fom"})
public class TpsServiceRoutineEndring {

    private String serviceRutinenavn;
    private String offentligIdent;
    private LocalDate fom;
}
