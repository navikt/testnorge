package no.nav.registre.sdforvalter.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import no.nav.registre.sdforvalter.domain.TpsIdent;

@Entity
@ToString(callSuper = true)
@Getter
@Setter
@Slf4j
@Table(name = "tps_identer")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TpsIdentModel extends FasteDataModel<TpsIdent> {

    @Id
    @JsonProperty
    private String fnr;
    @JsonProperty("fornavn")
    private String firstName;
    @JsonProperty("etternavn")
    private String lastName;
    @JsonProperty("adresse")
    private String address;
    @JsonProperty("postnr")
    private String postNr;
    @JsonProperty("by")
    private String city;

    public TpsIdentModel(TpsIdent tpsIdent, OpprinnelseModel opprinnelseModel, GruppeModel gruppeModel) {
        super(gruppeModel, opprinnelseModel);
        fnr = tpsIdent.getFnr();
        firstName = tpsIdent.getFirstName();
        lastName = tpsIdent.getLastName();
        address = tpsIdent.getAddress();
        postNr = tpsIdent.getPostNr();
        city = tpsIdent.getCity();
    }

    @Override
    public TpsIdent toDomain() {
        throw new UnsupportedOperationException("Ikke mulig å convertere til model");
    }
}
