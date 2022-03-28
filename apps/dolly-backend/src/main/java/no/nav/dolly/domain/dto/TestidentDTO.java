package no.nav.dolly.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import no.nav.dolly.domain.jpa.Testident;

@Data
public class TestidentDTO {

    private String ident;
    private Testident.Master Master;

    @JsonIgnore
    public boolean isTpsf() {
        return getMaster() == Master.TPSF;
    }

    @JsonIgnore
    public boolean isPdl() {
        return getMaster() == Testident.Master.PDL;
    }

    @JsonIgnore
    public boolean isPdlf() {
        return getMaster() == Master.PDLF;
    }
}
