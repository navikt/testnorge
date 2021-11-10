package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.Data;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlRootElement(name = "sfePersonData")
public class KontaktopplysningerRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @XmlType(propOrder = {"systemInfo", "endreKontaktopplysninger", "endringAvSprak", "endringAvKontonr"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private TpsServiceRoutineEndring endreKontaktopplysninger;

        private KontaktopplysningerRequestDTO.Spraak endringAvSprak;
        private KontaktopplysningerRequestDTO.Kontonummer endringAvKontonr;
    }
}
