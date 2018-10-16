package no.nav.identpool.ident.rest.v1;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;

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

    public int getAntall() {
        return antall;
    }

    public void setAntall(int antall) {
        this.antall = antall;
    }

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(0, antall);
    }
}
