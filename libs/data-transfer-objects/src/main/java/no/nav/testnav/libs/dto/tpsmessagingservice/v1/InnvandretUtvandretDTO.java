package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnvandretUtvandretDTO {

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private PersonDTO person;
    private InnUtvandret innutvandret;
    private String landkode;
    private LocalDateTime flyttedato;

    public enum InnUtvandret {INNVANDRET, UTVANDRET, NIL}
}