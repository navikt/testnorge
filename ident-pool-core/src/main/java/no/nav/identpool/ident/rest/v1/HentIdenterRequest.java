package no.nav.identpool.ident.rest.v1;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HentIdenterRequest {
    private Identtype identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private Kjoenn kjoenn;
    private Pageable pageable;
}
