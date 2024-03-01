package no.nav.testnav.apps.tpsmessagingservice.dto;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"serviceRutinenavn", "fnr", "aksjonsKode", "aksjonsKode2, aksjonsDato"})
public class TpsServiceRutine {

    private String serviceRutinenavn;
    private String fnr;
    private String aksjonsKode;
    private String aksjonsKode2;
    private String aksjonsDato;
}