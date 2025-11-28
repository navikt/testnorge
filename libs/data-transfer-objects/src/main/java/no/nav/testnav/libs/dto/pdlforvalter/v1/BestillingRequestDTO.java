package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

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
    private Boolean id2032;

    private String opprettFraIdent;

    private PersonDTO person;

    @JsonIgnore
    public boolean hasAlder() {

        return nonNull(alder) || nonNull(foedtEtter) || nonNull(foedtFoer);
    }
}