package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdForvalter.domain.TpsIdent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@ToString
@Getter
@Setter
@Slf4j
@Table(name = "tps_identer")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
        return new TpsIdent(this);
    }
}
