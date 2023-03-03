package no.nav.registre.sdforvalter.database.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import no.nav.registre.sdforvalter.domain.Krr;

@Entity
@ToString
@Setter
@Getter
@Slf4j
@NoArgsConstructor
@Table(name = "krr")
public class KrrModel extends FasteDataModel<Krr> {

    @Id
    private String fnr;
    private String name;
    private String email;
    private String sms;
    private boolean reserved;
    private boolean sdp;
    private boolean emailValid;
    private boolean smsValid;

    public KrrModel(Krr krr, OpprinnelseModel opprinnelseModel, GruppeModel gruppeModel) {
        super(gruppeModel, opprinnelseModel);
        fnr = krr.getFnr();
        name = krr.getName();
        email = krr.getEmail();
        sms = krr.getSms();
        reserved = krr.isReserved();
        sdp = krr.isSdp();
        emailValid = krr.isEmailValid();
        smsValid = krr.isSmsValid();
    }


    @Override
    public Krr toDomain() {
        return new Krr(this);
    }
}
