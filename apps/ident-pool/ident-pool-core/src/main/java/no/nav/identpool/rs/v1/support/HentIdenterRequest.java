package no.nav.identpool.rs.v1.support;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HentIdenterRequest {
    private Identtype identtype;
    @NotNull
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private Kjoenn kjoenn;
    @NotNull
    private int antall;
    private String rekvirertAv;

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(0, antall);
    }
}
