package no.nav.registre.sdForvalter.database.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import no.nav.registre.sdForvalter.domain.Aareg;

@Entity
@ToString
@Getter
@Setter
@Slf4j
@Table(name = "AAREG")
@NoArgsConstructor
public class AaregModel extends FasteDataModel<Aareg> {
    @Id
    private String fnr;
    private String orgId;

    public AaregModel(Aareg aareg, OpprinnelseModel opprinnelseModel, GruppeModel gruppeModel) {
        super(gruppeModel, opprinnelseModel);
        fnr = aareg.getFnr();
        orgId = aareg.getOrgId();
    }

    @Override
    public Aareg toDomain() {
        return new Aareg(this);
    }
}