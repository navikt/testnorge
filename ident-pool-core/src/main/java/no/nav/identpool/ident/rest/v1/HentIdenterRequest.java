package no.nav.identpool.ident.rest.v1;

import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HentIdenterRequest {
    private Identtype identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private Kjoenn kjoenn;
    private Pageable antall;

    public void setAntall(int antall) {
        this.antall = PageRequest.of(0, antall);
    }

    public int getAntall() {
        return antall.getPageSize();
    }
}
