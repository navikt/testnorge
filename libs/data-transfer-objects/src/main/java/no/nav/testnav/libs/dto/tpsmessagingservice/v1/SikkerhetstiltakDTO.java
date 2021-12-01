package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SikkerhetstiltakDTO {

    private String tiltakstype;
    private String beskrivelse;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;
}
