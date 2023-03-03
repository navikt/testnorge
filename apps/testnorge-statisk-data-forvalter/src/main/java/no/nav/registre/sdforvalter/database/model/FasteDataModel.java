package no.nav.registre.sdforvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

import no.nav.registre.sdforvalter.domain.FasteData;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public abstract class FasteDataModel<T extends FasteData> extends AuditModel {
    @OneToOne
    @JoinColumn(name = "gruppe_id")
    private GruppeModel gruppeModel;

    @OneToOne
    @JoinColumn(name = "opprinnelse_id")
    private OpprinnelseModel opprinnelseModel;

    public abstract T toDomain();
}
