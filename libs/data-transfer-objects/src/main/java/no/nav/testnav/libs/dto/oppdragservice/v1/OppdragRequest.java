package no.nav.testnav.libs.dto.oppdragservice.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entitet Oppdrag, Referanse ID 110")
public class OppdragRequest {

    private Oppdrag oppdrag;
}
