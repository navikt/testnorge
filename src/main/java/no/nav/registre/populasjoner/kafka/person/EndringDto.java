package no.nav.registre.populasjoner.kafka.person;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class EndringDto {

    String type;
    LocalDateTime registrert;
    String registrertAv;
    String systemkilde;
    String kilde;
}