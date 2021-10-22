package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingRequestDTO {

    private Identtype identtype;

    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private Integer alder;

    private Boolean syntetisk;

    private String opprettFraIdent;

    private PersonDTO person;
}