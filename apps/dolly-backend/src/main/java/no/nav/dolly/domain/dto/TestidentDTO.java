package no.nav.dolly.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Testident.Master;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestidentDTO {

    private String ident;
    private Master master;

    @JsonIgnore
    public boolean isTpsf() {
        return getMaster() == Master.TPSF;
    }

    @JsonIgnore
    public boolean isPdl() {
        return getMaster() == Master.PDL;
    }

    @JsonIgnore
    public boolean isPdlf() {
        return getMaster() == Master.PDLF;
    }
}
