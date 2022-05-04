package no.nav.testnav.identpool.providers.v1.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HentIdenterRequest {
    private Identtype identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private Kjoenn kjoenn;
    @NotNull
    private int antall;
    private String rekvirertAv;
    private Boolean syntetisk;

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(0, antall);
    }
}
