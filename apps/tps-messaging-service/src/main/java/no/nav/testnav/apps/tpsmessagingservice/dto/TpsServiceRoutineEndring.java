package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"serviceRutinenavn", "offentligIdent"})
public class TpsServiceRoutineEndring {

    private String serviceRutinenavn;
    private String offentligIdent;
}
