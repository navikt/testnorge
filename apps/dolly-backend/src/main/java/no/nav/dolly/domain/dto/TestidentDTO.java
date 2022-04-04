package no.nav.dolly.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import no.nav.dolly.domain.jpa.Testident.Master;

@Data
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
