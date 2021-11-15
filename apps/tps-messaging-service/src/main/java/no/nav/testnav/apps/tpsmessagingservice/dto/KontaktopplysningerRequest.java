package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "sfePersonData")
public class KontaktopplysningerRequest extends EndringsmeldingRequest {

    private SfeAjourforing sfeAjourforing;

    @Data
    @XmlType(propOrder = {"systemInfo", "endreKontaktopplysninger", "endringAvSprak", "endringAvKontonr"})
    public static class SfeAjourforing {

        private TpsSystemInfo systemInfo;
        private TpsServiceRoutineEndring endreKontaktopplysninger;

        private SfeSpraak endringAvSprak;
        private KontaktopplysningerRequestDTO.Kontonummer endringAvKontonr;
    }
}
