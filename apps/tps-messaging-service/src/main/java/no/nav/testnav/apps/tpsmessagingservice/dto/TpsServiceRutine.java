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
@XmlType(propOrder = {"serviceRutinenavn", "fnr", "aksjonsKode", "aksjonsKode2"})
public class TpsServiceRutine {

    private String serviceRutinenavn;
    private String fnr;
    private String aksjonsKode;
    private String aksjonsKode2;
}