package no.nav.registre.core.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class OppholdSammeVilkaar {
    private String oppholdPaaSammeVilkaar;
}
