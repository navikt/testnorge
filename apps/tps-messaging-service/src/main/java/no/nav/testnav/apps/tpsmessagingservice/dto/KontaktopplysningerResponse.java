package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@XmlRootElement(name = "sfePersonData")
public class KontaktopplysningerResponse extends EndringsmeldingResponse {

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
