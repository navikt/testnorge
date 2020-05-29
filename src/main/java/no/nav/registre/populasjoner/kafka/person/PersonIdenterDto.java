package no.nav.registre.populasjoner.kafka.person;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PersonIdenterDto {

    @Singular("identer")
    List<IdentDetaljDto> identer;

}