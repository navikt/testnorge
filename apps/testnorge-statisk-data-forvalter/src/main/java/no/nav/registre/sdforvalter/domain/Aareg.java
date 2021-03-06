package no.nav.registre.sdforvalter.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdforvalter.database.model.AaregModel;

@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class Aareg extends FasteData {


    @JsonProperty(required = true)
    private final String fnr;
    @JsonProperty(required = true)
    private final String orgId;

    public Aareg(String gruppe, String opprinnelse, String fnr, String orgId) {
        super(gruppe, opprinnelse);
        this.fnr = fnr;
        this.orgId = orgId;
    }

    public Aareg(AaregModel model) {
        super(model);
        fnr = model.getFnr();
        orgId = model.getOrgId();
    }
}