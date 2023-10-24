package no.nav.registre.sdforvalter.database.model;

import jakarta.persistence.*;
import lombok.*;
import no.nav.registre.sdforvalter.domain.FasteData;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class FasteDataModel<T extends FasteData> extends AuditModel {

    @ManyToOne
    @JoinColumn(name = "gruppe_id")
    private GruppeModel gruppeModel;

    @ManyToOne
    @JoinColumn(name = "opprinnelse_id")
    private OpprinnelseModel opprinnelseModel;

    public abstract T toDomain();
}
