package no.nav.organisasjonforvalter.dto.responses.ereg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Informasjon om forretningsadresse", allOf = Adresse.class)
public class Forretningsadresse extends Adresse {

}
