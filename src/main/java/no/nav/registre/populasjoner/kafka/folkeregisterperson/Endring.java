package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Endring {

    private String type;
    private LocalDateTime registrert;
    private String registrertAv;
    private String systemkilde;
    private String kilde;
}