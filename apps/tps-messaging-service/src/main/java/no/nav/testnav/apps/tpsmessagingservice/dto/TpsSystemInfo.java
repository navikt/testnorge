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
@XmlType(propOrder = {"kilde", "brukerID"})
public class TpsSystemInfo {

    private String kilde;
    private String brukerID;

    public static TpsSystemInfo getDefault() {
        return TpsSystemInfo.builder()
                .kilde("Dolly")
                .brukerID("anonymousUser")
                .build();
    }
}