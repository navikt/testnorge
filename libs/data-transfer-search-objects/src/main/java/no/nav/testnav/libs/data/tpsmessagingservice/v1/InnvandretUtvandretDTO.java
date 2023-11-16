package no.nav.testnav.libs.data.tpsmessagingservice.v1;

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
public class InnvandretUtvandretDTO {

    private InnUtvandret innutvandret;
    private String landkode;
    private LocalDateTime flyttedato;

    public enum InnUtvandret {INNVANDRET, UTVANDRET, NIL}
}