package no.nav.registre.hodejegeren.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Data {

    private LocalDateTime datoOpprettet;

    private LocalDateTime datoEndret;

    private Object innhold;
}
